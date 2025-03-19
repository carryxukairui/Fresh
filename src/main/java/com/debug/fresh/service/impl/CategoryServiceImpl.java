package com.debug.fresh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.debug.fresh.pojo.Category;
import com.debug.fresh.service.CategoryService;
import com.debug.fresh.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

/**
* @author 28611
* @description 针对表【category(物品分类表)】的数据库操作Service实现
* @createDate 2025-03-19 14:13:46
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

}




