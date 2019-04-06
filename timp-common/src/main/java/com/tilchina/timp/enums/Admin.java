package com.tilchina.timp.enums;

/**
 * 
 * 
 * @version 1.0.0 2018/7/12
 * @author Xiahong
 */
public enum Admin {
    USER("普通用户", 0),
    ADMIN("系统管理员", 1);

    private String name;
    private int index;

    public static String getName(int index){
        for(Admin type : Admin.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static Admin get(String name){
        for(Admin type : Admin.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    Admin(String name, int index) {
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
