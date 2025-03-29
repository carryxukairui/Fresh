package com.debug.fresh.model;

import com.debug.fresh.contants.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.debug.fresh.contants.ResultCode.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    // 新增带状态码的静态方法
    public static <T> Result<T> custom(int code, String msg, T data) {
        return new Result<>(code, msg, data);
    }

    // 快速构建被踢出响应
    public static <T> Result<T> kickOut(String msg) {
        return custom(ResultCode.KICK_OUT, msg, null);
    }

    // 快速构建密码修改响应
    public static <T> Result<T> passwordChanged(String msg) {
        return custom(ResultCode.PWD_CHANGED, msg, null);
    }


    // 保持原有方法...
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS, "", data);
    }
    public static <T> Result<T> error(String msg) {
        return new Result<T>(ERRORS, msg, null);
    }
    //查询 成功响应
    public static <T> Result<T> success(String msg ,T data) {
        return new Result<T>(ResultCode.SUCCESS, msg, data);
    }
    public static <T> Result<T> noUseToken(String msg) {
        return new Result<T>(INVALID_TOKEN, msg, null);
    }
    public static <T> Result<T> notLogin(String msg){return new Result<>(NOT_LOGIN,msg,null);}
}