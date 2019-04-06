package com.tilchina.timp.enums;

/*
 * @author Xiahong
 * @date 2018-06-15 14:20
 */

public enum DriverSettlementStatus {

	Unchecked("制单", 0),
	Check("审核", 1),
	Verify("已确认", 2);

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

	DriverSettlementStatus(String name, int index) {
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
