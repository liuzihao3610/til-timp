package com.tilchina.timp.enums;
/**
 *
 *
 * @version 1.0.0 2018/7/27
 * @author LiuShuqi
 */
public enum CarLevel {

	UnNamed("未指定", 0),Energy("新能源", 1),MiniCar("微型车", 2),SmallCar("小型车", 3),
	Compact("紧凑型车", 4),MidsizeCar("中型车", 5),MidLargeCar("中大型车", 6),LargeCar("大型车", 7),
	SUV("SUV", 8),MPV("MPV", 9),SportCar("跑车", 10),Pickup("皮卡", 11),
	LightPassenger("轻客", 12);

	private String name;
	private int index;

	public static CarLevel get(String name){
		for(CarLevel type : CarLevel.values()){
			if(type.getName().equals(name)){
				return type;
			}
		}
		return null;
	}

	public static CarLevel get(int index){
		for(CarLevel type : CarLevel.values()){
			if(type.getIndex() == index){
				return type;
			}
		}
		return null;
	}

	CarLevel(String name, int index) {
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
        return CarLevel.get(name).getIndex();
    }

    public static String getNameByIndex(int index) {
        return CarLevel.get(index).getName();
    }
}
