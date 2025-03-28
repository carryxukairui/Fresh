package com.debug.fresh.service.impl;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.debug.fresh.controller.user.vo.UserLoginByCodeDto;
import com.debug.fresh.mapper.SessionMapper;
import com.debug.fresh.model.Result;
import com.debug.fresh.pojo.Session;
import com.debug.fresh.service.SessionService;
import com.debug.fresh.util.MD5Util;
import com.debug.fresh.controller.user.vo.UserLoginByPasswordVo;
import com.debug.fresh.pojo.User;
import com.debug.fresh.service.UserService;
import com.debug.fresh.mapper.UserMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;
    @Resource
    private SessionService service;
    @Resource
    private SessionMapper sessionMapper;

    @Override
    public Result loginByPassword(UserLoginByPasswordVo userLoginByPasswordVo) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, userLoginByPasswordVo.getPhone()));
        if (user == null) {
            return Result.error("手机号有误");
        }
        // 检查密码是否已设置
        if (user.getPasswordHash() == null) {
            return Result.error("未设置密码,请先用验证码登录");
        }
        // 检查密码是否匹配
        if(!MD5Util.encrypt(userLoginByPasswordVo.getPassword()).equals(user.getPasswordHash()))
            return Result.error("密码错误");
        Integer userId = user.getUserId();

        // 获取设备唯一标识、IP 地址、客户端信息等
        String deviceHash = UUID.randomUUID().toString();  // 你可以根据设备的唯一标识生成哈希值
        String ipAddress = getClientIpAddress();  // 获取客户端 IP 地址 服务器根据请求自动获取的，无需客户端传递。
        String clientInfo = getClientInfo();  // 获取客户端信息

        handleMultiDeviceLogin(user.getUserId(), deviceHash);
        StpUtil.login(userId);
        service.createSession(userId,StpUtil.getTokenValue(),deviceHash,ipAddress,clientInfo);

        return Result.success("登录成功", StpUtil.getTokenValue());
    }

    @Override
    public Result<?> register(String password) {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserId, userId));
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (user.getPasswordHash() != null) {
            return Result.error("密码已设置，无需再次设置");
        }
        user.setPasswordHash(MD5Util.encrypt(password));
        userMapper.updateById(user);
        return Result.success("密码设置成功","");
    }

    @Override
    public Result loginByCode(UserLoginByCodeDto userLoginByPasswordVo) {
        String phone = userLoginByPasswordVo.getPhone();
        String code = userLoginByPasswordVo.getCode();
        if (!"123".equals(code))
            return Result.error("验证码错误");
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        // 登录自动创建账号
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setNickname("用户:"+phone);
            userMapper.insert(user);

        }

        // 获取设备唯一标识、IP 地址、客户端信息等
        String deviceHash = UUID.randomUUID().toString();  // 你可以根据设备的唯一标识生成哈希值
        String ipAddress = getClientIpAddress();  // 获取客户端 IP 地址 服务器根据请求自动获取的，无需客户端传递。
        String clientInfo = getClientInfo();  // 获取客户端信息

        handleMultiDeviceLogin(user.getUserId(), deviceHash);
        StpUtil.login(user.getUserId());

        service.createSession(user.getUserId(),StpUtil.getTokenValue(),deviceHash,ipAddress,clientInfo);
        return Result.success("登录成功", StpUtil.getTokenValue());
    }
    private void handleMultiDeviceLogin(Integer userId, String newDeviceHash) {
        // 获取当前有效会话数
        Long activeCount = sessionMapper.selectCount(new QueryWrapper<Session>()
                .eq("user_id", userId)
                .eq("is_valid", 1));

        // 已达上限时踢出最早会话
        if (activeCount >= SaManager.getConfig().getMaxLoginCount()) {
            Session oldest = sessionMapper.selectOne(new LambdaQueryWrapper<Session>()
                    .eq(Session::getUserId, userId)
                    .eq(Session::getIsValid, 1)
                    .orderByAsc(Session::getLoginSequence)
                    .last("LIMIT 1"));

            // 使旧token失效
            StpUtil.kickout(oldest.getSessionId());
            oldest.setIsValid(0);
            sessionMapper.updateById(oldest);
        }
    }




    private String getClientIpAddress() {
        // 通过请求上下文获取客户端 IP 地址
//        return request.getRemoteAddr();
        return "123-123";
    }

    private String getClientInfo() {
        // 通过请求获取客户端浏览器、操作系统等信息
//        return request.getHeader("User-Agent");
        return "User-Agent";
    }


}




