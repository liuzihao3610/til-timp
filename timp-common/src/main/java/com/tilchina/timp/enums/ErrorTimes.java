package com.tilchina.timp.enums;

/**
 * 
 * 
 * @version 1.0.0 2018/7/12
 * @author Xiahong
 */ 
public enum ErrorTimes {

    INITIALIZE("初始化", 0), UNLOCKED("密码输入错误次数合理范围", 4), BLOCK("密码输入错误次数上限", 5);

    private String name;
    private int index;

    public static String getName(int index){
        for(ErrorTimes type : ErrorTimes.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static ErrorTimes get(String name){
        for(ErrorTimes type : ErrorTimes.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    ErrorTimes(String name, int index) {
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
