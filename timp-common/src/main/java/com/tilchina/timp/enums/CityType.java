package com.tilchina.timp.enums;
/*
 * @author XueYuSong
 * @date 2018-06-19 10:41
 */

public enum CityType {

	Nation   ( "国家", 0),
	Province ( "省", 1),
	City     ( "市", 2),
	District ( "区", 3);

	private String name;
	private int index;

	public static String getName(int index){
		for(CityType type : CityType.values()){
			if(type.getIndex() == index){
				return type.getName();
			}
		}
		return null;
	}

	public static CityType get(String name){
		for(CityType type : CityType.values()){
			if(type.getName().equals(name)){
				return type;
			}
		}
		return null;
	}

	public static CityType get(int index){
		for(CityType type : CityType.values()){
			if(type.getIndex() == index){
				return type;
			}
		}
		return null;
	}

	CityType(String name, int index) {
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
