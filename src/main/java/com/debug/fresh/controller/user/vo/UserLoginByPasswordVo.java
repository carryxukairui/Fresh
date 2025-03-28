package com.debug.fresh.controller.user.vo;

import lombok.Data;

@Data
public class UserLoginByPasswordVo {
    private String phone;
    private String password;
    private String deviceHash;
}
