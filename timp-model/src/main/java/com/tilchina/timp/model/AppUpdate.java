package com.tilchina.timp.model;/*
 * @author XueYuSong
 * @date 2018-06-14 15:26
 */

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppUpdate {

	@JSONField(name = "update")
	private String appUpdate;

	@JSONField(name = "new_version")
	private String appVersion;

	@JSONField(name = "apk_file_url")
	private String appPath;

	@JSONField(name = "update_log")
	private String appUpdateLog;

	@JSONField(name = "target_size")
	private String appSize;

	@JSONField(name = "new_md5")
	private String appMd5;

	@JSONField(name = "constraint")
	private boolean appConstraint;
}
