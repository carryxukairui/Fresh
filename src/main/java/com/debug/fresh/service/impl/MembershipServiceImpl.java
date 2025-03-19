package com.debug.fresh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.debug.fresh.pojo.Membership;
import com.debug.fresh.service.MembershipService;
import com.debug.fresh.mapper.MembershipMapper;
import org.springframework.stereotype.Service;

/**
* @author 28611
* @description 针对表【membership(会员信息表)】的数据库操作Service实现
* @createDate 2025-03-19 14:13:46
*/
@Service
public class MembershipServiceImpl extends ServiceImpl<MembershipMapper, Membership>
    implements MembershipService{

}




