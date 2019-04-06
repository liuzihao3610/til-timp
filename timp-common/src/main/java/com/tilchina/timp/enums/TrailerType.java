package com.tilchina.timp.enums;
/*
 * @author XueYuSong
 * @date 2018-07-31 14:22
 */

public enum TrailerType {
	Fixed("固定", 0),
	Half("半挂", 1),
	Full("全挂", 2);

	private String name;
	private int index;

	public static TrailerType get(String name){
		for(TrailerType type : TrailerType.values()){
			if(type.getName().equals(name)){
				return type;
			}
		}
		return null;
	}

	public static TrailerType get(int index){
		for(TrailerType type : TrailerType.values()){
			if(type.getIndex() == index){
				return type;
			}
		}
		return null;
	}

	TrailerType(String name, int index) {
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
		return TrailerType.get(name).getIndex();
	}

	public static String getNameByIndex(int index) {
		return TrailerType.get(index).getName();
	}
}
