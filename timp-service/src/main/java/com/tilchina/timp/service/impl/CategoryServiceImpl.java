package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.CategoryMapper;
import com.tilchina.timp.model.Category;
import com.tilchina.timp.service.CategoryService;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.util.ExcelUtil;
import com.tilchina.timp.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* 类别档案
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class CategoryServiceImpl extends BaseServiceImpl<Category> implements CategoryService {

	@Autowired
    private CategoryMapper categorymapper;
	
	@Override
	protected BaseMapper<Category> getMapper() {
		return categorymapper;
	}

	@Override
	protected StringBuilder checkNewRecord(Category category) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("YES", "categoryCode", "类别编码", category.getCategoryCode(), 20));
        s.append(CheckUtils.checkString("NO", "categoryName", "类别名称", category.getCategoryName(), 40));
        s.append(CheckUtils.checkDate("YES", "createDate", "创建时间", category.getCreateDate()));
        s.append(CheckUtils.checkInteger("YES", "flag", "封存标志", category.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(Category category) {
        StringBuilder s = checkNewRecord(category);
        s.append(CheckUtils.checkPrimaryKey(category.getCategoryId()));
		return s;
	}

    /**
     * 新增类别
     * @param record
     */
	@Override
	@Transactional
    public void add(Category record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }
            Environment env=Environment.getEnv();
            record.setCreator(env.getUser().getUserId());
            record.setCreateDate(new Date());
            record.setCorpId(env.getCorp().getCorpId());
            record.setCategoryCode(DateUtil.dateToStringCode(new Date()));
            record.setFlag(0);
            categorymapper.insertSelective(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新保存！", e);
            } else {
                throw new RuntimeException("保存失败！", e);
            }
        }
    }

    /**
     * 批量删除
     * @param data
     */
	@Override
	@Transactional
	public void deleteList(int[] data) {
		if (null==data || 0==data.length) {
			throw new BusinessException(LanguageUtil.getMessage("9999"));
		}
		categorymapper.deleteList(data);
		
	}

    /**
     * 更新全部
     * @param records
     */
	@Override
 	@Transactional
    public void update(List<Category> records) {
        log.debug("updateBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
        	if (null==records || 0==records.size()) {
    			throw new BusinessException(LanguageUtil.getMessage("9999"));
    		}
        	for (int i = 0; i < records.size(); i++) {
        		Category record = records.get(i);
                StringBuilder check = checkUpdate(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            	categorymapper.updateByPrimaryKeySelective(records.get(i));
            }
            if (!checkResult) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
        } catch (Exception e) {
        	if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！",e);
            } else if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("批量更新类别失败");
            }
        }

    }

    /**
     * 参照
     * @param map
     * @param pageNum
     * @param pageSize
     * @return
     */
	@Override
	@Transactional
	public PageInfo<Category> getReferenceList(Map<String, Object> map,int pageNum,int pageSize) {
		log.debug("getReferenceList: {}, page: {},{}", pageNum,pageSize);
		PageHelper.startPage(pageNum, pageSize);
		List<Category> list=categorymapper.getReferenceList(map);
		return new PageInfo<Category>(list);
	}

    /**
     * 导入Excel
     * @param file
     */
    @Override
    @Transactional
    public void importExcel(MultipartFile file) {
        String filePath = null;
        Workbook workbook = null;
        String s = null;
        Map<String, Boolean> nullableMap = new HashMap<>();
        try{
            filePath=FileUtil.uploadFile(file,"category");
            workbook=ExcelUtil.getWorkbook(filePath);
            nullableMap.put("类别名称",false);
            s=ExcelUtil.validateWorkbook(workbook,nullableMap);
            List<Category> categories = ExcelUtil.getModelsFromWorkbook(workbook,Category.class);
            for (int i = 0; i <categories.size() ; i++) {
                add(categories.get(i));
            }

        }catch (Exception e){
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！",e);
            } else if(e instanceof BusinessException){
                throw new BusinessException(e.getMessage());
            }else {
                throw new RuntimeException("导入类别失败");
            }
        }

    }

    /**
     * 导出Excel
     * @return
     */
    @Override
    @Transactional
    public Workbook exportExcel() throws Exception {
        try {
            List<Category> categories = queryList(new HashMap<>());
            Workbook workbook = ExcelUtil.getWorkbookFromModels(categories);
            return workbook;
        } catch (Exception e) {
            log.error("{}", e);
            throw e;
        }
    }

    /**
     * 通过类别名称查询类别ID
     * @param categoryName
     * @return
     */
    @Override
    @Transactional
    public Long queryIdByName(String categoryName) {
        return categorymapper.queryIdByName(categoryName);
    }


    //部分更新
	@Override
	@Transactional
    public void updateSelective(Category record) {
        log.debug("updateSelective: {}",record);
        try {
        	StringBuilder s = new StringBuilder();
            try {
                s = s.append(CheckUtils.checkLong("NO", "categoryId", "商品车类别ID", record.getCategoryId(), 20));
                if (!StringUtils.isBlank(s)) {
                    throw new BusinessException("数据检查失败：" + s.toString());
                }
                Category category=categorymapper.selectByPrimaryKey(record.getCategoryId());
                if (category==null) {
                	throw new BusinessException("这条记录不存在!");
    			}
                record.setCategoryCode(null);
                categorymapper.updateByPrimaryKeySelective(record);
            } catch (Exception e) {
            	throw new BusinessException(e.getMessage());
            }
            
        } catch (Exception e) {
        	if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！",e);
            } else if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("更新类别失败");
            }
        }
    }
	
	//通过ID查询
	@Override
	@Transactional
    public Category queryById(Long id) {
        log.debug("query: {}",id);
        StringBuilder s = new StringBuilder();
        try {
            s = s.append(CheckUtils.checkLong("NO", "categoryId", "商品车类别ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            return categorymapper.selectByPrimaryKey(id);
        } catch (Exception e) {
        	throw new BusinessException(e.getMessage());
        }
    }
	//通过ID删除
	@Override
	@Transactional
    public void deleteById(Long id) {
        log.debug("delete: {}",id);
        StringBuilder s = new StringBuilder();
        try {
            s = s.append(CheckUtils.checkLong("NO", "categoryId", "商品车类别ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            categorymapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
        	throw new BusinessException(e.getMessage());
        }
        
    }

    /**
     * 分页查询
     * @param map
     * @param pageNum
     * @param pageSize
     * @return
     */
	@Override
	@Transactional
    public PageInfo<Category> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        try {
            if (map!=null){
                DateUtil.addTime(map);
            }
            return new PageInfo<Category>(categorymapper.selectList(map));
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }

    }

    /**
     * 查询所有类别
      * @param map
     * @return
     */
    @Override
    @Transactional
    public List<Category> queryList(Map<String, Object> map) {
        log.debug("queryList: {}", map);
        try {
            if (map!=null){
                DateUtil.addTime(map);
            }
            return categorymapper.selectList(map);
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }
	 
	
}
