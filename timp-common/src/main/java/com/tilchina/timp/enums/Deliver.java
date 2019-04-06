package com.tilchina.timp.enums;

/**
 * 
 * 
 * @version 1.0.0 2018/7/13
 * @author Xiahong
 */ 
public enum Deliver {

    UNLIMITED("无限制", 0),
    MUST("必须轿车", 1);
    private String name;
    private int index;

    public static String getName(int index){
        for(Deliver type : Deliver.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static Deliver get(String name){
        for(Deliver type : Deliver.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    Deliver(String name, int index) {
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
