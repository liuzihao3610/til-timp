package com.tilchina.timp.service;

import com.tilchina.timp.model.TransportRecord;

/**
* app:运输记录表
*
* @version 1.1.0
* @author Xiahong
*/
public interface AppTransportRecordService {
	
	/**
	 * app每隔30分钟上传坐标，相关信息。更新运输记录表
	 * @param record
	 */
    public void add(TransportRecord record);

}
