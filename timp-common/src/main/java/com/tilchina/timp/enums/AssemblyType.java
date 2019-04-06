package com.tilchina.timp.enums;

/**
 * 
 * 
 * @version 1.0.0 2018/5/7
 * @author WangShengguang   
 */ 
public enum AssemblyType {

    PROFIT("利润优先", 0), CLEARANCE("清仓模式", 1);

    private String name;
    private int index;

    public static String getName(int index){
        for(AssemblyType type : AssemblyType.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static AssemblyType get(String name){
        for(AssemblyType type : AssemblyType.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    AssemblyType(String name, int index) {
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
