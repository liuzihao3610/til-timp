package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.BillStatus;
import com.tilchina.timp.enums.FlagStatus;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.TrailerMapper;
import com.tilchina.timp.model.Trailer;
import com.tilchina.timp.service.BrandService;
import com.tilchina.timp.service.CorpService;
import com.tilchina.timp.service.TrailerService;
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

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
* 挂车型号档案
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class TrailerServiceImpl extends BaseServiceImpl<Trailer> implements TrailerService {

	@Autowired
    private TrailerMapper trailermapper;

	@Autowired
	private BrandService brandService;

	@Autowired
	private CorpService corpService;
	
	@Override
	protected BaseMapper<Trailer> getMapper() {
		return trailermapper;
	}
	private static boolean isNullAble(String nullable) {
		return "YES".equals(nullable.toUpperCase()) || "Y".equals(nullable.toUpperCase());
	}
	
	public static String checkBigDecimal(String nullable, String attName, String comment, BigDecimal decimal, int length, int scale) {
        String desc = comment+"["+attName+"]";
        if (decimal == null) {
            if (!isNullAble(nullable)) {
                return desc + " can not be null! ";
            } else {
                return "";
            }
        }
        if (decimal.toString().length() > length) {
            return desc + " value "+decimal+ " out of range [" + length + "].";
        }

        String scaleStr = decimal.toString();
        if (scale > 0 && scaleStr.contains(".") && scaleStr.split("\\.")[1].length() > scale) {
            return desc + " value "+decimal+ " scale out of range [" + scale + "].";
        }
        return "";
    }

	@Override
	protected StringBuilder checkNewRecord(Trailer trailer) {
		StringBuilder s = new StringBuilder();
		s.append(CheckUtils.checkString("NO", "trailerCode", "挂车型号", trailer.getTrailerCode(), 20));
        s.append(CheckUtils.checkString("NO", "trailerName", "挂车名称", trailer.getTrailerName(), 40));
        s.append(CheckUtils.checkInteger("NO", "trailerType", "挂车类型(0=固定", trailer.getTrailerType(), 10));
        s.append(CheckUtils.checkInteger("NO", "cargoType", "货物类型(0=通用", trailer.getCargoType(), 10));
        s.append(CheckUtils.checkInteger("YES", "trailerLong", "长(毫米）", trailer.getTrailerLong(), 10));
        s.append(CheckUtils.checkInteger("YES", "trailerWidth", "宽(毫米)", trailer.getTrailerWidth(), 10));
        s.append(CheckUtils.checkInteger("YES", "trailerHigh", "高(毫米)", trailer.getTrailerHigh(), 10));
        s.append(checkBigDecimal("YES", "price", "价格(元)", trailer.getPrice(), 10, 2));
        s.append(CheckUtils.checkString("YES", "origin", "产地", trailer.getOrigin(), 200));
        s.append(CheckUtils.checkInteger("YES", "maxQuantity", "装车数量", trailer.getMaxQuantity(), 10));
        s.append(CheckUtils.checkInteger("YES", "layer", "层数", trailer.getLayer(), 10));
        s.append(CheckUtils.checkInteger("YES", "billStatus", "状态(0=制单", trailer.getBillStatus(), 10));
        trailer.setCreateTime(new Date());
        s.append(CheckUtils.checkDate("YES", "createTime", "创建时间", trailer.getCreateTime()));
        s.append(CheckUtils.checkDate("YES", "checkDate", "审核时间", trailer.getCheckDate()));
        s.append(CheckUtils.checkInteger("YES", "flag", "封存标志", trailer.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(Trailer trailer) {
        StringBuilder s = checkNewRecord(trailer);
        s.append(CheckUtils.checkPrimaryKey(trailer.getTrailerId()));
		return s;
	}
	
	@Override
	@Transactional
	public void deleteList(int[] data) {
		if (null==data || 0==data.length) {
			throw new BusinessException(LanguageUtil.getMessage("9999"));
		}
		trailermapper.deleteList(data);
		
	}
	@Override
 	@Transactional
    public void update(List<Trailer> trailers) {
        log.debug("updateBatch: {}",trailers);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
        	if (null==trailers || 0==trailers.size()) {
    			throw new BusinessException(LanguageUtil.getMessage("9999"));
    		}
        	for (int i = 0; i < trailers.size(); i++) {
        		Trailer record = trailers.get(i);
                StringBuilder check = checkUpdate(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            	trailermapper.updateByPrimaryKeySelective(trailers.get(i));
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
	public void updateBillStatus(Long record,String path) {
		if (null==record) {
			throw new BusinessException("参数为空");
		}
			Trailer trailer=trailermapper.selectByPrimaryKey(record);
			if (trailer==null) {
				throw new BusinessException("挂车不存在");
			}
			if ("/til-timp/s1/trailer/audit".equals(path)) {
				trailer.setBillStatus(1);
				Environment env=Environment.getEnv();
				trailer.setChecker(env.getUser().getUserId());
				trailer.setCheckDate(new Date());
				trailermapper.updateByPrimaryKeySelective(trailer);
			}else if ("/til-timp/s1/trailer/unaudit".equals(path)) {
				trailermapper.removeDate(record);
			}
			
		
		
	}
	
	@Override
	@Transactional
	public PageInfo<Trailer> getReferenceList(Map<String, Object> map,int pageNum,int pageSize) {
		log.debug("getReferenceList: {}, page: {},{}", pageNum,pageSize);
		PageHelper.startPage(pageNum, pageSize);
		List<Trailer> list=trailermapper.getReferenceList(map);
		return new PageInfo<Trailer>(list);
	}
	
	//新增
	@Override
	@Transactional
    public void add(Trailer record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            Environment env=Environment.getEnv();
            record.setCreator(env.getUser().getUserId());
            //record.setCorpId(env.getCorp().getCorpId());
            trailermapper.insertSelective(record);
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
	
	
	//部分更新
	@Override
	@Transactional
    public void updateSelective(Trailer record) {
        log.debug("updateSelective: {}",record);
        	StringBuilder s = new StringBuilder();
        try {
                s = s.append(CheckUtils.checkLong("NO", "trailerId", "挂车ID", record.getTrailerId(), 20));
                if (!StringUtils.isBlank(s)) {
                    throw new BusinessException("数据检查失败：" + s.toString());
                }
                Trailer trailer=trailermapper.selectByPrimaryKey(record.getTrailerId());
                if (trailer==null) {
                	throw new BusinessException("这条记录不存在!");
    			}
                trailermapper.updateByPrimaryKeySelective(record);
            
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
    public Trailer queryById(Long id) {
        log.debug("query: {}",id);
        StringBuilder s = new StringBuilder();
        try {
            s = s.append(CheckUtils.checkLong("NO", "trailerId", "挂车ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            return trailermapper.selectByPrimaryKey(id);
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
            s = s.append(CheckUtils.checkLong("NO", "trailerId", "挂车ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            trailermapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
        	throw new BusinessException(e.getMessage());
        }
        
    }

    @Override
    @Transactional
    public List<Trailer> queryByList(List<Long> trailerIds){
	    return trailermapper.selectByTrailerIds(trailerIds);
    }

 	@Override
 	@Transactional
	public void removeDate(Long trailerId) {
		 try {
			Trailer trailer=trailermapper.selectByPrimaryKey(trailerId);
			if (trailer==null) {
				throw new BusinessException("轿运车车头不存在");
			}
			trailermapper.removeDate(trailerId);
		} catch (Exception e) {
			throw e;
		}
		
	}
	@Override
	@Transactional
	public void unaudit(Long record) {
			if (null==record) {
				throw new BusinessException("参数为空");
			}
			Trailer trailer=trailermapper.selectByPrimaryKey(record);
			if (trailer==null) {
				throw new BusinessException("挂车不存在");
			}
			
			trailermapper.removeDate(record);
			
		
	}
	@Override
	@Transactional
	public void audit(Long record) {
			if (null==record) {
				throw new BusinessException("参数为空");
			}
			Trailer trailer=trailermapper.selectByPrimaryKey(record);
			if (trailer==null) {
				throw new BusinessException("挂车不存在");
			}
			trailer.setBillStatus(1);
			Environment env=Environment.getEnv();
			trailer.setChecker(env.getUser().getUserId());
			trailer.setCheckDate(new Date());
			trailermapper.updateByPrimaryKeySelective(trailer);
			
		
	}
	
	@Override
	@Transactional
    public PageInfo<Trailer> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        try {
            if (map!=null){
                DateUtil.addTime(map);
            }
            return new PageInfo<Trailer>(getMapper().selectList(map));
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }

    }
    
    @Override
    @Transactional
    public List<Trailer> queryList(Map<String, Object> map) {
        log.debug("queryList: {}", map);
        return getMapper().selectList(map);
    }

	@Override
	public void importExcel(MultipartFile file) throws Exception {
		try {
			Environment environment = Environment.getEnv();
			String filePath = FileUtil.uploadExcel(file);
			Workbook workbook = ExcelUtil.getWorkbook(filePath);

			Map<String,Boolean> nullableMap = new HashMap<>();
			nullableMap.put("挂车名称",false);
			nullableMap.put("挂车类型",false);
			nullableMap.put("货物类型",false);
			nullableMap.put("装车数量",false);
			nullableMap.put("层数",false);
			nullableMap.put("品牌名称",false);

			String message = ExcelUtil.validateWorkbook(workbook, nullableMap);
			if (!StringUtils.isBlank(message)) {
				throw new BusinessException("数据检查失败：" + message);
			}

			List<Trailer> models = ExcelUtil.getModelsFromWorkbook(workbook, Trailer.class);

			List<String> brandNames = models.stream()
					.filter(model -> StringUtils.isNotBlank(model.getRefBrandName()))
					.map(Trailer::getRefBrandName)
					.distinct()
					.collect(Collectors.toList());
			List<String> supplierNames = models.stream()
					.filter(model -> StringUtils.isNotBlank(model.getRefSupplierName()))
					.map(Trailer::getRefSupplierName)
					.distinct()
					.collect(Collectors.toList());

			Map<String, Long> brandIdMap = new HashMap<>();
			Map<String, Long> supplierIdMap = new HashMap<>();

			for (String brandName : brandNames) {
				Long brandId = brandService.queryIdByName(brandName);
				if (Objects.isNull(brandId)) {
					throw new RuntimeException(String.format("品牌: %s不存在，请维护品牌档案后重试。", brandName));
				}
				brandIdMap.put(brandName, brandId);
			}
			for (String supplierName : supplierNames) {
				Long supplierId = corpService.queryIdByName(supplierName);
				if (Objects.isNull(supplierId)) {
					throw new RuntimeException(String.format("经销商: %s不存在，请维护公司档案后重试。", supplierName));
				}
				supplierIdMap.put(supplierName, supplierId);
			}

			models.forEach(model -> {
				model.setTrailerCode(DateUtil.dateToStringCode(new Date()));
				model.setBrandId(brandIdMap.get(model.getRefBrandName()));
				model.setSupplierId(supplierIdMap.get(model.getRefSupplierName()));
				model.setCreator(environment.getUser().getUserId());
				model.setCreateTime(new Date());
				model.setCorpId(environment.getUser().getCorpId());
				model.setBillStatus(BillStatus.Drafted.getIndex());
				model.setFlag(FlagStatus.No.getIndex());
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
			List<Trailer> results = queryList(new HashMap<>());
			Workbook workbook = ExcelUtil.getWorkbookFromModels(results);
			return workbook;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}
}
