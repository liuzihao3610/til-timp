package com.tilchina.timp.enums;

/**
 * 
 * 
 * @version 1.0.0 2018/7/12
 * @author Xiahong
 */
public enum RegistType {
    VIRTUAL("虚拟节点", 0),
    FUNCTIONS("功能节点", 1),
    BUTTON("按扭", 2);

    private String name;
    private int index;

    public static String getName(int index){
        for(RegistType type : RegistType.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static RegistType get(String name){
        for(RegistType type : RegistType.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    RegistType(String name, int index) {
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
