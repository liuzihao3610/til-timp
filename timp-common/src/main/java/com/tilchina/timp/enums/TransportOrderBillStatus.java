package com.tilchina.timp.enums;
/*
 * @author Xiahong
 * @date 2018-06-20 16:31
 */

public enum TransportOrderBillStatus {

	UNCHECKED("制单", 0),
	NOTSETTLE("未结算", 0),
	CHECK("审核", 1),
	LREADYSETTLE("已结算", 1),
	SEND("已发送", 2),
	ORDERRECEIVING("已接单", 3),
	ROUTE("在途", 4),
	CANCELPLAN("取消计划", 5),
	CCOMPLISH("已完成", 6);;

	private String name;
	private int index;

	public static String getName(int index){
		for(OrderType type : OrderType.values()){
			if(type.getIndex() == index){
				return type.getName();
			}
		}
		return null;
	}

	public static OrderType get(String name){
		for(OrderType type : OrderType.values()){
			if(type.getName().equals(name)){
				return type;
			}
		}
		return null;
	}

	public static OrderType get(int index){
		for(OrderType type : OrderType.values()){
			if(type.getIndex() == index){
				return type;
			}
		}
		return null;
	}

	TransportOrderBillStatus(String name, int index) {
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
