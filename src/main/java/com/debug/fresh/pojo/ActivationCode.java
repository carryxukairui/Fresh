package com.debug.fresh.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName activation_code
 */
@TableName(value ="activation_code")
@Data
public class ActivationCode {
    private Integer codeId;

    private String code;

    private Object type;

    private Integer generatedBy;

    private Integer usedBy;

    private String orderId;

    private Date generatedAt;

    private Date usedAt;

    private Object status;

    private Date expirationDate;
}