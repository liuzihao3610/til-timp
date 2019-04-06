package com.tilchina.timp.enums;/*
 * @author XueYuSong
 * @date 2018-07-31 14:24
 */

public enum CargoType {
	Universal("通用", 0),
	Car("轿车", 1),
	Cargo("集装箱", 2);

	private String name;
	private int index;

	public static CargoType get(String name){
		for(CargoType type : CargoType.values()){
			if(type.getName().equals(name)){
				return type;
			}
		}
		return null;
	}

	public static CargoType get(int index){
		for(CargoType type : CargoType.values()){
			if(type.getIndex() == index){
				return type;
			}
		}
		return null;
	}

	CargoType(String name, int index) {
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
		return CargoType.get(name).getIndex();
	}

	public static String getNameByIndex(int index) {
		return CargoType.get(index).getName();
	}
}
