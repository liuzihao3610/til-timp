package com.tilchina.timp.enums;

/**
 * 
 * 
 * @version 1.0.0 2018/7/13
 * @author Xiahong
 */ 
public enum Sex {

    WOMAN("女",0 ),
    MAN("男", 1);
    private String name;
    private int index;

    public static String getName(int index){
        for(Sex type : Sex.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static Integer getName(String name){
        for(Sex type : Sex.values()){
            if(type.getName().equals(name)){
                return type.getIndex();
            }
        }
        return null;
    }

    public static Sex get(String name){
        for(Sex type : Sex.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    public static Sex get(int index){
        for(Sex type : Sex.values()){
            if(type.getIndex() == index){
                return type;
            }
        }
        return null;
    }

    Sex(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public static int getIndexByName(String name) {
        return Sex.get(name).getIndex();
    }

    public static String getNameByIndex(int index) {
        return Sex.get(index).getName();
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }
}
