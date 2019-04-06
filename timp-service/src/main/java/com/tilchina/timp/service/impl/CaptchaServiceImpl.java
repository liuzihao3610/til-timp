package com.tilchina.timp.service.impl;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.catalyst.utils.MD5;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.CaptchaMapper;
import com.tilchina.timp.model.AppLogin;
import com.tilchina.timp.model.Captcha;
import com.tilchina.timp.model.User;
import com.tilchina.timp.service.AppLoginService;
import com.tilchina.timp.service.CaptchaService;
import com.tilchina.timp.service.UserService;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.util.SmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
* 短信验证码
*
* @version 1.0.0
* @author WangShengguang
*/
@Service
@Slf4j
public class CaptchaServiceImpl extends BaseServiceImpl<Captcha> implements CaptchaService {

	@Autowired
    private CaptchaMapper captchamapper;
	
	@Autowired
	private CaptchaService captchaService;
	
	@Autowired
	private AppLoginService appLoginService;
	
	@Autowired
	private UserService userService;
	
	@Override
	protected BaseMapper<Captcha> getMapper() {
		return captchamapper;
	}

	@Override
	public Captcha queryCaptcha(String phone, String captchaType) {
		return captchamapper.selectCaptcha(phone,captchaType);
	}

	@Override
	protected StringBuilder checkNewRecord(Captcha captcha) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "phoneNum", "手机号", captcha.getPhoneNum(), 11));
        s.append(CheckUtils.checkString("NO", "captchaCode", "验证码", captcha.getCaptchaCode(), 10));
        s.append(CheckUtils.checkString("YES", "identifier", "识别码", captcha.getIdentifier(), 30));
        s.append(CheckUtils.checkString("YES", "ip", "IP", captcha.getIp(), 20));
        s.append(CheckUtils.checkString("NO", "captchaType", "业务类型", captcha.getCaptchaType(), 10));
        s.append(CheckUtils.checkDate("NO", "sendTime", "发送时间", captcha.getSendTime()));
        s.append(CheckUtils.checkInteger("NO", "captchaStatus", "使用状态", captcha.getCaptchaStatus(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(Captcha captcha) {
        StringBuilder s = checkNewRecord(captcha);
        s.append(CheckUtils.checkPrimaryKey(captcha.getCaptchaId()));
		return s;
	}
	
	

	@Override
	@Transactional
	public Date queryByMobile(String mobile) {
		
		return captchamapper.queryByMobile(mobile);
	}
	 @Override
	 @Transactional
	 public String sendCode(Map<String, Object>  map,String ip) {
		try {
			if (map==null || map.size()==0) {
				throw new BusinessException("");
			}
			
			String username="DZGJWL888hy";
			Date date=new Date();
			String tkey=DateUtil.tkey(date);
			String password=MD5.MD5(MD5.MD5("KkKb68")+tkey);
			String number=(int)((Math.random()*9+1)*100000)+"";
			String content=null;
			if (null!=map.get("content")) {
				content=map.get("content").toString()+number;
			}else if (null==map.get("content")){
				content="【东泽科技】，您的验证码是："+number;
			}
			
		    if (content.length()>200) {
				throw new BusinessException("内容太长");
			}
			String mobile=(String) map.get("phone");
			if (mobile!=null) {
				Pattern p = Pattern.compile("^1([358][0-9]|4[135789]|7[0135678])[0-9]{8}$");  
				boolean result=p.matcher(mobile).find();
				if (!result) {
					throw new BusinessException("手机号码输入错误,请检查");
				}
			}
			User user=userService.getByPhone(mobile);
			if (user==null) {
				throw new BusinessException("手机号码错误");
			}

			String path = "http://www.api.zthysms.com/sendSms.do"+"?username="+username+"&tkey="+tkey+"&password="+password+"&mobile="+mobile+"&content="+content;

			//Environment environment=Environment.getEnv();
			Captcha captcha=new Captcha();
			captcha.setUserId(user.getUserId());
			captcha.setPhoneNum(mobile);
			captcha.setCaptchaCode(number);
			captcha.setIdentifier(DateUtil.dateToStringCode(new Date()));
			captcha.setIp(ip);
			captcha.setCaptchaType("login");
			captcha.setSendTime(date);
			captcha.setCaptchaStatus(0);
			captcha.setCorpId(user.getCorpId());
			Date before=captchaService.queryByMobile(mobile);
			if (null!=before) {
				long cha=date.getTime()-before.getTime();
				if (cha<=60000) {
					throw new BusinessException("您操作过于频繁,请一分钟再试");
				}else if (cha>600000) {
					captchaService.updateCaptchaStatus(mobile);
				}
			}
			captchaService.add(captcha);
			AppLogin appLogin=new AppLogin();
			appLogin.setUserId(captcha.getUserId());
			appLogin.setCaptcha(number);
			appLoginService.updateCaptcha(appLogin);
			
			String message =SmsUtil.sendMessage(path);
			String uid=message.substring(0, message.indexOf(","));
			if ("1".equals(uid)) {
				return "发送成功";
			}else {
				throw new BusinessException("发送失败");
			}
			
		} catch (Exception e) {
			throw e;
		}
	
	}
	
	@Override
	@Transactional
	public String  batchSend(Map<String, Object> map,String ip) {
		try {
			if (map.size()==0||map==null ||map.get("photos")==null) {
				throw new  BusinessException("参数为空,请检查");
			}
			//String[] photos=map.get("photos").toString().split("\"\\\\n| |,|，\"");
			List<Map<String, Object>> photos=(List<Map<String, Object>>) map.get("photos");
			
			if (photos.size()==0 || photos==null) {
				throw new BusinessException("参数为空");
			}
			for (Map<String, Object> m : photos) {
				m.put("phone", m.get("phone"));
				m.put("content", map.get("content"));
				captchaService.sendCode(m,ip);
			}
			return "发送成功";
		} catch (Exception e) {
			throw e;
		}
		
		
	}
	/**
	 * 更新验证码状态
	 */
	@Override
	@Transactional
	public void updateCaptchaStatus(String mobile) {
		captchamapper.updateCaptchaStatus(mobile);
		
	}
	/**
	 * 查询验证码剩余条数
	 */
	@Override
	@Transactional
	public int selectNumber(Map<String, Object> data) {
		try {
			String username="DZGJWL888hy";
			String tkey=DateUtil.tkey(new Date());
			String password=MD5.MD5(MD5.MD5("KkKb68")+tkey);
			String path = "http://www.api.zthysms.com/balance.do"+"?username="+username+"&tkey="+tkey+"&password="+password;
			String message =SmsUtil.sendSmsByPost(path);
			int number=Integer.parseInt(message);
			return number;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional
	public String business(Map<String, Object>  map,String ip) {
		try {
			if (map==null || map.size()==0) {
				throw new BusinessException("");
			}

			String username="DZGJWL888hy";
			Date date=new Date();
			String tkey=DateUtil.tkey(date);
			String password=MD5.MD5(MD5.MD5("KkKb68")+tkey);
			String number=(int)((Math.random()*9+1)*100000)+"";
			String content=null;
			if (null!=map.get("content")) {
				content=map.get("content").toString()+number;
			}

			if (content.length()>200) {
				throw new BusinessException("内容太长");
			}
			String mobile=(String) map.get("phone");
			if (mobile!=null) {
				Pattern p = Pattern.compile("^1([358][0-9]|4[135789]|7[0135678])[0-9]{8}$");
				boolean result=p.matcher(mobile).find();
				if (!result) {
					throw new BusinessException("手机号码输入错误,请检查");
				}
			}
			User user=userService.getByPhone(mobile);
			if (user==null) {
				throw new BusinessException("手机号码错误");
			}

			String path = "http://www.api.zthysms.com/sendSms.do"+"?username="+username+"&tkey="+tkey+"&password="+password+"&mobile="+mobile+"&content="+content;

			//Environment environment=Environment.getEnv();
			Captcha captcha=new Captcha();
			captcha.setUserId(user.getUserId());
			captcha.setPhoneNum(mobile);
			captcha.setCaptchaCode(number);
			captcha.setIdentifier(DateUtil.dateToStringCode(new Date()));
			captcha.setIp(ip);
			captcha.setCaptchaType("运单");
			captcha.setSendTime(date);
			captcha.setCaptchaStatus(0);
			captcha.setCorpId(user.getCorpId());
			Date before=captchaService.queryByMobile(mobile);
			if (null!=before) {
				long cha=date.getTime()-before.getTime();
				if (cha<=60000) {
					throw new BusinessException("您操作过于频繁,请一分钟再试");
				}else if (cha>600000) {
					captchaService.updateCaptchaStatus(mobile);
				}
			}
			captchaService.add(captcha);
			AppLogin appLogin=new AppLogin();
			appLogin.setUserId(captcha.getUserId());
			appLogin.setCaptcha(number);
			appLoginService.updateCaptcha(appLogin);

			String message =SmsUtil.sendMessage(path);
			String uid=message.substring(0, message.indexOf(","));
			if ("1".equals(uid)) {
				return "发送成功";
			}else {
				throw new BusinessException("发送失败");
			}

		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 *
	 * @param map
	 * @return
	 */
    @Override
    public Boolean validationCode(Map<String, Object> map) {
        if (map==null){
			throw  new BusinessException("验证码或手机号码为空");
		}
		String phone=map.get("phone").toString();
		Captcha captcha=captchamapper.selectCaptcha(phone,"运单");
		String code=map.get("code").toString();
	    return code.equals(captcha.getCaptchaCode());
    }
}
