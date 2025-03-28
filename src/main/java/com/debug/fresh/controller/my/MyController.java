package com.debug.fresh.controller.my;

import cn.dev33.satoken.stp.StpUtil;
import com.debug.fresh.model.Result;
import com.debug.fresh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("my")
@Controller
public class MyController {
    @Autowired
    private UserService userService;

    @GetMapping("/info")
    public Result<?> getUserInfo() {
        return userService.queryUserInfo();
    }

    @PutMapping("/modifyName")
    public Result<?> modifyName(@RequestParam("name") String name) {
        return userService.modifyName(name);
    }


}
