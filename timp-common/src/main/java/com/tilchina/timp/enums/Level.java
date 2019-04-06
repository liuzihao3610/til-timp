package com.tilchina.timp.enums;

/**
 * 
 * 
 * @version 1.0.0 2018/7/13
 * @author Xiahong
 */ 
public enum Level {

    COMMON("普通", 0),
    IMPORTANCE("重要", 1),
    VIP("VIP", 2),
    RETAIL("散户", 2);
    private String name;
    private int index;

    public static String getName(int index){
        for(Level type : Level.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static Level get(String name){
        for(Level type : Level.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    Level(String name, int index) {
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
