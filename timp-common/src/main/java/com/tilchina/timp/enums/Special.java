package com.tilchina.timp.enums;

/**
 * 
 * 
 * @version 1.0.0 2018/7/13
 * @author Xiahong
 */ 
public enum Special {

    NO("否", 0),
    YES("是", 1);
    private String name;
    private int index;

    public static String getName(int index){
        for(Special type : Special.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static Special get(String name){
        for(Special type : Special.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    Special(String name, int index) {
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
