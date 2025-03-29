package com.debug.fresh.controller.user;



import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.aliyun.teaopenapi.models.Config;

import java.util.Random;
import java.util.concurrent.TimeUnit;



@Service
@Slf4j
public class SmsService {
    @Value("${sms.accessKeyId}")
    private String accessKeyId;

    @Value("${sms.accessKeySecret}")
    private String accessKeySecret;

    @Value("${sms.signName}")
    private String signName;

    @Value("${sms.templateCode}")
    private String templateCode;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String SMS_CODE_PREFIX = "SMS_CODE_";

    private static final long TIMESTAMP_THRESHOLD = 60 * 1000; // 60s

    private static final String SECRET_KEY = "fresh_code_secret_key";

    public SmsService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 创建阿里云短信 Client
     */
    private Client createClient() throws Exception {
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint("dysmsapi.aliyuncs.com");
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    /**
     * 生成并存储验证码
     */
    public int sendSms(String phone,long timestamp,String sign) {
        long currentTimestamp = System.currentTimeMillis();
        // 检查时间戳是否过期
//        if (Math.abs(currentTimestamp - timestamp) > TIMESTAMP_THRESHOLD) {
//            return -1;
//        }

        // 计算签名并比
        // 计算签名并比对
//        String expectedSign = DigestUtils.md5Hex(phone + timestamp + SECRET_KEY);
        String expectedSign = phone  + SECRET_KEY;
        if (!expectedSign.equals(phone+sign)) {
            return -2;
        }

        String code = String.valueOf(new Random().nextInt(900000) + 100000);
        try {
            com.aliyun.dysmsapi20170525.Client client = createClient();
            SendSmsRequest request = new SendSmsRequest()
                    .setSignName(signName)
                    .setTemplateCode(templateCode)
                    .setPhoneNumbers(phone)
                    .setTemplateParam("{\"code\":\"" + code + "\"}");

            client.sendSms(request);
            log.info("短信验证码发送成功: 手机号: {}, 验证码: {}", phone, code);

            // 存入 Redis，有效期 5 分钟
            redisTemplate.opsForValue().set(SMS_CODE_PREFIX + phone, code, 5, TimeUnit.MINUTES);
            return 1;
        } catch (Exception e) {
            log.error("短信发送异常", e);
            throw new RuntimeException("短信发送异常");
        }
    }

    /**
     * 获取存储的验证码
     */
    public String getStoredCode(String phone) {
        return redisTemplate.opsForValue().get(SMS_CODE_PREFIX + phone);
    }

    /**
     * 删除验证码（成功登录后调用）
     */
    public void deleteCode(String phone) {
        redisTemplate.delete(SMS_CODE_PREFIX + phone);
    }
}
