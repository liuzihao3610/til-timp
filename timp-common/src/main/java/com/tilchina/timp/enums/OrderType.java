package com.tilchina.timp.enums;

/**
 *
 *
 * @version 1.0.0 2018/5/7
 * @author WangShengguang
 */
public enum OrderType {

    //0=长途,1=市内,2=短驳,3=回程,4=铁运,5=空运,6=海运

    In("原始订单", 0), Out("转包订单", 1), Section("分段订单", 2);

    private String name;
    private int index;

    public static String getName(int index){
        for(OrderType type : OrderType.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static OrderType get(String name){
        for(OrderType type : OrderType.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    public static OrderType get(int index){
        for(OrderType type : OrderType.values()){
            if(type.getIndex() == index){
                return type;
            }
        }
        return null;
    }

    OrderType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }
}
