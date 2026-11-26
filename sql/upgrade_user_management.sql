-- =====================================================================
-- 用户管理增量升级脚本（MySQL 8.0+）
-- 适用场景：已有 v1.4.0 数据库，需要新增商城用户表、用户管理菜单与接口权限。
--           脚本可重复执行，表、菜单与角色关联均使用幂等写法。
-- 使用前请备份数据库。
-- =====================================================================

USE `shop_admin`;

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `shop_user`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键，同时作为订单会员编号',
    `user_no`         VARCHAR(32)  NOT NULL COMMENT '用户编号',
    `username`        VARCHAR(30)  NOT NULL COMMENT '登录账号',
    `nickname`        VARCHAR(50)  NOT NULL COMMENT '昵称',
    `phone`           VARCHAR(20)           DEFAULT NULL COMMENT '手机号',
    `email`           VARCHAR(100)          DEFAULT NULL COMMENT '邮箱',
    `avatar`          VARCHAR(255)          DEFAULT NULL COMMENT '头像地址',
    `gender`          VARCHAR(10)  NOT NULL DEFAULT 'UNKNOWN' COMMENT '性别：UNKNOWN/MALE/FEMALE',
    `birthday`        DATE                  DEFAULT NULL COMMENT '出生日期',
    `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0 禁用，1 启用',
    `last_login_time` DATETIME              DEFAULT NULL COMMENT '最近登录时间',
    `remark`          VARCHAR(200)          DEFAULT NULL COMMENT '备注',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除，1 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_shop_user_no` (`user_no`),
    UNIQUE KEY `uk_shop_user_username` (`username`),
    UNIQUE KEY `uk_shop_user_phone` (`phone`),
    KEY `idx_shop_user_status_create` (`status`, `create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '商城用户表';

INSERT IGNORE INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `route_path`, `component`,
                               `icon`, `permission_code`, `sort`, `visible`, `status`,
                               `create_time`, `update_time`, `deleted`)
VALUES (12, 0, '用户管理', 'MENU', '/user', 'user/index', 'User', 'user:view',
        5, 1, 1, NOW(), NOW(), 0);

UPDATE `sys_menu` SET `sort` = 6 WHERE `route_path` = '/system' AND `deleted` = 0;

INSERT IGNORE INTO `sys_role_menu` (`role_code`, `menu_id`, `create_time`, `update_time`)
SELECT 'SUPER_ADMIN', `id`, NOW(), NOW()
FROM `sys_menu`
WHERE `route_path` = '/user'
  AND `deleted` = 0;

INSERT IGNORE INTO `sys_role_menu` (`role_code`, `menu_id`, `create_time`, `update_time`)
SELECT 'ADMIN', `id`, NOW(), NOW()
FROM `sys_menu`
WHERE `route_path` = '/user'
  AND `deleted` = 0;
