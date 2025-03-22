package com.debug.fresh.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName reminder
 */
@TableName(value ="reminder")
@Data
public class Reminder {
    @TableId
    private Integer reminderId;

    private Integer itemId;

    private Date triggerTime;

    private Object type;

    private Integer repeatInterval;

    private Object repeatUnit;

    private Integer advanceDays;

    private Integer isTriggered;

    private Date nextTrigger;
}