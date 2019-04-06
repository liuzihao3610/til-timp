package com.tilchina.timp.enums;

/**
 * 报价类型
 */
public enum OfferType {

	StandardPrice("标准价", 0),
	SettlementPrice("结算价", 1);

	private String name;
	private int index;

	public static OfferType get(String name){
		for(OfferType type : OfferType.values()){
			if(type.getName().equals(name)){
				return type;
			}
		}
		return null;
	}

	public static OfferType get(int index){
		for(OfferType type : OfferType.values()){
			if(type.getIndex() == index){
				return type;
			}
		}
		return null;
	}

	OfferType(String name, int index) {
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
