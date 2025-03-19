package com.debug.fresh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.debug.fresh.pojo.PaymentOrder;
import com.debug.fresh.service.PaymentOrderService;
import com.debug.fresh.mapper.PaymentOrderMapper;
import org.springframework.stereotype.Service;

/**
* @author 28611
* @description 针对表【payment_order(支付订单表)】的数据库操作Service实现
* @createDate 2025-03-19 14:13:46
*/
@Service
public class PaymentOrderServiceImpl extends ServiceImpl<PaymentOrderMapper, PaymentOrder>
    implements PaymentOrderService{

}




