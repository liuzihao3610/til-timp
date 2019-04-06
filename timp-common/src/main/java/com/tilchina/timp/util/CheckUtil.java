package com.tilchina.timp.util;

import com.tilchina.timp.model.Contacts;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.apache.ibatis.builder.BuilderException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CheckUtil {

	/**
	 * 校验字符串
	 *
	 * @since 1.0.0
	 * @param value			待校验字符串
	 * @param nullable		是否可以为空，true可以，false不能为空
	 * @param maxLength		最大长度
	 * @param name			字段名称
	 * @return java.lang.String
	 */
	public static String checkString(String value,boolean nullable,int maxLength, String name){
		if(!nullable && StringUtils.isBlank(value)){
			return name+"不能为空！";
		}
		if(!StringUtils.isBlank(value) && value.trim().length() > maxLength){
			return name+"长度不能超过"+maxLength+",";
		}
		return "";
	}

	/**
	 * 校验是否可以为空
	 *
	 * @since 1.0.0
	 * @param value		待校验字段
	 * @param nullable	是否可以为空
	 * @param name      字段名称
	 * @return java.lang.String
	 */
	public static String checkNull(Object value, boolean nullable, String name){
		if(!nullable && value == null){
			return name+"不能为空！";
		}
		return "";
	}

	/**
	 * 验证邮箱
	 *
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
	    boolean flag = false;
	    try {
	        String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
	        Pattern regex = Pattern.compile(check);
	        Matcher matcher = regex.matcher(email);
	        flag = matcher.matches();
	    } catch (Exception e) {
	        flag = false;
	    }
	    return flag;
	}

	/**
	 * 验证手机号码，11位数字，1开通，第二位数必须是3456789这些数字之一 *
	 * @param mobileNumber
	 * @return
	 */
	public static boolean checkMobileNumber(String mobileNumber) {
	    boolean flag = false;
	    try {
	        Pattern regex = Pattern.compile("^1[345789]\\d{9}$");
	        Matcher matcher = regex.matcher(mobileNumber);
	        flag = matcher.matches();
	    } catch (Exception e) {
	        e.printStackTrace();
	        flag = false;

	    }
	    return flag;
	}

	/**
	 * 验证国定电话：
	 * 手机号码，11位数字，1开通，第二位数必须是3456789这些数字之一 *
	 * 固定电话号码
	 * @param mobileNumber
	 * @return
	 */
	public static boolean checkPhoneAndTelephone(String mobileNumber) {
		boolean flag = false;
		try {
			Pattern regex = Pattern.compile("^1[345789]\\d{9}$|^(0[0-9]{2,3}/-)?([2-9][0-9]{6,7})+(/-[0-9]{1,4})?$|\\d{3}-\\d{8}|\\d{4}-\\d{7}");
			Matcher matcher = regex.matcher(mobileNumber);
			flag = matcher.matches();
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;

		}
		return flag;
	}

	/**
	 * 验证车架号
	 * @param carVins
	 * @return
	 */
	public static List<String> checkcarVins(String carVins) {
		Set<String>  set;
		  List<String> listCarVins = null ;
	    try {
	        Pattern regex = Pattern.compile("[a-zA-Z0-9]{17}");
	        Matcher matcher = regex.matcher(carVins);
	        listCarVins = new ArrayList<>();
	        set = new HashSet<>();
	        while (matcher.find()) { 
	        	set.add(matcher.group());
	         }   
	        listCarVins.addAll(set);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		return listCarVins;
	}
	
	public static String bytesToHexString(byte[] src) {  
        StringBuilder stringBuilder = new StringBuilder();  
        if (src == null || src.length <= 0) {  
            return null;  
        }  
        for (int i = 0; i < src.length; i++) {  
            int v = src[i] & 0xFF;  
            String hv = Integer.toHexString(v);  
            if (hv.length() < 2) {  
                stringBuilder.append(0);  
            }  
            stringBuilder.append(hv);  
        }  
        return stringBuilder.toString();  
    } 
	 
	/**
	 * 验证图片格式
	 * @param imageDataArr
	 * @return
	 * @throws IOException
	 */
	public static String[] getImgType(byte[] imageDataArr) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageDataArr);
                MemoryCacheImageInputStream is = new MemoryCacheImageInputStream(bais)) {
            Iterator<ImageReader> it = ImageIO.getImageReaders(is);

            if (!it.hasNext()) {
                throw new RuntimeException("非图片文件");
            }
            ImageReader reader = it.next();
            return reader.getOriginatingProvider().getFormatNames();
        }
	}
	/**
	 * 验证手机号码，11位数字，1开通，第二位数必须是3456789这些数字之一  *
	 * 验证邮箱
	 * @param mobileNumber email
	 * @return boolean
	 */
	public static boolean checkMobileNumberAndEmail(String mobileNumber,String email) {
	    boolean flag = true;									
	    try {
	        Pattern regex = Pattern.compile("^1[345789]\\d{9}$|^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
	        Matcher matcherNumber = regex.matcher(mobileNumber);
	        Matcher matcherEmail = null;
	        if(email != null ) {
	        	matcherEmail = regex.matcher(email);
		        flag = !matcherNumber.matches() || !matcherEmail.matches();
	        }else {
		        flag = !matcherNumber.matches();
	        }
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        flag = true;

	    }
	    return flag;
	}
	
	/**
	 * 牵引车车牌号码验证
	 * @param carnumber
	 * @return
	 */
    public static boolean tractorIsCarnumberNO(String carnumber) {
        String carnumRegex = "^([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1})[0-9A-Z]{5}$|^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[0-9A-Z]{5}$";
        if (TextUtils.isEmpty(carnumber)) return false;
        else return carnumber.matches(carnumRegex);
    }
    
	/**
	 * 挂车车牌号码验证
	 * @param carnumber
	 * @return
	 */
    public static boolean trailerIsCarnumberNO(String carnumber) {
        String carnumRegex = "^([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1})[0-9A-Z]{5}$|^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[0-9A-Z]{5}$|^([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1})[0-9A-Z]{4}挂$|^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[0-9A-Z]{4}挂$";
        if (TextUtils.isEmpty(carnumber)) return false;
        else return carnumber.matches(carnumRegex);
    }
    
	/**
	 * 取车牌号码（省份）
	 * @param carnumber
	 * @return
	 */
    public static String province(String carnumber) {
    	if(!tractorIsCarnumberNO(carnumber)) {
    		throw new BuilderException("该车牌号码验证失败！");
    	}
        String carnumRegex = "([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领])|[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领]";
        Pattern regex = Pattern.compile(carnumRegex);
        Matcher matcherNumber = regex.matcher(carnumber);
        if(matcherNumber.find()) {
        	return matcherNumber.group();
        }else {
        	throw new BuilderException("该车牌号码验证省份失败！");
        }
    }
	
	/**
	 * 取车牌号码（字母及数字）
	 * @param carnumber
	 * @return
	 */
    public static String lettersAndNumbers(String carnumber) {
    	if(!tractorIsCarnumberNO(carnumber)) {
    		throw new BuilderException("该车牌号码验证失败！");
    	}
        String carnumRegex = "[0-9A-Z]{5}";
        Pattern regex = Pattern.compile(carnumRegex);
        Matcher matcherNumber = regex.matcher(carnumber);
        if(matcherNumber.find()) {
        	return matcherNumber.group();
        }else {
        	throw new BuilderException("该车牌号码验证字母及数字失败！");
        }
    }

	/**
	 * 经度验证
	 * @param lng
	 * @return
	 */
	public static boolean checkLnt(String lng) {
		String carnumRegex = "^[\\-\\+]?(0?\\d{1,2}|0?\\d{1,2}\\.\\d{1,15}|1[0-7]?\\d{1}|1[0-7]?\\d{1}\\.\\d{1,15}|180|180\\.0{1,15})$";
		if (TextUtils.isEmpty(lng)) return false;
		else return lng.matches(carnumRegex);
	}

	/**
	 * 纬度验证
	 * @param lat
	 * @return
	 */
	public static boolean checkLat(String lat) {
		String carnumRegex = "^[\\-\\+]?([0-8]?\\d{1}|[0-8]?\\d{1}\\.\\d{1,15}|90|90\\.0{1,15})$";
		if (TextUtils.isEmpty(lat)) return false;
		else return lat.matches(carnumRegex);
	}

	/**
	 * 校验身份证号码
	 * @param identityCardNumber
	 * @return
	 */
	public static boolean checkIdentityCardNumber(String identityCardNumber) {
		String carnumRegex =  "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
		if (TextUtils.isEmpty(identityCardNumber)) return false;
		else return identityCardNumber.matches(carnumRegex);

	}

	/**
	 * 正浮点数验证
	 * @param strDouble
	 * @return
	 */
	public static boolean checkDouble(String strDouble) {
		String carnumRegex = "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$";
		if (TextUtils.isEmpty(strDouble)) return false;
		else return strDouble.matches(carnumRegex);
	}

	/**
	 * 整数验证
	 * @param integer
	 * @return
	 */
	public static boolean checkInteger(String integer) {
		String carnumRegex = "^[1-9]\\d*$";
		if (TextUtils.isEmpty(integer)) return false;
		else return integer.matches(carnumRegex);
	}

	/**
	 * qq号码验证
	 * @param qq
	 * @return
	 */
	public static boolean checkQq(String qq) {
		String carnumRegex = "^[1-9][0-9]{4,}$";
		if (TextUtils.isEmpty(qq)) return false;
		else return qq.matches(carnumRegex);
	}

	/**
	 * 获取收发货单位联系人
	 * @param content
	 * @return
	 */
	public static List<Contacts> checkContacts(String content) {
		String ContactsRegex = "[\\u4e00-\\u9fa5-a-zA-z-\\w]{1,}[:-：]1[345789]\\d{9}[;-；]";
		String PhoneRegex = "1[345789]\\d{9}";
		String NameRegex = "[\\u4e00-\\u9fa5-a-zA-z-\\w]{1,}";
		List<Contacts> contactsList = new ArrayList<Contacts>();
		Pattern regex = Pattern.compile(ContactsRegex);
		Matcher matcherNumber = regex.matcher(content);
		while (matcherNumber.find()) {
			String group = matcherNumber.group();
			Contacts contacts = new Contacts();
			Pattern PhonePattern = Pattern.compile(PhoneRegex);
			Matcher PhoneMatcher = PhonePattern.matcher(group);
			Pattern NamePattern = Pattern.compile(NameRegex);
			Matcher NameMatcher = NamePattern.matcher(group);
			if (PhoneMatcher.find()) {
				contacts.setPhone(PhoneMatcher.group());
			}
			if (NameMatcher.find()) {
				contacts.setContactsName(NameMatcher.group());
			}
			contactsList.add(contacts);
		}
		return contactsList;
	}

	/**
	 * 去除首尾空格
	 * @param name
	 * @return
	 */
	public static String checkStringTrim(String name){
		if (name != null){
			name = name.trim();
		}
		return name;
	}

	/**
	 * 校验价格
	 * @param nullable 是否可为空 YES/NO
	 * @param attName 属性名
	 * @param comment 属性含义
	 * @param decimal  价格
	 * @param length  长度
	 * @param scale   精度
	 * @return
	 */
	public static String checkBigDecimal(String nullable, String attName, String comment, BigDecimal decimal, int length, int scale) {
		String desc = comment + "[" + attName + "]";
		if (decimal == null) {
			return !isNullAble(nullable) ? desc + " can not be null! " : "";
		} else if (decimal.toString().length() > length) {
			return desc + " value " + decimal + " out of range [" + length + "].";
		} else {
			String scaleStr = decimal.toString();
			if (scale > 0 && scaleStr.contains(".") && scaleStr.split("\\.")[1].length() > scale) {
				return desc + " value " + decimal + " scale out of range [" + scale + "].";
			} else {
				return "";
			}
		}
	}

	/**
	 * 检验是否可为空
	 * @param nullable
	 * @return
	 */
	private static boolean isNullAble(String nullable) {
		return "YES".equals(nullable.toUpperCase()) || "Y".equals(nullable.toUpperCase());
	}

	/**
	 * 截取请求路劲末尾的url
	 * @param url
	 * @return
	 */
	public  static String checkUrl(String url) {
		String urlPath = "";
		try {
			String check = "[a-z0-9A-Z]{1,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(url);
			if (matcher.find()){
				urlPath = matcher.group();
			}
		} catch (Exception e) {
			urlPath = "";
		}
		return urlPath;
	}

}
