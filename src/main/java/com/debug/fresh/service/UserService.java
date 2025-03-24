package com.debug.fresh.service;

import com.debug.fresh.controller.user.vo.*;
import com.debug.fresh.model.Result;
import com.debug.fresh.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.scheduling.annotation.Scheduled;

/**
* @author 28611
* @description 针对表【user(用户主表)】的数据库操作Service
* @createDate 2025-03-19 14:13:46
*/
public interface UserService extends IService<User> {

    Result loginByPassword(UserLoginByPasswordVo userLoginByPasswordVo);

    Result<?> register(String password);

    Result loginByCode(UserLoginByCodeDto userLoginByPasswordVo);

    Result<?> sendCode(CodeVo code);

    Result<?> logout(UserLogoutVo logoutVo);

    Result<?> renewPassword(UserRenewPasswordVo userRenewPasswordVo);

    /**
     * 每天凌晨 00:00 触发，更新活跃用户的使用天数
     */
    void updateDaysUsed();

    Result<?> queryUserInfo();
}
