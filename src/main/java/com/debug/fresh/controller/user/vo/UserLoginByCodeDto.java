package com.debug.fresh.controller.user.vo;

import lombok.Data;

@Data
public class UserLoginByCodeDto {
    String phone;
    String code;
}
