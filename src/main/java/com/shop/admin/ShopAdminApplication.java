package com.shop.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * 商城后台管理系统启动类。
 *
 * @author shop-admin
 */
@SpringBootApplication
@ConfigurationPropertiesScan("com.shop.admin")
public class ShopAdminApplication {

    /**
     * 应用程序入口。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(ShopAdminApplication.class, args);
    }
}
