package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.Brand;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* 品牌档案
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface BrandMapper extends BaseMapper<Brand> {

	void deleteList(int[] brandIds);

	List<Brand> getReferenceList(Map<String, Object> map);

	Brand getByName(String brandName);

	List<Brand> selectByNames(@Param("names") Set<String> names);
	/**
	 * 按创建时间倒序排列
	 * @param map
	 * @return
	 */
	List<Brand> getList(Map<String, Object> map);

	Long queryIdByName(String brandName);

	List<Brand> selectByBrandName(@Param("brandNames") List<String> brandNames);

	List<Long> queryByName(@Param("carBrandName") String carBrandName);
}
