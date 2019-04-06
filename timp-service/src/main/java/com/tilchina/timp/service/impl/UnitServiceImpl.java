package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.BCryptUtil;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.*;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.UnitMapper;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import com.tilchina.timp.util.CheckUtil;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.util.ExcelUtil;
import com.tilchina.timp.util.FileUtil;
import com.tilchina.timp.utils.GenPassword;
import com.tilchina.timp.vo.UnitExcelVO;
import com.tilchina.timp.vo.UnitVO;
import com.tilchina.timp.vo.UniversalExcelVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.midi.Receiver;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
* 收发货单位
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class UnitServiceImpl /*extends BaseServiceImpl<Unit>*/  implements UnitService {

	@Autowired
	private UnitMapper unitmapper;

	@Autowired
	private UnitRelationService unitRelationService;

	@Autowired
	private UnitLoginService unitLoginService;

	@Autowired
	private UnitStopService unitStopService;

	@Autowired
	private ContactsService contactsService;

	@Autowired
	private CorpService corpService;

	@Autowired
	private CityService cityService;

	protected BaseMapper<Unit> getMapper() {
		return unitmapper;
	}

	protected StringBuilder checkNewRecord(Unit unit) {
		StringBuilder s = new StringBuilder();
		if (unit.getUnitCode() == null || "".equals(unit.getUnitCode())){
			unit.setUnitCode(DateUtil.dateToStringCode(new Date()));
		}
		s.append(CheckUtils.checkString("YES", "unitCode", "单位编码", unit.getUnitCode(), 20));
		s.append(CheckUtils.checkString("NO", "unitName", "单位名称", unit.getUnitName(), 40));
		s.append(CheckUtils.checkString("YES", "enName", "英文名称", unit.getEnName(), 40));
		s.append(CheckUtils.checkInteger("NO", "unitType", "单位类型(0=经销店", unit.getUnitType(), 10));
		s.append(CheckUtils.checkInteger("YES", "deliver", "交车店(0=否", unit.getDeliver(), 10));
		s.append(CheckUtils.checkInteger("YES", "special", "特殊经销店(0=否", unit.getSpecial(), 10));
		s.append(CheckUtils.checkString("YES", "unitCityCode", "城市编号(客户提供)", unit.getUnitCityCode(), 20));
		s.append(CheckUtils.checkLong("YES", "provinceId", "省", unit.getProvinceId(), 20));
		s.append(CheckUtils.checkLong("NO", "cityId", "市", unit.getCityId(), 20));
		s.append(CheckUtils.checkLong("YES", "areaId", "区", unit.getAreaId(), 20));
		s.append(CheckUtils.checkString("NO", "address", "单位地址", unit.getAddress(), 100));
		s.append(CheckUtils.checkString("YES", "telephone", "固定电话", unit.getTelephone(), 30));
		s.append(CheckUtils.checkString("YES", "fax", "传真", unit.getFax(), 30));
		s.append(CheckUtils.checkBigDecimal("YES", "deliverWash", "交车洗车费用", unit.getDeliverWash(), 10, 2));
		s.append(CheckUtils.checkBigDecimal("YES", "swingWash", "甩车洗车费", unit.getSwingWash(), 10, 2));
		s.append(CheckUtils.checkInteger("YES", "washCar", "洗车费结算方式:0=都支持", unit.getWashCar(), 10));
		s.append(CheckUtils.checkInteger("YES", "level", "级别:0=普通", unit.getLevel(), 10));
		s.append(CheckUtils.checkInteger("YES", "receive", "接车方式:0=本店", unit.getReceive(), 10));
		s.append(CheckUtils.checkString("YES", "dealerCode", "经销商代码", unit.getDealerCode(), 20));
		s.append(CheckUtils.checkString("YES", "citySettlement", "同城结算点:用于同城业务识别结算路线和报价", unit.getCitySettlement(), 20));
		unit.setCreateTime(new Date());
		s.append(CheckUtils.checkDate("YES", "createTime", "创建时间", unit.getCreateTime()));
		s.append(CheckUtils.checkInteger("YES", "flag", "封存标志", unit.getFlag(), 10));
		if (unit.getLat() != null){
			if (!CheckUtil.checkLat(unit.getLat().toString())){
				s.append("此纬度格式无效：" + unit.getLat()+"。");
			}
		}
		if (unit.getLng() != null){
			if (!CheckUtil.checkLnt(unit.getLng().toString())){
				s.append("此经度格式无效：" + unit.getLng()+"。");
			}
		}
		if (unit.getTelephone() != null && !"".equals(unit.getTelephone())){
			if (!CheckUtil.checkPhoneAndTelephone(unit.getTelephone())){
				s.append("此固定电话号码格式无效：" + unit.getTelephone()+"。");
			}
		}
		return s;
	}

	/**
	 * 去除首尾空格
	 * @param unit
	 * @return
	 */
	protected Unit checkTrim(Unit unit) {
		CheckUtil.checkStringTrim(unit.getUnitName());
		CheckUtil.checkStringTrim(unit.getUnitCode());
		CheckUtil.checkStringTrim(unit.getEnName());
		CheckUtil.checkStringTrim(unit.getUnitCityCode());
		CheckUtil.checkStringTrim(unit.getAddress());
		CheckUtil.checkStringTrim(unit.getTelephone());
		CheckUtil.checkStringTrim(unit.getFax());
		CheckUtil.checkStringTrim(unit.getDeliverDescription());
		CheckUtil.checkStringTrim(unit.getDealerCode());
		CheckUtil.checkStringTrim(unit.getCitySettlement());
		return unit;
	}

	protected StringBuilder checkUpdate(Unit unit) {
		StringBuilder s = checkNewRecord(unit);
		s.append(CheckUtils.checkPrimaryKey(unit.getUnitId()));
		return s;
	}

	@Override
	public Map<Long, Unit> queryMap() {
		List<Unit> units = queryList(null);
		Map<Long, Unit> unitMap = new HashMap<>();
		for (Unit unit : units) {
			unitMap.put(unit.getUnitId(), unit);
		}
		return unitMap;
	}

	@Override
	public Map<Long, Unit> queryMapByIds(List<Long> unitIds) {
		List<Unit> units = queryByIds(unitIds);
		Map<Long, Unit> unitMap = new HashMap<>();
		for (Unit unit : units) {
			unitMap.put(unit.getUnitId(), unit);
		}
		return unitMap;
	}

	@Override
	public List<Unit> queryByIds(List<Long> unitIds) {
		if (CollectionUtils.isEmpty(unitIds)) {
			throw new BusinessException("查询【收发货单位】请求参数错误！");
		}
		return unitmapper.selectByUnitIds(unitIds);
	}




	@Override
	@Transactional
	public void deleteList(int[] data) {
		if (null == data || 0 == data.length) {
			throw new BusinessException(LanguageUtil.getMessage("9999"));
		}
		unitmapper.deleteList(data);

	}


	@Override
	@Transactional
	public void update(List<Unit> records) {
		log.debug("updateBatch: {}", records);
		StringBuilder s = new StringBuilder();
		boolean checkResult = true;
		try {
			if (null == records || 0 == records.size()) {
				throw new BusinessException(LanguageUtil.getMessage("9999"));
			}
			for (int i = 0; i < records.size(); i++) {
				Unit record = records.get(i);
				StringBuilder check = checkUpdate(record);
				if (!StringUtils.isBlank(check)) {
					checkResult = false;
					s.append("第" + (i + 1) + "行，" + check + "/r");
				}
				record = checkTrim(record);
				unitmapper.updateByPrimaryKeySelective(record);
			}
			if (!checkResult) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("【收发货单位】单位名称数据重复，请查证后重新提交！", e);
			} else if (e instanceof BusinessException) {
				throw e;
			} else {
				throw new RuntimeException("操作失败");
			}
		}

	}

	@Override
	@Transactional
	public PageInfo<Unit> getReferenceList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("getReferenceList: {}, page: {},{}", pageNum, pageSize);
		PageHelper.startPage(pageNum, pageSize);
		List<Unit> list = unitmapper.getReferenceList(map);
		return new PageInfo<Unit>(list);
	}

	@Override
	@Transactional
	public List<UnitVO> add(List<Unit> records) {
		log.debug("addBatch: {}", records);
		StringBuilder s = new StringBuilder();
		List<UnitRelation> relations = new ArrayList<UnitRelation>();
		boolean checkResult = true;
		List<UnitVO> unitVOS = new ArrayList<UnitVO>();
		List<UnitLogin> unitLogins = new ArrayList<UnitLogin>();
		try {
			Environment env = Environment.getEnv();
			for (int i = 0; i < records.size(); i++) {
				Unit unit = records.get(i);
				StringBuilder check = checkNewRecord(unit);
				UnitRelation unitRelation = new UnitRelation();
				unitRelation.setUnitId(unit.getUnitId());
				unitRelation.setAdsCorpId(env.getCorp().getCorpId());
				;
				relations.add(unitRelation);
				//	收发货单位登陆信息
				UnitVO unitVO = new UnitVO();
				String password = "til@" + unit.getDealerCode();
				if (unit.getDealerCode() == null || "".equals(unit.getDealerCode())){
					password = "til@" + DateUtil.dateToStringYearCode(new Date());
				}
				unitVO.setUnitCode(unit.getUnitCode());
				unitVO.setPassword(password);
				// 维护认证信息
				UnitLogin login = null;
				if (login == null) {
					login = new UnitLogin();
					login.setUnitId(unit.getUnitId());
					login.setCorpId(env.getCorp().getCorpId());
					login.setPassword(BCryptUtil.hash(password));

					login.setClientType(ClientType.WEB.getIndex());
					login.setBlock(Block.UNLOCKED.getIndex());
					login.setFlag(FlagStatus.No.getIndex());
					login.setErrorTimes(ErrorTimes.INITIALIZE.getIndex());
					unitVOS.add(unitVO);
					unitLogins.add(login);
				}
				if (!StringUtils.isBlank(check)) {
					checkResult = false;
					s.append("第" + (i + 1) + "行，" + check + "/r");
				}
			}
			if (!checkResult) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			getMapper().insert(records);
			unitRelationService.add(relations);
			unitLoginService.add(unitLogins);
			return unitVOS;
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新保存！", e);
			} else {
				throw e;
			}
		}
	}

	//新增
	@Override
	@Transactional
	public UnitVO add(Unit unit) {
		log.debug("add: {}", unit);
		StringBuilder s;
		UnitVO unitVO = new UnitVO();
		try {
			s = checkNewRecord(unit);
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			Environment env = Environment.getEnv();
			unit.setCreator(env.getUser().getUserId());
			unit.setCorpId(env.getCorp().getCorpId());
			unit = checkTrim(unit);
			unitmapper.insertSelective(unit);
			//	公司收发货单位关系表
			UnitRelation unitRelation = new UnitRelation();
			unitRelation.setUnitId(unit.getUnitId());
			unitRelation.setAdsCorpId(env.getCorp().getCorpId());

			unitRelationService.add(unitRelation);

			//	收发货单位登陆信息:经销商代码为空则默认当前年份
			String password = "til@" + unit.getDealerCode();
			if (unit.getDealerCode() == null || "".equals(unit.getDealerCode())){
				password = "til@" + DateUtil.dateToStringYearCode(new Date());
			}
			unitVO.setUnitCode(unit.getUnitCode());
			unitVO.setPassword(password);
			// 维护认证信息
			UnitLogin login = null;
			if (login == null) {
				login = new UnitLogin();
				login.setUnitId(unit.getUnitId());
				login.setCorpId(env.getCorp().getCorpId());
				login.setPassword(BCryptUtil.hash(password));

				login.setClientType(ClientType.WEB.getIndex());
				login.setBlock(Block.UNLOCKED.getIndex());
				login.setFlag(FlagStatus.No.getIndex());
				login.setErrorTimes(ErrorTimes.INITIALIZE.getIndex());
			}
			unitLoginService.add(login);
			return unitVO;
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("【收发货单位】单位名称："+unit.getUnitName()+"数据重复，请查证后重新提交！", e);
			} else if (e instanceof BusinessException) {
				throw e;
			} else {
				throw new RuntimeException("操作失败");
			}
		}
	}


	//部分更新
	@Override
	@Transactional
	public void updateSelective(Unit record) {
		log.debug("updateSelective: {}", record);
		StringBuilder s = new StringBuilder();
		try {
			s = s.append(CheckUtils.checkLong("NO", "unitId", "收发货单位ID", record.getUnitId(), 20));
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			Unit unit = unitmapper.selectByPrimaryKey(record.getUnitId());
			if (unit == null) {
				throw new BusinessException("这条记录不存在!");
			}
			record = checkTrim(record);
			unitmapper.updateByPrimaryKeySelective(record);

		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("【收发货单位】单位名称："+record.getUnitName()+"数据重复，请查证后重新提交！", e);
			} else if (e instanceof BusinessException) {
				throw e;
			} else {
				throw new RuntimeException("操作失败");
			}
		}
	}

	@Override
	public void update(Unit record) {

	}

	//通过ID查询
	@Override
	@Transactional
	public Unit queryById(Long id) {
		log.debug("query: {}", id);
		StringBuilder s = new StringBuilder();
		try {
			s = s.append(CheckUtils.checkLong("NO", "unitId", "收发货单位ID", id, 20));
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}

			return unitmapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
	}

	//通过ID删除
	@Override
	@Transactional
	public void deleteById(Long id) {
		log.debug("delete: {}", id);
		StringBuilder s = new StringBuilder();
		try {
			s = s.append(CheckUtils.checkLong("NO", "unitId", "收发货单位ID", id, 20));
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}

			unitmapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}

	}

	@Override
	@Transactional
	public Unit getSingleUnit(Map<String, Object> map) {
		return unitmapper.getSingleUnit(map);
	}

	@Override
	@Transactional
	public Unit getContactInfoById(Long unitId) {

		return unitmapper.getContactInfoById(unitId);
	}

	@Override
	@Transactional
	public Unit getByName(String startPlace) {
		try {
			return unitmapper.getByName(startPlace);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public List<Unit> queryByName(String unitName) {
		try {
			List<Unit> units = unitmapper.queryByName(unitName);
			return units;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public List<Unit> queryByNames(Set<String> names) {
		if (CollectionUtils.isEmpty(names)) {
			return null;
		}
		return unitmapper.queryByNames(names);
	}

	@Override
	@Transactional
	public List<Unit> queryListByNames(List<String> names) {
		try {
			if (names == null || names.size() == 0) {
				throw new BusinessException("参数为空");
			}
			List<Unit> units = new ArrayList<Unit>();
			for (int i = 0; i < names.size(); i++) {
				Unit unit = unitmapper.queryListByNames(names.get(i));
				if (unit == null) {
					throw new BusinessException("收货单位" + names.get(i) + "不存在");
				}
				units.add(unit);
			}
			return units;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public List<Long> queryByUnitName(String unitName) {
		List<Long> unitIds = unitmapper.queryByUnitName(unitName);
		return unitIds;
	}

	@Override
	public PageInfo<Unit> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map, pageNum, pageSize);
		PageHelper.startPage(pageNum, pageSize);
		Map<String, Object> date;
		try {

			Environment environment = Environment.getEnv();
			date = Optional.ofNullable(map).orElse(new HashMap<>());
			DateUtil.addTime(date);
			List<UnitRelation> relations = new ArrayList<>();
				// 	用户登陆/收发货单位登陆
		/*	List<UnitRelation> unitRelations = 	Optional.ofNullable(unitRelationService.queryByCorpId(environment.getCorp().getCorpId())).orElse(new ArrayList<UnitRelation>());
			if (environment.getCorp().getCorpId() != null){
				relations = unitRelationService.queryByCorpId(environment.getCorp().getCorpId());
			}else if (unitRelations.size() > 0){
				relations = unitRelations;
			}
			List<Long> ids = new ArrayList<>();
			relations.forEach(unitRelation -> ids.add(unitRelation.getUnitId()));
			if (ids.size() > 0){
				date.put("unitIds",ids);
			}*/

			PageInfo pageInfo = new PageInfo(this.getMapper().selectList(date));
			// 添加收发货联系人信息
			List<Unit> unitList = pageInfo.getList();
			List<Long> unitIds = new ArrayList<>();
			unitList.forEach(unit -> unitIds.add(unit.getUnitId()));
            if (!org.springframework.util.CollectionUtils.isEmpty(unitIds)){
                List<Contacts> contactsList = contactsService.queryByUnitIds(unitIds);
                StringBuilder refUnitContacts = new StringBuilder() ;
                for (Unit unit : unitList) {
                    for (Contacts contacts : contactsList) {
                        if (unit.getUnitId().longValue() == contacts.getUnitId().longValue()){
                            refUnitContacts.append(contacts.getContactsName()+"："+contacts.getPhone()+"；");
                        }
                    }
                    unit.setRefUnitContacts(refUnitContacts.toString());
                    refUnitContacts = new StringBuilder() ;
                }
                pageInfo.setList(unitList);
            }
			return pageInfo;
		} catch (Exception e) {
			if (e instanceof BusinessException) {
				throw e;
			} else {
				throw new RuntimeException("操作失败！", e);
			}
		}

	}

	@Override
	public List<Unit> queryList(Map<String, Object> map) {
		log.debug("queryList: {}", map);
		try {
			List<Unit> unitList = getMapper().selectList(map);
			// 添加收发货联系人信息
			List<Long> unitIds = new ArrayList<>();
			unitList.forEach(unit -> unitIds.add(unit.getUnitId()));
			List<Contacts> contactsList = contactsService.queryByUnitIds(unitIds);
			StringBuilder refUnitContacts = new StringBuilder() ;
			for (Unit unit : unitList) {
				for (Contacts contacts : contactsList) {
					if (unit.getUnitId().longValue() == contacts.getUnitId().longValue()){
						refUnitContacts.append(contacts.getContactsName()+"："+contacts.getPhone()+"；");
					}
				}
				unit.setRefUnitContacts(refUnitContacts.toString());
				refUnitContacts = new StringBuilder() ;
			}
			return unitList;
		}catch (Exception e){
			if (e instanceof BusinessException) {
				throw e;
			} else {
				throw new RuntimeException("操作失败！", e);
			}
		}

	}

	@Override
	public void updateBlock(Unit unit) {
		log.debug("updateBlock:{}", unit);
		StringBuilder s;
		UnitLogin login = null;
		UnitLogin loginBak = null;
		try {
			s = new StringBuilder();
			s.append(CheckUtils.checkLong("NO", "unitId", "收发货单位Id", unit.getUnitId(), 20));
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			login = new UnitLogin();
			queryById(unit.getUnitId());
			loginBak = unitLoginService.queryByUnitId(unit.getUnitId());
			login.setBlock(Block.UNLOCKED.getIndex());
			login.setErrorTimes(ErrorTimes.INITIALIZE.getIndex());
			login.setUnitLoginId(loginBak.getUnitLoginId());
			unitLoginService.updateSelective(login);
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新保存！", e);
			} else if (e instanceof BusinessException) {
				throw e;
			} else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}

	@Override
	public UnitVO updatePasswords(Unit unit) {
		log.debug("updatePasswords:{}", unit);
		StringBuilder s;
		UnitLogin login = null;
		UnitVO unitVO = new UnitVO();
		/*JSONObject json = null;*/
		String password = null;
		try {
			s = new StringBuilder();
			s.append(CheckUtils.checkLong("NO", "unitId", "收发货单位Id", unit.getUnitId(), 20));
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			unit = queryById(unit.getUnitId());
			login = new UnitLogin();
			login.setUnitLoginId(unitLoginService.queryByUnitId(unit.getUnitId()).getUnitLoginId());
			password = GenPassword.get();
			login.setPassword(BCryptUtil.hash(password));
			login.setErrorTimes(ErrorTimes.INITIALIZE.getIndex());
			login.setBlock(Block.UNLOCKED.getIndex());
			unitLoginService.updateSelective(login);
			//	设置返回信息
			unitVO.setUnitCode(unit.getUnitCode());
			unitVO.setUnitName(unit.getUnitName());
			unitVO.setPassword(password);
			return unitVO;
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新保存！", e);
			} else if (e instanceof BusinessException) {
				throw e;
			} else {
				throw new RuntimeException("操作失败！", e);
			}
		}

	}

	@Override
	public String importContacts(Map<String, Object> map) {
		log.debug("importContacts:{}", map);
		StringBuilder message;
		try {
			Environment environment = Environment.getEnv();
			message = new StringBuilder();
			Optional.ofNullable(map.get("unitId"))
					.orElseThrow(() -> new BusinessException("请输入有效的收发货单位id。"));
			Optional.ofNullable(map.get("contactsList"))
					.orElseThrow(() -> new BusinessException("请输入有效的收发货单位联系人信息。"));
			List<Contacts> contactsList = CheckUtil.checkContacts(map.get("contactsList").toString().trim());
			Long unitId = Long.valueOf(map.get("unitId").toString().trim());
			Unit unit = queryById(unitId);
			if (contactsList.size() > 0){
				message.append("新增成功,联系人信息：收发货单位名称："+unit.getUnitName()+"。\r\n");
			}else {
				throw new BusinessException("此"+map.get("contactsList").toString().trim()+"格式无效，请输入有效的信息。");
			}
			contactsList.stream().forEach(contacts -> {
				contacts.setUnitId(unitId);
				contacts.setCreator(environment.getUser().getUserId());
				contacts.setCreateDate(new Date());
				contacts.setFlag(FlagStatus.No.getIndex());
				contacts.setCorpId(environment.getCorp().getCorpId());
				message.append("收发货单位联系人："+contacts.getContactsName()+"，手机号："+contacts.getPhone()+"；\r\n");
			});
			contactsService.add(contactsList);
			return message.toString();
		} catch (Exception e) {
			if (e instanceof BusinessException) {
				throw e;
			} else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}

	@Transactional
	@Override
	public void importExcel(MultipartFile file) throws Exception {
		log.debug("importExcel:{}", file);
		String filePath;
		InputStream fileStream;
		try {
			filePath = FileUtil.uploadFile(file, "Unit");
			fileStream = new FileInputStream(filePath);
			Workbook workbook = ExcelUtil.getWorkbook(filePath);
			if (filePath.endsWith(".xls")) {
				workbook = new HSSFWorkbook(fileStream);
			} else if (filePath.endsWith(".xlsx")) {
				workbook = new XSSFWorkbook(fileStream);
			} else {
				throw new BusinessException("文件类型有误，请重新上传。");
			}
			parseExcelForUnit(workbook);
		} catch (Exception e) {
			if (e instanceof BusinessException) {
				throw e;
			} else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}

	private void parseExcelForUnit(Workbook workbook){
		List<UnitExcelVO> excelVOList = new ArrayList<UnitExcelVO>();
		List<Unit> unitList = new ArrayList<Unit>();
		List<UnitStop> unitStopList = new ArrayList<UnitStop>();
		List<Contacts> contactsList = new ArrayList<Contacts>();
		try {
			Environment env = Environment.getEnv();
			Sheet sheet = workbook.getSheetAt(0);

			int startRow = 1;
			int colCount = 34;
			int rowCount = ExcelUtil.getLastRowNumber(workbook);

			StringBuilder errorMsg = new StringBuilder();
			String[] headerValues = {
					"单位名称", "英文名称", "单位类型", "特殊经销店", "结算隶属点","城市编号","省", "市", "区", "单位地址", "固定电话", "传真","经度", "维度",
					"归属客户", "交车方式", "轿车方式描述", "交车洗车费用","甩车洗车费用", "洗车费结算方式", "级别", "接车方式", "经销商代码", "同城结算",
					"联系人姓名", "手机号", "固定电话", "邮箱", "QQ", "微信号","停靠点地址","是否需要继续运输", "小板费支付方式", "接车点经度", "接车点维度",
			};
			Map<Integer, Boolean> nullable = new HashMap();
			nullable.put(0, false);
			nullable.put(1, true);
			nullable.put(2, true);
			nullable.put(3, true);
			nullable.put(4, true);
			nullable.put(5, true);
			nullable.put(6, false);
			nullable.put(7, false);
			nullable.put(8, true);
			nullable.put(9, false);
			nullable.put(10, true);
			nullable.put(11, true);
			nullable.put(12, false);
			nullable.put(13, false);
			nullable.put(14, true);
			nullable.put(15, true);
			nullable.put(16, true);
			nullable.put(17, true);
			nullable.put(18, true);
			nullable.put(19, true);
			nullable.put(20, true);
			nullable.put(21, true);
			nullable.put(22, true);
			nullable.put(23, true);
			nullable.put(24, true);
			nullable.put(25, true);
			nullable.put(26, true);
			nullable.put(27, true);
			nullable.put(28, true);
			nullable.put(29, true);
			nullable.put(30, true);
			nullable.put(31, true);
			nullable.put(32, true);
			nullable.put(33, true);
			nullable.put(34, true);
			DataFormatter formatter = new DataFormatter();
			Set<String> names = new HashSet<String>();
			if(rowCount == 0){
				throw  new BusinessException("您导入的Excel中没有数据，请检查。");
			}
			for (int i = startRow; i <= rowCount; i++) {

				String[] cellValues = new String[colCount + 1];
				for (int j = 0; j <= colCount; j++) {

					Cell cell = sheet.getRow(i).getCell(j);
					if (cell == null) {
						if (!nullable.get(j)){
							errorMsg.append(String.format("[行:%d][列:%s] [%s]必填，不能为空。", i + 1, j + 1, headerValues[j]));
						}

						continue;
					}
					cellValues[j] = formatter.formatCellValue(cell);

					if (!nullable.get(j) && StringUtils.isBlank(cellValues[j])) {
						errorMsg.append(String.format("[行:%d][列:%s] [%s]必填，不能为空。", i + 1, j + 1, headerValues[j]));
					}
				}
                UnitExcelVO excelVO = new UnitExcelVO();
                //	收发货单位
                excelVO.setUnitName(cellValues[0]);
                if (excelVO.getUnitName() != null)
                {
                    names.add(excelVO.getUnitName().trim());
                }
                excelVO.setEnName(cellValues[1]);
                excelVO.setUnitType(cellValues[2]);
                excelVO.setSpecial(cellValues[3]);
                excelVO.setAccountCityName(cellValues[4]);
                excelVO.setUnitCityCode(cellValues[5]);
                excelVO.setProvinceName(cellValues[6]);
                excelVO.setCityName(cellValues[7]);
                excelVO.setAreaName(cellValues[8]);
                excelVO.setAddress(cellValues[9]);
                excelVO.setTelephone(cellValues[10]);
                excelVO.setFax(cellValues[11]);
                if (cellValues[12] != null){
                	if (!CheckUtil.checkDouble(cellValues[12])){
						 throw new BusinessException("此数值是无效Double的"+cellValues[12]+"。例：有效的Double：12.21");
					}
                    excelVO.setLng(Double.parseDouble(cellValues[12]));
                }
                if (cellValues[13] != null){
					if (!CheckUtil.checkDouble(cellValues[13])){
						throw new BusinessException("此数值是无效Double的"+cellValues[13]+"。例：有效的Double：12.21");
					}
                    excelVO.setLat(Double.parseDouble(cellValues[13]));
                }

                excelVO.setCustomerCorpName(cellValues[14]);
                excelVO.setDeliver(cellValues[15]);
                excelVO.setDeliverDescription(cellValues[16]);
                if (cellValues[17] != null){
					excelVO.setDeliverWash( new BigDecimal(cellValues[17]));
				}else {
					excelVO.setDeliverWash( BigDecimal.ZERO);
				}
				if (cellValues[18] != null){
					excelVO.setSwingWash( new BigDecimal(cellValues[18]));
				}else {
					excelVO.setDeliverWash( BigDecimal.ZERO);
				}

                excelVO.setWashCar(cellValues[19]);
                excelVO.setLevel(cellValues[20]);
                excelVO.setReceive(cellValues[21]);
                excelVO.setDealerCode(cellValues[22]);
                excelVO.setCitySettlement(cellValues[23]);

                //	收发货单位联系人
                excelVO.setContactsName(cellValues[24]);
				excelVO.setPhone(cellValues[25]);
                excelVO.setFix(cellValues[26]);
                excelVO.setEmail(cellValues[27]);
                if (cellValues[28] != null){
					if (!CheckUtil.checkInteger(cellValues[28])){
						throw new BusinessException("此数值是无效Integer的"+cellValues[28]+"。例：有效的Integer：12");
					}
                    excelVO.setQq(Integer.parseInt(cellValues[28]));
                }
                excelVO.setWechat(cellValues[29]);
                //	接车点信息
                excelVO.setUnitStopAdress(cellValues[30]);
                excelVO.setContinueTransport(cellValues[31]);
                excelVO.setSmallCartPay(cellValues[32]);
                if (cellValues[33] != null){
                    excelVO.setUnitStopLng(Double.parseDouble(cellValues[33]));
                }
                if (cellValues[34] != null){
                    excelVO.setUnitStopLat(Double.parseDouble(cellValues[34]));
                }
                excelVOList.add(excelVO);
			}

            if (StringUtils.isNotBlank(errorMsg.toString())) {
				throw  new BusinessException(errorMsg.toString());
            }
                // 收发货单位档案
            List<Unit> units = queryByNames(names);
            //	城市:	省、市、区、城市ID
			List<String> cityNames = excelVOList.stream().map(UnitExcelVO::getCityName).distinct().collect(Collectors.toList());
			cityNames.addAll(excelVOList.stream().map(UnitExcelVO::getAreaName).distinct().collect(Collectors.toList()));
			cityNames.addAll(excelVOList.stream().map(UnitExcelVO::getProvinceName).distinct().collect(Collectors.toList()));
			cityNames.addAll(excelVOList.stream().map(UnitExcelVO::getAccountCityName).distinct().collect(Collectors.toList()));
				//	公司:	所属客户
			List<String> customerCorpNames = excelVOList.stream().map(UnitExcelVO::getCustomerCorpName).distinct().collect(Collectors.toList());
			if(CollectionUtils.isEmpty(cityNames) && CollectionUtils.isEmpty(customerCorpNames)){
				throw new BusinessException("没有有效的收发货单位名称或省市区档案！");
			}else if (CollectionUtils.isEmpty(cityNames) ){
				throw new BusinessException("没有有效的收发货单位名称！");
			}else if (CollectionUtils.isEmpty(customerCorpNames) ){
				throw new BusinessException("没有有效的省市区档案！");
			}else {
				List<City> cityList = cityService.queryIdByNames(cityNames);
				List<Corp>  corpList = corpService.queryByNames(customerCorpNames);

				Map<String, Unit> unitMap = new HashMap<String, Unit>();
				Map<String, City> provinceMap = new HashMap<String, City>();
				Map<String, City> cityMap = new HashMap<String, City>();
				Map<String, City> areaMap = new HashMap<String, City>();
				Map<String, Long> corpMap = new HashMap<String, Long>();

				units.forEach(unit -> unitMap.put(unit.getUnitName(),unit));
				cityList.forEach(city -> {
					if (city.getCityType().intValue() == CityType.Province.getIndex()){
						provinceMap.put(city.getCityName(),city);
					}
				});
				cityList.forEach(city -> {
					if (city.getCityType().intValue() == CityType.City.getIndex()){
						cityMap.put(city.getCityName(),city);
					}
				});
				cityList.forEach(city -> {
					if (city.getCityType().intValue() == CityType.District.getIndex()){
						areaMap.put(city.getCityName(),city);
					}
				});
				corpList.forEach(corp -> corpMap.put(corp.getCorpName(),corp.getCorpId()));
				//	校验
				for (UnitExcelVO unitExcelVO : excelVOList) {
					//	收发货单位
					if (UnitType.get(unitExcelVO.getUnitType()) == null){
						unitExcelVO.setUnitType(UnitType.AGENCY.getName());
//					errorMsg.append("没有此单位类型：" + unitExcelVO.getUnitType()+"。");
					}
					if (Special.get(unitExcelVO.getSpecial()) == null){
						unitExcelVO.setSpecial(Special.NO.getName());
//					errorMsg.append("没有此特殊经销店：" + unitExcelVO.getSpecial()+"。");
					}
					if (Deliver.get(unitExcelVO.getDeliver()) == null){
						unitExcelVO.setDeliver(Deliver.UNLIMITED.getName());
//					errorMsg.append("没有此交车方式：" + unitExcelVO.getDeliver()+"。");
					}
					if (WashCar.get(unitExcelVO.getWashCar()) == null){
						unitExcelVO.setWashCar(WashCar.UNLIMITED.getName());
//					errorMsg.append("没有此洗车费结算方式：" + unitExcelVO.getWashCar()+"。");
					}
					if (Level.get(unitExcelVO.getLevel()) == null){
						unitExcelVO.setLevel(Level.COMMON.getName());
//					errorMsg.append("没有此级别：" + unitExcelVO.getLevel()+"。");
					}
					if (Receive.get(unitExcelVO.getReceive()) == null){
						unitExcelVO.setReceive(Receive.OUR.getName());
//					errorMsg.append("没有此接车方式：" + unitExcelVO.getReceive()+"。");
					}
					/*if (cityMap.get(unitExcelVO.getAccountCityName()) == null){
						errorMsg.append("没有此结算隶属点：" + unitExcelVO.getAccountCityName()+"。");
					}*/
					if (provinceMap.get(unitExcelVO.getProvinceName()) == null){
						errorMsg.append("没有此省：" + unitExcelVO.getProvinceName()+"。");
					}
					if (cityMap.get(unitExcelVO.getCityName()) == null){
						errorMsg.append("没有此市：" + unitExcelVO.getCityName()+"。");
					}
				/*if (areaMap.get(unitExcelVO.getAreaName()) == null){
					errorMsg.append("没有此区：" + unitExcelVO.getAreaName()+"。");
				}*/
					if (corpMap.get(unitExcelVO.getCustomerCorpName()) == null){
						errorMsg.append("没有此所属客户：" + unitExcelVO.getCustomerCorpName()+"。");
					}
					if (unitExcelVO.getLat() != null){
						if (!CheckUtil.checkLat(unitExcelVO.getLat().toString())){
							errorMsg.append("收发货单位名称："+unitExcelVO.getUnitName()+"的纬度格式无效：" + unitExcelVO.getLat()+",纬度合理取值范围0——90。");
						}
					}
					if (unitExcelVO.getLng() != null){
						if (!CheckUtil.checkLnt(unitExcelVO.getLng().toString())){
							errorMsg.append("收发货单位名称："+unitExcelVO.getUnitName()+"的经度格式无效：" + unitExcelVO.getLng()+",经度合理取值范围0——180。");
						}
					}
					if (unitExcelVO.getTelephone() != null){
						if (!CheckUtil.checkPhoneAndTelephone(unitExcelVO.getTelephone().toString())){
							errorMsg.append("收发货单位名称："+unitExcelVO.getUnitName()+"的固定电话号码格式无效：" + unitExcelVO.getTelephone()+"。");
						}
					}
					//收发货联系人：当收发货联系人姓名存在时，则校验手机号码、固定电话、邮箱
					if (unitExcelVO.getContactsName() != null){
						if(unitExcelVO.getPhone() != null){
							if (!CheckUtil.checkMobileNumber(unitExcelVO.getPhone().trim())){
								errorMsg.append("收发货联系人："+unitExcelVO.getContactsName()+"的手机号码格式不规范：" + unitExcelVO.getPhone()+"。");
							}
						}
						if(unitExcelVO.getEmail() != null && !"".equals(unitExcelVO.getEmail())){
							if (!CheckUtil.checkEmail(unitExcelVO.getEmail().trim())){
								errorMsg.append("收发货联系人："+unitExcelVO.getContactsName()+"的邮箱格式不规范：" + unitExcelVO.getEmail()+"。");
							}
						}
						if (unitExcelVO.getFix() != null){
							if (!CheckUtil.checkPhoneAndTelephone(unitExcelVO.getTelephone().toString())){
								errorMsg.append("收发货联系人："+unitExcelVO.getContactsName()+"固定电话号码格式无效：" + unitExcelVO.getFix()+"。");
							}
						}
						if (unitExcelVO.getQq() != null){
							if (!CheckUtil.checkQq(unitExcelVO.getQq().toString())){
								errorMsg.append("收发货联系人："+unitExcelVO.getContactsName()+"QQ号码格式无效：" + unitExcelVO.getQq()+"。");
							}
						}
					}

					//	接车点管理
					if (unitExcelVO.getUnitStopAdress() != null){
						if (ContinueTransport.get(unitExcelVO.getContinueTransport()) == null){
							unitExcelVO.setContinueTransport(ContinueTransport.NO.getName());
//                        errorMsg.append("没有此是否需要继续运输：" + unitExcelVO.getContinueTransport()+"。");
						}
						if (SmallCartPay.get(unitExcelVO.getSmallCartPay()) == null){
							unitExcelVO.setSmallCartPay(SmallCartPay.SELF_PAY.getName());
//                        errorMsg.append("没有此小板费支付方式：" + unitExcelVO.getSmallCartPay()+"。");
						}
						if (unitExcelVO.getUnitStopLat() != null){
							errorMsg.append("接车点地址："+unitExcelVO.getUnitStopAdress()+"的纬度格式无效：" + unitExcelVO.getUnitStopLat()+",纬度合理取值范围0——90。");
						}
						if (unitExcelVO.getUnitStopLng() != null){
							errorMsg.append("接车点地址："+unitExcelVO.getUnitStopAdress()+"的经度格式无效：" + unitExcelVO.getUnitStopLng()+",经度合理取值范围0——180。");
						}
					}
				}

				if (StringUtils.isNotBlank(errorMsg.toString())) {
					throw new  BusinessException(errorMsg.toString());
				}
				//	添加数据
				for (UnitExcelVO unitExcelVO : excelVOList) {
					UnitType unitType = Optional.ofNullable(UnitType.get(unitExcelVO.getUnitType()))
							.orElseThrow(() ->  new BusinessException("没有此单位类型：" + unitExcelVO.getUnitType()+"。"));

					Special special = Optional.ofNullable(Special.get(unitExcelVO.getSpecial()))
							.orElseThrow(() -> new BusinessException("没有此特殊经销店：" + unitExcelVO.getSpecial() + "。"));

					Deliver deliver = Optional.ofNullable(Deliver.get(unitExcelVO.getDeliver()))
							.orElseThrow(() -> new BusinessException("没有此交车方式：" + unitExcelVO.getDeliver() + "。"));

					WashCar washCar = Optional.ofNullable(WashCar.get(unitExcelVO.getWashCar()))
							.orElseThrow(() -> new BusinessException("没有此洗车费结算方式：" + unitExcelVO.getWashCar() + "。"));

					Level level = Optional.ofNullable(Level.get(unitExcelVO.getLevel()))
							.orElseThrow(() -> new BusinessException("没有此级别：" + unitExcelVO.getLevel() + "。"));

					Receive receive = Optional.ofNullable(Receive.get(unitExcelVO.getReceive()))
							.orElseThrow(() -> new BusinessException("没有此接车方式：" + unitExcelVO.getReceive() + "。"));

					Long customerCorpId = Optional.ofNullable(corpMap.get(unitExcelVO.getCustomerCorpName()))
							.orElseThrow(() -> new BusinessException("没有此归属客户：" + unitExcelVO.getCustomerCorpName() + "。"));

				/*	City accountCityId = Optional.ofNullable(cityMap.get(unitExcelVO.getAccountCityName()))
							.orElseThrow(() -> new BusinessException("没有此结算隶属点：" + unitExcelVO.getAccountCityName() + "。"));*/

					City provinceId = Optional.ofNullable(provinceMap.get(unitExcelVO.getProvinceName())).orElse(new City());

					City cityId = Optional.ofNullable(cityMap.get(unitExcelVO.getCityName()))
							.orElseThrow(() -> new BusinessException("没有此市：" + unitExcelVO.getCityName() + "。"));

					City areaId =  Optional.ofNullable(areaMap.get(unitExcelVO.getAreaName())).orElse(new City());;

			/*	City areaId = Optional.ofNullable(areaMap.get(unitExcelVO.getAreaName()))
						.orElseThrow(() ->  new BusinessException("没有此区：" + unitExcelVO.getAreaName() + "。"));*/
					//	验证省市区上下级关系
					if (provinceId.getCityId() != null && areaId.getCityId() != null){
						if (cityId.getUpCityId().longValue() != provinceId.getCityId().longValue()){
							if (cityId.getCityId().longValue() != areaId.getUpCityId().longValue()){
								throw new  BusinessException("该省市区上下级关系有误：省：" + unitExcelVO.getProvinceName() + "，市："+unitExcelVO.getCityName()+"，区："+unitExcelVO.getAreaName()+"。");
							}
						}
					}else if (provinceId.getCityId() != null && areaId.getCityId() == null){
						if (cityId.getUpCityId().longValue() != provinceId.getCityId().longValue()){
							throw new  BusinessException("该省市区上下级关系有误：省：" + unitExcelVO.getProvinceName() + "，市："+unitExcelVO.getCityName()+"，区："+unitExcelVO.getAreaName()+"。");
						}
					}else if (provinceId.getCityId() == null &&  areaId.getCityId() != null){
						if (cityId.getCityId().longValue() != areaId.getUpCityId().longValue()){
							throw new  BusinessException("该省市区上下级关系有误：省：" + unitExcelVO.getProvinceName() + "，市："+unitExcelVO.getCityName()+"，区："+unitExcelVO.getAreaName()+"。");
						}
					}
					// 接车点
					Unit unit = new Unit();
					unit.setUnitType(unitType.getIndex());
					unit.setSpecial(special.getIndex());
					unit.setDeliver(deliver.getIndex());
					unit.setWashCar(washCar.getIndex());
					unit.setLevel(level.getIndex());
					unit.setReceive(receive.getIndex());
					unit.setCustomerCorpId(customerCorpId);

					unit.setUnitName(unitExcelVO.getUnitName());
					unit.setEnName(unitExcelVO.getEnName());
//					unit.setAccountCityId(accountCityId.getCityId());
					unit.setUnitCityCode(unitExcelVO.getUnitCityCode());
//					unit.setAccountCityId(accountCityId.getCityId());
					unit.setProvinceId(provinceId.getCityId());
					unit.setCityId(cityId.getCityId());
					unit.setAreaId(areaId.getCityId());
					unit.setAddress(unitExcelVO.getAddress());
					unit.setTelephone(unitExcelVO.getTelephone());
					unit.setFax(unitExcelVO.getFax());
					unit.setLat(unitExcelVO.getLat());
					unit.setLng(unitExcelVO.getLng());
					unit.setCreator(env.getUser().getUserId());
					unit.setCreateTime(new Date());
					unit.setCorpId(env.getCorp().getCorpId());
					unit.setDealerCode(unitExcelVO.getDealerCode());
					unit.setFlag(FlagStatus.No.getIndex());
					//	添加收发货
					add(unit);
					if (unitExcelVO.getUnitStopAdress() != null){
						Integer continueTransport = Optional.ofNullable(ContinueTransport.getName(unitExcelVO.getContinueTransport()))
								.orElseThrow(() -> new BusinessException("没有此是否需要继续运输：" + unitExcelVO.getContinueTransport() + "。"));

						Integer smallCartPay = Optional.ofNullable(SmallCartPay.getName(unitExcelVO.getSmallCartPay()))
								.orElseThrow(() -> new BusinessException("没有此小板费支付方式：" + unitExcelVO.getSmallCartPay() + "。"));
						UnitStop unitStop = new UnitStop();
						if (unit.getUnitId() == null){
							if (unitMap.get(unit.getUnitName()) == null){
								throw new BusinessException("收发货单位档案中没有找到："+unit.getUnitName()+"收发货单位信息！");
							}
							Unit unit1 = unitMap.get(unit.getUnitName());
							unit.setUnitId(unit1.getUnitId());
						}
						unitStop.setUnitId(unit.getUnitId());
						unitStop.setContinueTransport(continueTransport);
						unitStop.setSmallCartPay(smallCartPay);

						unitStop.setUnitStopAdress(unitExcelVO.getUnitStopAdress());
						unitStop.setLat(unitExcelVO.getUnitStopLat());
						unitStop.setLng(unitExcelVO.getUnitStopLng());
						unitStop.setCreator(env.getUser().getUserId());
						unitStop.setCreateDate(new Date());
						unitStop.setCorpId(env.getCorp().getCorpId());
						unitStop.setFlag(FlagStatus.No.getIndex());
						//	添加接车点管理
						unitStopService.add(unitStop);
					}
					if (unitExcelVO.getContactsName() != null){
						Contacts contacts = new Contacts();
						if (unit.getUnitId() == null){
							if (unitMap.get(unit.getUnitName()) == null){
								throw new BusinessException("收发货单位档案中没有找到："+unit.getUnitName()+"收发货单位信息！");
							}
							Unit unit1 = unitMap.get(unit.getUnitName());
							unit.setUnitId(unit1.getUnitId());
						}
						contacts.setUnitId(unit.getUnitId());
						contacts.setContactsName(unitExcelVO.getContactsName());
						contacts.setPhone(unitExcelVO.getPhone());
						contacts.setFix(unitExcelVO.getFix());
						contacts.setQq(unitExcelVO.getQq());
						contacts.setEmail(unitExcelVO.getEmail());
						contacts.setWechat(unitExcelVO.getWechat());
						contacts.setCreator(env.getUser().getUserId());
						contacts.setCreateDate(new Date());
						contacts.setCorpId(env.getCorp().getCorpId());
						contacts.setFlag(FlagStatus.No.getIndex());
						//	添加收发货联系人
						contactsService.add(contacts);
					}
				}
			}

	} catch (Exception e) {
		if (e instanceof BusinessException) {
				throw e;
            } else {
                throw new RuntimeException("操作失败！", e);
            }
		}
	}

}
