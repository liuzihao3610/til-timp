package com.tilchina.timp.enums;/*
 * @author XueYuSong
 * @date 2018-06-19 15:47
 */

public enum TemplateType {

	Porsche      ("保时捷", 0),
	MercedesBenz ("梅赛德斯奔驰", 1),
	Toyota       ("丰田", 2),
	Universal    ("通用", 3);

	private String name;
	private int index;

	public static String getName(int index){
		for(TemplateType type : TemplateType.values()){
			if(type.getIndex() == index){
				return type.getName();
			}
		}
		return null;
	}

	public static TemplateType get(String name){
		for(TemplateType type : TemplateType.values()){
			if(type.getName().equals(name)){
				return type;
			}
		}
		return null;
	}

	public static TemplateType get(int index){
		for(TemplateType type : TemplateType.values()){
			if(type.getIndex() == index){
				return type;
			}
		}
		return null;
	}

	TemplateType(String name, int index) {
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
