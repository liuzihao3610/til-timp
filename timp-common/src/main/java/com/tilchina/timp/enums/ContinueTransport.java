package com.tilchina.timp.enums;

/**
 * 
 * 
 * @version 1.0.0 2018/7/13
 * @author Xiahong
 */ 
public enum ContinueTransport {

    NO("否", 0),
    YES("是", 1);
    private String name;
    private int index;

    public static String getName(int index){
        for(ContinueTransport type : ContinueTransport.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static ContinueTransport get(String name){
        for(ContinueTransport type : ContinueTransport.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    public static Integer getName(String name){
        for(ContinueTransport type : ContinueTransport.values()){
            if(type.getName().equals(name)){
                return type.getIndex();
            }
        }
        return null;
    }


    ContinueTransport(String name, int index) {
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
