package com.debug.fresh.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User {
    @TableId
    private Integer userId;

    private String phone;

    private String passwordHash;

    private String nickname;

    private String avatarUrl;

    private Date createdAt;

    private Date lastLogin;

    private Integer membershipId;

    private Integer daysUsed;

    private Integer allowAutoUpdate;

    private String invitationCode;

    private Integer invitedBy;

    private Date invitationCodeExpire;
}