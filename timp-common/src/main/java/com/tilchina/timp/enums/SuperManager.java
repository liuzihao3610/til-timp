package com.tilchina.timp.enums;

/**
 * 
 * 
 * @version 1.0.0 2018/7/13
 * @author Xiahong
 */ 
public enum SuperManager {

    USER("系统用户", 0),
    SUPER_MANAGER("超级经理", 1);
    private String name;
    private int index;

    public static String getName(int index){
        for(SuperManager type : SuperManager.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static Integer getName(String name){
        for(SuperManager type : SuperManager.values()){
            if(type.getName().equals(name)){
                return type.getIndex();
            }
        }
        return null;
    }

    public static SuperManager get(String name){
        for(SuperManager type : SuperManager.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    SuperManager(String name, int index) {
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
