package com.debug.fresh.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName app_version
 */
@TableName(value ="app_version")
@Data
public class AppVersion {
    private Integer versionId;

    private String versionNumber;

    private Integer mandatoryUpdate;

    private String downloadUrl;

    private String releaseNotes;

    private Date publishedAt;
}