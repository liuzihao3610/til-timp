package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Category;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
* 类别档案
*
* @version 1.1.0
* @author XiaHong
*/
public interface CategoryService extends BaseService<Category> {
	/**
	 * 批量删除
	 * @param data
	 */
	void deleteList(int[] data);

	/**
	 * 参照
	 * @param map
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageInfo<Category> getReferenceList(Map<String, Object> map, int pageNum, int pageSize);

	/**
	 * 导入类别Excel
	 * @param file
	 */
    void importExcel(MultipartFile file) throws Exception;

	/**
	 *导出Excel
	 * @return
	 */
	Workbook exportExcel() throws Exception;

	/**
	 * 通过类别名称查询类别ID
	 * @param categoryName
	 * @return
	 */
	Long queryIdByName(String categoryName);
}
