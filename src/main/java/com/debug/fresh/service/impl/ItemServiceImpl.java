package com.debug.fresh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.debug.fresh.pojo.Item;
import com.debug.fresh.service.ItemService;
import com.debug.fresh.mapper.ItemMapper;
import org.springframework.stereotype.Service;

/**
* @author 28611
* @description 针对表【item(物品信息表)】的数据库操作Service实现
* @createDate 2025-03-19 14:13:46
*/
@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item>
    implements ItemService{

}




