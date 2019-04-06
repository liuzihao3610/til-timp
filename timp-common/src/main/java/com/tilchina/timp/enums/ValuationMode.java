package com.tilchina.timp.enums;

/**
 * 计价方式
 */
public enum ValuationMode {

	UniformPrice("统一价格", 0),
	Mileage("按里程计价", 1);

	private String name;
	private int index;

	public static ValuationMode get(String name){
		for(ValuationMode type : ValuationMode.values()){
			if(type.getName().equals(name)){
				return type;
			}
		}
		return null;
	}

	public static ValuationMode get(int index){
		for(ValuationMode type : ValuationMode.values()){
			if(type.getIndex() == index){
				return type;
			}
		}
		return null;
	}

	ValuationMode(String name, int index) {
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
