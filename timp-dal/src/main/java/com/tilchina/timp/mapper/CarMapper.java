package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.Car;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* 
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface CarMapper extends BaseMapper<Car> {

	void deleteList(int[] data);

	List<Car> getReferenceList(Map<String, Object> map);

	Car getByCarName(String carName);

	List<Car> selectByCarIds(@Param("ids") Set<Long> ids);

	List<Car> selectByCarNames(@Param("names") Set<String> codes);

	void updateSelective(Car record);

	List<Car> getList(Map<String, Object> map);

	Long queryIdByName(String modelName);
	/**
	 * 通过编码.品牌..类型查询
	 * @param record
	 * @return
	 */
	Car selectByMap(Car record);

	/**
	 * 取消审核
	 * @param car
	 */
    void unaudit(Car car);
}
