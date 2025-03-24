package com.debug.fresh.mapper;

import com.debug.fresh.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

/**
* @author 28611
* @description 针对表【user(用户主表)】的数据库操作Mapper
* @createDate 2025-03-19 14:13:46
* @Entity com.debug.fresh.pojo.User
*/
public interface UserMapper extends BaseMapper<User> {

    /**
     * 更新最近登录过的用户的使用天数
     */
    @Update("UPDATE `user` SET `days_used` = `days_used` + 1")
    int incrementDaysUsedForAllUsers();

}




