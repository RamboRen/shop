-- =====================================================================
-- 商品规格、优惠券表及菜单增量升级脚本（MySQL 8.0+）
-- 适用场景：已有 v1.1/v1.2 数据库，需要保留原业务数据新增
--           “规格管理”“优惠券管理”数据表、菜单与接口权限。
-- 脚本可重复执行，不会覆盖已有表、菜单和角色分配。
-- 使用前请备份数据库。
-- =====================================================================

USE `shop_admin`;

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `shop_product_spec`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        VARCHAR(50)  NOT NULL COMMENT '规格名称',
    `spec_values` TEXT         NOT NULL COMMENT '规格值 JSON 数组',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0 禁用，1 启用',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序值',
    `remark`      VARCHAR(200)          DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除，1 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_shop_product_spec_name` (`name`),
    KEY `idx_shop_product_spec_status_sort` (`status`, `sort`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '商品规格表';

CREATE TABLE IF NOT EXISTS `shop_coupon`
(
    `id`               BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `coupon_code`      VARCHAR(32)    NOT NULL COMMENT '优惠券编码',
    `name`             VARCHAR(100)   NOT NULL COMMENT '优惠券名称',
    `coupon_type`      VARCHAR(20)    NOT NULL COMMENT '类型：AMOUNT 满减券，DISCOUNT 折扣券',
    `discount_value`   DECIMAL(10, 2) NOT NULL COMMENT '满减金额或折扣值',
    `threshold_amount` DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '使用门槛，0 表示无门槛',
    `total_count`      INT            NOT NULL DEFAULT 0 COMMENT '发行总量',
    `received_count`   INT            NOT NULL DEFAULT 0 COMMENT '已领取数量',
    `used_count`       INT            NOT NULL DEFAULT 0 COMMENT '已使用数量',
    `valid_start_time` DATETIME       NOT NULL COMMENT '有效期开始时间',
    `valid_end_time`   DATETIME       NOT NULL COMMENT '有效期结束时间',
    `status`           TINYINT        NOT NULL DEFAULT 1 COMMENT '状态：0 禁用，1 启用',
    `description`      VARCHAR(500)            DEFAULT NULL COMMENT '使用说明',
    `create_time`      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          TINYINT        NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除，1 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_shop_coupon_code` (`coupon_code`),
    KEY `idx_shop_coupon_type_status` (`coupon_type`, `status`),
    KEY `idx_shop_coupon_valid_end` (`valid_end_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '优惠券表';

INSERT IGNORE INTO `shop_product_spec` (`id`, `name`, `spec_values`, `status`, `sort`, `remark`,
                                        `create_time`, `update_time`, `deleted`)
VALUES (1, '颜色', '["黑色","白色","蓝色"]', 1, 1, '商品颜色规格', NOW(), NOW(), 0),
       (2, '尺寸', '["S","M","L","XL"]', 1, 2, '服装尺寸规格', NOW(), NOW(), 0),
       (3, '容量', '["128GB","256GB","512GB"]', 1, 3, '数码产品容量规格', NOW(), NOW(), 0);

INSERT IGNORE INTO `shop_coupon` (`id`, `coupon_code`, `name`, `coupon_type`, `discount_value`,
                                  `threshold_amount`, `total_count`, `received_count`, `used_count`,
                                  `valid_start_time`, `valid_end_time`, `status`, `description`,
                                  `create_time`, `update_time`, `deleted`)
VALUES (1, 'CP20260001', '新人满减券', 'AMOUNT', 20.00, 100.00, 1000, 260, 120,
        DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 20 DAY), 1,
        '满 100 元减 20 元，新用户专享。', NOW(), NOW(), 0),
       (2, 'CP20260002', '全场折扣券', 'DISCOUNT', 9.50, 0.00, 500, 80, 35,
        DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), 1,
        '全场 9.5 折，无使用门槛。', NOW(), NOW(), 0),
       (3, 'CP20260003', '节日满减券', 'AMOUNT', 50.00, 300.00, 200, 20, 5,
        DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY), 1,
        '满 300 元减 50 元，节日活动专用。', NOW(), NOW(), 0);

INSERT IGNORE INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `route_path`, `component`,
                               `icon`, `permission_code`, `sort`, `visible`, `status`,
                               `create_time`, `update_time`, `deleted`)
SELECT 2, '规格管理', 'MENU', '/product/spec', 'product/spec/index',
       NULL, 'product:spec', 3, 1, 1, NOW(), NOW(), 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1
                  FROM `sys_menu`
                  WHERE `route_path` = '/product/spec')
  AND EXISTS (SELECT 1
              FROM `sys_menu`
              WHERE `id` = 2
                AND `deleted` = 0);

INSERT IGNORE INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `route_path`, `component`,
                               `icon`, `permission_code`, `sort`, `visible`, `status`,
                               `create_time`, `update_time`, `deleted`)
SELECT 2, '优惠券管理', 'MENU', '/product/coupon', 'product/coupon/index',
       NULL, 'product:coupon', 4, 1, 1, NOW(), NOW(), 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1
                  FROM `sys_menu`
                  WHERE `route_path` = '/product/coupon')
  AND EXISTS (SELECT 1
              FROM `sys_menu`
              WHERE `id` = 2
                AND `deleted` = 0);

INSERT IGNORE INTO `sys_role_menu` (`role_code`, `menu_id`, `create_time`, `update_time`)
SELECT 'SUPER_ADMIN', `id`, NOW(), NOW()
FROM `sys_menu`
WHERE `deleted` = 0;

INSERT IGNORE INTO `sys_role_menu` (`role_code`, `menu_id`, `create_time`, `update_time`)
SELECT 'ADMIN', `id`, NOW(), NOW()
FROM `sys_menu`
WHERE `route_path` IN ('/product/spec', '/product/coupon')
  AND `deleted` = 0;
