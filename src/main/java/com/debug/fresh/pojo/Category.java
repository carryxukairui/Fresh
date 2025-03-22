package com.debug.fresh.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName category
 */
@TableName(value ="category")
@Data
public class Category {
    @TableId
    private Integer categoryId;

    private String name;

    private Integer parentId;

    private Integer level;

    private Integer isSystem;

    private Date createdAt;
}