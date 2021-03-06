package com.tilchina.timp.enums;/*
 * @author XueYuSong
 * @date 2018-06-10 16:31
 */

public enum ReconciliationStatus {

	No("否", 0),
	Yes("是", 1);

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

	ReconciliationStatus(String name, int index) {
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
