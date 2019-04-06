package com.tilchina.timp.enums;

/**
 * 树形结构勾选状态
 *
 * @version 1.0.0 2018/7/12
 * @author Xiahong
 */
public enum Checked {

    FALSE("不勾选", 0), TRUE("勾选", 1);

    private String name;
    private int index;

    public static String getName(int index){
        for(Checked type : Checked.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static Checked get(String name){
        for(Checked type : Checked.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    Checked(String name, int index) {
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
