package com.debug.fresh.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @TableName item
 */
@TableName(value ="item")
@Data
public class Item {
    private Integer itemId;

    private Integer userId;

    private String name;

    private String imageUrl;

    private Integer categoryId;

    private Integer quantity;

    private BigDecimal price;

    private String location;

    private String notes;

    private Object type;

    private Date productionDate;

    private Integer shelfLife;

    private Object shelfLifeUnit;

    private Date expirationDate;

    private Date limitedDate;

    private Date startDate;

    private Date endDate;

    private Date createdAt;

    private Date updatedAt;
}