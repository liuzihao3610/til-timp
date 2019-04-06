package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.framework.enmus.RowStatus;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.OrderType;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.OrderDetailMapper;
import com.tilchina.timp.mapper.OrderMapper;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import com.tilchina.timp.util.CheckUtil;
import com.tilchina.timp.utils.GenerateCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 新订单逻辑
 * 
 * @version 1.0.0 2018/6/14
 * @author WangShengguang   
 */ 
@Service
@Slf4j
public class SuperOrderServiceImpl implements SuperOrderService {

    @Autowired
    private CorpService corpService;

    @Autowired
    private CarService carService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private QuotationDetailService quotationDetailService;

    /**
     * 根据订单日期起止获取对账数据
     *
     * @since 1.0.0
     * @param customerId    客户ID
     * @param carrierId     承运公司ID
     * @param startDate     订单日期起
     * @param endDate       订单日期止
     * @return java.util.List<com.tilchina.timp.model.Order>
     */
    @Override
    public List<Order> queryForSettlement(Long customerId, Long carrierId, Date startDate, Date endDate){
        Map<String,Object> map = new HashMap<>();
        map.put("corpCustomerId",customerId);
        map.put("corpCarrierId",carrierId);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("billStatus",2);
        map.put("reconciliation",0);
        return queryForSettlement(map);
    }

    /**
     * 根据车架号列表获取对账数据
     *
     * @since 1.0.0
     * @param customerId    客户ID
     * @param carrierId     承运公司ID
     * @param vins          车架号列表
     * @return java.util.List<com.tilchina.timp.model.Order>
     */
    @Override
    public List<Order> queryForSettlement(Long customerId, Long carrierId, List<String> vins){
        Map<String,Object> map = new HashMap<>();
        map.put("corpCustomerId",customerId);
        map.put("corpCarrierId",carrierId);
        map.put("billStatus",2);
        map.put("reconciliation",0);
        map.put("vins",vins);
        return queryForSettlement(map);
    }

    @Override
    public List<Order> queryForSettlement(Map<String, Object> map){
        List<OrderDetail> details = orderDetailMapper.selectForSettlement(map);
        if(CollectionUtils.isEmpty(details)) {
            return null;
        }
        Map<Long,List<OrderDetail>> orderMap = details.stream().collect(Collectors.groupingBy(OrderDetail::getOrderId));
        List<Order> orders = orderMapper.selectByIds(orderMap.keySet());
        for (Order order : orders) {
            order.setOds(orderMap.get(order.getOrderId()));
        }
        return orders;
    }

