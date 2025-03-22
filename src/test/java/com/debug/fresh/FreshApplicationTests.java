package com.debug.fresh;

import com.debug.fresh.util.MD5Util;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FreshApplicationTests {

    @Test
    void contextLoads() {
        String password = "123";
        String encrypt = MD5Util.encrypt(password);
        System.out.println(encrypt);
        String p = "123";
        System.out.println(MD5Util.encrypt(p));
        boolean equals = MD5Util.encrypt(p).equals(encrypt);
        System.out.println(equals);
    }
    git checkout --orphan newb


}
