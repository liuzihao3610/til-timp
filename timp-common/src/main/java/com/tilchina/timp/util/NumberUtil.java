package com.tilchina.timp.util;/*
 * @author XueYuSong
 * @date 2018-07-12 09:28
 */

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class NumberUtil {

	public static Long parseLong(String str) {

		if (!StringUtils.isNotBlank(str)) {
			throw new RuntimeException("ID字段为空，请检查后重试。");
		}

		if (!StringUtils.isNumeric(str)) {
			throw new RuntimeException("ID字段为非数值类型，请检查后重试。");
		}

		if (!NumberUtils.isParsable(str)) {
			throw new RuntimeException("ID字段无法解析，请检查后重试。");
		}

		return NumberUtils.toLong(str);
	}
}
