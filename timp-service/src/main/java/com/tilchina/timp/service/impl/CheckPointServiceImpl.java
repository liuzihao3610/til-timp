package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.CheckPointMapper;
import com.tilchina.timp.model.CheckPoint;
import com.tilchina.timp.service.CheckPointService;
import com.tilchina.timp.service.CityService;
import com.tilchina.timp.service.ContactsService;
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
import java.util.stream.Collectors;

/**
* 检查站
*
* @version 1.0.0
* @author LiShuqi
*/
@Service
@Slf4j
public class CheckPointServiceImpl extends BaseServiceImpl<CheckPoint> implements CheckPointService {

	@Autowired
    private CheckPointMapper checkpointmapper;

	@Autowired
	private ContactsService contactsService;

	@Autowired
	private CityService cityService;
	
	@Override
	protected BaseMapper<CheckPoint> getMapper() {
		return checkpointmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(CheckPoint checkpoint) {
		StringBuilder s = new StringBuilder();
        checkpoint.setCheckPointCode(DateUtil.dateToStringCode(new Date()));
        s.append(CheckUtils.checkString("YES", "checkPointCode", "检查站编码", checkpoint.getCheckPointCode(), 20));
        s.append(CheckUtils.checkString("NO", "checkPointName", "检查站名称", checkpoint.getCheckPointName(), 40));
        s.append(CheckUtils.checkString("YES", "enName", "英文名称", checkpoint.getEnName(), 40));
        s.append(CheckUtils.checkInteger("NO", "checkPointType", "检查站类型", checkpoint.getCheckPointType(), 10));
        s.append(CheckUtils.checkString("NO", "checkContent", "检查内容", checkpoint.getCheckContent(), 100));
        s.append(CheckUtils.checkLong("NO", "contact", "联系人", checkpoint.getContact(), 20));
        s.append(CheckUtils.checkString("NO", "address", "检查站地址", checkpoint.getAddress(), 100));
        s.append(CheckUtils.checkString("YES", "telephone", "固定电话", checkpoint.getTelephone(), 30));
        s.append(CheckUtils.checkString("YES", "fax", "传真", checkpoint.getFax(), 30));
        checkpoint.setCreateTime(new Date());
        s.append(CheckUtils.checkDate("YES", "createTime", "创建时间", checkpoint.getCreateTime()));
        s.append(CheckUtils.checkInteger("YES", "flag", "封存标志", checkpoint.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(CheckPoint checkpoint) {
        StringBuilder s = checkNewRecord(checkpoint);
        s.append(CheckUtils.checkPrimaryKey(checkpoint.getCheckPointId()));
		return s;
	}
	
	
	@Override
	@Transactional
	public void deleteList(int[] data) {
		if (null==data || 0==data.length) {
			throw new BusinessException(LanguageUtil.getMessage("9999"));
		}
		checkpointmapper.deleteList(data);
		
	}
	

	
 	@Override
 	@Transactional
    public void update(List<CheckPoint> records) {
        log.debug("updateBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
        	if (null==records || 0==records.size()) {
    			throw new BusinessException(LanguageUtil.getMessage("9999"));
    		}
        	for (int i = 0; i < records.size(); i++) {
        		CheckPoint record = records.get(i);
                StringBuilder check = checkUpdate(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            	checkpointmapper.updateByPrimaryKeySelective(records.get(i));
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
	public PageInfo<CheckPoint> getReferenceList(Map<String, Object> map,int pageNum,int pageSize) {
		log.debug("getReferenceList: {}, page: {},{}", pageNum,pageSize);
		PageHelper.startPage(pageNum, pageSize);
		List<CheckPoint> list=checkpointmapper.getReferenceList(map);
		return new PageInfo<CheckPoint>(list);
	}
 	
	//新增
	@Override
	@Transactional
    public void add(CheckPoint record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            Environment env=Environment.getEnv();
            record.setCreator(env.getUser().getUserId());
            checkpointmapper.insertSelective(record);
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
    public void updateSelective(CheckPoint record) {
        log.debug("updateSelective: {}",record);
        try {
        	StringBuilder s = new StringBuilder();
            try {
                s = s.append(CheckUtils.checkLong("NO", "checkPointId", "检查站ID", record.getCheckPointId(), 20));
                if (!StringUtils.isBlank(s)) {
                    throw new BusinessException("数据检查失败：" + s.toString());
                }
                CheckPoint checkPoint=checkpointmapper.selectByPrimaryKey(record.getCheckPointId());
                if (checkPoint==null) {
                	throw new BusinessException("这条记录不存在!");
    			}
                record.setCheckPointCode(null);
                checkpointmapper.updateByPrimaryKeySelective(record);
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
    public CheckPoint queryById(Long id) {
        log.debug("query: {}",id);
        StringBuilder s = new StringBuilder();
        try {
            s = s.append(CheckUtils.checkLong("NO", "checkPointId", "检查站ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            return checkpointmapper.selectByPrimaryKey(id);
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
            s = s.append(CheckUtils.checkLong("NO", "checkPointId", "检查站ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            checkpointmapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
        	throw new BusinessException(e.getMessage());
        }
        
    }
	 
	 
	 /**
	 * 按创建时间倒序排列
	 * @param map
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
   @Override
   @Transactional
   public PageInfo<CheckPoint> getList(Map<String, Object> map, int pageNum, int pageSize) {
       log.debug("queryList: {}", map);
       PageHelper.startPage(pageNum, pageSize);
       Environment environment=Environment.getEnv();
       map.put("corpId", environment.getCorp().getCorpId());
       return new PageInfo<CheckPoint>(checkpointmapper.getList(map)) ;
   }

    @Override
    @Transactional
    public PageInfo<CheckPoint> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        try {
            if (map!=null){
                DateUtil.addTime(map);
            }
            return new PageInfo<CheckPoint>(checkpointmapper.selectList(map));
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
			map.put("检查站名称",false);
			map.put("检查站类型",false);
			map.put("检查内容",false);
			map.put("检查站地址",false);
			map.put("固定电话",false);
			map.put("联系人",false);
			map.put("省名称",false);
			map.put("市名称",false);
			map.put("区名称",false);

			String message = ExcelUtil.validateWorkbook(workbook, map);
			if (!StringUtils.isBlank(message)) {
				throw new BusinessException("数据导入失败: " + message);
			}
			List<CheckPoint> models = ExcelUtil.getModelsFromWorkbook(workbook, CheckPoint.class);

			Map<String, Long> contactNameMap = models.stream()
					.filter(model -> StringUtils.isNotBlank(model.getRefUserName()))
					.collect(Collectors.toMap(CheckPoint::getRefUserName, model -> contactsService.queryByName(model.getRefUserName()).getContactsId(), (oldKey, newKey) -> newKey));

			List<String> refProvinceNameList = models.stream()
					.filter(model -> StringUtils.isNotBlank(model.getRefProvinceName()))
					.map(CheckPoint::getRefProvinceName)
					.distinct()
					.collect(Collectors.toList());

			List<String> refCityNameList = models.stream()
					.filter(model -> StringUtils.isNotBlank(model.getRefCityName()))
					.map(CheckPoint::getRefCityName)
					.distinct()
					.collect(Collectors.toList());

			List<String> refAreaNameList = models.stream()
					.filter(model -> StringUtils.isNotBlank(model.getRefAreaName()))
					.map(CheckPoint::getRefAreaName)
					.distinct()
					.collect(Collectors.toList());

			models.forEach(model -> {
				model.setCheckPointCode(DateUtil.dateToStringCode(new Date()));
				model.setCreator(environment.getUser().getUserId());
				model.setCreateTime(new Date());
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
			List<CheckPoint> checkPoints = queryList(new HashMap<>());
			Workbook workbook = ExcelUtil.getWorkbookFromModels(checkPoints);
			return workbook;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}
}
