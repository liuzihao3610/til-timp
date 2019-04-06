package com.tilchina.timp.enums;/*
 * @author XueYuSong
 * @date 2018-07-31 16:31
 */

public enum SourceType {
	Purchase("采购", 0),
	ClientHedge("客户抵用", 1);

	private String name;
	private int index;

	public static SourceType get(String name){
		for(SourceType type : SourceType.values()){
			if(type.getName().equals(name)){
				return type;
			}
		}
		return null;
	}

	public static SourceType get(int index){
		for(SourceType type : SourceType.values()){
			if(type.getIndex() == index){
				return type;
			}
		}
		return null;
	}

	SourceType(String name, int index) {
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
		return SourceType.get(name).getIndex();
	}

	public static String getNameByIndex(int index) {
		return SourceType.get(index).getName();
	}
}
