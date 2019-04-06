package com.tilchina.timp.enums;/*
 * @author XueYuSong
 * @date 2018-07-31 16:29
 */

public enum CardStatus {
	NotUsed("未领用", 0),
	Used("领用", 1);

	private String name;
	private int index;

	public static CardStatus get(String name){
		for(CardStatus type : CardStatus.values()){
			if(type.getName().equals(name)){
				return type;
			}
		}
		return null;
	}

	public static CardStatus get(int index){
		for(CardStatus type : CardStatus.values()){
			if(type.getIndex() == index){
				return type;
			}
		}
		return null;
	}

	CardStatus(String name, int index) {
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
		return CardStatus.get(name).getIndex();
	}

	public static String getNameByIndex(int index) {
		return CardStatus.get(index).getName();
	}
}
