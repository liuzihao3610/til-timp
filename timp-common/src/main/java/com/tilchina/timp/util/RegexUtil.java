package com.tilchina.timp.util;
/*
 * @author XueYuSong
 * @date 2018-06-01 09:36
 */

import com.tilchina.catalyst.spring.PropertiesUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

	/**
	 * 检查text是否符合规则
	 * @param key    config.properties中pattern对应的key
	 * @param text   文本
	 * @return boolean
	 */
	public static Boolean validate(String key, String text) {

		String pattern = PropertiesUtils.getString(key);
		Pattern compile = Pattern.compile(pattern);

		Matcher matcher = compile.matcher(text);
		while (matcher.find()) {
			return true;
		}

		return false;
	}

	public static String validate(String fieldName, String key, String text) {
		String pattern = PropertiesUtils.getString(key);
		Pattern compile = Pattern.compile(pattern);

		Matcher matcher = compile.matcher(text);
		while (matcher.find()) {
			return "";
		}

		return String.format("%s格式有误，请检查后重试。", fieldName);
	}

	public static String validate(String fieldName, Integer fieldLength, String key, String text) {
		if (ObjectUtils.notEqual(text.length(), fieldLength)) {
			return String.format("%s长度有误，请检查后重试。", fieldName);
		}

		String pattern = PropertiesUtils.getString(key);
		Pattern compile = Pattern.compile(pattern);

		Matcher matcher = compile.matcher(text);
		while (matcher.find()) {
			return "";
		}

		return String.format("%s格式有误，请检查后重试。", fieldName);
	}

	/**
	 * 从文本中提取数字
	 * @param text
	 * @return Integer[]
	 */
	public static Integer[] extractDigits(String text) {

		List<Integer> digits = new ArrayList<>();

		String pattern = "(\\d+)";
		Pattern compile = Pattern.compile(pattern);

		Matcher matcher = compile.matcher(text);
		while (matcher.find()) {
			String digitString = matcher.group(0);
			if (NumberUtils.isDigits(digitString)) {
				Integer digit = NumberUtils.toInt(digitString);
				digits.add(digit);
			}
		}

		Integer[] result = new Integer[digits.size()];
		if (CollectionUtils.isNotEmpty(digits)) {
			result = digits.toArray(result);
		}

		return result;
	}
}
