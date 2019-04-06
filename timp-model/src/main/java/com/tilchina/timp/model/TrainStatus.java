package com.tilchina.timp.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TrainStatus {

    private Date dateTime;
    private String province;
    private String city;
    private String station;
    private String status;
    private String other;

    public String toString()
    {
        return String.format("%s %s %s %s %s %s", dateTime, province, city, station, status, other);
    }
}
