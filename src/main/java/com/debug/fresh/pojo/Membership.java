package com.debug.fresh.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName membership
 */
@TableName(value ="membership")
@Data
public class Membership {
    @TableId
    private Integer membershipId;

    private Integer userId;

    private Integer type;

    private Date startDate;

    private Date endDate;

    private Integer rewardDays;

    private Integer maxRewardDays;

    private Object source;

    private Date createdAt;
}