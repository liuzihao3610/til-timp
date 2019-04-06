package com.tilchina.timp.enums;

/**
 * 
 * 
 * @version 1.0.0 2018/7/10
 * @author Xiahong
 */ 
public enum PlatformType {

    ALL("全部设备", 0), ANDROID("android", 1),IOS("ios", 2);

    private String name;
    private int index;

    public static String getName(int index){
        for(PlatformType type : PlatformType.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static PlatformType get(String name){
        for(PlatformType type : PlatformType.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    PlatformType(String name, int index) {
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
