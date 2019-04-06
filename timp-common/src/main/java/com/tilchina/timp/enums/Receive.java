package com.tilchina.timp.enums;

/**
 * 
 * 
 * @version 1.0.0 2018/7/13
 * @author Xiahong
 */ 
public enum Receive {

    OUR("本店", 0),
    RESTS("其他接车点", 1);
    private String name;
    private int index;

    public static String getName(int index){
        for(Receive type : Receive.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static Receive get(String name){
        for(Receive type : Receive.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    Receive(String name, int index) {
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
