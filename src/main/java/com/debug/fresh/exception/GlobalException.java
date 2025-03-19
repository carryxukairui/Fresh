package com.debug.fresh.exception;

import com.debug.fresh.model.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler
    public Result<?> globalException(Exception e){
        return Result.error(e.getMessage());
    }

}
