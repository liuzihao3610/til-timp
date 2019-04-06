package com.tilchina.timp.mapper;

import java.util.Date;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.Captcha;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 短信验证码
*
* @version 1.0.0
* @author WangShengguang
*/
@Repository
public interface CaptchaMapper extends BaseMapper<Captcha> {

    Captcha selectCaptcha(@Param("phoneNum") String phone, @Param("captchaType") String captchaType);

	Date queryByMobile(String mobile);

	void updateCaptchaStatus(String mobile);
}
