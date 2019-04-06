package com.tilchina.timp.enums;

/**
 * 
 * 
 * @version 1.0.0 2018/7/10
 * @author Xiahong
 */ 
public enum AudienceType {

    ALL("全部设备", 0), TAG("推送给多个标签（只要在任何一个标签范围内都满足）", 1),TAG_AND("推送给多个标签（需要同时在多个标签范围内）", 2),
    TAG_NOT("多个标签之间，先取多标签的并集，再对该结果取补集", 3), ALIAS("推送给多个别名", 4),REGISTRATION_ID("多个注册ID之间是 OR 关系，即取并集", 5);

    private String name;
    private int index;

    public static String getName(int index){
        for(AudienceType type : AudienceType.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static AudienceType get(String name){
        for(AudienceType type : AudienceType.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    AudienceType(String name, int index) {
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
