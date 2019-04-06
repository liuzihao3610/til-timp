package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.TransporterMapper;
import com.tilchina.timp.model.Transporter;
import com.tilchina.timp.service.CorpService;
import com.tilchina.timp.service.TransporterService;
import com.tilchina.timp.service.UserService;
import com.tilchina.timp.service.VehicleLicenseService;
import com.tilchina.timp.util.CheckUtil;
import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * 轿运车档案
 *
 * @author Xiahong
 * @version 1.0.0
 */
@Service
@Slf4j
public class TransporterServiceImpl extends BaseServiceImpl<Transporter> implements TransporterService {

	@Autowired
	private TransporterMapper transportermapper;

	@Override
	protected BaseMapper<Transporter> getMapper() {
		return transportermapper;
	}

	@Autowired
	private CorpService corpService;

	@Autowired
	private UserService userService;

	@Autowired
	private VehicleLicenseService vehicleLicenseService;

	@Override
	protected StringBuilder checkNewRecord(Transporter transporter) {
		StringBuilder s = new StringBuilder();
		transporter.setTransporterCode(DateUtil.dateToStringCode(new Date()));
		transporter.setCreateDate(new Date());
		s.append(CheckUtils.checkString("NO", "transporterCode", "轿运车编号", transporter.getTransporterCode(), 20));
		s.append(CheckUtils.checkString("NO", "tractirPlateCode", "车头车牌号", transporter.getTractirPlateCode(), 10));
		s.append(CheckUtils.checkString("YES", "tractirVin", "车头车架号", transporter.getTractirVin(), 20));
		s.append(CheckUtils.checkString("YES", "engineCode", "发动机号", transporter.getEngineCode(), 20));
		s.append(CheckUtils.checkString("YES", "trailerPlateCode", "挂车车牌号", transporter.getTrailerPlateCode(), 20));
		s.append(CheckUtils.checkString("YES", "trailerVin", "挂车车架号", transporter.getTrailerVin(), 20));
		s.append(CheckUtils.checkDate("YES", "contractorEndDate", "承包到期时间", transporter.getContractorEndDate()));
		s.append(CheckUtils.checkInteger("NO", "billStatus", "状态:0=制单,1=审核", transporter.getBillStatus(), 10));
		s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", transporter.getCreateDate()));
		s.append(CheckUtils.checkDate("YES", "checkDate", "审核时间", transporter.getCheckDate()));
		s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", transporter.getFlag(), 10));
		s.append(CheckUtils.checkInteger("NO", "trucksType", "货车类型:0=重型货车,1=中型货车,2=轻型货车,3=微型货车。默认重型货车", transporter.getTrucksType(), 10));
		s.append(CheckUtils.checkInteger("NO", "axleCount", "0~6", transporter.getAxleCount(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(Transporter transporter) {
		StringBuilder s = checkNewRecord(transporter);
		s.append(CheckUtils.checkPrimaryKey(transporter.getTransporterId()));
		return s;
	}

	@Override
	public void add(Transporter record) {
		log.debug("add: {}", record);
		Environment env = Environment.getEnv();
		StringBuilder s;
		try {
			s = checkNewRecord(record);
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			if (!CheckUtil.tractorIsCarnumberNO(record.getTractirPlateCode())) {
				throw new BusinessException("9007", "车头车牌号码");
			}
			if (!StringUtil.isEmpty(record.getTrailerPlateCode())) {
				if (!CheckUtil.trailerIsCarnumberNO(record.getTrailerPlateCode())) {
					throw new BusinessException("9007", "挂车车牌号码");
				}
			}
			corpService.queryById(record.getCorpId());
			record.setCreator(env.getUser().getUserId());
			getMapper().insertSelective(record);
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新保存！", e);
			}
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public Transporter queryByContractorId(Long contractorId) {
		log.debug("queryByContractorId: {}", contractorId);
		StringBuilder s;
		Transporter transporter = null;
		try {
			s = new StringBuilder();
			s.append(CheckUtils.checkLong("NO", "contractorId", "承包人ID", contractorId, 20));
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			transporter = transportermapper.selectByContractorId(contractorId);
		} catch (Exception e) {
			if (e instanceof BusinessException) {
				throw e;
			} else {
				throw new RuntimeException("操作失败！", e);
			}
		}
		return transporter;
	}

	@Override
	public void updateContractorById(Map<String, Object> params) {
		try {
			transportermapper.updateContractorById(params);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public Transporter queryByDriverId(Long driverId) {
		log.debug("queryByDriverId: {}", driverId);
		StringBuilder s;
		Transporter transporter = null;
		try {
			s = new StringBuilder();
			s.append(CheckUtils.checkLong("NO", "data", "司机ID", driverId, 20));
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			transporter = transportermapper.selectByDriverId(driverId);
		} catch (Exception e) {
			if (e instanceof BusinessException) {
				throw e;
			} else {
				throw new RuntimeException("操作失败！", e);
			}
		}
		return transporter;
	}

	@Override
	public void deleteById(Long id) {
		log.debug("delete: {}", id);
		StringBuilder s;
		try {
			s = new StringBuilder();
			s.append(CheckUtils.checkLong("NO", "data", "轿运车ID", id, 20));
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			queryById(id);
			getMapper().deleteByPrimaryKey(id);
		} catch (Exception e) {
			if (e instanceof BusinessException) {
				throw e;
			} else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}

	@Override
	public void deleteByIdList(int[] ids) {
		log.debug("logicDeleteByIdList: {}", ids);
		StringBuilder s;
		try {
			if (ids.length > 0) {
				s = new StringBuilder();
				for (int id : ids) {
					s.append(CheckUtils.checkInteger("NO", "data", "轿运车ID", id, 20));
					if (!StringUtils.isBlank(s)) {
						throw new BusinessException("数据检查失败：" + s.toString());
					}
					queryById(Long.valueOf(id));
				}
				transportermapper.deleteByIdList(ids);
			} else {
				throw new BusinessException("9001", "轿运车ID");
			}

		} catch (Exception e) {
			if (e instanceof BusinessException) {
				throw e;
			} else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}


	@Override
	public void logicDeleteByIdList(int[] ids) {
		log.debug("logicDeleteByIdList: {}", ids);
		StringBuilder s;
		try {
			if (ids.length > 0) {
				s = new StringBuilder();
				for (int id : ids) {
					s.append(CheckUtils.checkInteger("NO", "data", "轿运车ID", id, 20));
					if (!StringUtils.isBlank(s)) {
						throw new BusinessException("数据检查失败：" + s.toString());
					}
					queryById(Long.valueOf(id));
				}
				transportermapper.logicDeleteByPrimaryKeyList(ids);
			} else {
				throw new BusinessException("9001", "轿运车ID");
			}

		} catch (Exception e) {
			if (e instanceof BusinessException) {
				throw e;
			} else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}

	@Override
	public void logicDeleteById(Long id) {
		log.debug("logicDeleteById: {}", id);
		StringBuilder s;
		try {
			s = new StringBuilder();
			s.append(CheckUtils.checkLong("NO", "data", "轿运车ID", id, 20));
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			queryById(id);
			transportermapper.logicDeleteByPrimaryKey(id);
		} catch (Exception e) {
			if (e instanceof BusinessException) {
				throw e;
			} else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}

	@Override
	public Transporter queryById(Long id) {
		log.debug("query: {}", id);
		Transporter transporter = null;
		StringBuilder s;
		try {
			s = new StringBuilder();
			s.append(CheckUtils.checkLong("NO", "data", "轿运车ID", id, 20));
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			transporter = getMapper().selectByPrimaryKey(id);
			if (transporter == null) {
				throw new BusinessException("9008", "轿运车ID");
			}
			if (transporter.getAxleCount() == null) {
				transporter.setAxleCount(0);
			}
			if (transporter.getTrucksHigh() == null) {
				transporter.setTrucksHigh(0.00);
			}
			if (transporter.getTrucksWidth() == null) {
				transporter.setTrucksWidth(0.00);
			}
			if (transporter.getTrucksLong() == null) {
				transporter.setTrucksLong(0.00);
			}
			if (transporter.getCheckLoadweight() == null) {
				transporter.setCheckLoadweight(0.00);
			}
			if (transporter.getWeightCount() == null) {
				transporter.setWeightCount(0.00);
			}
			if (transporter.getTrucksType() == null) {
				transporter.setTrucksType(0);
			}
			if (transporter.getAppTractirPlateProvince() == null) {
				transporter.setAppTractirPlateProvince("");
			}
			if (transporter.getAppTractirPlateCode() == null) {
				transporter.setAppTractirPlateCode("");
			}
		} catch (Exception e) {
			if (e instanceof BusinessException) {
				throw e;
			} else {
				throw new RuntimeException("操作失败！", e);
			}
		}
		return transporter;
	}

	@Override
	public void updateCheck(Transporter updateTransporter) {
		StringBuilder s;
		Environment env = Environment.getEnv();
		Transporter transporter = null;
		try {
			s = new StringBuilder();
			s.append(CheckUtils.checkLong("NO", "transporterId", "轿运车Id", updateTransporter.getTransporterId(), 20));
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			transporter = queryById(updateTransporter.getTransporterId());
			if (!vehicleLicenseService.checkLicenseStatusByVehicleId(transporter.getTransporterId())) {
				throw new RuntimeException("该轿运车证件不全，请维护当前轿运车证件档案，需维护行驶证，营运证，保单信息。");
			}
			if (transporter.getBillStatus().intValue() == 1 && updateTransporter.getBillStatus().intValue() == transporter.getBillStatus().intValue()) {
				throw new BusinessException("该轿运车档案已审核通过,请勿重复审核！");
			} else if (updateTransporter.getBillStatus() > 1) {
				throw new BusinessException("传入状态有误，请联系管理员！");
			} else if (transporter.getBillStatus().intValue() == 0 && updateTransporter.getBillStatus().intValue() == transporter.getBillStatus().intValue()) {
				throw new BusinessException("该轿运车档案未通过审核,不需要取消审核！");
			} else if (updateTransporter.getBillStatus().intValue() == 0) {
				updateTransporter.setChecker(null);
				updateTransporter.setCheckDate(null);
			} else if (updateTransporter.getBillStatus().intValue() == 1) {
				updateTransporter.setChecker(env.getUser().getUserId());
				updateTransporter.setCheckDate(new Date());
			}
			transportermapper.updateCheck(updateTransporter);
		} catch (Exception e) {
			log.error("{}", e);
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新保存！", e);
			}
			throw e;
		}
	}

	@Override
	public void updateSelective(Transporter record) {
		log.debug("updateSelective: {}", record);
		StringBuilder s;
		try {
			s = new StringBuilder();
			s.append(CheckUtils.checkLong("NO", "transporterId", "轿运车Id", record.getTransporterId(), 20));
			if (record.getTractirPlateCode() != null) {
				if (!CheckUtil.tractorIsCarnumberNO(record.getTractirPlateCode())) {
					throw new BusinessException("9007", "车头车牌号码");
				}
			}
			if (!StringUtil.isEmpty(record.getTrailerPlateCode())) {
				if (!CheckUtil.trailerIsCarnumberNO(record.getTrailerPlateCode())) {
					throw new BusinessException("9007", "挂车车牌号码");
				}
			}
			queryById(record.getTransporterId());
			getMapper().updateByPrimaryKeySelective(record);
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
	public PageInfo<Transporter> getRefer(Map<String, Object> data, int pageNum, int pageSize) {
		log.debug("getRefer: {}, page: {},{}", data, pageNum, pageSize);
		PageHelper.startPage(pageNum, pageSize);
		PageInfo<Transporter> transporters = null;
		try {
			transporters = new PageInfo<Transporter>(transportermapper.selectRefer(data));
		} catch (Exception e) {
			if (e instanceof BusinessException) {
				throw e;
			} else {
				throw new RuntimeException("操作失败！", e);
			}
		}
		return transporters;
	}

	@Override
	public PageInfo<Transporter> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map, pageNum, pageSize);
		PageHelper.startPage(pageNum, pageSize);
		PageInfo<Transporter> transporters = null;
		try {
			Map<String, Object> data = Optional.ofNullable(map).orElse(new HashMap<>());
			if (data.get("endTime") != null && !"".equals(data.get("endTime"))) {
				data.put("endTime", DateUtil.dateToString(DateUtil.endTime(data.get("endTime").toString())));
			}
			transporters = new PageInfo<Transporter>(transportermapper.selectList(data));
		} catch (Exception e) {
			log.error("{}", e);
			throw new RuntimeException("操作失败！", e);
		}
		return transporters;
	}

}