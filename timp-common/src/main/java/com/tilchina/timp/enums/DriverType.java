package com.tilchina.timp.enums;

/**
 * 
 * 
 * @version 1.0.0 2018/7/13
 * @author Xiahong
 */ 
public enum DriverType {

    NOT_SPECIFIED("未指定", 0),
    YES_CONTRACTOR("承包司机", 1),
    NO_CONTRACTOR("非承包司机",2);
    private String name;
    private int index;

    public static String getName(int index){
        for(DriverType type : DriverType.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static Integer getName(String name){
        for(DriverType type : DriverType.values()){
            if(type.getName().equals(name)){
                return type.getIndex();
            }
        }
        return null;
    }

    public static DriverType get(String name){
        for(DriverType type : DriverType.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    DriverType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public static DriverType get(int index){
        for(DriverType type : DriverType.values()){
            if(type.getIndex() == index){
                return type;
            }
        }
        return null;
    }

    public static int getIndexByName(String name) {
        return DriverType.get(name).getIndex();
    }

    public static String getNameByIndex(int index) {
        return DriverType.get(index).getName();
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }
}
