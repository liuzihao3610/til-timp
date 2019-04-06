package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.FlagStatus;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.BrandMapper;
import com.tilchina.timp.model.Brand;
import com.tilchina.timp.service.BrandService;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.util.ExcelUtil;
import com.tilchina.timp.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
* 品牌档案
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class BrandServiceImpl extends BaseServiceImpl<Brand> implements BrandService {

	@Autowired
    private BrandMapper brandmapper;

	@Override
	protected BaseMapper<Brand> getMapper() {
		return brandmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(Brand brand) {
		StringBuilder s = new StringBuilder();
		brand.setBrandCode(DateUtil.dateToStringCode(new Date()));
		s.append(CheckUtils.checkString("YES", "brandCode", "品牌编码", brand.getBrandCode(), 20));
        s.append(CheckUtils.checkString("NO", "brandName", "品牌名称", brand.getBrandName(), 40));
        s.append(CheckUtils.checkString("YES", "brandEnName", "英文名称", brand.getBrandEnName(), 40));
        s.append(CheckUtils.checkLong("YES", "creator", "创建人", brand.getCreator(), 20));
        brand.setCreateDate(new Date());
        s.append(CheckUtils.checkDate("YES", "createDate", "创建时间", brand.getCreateDate()));
        s.append(CheckUtils.checkLong("YES", "corpId", "公司ID", brand.getCorpId(), 20));
        s.append(CheckUtils.checkInteger("YES", "flag", "封存标志", brand.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(Brand brand) {
        StringBuilder s = checkNewRecord(brand);
        s.append(CheckUtils.checkPrimaryKey(brand.getBrandId()));
		return s;
	}
	
	//新增
	@Override
	@Transactional
    public void add(Brand record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
           
            Environment env=Environment.getEnv();
            record.setCreator(env.getUser().getUserId());
            brandmapper.insertSelective(record);
        } catch (Exception e) {
            log.error("品牌档案添加失败！",e);
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新保存！",e);
            } else if(e instanceof BusinessException){
                throw e;
            }else {
            	throw new RuntimeException("操作失败");
            }
        }
    }

    @Override
    public Map<Long,Brand> queryMap(){
        List<Brand> brands = queryList(null);
        Map<Long,Brand> brandMap = new HashMap<>();
        for (Brand brand : brands) {
            brandMap.put(brand.getBrandId(),brand);
        }
        return brandMap;
    }

	//部分更新
	@Override
	@Transactional
    public void updateSelective(Brand record) {
        log.debug("updateSelective: {}",record);
        	StringBuilder s = new StringBuilder();
        try {
                s = s.append(CheckUtils.checkLong("NO", "brandId", "品牌ID", record.getBrandId(), 20));
                if (!StringUtils.isBlank(s)) {
                    throw new BusinessException("数据检查失败：" + s.toString());
                }
                Brand brand=brandmapper.selectByPrimaryKey(record.getBrandId());
                if (brand==null) {
                	throw new BusinessException("这条记录不存在!");
				}
                record.setBrandCode(null);
                record.setCorpId(null);
                brandmapper.updateByPrimaryKeySelective(record);
            
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！",e);
            } else if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("更新失败！");
            }
        }
    }
	
	//通过ID查询
	@Override
	@Transactional
    public Brand queryById(Long id) {
        log.debug("query: {}",id);
        StringBuilder s = new StringBuilder();
        try {
            s = s.append(CheckUtils.checkLong("NO", "brandId", "品牌ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            return brandmapper.selectByPrimaryKey(id);
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
            s = s.append(CheckUtils.checkLong("NO", "brandId", "品牌ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            brandmapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
        	throw new BusinessException(e.getMessage());
        }
        
    }
	 //批量删除
	@Override
	@Transactional
	public void deleteList(int[] data) {
		try {
			if (null==data || 0==data.length) {
				throw new BusinessException("参数为空");
			}
			brandmapper.deleteList(data);
		} catch (Exception e) {
			throw e;
		}
		
		
	}
	
 	@Override
 	@Transactional
    public void update(List<Brand> records) {
        log.debug("updateBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
        	if (null==records || 0==records.size()) {
    			throw new BusinessException("参数为空,请检查!");
    		}
        	for (int i = 0; i < records.size(); i++) {
        		Brand record = records.get(i);
                StringBuilder check = checkUpdate(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
                brandmapper.updateByPrimaryKeySelective(record);
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
                throw new RuntimeException("更新失败");
            }
        }

    }

 	@Override
 	@Transactional
	public PageInfo<Brand> getReferenceList(Map<String, Object> map,int pageNum,int pageSize) {
		log.debug("getReferenceList: {}, page: {},{}", pageNum,pageSize);
		PageHelper.startPage(pageNum, pageSize);
		List<Brand> list=brandmapper.getReferenceList(map);
		return new PageInfo<Brand>(list);
	}

	@Override
	@Transactional
	public Brand getByName(String brandName) {
		try {
			return brandmapper.getByName(brandName);
		} catch (Exception e) {
			throw e;
		}
		
	}
	/**
	 * 通过品牌名称查询
	 */
	@Override
	@Transactional
	public List<Brand> queryByNames(Set<String> names){
        if(CollectionUtils.isEmpty(names)){
            return null;
        }
        return brandmapper.selectByNames(names);
    }
	
	@Override
	@Transactional
    public PageInfo<Brand> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        try {
            if (map!=null){
                DateUtil.addTime(map);
            }
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
        return new PageInfo<Brand>(getMapper().selectList(map));
    }
    
    @Override
    @Transactional
    public List<Brand> queryList(Map<String, Object> map) {
        log.debug("queryList: {}", map);
        return brandmapper.selectList(map);
    }
    /**
     * 查询结果按创建时间倒序排序
     */
	@Override
	@Transactional
	public PageInfo<Brand> getList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        
		try {
			PageHelper.startPage(pageNum, pageSize);
			return new PageInfo<Brand>(brandmapper.getList(map));
		} catch (Exception e) {
			throw e;
		}
	}

	/* 请勿更改
	 * XueYuSong
	 */
	@Override
	public Long queryIdByName(String brandName) {

		try {
			Long brandId = brandmapper.queryIdByName(brandName);
			return brandId;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public List<Long> queryByName(String carBrandName) {
		List<Long> carBrandIds = brandmapper.queryByName(carBrandName);
		return carBrandIds;
	}

    @Override
    public void add(List<Brand> records) {
        log.debug("addBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
            Environment env = Environment.getEnv();
            for (int i = 0; i < records.size(); i++) {
                Brand record = records.get(i);
                record.setFlag(FlagStatus.No.getIndex());
                record.setBrandCode(DateUtil.dateToStringCode(new Date()));
                record.setCreateDate(new Date());
                record.setCreator(env.getUser().getUserId());
                record.setCorpId(env.getCorp().getCorpId());
                StringBuilder check = checkNewRecord(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            }
            if (!checkResult) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            getMapper().insert(records);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新保存！", e);
            } else {
                throw e;
            }
        }
    }

    @Override
    public void importExcel(MultipartFile file) throws Exception {
        String filePath;
        try {
            filePath = FileUtil.uploadExcel(file);
            Workbook workbook = ExcelUtil.getWorkbook(filePath);
            Map<String,Boolean> map = new HashMap<>();
            map.put("品牌名称",false);
            String massage = ExcelUtil.validateWorkbook(workbook, map);
            if (!StringUtils.isBlank(massage)) {
                throw new BusinessException("数据检查失败：" + massage);
            }
            List<Brand> importBrands = ExcelUtil.getModelsFromWorkbook(workbook, Brand.class);
            List<Brand> brands = new ArrayList<Brand>();
            importBrands.forEach(importBrand -> {
                Brand brand = new Brand();
                brand.setBrandName(importBrand.getBrandName());
                brand.setBrandEnName(importBrand.getBrandEnName());
                brands.add(brand);
            });
            add(brands);
        } catch (Exception e) {
            log.error("{}", e);
            throw e;
        }
    }

	@Override
	public Workbook exportExcel() throws Exception {
		try {
			List<Brand> brands = queryList(new HashMap<>());
			Workbook workbook = ExcelUtil.getWorkbookFromModels(brands);
			return workbook;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}
}
