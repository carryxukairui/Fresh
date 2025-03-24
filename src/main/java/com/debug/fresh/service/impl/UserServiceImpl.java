package com.debug.fresh.service.impl;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.debug.fresh.config.WebSocketService;
import com.debug.fresh.controller.user.SmsService;
import com.debug.fresh.controller.user.vo.*;
import com.debug.fresh.mapper.SessionMapper;
import com.debug.fresh.model.Result;
import com.debug.fresh.pojo.Session;
import com.debug.fresh.service.SessionService;
import com.debug.fresh.util.MD5Util;
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
    @Resource
    private SmsService smsService;
    @Resource
    private WebSocketService webSocketService;

    @Override
    public Result loginByPassword(UserLoginByPasswordVo userLoginByPasswordVo) {
        String phone = userLoginByPasswordVo.getPhone();
        // 校验手机号格式，必须是 +86 开头，并且是 11 位中国手机号
        if (!phone.matches("^\\+86[1-9]\\d{10}$")) {
            return Result.error("手机号格式不正确，必须以 +86 开头，且为 11 位有效手机号");
        }
        String purePhone = phone.substring(3);
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone,purePhone));
        if (user == null) {
            return Result.error("未注册");
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
    public Result loginByCode(UserLoginByCodeDto userLoginByCodeVo) {
        String phone = userLoginByCodeVo.getPhone();
        String code = userLoginByCodeVo.getCode();
        String returnData = "老用户";
        // 校验手机号格式，必须是 +86 开头，并且是 11 位中国手机号
        if (!phone.matches("^\\+86[1-9]\\d{10}$")) {
            return Result.error("手机号格式不正确，必须以 +86 开头，且为 11 位有效手机号");
        }
        String purePhone = phone.substring(3);
        String storedCode = smsService.getStoredCode(purePhone);
        if (storedCode == null || !storedCode.equals(code)) {
            return Result.error("验证码错误或已过期");
        }

        // 查询用户，若不存在则创建
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, purePhone));
        if (user == null) {
            user = new User();
            user.setPhone(purePhone);
            user.setNickname("用户:" + purePhone);
            userMapper.insert(user);
            returnData = "新用户";
        }

        // 获取设备唯一标识、IP 地址、客户端信息
        String deviceHash = UUID.randomUUID().toString();  // 可根据实际设备生成唯一标识
        String ipAddress = getClientIpAddress(); // 获取客户端 IP 地址
        String clientInfo = getClientInfo(); // 获取客户端信息

        // 处理多设备登录
        handleMultiDeviceLogin(user.getUserId(), deviceHash);

        StpUtil.login(user.getUserId());
        service.createSession(user.getUserId(), StpUtil.getTokenValue(), deviceHash, ipAddress, clientInfo);

        smsService.deleteCode(purePhone);
        return Result.success("登录成功", returnData);
    }

    @Override
    public Result<?> sendCode(CodeVo codeVo) {
        String phone = codeVo.getPhone();
        if (!phone.matches("^[1-9]\\d{10}$")) {
            return Result.error("手机号格式不正确，需 11 位有效手机号");
        }
        int flag = smsService.sendSms(phone, codeVo.getTimestamp(), codeVo.getSign());
        if (flag==-1){
            return Result.error("请求超时，请重试");
        }else if (flag==-2){
            return Result.error("非法请求");
        }
        return Result.success("验证码发送成功");
    }

    @Override
    public Result<?> logout(UserLogoutVo logoutVo) {
        if(!StpUtil.isLogin())
            return Result.error("未登录");
        Integer userId = logoutVo.getUserId();
        String deviceHash = logoutVo.getDeviceHash();

        if (userId == null || deviceHash == null) {
            return Result.error("userId和deviceHash不能为空");
        }

        boolean success = service.logoutDevice(deviceHash);  // 这里调用 sessionService 来处理
        if (success) {
            return Result.success("注销成功");
        } else {
            return Result.error("注销失败");
        }
    }

    @Override
    public Result<?> renewPassword(UserRenewPasswordVo userRenewPasswordVo) {
        String nowPassword =MD5Util.encrypt(userRenewPasswordVo.getPassword());
        Integer userId = userRenewPasswordVo.getUserId();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserId, userId));
        if (user == null) {
            return Result.error("用户不存在");
        }
        String oldPasswordHash = user.getPasswordHash();
        if (user.getPasswordHash() == null) {
            return Result.error("未设置密码");
        }
        if (nowPassword.equals(oldPasswordHash)){
            return Result.error("新密码不能与旧密码一致");
        }
        user.setPasswordHash(nowPassword);
        userMapper.updateById(user);

        //所有设备退出登录
        service.invalidateUserSessions(userId);
        return Result.success("修改密码成功");
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

            if (oldest != null) {
                String token = oldest.getToken();
                // **通过 WebSocket 通知被踢出的设备**
                webSocketService.sendMessageToUser(token, "您的账号已在其他设备上登录，如非本人操作，请修改密码！");

                // 使旧token失效
                StpUtil.kickoutByTokenValue(token);
                oldest.setIsValid(0);
                sessionMapper.updateById(oldest);
            }
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




