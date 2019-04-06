package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.FlagStatus;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.OilPriceMapper;
import com.tilchina.timp.model.OilPrice;
import com.tilchina.timp.service.OilPriceService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* 油价档案
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class OilPriceServiceImpl extends BaseServiceImpl<OilPrice> implements OilPriceService {

	@Autowired
    private OilPriceMapper oilpricemapper;
	
	@Override
	protected BaseMapper<OilPrice> getMapper() {
		return oilpricemapper;
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
	protected StringBuilder checkNewRecord(OilPrice oilprice) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "oilName", "名称", oilprice.getOilName(), 20));
        s.append(CheckUtils.checkInteger("NO", "oilType", "类型(0=汽油", oilprice.getOilType(), 10));
        s.append(CheckUtils.checkString("NO", "labeling", "标号", oilprice.getLabeling(), 10));
        s.append(checkBigDecimal("NO", "price", "价格", oilprice.getPrice(), 10, 3));
        s.append(CheckUtils.checkDate("NO", "effectiveDate", "生效日期", oilprice.getEffectiveDate()));
        s.append(CheckUtils.checkInteger("YES", "flag", "封存标志", oilprice.getFlag(), 10));
		return s;
	}
	

	@Override
	protected StringBuilder checkUpdate(OilPrice oilprice) {
        StringBuilder s = checkNewRecord(oilprice);
        s.append(CheckUtils.checkPrimaryKey(oilprice.getOilPriceId()));
		return s;
	}
	
	@Override
	@Transactional
    public void add(OilPrice record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            Environment env=Environment.getEnv();
            record.setCorpId(env.getCorp().getCorpId());
            oilpricemapper.insertSelective(record);
        } catch (Exception e) {
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
	@Transactional
	public void deleteList(int[] data) {
		if (null==data || 0==data.length) {
			throw new BusinessException(LanguageUtil.getMessage("9999"));
		}
		oilpricemapper.deleteList(data);
		
	}
	

	
 	@Override
 	@Transactional
    public void update(List<OilPrice> oilPrices) {
        log.debug("updateBatch: {}",oilPrices);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
        	if (null==oilPrices || 0==oilPrices.size()) {
    			throw new BusinessException(LanguageUtil.getMessage("9999"));
    		}
        	for (int i = 0; i < oilPrices.size(); i++) {
        		OilPrice record = oilPrices.get(i);
                StringBuilder check = checkUpdate(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            	oilpricemapper.updateByPrimaryKeySelective(oilPrices.get(i));
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
	public PageInfo<OilPrice> getReferenceList(Map<String, Object> map,int pageNum,int pageSize) {
		log.debug("getReferenceList: {}, page: {},{}", pageNum,pageSize);
		PageHelper.startPage(pageNum, pageSize);
		List<OilPrice> list=oilpricemapper.getReferenceList(map);
		return new PageInfo<OilPrice>(list);
	}	
	//部分更新
	@Override
	@Transactional
    public void updateSelective(OilPrice record) {
        log.debug("updateSelective: {}",record);
        	StringBuilder s = new StringBuilder();
        try {
                s = s.append(CheckUtils.checkLong("NO", "oilPriceId", "油价ID", record.getOilPriceId(), 20));
                if (!StringUtils.isBlank(s)) {
                    throw new BusinessException("数据检查失败：" + s.toString());
                }
                OilPrice oilPrice=oilpricemapper.selectByPrimaryKey(record.getOilPriceId());
                if (oilPrice==null) {
                	throw new BusinessException("这条记录不存在!");
    			}
                oilpricemapper.updateByPrimaryKeySelective(record);
            
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
    public OilPrice queryById(Long id) {
        log.debug("query: {}",id);
        StringBuilder s = new StringBuilder();
        try {
            s = s.append(CheckUtils.checkLong("NO", "oilPriceId", "油价ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            return oilpricemapper.selectByPrimaryKey(id);
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
            s = s.append(CheckUtils.checkLong("NO", "oilPriceId", "油价ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            oilpricemapper.deleteByPrimaryKey(id);
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

			Map<String,Boolean> nullableMap = new HashMap<>();
			nullableMap.put("名称",false);
			nullableMap.put("类型",false);
			nullableMap.put("标号",false);
			nullableMap.put("价格",false);
			nullableMap.put("生效日期",false);

			String message = ExcelUtil.validateWorkbook(workbook, nullableMap);
			if (!StringUtils.isBlank(message)) {
				throw new BusinessException("数据检查失败：" + message);
			}

			List<OilPrice> models = ExcelUtil.getModelsFromWorkbook(workbook, OilPrice.class);
			models.forEach(model -> {
				model.setCorpId(environment.getUser().getCorpId());
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
			List<OilPrice> results = queryList(new HashMap<>());
			Workbook workbook = ExcelUtil.getWorkbookFromModels(results);
			return workbook;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}
}
