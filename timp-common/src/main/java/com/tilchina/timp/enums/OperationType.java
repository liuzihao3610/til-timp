package com.tilchina.timp.enums;

/**
 *
 *
 * @version 1.0.0 2018/5/7
 * @author WangShengguang
 */
public enum OperationType {

    //0=长途,1=市内,2=短驳,3=回程,4=铁运,5=空运,6=海运

    LongHaul("长途", 0), CITY("市内", 1), SHORT("短驳", 2), RETURN("回程", 3), RAILWAY("铁运",4), AIR("空运",5), SHIPPING("海运",0);

    private String name;
    private int index;

    public static String getName(int index){
        for(OperationType type : OperationType.values()){
            if(type.getIndex() == index){
                return type.getName();
            }
        }
        return null;
    }

    public static OperationType get(String name){
        for(OperationType type : OperationType.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    public static OperationType get(int index){
        for(OperationType type : OperationType.values()){
            if(type.getIndex() == index){
                return type;
            }
        }
        return null;
    }

    OperationType(String name, int index) {
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
