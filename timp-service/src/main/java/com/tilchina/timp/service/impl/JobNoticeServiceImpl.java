package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.JobNoticeMapper;
import com.tilchina.timp.model.JobNotice;
import com.tilchina.timp.model.JobNoticeB;
import com.tilchina.timp.service.JobNoticeBService;
import com.tilchina.timp.service.JobNoticeService;
import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* 定时任务到期提醒
*
* @version 1.0.0
* @author LiuShuqi
*/
@Service
@Slf4j
public class JobNoticeServiceImpl  extends BaseServiceImpl<JobNotice> implements JobNoticeService {

	@Autowired
    private JobNoticeMapper jobnoticemapper;

	@Autowired
	private JobNoticeBService jobNoticeBService;

	@Override
	protected BaseMapper<JobNotice> getMapper() {
		return jobnoticemapper;
	}

	@Override
	protected StringBuilder checkNewRecord(JobNotice jobnotice) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "jobNoticeCode", "任务编号", jobnotice.getJobNoticeCode(), 20));
        s.append(CheckUtils.checkString("NO", "jobNoticeName", "任务名称", jobnotice.getJobNoticeName(), 20));
        s.append(CheckUtils.checkString("YES", "remark", "描述", jobnotice.getRemark(), 200));
        s.append(CheckUtils.checkDate("YES", "createDate", "创建时间", jobnotice.getCreateDate()));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(JobNotice jobnotice) {
        StringBuilder s = checkNewRecord(jobnotice);
        s.append(CheckUtils.checkPrimaryKey(jobnotice.getJobNoticeId()));
		return s;
	}

	/**
	 * 通过定时任务ID查询
	 * @param id
	 * @return
	 */
	@Override
	@Transactional
	public JobNotice queryById(Long id) {
		log.debug("query: {}", id);
		JobNotice jobNotice=jobnoticemapper.selectByPrimaryKey(id);
		jobNotice.setJobNoticeBS(jobNoticeBService.queryByJobNoticeId(jobNotice.getJobNoticeId()));
		return jobNotice;
	}

	/**
	 * 分页查询所有的定时任务
	 * @param map
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@Override
	@Transactional
	public PageInfo<JobNotice> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", new Object[]{map, pageNum, pageSize});
		PageHelper.startPage(pageNum, pageSize);
		if (map!=null){
			DateUtil.addTime(map);
		}
		return new PageInfo(jobnoticemapper.selectList(map));

	}

	/**
	 * 查询所有的定时任务
	 * @param map
	 * @return
	 */
	@Override
	@Transactional
	public List<JobNotice> queryList(Map<String, Object> map) {
		log.debug("queryList: {}", map);
		return jobnoticemapper.selectList(map);
	}

	/**
	 * 添加定时任务
	 * @param record
	 */
	@Override
	@Transactional
	public void add(JobNotice record) {
		log.debug("add: {}", record);

		try {
			StringBuilder s = this.checkNewRecord(record);
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			Environment environment=Environment.getEnv();
			record.setCreator(environment.getUser().getUserId());
			record.setCreateDate(new Date());
			record.setCorpId(environment.getCorp().getCorpId());
			jobnoticemapper.insertSelective(record);
			//保存任务子表
			List<JobNoticeB> jobNoticeBS=record.getJobNoticeBS();
			if (CollectionUtils.isEmpty(jobNoticeBS)){
				throw new BusinessException("至少添加一条任务明细");
			}
			jobNoticeBS.forEach(jobNoticeB -> {
				jobNoticeB.setJobNoticeId(record.getJobNoticeId());
				jobNoticeB.setCorpId(record.getCorpId());
				jobNoticeBService.add(jobNoticeB);
			});


		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new RuntimeException("数据重复，请查证后重新保存！", e);
			} else if (e instanceof BusinessException){
				throw e;
			}else{
				throw new BusinessException("添加失败");
			}
		}
	}

	/**
	 * 部分更新定时任务
	 * @param record
	 */
	@Override
	@Transactional
	public void updateSelective(JobNotice record) {
		log.debug("updateSelective: {}", record);

		try {
			JobNotice resultJobNotice=jobnoticemapper.selectByPrimaryKey(record.getJobNoticeId());
			if ( resultJobNotice == null ){
				throw new BusinessException("定时任务不存在,请核实");
			}
			record.setJobNoticeCode(null);
			jobnoticemapper.updateByPrimaryKeySelective(record);
			List<JobNoticeB> newJobNoticeBS=record.getJobNoticeBS();
			//rowStatus: 0 unchange; 1 new ; 2 edit ; 3 del
			newJobNoticeBS.forEach(jobNoticeB -> {
				if (jobNoticeB.getRowStatus()==1){
					jobNoticeB.setJobNoticeId(record.getJobNoticeId());
					jobNoticeBService.add(jobNoticeB);
				}
				if (jobNoticeB.getRowStatus()==2){
					jobNoticeBService.updateSelective(jobNoticeB);
				}
				if (jobNoticeB.getRowStatus()==3){
					jobNoticeBService.deleteById(jobNoticeB.getJobNoticeBId());
				}
			});

		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new RuntimeException("数据重复，请查证后重新提交！", e);
			} else {
				throw e;
			}
		}
	}

	/**
	 * 通过ID删除定时任务
	 * @param id
	 */
	@Override
	@Transactional
	public void deleteById(Long id) {
		log.debug("delete: {}", id);
		JobNotice jobNotice=jobnoticemapper.selectByPrimaryKey(id);
		if ( jobNotice == null ){
			throw new BusinessException("定时任务不存在");
		}
		List<JobNoticeB> jobNoticeBS=jobNoticeBService.queryByJobNoticeId(id);
		jobNoticeBS.forEach(jobNoticeB -> {
			jobNoticeBService.deleteById(jobNoticeB.getJobNoticeBId());
		});
		jobnoticemapper.deleteByPrimaryKey(id);
	}

	/**
	 * 通过定时任务编号查询定时任务
	 * @param jobNoticeCode
	 * @return
	 */
    @Override
	@Transactional
    public JobNotice queryByCode(String jobNoticeCode) {

        return jobnoticemapper.queryByCode(jobNoticeCode);
    }
}
