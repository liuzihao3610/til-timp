package com.tilchina.timp.enums;

/**
 * 
 * 
 * @version 1.0.0 2018/7/13
 * @author Xiahong
 */ 
public enum UnitType {

    AGENCY("经销店", 0), WAREHOUSE("仓库", 1),EXHIBITION("展馆", 2);

    private String name;
    private int index;

    public static String getName(int index){
        for(UnitType type : UnitType.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static UnitType get(String name){
        for(UnitType type : UnitType.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    UnitType(String name, int index) {
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
