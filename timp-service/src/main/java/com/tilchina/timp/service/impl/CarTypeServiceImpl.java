package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.CarTypeMapper;
import com.tilchina.timp.model.Brand;
import com.tilchina.timp.model.CarType;
import com.tilchina.timp.service.BrandService;
import com.tilchina.timp.service.CarTypeService;
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

import java.util.*;
import java.util.stream.Collectors;

/**
* 商品车类别档案
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class CarTypeServiceImpl extends BaseServiceImpl<CarType> implements CarTypeService {

	@Autowired
    private CarTypeMapper cartypemapper;
	
	@Autowired
	private BrandService brandService;
	
	@Override
	protected BaseMapper<CarType> getMapper() {
		return cartypemapper;
	}

	@Override
	protected StringBuilder checkNewRecord(CarType cartype) {
		StringBuilder s = new StringBuilder();
		cartype.setCarTypeCode(DateUtil.dateToStringCode(new Date()));
        s.append(CheckUtils.checkString("YES", "carTypeCode", "类别编码", cartype.getCarTypeCode(), 20));
        s.append(CheckUtils.checkString("NO", "carTypeName", "类别名称", cartype.getCarTypeName(), 40));
        cartype.setCreateDate(new Date());
        s.append(CheckUtils.checkDate("YES", "createDate", "创建时间", cartype.getCreateDate()));
        s.append(CheckUtils.checkInteger("YES", "flag", "封存标志", cartype.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(CarType cartype) {
        StringBuilder s = checkNewRecord(cartype);
        s.append(CheckUtils.checkPrimaryKey(cartype.getCarTypeId()));
		return s;
	}

	@Override
	public Map<String,CarType> queryMap(){
        List<CarType> carTypes = queryList(null);
        Map<String,CarType> carTypeMap = new HashMap<>();
        for (CarType carType : carTypes) {
            carTypeMap.put(carType.getBrandId()+"_"+carType.getCarTypeId(),carType);
        }
        return carTypeMap;
    }
	
	@Override
	@Transactional
    public void add(CarType record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            Brand brand=brandService.queryById(record.getBrandId());
            if (brand==null) {
            	throw new BusinessException("品牌不存在") ;
			}
            Environment env=Environment.getEnv();
            record.setCreator(env.getUser().getUserId());
            cartypemapper.insertSelective(record);
        } catch (Exception e) {
        	if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！",e);
            } else if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败");
            }
        }
    }
	
	@Override
	@Transactional
	public void deleteList(int[] data) {
		if (null==data || 0==data.length) {
			throw new BusinessException(LanguageUtil.getMessage("9999"));
		}
		cartypemapper.deleteList(data);
		
	}
	@Override
 	@Transactional
    public void update(List<CarType> records) {
        log.debug("updateBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
        	if (null==records || 0==records.size()) {
    			throw new BusinessException(LanguageUtil.getMessage("9999"));
    		}
        	for (int i = 0; i < records.size(); i++) {
        		CarType record = records.get(i);
                StringBuilder check = checkUpdate(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            	cartypemapper.updateByPrimaryKeySelective(records.get(i));
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
                throw new RuntimeException("操作失败");
            }
        }

    }

	@Override
	@Transactional
	public PageInfo<CarType> getCarTypeName(Map<String, Object> map,int pageNum,int pageSize) {
		log.debug("getReferenceList: {}, page: {},{}", pageNum,pageSize);
		PageHelper.startPage(pageNum, pageSize);
		try {
			if (null==map||0==map.size()) {
				throw new BusinessException("参数为空");
			}
			Brand brand=brandService.queryById(Long.parseLong(map.get("brandId").toString()));
			if (brand==null) {
				throw new BusinessException("品牌不存在");
			}
			List<CarType> list=cartypemapper.getCarTypeName(map);
			return new PageInfo<CarType>(list);
		} catch (Exception e) {
			throw e;
		}
		
	}	
	
	//部分更新
	@Override
	@Transactional
    public void updateSelective(CarType record) {
        log.debug("updateSelective: {}",record);
        try {
        	StringBuilder s = new StringBuilder();
            try {
                s = s.append(CheckUtils.checkLong("NO", "carTypeId", "商品车类型ID", record.getCarTypeId(), 20));
                if (!StringUtils.isBlank(s)) {
                    throw new BusinessException("数据检查失败：" + s.toString());
                }
                CarType carType=cartypemapper.selectByPrimaryKey(record.getCarTypeId());
                if (carType==null) {
                	throw new BusinessException("这条记录不存在!");
    			}
                record.setCarTypeCode(null);
                cartypemapper.updateByPrimaryKeySelective(record);
            } catch (Exception e) {
            	throw new BusinessException(e.getMessage());
            }
            
        } catch (Exception e) {
        	if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！",e);
            } else if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败");
            }
        }
    }
	
	//通过ID查询
	@Override
	@Transactional
    public CarType queryById(Long id) {
        log.debug("query: {}",id);
        StringBuilder s = new StringBuilder();
        try {
            s = s.append(CheckUtils.checkLong("NO", "carTypeId", "商品车类型ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            return cartypemapper.selectByPrimaryKey(id);
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
            s = s.append(CheckUtils.checkLong("NO", "carTypeId", "商品车类型ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            cartypemapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
        	throw new BusinessException(e.getMessage());
        }
        
    }

	 @Override
	 @Transactional
	public PageInfo<CarType> getReferenceList(Map<String, Object> map,int pageNum,int pageSize) {
		log.debug("getReferenceList: {}, page: {},{}", pageNum,pageSize);
		PageHelper.startPage(pageNum, pageSize);
		List<CarType> list=cartypemapper.getReferenceList(map);
		return new PageInfo<CarType>(list);
	}

	@Override
	public Long queryIdByName(String typeName) {
		try {
			Long carTypeId = cartypemapper.queryIdByName(typeName);
			return carTypeId;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public List<Long> queryByName(String carTypeName) {
		List<Long> carTypeIds = cartypemapper.queryByName(carTypeName);
		return carTypeIds;
	}

	@Override
    @Transactional
    public PageInfo<CarType> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map, pageNum, pageSize);
        PageHelper.startPage(pageNum, pageSize);
        try {
            if (map!=null){
                DateUtil.addTime(map);
            }
            return new PageInfo(this.getMapper().selectList(map));
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

	@Override
	public void importExcel(MultipartFile file) throws Exception {
		try {
			Environment environment = Environment.getEnv();
			String filePath = FileUtil.uploadExcel(file);
			Workbook workbook = ExcelUtil.getWorkbook(filePath);

			Map<String,Boolean> map = new HashMap<>();
			map.put("类别名称",false);
			map.put("品牌名称",false);

			String message = ExcelUtil.validateWorkbook(workbook, map);
			if (!StringUtils.isBlank(message)) {
				throw new BusinessException("数据检查失败：" + message);
			}

			List<CarType> models = ExcelUtil.getModelsFromWorkbook(workbook, CarType.class);
			List<String> brandNames = models.stream()
					.filter(model -> StringUtils.isNotBlank(model.getRefBrandName()))
					.map(CarType::getRefBrandName)
					.distinct()
					.collect(Collectors.toList());

			Map<String, Long> brandIdMap = new HashMap<>();
			for (String brandName : brandNames) {
				Long brandId = brandService.queryIdByName(brandName);
				if (Objects.isNull(brandId)) {
					throw new RuntimeException("品牌名称不存在，请检查后重试。");
				}
				brandIdMap.put(brandName, brandId);
			}

			models.forEach(model -> {
				model.setCarTypeCode(DateUtil.dateToStringCode(new Date()));
				model.setBrandId(brandIdMap.get(model.getRefBrandName()));
				model.setCreator(environment.getUser().getUserId());
				model.setCreateDate(new Date());
				model.setCorpId(environment.getUser().getCorpId());
				model.setFlag(0);
			});
			add(models);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public Workbook exportExcel() throws Exception {
		try {
			List<CarType> results = queryList(new HashMap<>());
			Workbook workbook = ExcelUtil.getWorkbookFromModels(results);
			return workbook;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}
}
