package com.tilchina.timp.enums;

/**
 * 
 * 
 * @version 1.0.0 2018/7/13
 * @author Xiahong
 */ 
public enum UserType {

    USER("系统用户", 0),
    DRIVER("司机", 1);
    private String name;
    private int index;

    public static String getName(int index){
        for(UserType type : UserType.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static Integer getName(String name){
        for(UserType type : UserType.values()){
            if(type.getName().equals(name)){
                return type.getIndex();
            }
        }
        return null;
    }

    public static UserType get(String name){
        for(UserType type : UserType.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    UserType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public static UserType get(int index){
        for(UserType type : UserType.values()){
            if(type.getIndex() == index){
                return type;
            }
        }
        return null;
    }

    public static int getIndexByName(String name) {
        return UserType.get(name).getIndex();
    }

    public static String getNameByIndex(int index) {
        return UserType.get(index).getName();
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }
}
