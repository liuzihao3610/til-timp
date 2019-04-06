package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.JobNoticeBMapper;
import com.tilchina.timp.model.JobNotice;
import com.tilchina.timp.model.JobNoticeB;
import com.tilchina.timp.model.User;
import com.tilchina.timp.service.JobNoticeBService;
import com.tilchina.timp.service.JobNoticeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
* 定时到期任务提醒子表
*
* @version 1.0.0
* @author LiuShuqi
*/
@Service
@Slf4j
public class JobNoticeBServiceImpl extends BaseServiceImpl<JobNoticeB> implements JobNoticeBService {

	@Autowired
    private JobNoticeBMapper jobnoticebmapper;

	@Autowired
	private JobNoticeService jobNoticeService;
	
	@Override
	protected BaseMapper<JobNoticeB> getMapper() {
		return jobnoticebmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(JobNoticeB jobnoticeb) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkInteger("NO", "userType", "用户类型(0=系统用户", jobnoticeb.getUserType(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(JobNoticeB jobnoticeb) {
        StringBuilder s = checkNewRecord(jobnoticeb);
        s.append(CheckUtils.checkPrimaryKey(jobnoticeb.getJobNoticeBId()));
		return s;
	}

	/**
	 * 通过ID查询定时任务子表
	 * @param id
	 * @return
	 */
	public JobNoticeB queryById(Long id) {
		log.debug("query: {}", id);
		return jobnoticebmapper.selectByPrimaryKey(id);
	}

	/**
	 * 查询所有任务子表并分页
	 * @param map
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public PageInfo<JobNoticeB> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", new Object[]{map, pageNum, pageSize});
		PageHelper.startPage(pageNum, pageSize);
		return new PageInfo(jobnoticebmapper.selectList(map));
	}

	/**
	 * 查询所有的定时任务
	 * @param map
	 * @return
	 */
	public List<JobNoticeB> queryList(Map<String, Object> map) {
		log.debug("queryList: {}", map);
		return jobnoticebmapper.selectList(map);
	}

	/**
	 * 添加定时任务子表
	 * @param record
	 */
	public void add(JobNoticeB record) {
		log.debug("add: {}", record);

		try {
			StringBuilder s = this.checkNewRecord(record);
			if (!StringUtils.isBlank(s)) {
				throw new RuntimeException("数据检查失败：" + s.toString());
			}
			Environment environment=Environment.getEnv();
			record.setCorpId(environment.getCorp().getCorpId());
			jobnoticebmapper.insertSelective(record);

		} catch (Exception var4) {
			if (var4.getMessage().indexOf("IDX_") != -1) {
				throw new RuntimeException("数据重复，请查证后重新保存！", var4);
			} else {
				throw var4;
			}
		}
	}

	/**
	 * 更新任务子表
	 * @param record
	 */
	public void updateSelective(JobNoticeB record) {
		log.debug("updateSelective: {}", record);

		try {
			jobnoticebmapper.updateByPrimaryKeySelective(record);
		} catch (Exception var3) {
			if (var3.getMessage().indexOf("IDX_") != -1) {
				throw new RuntimeException("数据重复，请查证后重新提交！", var3);
			} else {
				throw var3;
			}
		}
	}

	/**
	 * 删除任务子表
	 * @param id
	 */
	public void deleteById(Long id) {
		log.debug("delete: {}", id);
		jobnoticebmapper.deleteByPrimaryKey(id);
	}

	/**
	 * 通过定时任务主表ID查询子表
	 * @param id
	 * @return
	 */
	@Override
	public List<JobNoticeB> queryByJobNoticeId(Long id) {
		return jobnoticebmapper.queryByJobNoticeId(id);
	}

	/**
	 * 通过任务编号查询发送列表
	 * @param jobNoticeCode
	 * @return
	 */
    @Override
	@Transactional
    public List<User> queryByJobNoticeCode(String jobNoticeCode) {
		JobNotice jobNotice=jobNoticeService.queryByCode(jobNoticeCode);
		if (jobNotice==null){
			throw new BusinessException("定时任务不存在");
		}
		List<User> userList=jobnoticebmapper.queryByJobNoticeCode(jobNoticeCode);
        return userList;
    }
}
