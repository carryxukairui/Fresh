package com.debug.fresh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.debug.fresh.pojo.AppVersion;
import com.debug.fresh.service.AppVersionService;
import com.debug.fresh.mapper.AppVersionMapper;
import org.springframework.stereotype.Service;

/**
* @author 28611
* @description 针对表【app_version(应用版本表)】的数据库操作Service实现
* @createDate 2025-03-19 14:13:46
*/
@Service
public class AppVersionServiceImpl extends ServiceImpl<AppVersionMapper, AppVersion>
    implements AppVersionService{

}




