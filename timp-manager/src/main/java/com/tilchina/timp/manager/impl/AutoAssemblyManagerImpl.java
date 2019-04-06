package com.tilchina.timp.manager.impl;

import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.AssemblyType;
import com.tilchina.timp.enums.OperationType;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.manager.AutoAssemblyManager;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import com.tilchina.timp.support.AssemblyCitySupport;
import com.tilchina.timp.util.GaodeApi;
import com.tilchina.timp.vo.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author WangShengguang
 * @version 1.0.0 2018/5/7
 */
@Service
public class AutoAssemblyManagerImpl implements AutoAssemblyManager {

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private TrailerService trailerService;

    @Autowired
    private TransporterAndDriverService transporterAndDriverService;

    @Autowired
    private CargoService cargoService;

    @Autowired
    private RouteService routeService;

    @Autowired
    private CarService carService;

    @Autowired
    private FreightService freightService;

    @Autowired
    private AssemblyResultService assemblyResultService;

    @Override
    public void assembly(AssemblyParam param){
        assembly(param.getTransportCorpId(),param.getSendCityId(),param.getOperationType(),param.getAssemblyType(),param.getTransporterType(),param.getCounts());
    }

    /**
     *
     * @since 1.0.0
     * @param transportCorpId   运输公司
     * @param sendCityId        起运地城市
     * @param operationType     作业类型
     * @param assemblyType      配板方案
     * @param transporterType   运力获取方式
     * @param counts            自定义运力列表
     * @return void
     */
    @Override
    public void assembly(Long transportCorpId, Long sendCityId, Integer operationType, Integer assemblyType, Integer transporterType, List<TransporterCountVO> counts) {
        Environment env = Environment.getEnv();
        if(transportCorpId == null){
            transportCorpId = env.getCorp().getCorpId();
        }
        if (transportCorpId == null || operationType == null || StringUtils.isBlank(OperationType.getName(operationType))
                || assemblyType == null || StringUtils.isBlank(AssemblyType.getName(assemblyType))){
            throw new BusinessException("9003"); // 请求参数错误
        }

        // 获取待配板车辆明细
        Map<String,Object> map = new HashMap<>();
        map.put("corpCarrierId",transportCorpId);
        map.put("sendCityId",sendCityId);
        map.put("workType",operationType);
        List<PendingOrderVO> orders = orderDetailService.queryForAssembly(map);

        if(CollectionUtils.isEmpty(orders)){
            throw new BusinessException("没有待配板的订单。");
        }
        //获取运价
        for (PendingOrderVO order : orders) {
            BigDecimal price = freightService.getPrice(order.getSendCityId(),order.getReceiveCityId(),order.getBrandId(),order.getCarTypeId(),order.getOrderDate());
            order.setTransPrice(price == null?BigDecimal.ZERO:price);
        }

        // 获取车型信息，并关联到订单明细
        Map<Long,List<PendingOrderVO>> carIds = orders.stream().collect(Collectors.groupingBy(PendingOrderVO::getCarId));
        List<Car> cars = carService.queryByCarIds(carIds.keySet());
        Map<Long,Car> carMap = new HashMap<>();
        for (Car car : cars) {
            carMap.put(car.getCarId(),car);
        }
        for (PendingOrderVO order : orders) {
            order.setCar(carMap.get(order.getCarId()));
        }

        // 按虚拟城市分组
        List<CityAreaGroupVO> groups = buildGroup(orders);
        initDistance(groups);

        // 获取运力信息
        if(transporterType == 0){
            List<Map<String,Object>> list = transporterAndDriverService.queryIdleTransporter();
            counts = new ArrayList<>();
            for (Map<String, Object> stringObjectMap : list) {
                Long trailerId = Long.valueOf(stringObjectMap.get("trailerId").toString());
                Integer count = Integer.valueOf(stringObjectMap.get("num").toString());
                if(count == null || count == 0){
                    continue;
                }
                TransporterCountVO vo = new TransporterCountVO();
                vo.setTrailerId(trailerId);
                vo.setCount(count);
                counts.add(vo);
            }
        }

        List<Long> trailerIds = new ArrayList<>();
        for (TransporterCountVO count : counts) {
            if(count.getCount() == null || count.getCount() == 0){
                continue;
            }
            trailerIds.add(count.getTrailerId());
        }
        List<Trailer> trailers = trailerService.queryByList(trailerIds);

        // 获取货物装配信息
        List<Cargo> cargos = cargoService.queryByTrailers(trailerIds);
        //构建可用运力对象
        buildTransporter(counts,trailers,cargos);

        //获取虚拟线路及虚拟城市
        List<Route> routes = routeService.queryByRouteType(assemblyType);

        //开始配板
        List<BestAssemblyResultVO> assemblies = build(groups, counts, routes, assemblyType);

        //保存结果
        saveResult(assemblies);
    }

