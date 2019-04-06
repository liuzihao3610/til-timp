package com.tilchina.timp.service.impl;

import com.tilchina.timp.util.JpushUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.Push;
import com.tilchina.timp.service.PushService;
import com.tilchina.timp.util.JiGuangApi;
import com.tilchina.timp.util.JpushClientUtil;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.PushMapper;
/*import org.apache.http.client.fluent;*/

/**
* 推送档案
*
* @version 1.0.0
* @author Xiahong
*/
@Service
@Slf4j
public class PushServiceImpl implements PushService {

	@Autowired
    private PushMapper pushmapper;
	
	protected BaseMapper<Push> getMapper() {
		return pushmapper;
	}
	protected StringBuilder checkNewRecord(Push push) {
		StringBuilder s = new StringBuilder();
		s.append(CheckUtils.checkInteger("NO", "platformType", "推送平台设置类型:0=all,1=android,2=ios", push.getPlatformType(), 10));
        s.append(CheckUtils.checkInteger("NO", "pushType", "推送类型:0=自动推送,1=人工推送", push.getPushType(), 10));
        s.append(CheckUtils.checkInteger("NO", "audienceType", "推送设备指定类型:0=all,1=多个标签(只要在任何一个标签范围内都满足),2=多个标签(需要同时在多个标签范围内),3=多个别名,4=多个注册ID,5=多类推送目标", push.getAudienceType(), 10));
        s.append(CheckUtils.checkString("NO", "audience", "推送设备指定内容", push.getAudience(), 100));
        s.append(CheckUtils.checkInteger("NO", "notificationType", "通知类型:0=通知内容体,1=消息内容体", push.getNotificationType(), 10));
        s.append(CheckUtils.checkString("NO", "content", "通知内容", push.getContent(), 150));
        s.append(CheckUtils.checkString("NO", "title", "通知标题", push.getTitle(), 50));
        s.append(CheckUtils.checkString("YES", "cid", "用于防止", push.getCid(), 100));
        s.append(CheckUtils.checkInteger("NO", "pushCount", "推送信息条数", push.getPushCount(), 10));
        s.append(CheckUtils.checkDate("YES", "createDate", "创建时间", push.getCreateDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", push.getFlag(), 10));
		return s;
	}

	protected StringBuilder checkUpdate(Push push) {
        StringBuilder s = checkNewRecord(push);
        s.append(CheckUtils.checkPrimaryKey(push.getPushId()));
		return s;
	}
	
	@Override
    public Push queryById(Long id) {
        log.debug("query: {}",id);
        Push push = null;
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "id", "推送id", id, 10));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
           }
        	push = getMapper().selectByPrimaryKey(id);
        	if(push == null) {
   		    	throw new BusinessException("9008","推送id");
   	   		}
		} catch (Exception e) {
            if(e instanceof BusinessException){
    				throw e;
    		}else {
    				throw new RuntimeException("操作失败！", e);
    	    }
        }
		return push;
    }

    @Override
    public PageInfo<Push> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<Push> pushPageInfo = null;
        try {
        	pushPageInfo = new PageInfo<Push>(getMapper().selectList(map));
		} catch (Exception e) {
				throw new RuntimeException("操作失败！", e);
        }
        return pushPageInfo;
    }
    
    @Override
    public List<Push> queryList(Map<String, Object> map) {
        log.debug("queryList: {}", map);
        List<Push>  pushs = null;
        try {
        	pushs = getMapper().selectList(map);
		} catch (Exception e) {
			throw new RuntimeException("操作失败！", e);
	   }
        return pushs;
    }
    @Override
    public String addPush(Push push) {
        log.debug("push: {}",push);
        StringBuilder s;
        Environment env = Environment.getEnv();
        int pushStatus = 0;
        try {
            s = checkNewRecord(push);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            if(env.getUser() != null && env.getCorp() != null) {
                push.setCreator(env.getUser().getUserId());
                push.setCorpId(env.getCorp().getCorpId());
                push.setCreateDate(new Date());
            }
            getMapper().insertSelective(push);
            JpushUtil.jiguangPush(push.getPlatformType(),push.getAudience(),push.getContent(),push.getTitle());
        } catch (Exception e) {
        	log.error(e.getMessage());
        	/*
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！", e);
            } else if(e instanceof BusinessException){
    				throw e;
    		}else {
    				throw new RuntimeException("操作失败！", e);
    	    }*/
        }
        return null;
    }
	@Override
	public void push(Push push) {
		log.debug("push: {}",push);
		 StringBuilder s;
        Environment env = Environment.getEnv();
        int pushStatus = 0;
		try {
		   s = checkNewRecord(push);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            if(env.getUser() != null && env.getCorp() != null) {
            	push.setCreator(env.getUser().getUserId());
            	push.setCorpId(env.getCorp().getCorpId());
            	push.setCreateDate(new Date());
            }
            pushStatus = JiGuangApi.sendToAll(push);
			getMapper().insertSelective(push);
			 log.info(pushStatus != 0?"推送成功":"推送失败");
		} catch (Exception e) {
            if(e instanceof BusinessException){
    				throw e;
    		}else {
    				throw new RuntimeException("推送失败！", e);
    	    }
        }
		
	}
    

}
