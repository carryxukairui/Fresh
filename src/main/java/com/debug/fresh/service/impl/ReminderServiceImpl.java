package com.debug.fresh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.debug.fresh.pojo.Reminder;
import com.debug.fresh.service.ReminderService;
import com.debug.fresh.mapper.ReminderMapper;
import org.springframework.stereotype.Service;

/**
* @author 28611
* @description 针对表【reminder(过期提醒表)】的数据库操作Service实现
* @createDate 2025-03-19 14:13:46
*/
@Service
public class ReminderServiceImpl extends ServiceImpl<ReminderMapper, Reminder>
    implements ReminderService{

}




