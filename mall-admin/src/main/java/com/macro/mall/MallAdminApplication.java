package com.macro.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 应用启动入口
 * Created by macro on 2018/4/26.
 */
@SpringBootApplication
public class MallAdminApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(MallAdminApplication.class, args);
    }
}
