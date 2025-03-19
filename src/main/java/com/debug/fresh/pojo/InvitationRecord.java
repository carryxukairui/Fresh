package com.debug.fresh.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName invitation_record
 */
@TableName(value ="invitation_record")
@Data
public class InvitationRecord {
    private Integer recordId;

    private Integer inviterId;

    private Integer inviteeId;

    private String usedCode;

    private Object rewardStatus;

    private Date createdAt;

    private Date rewardedAt;
}