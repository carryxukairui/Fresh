package com.debug.fresh.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName session
 */
@TableName(value ="session")
@Data
public class Session {
    private Integer sessionId;

    private Integer userId;

    private String deviceHash;

    private String token;

    private Date loginTime;

    private Date lastActive;

    private Integer isValid;

    private String ipAddress;

    private String clientInfo;
}