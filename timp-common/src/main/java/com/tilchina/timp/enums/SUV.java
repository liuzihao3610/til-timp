package com.tilchina.timp.enums;

/**
 * 
 * 
 * @version 1.0.0 2018/7/27
 * @author LiuShuqi
 */
public enum SUV {

    NO("否", 0), YES("是", 1);

    private String name;
    private int index;

    public static String getName(int index){
        for(SUV type : SUV.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static SUV get(String name){
        for(SUV type : SUV.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    public static SUV get(int index){
        for(SUV type : SUV.values()){
            if(type.getIndex() == index){
                return type;
            }
        }
        return null;
    }


    SUV(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public static int getIndexByName(String name) {
        return SUV.get(name).getIndex();
    }

    public static String getNameByIndex(int index) {
        return SUV.get(index).getName();
    }
}
