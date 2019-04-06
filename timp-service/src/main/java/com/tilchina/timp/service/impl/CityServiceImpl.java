package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.CityType;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.CityMapper;
import com.tilchina.timp.model.City;
import com.tilchina.timp.service.CityService;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.util.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* 省市区档案
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class CityServiceImpl extends BaseServiceImpl<City> implements CityService {

	@Autowired
    private CityMapper citymapper;
	
	@Autowired
    private CityService cityService;
	
	@Override
	protected BaseMapper<City> getMapper() {
		return citymapper;
	}

	@Override
	protected StringBuilder checkNewRecord(City city) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "cityCode", "城市编码", city.getCityCode(), 20));
        s.append(CheckUtils.checkString("NO", "cityName", "城市名称", city.getCityName(), 20));
        s.append(CheckUtils.checkInteger("NO", "cityType", "类型(0=国家", city.getCityType(), 10));
        s.append(CheckUtils.checkInteger("YES", "flag", "封存标志", city.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(City city) {
        StringBuilder s = checkNewRecord(city);
        s.append(CheckUtils.checkPrimaryKey(city.getCityId()));
		return s;
	}
	
	@Override
    public void add(City record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }
            Environment env=Environment.getEnv();
            record.setCorpId(env.getCorp().getCorpId());
            citymapper.insertSelective(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新保存！", e);
            } else {
                throw new BusinessException(e);
            }
        }
    }
	@Override
	@Transactional
	public void deleteList(int[] data) {
		if (null==data || 0==data.length) {
			throw new BusinessException(LanguageUtil.getMessage("9999"));
		}
		citymapper.deleteList(data);
		
	}
	

	
	 	@Override
	 	@Transactional
	    public void update(List<City> records) {
	        log.debug("updateBatch: {}",records);
	        StringBuilder s = new StringBuilder();
	        boolean checkResult = true;
	        try {
	        	if (null==records || 0==records.size()) {
	    			throw new BusinessException(LanguageUtil.getMessage("9999"));
	    		}
	        	for (int i = 0; i < records.size(); i++) {
	        		City record = records.get(i);
	                StringBuilder check = checkUpdate(record);
	                if(!StringUtils.isBlank(check)){
	                    checkResult = false;
	                    s.append("第"+(i+1)+"行，"+check+"/r/n");
	                }
	            	citymapper.updateByPrimaryKeySelective(records.get(i));
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
	                throw new BusinessException(e);
	            }
	        }

	    }
	 	
	 	@Override
	 	@Transactional
		public PageInfo<City> getReferenceList(Map<String, Object> map,int pageNum,int pageSize) {
			log.debug("getReferenceList: {}, page: {},{}", pageNum,pageSize);
			PageHelper.startPage(pageNum, pageSize);
			List<City> list=new ArrayList<City>();
			if (map==null || map.size()==0) {
				list=cityService.getFirstLevel();
			}else {
				list=citymapper.getReferenceList(map);
			}
			for (City city : list) {
				List<City> secList=citymapper.getCityList(city.getCityId());
				city.setLowerCity(secList);
				for (City second : secList) {
					List<City> thirdList=citymapper.getCityList(second.getCityId());
					second.setLowerCity(thirdList);
				}
			}
			return new PageInfo<City>(list);
		}		
		
		//部分更新
		@Override
	    public void updateSelective(City record) {
	        log.debug("updateSelective: {}",record);
	        try {
	        	StringBuilder s = new StringBuilder();
	            try {
	                s = s.append(CheckUtils.checkLong("NO", "cityId", "城市ID", record.getCityId(), 20));
	                if (!StringUtils.isBlank(s)) {
	                    throw new BusinessException("数据检查失败：" + s.toString());
	                }
	                City city=citymapper.selectByPrimaryKey(record.getCityId());
	                if (city==null) {
	                	throw new BusinessException("这条记录不存在!");
	    			}
	                citymapper.updateSelective(record);
	            } catch (Exception e) {
	            	throw new BusinessException(e.getMessage());
	            }
	            
	        } catch (Exception e) {
	        	if (e.getMessage().indexOf("IDX_") != -1) {
	                throw new BusinessException("数据重复，请查证后重新提交！",e);
	            } else if(e instanceof BusinessException){
	                throw e;
	            }else {
	                throw new BusinessException(e);
	            }
	        }
	    }
		
		//通过ID查询
		@Override
	    public City queryById(Long id) {
	        log.debug("query: {}",id);
	        StringBuilder s = new StringBuilder();
	        try {
	            return citymapper.selectByPrimaryKey(id);
	        } catch (Exception e) {
	        	throw new BusinessException(e.getMessage());
	        }
	    }
		//通过ID删除
		 @Override
	    public void deleteById(Long id) {
	        log.debug("delete: {}",id);
	        StringBuilder s = new StringBuilder();
	        try {
	            s = s.append(CheckUtils.checkLong("NO", "cityId", "城市ID", id, 20));
	            if (!StringUtils.isBlank(s)) {
	                throw new BusinessException("数据检查失败：" + s.toString());
	            }

	            citymapper.deleteByPrimaryKey(id);
	        } catch (Exception e) {
	        	throw new BusinessException(e.getMessage());
	        }
	        
	    }		

		@Override
		public List<City> getFirstLevel() {
			
			return citymapper.getFirstLevel();
		}

		@Override
		public List<City> getCityList(Long data) {
			try {
				City city=citymapper.selectByPrimaryKey(data);
				if (city==null) {
					throw new BusinessException("城市不存在");
				}
				return citymapper.getCityList(data);
			} catch (Exception e) {
				throw e;
			}
			
		}

	@Override
	public Long queryIdByName(String cityName) {

		try {
			Long cityId = citymapper.queryIdByName(cityName);
			return cityId;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public List<Long> queryByName(String cityName, CityType cityType) {

		Map<String, Object> params = new HashMap<>();
		params.put("cityName", cityName);
		params.put("cityType", cityType.getIndex());

		List<Long> cityIds = citymapper.queryByName(params);
		return cityIds;
	}

	@Override
	public List<City> queryIdByNames(List<String> cityNames) {

		List<City> cityIds = citymapper.selectByNames(cityNames);
		return cityIds;
	}

	@Override
	@Transactional
	public PageInfo<City> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
		PageHelper.startPage(pageNum, pageSize);
		try {
			if (map!=null){
				DateUtil.addTime(map);
			}
			return new PageInfo<City>(citymapper.selectList(map));
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
	}

	@Override
	public Workbook exportExcel() throws Exception {
		try {
			List<City> brands = queryList(new HashMap<>());
			Workbook workbook = ExcelUtil.getWorkbookFromModels(brands);
			return workbook;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}
}
