package com.tilchina.timp.vo;/*
 * @author XueYuSong
 * @date 2018-06-20 10:39
 */

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UniversalExcelVO {

	private String cityName;
	private String sendCityName;
	private String recvCityName;
	private String sendAreaName;
	private String sendUnitName;
	private String recvAreaName;
	private String recvUnitName;
	private String carBrandName;
	private String carTypeName;
	private Integer leadTime;
	private BigDecimal amount;

}
