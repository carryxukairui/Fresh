package com.debug.fresh.controller.user.vo;

import lombok.Data;

@Data
public class UserLoginByCodeDto {
    private String phone;
    private String code;
    private String deviceHash;
}
