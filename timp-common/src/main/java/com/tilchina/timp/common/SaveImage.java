package com.tilchina.timp.common;

import com.tilchina.catalyst.spring.PropertiesUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.UUID;

/**
 * @author WangShengguang
 * @version 1.0.0 2018/4/22
 */
public class SaveImage {

	public static String save(File image, String suffix) {
		if (StringUtils.isBlank(suffix)) {
			suffix = ".jpg";
		}
        String name = UUID.randomUUID().toString();
        String path = PropertiesUtils.getString("timp.image.path");
		String imageName = path + "/" + name + suffix;

        return null;
    }
}
