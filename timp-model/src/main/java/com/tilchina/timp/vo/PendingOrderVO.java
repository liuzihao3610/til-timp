package com.tilchina.timp.vo;

import com.tilchina.timp.model.Car;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 *
 * @version 1.0.0 2018/5/8
 * @author WangShengguang
 */
@Data
public class PendingOrderVO implements Serializable{

    private Long orderId;
    private Long orderDetailId;
    private Date orderDate;

    private String carVin;
    private Long carId;
    private Long brandId;
    private Long carTypeId;
    private Long sendUnitId;
    private String sendUnitName;
    private Long sendCityId;
    private String sendCityName;
    private Double sendLng;
    private Double sendLat;

    private Long receiveUnitId;
    private String receiveUnitName;
    private Long receiveCityId;
    private String receiveCityName;
    private Integer deliver;
    private Integer special;
    private Double receiveLng;
    private Double receiveLat;
    private Long cityAreaId;

    private Long settlementCityId;
    private String settlementCityName;

    private String instructionNumber;
    private Date claimLoadDate;
    private Date claimDeliveryDate;
    private Integer urgent;
    private Double distance;

    private BigDecimal customerPrice;
    private BigDecimal transPrice;

    private Car car;

    private boolean used;
}
