package com.tilchina.timp.service;

import java.util.Date;
import java.util.Map;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Captcha;

/**
* 短信验证码
*
* @version 1.0.0
* @author WangShengguang
*/
public interface CaptchaService extends BaseService<Captcha> {
	/**
	 * 查询验证码
	 * @param phone 手机号码
	 * @param captchaType 验证码类型
	 * @return
	 */
    Captcha queryCaptcha(String phone, String captchaType);
    /**
     * 查询发送时间
     * @param mobile 手机
     * @return
     */
	Date queryByMobile(String mobile);
	/**
	 * 批量发送验证码
	 * @param data 参数
	 * @param ip IP地址
	 * @return
	 */
	String batchSend(Map<String, Object> data, String ip);
	/**
	 * 发送单条验证码
	 * @param data 参数
	 * @param ip IP地址
	 * @return
	 */
	String sendCode(Map<String, Object> data, String ip);
	/**
	 * 更新验证码状态
	 * @param mobile 手机号码
	 */
	void updateCaptchaStatus(String mobile);
	/**
	 * 查询剩余条数
	 * @param data 参数
	 * @return
	 */
	int selectNumber(Map<String, Object> data);

	/**
	 * 运单发送验证码
	 * @param data
	 * @param ip
	 * @return
	 */
    String business(Map<String,Object> data, String ip);

	/**
	 * 验证运单验证码
	 * @param data
	 * @return
	 */
	Boolean validationCode(Map<String,Object> data);
}
