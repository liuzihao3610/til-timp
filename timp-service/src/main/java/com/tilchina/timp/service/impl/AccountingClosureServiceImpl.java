package com.tilchina.timp.service.impl;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.catalyst.utils.DateUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.AccountingClosureMapper;
import com.tilchina.timp.model.AccountingClosure;
import com.tilchina.timp.model.StockCar;
import com.tilchina.timp.service.AccountingClosureService;
import com.tilchina.timp.service.StockCarService;
import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
* 关账
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class AccountingClosureServiceImpl extends BaseServiceImpl<AccountingClosure> implements AccountingClosureService {

	@Autowired
    private AccountingClosureMapper accountingClosureMapper;

	@Autowired
	private StockCarService stockCarService;
	
	@Override
	protected BaseMapper<AccountingClosure> getMapper() {
		return accountingClosureMapper;
	}

	@Override
	protected StringBuilder checkNewRecord(AccountingClosure accountingclosure) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("YES", "closureNumber", "关账单号", accountingclosure.getClosureNumber(), 20));
        s.append(CheckUtils.checkDate("YES", "accountPeriod", "账期", accountingclosure.getAccountPeriod()));
        s.append(CheckUtils.checkInteger("YES", "vinCount", "车架号数量", accountingclosure.getVinCount(), 10));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", accountingclosure.getCreateDate()));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(AccountingClosure accountingclosure) {
        StringBuilder s = checkNewRecord(accountingclosure);
        s.append(CheckUtils.checkPrimaryKey(accountingclosure.getClosureId()));
		return s;
	}

	@Override
	public AccountingClosure queryById(Long id) {
		return super.queryById(id);
	}

	@Transactional
	public void generateRecord(Date date) {

		try {
			List<String> carVins = accountingClosureMapper.selectCarVinByDate(date);

			Environment env = Environment.getEnv();
			AccountingClosure accountingClosure = new AccountingClosure();
			accountingClosure.setClosureNumber(String.format("CQ%s", DateUtil.dateToStringCode(new Date())));
			accountingClosure.setAccountPeriod(date);
			accountingClosure.setVinCount(carVins.size());
			accountingClosure.setCreator(env.getUser().getUserId());
			accountingClosure.setCreateDate(new Date());
			accountingClosure.setCorpId(env.getUser().getCorpId());

			StringBuilder sb = checkNewRecord(accountingClosure);
			if (carVins.size() < 1) {
				sb.append(String.format("%s前已无未关账车辆", DateUtils.format(date, "yyyy-MM-dd")));
			}
			if (!StringUtils.isBlank(sb)) {
				throw new BusinessException("数据检查失败：" + sb.toString());
			}
			accountingClosureMapper.insertSelective(accountingClosure);

			carVins.forEach(carVin -> {
				StockCar stockCar = stockCarService.queryByCarVin(carVin);
				if (stockCar != null) {
					stockCar.setShutBillId(accountingClosure.getClosureId());
					stockCar.setShutBillStatus(1);
					stockCar.setShutBillDate(accountingClosure.getCreateDate());
					stockCarService.updateSelective(stockCar);
				}
			});
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
}
