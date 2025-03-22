package com.debug.fresh.mapper;

import com.debug.fresh.pojo.Session;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 28611
* @description 针对表【session(登录会话表)】的数据库操作Mapper
* @createDate 2025-03-20 22:13:38
* @Entity com.debug.fresh.pojo.Session
*/
public interface SessionMapper extends BaseMapper<Session> {

    Integer selectMaxLoginSequence(Long userId);
}




