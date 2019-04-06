package com.tilchina.timp.enums;

/**
 * 
 * 
 * @version 1.0.0 2018/7/13
 * @author Xiahong
 */ 
public enum WashCar {

    UNLIMITED("都支持", 0),
    SELF_PAY("自付", 1),
    MONTHLY("公司月结", 2);
    private String name;
    private int index;

    public static String getName(int index){
        for(WashCar type : WashCar.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static WashCar get(String name){
        for(WashCar type : WashCar.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    WashCar(String name, int index) {
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
