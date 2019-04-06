package com.tilchina.timp.service;

import java.util.List;
import com.tilchina.timp.model.TransporterAndDriver;

/**
 * 获取车辆信息
 * @author Xiahong
 *
 */
public interface AppDriverService {
	
	/**
	 * 查询当前驾驶员名下所有轿运车信息
	 * @return
	 */
	List<TransporterAndDriver> queryList();

}
