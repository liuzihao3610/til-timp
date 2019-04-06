package com.tilchina.timp.enums;

public enum BillStatus {

	Drafted("制单", 0),
	Audited("审核", 1),
	Settled("结算", 2),
	Invalid("作废", 2);

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

	BillStatus(String name, int index) {
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
