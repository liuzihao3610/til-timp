package com.tilchina.timp.support;

import com.tilchina.timp.model.Car;
import com.tilchina.timp.model.Cargo;
import com.tilchina.timp.util.CopyUtil;
import com.tilchina.timp.vo.CityAreaGroupVO;
import com.tilchina.timp.vo.PendingOrderVO;
import com.tilchina.timp.vo.AssemblyResultVO;
import com.tilchina.timp.vo.TransporterCountVO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 
 * 
 * @version 1.0.0 2018/5/18
 * @author WangShengguang   
 */ 
public class AssemblyCitySupport {

    Map<String,List<AssemblyResultVO>> resultMap = new HashMap<>();

    public List<List<AssemblyResultVO>> run(List<PendingOrderVO> orders,List<TransporterCountVO> counts){
        if(CollectionUtils.isEmpty(orders)){
            return null;
        }
        if(CollectionUtils.isEmpty(counts)){
            return null;
        }
        test(orders,counts,null,0,0,UUID.randomUUID().toString());
        if(resultMap.size() == 0){
            return null;
        }

        // 初步过滤，获取最优解
        List<String> tags = new ArrayList<>();
        // 识别最优结果：装车数最多、经销店数量最少
        int maxTrans = 0;
        int minUnit = 999;
        for (String s : resultMap.keySet()) {
            List<AssemblyResultVO> results = resultMap.get(s);
            if(results.size() > maxTrans){
                maxTrans = results.size();
            }else{
                continue;
            }

        }

        for (String s : resultMap.keySet()) {
            List<AssemblyResultVO> results = resultMap.get(s);
            if(results.size() == maxTrans){
                int unitSize = getUnitSize(results);
                if(unitSize < minUnit){
                    minUnit = unitSize;
                }
            }
        }

        for (String s : resultMap.keySet()) {
            List<AssemblyResultVO> results = resultMap.get(s);
            if(results.size() == maxTrans){
                if(getUnitSize(results) == minUnit){
                    tags.add(s);
                }
            }
        }

        List<List<AssemblyResultVO>> results = new ArrayList<>();
        for (String tag : tags) {
            results.add(resultMap.get(tag));
        }

        return results;
    }

    public void test(List<PendingOrderVO> orders, List<TransporterCountVO> counts, AssemblyResultVO result, int oIndex,
                     int countIndex, String tag){
        if(oIndex == orders.size()-1){
            return;
        }
        // 获取板车信息
        if(result == null){
            for (int c = countIndex; c < counts.size(); c++) {
                if(counts.get(c).getCount() > counts.get(c).getTempUseCount()
                        && orders.size() >= counts.get(c).getMax()){
                    result = new AssemblyResultVO();
                    result.setTrailer(counts.get(c).getTrailer());
                    result.setTrailerId(counts.get(c).getTrailerId());
                    result.setCargos(counts.get(c).getCargos());
                    result.setMax(counts.get(c).getMax());

                    counts.get(c).setTempUseCount(counts.get(c).getTempUseCount()+1);
                    break;
                }
                countIndex = c;
            }
            if(result == null){
                return;
            }
        }
        AssemblyResultVO temp;
        for (int i = oIndex; i < orders.size(); i++) {
            if(CollectionUtils.isNotEmpty(result.getOrders()) && result.getOrders().contains(orders.get(i))){
                continue;
            }
            if(orders.get(i).isUsed()){
                continue;
            }

            // 装配
            temp = copyResult(result);
            if(check(orders.get(i),result)){
                result.addOrder(orders.get(i));
//                test(orders,counts,result,i+1,countIndex,tag);
            }else{
                continue;
            }
            if(result.isFull()){
                if(result.getOrders().size() < result.getMax()){
                    return;
                }
                // 装满一辆车
                String newTag = UUID.randomUUID().toString();
                // 处理结果
                List<AssemblyResultVO> results = resultMap.get(tag);
                if(CollectionUtils.isEmpty(results)){
                    results = new ArrayList<>();
                    results.add(result);
                    resultMap.put(newTag,results);
                }else{
                    List<AssemblyResultVO> newResults = CopyUtil.copyList(results);
                    newResults.add(result);
                    resultMap.put(newTag,newResults);
                }
                // 继续装配下一辆
                test(orders,counts,null,i+1,countIndex,newTag);
            }
            test(orders,counts,result,i+1,countIndex,tag);
            // 回退，遍历下一个
            result = temp;
        }
    }

    private boolean check(PendingOrderVO order, AssemblyResultVO result){

        Cargo cargo;
        Integer useLong = 0;
        if(result.getUseIndex() >= result.getMax()){
            cargo = result.getCargos().get(result.getMax()-1);
        }else{
            cargo = result.getCargos().get(result.getUseIndex());
            useLong = cargo.getCargoLong();
        }

        if(result.getUseIndex() != 0 && result.getLastLong() > 0){
            useLong = useLong + result.getLastLong();
        }
        Car car = order.getCar();
        if(useLong >= car.getCarLong() && cargo.getCargoWidth() >= car.getCarWidth() && cargo.getCargoHigh() >= car.getCarHigh()){
            if(1 == cargo.getCarryOver().intValue()){
                result.setLastLong(useLong - car.getCarLong());
            }else{
                result.setLastLong(0);
            }
            if(result.getUseIndex() >= result.getMax() - 1 && result.getLastLong() < 2500){
                result.setFull(true);
            }

            result.setUseIndex(result.getUseIndex() + 1);
            return true;
        }
        return false;
    }

    private AssemblyResultVO copyResult(AssemblyResultVO result){
        AssemblyResultVO newResult = new AssemblyResultVO();
        newResult.setTrailerId(result.getTrailerId());
        newResult.setTrailer(result.getTrailer());
        newResult.setCargos(result.getCargos());
        newResult.setMax(result.getMax());
        newResult.setLastLong(result.getLastLong());
        newResult.setUseIndex(result.getUseIndex());
        newResult.setFull(result.isFull());
        return newResult;
    }

    private int getUnitSize(List<AssemblyResultVO> results){
        int size = 0;
        for (AssemblyResultVO result : results) {
            List<PendingOrderVO> orders = result.getOrders();
            Map<Long,List<PendingOrderVO>> unitMap = orders.stream().collect(Collectors.groupingBy(PendingOrderVO::getReceiveUnitId));
            size += unitMap.size();
        }
        return size;
    }
}
