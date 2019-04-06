package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.tilchina.timp.model.OilDepot;
import com.tilchina.timp.model.OilDepotSupplyRecord;
import com.tilchina.timp.model.TransporterFuelRecord;

/**
* 油库管理
*
* @version 1.0.0
* @author LiushuQi
*/
public interface OilDepotService{

	public OilDepot queryById(Long id);
    
    public PageInfo<OilDepot> queryList(Map<String, Object> map, int pageNum, int pageSize);

    public List<OilDepot> queryList(Map<String, Object> map);
    
    public void add(OilDepot record);
    
    public void add(List<OilDepot> records);

    void updateSelective(OilDepot record);

    public void update(OilDepot record);
    
    public void update(List<OilDepot> records);
    
    public void deleteById(Long id);

	public void supply(OilDepotSupplyRecord oilDepotSupplyRecord);

	public void fuel(TransporterFuelRecord transporterFuelRecord);
	/**
	 * 启用
	 * @param oilDepotId
	 */
	public void start(Map<String, Object> map);
	/**
	 * 停用
	 * @param map
	 */
	public void stop(Map<String, Object> map);
	/**
	 * 按创建时间倒序排列
	 * @param data
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public PageInfo<OilDepot> getList(Map<String, Object> data, int pageNum, int pageSize);
}
