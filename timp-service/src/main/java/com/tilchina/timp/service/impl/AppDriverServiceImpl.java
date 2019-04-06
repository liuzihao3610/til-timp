package com.tilchina.timp.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.TransporterAndDriverMapper;
import com.tilchina.timp.model.TransporterAndDriver;
import com.tilchina.timp.service.AppDriverService;
import lombok.extern.slf4j.Slf4j;

/**
* 
*
* @version 1.0.0
* @author Xiahong
*/
@Service
@Slf4j
public class AppDriverServiceImpl implements AppDriverService{
	
	@Autowired
    private TransporterAndDriverMapper transporterAndDriverMapper;
    
	@Override
	public List<TransporterAndDriver> queryList() {
		List<TransporterAndDriver> tansporterAndDrivers = null;
		Map<String, Object> map;
		Environment env = Environment.getEnv();
		try {
			map = new HashMap<>();
			map.put("driverId",env.getUser().getUserId());
			tansporterAndDrivers = transporterAndDriverMapper.selectByAppDriverId(map);
			//0=一般货车 1=半挂牵引车 2=全挂牵引车
			/*for (TransporterAndDriver transporterAndDriver : tansporterAndDrivers) {
				if("0".equals(transporterAndDriver.getRefTractorType())) {
					transporterAndDriver.setRefTractorType("固定");
				}else if("1".equals(transporterAndDriver.getRefTractorType())) {
					transporterAndDriver.setRefTractorType("半挂");
			    }else if("2".equals(transporterAndDriver.getRefTractorType())) {
			    	transporterAndDriver.setRefTractorType("全挂");
			    }
			}*/
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw new BusinessException(e.getMessage());
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
		return tansporterAndDrivers;
	}

}
