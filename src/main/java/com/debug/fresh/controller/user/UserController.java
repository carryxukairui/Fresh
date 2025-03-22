package com.debug.fresh.controller.user;


import com.debug.fresh.controller.user.vo.CodeVo;
import com.debug.fresh.controller.user.vo.UserLoginByCodeDto;
import com.debug.fresh.controller.user.vo.UserLoginByPasswordVo;
import com.debug.fresh.model.Result;
import com.debug.fresh.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @PostMapping("/loginByPassword")
    public Result loginByPassword(@RequestBody UserLoginByPasswordVo userLoginByPasswordVo){
        return userService.loginByPassword(userLoginByPasswordVo);
    }

    @PostMapping("/loginByCode")
    public Result loginByCode(@RequestBody UserLoginByCodeDto userLoginByPasswordVo){
        return userService.loginByCode(userLoginByPasswordVo);
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody UserLoginByPasswordVo userLoginByPasswordVo) {
        return userService.register(userLoginByPasswordVo.getPassword());
    }

    @PostMapping("/code")
    public Result<?> sendCode(@RequestBody CodeVo codeVo) {
        return userService.sendCode(codeVo);
    }


}
