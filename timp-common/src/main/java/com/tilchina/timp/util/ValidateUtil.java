package com.tilchina.timp.util;/*
 * @author XueYuSong
 * @date 2018-06-19 14:15
 */

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.expection.BusinessException;

public class ValidateUtil {

	private static final String NULL = "9001";
	private static final String NOT_EXISTS = "9001";

	public static void validate(Long id, BaseService service, String fieldName) {

		if (id == null) {
			throw new BusinessException(NULL, fieldName);
		}

		if (service != null && service.queryById(id) == null) {
			throw new BusinessException(NOT_EXISTS, fieldName);
		}
	}

	public static void validateEnum(int enumValue) {

	}
}
