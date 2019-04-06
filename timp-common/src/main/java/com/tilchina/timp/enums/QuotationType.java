package com.tilchina.timp.enums;
/*
 * @author XueYuSong
 * @date 2018-06-19 14:34
 */

public enum QuotationType {

	ContractPrice("合同价", 0),
	EstimatePrice("预估价", 1);

	private String name;
	private int index;

	public static String getName(int index) {
		for (OperationType type : OperationType.values()) {
			if (type.getIndex() == index) {
				return type.getName();
			}
		}
		return null;
	}

	public static QuotationType get(String name) {
		for (QuotationType type : QuotationType.values()) {
			if (type.getName().equals(name)) {
				return type;
			}
		}
		return null;
	}

	public static QuotationType get(int index) {
		for (QuotationType type : QuotationType.values()) {
			if (type.getIndex() == index) {
				return type;
			}
		}
		return null;
	}

	QuotationType(String name, int index) {
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