    /**
     * 更新对账标志
     *
     * @since 1.0.0
     * @param orderDetailIds    订单明细ID列表
     * @param isReverse         是否逆向更新，true:变更为未对账，false：变更为已对账
     * @return void
     */
    @Override
    public void updateForReconciliation(List<Long> orderDetailIds, boolean isReverse){
        List<OrderDetail> details = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(orderDetailIds)){
            for (Long orderDetailId : orderDetailIds) {
                OrderDetail detail = new OrderDetail();
                detail.setOrderDetailId(orderDetailId);
                detail.setReconciliation(isReverse?0:1);
                details.add(detail);
            }
        }
        orderDetailMapper.updateByPrimaryKeySelective(details);
    }

    /**
     * 标记为回单
     *
     * @since 1.0.0
     * @param orderDetailIds    订单明细ID列表
     * @return void
     */
    @Override
    public void updateForTransfer(List<Long> orderDetailIds){
        List<OrderDetail> details = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(orderDetailIds)){
            for (Long orderDetailId : orderDetailIds) {
                OrderDetail detail = new OrderDetail();
                detail.setOrderDetailId(orderDetailId);
                detail.setTransferOrderStatus(4);
            }
        }
        orderDetailMapper.updateByPrimaryKeySelective(details);
    }

    /**
     * 标记结算
     *
     * @since 1.0.0
     * @param orderDetailIds    订单明细ID列表
     * @return void
     */
    @Override
    public void updateForSettlement(List<Long> orderDetailIds){
        List<OrderDetail> details = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(orderDetailIds)){
            for (Long orderDetailId : orderDetailIds) {
                OrderDetail detail = new OrderDetail();
                detail.setOrderDetailId(orderDetailId);
                detail.setSettlement(1);
                details.add(detail);
            }
        }
        orderDetailMapper.updateByPrimaryKeySelective(details);
    }

    @Override
    public List<Order> queryList(Map<String, Object> map){
        Environment env = Environment.getEnv();
        map.put("corpId",env.getCorp().getCorpId());
        return orderMapper.selectList(map);
    }

    @Override
    public PageInfo<Order> queryList(Map<String, Object> map, int pageNum, int pageSize){
        Environment env = Environment.getEnv();
        if(map == null){
            map = new HashMap<>();
        }
        map.put("corpId",env.getCorp().getCorpId());
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orders = orderMapper.selectList(map);
        for (Order order : orders) {
            order.setOds(queryDetailByOrderId(order.getOrderId()));
        }
        return new PageInfo(orders);
    }

    @Override
    public List<OrderDetail> queryDetailByDetailIds(List<Long> detailIds){
        if(CollectionUtils.isEmpty(detailIds)){
            throw new BusinessException("9003");
        }
        return orderDetailMapper.selectDetailByDetailIds(detailIds);
    }

    @Override
    public List<OrderDetail> queryDetailByOrderId(Long orderId){
        if(orderId == null){
            throw new BusinessException("9003");
        }
        return orderDetailMapper.queryByOrderId(orderId);
    }

    @Override
    @Transactional
    public void oneTouchOut(Long orderId, Long carrierId){
        if(orderId == null || carrierId == null){
            throw new BusinessException("9003");
        }
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if(order == null){
            throw new BusinessException("订单不存在或已删除！");
        }
        List<OrderDetail> details = orderDetailMapper.selectByOrderId(orderId);
        if(CollectionUtils.isEmpty(details)){
            throw new BusinessException("订单异常，无法获取订单明细！");
        }
        Corp carrierCorp = corpService.queryById(carrierId);
        if(carrierCorp == null){
            throw new BusinessException("承运公司不存在！");
        }

        Environment env = Environment.getEnv();
        Order vo = new Order();
        vo.setOrderCode(GenerateCode.getCode());
        vo.setCorpCustomerId(order.getCorpCarrierId());
        vo.setCorpCarrierId(carrierId);
        vo.setWorkType(order.getWorkType());
        vo.setBillStatus(2);
        vo.setCreator(env.getUser().getUserId());
        vo.setCreateTime(new Date());
        vo.setChecker(order.getCreator());
        vo.setCheckDate(order.getCreateTime());
        vo.setTransmitDate(order.getCreateTime());
        vo.setSalesManId(order.getCreator());

        boolean isSameCorp = false;
        List<OrderDetail> bvos = new ArrayList<>();
        details.forEach( detail -> {
            OrderDetail bvo = new OrderDetail();

            bvo.setCarVin(detail.getCarVin());
            bvo.setCarId(detail.getCarId());
            bvo.setBrandId(detail.getBrandId());
            bvo.setCarTypeId(detail.getCarTypeId());
            bvo.setSendUnitId(detail.getSendUnitId());
            bvo.setSendCityId(detail.getSendCityId());
            bvo.setReceiveUnitId(detail.getReceiveUnitId());
            bvo.setReceiveCityId(detail.getReceiveCityId());
            bvo.setClaimLoadDate(detail.getClaimLoadDate());
            bvo.setCorpId(order.getCorpId());
            if(isSameCorp) {
                detail.setClaimDeliveryDate(detail.getClaimLoadDate());
                detail.setQuotedPriceId(detail.getQuotedPriceId());
                detail.setCustomerQuotedPrice(detail.getCustomerQuotedPrice());
            }
            bvo.setOriginalOrderId(detail.getOriginalOrderId());
            bvo.setOriginalOrderDetailId(detail.getOriginalOrderDetailId());
            bvos.add(bvo);
        });

        orderMapper.insertSelective(vo);
        for (OrderDetail bvo : bvos) {
            bvo.setOrderId(vo.getOrderId());
            bvo.setOrderCode(vo.getOrderCode());
        }
        orderDetailMapper.insertSelective(bvos);
    }

    @Override
    @Transactional
    public void addSectionOrder(Order order){
        Environment env = Environment.getEnv();
        if (env.isAdmin()) {
            throw new BusinessException("管理员无法制单！");
        }
        if (order.getOrderType() == null) {
            throw new BusinessException("订单类型错误！");
        }
        List<OrderPieceVO> pieces = order.getPieceList();
        if(CollectionUtils.isEmpty(pieces)){
            throw new BusinessException("分段信息为空，无法创建分段订单。");
        }
        if(pieces.size() == 1){
            throw new BusinessException("只有一条分段信息，请使用转包订单。");
        }
        // 检查分段信息
        for (int i = 0; i < pieces.size(); i++) {
            StringBuilder sub = new StringBuilder();
            OrderPieceVO piece = pieces.get(i);
            Corp corp = corpService.queryById(piece.getCorpCustomerId());
            if(corp == null){
                sub.append("分段信息第"+(i+1)+"条，承运公司不存在！");
            }
            if(i == pieces.size()-1){
                break;
            }
            //检查中转单位
            Unit unit = unitService.queryById(piece.getUnitId());
            if(unit == null ){
                sub.append("分段信息第"+(i+1)+"条，中转单位不存在！");
            }else {
                piece.setCityId(unit.getCityId());
            }
        }
        //生成分段订单
        checkDetailForOther(order.getOds(),false);
        Long sendUnitId = null;
        Long receiveUnitId = null;
        for (int i = 0; i < pieces.size(); i++) {
            OrderPieceVO piece = pieces.get(i);
            order.setCorpCarrierId(piece.getCorpCustomerId());
            order.setOrderId(null);
            if(i == 0 ){
                receiveUnitId = piece.getUnitId();
            }else if(i == pieces.size()-1){
                sendUnitId = pieces.get(i-1).getUnitId();
                receiveUnitId = null;
            }else{
                sendUnitId = pieces.get(i-1).getUnitId();
                receiveUnitId = piece.getUnitId();
            }

            List<OrderDetail> details = new ArrayList<>();
            for (OrderDetail oldDetail : order.getOds()) {
                OrderDetail detail = new OrderDetail();
                detail.setSendUnitId(sendUnitId==null?detail.getSendUnitId():sendUnitId);
                detail.setReceiveUnitId(receiveUnitId==null?detail.getOriReceiveUnitId():receiveUnitId);
                detail.setClaimLoadDate(oldDetail.getClaimLoadDate());
                detail.setCarVin(oldDetail.getCarVin());
                detail.setCarId(oldDetail.getCarId());
                detail.setBrandId(oldDetail.getBrandId());
                detail.setCarTypeId(oldDetail.getCarTypeId());
                detail.setOriginalOrderId(oldDetail.getOrderId());
                detail.setOriginalOrderDetailId(oldDetail.getOrderDetailId());

                if(i==0){
                    detail.setOriReceiveUnitId(detail.getReceiveUnitId());
                }
                if( i > 0 ){
                    detail.setClaimLoadDate(detail.getClaimDeliveryDate());
                }
                details.add(detail);
            }
            order.setOds(details);
            // 保存订单
            checkOrder(order);
            order.setOrderCode(GenerateCode.getCode());
            order.setCreator(env.getUser().getUserId());
            order.setCreateTime(new Date());
            order.setCorpId(env.getCorp().getCorpId());
            order.setBillStatus(0);
            orderMapper.insertSelective(order);
            for (OrderDetail orderDetail : order.getOds()) {
                orderDetail.setOrderId(order.getOrderId());
                orderDetail.setOrderCode(order.getOrderCode());
                orderDetail.setCorpId(order.getCorpId());
            }
            orderDetailMapper.insertSelective(order.getOds());
        }
    }

    @Override
    @Transactional
    public void addOrder(Order order) {
        Environment env = Environment.getEnv();
        if (env.isAdmin()) {
            throw new BusinessException("管理员无法制单！");
        }
        if (order.getOrderType() == null) {
            throw new BusinessException("订单类型错误！");
        }
        checkOrder(order);

        order.setOrderCode(GenerateCode.getCode());
        order.setCreator(env.getUser().getUserId());
        order.setCreateTime(new Date());
        order.setCorpId(env.getCorp().getCorpId());
        if(OrderType.In.getIndex() == order.getOrderType()) {
            order.setBillStatus(2);
            order.setChecker(order.getCreator());
            order.setCheckDate(order.getCreateTime());
            order.setTransmitDate(order.getCreateTime());
            order.setSalesManId(order.getCreator());
        }else{
            order.setBillStatus(0);
        }

        orderMapper.insertSelective(order);
        if(OrderType.In.getIndex() == order.getOrderType()) {
            checkDetailForIn(order.getOds());
        }else if(OrderType.Out.getIndex() == order.getOrderType()){
            // 检查是否上下级公司
            boolean isSameCorp = false;
            // 转包几分段订单只检查订单信息
            checkDetailForOther(order.getOds(),isSameCorp);
        }else if(OrderType.Section.getIndex() == order.getOrderType()){
            throw new BusinessException("分段订单业务处理错误。");
        }
        for (OrderDetail orderDetail : order.getOds()) {
            orderDetail.setOrderId(order.getOrderId());
            orderDetail.setOrderCode(order.getOrderCode());
            orderDetail.setCorpId(order.getCorpId());
            orderDetail.setOrderDetailId(null);
        }

        //填充报价
        quotationDetailService.getQuotationByOrder(order.getCorpCustomerId(),order.getCorpCarrierId(),order.getOrderDate(),
                order.getWorkType(),order.getOds());

        orderDetailMapper.insertSelective(order.getOds());

        if(OrderType.In.getIndex() == order.getOrderType()) {
            List<OrderDetail> details = new ArrayList<>();
            order.getOds().forEach(d -> {
                OrderDetail newD = new OrderDetail();
                newD.setOrderDetailId(d.getOrderDetailId());
                newD.setOriginalOrderId(d.getOrderId());
                newD.setOriginalOrderDetailId(d.getOrderDetailId());
                details.add(newD);
            });
            orderDetailMapper.updateByPrimaryKeySelective(details);
        }
    }

    @Override
    @Transactional
    public void update(Order order){
        checkOrder(order);
        int count = orderMapper.updateByPrimaryKeySelective(order);
        if(count == 0){
            throw new BusinessException("订单不存在或已删除！");
        }
        List<OrderDetail> adds = new ArrayList<>();
        List<OrderDetail> edits = new ArrayList<>();
        List<Long> dels = new ArrayList<>();
        order.getOds().forEach( detail -> {
            int rowStatus = detail.getRowStatus();
            switch (RowStatus.get(rowStatus)){
                case NEW:
                    adds.add(detail);
                    detail.setOrderId(order.getOrderId());
                    detail.setOrderCode(order.getOrderCode());
                    break;
                case EDIT:edits.add(detail);break;
                case DELETE:dels.add(detail.getOrderDetailId());
                default:break;
            }
        });
        if(CollectionUtils.isNotEmpty(adds)){
            checkDetailForIn(adds);
            orderDetailMapper.insertSelective(adds);
        }
        if(CollectionUtils.isNotEmpty(edits)){
            checkDetailForIn(edits);
            orderDetailMapper.updateByPrimaryKeySelective(edits);
        }
        if(CollectionUtils.isNotEmpty(dels)){
            orderDetailMapper.deleteByPrimaryKey(dels);
        }
    }

    @Override
    public void checkDetailForOther(List<OrderDetail> details, boolean isSameCorp){
        if (CollectionUtils.isEmpty(details)) {
            throw new BusinessException("订单明细不能为空！");
        }
        StringBuilder s = new StringBuilder();
        List<Long> detailIds = new ArrayList<>();
        for (int i = 0; i < details.size(); i++) {
            OrderDetail detail = details.get(i);
            StringBuilder sub = new StringBuilder();
            if(detailIds.contains(detail.getOrderDetailId())){
                sub.append("数据重复，");
            }
            detailIds.add(detail.getOrderDetailId());
            if (sub.length() > 0) {
                s.append("订单明细第" + (i + 1) + "行，").append(sub).append("<br/>");
            }
        }
        if (s.length() > 0) {
            throw new BusinessException(s.toString());
        }

        List<OrderDetail> orderDetails = orderDetailMapper.selectDetailByDetailIds(detailIds);
        Map<Long,OrderDetail> detailMap = new HashMap<>();
        orderDetails.forEach(d -> detailMap.put(d.getOrderDetailId(),d));

        for (int i = 0; i < details.size(); i++) {
            StringBuilder sub = new StringBuilder();
            OrderDetail detail = details.get(i);
            OrderDetail oldDetail = detailMap.get(detail.getOrderDetailId());
            if(oldDetail == null){
                sub.append("找不到对应的客户订单，");
            }else{
                detail.setCarVin(oldDetail.getCarVin());
                detail.setCarId(oldDetail.getCarId());
                detail.setBrandId(oldDetail.getBrandId());
                detail.setCarTypeId(oldDetail.getCarTypeId());
                detail.setSendUnitId(oldDetail.getSendUnitId());
                detail.setSendCityId(oldDetail.getSendCityId());
                detail.setReceiveUnitId(oldDetail.getReceiveUnitId());
                detail.setReceiveCityId(oldDetail.getReceiveCityId());
                detail.setClaimLoadDate(oldDetail.getClaimLoadDate());
                if(isSameCorp) {
                    detail.setClaimDeliveryDate(oldDetail.getClaimLoadDate());
                    detail.setQuotedPriceId(oldDetail.getQuotedPriceId());
                    detail.setCustomerQuotedPrice(oldDetail.getCustomerQuotedPrice());
                }
                detail.setOriginalOrderId(oldDetail.getOriginalOrderId());
                detail.setOriginalOrderDetailId(oldDetail.getOriginalOrderDetailId());
            }
            if (sub.length() > 0) {
                s.append("订单明细第" + (i + 1) + "行，").append(sub).append("<br/>");
            }
        }
        if (s.length() > 0) {
            throw new BusinessException(s.toString());
        }
    }


    @Override
    public void checkDetailForIn(List<OrderDetail> details) {
        if (CollectionUtils.isEmpty(details)) {
            throw new BusinessException("订单明细不能为空！");
        }
        StringBuilder s = new StringBuilder();
        List<String> vins = new ArrayList<>();
        Set<Long> carIds = new HashSet<>();
        Set<Long> unitIds = new HashSet<>();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        //检查字段
        for (int i = 0; i < details.size(); i++) {
            StringBuilder sub = new StringBuilder();
            OrderDetail detail = details.get(i);
            sub.append(checkDetailForIn(detail));

            String vinKey = detail.getCarVin()+detail.getSendUnitId()+detail.getReceiveCityId()+sf.format(detail.getClaimLoadDate());
            if (vins.contains(vinKey)) {
                sub.append("车架号[" + detail.getCarVin() + "]重复，");
            } else {
                vins.add(vinKey);
            }
            carIds.add(detail.getCarId());
            unitIds.add(detail.getSendUnitId());
            unitIds.add(detail.getReceiveUnitId());
            if (sub.length() > 0) {
                s.append("订单明细第" + (i + 1) + "行，").append(sub).append("<br/>");
            }
        }
        if (s.length() > 0) {
            throw new BusinessException(s.toString());
        }

        //数据库核验
        Map<Long, Unit> unitMap = unitService.queryMapByIds(new ArrayList<>(unitIds));
        Map<Long, Car> carMap = carService.queryMapByCarIds(carIds);
        for (int i = 0; i < details.size(); i++) {
            OrderDetail detail = details.get(i);
            StringBuilder sub = new StringBuilder();
            Car car = carMap.get(detail.getCarId());
            if (car == null) {
                sub.append("商品车不存在，");
            } else if (car.getBrandId() == null) {
                sub.append("商品车未维护品牌，");
            }else{
                detail.setBrandId(car.getBrandId());
                detail.setCarTypeId(car.getCarTypeId());
            }

            Unit sendUnit = unitMap.get(detail.getSendUnitId());
            if(sendUnit == null){
                sub.append("发货单位不存在，");
            }else if(sendUnit.getCityId() == null){
                sub.append("发货单位未维护城市信息，");
            }else{
                detail.setSendCityId(sendUnit.getCityId());
            }

            Unit receiveUnit = unitMap.get(detail.getReceiveUnitId());
            if(receiveUnit == null){
                sub.append("发货单位不存在，");
            }else if(receiveUnit.getCityId() == null){
                sub.append("发货单位未维护城市信息，");
            }else{
                detail.setReceiveCityId(receiveUnit.getCityId());
            }
            if (sub.length() > 0) {
                s.append("订单明细第" + (i + 1) + "行，").append(sub).append("<br/>");
            }
        }
        if (s.length() > 0) {
            throw new BusinessException(s.toString());
        }
    }

    private StringBuilder checkDetailForIn(OrderDetail detail) {
        StringBuilder s = new StringBuilder();
        s.append(CheckUtil.checkString(detail.getCarVin(), false, 20, "车架号"));
        s.append(CheckUtil.checkNull(detail.getCarId(), false, "商品车型号"));
        s.append(CheckUtil.checkNull(detail.getBrandId(), false, "品牌"));
        s.append(CheckUtil.checkNull(detail.getSendUnitId(), false, "发货单位"));
        s.append(CheckUtil.checkNull(detail.getReceiveUnitId(), false, "收货单位"));
        s.append(CheckUtil.checkString(detail.getInstructionNumber(), true, 20, "指示书号"));
        s.append(CheckUtil.checkNull(detail.getClaimLoadDate(), false, "指定装车日期"));
        s.append(CheckUtil.checkString(detail.getProductNumber(), true, 20, "生产号"));
        s.append(CheckUtil.checkString(detail.getRemark(), true, 200, "备注"));
        return s;
    }

    @Override
    public void checkOrder(Order order) {
        if(order == null){
            throw new BusinessException("9003");
        }
        Environment env = Environment.getEnv();
        if (env.isAdmin()) {
            throw new BusinessException("管理员无法制单！");
        }
        if (order.getOrderType() == null) {
            throw new BusinessException("订单类型错误！");
        }
        if (order.getCorpCustomerId() == null || order.getCorpCarrierId() == null) {
            throw new BusinessException("客户或承运公司不能为空！");
        }

        // 初始化默认值
        if (!env.isSuperManager()) {
            switch (OrderType.get(order.getOrderType())) {
                case In:
                    // 原始订单承运商默认为当前公司
                    if (!env.getCorp().getCorpId().equals(order.getCorpCarrierId())) {
                        log.error("[订单管理]非法操作：" + env.getUser().getUserId());
                        throw new BusinessException("原始订单运输公司只能为登录用户所属公司！");
                    }
                    break;
                case Out:
                    // 转包订单客户默认为当前公司
                    if (!env.getCorp().getCorpId().equals(order.getCorpCustomerId())) {
                        throw new BusinessException("转包订单运客户只能为登录用户所属公司！");
                    }
                    break;
                case Section:
                    // 分段订单客户默认为当前公司
                    if (!env.getCorp().getCorpId().equals(order.getCorpCustomerId())) {
                        throw new BusinessException("转包订单运客户只能为登录用户所属公司！");
                    }
                    break;
                default:
                    throw new BusinessException("订单类型错误！");
            }
        }

        Corp customerCorp = corpService.queryById(order.getCorpCustomerId());
        if (customerCorp == null) {
            throw new BusinessException("客户不存在！");
        }
        Corp carrierCorp = corpService.queryById(order.getCorpCarrierId());
        if (carrierCorp == null) {
            throw new BusinessException("承运公司不存在！");
        }
    }
}
