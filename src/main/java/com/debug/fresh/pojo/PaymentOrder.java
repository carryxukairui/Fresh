package com.debug.fresh.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @TableName payment_order
 */
@TableName(value ="payment_order")
@Data
public class PaymentOrder {
    @TableId
    private String orderId;

    private Integer userId;

    private BigDecimal amount;

    private Object paymentType;

    private Object status;

    private Object productType;

    private Integer activationCodeId;

    private String transactionId;

    private Date createdAt;

    private Date paidAt;

    private Date expiredAt;
}