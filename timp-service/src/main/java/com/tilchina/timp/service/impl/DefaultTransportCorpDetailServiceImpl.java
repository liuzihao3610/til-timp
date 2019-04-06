package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.DefaultTransportCorpDetailMapper;
import com.tilchina.timp.model.Corp;
import com.tilchina.timp.model.DefaultTransportCorpDetail;
import com.tilchina.timp.model.Unit;
import com.tilchina.timp.service.CorpService;
import com.tilchina.timp.service.DefaultTransportCorpDetailService;
import com.tilchina.timp.service.UnitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
* 默认运输公司明细表
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class DefaultTransportCorpDetailServiceImpl extends BaseServiceImpl<DefaultTransportCorpDetail> implements DefaultTransportCorpDetailService {

	@Autowired
    private DefaultTransportCorpDetailMapper defaultTransportCorpDetailMapper;

	@Autowired
	private CorpService corpService;

	@Autowired
	private UnitService unitService;

	@Override
	protected BaseMapper<DefaultTransportCorpDetail> getMapper() {
		return defaultTransportCorpDetailMapper;
	}

	@Override
	protected StringBuilder checkNewRecord(DefaultTransportCorpDetail defaulttransportcorpdetail) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkInteger("NO", "sequenceNumber", "序号", defaulttransportcorpdetail.getSequenceNumber(), 10));
        s.append(CheckUtils.checkInteger("NO", "jobType", "作业类型", defaulttransportcorpdetail.getJobType(), 10));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", defaulttransportcorpdetail.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(DefaultTransportCorpDetail defaulttransportcorpdetail) {
        StringBuilder s = checkNewRecord(defaulttransportcorpdetail);
        s.append(CheckUtils.checkPrimaryKey(defaulttransportcorpdetail.getDefaultCorpDetailId()));
		return s;
	}

	@Override
	public DefaultTransportCorpDetail queryById(Long id) {
		return super.queryById(id);
	}

	@Override
	public PageInfo<DefaultTransportCorpDetail> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		return super.queryList(map, pageNum, pageSize);
	}

	@Override
	public List<DefaultTransportCorpDetail> queryList(Map<String, Object> map) {
		return super.queryList(map);
	}

	@Override
	public void add(DefaultTransportCorpDetail defaultTransportCorpDetail) {

		try {
			StringBuilder sb = new StringBuilder();

			Corp corp = corpService.queryById(defaultTransportCorpDetail.getTransportCorpId());
			if (corp == null) {
				throw new BusinessException("9008", "运输公司");
			}

			List<DefaultTransportCorpDetail> details = selectByDefaultCorpId(defaultTransportCorpDetail.getDefaultCorpId());
			if (details == null || details.size() == 0) {
				if (defaultTransportCorpDetail.getSequenceNumber() != 1) {
					throw new RuntimeException("序号应从数字1开始编号，请重新输入。");
				}
			} else {

				Optional<DefaultTransportCorpDetail> any = details.stream().filter(detail -> detail.getSequenceNumber() == defaultTransportCorpDetail.getSequenceNumber()).findAny();
				if (any.isPresent()) {
					DefaultTransportCorpDetail updateDetail = any.get();
					if (defaultTransportCorpDetail.getDefaultCorpDetailId() != updateDetail.getDefaultCorpDetailId()) {
						throw new RuntimeException(String.format("序号：%d已存在，请重新输入。", defaultTransportCorpDetail.getSequenceNumber()));
					}
				}

				// 根据对象属性返回某一属性值最大的对象
				// https://howtodoinjava.com/java-8/stream-max-min-examples/
				Comparator<DefaultTransportCorpDetail> comparator = Comparator.comparing(DefaultTransportCorpDetail::getSequenceNumber);
				DefaultTransportCorpDetail maxDetail = details.stream().max(comparator).get();

				if (defaultTransportCorpDetail.getSequenceNumber() != maxDetail.getSequenceNumber() + 1) {
					throw new RuntimeException("序号应连续编号，请重新输入。");
				}

				if (defaultTransportCorpDetail.getTransferUnitId() != null &&
						defaultTransportCorpDetail.getTransferUnitId() != 0) {

					Unit unit = unitService.queryById(defaultTransportCorpDetail.getTransferUnitId());
					if (unit == null) {
						throw new BusinessException("9008", "中转单位");
					}
// TODO XYS - 隶属公司字段作废后代码逻辑需要更新
//					if (!corp.getCorpId().equals(unit.getSuperorCorpId())) {
//						throw new RuntimeException(String.format("%s非当前选择%s下所属单位，请重新选择。", unit.getUnitName(), corp.getCorpName()));
//					}
				}

				// 检查是否存在中转单位为空的记录
				List<DefaultTransportCorpDetail> nullDetails = details.stream().filter(detail -> detail.getTransferUnitId() == null).collect(Collectors.toList());
				if (nullDetails != null && nullDetails.size() > 0) {
					sb.append("序号：");
					for (int i = 0; i < nullDetails.size(); i++) {
						if (i == nullDetails.size() - 1) {
							sb.append(String.format("%d", nullDetails.get(i).getSequenceNumber()));
						} else {
							sb.append(String.format("%d, ", nullDetails.get(i).getSequenceNumber()));
						}
					}
					sb.append("的中转单位为空，请修改后重试。");
					if (!StringUtils.isBlank(sb.toString())) {
						throw new RuntimeException(sb.toString());
					}
				}
			}

			Environment env = Environment.getEnv();
			defaultTransportCorpDetail.setCorpId(env.getUser().getCorpId());

			sb = checkNewRecord(defaultTransportCorpDetail);
			if (!StringUtils.isBlank(sb)) {
				throw new RuntimeException("数据检查失败：" + sb.toString());
			}
			defaultTransportCorpDetailMapper.insertSelective(defaultTransportCorpDetail);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void add(List<DefaultTransportCorpDetail> records) {
		super.add(records);
	}

	@Override
	public void updateSelective(DefaultTransportCorpDetail defaultTransportCorpDetail) {

		try {
			StringBuilder sb = new StringBuilder();

			Corp corp = corpService.queryById(defaultTransportCorpDetail.getTransportCorpId());
			if (corp == null) {
				throw new BusinessException("9008", "运输公司");
			}

			List<DefaultTransportCorpDetail> details = selectByDefaultCorpId(defaultTransportCorpDetail.getDefaultCorpId());
			if (details == null || details.size() == 0) {
				if (defaultTransportCorpDetail.getSequenceNumber() != 1) {
					throw new RuntimeException("序号应从数字1开始编号，请重新输入。");
				}
			} else {

				Optional<DefaultTransportCorpDetail> any = details.stream().filter(detail -> detail.getSequenceNumber() == defaultTransportCorpDetail.getSequenceNumber()).findAny();
				if (any.isPresent()) {
					DefaultTransportCorpDetail updateDetail = any.get();
					if (defaultTransportCorpDetail.getDefaultCorpDetailId() != updateDetail.getDefaultCorpDetailId()) {
						throw new RuntimeException(String.format("序号：%d已存在，请重新输入。", defaultTransportCorpDetail.getSequenceNumber()));
					}
				}

				if (defaultTransportCorpDetail.getTransferUnitId() != null &&
					defaultTransportCorpDetail.getTransferUnitId() != 0) {

					Unit unit = unitService.queryById(defaultTransportCorpDetail.getTransferUnitId());
					if (unit == null) {
						throw new BusinessException("9008", "中转单位");
					}
// TODO XYS - 隶属公司字段作废后代码逻辑需要更新
//					if (!corp.getCorpId().equals(unit.getSuperorCorpId())) {
//						throw new RuntimeException(String.format("%s非当前选择%s下所属单位，请重新选择。", unit.getUnitName(), corp.getCorpName()));
//					}
				}
			}

			sb.append(CheckUtils.checkLong("NO", "defaultTransportCorpDetail", "默认运输公司明细ID", defaultTransportCorpDetail.getDefaultCorpId(), 20));
			if (!StringUtils.isBlank(sb)) {
				throw new RuntimeException("数据检查失败：" + sb.toString());
			}
			defaultTransportCorpDetailMapper.updateByPrimaryKeySelective(defaultTransportCorpDetail);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public void update(DefaultTransportCorpDetail record) {
		super.update(record);
	}

	@Override
	public void update(List<DefaultTransportCorpDetail> records) {
		super.update(records);
	}

	@Override
	public void deleteById(Long id) {
		super.deleteById(id);
	}

	@Override
	public List<DefaultTransportCorpDetail> selectByDefaultCorpId(Long defaultCorpId) {

		try {
			List<DefaultTransportCorpDetail> defaultTransportCorpDetails = defaultTransportCorpDetailMapper.selectByDefaultCorpId(defaultCorpId);
			return defaultTransportCorpDetails;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}
}
