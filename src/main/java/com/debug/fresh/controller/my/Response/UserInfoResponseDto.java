package com.debug.fresh.controller.my.Response;


import lombok.Data;

@Data
public class UserInfoResponseDto {
    private Integer userId;
    private String userName;
    private String photo;
    private String merDate;
    private String useDate;
}
