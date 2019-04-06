package com.tilchina.timp.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RailtransCabinStatus {

    private String cabinNumber;   //车厢号
    private String latestProvince;   //车厢最新停靠站所在省市
    private String latestCity;   //车厢最新停靠站所在城市
    private String latestStation;   //车厢最新停靠站所在站台
    private String latestStatus;   //车厢最新停靠状态
    private Integer isArrived;
	private String zone;
    private String remark;
	private Date latestUpdateTime;
}
