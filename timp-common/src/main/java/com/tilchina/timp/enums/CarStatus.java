package com.tilchina.timp.enums;

/**
 *
 *
 * @version 1.0.0 2018/5/7
 * @author WangShengguang
 */
public enum CarStatus {

    //0=长途,1=市内,2=短驳,3=回程,4=铁运,5=空运,6=海运

    In("入库", 0), Section("分段", 1), Out("出库", 2), Advance("预排",3), Confirm("预排确认",4), ToDriver("下达给司机",5),
    Accept("司机接单",6), Appointed("已预约",7), Hauled("已装车",8), OnTheWay("在途",9), Arrived("已到店",10), Closed("已关闭",11);

    private String name;
    private int index;

    public static CarStatus get(String name){
        for(CarStatus type : CarStatus.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }

    public static CarStatus get(int index){
        for(CarStatus type : CarStatus.values()){
            if(type.getIndex() == index){
                return type;
            }
        }
        return null;
    }

    CarStatus(String name, int index) {
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
