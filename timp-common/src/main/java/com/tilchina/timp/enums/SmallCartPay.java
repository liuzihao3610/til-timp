package com.tilchina.timp.enums;

/**
 * 
 * 
 * @version 1.0.0 2018/7/13
 * @author Xiahong
 */ 
public enum SmallCartPay {

    SELF_PAY("司机自付", 0),
    CORP_PAY("公司支付", 1),
    ADVANCE("经销店垫付", 2);
    private String name;
    private int index;

    public static String getName(int index){
        for(SmallCartPay type : SmallCartPay.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static Integer getName(String name){
        for(SmallCartPay type : SmallCartPay.values()){
            if(type.getName().equals(name)){
                return type.getIndex();
            }
        }
        return null;
    }

    public static SmallCartPay get(String name){
        for(SmallCartPay type : SmallCartPay.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    SmallCartPay(String name, int index) {
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
