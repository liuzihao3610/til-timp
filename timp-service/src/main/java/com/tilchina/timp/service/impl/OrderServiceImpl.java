package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.framework.enmus.RowStatus;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.CarStatus;
import com.tilchina.timp.enums.OperationType;
import com.tilchina.timp.enums.OrderType;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.OrderDetailMapper;
import com.tilchina.timp.mapper.OrderMapper;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import com.tilchina.timp.util.CheckUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
* 订单管理
*
* @version 1.0.0
* @author WangShengguang
*/
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

	@Autowired
    private OrderMapper orderMapper;

	@Autowired
    private OrderDetailMapper orderDetailMapper;

	@Autowired
    private CorpService corpService;

	@Autowired
    private CarService carService;

	@Autowired
    private BrandService brandService;

	@Autowired
    private CarTypeService carTypeService;

	@Autowired
    private UnitService unitService;

	@Autowired
    private QuotationDetailService quotationDetailService;

	@Autowired
    private StockCarService stockCarService;

	@Autowired
    private StockCarOutService stockCarOutService;

	@Autowired
    private TransportOrderService transportOrderService;

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
//        for (Order order : orders) {
//            order.setOds(queryDetailByOrderId(order.getOrderId()));
//        }
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
    public PageInfo<OrderDetail> queryDetails(Map<String, Object> map, int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<OrderDetail>(queryDetails(map));
    }

    @Override
    public List<OrderDetail> queryDetails(Map<String, Object> map){
        if(map == null){
            map = new HashMap<>();
        }
        Environment env = Environment.getEnv();
        map.put("corpCarrierId", env.getCorp().getCorpId());
        map.put("billStatus",2);
        map.put("carStatus", CarStatus.In.getIndex());
        Set<String> vins = new HashSet<>();
        if(map.get("carVins") != null){
            String[] carVins = map.get("carVins").toString().split("\\n+|\\s+| +|,+|，+");
            for (String carVin : carVins) {
                if(!StringUtils.isBlank(carVin)){
                    vins.add(carVin.trim());
                }
            }
        }
        if(CollectionUtils.isNotEmpty(vins)){
            map.put("vins",vins);
        }
        return orderDetailMapper.selectDetails(map);
    }
            
	@Override
    @Transactional
	public void add(Order order) {
		// 初始化默认值
		Environment env = Environment.getEnv();
		order.setOrderCode(getOrderCode());
		order.setCreator(env.getUser().getUserId());
		order.setCreateTime(new Date());
		order.setCorpId(env.getCorp().getCorpId());
		order.setBillStatus(0);

		switch (OrderType.get(order.getOrderType())){
            case In: order.setCorpCarrierId(env.getCorp().getCorpId()); break; // 原属订单承运商默认为当前公司
            case Out: order.setCorpCustomerId(env.getCorp().getCorpId()); break; // 转包订单客户默认为当前公司
            case Section:order.setCorpCustomerId(env.getCorp().getCorpId()); break; // 分段订单客户默认为当前公司
            default: throw new BusinessException("订单类型错误！");
        }

        if(CollectionUtils.isEmpty(order.getOds())){
            throw new BusinessException("需要至少一条订单明细。");
        }

        //字段检查
		String check = checkColumn(order);
		if(check.trim().length() > 0){
			throw new BusinessException(check);
		}
        boolean out = false;
        Corp corp;
        switch (OrderType.get(order.getOrderType())){
            case In:
                corp = corpService.queryById(order.getCorpCustomerId());
                if(corp == null){
                    throw new BusinessException("客户公司不存在！");
                }
                break;
            case Out:
                corp = corpService.queryById(order.getCorpCarrierId());
                if(corp == null){
                    throw new BusinessException("承运公司不存在！");
                }
                out = true;
                break;
            case Section:
                corp = corpService.queryById(order.getCorpCarrierId());
                if(corp == null){
                    throw new BusinessException("承运公司不存在！");
                }
                out = true;
                break;
            default: throw new BusinessException("订单类型错误！");
        }

        //保存表头
        orderMapper.insertSelective(order);

		//获取表体校验信息
        Map<Long,Brand> brandMap = brandService.queryMap();
        Map<String,CarType> carTypeMap = carTypeService.queryMap();
        Map<String,Car> carMap = carService.queryMap();
        Map<Long,Unit> unitMap = unitService.queryMap();
        StringBuilder s = new StringBuilder();

        List<String> vins = new ArrayList<>();
        //保存表体
        List<OrderDetail> details = order.getOds();
        for (int i = 0; i < details.size(); i++) {
            OrderDetail detail = details.get(i);
            detail.setOrderCode(order.getOrderCode());
            detail.setOrderId(order.getOrderId());
            detail.setCorpId(order.getCorpId());
            //表体数据库检查
            StringBuilder sb = new StringBuilder();
            if(vins.contains(detail.getCarVin())){
                sb.append("车架号["+detail.getCarVin()+"重复！");
            }else {
                vins.add(detail.getCarVin());
            }

            if(brandMap.get(detail.getBrandId()) == null){
                sb.append("品牌在系统中不存在，");
            }

            if(detail.getCarTypeId() != null && carTypeMap.get(detail.getBrandId()+"_"+detail.getCarTypeId()) == null){
                sb.append("车型类别在系统中不存在或与品牌不匹配，");
            }
            if(carMap.get(detail.getBrandId()+"_"+detail.getCarId()) == null){
                sb.append("商品车型号在系统中不存在或与品牌不匹配，");
            }
            if(unitMap.get(detail.getSendUnitId()) == null){
                sb.append("发货单位在系统中不存在，");
            }
            detail.setSendCityId(unitMap.get(detail.getSendUnitId()).getCityId());
            if(unitMap.get(detail.getReceiveUnitId()) == null){
                sb.append("收货单位在系统中不存在，");
            }
            if(unitMap.get(detail.getReceiveUnitId()) == null){
                sb.append("收货单位在系统中不存在，");
            }else {
                detail.setReceiveCityId(unitMap.get(detail.getReceiveUnitId()).getCityId());
            }

            if(sb.length() > 0){
                s.append("订单明细第"+(i+1)+"行：").append(sb);
            }
        }
        if(s.length() > 0){
            throw new BusinessException(s.toString());
        }
        //填充报价
        quotationDetailService.getQuotationByOrder(order.getCorpCustomerId(),order.getCorpCarrierId(),order.getOrderDate(),
                order.getWorkType(),details);

        if(out){
            // 获取对应的入库订单
            List<StockCar> stockCars = stockCarService.queryByVins(vins,order.getCorpCustomerId());
            List<StockCar> originals = new ArrayList<>();
            List<StockCar> forwards = new ArrayList<>();
            Map<String,OrderDetail> map = new HashMap<>();
            details.forEach( d -> map.put(d.getCarVin(),d));
            for (StockCar stockCar : stockCars) {
                if(stockCar.getOrderType() == OrderType.In.getIndex()){
                    originals.add(stockCar);
                }else{
                    forwards.add(stockCar);
                }
            }
            if(CollectionUtils.isNotEmpty(originals)){
                List<Long> detailIds = new ArrayList<>();
                originals.forEach( o -> detailIds.add(o.getOrderDetailId()));
                List<OrderDetail> oriDetials = orderDetailMapper.selectDetailByDetailIds(detailIds);
                oriDetials.forEach( detail -> {
                    map.get(detail.getCarVin()).setOriginalOrderId(detail.getOrderId());
                    map.get(detail.getCarVin()).setOriginalOrderDetailId(detail.getOrderDetailId());
                    map.get(detail.getCarVin()).setCustomerQuotedPrice(detail.getCustomerQuotedPrice());
                });
            }
            if(CollectionUtils.isNotEmpty(forwards)){
                //获取运单
                List<Long> detailIds = new ArrayList<>();
                forwards.forEach(f -> detailIds.add(f.getOrderDetailId()));
                List<OrderDetail> forDetails = orderDetailMapper.selectDetailByDetailIds(detailIds);
                forDetails.forEach( detail -> {
                    map.get(detail.getCarVin()).setOriginalOrderId(detail.getOriginalOrderId());
                    map.get(detail.getCarVin()).setOriginalOrderDetailId(detail.getOriginalOrderDetailId());
                    map.get(detail.getCarVin()).setCustomerQuotedPrice(detail.getCustomerQuotedPrice());
                });
            }
        }
        orderDetailMapper.insertSelective(details);
        if(!out){
            for (OrderDetail detail : details) {
                detail.setOriginalOrderId(detail.getOrderId());
                detail.setOriginalOrderDetailId(detail.getOrderDetailId());
            }
            orderDetailMapper.updateByPrimaryKeySelective(details);
        }
	}

	@Override
    @Transactional
	public void update(Order order){
	    if(order.getOrderId() == null){
	        throw new BusinessException("操作失败，请刷新页面后重试。");
        }
        if(order.getBillStatus() != 0){
            throw new BusinessException("非制单状态，不可修改。");
        }
        Environment env = Environment.getEnv();
        switch (OrderType.get(order.getOrderType())){
            case In:
                if(!order.getCorpCarrierId().equals(env.getCorp().getCorpId())){
                    throw new BusinessException("非本公司订单，不可修改。");
                }
                break; // 原属订单承运商默认为当前公司
            case Out:
                if(!order.getCorpCustomerId().equals(env.getCorp().getCorpId())){
                    throw new BusinessException("非本公司订单，不可修改。");
                }
                break; // 转包订单客户默认为当前公司
            case Section:
                if(!order.getCorpCustomerId().equals(env.getCorp().getCorpId())){
                    throw new BusinessException("非本公司订单，不可修改。");
                }
                break; // 分段订单客户默认为当前公司
            default: throw new BusinessException("订单类型错误！");
        }

        if(CollectionUtils.isEmpty(order.getOds())){
            throw new BusinessException("需要至少一条订单明细。");
        }

        //字段检查
        String check = checkColumn(order);
        if(check.trim().length() > 0){
            throw new BusinessException(check);
        }
        boolean changePrice = false;
        Order old = orderMapper.selectByPrimaryKey(order.getOrderId());
        if(!old.getCorpCustomerId().equals(order.getCorpCustomerId()) ||
                !old.getCorpCarrierId().equals(order.getCorpCarrierId())){
            changePrice = true;
        }
        //更新表头
        orderMapper.updateByPrimaryKeySelective(order);

        //获取表体校验信息
        Map<Long,Brand> brandMap = brandService.queryMap();
        Map<String,CarType> carTypeMap = carTypeService.queryMap();
        Map<String,Car> carMap = carService.queryMap();
        Map<Long,Unit> unitMap = unitService.queryMap();
        StringBuilder s = new StringBuilder();

        //保存表体
        List<OrderDetail> details = order.getOds();
        List<OrderDetail> adds = new ArrayList<>();
        List<OrderDetail> updates = new ArrayList<>();
        List<Long> dels = new ArrayList<>();
        for (int i = 0; i < details.size(); i++) {
            OrderDetail detail = details.get(i);
            if(changePrice){
                detail.setQuotedPriceId(null);
                detail.setClaimDeliveryDate(null);
                if(RowStatus.UNCHANGE.getValue() == detail.getRowStatus()){
                    detail.setRowStatus(RowStatus.EDIT.getValue());
                }
            }
            if(RowStatus.UNCHANGE.getValue() == detail.getRowStatus()){
                continue;
            }
            if(RowStatus.DELETE.getValue() == detail.getRowStatus()){
                dels.add(detail.getOrderDetailId());
                continue;
            }
            if(RowStatus.NEW.getValue() == detail.getRowStatus()) {
                detail.setOrderCode(order.getOrderCode());
                detail.setOrderId(order.getOrderId());
                detail.setCorpId(order.getCorpId());
                adds.add(detail);
            }
            if(RowStatus.EDIT.getValue() == detail.getRowStatus()){
                if(detail.getOrderDetailId() == null){
                    throw new BusinessException("参数传递错误，请刷新页面后重试。");
                }
                updates.add(detail);
            }

            //表体数据库检查
            StringBuilder sb = new StringBuilder();
            if(brandMap.get(detail.getBrandId()) == null){
                sb.append("品牌在系统中不存在，");
            }

            if(detail.getCarTypeId() != null && carTypeMap.get(detail.getBrandId()+"_"+detail.getCarTypeId()) == null){
                sb.append("车型类别在系统中不存在或与品牌不匹配，");
            }
            if(carMap.get(detail.getBrandId()+"_"+detail.getCarId()) == null){
                sb.append("商品车型号在系统中不存在或与品牌不匹配，");
            }
            if(unitMap.get(detail.getSendUnitId()) == null){
                sb.append("发货单位在系统中不存在，");
            }
            detail.setSendCityId(unitMap.get(detail.getSendUnitId()).getCityId());
            if(unitMap.get(detail.getReceiveUnitId()) == null){
                sb.append("收货单位在系统中不存在，");
            }
            if(unitMap.get(detail.getReceiveUnitId()) == null){
                sb.append("收货单位在系统中不存在，");
            }else {
                detail.setReceiveCityId(unitMap.get(detail.getReceiveUnitId()).getCityId());
            }
            if(sb.length() > 0){
                s.append("订单明细第"+(i+1)+"行：").append(sb);
            }
        }
        if(s.length() > 0){
            throw new BusinessException(s.toString());
        }

        //填充报价
        List<OrderDetail> allDetails = new ArrayList<>();
        allDetails.addAll(adds);
        allDetails.addAll(updates);
        if(CollectionUtils.isNotEmpty(allDetails)) {
            quotationDetailService.getQuotationByOrder(order.getCorpCustomerId(), order.getCorpCarrierId(), order.getOrderDate(),
                    order.getWorkType(), allDetails);
        }
        if(CollectionUtils.isNotEmpty(adds)) {
            orderDetailMapper.insert(adds);
        }
        if(CollectionUtils.isNotEmpty(updates)) {
            orderDetailMapper.updateByPrimaryKeySelective(updates);
        }
        if(CollectionUtils.isNotEmpty(dels)) {
            orderDetailMapper.deleteByPrimaryKey(dels);
        }
    }

    @Override
    @Transactional
    public void updateDetailSelective(List<OrderDetail> details){
	    orderDetailMapper.updateByPrimaryKeySelective(details);
    }

    @Override
    @Transactional
    public void delete(Long orderId){
        Order order = orderMapper.selectByPrimaryKey(orderId);
        Environment env = Environment.getEnv();
        checkOperate(order,env);
        if(order == null || order.getBillStatus() != 0){
            throw new BusinessException("订单不存在或非制单状态，无法删除。");
        }

        orderMapper.deleteByPrimaryKey(orderId);
        orderDetailMapper.deleteByOrderId(orderId);
    }


	@Override
    @Transactional
    public void check(Long orderId){
        Order order = orderMapper.selectByPrimaryKey(orderId);
        Environment env = Environment.getEnv();
        checkOperate(order,env);
        order.setChecker(env.getUser().getUserId());
        order.setCheckDate(new Date());
        order.setBillStatus(1);
        int i = orderMapper.check(order);
        if(i == 0){
            throw new BusinessException("当前订单不存在或已审核，请刷新当前页面数据后重试。");
        }
        stockCarService.stockForOrder(order,false);
    }

    @Override
    @Transactional
    public void unCheck(Long orderId){
        Order order = orderMapper.selectByPrimaryKey(orderId);
        Environment env = Environment.getEnv();
        checkOperate(order,env);
        order.setBillStatus(0);
        int i = orderMapper.unCheck(order);
        if(i == 0){
            throw new BusinessException("当前订单不存在或不是审核状态，请刷新当前页面数据后重试。");
        }
        stockCarService.stockForOrder(order,true);
    }

    @Override
    @Transactional
    public void release(Long orderId){
        Order order = orderMapper.selectByPrimaryKey(orderId);
        Environment env = Environment.getEnv();
        checkOperate(order,env);
        order.setTransmitDate(new Date());
        order.setSalesManId(env.getUser().getUserId());
        order.setBillStatus(2);
        int i = orderMapper.release(order);
        if(i == 0){
            throw new BusinessException("当前订单不存在或已下达，请刷新当前页面数据后重试。");
        }
        stockCarService.stockForOrder(order,false);
    }

    @Override
    @Transactional
    public void unRelease(Long orderId){
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if(OrderType.In.getIndex() != order.getOrderType()){
            List<OrderDetail> details = queryDetailByOrderId(orderId);
            for (OrderDetail detail : details) {
                if(detail.getCarStatus() != CarStatus.In.getIndex()){
                    throw new BusinessException("承运公司已进行后续操作，无法取消下达，请使用关闭订单操作！");
                }
            }
        }
        Environment env = Environment.getEnv();
        checkOperate(order,env);
        if(OrderType.In.getIndex() != order.getOrderType() && !order.getCorpCustomerId().equals(env.getCorp().getCorpId())){
            throw new BusinessException("此订单为客户下达的订单，无法取消下达。");
        }

        order.setTransmitDate(new Date());
        order.setSalesManId(env.getUser().getUserId());
        order.setBillStatus(1);
        int i = orderMapper.unRelease(order);
        if(i == 0){
            throw new BusinessException("当前订单不存在或不是下达状态，请刷新当前页面数据后重试。");
        }

        List<OrderDetail> details = queryDetailByOrderId(orderId);
        List<StockCar> stocks = new ArrayList<>();
        for (OrderDetail detail : details) {
            closeVin(detail.getOrderDetailId(), stocks);
        }

        stockCarService.closeCar(stocks);
//        stockCarService.stockForOrder(order,true);
    }

    @Override
    @Transactional
    public void addSection(Order order) {
        List<OrderDetail> details = order.getOds();
        List<OrderPieceVO> pieces = order.getPieceList();
        if(CollectionUtils.isEmpty(pieces)){
            throw new BusinessException("分段信息为空，无法创建分段订单。");
        }
        if(pieces.size() == 1){
            throw new BusinessException("只有一条分段信息，请使用转包订单。");
        }
        if(CollectionUtils.isEmpty(details)){
            throw new BusinessException("需要至少一条订单明细。");
        }

//        //字段检查
//        String check = checkColumn(order);
//        if(check.trim().length() > 0){
//            throw new BusinessException(check);
//        }
        // 检查分段信息
        for (int i = 0; i < pieces.size(); i++) {
            OrderPieceVO piece = pieces.get(i);
            Corp corp = corpService.queryById(piece.getCorpCustomerId());
            if(corp == null){
                throw new BusinessException("分段信息第"+(i+1)+"条，承运公司不存在！");
            }
            if(i == pieces.size()-1){
                break;
            }
            //检查中转单位
            Unit unit = unitService.queryById(piece.getUnitId());
            if(unit == null ){
                throw new BusinessException("分段信息第"+(i+1)+"条，中转单位不存在！");
            }
            piece.setCityId(unit.getCityId());
        }

        //生成分段订单
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
            for (OrderDetail detail : details) {
                if(i==0){
                    detail.setOriReceiveUnitId(detail.getReceiveUnitId());
                }
                detail.setOrderDetailId(null);
                detail.setOrderId(null);
                detail.setOrderCode(null);
                detail.setSendUnitId(sendUnitId==null?detail.getSendUnitId():sendUnitId);
                detail.setReceiveUnitId(receiveUnitId==null?detail.getOriReceiveUnitId():receiveUnitId);
                if( i > 0 ){
                    detail.setClaimLoadDate(detail.getClaimDeliveryDate());
                }
            }
            add(order);
        }
    }

	private String checkColumn(Order order){
		StringBuilder s = new StringBuilder();
		s.append(CheckUtil.checkNull(order.getOrderDate(),false,"订单日期"));
		s.append(CheckUtil.checkNull(order.getCorpCustomerId(),false,"客户"));
		s.append(CheckUtil.checkNull(order.getCorpCarrierId(),false,"承运公司"));
		s.append(CheckUtil.checkNull(order.getWorkType(),false,"作业类型"));
		s.append(CheckUtil.checkString(order.getBatchNumber(),true,20,"批次号"));
		s.append(CheckUtil.checkString(order.getRemark(),true,200,"备注"));
		if(OperationType.get(order.getWorkType()) == null){
			s.append("错误的作业类型");
		}
		if(CollectionUtils.isEmpty(order.getOds())){
            s.append("订单明细不能为空！");
        }else {
            List<OrderDetail> details = order.getOds();
            List<String> vins = new ArrayList<>();
            for (int i = 0; i < details.size(); i++) {
                StringBuilder sb = new StringBuilder();
                OrderDetail detail = details.get(i);
                sb.append(CheckUtil.checkNull(detail.getCarId(), false, "商品车型号"));
//                sb.append(CheckUtil.checkNull(detail.getCarTypeId(), false, "车型类别"));
                sb.append(CheckUtil.checkString(detail.getCarVin(), false, 20, "车架号"));
                if(vins.contains(detail.getCarVin())){
                    sb.append("车架号["+detail.getCarVin()+"]重复，");
                }else{
                    vins.add(detail.getCarVin());
                }
                sb.append(CheckUtil.checkNull(detail.getBrandId(), false, "品牌"));
                sb.append(CheckUtil.checkNull(detail.getSendUnitId(), false, "发货单位"));
                sb.append(CheckUtil.checkNull(detail.getReceiveUnitId(), false, "收货单位"));
                sb.append(CheckUtil.checkString(detail.getInstructionNumber(), true, 20, "指示书号"));
                sb.append(CheckUtil.checkNull(detail.getClaimLoadDate(), false, "指定装车日期"));
                sb.append(CheckUtil.checkString(detail.getProductNumber(), true, 20, "生产号"));
                sb.append(CheckUtil.checkString(detail.getRemark(), true, 200, "备注"));
                if (sb.length() > 0) {
                    s.append("订单明细第" + (i + 1) + "行：").append(sb);
                }
            }
        }

		return s.toString();
	}

	private String getOrderCode(){
		SimpleDateFormat sf = new SimpleDateFormat("YYYYMMDDHHmmss");
		return sf.format(new Date());
	}

	private void checkOperate(Order order, Environment env){
        switch (OrderType.get(order.getOrderType())){
            case In:
                if(!order.getCorpCarrierId().equals(env.getCorp().getCorpId())){
                    throw new BusinessException("非本公司订单，不可操作。");
                }
                break; // 原属订单承运商默认为当前公司
            case Out:
                if(!order.getCorpCustomerId().equals(env.getCorp().getCorpId())){
                    throw new BusinessException("非本公司订单，不可操作。");
                }
                break; // 转包订单客户默认为当前公司
            case Section:
                if(!order.getCorpCustomerId().equals(env.getCorp().getCorpId())){
                    throw new BusinessException("非本公司订单，不可操作。");
                }
                break; // 分段订单客户默认为当前公司
            default: break;
        }
    }

	@Override
	public Order queryById(Long orderId) {
		try {
			return orderMapper.selectByPrimaryKey(orderId);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
    @Transactional
	public void closeOrder(Long orderId){
        Order order = orderMapper.selectByPrimaryKey(orderId);
	    if(order.getBillStatus() != 2){
	        throw new BusinessException("非下达状态的订单不可关闭，您可以取消审核后删除。");
        }
        List<OrderDetail> details = queryDetailByOrderId(orderId);
        List<StockCar> stocks = new ArrayList<>();
        for (OrderDetail detail : details) {
            closeVin(detail.getOrderDetailId(), stocks);
        }
        stockCarService.closeCar(stocks);
        // 其它订单 取消计划=库存入库
        if(order.getOrderType() != OrderType.In.getIndex()){
            List<StockCar> newStocks = new ArrayList<>();
            for (OrderDetail detail : details) {
                StockCar stock = stockCarService.queryByOrderDetailId(detail.getOrderDetailId());
                StockCar newStock = new StockCar();
                newStock.setStockCarId(stock.getStockCarId());
                newStock.setCarStatus(CarStatus.In.getIndex());
                newStocks.add(newStock);
            }
            stockCarService.updateBySelective(newStocks);
        }else{
            List<Long> detailIds = new ArrayList<>();
            for (StockCar stock : stocks) {
                detailIds.add(stock.getOrderDetailId());
            }
            updateDetailForClose(detailIds,"计划取消");
            Order newOrder = new Order();
            newOrder.setOrderId(orderId);
            newOrder.setBillStatus(3);
            orderMapper.updateByPrimaryKeySelective(newOrder);
        }
    }

    @Override
    @Transactional
    public void closeVin(Long detailId, String reason){
	    OrderDetail detail = orderDetailMapper.selectByPrimaryKey(detailId);
	    Order order = orderMapper.selectByPrimaryKey(detail.getOrderId());
        if(order.getBillStatus() != 2){
            throw new BusinessException("非下达状态的订单不可关闭，您可以取消审核后删除。");
        }
        List<StockCar> stocks = new ArrayList<>();
        closeVin(detail.getOrderDetailId(), stocks);
        stockCarService.closeCar(stocks);
        // 更新订单表体

        if(order.getOrderType() != OrderType.In.getIndex()){
            StockCar stock = stockCarService.queryByOrderDetailId(detail.getOrderDetailId());
            StockCar newStock = new StockCar();
            newStock.setStockCarId(stock.getStockCarId());
            newStock.setCarStatus(CarStatus.In.getIndex());
            stockCarService.updateBySelective(newStock);
        }else{
            List<Long> detailIds = new ArrayList<>();
            for (StockCar stock : stocks) {
                detailIds.add(stock.getOrderDetailId());
            }
            updateDetailForClose(detailIds,reason);
        }

    }

    @Override
    public void closeVin(Long detailId, List<StockCar> stocks){
	    // 获取库存信息
        StockCar stock = stockCarService.queryByOrderDetailId(detailId);
        if(stock.getCarStatus() == CarStatus.Closed.getIndex()){
            return;
        }
        // 入库状态: 无后续操作，直接关闭
        if(stock.getCarStatus() == CarStatus.In.getIndex()){
            stocks.add(stock);
            return;
        }

        // 出库状态：获取后续单据
        if(stock.getCarStatus() == CarStatus.Out.getIndex()){
            List<Long> stockIds = new ArrayList<>();
            stockIds.add(stock.getStockCarId());
            List<StockCarOut> outs = stockCarOutService.queryByStockIds(stockIds);
            for (StockCarOut out : outs) {
                closeVin(out.getOrderDetailId(),stocks);
            }
        }

       /* // 运输状态：大于等于3 < 9 可关闭
        if(stock.getCarStatus() >= 3){
            // 调用运单取消接口
            List<String> vins = new ArrayList<>();
            vins.add(stock.getCarVin());
            transportOrderService.updateCancelTransmit(vins);
        }*/

        if(stock.getCarStatus() == CarStatus.OnTheWay.getIndex()){
            throw new BusinessException("车架号【"+stock.getCarVin()+"]已在途，无法取消。");
        }
        if(stock.getCarStatus() == CarStatus.Arrived.getIndex()){
            throw new BusinessException("车架号【"+stock.getCarVin()+"]已到店，无法取消。");
        }

        // 运输状态：大于等于3 < 9 可关闭
        if(stock.getCarStatus() >= 3){
            // 调用运单取消接口
            List<String> vins = new ArrayList<>();
            vins.add(stock.getCarVin());
            transportOrderService.updateCancelTransmit(vins);
        }

    }

    @Override
    @Transactional
    public void updateDetailForClose(List<Long> orderDetailIds, String reason){
        List<OrderDetail> details = new ArrayList<>();
        for (Long orderDetilId : orderDetailIds) {
            OrderDetail detail = new OrderDetail();
            detail.setOrderDetailId(orderDetilId);
            detail.setCarStatus(CarStatus.Closed.getIndex());
            detail.setCancelReason(reason);
            details.add(detail);
        }
        orderDetailMapper.updateByPrimaryKeySelective(details);
    }

	@Override
	public Order getOriginOrder(Map<String, Object> map) {
		try {
			if (map.get("carVin")!=null) {
				return orderMapper.getOriginOrder(map);
			}
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

    @Override
    public Order queryByOrderNumber(String orderNumber) {

        try {
            Order order = orderMapper.queryByOrderNumber(orderNumber);
            return order;
        } catch (Exception e) {
            log.error("{}", e);
            throw e;
        }
    }
    /**
     * 通过客户查询原始订单并去重
     */
	@Override
	@Transactional
	public PageInfo<Order> getOriginOrder(Map<String, Object> map, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		if (map==null) {
			map=new HashMap<String, Object>();
		}
		map.put("orderType", 0);
		List<Order> orders=orderMapper.selectList(map);
		Map<Long, List<Order>> m=orders.stream().collect(Collectors.groupingBy(Order::getCorpCustomerId));
		List<Long> corpCustomerIds=m.keySet().stream().collect(Collectors.toList());
		
		List<Order> list=new ArrayList<Order>();
		for (int i = 0; i < corpCustomerIds.size(); i++) {
			Corp corp=corpService.queryById(corpCustomerIds.get(i));
			Order order=new Order();
			order.setCorpCustomerId(corpCustomerIds.get(i));
			order.setRefCorpCustomerName(corp.getCorpName());
			list.add(order);
			
		}
		return new PageInfo<Order>(list);
	}
}