    @Override
    @Transactional
    public void saveResult(List<BestAssemblyResultVO> assemblies){
        List<AssemblyResult> rs = new ArrayList<>();
        // 保存结果
        for (BestAssemblyResultVO assembly : assemblies) {
            List<AssemblyResultVO> results = assembly.getResults();
            for (AssemblyResultVO result : results) {
                rs.add(buildResult(result));
            }
        }
        assemblyResultService.add(rs);
    }

    private AssemblyResult buildResult(AssemblyResultVO vo){
        AssemblyResult result = new AssemblyResult();
        SimpleDateFormat sf = new SimpleDateFormat("YYYYMMDDHHmmss");
        result.setResultCode(sf.format(new Date()));
        Environment env = Environment.getEnv();
        result.setCreator(env.getUser().getUserId());
        result.setCreateDate(new Date());
        result.setBillStatus(0);
        // 填充挂车信息
        result.setTrailerId(vo.getTrailerId());
        result.setMaxQuantity(vo.getMax());
        result.setCarCount(vo.getOrders().size());

        // 构建表体
        BigDecimal allTransPrice = BigDecimal.ZERO;
        BigDecimal allCustomerPrice = BigDecimal.ZERO;
        BigDecimal baseTransPrice = BigDecimal.ZERO;
        BigDecimal allSubsidy = BigDecimal.ZERO;

        List<PendingOrderVO> orders = vo.getOrders();
        for (PendingOrderVO order : orders) {
            AssemblyResultDetail detail = new AssemblyResultDetail();
            result.addItem(detail);

            detail.setOrderId(order.getOrderId());
            detail.setOrderDetailId(order.getOrderDetailId());
            detail.setCarVin(order.getCarVin());

            //汇总信息
            baseTransPrice = baseTransPrice.add(order.getTransPrice());
            allCustomerPrice = allCustomerPrice.add(order.getCustomerPrice());
        }

        //获取城市数量
        Map<String,List<PendingOrderVO>> cityMap = orders.stream().collect(Collectors.groupingBy(PendingOrderVO::getReceiveCityName));
        //获取经销店数量
        Map<String,List<PendingOrderVO>> unitMap = orders.stream().collect(Collectors.groupingBy(PendingOrderVO::getReceiveUnitName));

        if(cityMap.size() > 1){
            allSubsidy = allSubsidy.add(new BigDecimal(cityMap.size() * 100));
        }
        if(unitMap.size() > 2){
            allSubsidy = allSubsidy.add(new BigDecimal((unitMap.size()-2) * 50));
        }
        result.setCityCount(cityMap.size());
        result.setUnitCount(unitMap.size());
        result.setAllSubsidy(allSubsidy);
        result.setBaseTransPrice(baseTransPrice);

        allTransPrice = baseTransPrice.add(allSubsidy);
        result.setAllTransPrice(allTransPrice);

        if(allTransPrice.compareTo(BigDecimal.ZERO) == 0 || allCustomerPrice.compareTo(BigDecimal.ZERO) == 0){
            result.setRate(BigDecimal.ZERO);
        }else {
            BigDecimal rate = allTransPrice.divide(allCustomerPrice, 2, BigDecimal.ROUND_HALF_UP);
            result.setRate(BigDecimal.ONE.subtract(rate));
        }

        return result;
    }

