package com.tilchina.timp.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class QuotationVO {

	private Long customerId;
	private Long vendorCorpId;
	private Long recvCityId;
	private Long sendCityId;
	private Long carBrandId;
	private Long carTypeId;
	private Long carModelId;
	private Integer jobType;
	private Date orderDate;
}