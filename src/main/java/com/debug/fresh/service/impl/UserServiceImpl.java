package com.debug.fresh.service.impl;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.debug.fresh.contants.RedisConstants;
import com.debug.fresh.webSocket.WebSocketService;
import com.debug.fresh.controller.my.Response.UserInfoResponseDto;
import com.debug.fresh.controller.user.SmsService;
import com.debug.fresh.controller.user.vo.*;
import com.debug.fresh.mapper.SessionMapper;
import com.debug.fresh.model.Result;
import com.debug.fresh.pojo.Session;
import com.debug.fresh.service.MembershipService;
import com.debug.fresh.service.SessionService;
import com.debug.fresh.util.MD5Util;
import com.debug.fresh.pojo.User;
import com.debug.fresh.service.UserService;
import com.debug.fresh.mapper.UserMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.debug.fresh.contants.ErrorContants.*;
import static com.debug.fresh.contants.RedisConstants.*;
import static net.sf.jsqlparser.parser.feature.Feature.update;

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
    private MembershipService membershipService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result<?> loginByPassword(UserLoginByPasswordVo userLoginByPasswordVo) {
        String phone = userLoginByPasswordVo.getPhone();

        if (!phone.matches("^\\+86[1-9]\\d{10}$")) {
            return Result.error("手机号格式不正确，必须以 +86 开头，且为 11 位有效手机号");
        }
        String purePhone = phone.substring(3);
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone,purePhone));
        if (user == null) {
            return Result.error("未注册");
        }
        if (user.getPasswordHash() == null) {
            return Result.error("未设置密码,请先用验证码登录");
        }
        if(!MD5Util.encrypt(userLoginByPasswordVo.getPassword()).equals(user.getPasswordHash())){
            return Result.error("密码错误");
        }

        Integer userId = user.getUserId();

        // 获取设备唯一标识、IP 地址、客户端信息等
        String deviceHash = userLoginByPasswordVo.getDeviceHash();
        String ipAddress = getClientIpAddress();
        String clientInfo = getClientInfo();
        StpUtil.login(userId);
        handleMultiDeviceLogin(user.getUserId(), deviceHash);
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
    public Result<?> loginByCode(UserLoginByCodeDto userLoginByCodeVo) {
        String phone = userLoginByCodeVo.getPhone();
        String code = userLoginByCodeVo.getCode();
        String returnData = "老用户";

        if (!phone.matches("^\\+86[1-9]\\d{10}$")) {
            return Result.error("手机号格式不正确，必须以 +86 开头，且为 11 位有效手机号");
        }
        String purePhone = phone.substring(3);
        String storedCode = smsService.getStoredCode(purePhone);
        if (storedCode == null || !storedCode.equals(code)) {
            return Result.error("验证码错误或已过期");
        }

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, purePhone));
        if (user == null) {
            user = new User();
            user.setPhone(purePhone);
            user.setNickname("用户:" + purePhone);
            userMapper.insert(user);
            returnData = "新用户";
        }

        // 获取设备唯一标识、IP 地址、客户端信息
        String deviceHash = userLoginByCodeVo.getDeviceHash();
        String ipAddress = getClientIpAddress();
        String clientInfo = getClientInfo();


        StpUtil.login(user.getUserId());
        // 处理多设备登录
        handleMultiDeviceLogin(user.getUserId(), deviceHash);

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
        if(!StpUtil.isLogin()){
            return Result.error("未登录");
        }
        Integer userId = logoutVo.getUserId();
        String deviceHash = logoutVo.getDeviceHash();

        if (userId == null || deviceHash == null) {
            return Result.error("userId和deviceHash不能为空");
        }

        boolean success = service.logoutDevice(deviceHash);
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

        // 生成新的密码版本
        String newPasswordVersion  = UUID.randomUUID().toString();
        String passwordVersionKey = USER_PWD_VERSION_PREFIX + userId;
        // 更新 Redis 密码版本
        stringRedisTemplate.opsForValue().set(passwordVersionKey, newPasswordVersion);

        return Result.success("修改密码成功");
    }

    private void handleMultiDeviceLogin(Integer userId, String newDeviceHash) {
        // 用户登录成功后
        String passwordVersionKey = USER_PWD_VERSION_PREFIX + userId;
        String currentPasswordVersion = stringRedisTemplate.opsForValue().get(passwordVersionKey);
        System.out.println("当前密码版本：" + currentPasswordVersion);
        if (currentPasswordVersion == null) {
            currentPasswordVersion = UUID.randomUUID().toString();
            stringRedisTemplate.opsForValue().set(passwordVersionKey, currentPasswordVersion);
        }
        StpUtil.getSession().set("password_version", currentPasswordVersion);

        String sessionKey = USER_SESSIONS_PREFIX + userId;
        String newToken = StpUtil.getTokenValue();
        // 获取当前用户的已登录设备
        List<String> sessions = stringRedisTemplate.opsForList().range(sessionKey, 0, -1);
        if (sessions != null && sessions.size() >= 3) {
            String oldestToken = sessions.get(0);
            kickDeviceSession(userId, oldestToken);
            stringRedisTemplate.opsForList().remove(sessionKey, 1, oldestToken);
        }

        stringRedisTemplate.opsForList().rightPush(sessionKey, newToken);
        stringRedisTemplate.expire(sessionKey, 30, TimeUnit.DAYS);

    }

    public void kickDeviceSession(Integer userId, String targetToken) {
        String kickoutKey = USER_KICKOUT_PREFIX + userId + ":" + targetToken;
        stringRedisTemplate.opsForValue().set(kickoutKey, "true");
        Session session = sessionMapper.selectOne(new LambdaQueryWrapper<Session>()
                .eq(Session::getUserId, userId)
                .eq(Session::getIsValid, 1)
                .eq(Session::getToken, targetToken));
        if (session != null){
            session.setIsValid(0);
            sessionMapper.updateById(session);
        }
    }
    /**
     * 每天凌晨 00:00 触发，更新用户的使用天数
     */
    @Override
    @Scheduled(cron = "0 0 0 * * ?") // 每天 00:00 触发
    public void updateDaysUsed() {
        int updatedRows = userMapper.incrementDaysUsedForAllUsers();
        System.out.println("成功更新 " + updatedRows + " 名活跃用户的使用天数！");
    }

    @Override
    public Result<?> queryUserInfo() {
        Integer userId ;
        try {
            userId = StpUtil.getLoginIdAsInt();
        } catch (Exception e) {
            return Result.error(USER_NOT_LOGGED_IN);
        }
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserId, userId));
        if (user == null) {
            return Result.error("用户不存在");
        }
        UserInfoResponseDto userInfoResponseDto = new UserInfoResponseDto();
        userInfoResponseDto.setUserId(user.getUserId());
        userInfoResponseDto.setUserName(user.getNickname());
        userInfoResponseDto.setPhoto(user.getAvatarUrl());
       userInfoResponseDto.setUseDate(user.getDaysUsed()+"天");
       //是否是会员
        String remainTime = membershipService.remainTime(userId);
        userInfoResponseDto.setMerDate(remainTime);

        return Result.success("查询信息成功", userInfoResponseDto);
    }



    /*
     * 修改用户名
     */
    @Override
    public Result<?> modifyName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Result.error(USER_NAME_NOT_NULL);
        }
        if (name.length() > 12) {
            return Result.error(USER_NAME_TOO_LONG);
        }
        Integer userId;
        try {
            userId = StpUtil.getLoginIdAsInt();
        } catch (Exception e) {
            return Result.error(USER_NOT_LOGGED_IN);
        }
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserId, userId));
        if (user==null){
            return Result.error(USER_NOT_EXIST);
        }
        user.setNickname(name);
        int updateById = userMapper.updateById(user);

        if (updateById > 0) {
            return Result.success(USER_NAME_MODIFY_SUCCESS);
        } else {
            return Result.error(USER_NAME_MODIFY_FAIL);
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




