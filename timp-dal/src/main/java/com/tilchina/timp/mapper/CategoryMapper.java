package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.Category;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* 类别档案
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface CategoryMapper extends BaseMapper<Category> {

	void deleteList(int[] data);

	List<Category> getReferenceList(Map<String, Object> map);

	List<Category> getList(Map<String, Object> map);

	/**
	 * 通过类别名称查询类别ID
	 * @param categoryName
	 * @return
	 */
    Long queryIdByName(String categoryName);
}
