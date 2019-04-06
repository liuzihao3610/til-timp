package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Car;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* 
*
* @version 1.1.0
* @author XiaHong
*/
public interface CarService extends BaseService<Car> {

	void deleteList(int[] data);

	PageInfo<Car> getReferenceList(Map<String, Object> map, int pageNum, int pageSize);

	void audit(Long carId);
	void unaudit(Long carId);

	Car getByCarName(String model);

    Map<String,Car> queryMap();

    List<Car> queryByCarNames(Set<String> codes);

	Map<Long,Car> queryMapByCarIds(Set<Long> carIds);

	List<Car> queryByCarIds(Set<Long> ids);

	PageInfo<Car> getList(Map<String, Object> map, int pageNum, int pageSize);

	Long queryIdByName(String modelName);

	/**
	 * 封存
	 * @param carId
	 */
    void sequestration(Long carId);

	/**
	 * 取消封存
	 * @param carId
	 */
	void unsequestration(Long carId);

	/**
	 * 导入Excel
	 * @param file
	 */
	void importExcel(MultipartFile file);

	/**
	 *导出Excel
	 * @return
	 */
	Workbook exportExcel() throws Exception;
}
