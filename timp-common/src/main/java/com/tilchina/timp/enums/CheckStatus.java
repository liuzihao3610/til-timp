package com.tilchina.timp.enums;/*
 * @author XueYuSong
 * @date 2018-06-06 19:20
 */

public enum CheckStatus {

	Unchecked("未检查", 0),
	Unpassed("未通过", 1),
	Passed("已通过", 2);

	private String name;
	private int index;

	public static BillStatus get(String name){
		for(BillStatus type : BillStatus.values()){
			if(type.getName().equals(name)){
				return type;
			}
		}
		return null;
	}

	public static BillStatus get(int index){
		for(BillStatus type : BillStatus.values()){
			if(type.getIndex() == index){
				return type;
			}
		}
		return null;
	}

	CheckStatus(String name, int index) {
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
