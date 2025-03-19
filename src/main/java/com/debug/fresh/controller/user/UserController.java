package com.debug.fresh.controller.user;


import com.debug.fresh.controller.user.vo.UserDto;
import com.debug.fresh.model.Result;
import com.debug.fresh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @PostMapping("/login")
    public Result login(@RequestBody UserDto userDto){
        return userService.login(userDto);
    }
}
