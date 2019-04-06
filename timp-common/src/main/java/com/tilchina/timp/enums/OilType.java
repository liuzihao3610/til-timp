package com.tilchina.timp.enums;
/*
 * @author XueYuSong
 * @date 2018-07-31 15:44
 */

public enum OilType {
	Gasoline("汽油", 0),
	Diesel("柴油", 1);

	private String name;
	private int index;

	public static OilType get(String name){
		for(OilType type : OilType.values()){
			if(type.getName().equals(name)){
				return type;
			}
		}
		return null;
	}

	public static OilType get(int index){
		for(OilType type : OilType.values()){
			if(type.getIndex() == index){
				return type;
			}
		}
		return null;
	}

	OilType(String name, int index) {
		this.name = name;
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}

	public static int getIndexByName(String name) {
		return OilType.get(name).getIndex();
	}

	public static String getNameByIndex(int index) {
		return OilType.get(index).getName();
	}
}
