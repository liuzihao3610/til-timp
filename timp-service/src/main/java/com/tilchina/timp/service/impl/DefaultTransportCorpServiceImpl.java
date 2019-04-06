package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.DefaultTransportCorpMapper;
import com.tilchina.timp.model.Corp;
import com.tilchina.timp.model.DefaultTransportCorp;
import com.tilchina.timp.model.DefaultTransportCorpDetail;
import com.tilchina.timp.service.CityService;
import com.tilchina.timp.service.CorpService;
import com.tilchina.timp.service.DefaultTransportCorpDetailService;
import com.tilchina.timp.service.DefaultTransportCorpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* 默认运输公司表
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class DefaultTransportCorpServiceImpl extends BaseServiceImpl<DefaultTransportCorp> implements DefaultTransportCorpService {

	@Autowired
    private DefaultTransportCorpMapper defaultTransportCorpMapper;

	@Autowired
	private CorpService corpService;

	@Autowired
	private CityService cityService;

	@Autowired
	private DefaultTransportCorpDetailService detailService;
	
	@Override
	protected BaseMapper<DefaultTransportCorp> getMapper() {
		return defaultTransportCorpMapper;
	}

	@Override
	protected StringBuilder checkNewRecord(DefaultTransportCorp defaulttransportcorp) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkInteger("NO", "isUniversal", "是否通用", defaulttransportcorp.getIsUniversal(), 10));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", defaulttransportcorp.getCreateDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", defaulttransportcorp.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(DefaultTransportCorp defaulttransportcorp) {
        StringBuilder s = checkNewRecord(defaulttransportcorp);
        s.append(CheckUtils.checkPrimaryKey(defaulttransportcorp.getDefaultCorpId()));
		return s;
	}

	@Override
	public DefaultTransportCorp queryById(Long id) {
		return super.queryById(id);
	}

	@Override
	public PageInfo<DefaultTransportCorp> queryList(Map<String, Object> map, int pageNum, int pageSize) {

		try {
			List<DefaultTransportCorp> defaultTransportCorps = defaultTransportCorpMapper.selectList(map);
			defaultTransportCorps.forEach(defaultTransportCorp -> {
				List<DefaultTransportCorpDetail> defaultTransportCorpDetails = detailService.selectByDefaultCorpId(defaultTransportCorp.getDefaultCorpId());
				defaultTransportCorp.setDetails(defaultTransportCorpDetails);
			});

			PageHelper.startPage(pageNum, pageSize);
			return new PageInfo(defaultTransportCorps);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public List<DefaultTransportCorp> queryList(Map<String, Object> map) {

		try {
			List<DefaultTransportCorp> defaultTransportCorps = defaultTransportCorpMapper.selectList(map);
			defaultTransportCorps.forEach(defaultTransportCorp -> {
				List<DefaultTransportCorpDetail> defaultTransportCorpDetails = detailService.selectByDefaultCorpId(defaultTransportCorp.getDefaultCorpId());
				defaultTransportCorp.setDetails(defaultTransportCorpDetails);
			});

			return defaultTransportCorps;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Transactional
	@Override
	public void add(DefaultTransportCorp defaultTransportCorp) {

		try {
			StringBuilder sb;
			if (corpService.queryById(defaultTransportCorp.getAffiliatedCorpId()) == null) {
				throw new BusinessException("9008", "所属公司");
			}
			if (defaultTransportCorp.getRecvCityId() != 0 && defaultTransportCorp.getRecvCityId() != null && cityService.queryById(defaultTransportCorp.getRecvCityId()) == null) {
				throw new BusinessException("9008", "收货城市");
			}
			if (defaultTransportCorp.getSendCityId() != 0 && defaultTransportCorp.getSendCityId() != null && cityService.queryById(defaultTransportCorp.getSendCityId()) == null) {
				throw new BusinessException("9008", "发货城市");
			}

			Environment env = Environment.getEnv();
			defaultTransportCorp.setCreator(env.getUser().getUserId());
			defaultTransportCorp.setCorpId(env.getUser().getCorpId());
			defaultTransportCorp.setCreateDate(new Date());

			sb = checkNewRecord(defaultTransportCorp);
			if (!StringUtils.isBlank(sb)) {
				throw new RuntimeException("数据检查失败：" + sb.toString());
			}
			defaultTransportCorpMapper.insertSelective(defaultTransportCorp);

			List<DefaultTransportCorpDetail> details = defaultTransportCorp.getDetails();
			if (details != null && details.size() > 0) {
				// 只有一条记录时，中转单位设置为NULL
				if (details.size() == 1) {
					DefaultTransportCorpDetail detail = details.stream().findFirst().get();
					if (detail.getSequenceNumber() != 1) {
						throw new RuntimeException("序号应从数字1开始编号，请重新输入。");
					}
					detail.setDefaultCorpId(defaultTransportCorp.getDefaultCorpId());
					detail.setTransferUnitId(null);
					detailService.add(detail);
				} else {

					Comparator<DefaultTransportCorpDetail> comparator = Comparator.comparing(DefaultTransportCorpDetail::getSequenceNumber);
					DefaultTransportCorpDetail minDetail = details.stream().min(comparator).get();

					details = details.stream().sorted(comparator).collect(Collectors.toList());
					for (int i = 0; i < details.size(); i++) {
						if (i == 0) {
							if (minDetail.getSequenceNumber() != 1) {
								throw new RuntimeException("序号应从数字1开始编号，请重新输入。");
							}
						}
						if (i > 0) {
							// 上一个元素
							Integer prev = details.get(i - 1).getSequenceNumber();
							// 当前元素
							Integer curr = details.get(i).getSequenceNumber();
							if (curr != prev + 1) {
								throw new RuntimeException("序号应连续编号，请重新输入。");
							}
						}
					}
					details.forEach(detail -> {
						detail.setDefaultCorpId(defaultTransportCorp.getDefaultCorpId());
						detailService.add(detail);
					});
				}
			}
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void add(List<DefaultTransportCorp> records) {
		super.add(records);
	}

	@Override
	public void updateSelective(DefaultTransportCorp defaultTransportCorp) {

		try {
			StringBuilder sb = new StringBuilder();

			Corp corp = corpService.queryById(defaultTransportCorp.getAffiliatedCorpId());
			if (corp == null) {
				throw new BusinessException("9008", "所属公司");
			}
			if (defaultTransportCorp.getRecvCityId() != null && defaultTransportCorp.getRecvCityId() != 0 &&  cityService.queryById(defaultTransportCorp.getRecvCityId()) == null) {
				throw new BusinessException("9008", "收货城市");
			}
			if (defaultTransportCorp.getSendCityId() != null && defaultTransportCorp.getSendCityId() != 0 && cityService.queryById(defaultTransportCorp.getSendCityId()) == null) {
				throw new BusinessException("9008", "发货城市");
			}
			sb.append(CheckUtils.checkLong("NO", "defaultTransportCorpId", "默认运输公司ID", defaultTransportCorp.getDefaultCorpId(), 20));
			if (!StringUtils.isBlank(sb)) {
				throw new RuntimeException("数据检查失败：" + sb.toString());
			}

			List<DefaultTransportCorpDetail> details = defaultTransportCorp.getDetails();

			// 筛选出需要新增的记录
			List<DefaultTransportCorpDetail> newRecords = details.stream().filter(detail -> detail.getRowStatus() == 1).collect(Collectors.toList());

			// 筛选出需要更新的记录
			List<DefaultTransportCorpDetail> updateRecords = details.stream().filter(detail -> detail.getRowStatus() == 2).collect(Collectors.toList());

			// 筛选出需要删除的记录
			List<DefaultTransportCorpDetail> deleteRecords = details.stream().filter(detail -> detail.getRowStatus() == 3).collect(Collectors.toList());

			if (updateRecords != null && updateRecords.size() > 0) {
				updateRecords.forEach(updateRecord -> detailService.updateSelective(updateRecord));
			}
			if (deleteRecords != null && deleteRecords.size() > 0) {
				deleteRecords.forEach(deleteRecord -> detailService.deleteById(deleteRecord.getDefaultCorpDetailId()));
			}
			if (newRecords != null && newRecords.size() > 0) {
				newRecords.forEach(newRecord -> {
					newRecord.setDefaultCorpId(defaultTransportCorp.getDefaultCorpId());
					detailService.add(newRecord);
				});
			}
			defaultTransportCorpMapper.updateByPrimaryKeySelective(defaultTransportCorp);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void update(DefaultTransportCorp record) {
		super.update(record);
	}

	@Override
	public void update(List<DefaultTransportCorp> records) {
		super.update(records);
	}

	@Override
	public void deleteById(Long defaultCorpId) {

		try {
			defaultTransportCorpMapper.deleteByPrimaryKey(defaultCorpId);
			List<DefaultTransportCorpDetail> details = detailService.selectByDefaultCorpId(defaultCorpId);
			details.forEach(detail -> detailService.deleteById(detail.getDefaultCorpDetailId()));
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public PageInfo<List<Map<Long, String>>> getReferenceList(Long defaultCorpId, int pageNum, int pageSize) {

		try {
			List<Map<Long, String>> referenceList = defaultTransportCorpMapper.getReferenceList(defaultCorpId);
			PageHelper.startPage(pageNum, pageSize);
			return new PageInfo(referenceList);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}
}
