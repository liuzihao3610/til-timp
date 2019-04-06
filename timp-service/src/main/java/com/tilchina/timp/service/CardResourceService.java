package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.timp.model.CardReceiveRecord;
import com.tilchina.timp.model.CardRechargeRecord;
import com.tilchina.timp.model.CardResource;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
* 卡资源管理
*
* @version 1.0.0
* @author LiushuQi
*/
public interface CardResourceService{
	
	CardResource queryById(Long id);
    
    PageInfo<CardResource> queryList(Map<String, Object> map, int pageNum, int pageSize);

    List<CardResource> queryList(Map<String, Object> map);
    
    void add(CardResource record);
    
    void add(List<CardResource> records);

    void updateSelective(CardResource record);

    void update(CardResource record);
    
    void update(List<CardResource> records);
    
    void deleteById(Long id);

	void receive(CardReceiveRecord cardReceiveRecord);

	void recharge(CardRechargeRecord cardRechargeRecord);
	/**
	 * 按创建时间倒序排列
	 * @param data
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageInfo<CardResource> getList(Map<String, Object> data, int pageNum, int pageSize);

	/**
	 * 丢失
	 * @param data
	 */
    void lose(Long data);
	/**
	 * 丢失
	 * @param data
	 */
	void invalid(Long data);

	/**
	 * 取消领用/归还
	 * @param data
	 */
	void unReceive(Long data);

	/**
	 * 取消丢失
	 * @param data
	 */
	void find(Long data);

	/**
	 * 取消作废
	 * @param data
	 */
	void unInvalid(Long data);

	void importExcel(MultipartFile file) throws Exception;

	Workbook exportExcel() throws Exception;
}
