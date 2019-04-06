package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
* APP版本升级表
*
* @version 1.0.0
* @author XueYuSong
*/
@Getter
@Setter
@ToString
public class AppVersion extends SuperVO {

    private Long appId;
    private String appVersion;     // APP版本号
    private Integer appType;       // APP类型
    private String appPath;        // APP下载路径
    private String appUpdateLog;   // APP更新内容
    private Long appSize;          // APP更新文件大小
    private String appMd5;         // APP文件MD5
    private Integer appConstraint; // 是否强制更新
    private Long creator;          // 创建人
    private Date createDate;       // 创建时间
    private Long corpId;           // 公司ID
    private Integer flag;          // 封存标志

}