    private List<BestAssemblyResultVO> build(List<CityAreaGroupVO> groups, List<TransporterCountVO> counts, List<Route> routes, Integer assemblyType){
        // 配板结果
        List<BestAssemblyResultVO> assemblies = new ArrayList<>();
        // 按距离从远至近排序
        groups = groups.stream().sorted(Comparator.comparing(CityAreaGroupVO::getDistance).reversed())
                .collect(Collectors.toList());
        //运力按装车数量由多到少
        counts = counts.stream().sorted(Comparator.comparing(TransporterCountVO::getMax).reversed())
                .collect(Collectors.toList());

        // 同城配板
        for (CityAreaGroupVO group : groups) {
            List<PendingOrderVO> orders = group.getOrders();
            AssemblyCitySupport support = new AssemblyCitySupport();
            List<List<AssemblyResultVO>> results = support.run(orders,counts);
            if(CollectionUtils.isEmpty(results)){
                continue;
            }
            // 获取最优结果,同城只要经销店补贴最少
            BestAssemblyResultVO best = getBestCityResult(results);
            assemblies.add(best);
            // 更新订单及车辆状态
            updateResult(best,counts);
        }


        boolean hasNext = true;
        List<Long> notUsed = new ArrayList<>();
        while(hasNext){
            groups = sorted(groups,assemblyType.intValue());
            // 检查是否继续
            if(CollectionUtils.isEmpty(groups)){
                break;
            }
            if(groups.get(0).getLastQuantity() == 0){
                break;
            }
            Optional<TransporterCountVO> countVO = counts.stream().filter( count -> count.getCount()-count.getUseCount()>0).findFirst();
            if(!countVO.isPresent()){
                break;
            }
            CityAreaGroupVO group = groups.get(0);

            // 获取相关路线
            List<Route> useRoutes = new ArrayList<>();
            routes.forEach( route -> {
                if(route.getCityAreas().contains(group.getCityAreaId())){
                    useRoutes.add(route);
                }
            });
            // 按虚拟城市分组
            Map<Long,List<CityAreaGroupVO>> cityMap = groups.stream().collect(Collectors.groupingBy(CityAreaGroupVO::getCityAreaId));
            Map<Long,List<CityAreaGroupVO>> routeMap = new HashMap<>();

            for (Route useRoute : useRoutes) {
                if(notUsed.contains(useRoute.getRouteId())){
                    continue;
                }
                List<CityAreaGroupVO> cities = new ArrayList<>();
                useRoute.getCityAreas().forEach( id -> cities.addAll(cityMap.get(id)));
                routeMap.put(useRoute.getRouteId(),cities);
            }
            // 检查路线是否可用
            routeMap.forEach((k,v) ->{
                int count = 0;
                for (CityAreaGroupVO g : v) {
                    count += g.getLastQuantity();
                }
                if(count < countVO.get().getMax()){
                    routeMap.remove(k);
                }
            });
            if(routeMap.size() == 0){
                break;
            }

            // 多路线最优结果汇总
            List<BestAssemblyResultVO> bests = new ArrayList<>();
            // 自动配板
            for (Long aLong : routeMap.keySet()) {
                List<CityAreaGroupVO> useGroups = routeMap.get(aLong);
                List<PendingOrderVO> useOrders = new ArrayList<>();
                useGroups.forEach( g -> useOrders.addAll(g.getOrders().stream().filter( o -> !o.isUsed()).collect(Collectors.toList())));
                AssemblyCitySupport support = new AssemblyCitySupport();
                List<List<AssemblyResultVO>> results = support.run(useOrders,counts);
                if(CollectionUtils.isEmpty(results)){
                    notUsed.add(aLong);
                    continue;
                }
                // 获取路线最优结果
                BestAssemblyResultVO best = getBestResult(results);
                bests.add(best);
            }
            if(CollectionUtils.isEmpty(bests)){
                notUsed.addAll(routeMap.keySet());
                continue;
            }
            bests = bests.stream().sorted(Comparator.comparing(BestAssemblyResultVO::getRate).reversed()).collect(Collectors.toList());
            assemblies.add(bests.get(0));
            // 更新订单及车辆状态
            updateResult(bests.get(0),counts);
        }

        return assemblies;
    }

    private List<CityAreaGroupVO> sorted(List<CityAreaGroupVO> groups, int assemblyType){
        for (CityAreaGroupVO group : groups) {
            List<PendingOrderVO> orders = group.getOrders();
            List<PendingOrderVO> lasts = orders.stream().filter( order -> !order.isUsed()).collect(Collectors.toList());
            group.setLastQuantity(lasts.size());
        }
        //如果是清仓模式由远至近，如果是利润模式由多到少
        if( 0 == assemblyType ){
            // 统计订单余量
            return groups.stream().filter(group -> group.getLastQuantity() > 0)
                    .sorted(Comparator.comparing(CityAreaGroupVO::getLastQuantity).reversed())
                    .collect(Collectors.toList());
        }else{
            return groups.stream().filter(group -> group.getLastQuantity() > 0)
                    .sorted(Comparator.comparing(CityAreaGroupVO::getDistance).reversed()).collect(Collectors.toList());
        }
    }

