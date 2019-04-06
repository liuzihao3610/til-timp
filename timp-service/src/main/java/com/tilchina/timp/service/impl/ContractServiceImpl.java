package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.BillStatus;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.ContractMapper;
import com.tilchina.timp.model.Contract;
import com.tilchina.timp.service.CityService;
import com.tilchina.timp.service.ContractService;
import com.tilchina.timp.service.CorpService;
import com.tilchina.timp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
* 合同管理表
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class ContractServiceImpl extends BaseServiceImpl<Contract> implements ContractService {

	@Autowired
    private ContractMapper contractMapper;

	@Autowired
    private CorpService corpService;

	@Autowired
    private UserService userService;

	@Autowired
    private CityService cityService;
	
	@Override
	protected BaseMapper<Contract> getMapper() {
		return contractMapper;
	}

	@Override
	protected StringBuilder checkNewRecord(Contract contract) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "contractNumber", "合同编号", contract.getContractNumber(), 50));
        s.append(CheckUtils.checkInteger("NO", "contractType", "合同类型(0:正式", contract.getContractType(), 10));
        s.append(CheckUtils.checkDate("YES", "signingDate", "签订日期", contract.getSigningDate()));
        s.append(CheckUtils.checkDate("NO", "contractStartDate", "合同有效期起", contract.getContractStartDate()));
        s.append(CheckUtils.checkDate("NO", "contractEndDate", "合同有效期止", contract.getContractEndDate()));
        s.append(CheckUtils.checkString("YES", "contractAttachment", "合同附件", contract.getContractAttachment(), 100));
        s.append(CheckUtils.checkInteger("YES", "billStatus", "单据状态(0:制单", contract.getBillStatus(), 10));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", contract.getCreateDate()));
        s.append(CheckUtils.checkDate("YES", "checkDate", "审核时间", contract.getCheckDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志(0:否", contract.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(Contract contract) {
        StringBuilder s = checkNewRecord(contract);
        s.append(CheckUtils.checkPrimaryKey(contract.getContractId()));
		return s;
	}

    @Override
    public Contract queryById(Long id) {
        return super.queryById(id);
    }

    @Override
    public PageInfo<Contract> queryList(Map<String, Object> map, int pageNum, int pageSize) {

        try {
            Environment env = Environment.getEnv();

            if (map == null) { map = new HashMap<>(); }

            // 当前登陆用户只能看到甲方或者乙方为用户所属公司的合同
            map.put("party", env.getUser().getCorpId());

            PageHelper.startPage(pageNum, pageSize);
            List<Contract> contracts = contractMapper.selectList(map);

            return new PageInfo(contracts);
        } catch (Exception e) {
            log.error("{}", e);
            throw e;
        }
    }

    @Override
    public List<Contract> queryList(Map<String, Object> map) {

        try {
            Environment environment = Environment.getEnv();
            List<Contract> contracts = contractMapper.selectList(map);
            if (CollectionUtils.isNotEmpty(contracts)) {

                // 筛选当前登陆用户所属公司为甲方或者乙方的合同列表
                contracts = contracts
                        .stream()
                        .filter(contract ->
                                contract.getPartya().equals(environment.getUser().getCorpId()) ||
                                        contract.getPartyb().equals(environment.getUser().getCorpId()))
                        .collect(Collectors.toList());
            }

            return contracts;
        } catch (Exception e) {
            log.error("{}", e);
            throw e;
        }
    }

    @Override
    public void add(Contract contract) {

	    StringBuilder sb;
        try {
            if (corpService.queryById(contract.getPartya()) == null) {
                throw new BusinessException("9008", "甲方");
            }
            if (corpService.queryById(contract.getPartyb()) == null) {
                throw new BusinessException("9008", "乙方");
            }
            if (userService.queryById(contract.getPartyaManager()) == null) {
                throw new BusinessException("9008", "甲方经办人");
            }
            if (userService.queryById(contract.getPartybManager()) == null) {
                throw new BusinessException("9008", "乙方经办人");
            }
            if (cityService.queryById(contract.getSigningPlace()) == null) {
                throw new BusinessException("9008", "签订地点");
            }

            Environment env = Environment.getEnv();
            contract.setCreator(env.getUser().getUserId());
            contract.setCreateDate(new Date());
            contract.setCorpId(env.getUser().getCorpId());
            sb = checkNewRecord(contract);
            if (!StringUtils.isBlank(sb)) {
                throw new RuntimeException("数据检查失败：" + sb.toString());
            }
            contractMapper.insertSelective(contract);
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
    public void add(List<Contract> records) {
        super.add(records);
    }

    @Override
    public void updateSelective(Contract contract) {

	    StringBuilder sb = new StringBuilder();
        try {
            if (contract.getBillStatus() == BillStatus.Invalid.getIndex()) {
                throw new RuntimeException("当前合同已作废，无法进行任何操作。");
            }
            if (corpService.queryById(contract.getPartya()) == null) {
                throw new BusinessException("9008", "甲方");
            }
            if (corpService.queryById(contract.getPartyb()) == null) {
                throw new BusinessException("9008", "乙方");
            }
            if (userService.queryById(contract.getPartyaManager()) == null) {
                throw new BusinessException("9008", "甲方经办人");
            }
            if (userService.queryById(contract.getPartybManager()) == null) {
                throw new BusinessException("9008", "乙方经办人");
            }
            if (cityService.queryById(contract.getSigningPlace()) == null) {
                throw new BusinessException("9008", "签订地点");
            }

            if (!contract.getPartya().equals(userService.queryById(contract.getPartyaManager()).getCorpId())) {
                throw new BusinessException("甲方经办人不属于甲方公司，请检查后重试。");
            }
            if (!contract.getPartyb().equals(userService.queryById(contract.getPartybManager()).getCorpId())) {
                throw new BusinessException("乙方经办人不属于乙方公司，请检查后重试。");
            }

            sb.append(CheckUtils.checkLong("NO", "contractId", "合同ID", contract.getContractId(), 20));
            if (!StringUtils.isBlank(sb)) {
                throw new RuntimeException("数据检查失败：" + sb.toString());
            }
            contractMapper.updateByPrimaryKeySelective(contract);
        } catch (Exception e) {
            log.error("{}", e);
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
    public void update(Contract record) {
        super.update(record);
    }

    @Override
    public void update(List<Contract> records) {
        super.update(records);
    }

    @Override
    public void deleteById(Long id) {
        Contract contract = Optional.ofNullable(queryById(id)).orElseThrow(() -> new RuntimeException("未查询到该记录"));
        if (contract.getBillStatus() == BillStatus.Audited.getIndex()) {
            throw new RuntimeException("当前合同已审核，无法进行删除操作。");
        }
        if (contract.getBillStatus() == BillStatus.Invalid.getIndex()) {
            throw new RuntimeException("当前合同已作废，无法进行任何操作。");
        }
        super.deleteById(id);
    }

    @Override
    public PageInfo<Map<String, String>> getReferenceList(Map<String, Object> map,int pageNum, int pageSize) {

        try {
            PageHelper.startPage(pageNum, pageSize);
            List<Map<String, String>> referenceList = contractMapper.getReferenceList(map);
            return new PageInfo<>(referenceList);
        } catch (Exception e) {
            log.error("{}", e);
            throw e;
        }
    }

    @Override
    public void setDocumentsChecked(Contract contract) {

        try {
            contract = Optional.ofNullable(queryById(contract.getContractId())).orElseThrow(() -> new RuntimeException("未查询到该记录"));
            if (contract.getBillStatus() == BillStatus.Audited.getIndex()) {
                throw new RuntimeException("当前合同已审核，无法进行审核操作。");
            }
            if (contract.getBillStatus() == BillStatus.Invalid.getIndex()) {
                throw new RuntimeException("当前合同已作废，无法进行审核操作。");
            }

            Environment env = Environment.getEnv();
            Contract model = new Contract();
            model.setContractId(contract.getContractId());
            model.setChecker(env.getUser().getUserId());
            model.setCheckDate(new Date());
            model.setBillStatus(BillStatus.Audited.getIndex());
            contractMapper.setCheckState(model);
        } catch (Exception e) {
            log.error("{}", e);
            throw e;
        }
    }

    @Override
    public void setDocumentsUnchecked(Contract contract) {
        try {
            contract = Optional.ofNullable(queryById(contract.getContractId())).orElseThrow(() -> new RuntimeException("未查询到该记录"));
            if (contract.getBillStatus() == BillStatus.Drafted.getIndex()) {
                throw new RuntimeException("当前合同未审核，无法进行取消审核操作。");
            }
            if (contract.getBillStatus() == BillStatus.Invalid.getIndex()) {
                throw new RuntimeException("当前合同已作废，无法进行取消审核操作。");
            }

            Contract model = new Contract();
            model.setContractId(contract.getContractId());
            model.setChecker(null);
            model.setCheckDate(null);
            model.setBillStatus(BillStatus.Drafted.getIndex());
            contractMapper.setCheckState(model);
        } catch (Exception e) {
            log.error("{}", e);
            throw e;
        }
    }

    @Override
    public void setDocumentsInvalid(Contract contract) {
        try {
            contract = Optional.ofNullable(queryById(contract.getContractId())).orElseThrow(() -> new RuntimeException("未查询到该记录"));
            if (contract.getBillStatus() == BillStatus.Invalid.getIndex()) {
                throw new RuntimeException("当前合同已作废，无法进行作废操作。");
            }

            Contract model = new Contract();
            model.setContractId(contract.getContractId());
            model.setBillStatus(BillStatus.Invalid.getIndex());
            contractMapper.setCheckState(model);
        } catch (Exception e) {
            log.error("{}", e);
            throw e;
        }
    }
}
