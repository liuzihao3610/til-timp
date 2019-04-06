package com.tilchina.timp.support;

import com.tilchina.timp.util.CopyUtil;
import com.tilchina.timp.vo.BackTraceVO;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

/**
 * Created by demon on 2018/2/5.
 */
public class BestRouteActual {

    int c = 0;

    Map<String,List<List<BackTraceVO>>> map = new HashMap<>();

    public static void main(String[] args){
        BestRouteActual t = new BestRouteActual();
        int[] orders = {2,2,1,1,2,2,1,1};
        Integer[] cargos = {7,7,7,7};
        Vector<BackTraceVO> v = new Vector<>();
        BackTraceVO v1 = new BackTraceVO(1L,2);
        v.add(v1);
        BackTraceVO v2 = new BackTraceVO(1L,2);
        v.add(v2);
        BackTraceVO v3 = new BackTraceVO(1L,1);
        v.add(v3);
        BackTraceVO v4 = new BackTraceVO(1L,1);
        v.add(v4);

        BackTraceVO v5 = new BackTraceVO(2L,2);
        v.add(v5);
        BackTraceVO v6 = new BackTraceVO(2L,2);
        v.add(v6);
        BackTraceVO v7 = new BackTraceVO(2L,1);
        v.add(v7);
        BackTraceVO v8 = new BackTraceVO(2L,1);
        v.add(v8);

        BackTraceVO[] traces = new BackTraceVO[v.size()];
        v.toArray(traces);


        t.run(traces,cargos);
    }

    public List<List<List<BackTraceVO>>> run(BackTraceVO[] traces, Integer[] cargos){

        int orderCount = orderCount(traces);

        try {
            test(traces, cargos,0,0, 0, new ArrayList<>(), null);
        }catch(Exception e){
            e.printStackTrace();
        }

        int cars = 0;
        for (int i = 0; i < cargos.length; i++) {
            orderCount -= cargos[i];
            if(orderCount < 0){
                break;
            }
            cars = i+1;
        }

        int maxSize = 999;
        int minLength = 999;
        for (String key : map.keySet()) {
            int groupSize = 0;
            int groupLength = 0;
            if(map.get(key).size() != cars){
                continue;
            }
            for (List<BackTraceVO> list : map.get(key)) {
                if(list.size() > groupSize){
                    groupSize = list.size();
                }
//                groupLength += list.size();
            }
            if(maxSize > groupSize){
                maxSize = groupSize;
            }
//            if(minLength > groupLength){
//                minLength = groupLength;
//            }
        }
        for (String key : map.keySet()) {
            int groupLength = 0;
            if(map.get(key).size() != cars){
                continue;
            }
            if(checkSize(map.get(key),maxSize)){
                continue;
            }
            for (List<BackTraceVO> list : map.get(key)) {
                groupLength += list.size();
            }
            if(minLength > groupLength){
                minLength = groupLength;
            }
        }

        List<List<List<BackTraceVO>>> result = new ArrayList<>();
        for (String key : map.keySet()) {
            {
                List<List<BackTraceVO>> v = map.get(key);
                if(v.size() != cars){
                    continue;
                }
//                if(checkSize(v,maxSize)){
//                    continue;
//                }
//                if(checkLength(v,minLength)){
//                    continue;
//                }
//                if(has(result,v)){
//                    continue;
//                }

                v = check(v);
                if(CollectionUtils.isEmpty(v)){
                    continue;
                }

                if(!result.contains(v)) {
                    result.add(v);
                }
            }
        }

        result.forEach(r -> {
            System.out.print("result : ");
            r.forEach(l -> {
                l.forEach( s-> System.out.print(s.getId()+":"+s.getCount()+" "));
                System.out.print("| ");
            });
            System.out.println();
        });
        System.out.println("次数："+c);

        return result;
    }

    private void test(BackTraceVO[] traces, Integer[] cargos,int cargoIndex,int index,int current, List<BackTraceVO> list, String tag) throws Exception{
        c++;

        for (int i = index; i < traces.length; i++) {
            if(cargoIndex+1 > cargos.length){
                return;
            }
            if(traces[i].getCount() == 0){
                continue;
            }
            int length = traces[i].getCount();
            if(traces[i].getCount() > cargos[cargoIndex]){
                length = cargos[cargoIndex];
            }
            for(int j=length;j>length/2;j--){
//            for(int j=1;j<=length;j++){
                if(cargoIndex+1 > cargos.length){
                    return;
                }
                if(j == 0){
                    continue;
                }
                current += j;
                traces[i].setCount(traces[i].getCount() - j);

                list.add(new BackTraceVO(traces[i].getId(),j));
                BackTraceVO tmp = traces[i];
                if(current == cargos[cargoIndex] ){
                    if(tag == null) {
                        tag = UUID.randomUUID().toString();
                    }

                    map.putIfAbsent(tag,new ArrayList<>());
                    List<List<BackTraceVO>> group = map.get(tag);

                    List<List<BackTraceVO>> newGroup = CopyUtil.copyList(group);


                    newGroup.add(CopyUtil.copyList(list));
                    String newTag = UUID.randomUUID().toString();
                    map.put(newTag,newGroup);

                    test(traces,cargos,cargoIndex+1,0,0,new ArrayList<>(),newTag);
                    traces[i] = tmp;
                }

                test(traces,cargos,cargoIndex,i+1,current,list,tag);
                current -=j;
                traces[i].setCount(traces[i].getCount() + j);
                list.remove(list.size()-1);
            }
        }
    }

    private int orderCount(BackTraceVO[] traces){
        int c = 0;
        for (BackTraceVO trace : traces) {
            c += trace.getCount();
        }
        return c;
    }

    public boolean checkSize(List<List<BackTraceVO>> list, int maxSize){
        for (List<BackTraceVO> l : list) {
            if(l.size() > maxSize){
                return true;
            }
        }
        return false;
    }

    public boolean checkLength(List<List<BackTraceVO>> list, int minLength){
        int length = 0;
        for (List<BackTraceVO> l : list) {
            length += l.size();
        }

        return length > minLength;
    }

    public List<List<BackTraceVO>> check(List<List<BackTraceVO>> sources){
        List<List<BackTraceVO>> result = new ArrayList<>();
        for (List<BackTraceVO> source : sources) {
            Map<Long,Integer> map = new LinkedHashMap<>();
            for (int i = 0; i < source.size(); i++) {
                map.put(source.get(i).getId(),map.getOrDefault(source.get(i).getId(),0)+source.get(i).getCount());
            }
            int tmp = -1;
            boolean cr = true;
            for (Long key : map.keySet()) {
                if(tmp == -1){
                    tmp = map.get(key);
                    continue;
                }
                if(map.get(key) > tmp){
                    cr = false;
                    break;
                }
                tmp = map.get(key);
            }
            if(cr){
                result.add(source);
            }
        }
        return result;
    }

}