    private BestAssemblyResultVO getBestCityResult(List<List<AssemblyResultVO>> results){
        if(results.size() == 1){
            BestAssemblyResultVO vo = new BestAssemblyResultVO();
            vo.setResults(results.get(0));
            return vo;
        }

        List<BestAssemblyResultVO> bests = new ArrayList<>();
        for (List<AssemblyResultVO> result : results) {
            BestAssemblyResultVO vo = new BestAssemblyResultVO();
            vo.setResults(result);
            bests.add(vo);
            for (AssemblyResultVO assemblyResultVO : result) {
                List<PendingOrderVO> orders = assemblyResultVO.getOrders();
                Map<Long,List<PendingOrderVO>> map = orders.stream().collect(Collectors.groupingBy(PendingOrderVO::getReceiveUnitId));
                if(map.size() > 2){
                    int price = vo.getMoreCityPrice()+(map.size()-2)*50;
                    vo.setMoreCityPrice(price);
                }
            }
        }

        bests = bests.stream().sorted(Comparator.comparing(BestAssemblyResultVO::getMoreCityPrice)).collect(Collectors.toList());
        return bests.get(0);
    }



    private void updateResult(BestAssemblyResultVO best,List<TransporterCountVO> counts){
        Map<Long,TransporterCountVO> countMap = new HashMap<>();
        for (TransporterCountVO count : counts) {
            countMap.put(count.getTrailerId(),count);
        }
        for (AssemblyResultVO assemblyResultVO : best.getResults()) {
            TransporterCountVO count = countMap.get(assemblyResultVO.getTrailerId());
            count.setUseCount(count.getUseCount()+1);
            count.setTempUseCount(count.getUseCount());

            List<PendingOrderVO> orders = assemblyResultVO.getOrders();
            for (PendingOrderVO order : orders) {
                order.setUsed(true);
            }
        }
    }

    private BestAssemblyResultVO getBestResult(List<List<AssemblyResultVO>> results){
        List<BestAssemblyResultVO> bests = new ArrayList<>();
        for (List<AssemblyResultVO> result : results) {
            BigDecimal rate = getRate(result);
            BestAssemblyResultVO best = new BestAssemblyResultVO();
            best.setRate(rate);
            best.setResults(result);
            bests.add(best);
        }
        bests = bests.stream().sorted(Comparator.comparing(BestAssemblyResultVO::getRate).reversed()).collect(Collectors.toList());
        return bests.get(0);
    }

    private BigDecimal getRate(List<AssemblyResultVO> results){
        BigDecimal allCustomer = BigDecimal.ZERO;
        BigDecimal allTrans = BigDecimal.ZERO;
        for (AssemblyResultVO result : results) {
            List<PendingOrderVO> orders = result.getOrders();
            for (PendingOrderVO order : orders) {
                allCustomer = allCustomer.add(order.getCustomerPrice());
                allTrans = allTrans.add(order.getTransPrice());
            }
        }

        return BigDecimal.ONE.subtract(allTrans.divide(allCustomer,2,BigDecimal.ROUND_HALF_UP));
    }


    private void buildTransporter(List<TransporterCountVO> counts, List<Trailer> trailers,List<Cargo> cargos){
        Map<Long,List<Cargo>> cargoMap = cargos.stream().collect(Collectors.groupingBy(Cargo::getTrailerId));
        for (TransporterCountVO count : counts) {
            Optional<Trailer> trailer = trailers.stream().filter(t -> t.getTrailerId().equals(count.getTrailerId()))
                    .findFirst();
            if(trailer.isPresent()){
                count.setTrailer(trailer.get());
                count.setMax(trailer.get().getMaxQuantity());
            }
            count.setCargos(cargoMap.get(count.getTrailerId()));
        }
    }

    private List<CityAreaGroupVO> buildGroup(List<PendingOrderVO> orders){
        Map<Long,List<PendingOrderVO>> map = orders.stream().collect(Collectors.groupingBy(PendingOrderVO::getCityAreaId));
        List<CityAreaGroupVO> groups = new ArrayList<>();
        for (Long key : map.keySet()) {
            CityAreaGroupVO vo = new CityAreaGroupVO();
            vo.setCityAreaId(key);
            vo.setSendCityId(map.get(key).get(0).getSendCityId());
            vo.setSendCityName(map.get(key).get(0).getSendCityName());
            vo.setOrders(map.get(key));
            vo.setLastQuantity(map.get(key).size());
            vo.setSettlementCityId(map.get(key).get(0).getSettlementCityId());
            vo.setSettlementCityName(map.get(key).get(0).getSettlementCityName());
            groups.add(vo);
        }
        return groups;
    }

    private void initDistance(List<CityAreaGroupVO> groups){
        for (CityAreaGroupVO group : groups) {
            Double distance = GaodeApi.getDistanceByAddress(group.getSendCityName(),group.getSettlementCityName());
            group.setDistance(distance);
        }
    }



}
