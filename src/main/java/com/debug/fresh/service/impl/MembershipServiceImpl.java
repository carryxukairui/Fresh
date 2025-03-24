package com.debug.fresh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.debug.fresh.pojo.Membership;
import com.debug.fresh.service.MembershipService;
import com.debug.fresh.mapper.MembershipMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
* @author 28611
* @description 针对表【membership(会员信息表)】的数据库操作Service实现
* @createDate 2025-03-19 14:13:46
*/
@Service
public class MembershipServiceImpl extends ServiceImpl<MembershipMapper, Membership>
    implements MembershipService{

    @Resource
    private MembershipMapper membershipMapper;
    @Override
    public String remainTime(Integer userid) {
        List<Membership> memberships = membershipMapper.selectList(new LambdaQueryWrapper<Membership>()
                .eq(Membership::getUserId, userid));
        if (memberships.isEmpty()) {
            return null;
        }
        // 计算最早的开始时间（成为会员的日期）
        Date earliestStartDate = memberships.stream()
                .map(Membership::getStartDate)
                .min(Date::compareTo)
                .orElse(null);
        // 计算最晚的到期时间（会员到期日期）
        Date latestEndDate = memberships.stream()
                .map(Membership::getEndDate)
                .max(Date::compareTo)
                .orElse(null);

        if (latestEndDate == null || latestEndDate.getTime() <= System.currentTimeMillis()) {
            return "已过期";
        }
        // 计算成为会员的天数
        long membershipDays = (System.currentTimeMillis() - earliestStartDate.getTime()) / (1000 * 60 * 60 * 24);

        // 计算剩余时间
        long remainTimeMillis = latestEndDate.getTime() - System.currentTimeMillis();
        long remainDays = remainTimeMillis / (1000 * 60 * 60 * 24); // 转换成天数
        long remainMonths = remainDays / 30; // 近似计算为月数

        String remainingTimeDisplay;
        if (remainDays < 30) {
            remainingTimeDisplay = "剩余 " + remainDays + " 天";
        } else if (remainDays < 180) {
            remainingTimeDisplay = "剩余 " + remainMonths + " 个月";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            remainingTimeDisplay = "到期日期：" + sdf.format(latestEndDate);
        }

        return remainingTimeDisplay + " | 成为会员 " + membershipDays + " 天";
    }

    @Transactional
    public synchronized void addMembership(Integer userId, Integer type, int days) {
        // 1. 查询该用户的最新会员信息，并锁定该行
        Membership latestMembership = membershipMapper.selectOne(new LambdaQueryWrapper<Membership>()
                .eq(Membership::getUserId, userId)
                .orderByDesc(Membership::getEndDate)
                .last("LIMIT 1 FOR UPDATE"));  // 🔒 锁住查询到的行，避免并发问题

        Date startDate;
        if (latestMembership != null && latestMembership.getEndDate().after(new Date())) {
            // 续期：从最新 `endDate` 开始
            startDate = latestMembership.getEndDate();
        } else {
            // 新开会员：从当前时间开始
            startDate = new Date();
        }

        // 计算 `endDate`
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        Date endDate = calendar.getTime();

        // 2. 插入新的会员记录
        Membership newMembership = new Membership();
        newMembership.setUserId(userId);
        newMembership.setType(type);
        newMembership.setStartDate(startDate);
        newMembership.setEndDate(endDate);
//        newMembership.setSource(type == 4 ? "invitation" : "purchase");

        membershipMapper.insert(newMembership);
    }


}




