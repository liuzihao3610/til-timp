package com.tilchina.test;

import com.tilchina.timp.util.CopyUtil;

import java.util.*;

/**
 * Created by demon on 2018/2/5.
 */
public class CarTest {

    int c = 0;
    int t = 0;

    Map<String,List<List<Integer>>> map = new HashMap<>();

    public static void main(String[] args){
        CarTest t = new CarTest();
        int[] orders = {18,3,3,2,2};
//        int[] orders = {12,8,1};
        int[] cargos = {7,7,7,7,7};

        t.run(orders,cargos);
    }

    public void run(int[] orders, int[] cargos){

        int orderCount = orderCount(orders);

        try {
            test(orders, cargos,0,0, 0, new ArrayList<>(), null);
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
            for (List<Integer> list : map.get(key)) {
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
            for (List<Integer> list : map.get(key)) {
                groupLength += list.size();
            }
            if(minLength > groupLength){
                minLength = groupLength;
            }
        }

        List<List<List<Integer>>> result = new ArrayList<>();
        for (String key : map.keySet()) {
            {
                List<List<Integer>> v = map.get(key);
                if(v.size() != cars){
                    continue;
                }
//                if(checkSize(v,maxSize)){
//                    continue;
//                }
//                if(checkLength(v,minLength)){
//                    continue;
//                }
                if(has(result,v)){
                    continue;
                }
                result.add(v);
//                System.out.print(key+": ");
//                v.forEach( l -> {
//                  l.forEach( s-> System.out.print(s+" "));
//                    System.out.print("| ");
//                });
//                System.out.println();
            }
        }

        result.forEach(r -> {
            System.out.print("result : ");
            r.forEach(l -> {
                l.forEach( s-> System.out.print(s+" "));
                System.out.print("| ");
            });
            System.out.println();
        });
        System.out.println("次数："+c);
        System.out.println("结果数："+t);

    }

    private void test(int[] orders, int[] cargos,int cargoIndex,int index,int current, List<Integer> list, String tag) throws Exception{
        c++;

        for (int i = index; i < orders.length; i++) {
            if(cargoIndex+1 > cargos.length){
                return;
            }
            if(orders[i] == 0){
                continue;
            }
            int length = orders[i];
            if(orders[i] > cargos[cargoIndex]){
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
                orders[i] = orders[i]-j;
                list.add(j);
                int tmp = orders[i];
                if(current == cargos[cargoIndex] ){
                    t++;
                    if(tag == null) {
                        tag = UUID.randomUUID().toString();
                    }

                    map.putIfAbsent(tag,new ArrayList<>());
                    List<List<Integer>> group = map.get(tag);

                    List<List<Integer>> newGroup = CopyUtil.copyList(group);


                    newGroup.add(CopyUtil.copyList(list));
                    String newTag = UUID.randomUUID().toString();
                    map.put(newTag,newGroup);

                    test(orders,cargos,cargoIndex+1,0,0,new ArrayList<>(),newTag);
                    orders[i] = tmp;
                }

                test(orders,cargos,cargoIndex,i+1,current,list,tag);
                current -=j;
                orders[i] = orders[i]+j;
                list.remove(list.size()-1);
            }
        }
    }

    private int orderCount(int[] orders){
        int c = 0;
        for (int order : orders) {
            c += order;
        }
        return c;
    }

    public boolean checkSize(List<List<Integer>> list, int maxSize){
        for (List<Integer> l : list) {
            if(l.size() > maxSize){
                return true;
            }
        }
        return false;
    }

    public boolean checkLength(List<List<Integer>> list, int minLength){
        int length = 0;
        for (List<Integer> l : list) {
            length += l.size();
        }

        return length > minLength;
    }

    private boolean has(List<List<List<Integer>>> result,List<List<Integer>> list){
        for (List<List<Integer>> rs : result) {
            boolean has = true;
            for (List<Integer> r : rs) {
                boolean sub = false;
                for (List<Integer> l : list) {
                    sub = sub || r.containsAll(l);
                }
                has = has && sub;
            }
            if(has){
                return true;
            }
        }
        return false;
    }

}
