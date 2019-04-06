package com.tilchina.timp.enums;
/*
 * @author XueYuSong
 * @date 2018-06-19 10:09
 */

public enum  JobType {

	LongHaul   ("长途", 0),
	City       ("市内", 1),
	ShortBarge ("短驳", 2),
	Return     ("回程", 3),
	Rail       ("铁运", 4),
	Air        ("空运", 5),
	Shipping   ("海运", 6),
	Exhibition ("展会", 7);

	private String name;
	private int index;

	public static String getName(int index){
		for(OperationType type : OperationType.values()){
			if(type.getIndex() == index){
				return type.getName();
			}
		}
		return null;
	}

	public static JobType get(String name){
		for(JobType type : JobType.values()){
			if(type.getName().equals(name)){
				return type;
			}
		}
		return null;
	}

	public static JobType get(int index){
		for(JobType type : JobType.values()){
			if(type.getIndex() == index){
				return type;
			}
		}
		return null;
	}

	JobType(String name, int index) {
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
