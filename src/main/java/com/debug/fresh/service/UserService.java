package com.debug.fresh.service;

import com.debug.fresh.controller.user.vo.UserDto;
import com.debug.fresh.model.Result;
import com.debug.fresh.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 28611
* @description 针对表【user(用户主表)】的数据库操作Service
* @createDate 2025-03-19 14:13:46
*/
public interface UserService extends IService<User> {

    Result login(UserDto userDto);
}
