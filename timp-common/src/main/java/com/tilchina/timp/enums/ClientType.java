package com.tilchina.timp.enums;

/**
 * @author WangShengguang
 * @version 1.0.0 2018/4/1
 */
public enum ClientType {

    WEB("web", 0), APP("app", 1), PAD("pad", 2), WX("wx", 3), TRANSPORT("transport",4);

    private String name;
    private int index;

    public static String getName(int index){
        for(ClientType type : ClientType.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static ClientType get(String name){
        for(ClientType type : ClientType.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    ClientType(String name, int index) {
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
