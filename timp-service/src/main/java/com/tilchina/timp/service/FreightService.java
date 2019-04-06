package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Freight;
import com.tilchina.timp.model.TransportOrder;
import com.tilchina.timp.model.TransportOrderDetail;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* 运价管理
*
* @version 1.0.0
* @author LiuShuqi
*/
public interface FreightService extends BaseService<Freight> {

    BigDecimal getAllPrice(TransportOrder order);

	BigDecimal getAllPrice(List<TransportOrderDetail> details);

    void getPrice(TransportOrder order);

	BigDecimal getPrice(Long startPlaceId, Long arrivalPlaceId, Long brandId, Long carTypeId, Date billDate);

	Freight getFreight(Long startPlaceId, Long arrivalPlaceId, Long brandId, Long carTypeId, Date billDate);

	void deleteList(int[] freightId);

	void unaudit(Long data);

	void audit(Long data);

	void updateCheckDate(Freight freight);

	List<Freight> getReferenceList(Map<String, Object> map);

	Freight getFreight(Date orderDate,Long SeCityId, Long ReCityId, Long brandId, Long carTypeId);
	/**
	 * 导入Excel
	 * @param file
	 * @throws Exception 
	 */
	void importFile(MultipartFile file) throws Exception;

	Freight getFreight(Map<String, Object> data);
	/**
	 * 按创建时间倒序排列
	 * @param data
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageInfo<Freight> getList(Map<String, Object> data, int pageNum, int pageSize);
}
