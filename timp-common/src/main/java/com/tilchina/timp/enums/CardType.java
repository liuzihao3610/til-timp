package com.tilchina.timp.enums;/*
 * @author XueYuSong
 * @date 2018-07-31 16:27
 */

public enum CardType {
	Oil("油卡", 0),
	Etc("ETC", 1);

	private String name;
	private int index;

	public static CardType get(String name){
		for(CardType type : CardType.values()){
			if(type.getName().equals(name)){
				return type;
			}
		}
		return null;
	}

	public static CardType get(int index){
		for(CardType type : CardType.values()){
			if(type.getIndex() == index){
				return type;
			}
		}
		return null;
	}

	CardType(String name, int index) {
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
		return CardType.get(name).getIndex();
	}

	public static String getNameByIndex(int index) {
		return CardType.get(index).getName();
	}
}
