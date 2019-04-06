package com.tilchina.timp.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CabinStatus {
	private Date arrivalTime;
	private String province;
	private String city;
	private String station;
	private String status;
	private String other;
}
