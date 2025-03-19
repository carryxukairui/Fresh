package com.debug.fresh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.debug.fresh.model.Result;
import com.debug.fresh.util.ResultCodeEnum;
import com.debug.fresh.controller.user.vo.UserDto;
import com.debug.fresh.pojo.User;
import com.debug.fresh.service.UserService;
import com.debug.fresh.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
* @author 28611
* @description 针对表【user(用户主表)】的数据库操作Service实现
* @createDate 2025-03-19 14:13:46
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;
    @Override
    public Result login(UserDto userDto) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, userDto.getPhone()));
        if (user == null){
            return Result.error("账号有误");
        }
        if (!user.getPasswordHash().equals(userDto.getPassword())){
            return Result.error("密码错误");
        }
        return Result.success("登录成功");
    }
}




