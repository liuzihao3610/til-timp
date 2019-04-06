package com.tilchina.timp.enums;

/**
 * 
 * 
 * @version 1.0.0 2018/7/12
 * @author Xiahong
 */
public enum Block {

    UNLOCKED("未锁定", 0), BLOCK("锁定", 1);

    private String name;
    private int index;

    public static String getName(int index){
        for(Block type : Block.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static Block get(String name){
        for(Block type : Block.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    Block(String name, int index) {
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
