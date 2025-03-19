package com.debug.fresh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.debug.fresh.pojo.Session;
import com.debug.fresh.service.SessionService;
import com.debug.fresh.mapper.SessionMapper;
import org.springframework.stereotype.Service;

/**
* @author 28611
* @description 针对表【session(登录会话表)】的数据库操作Service实现
* @createDate 2025-03-19 14:13:46
*/
@Service
public class SessionServiceImpl extends ServiceImpl<SessionMapper, Session>
    implements SessionService{

}




