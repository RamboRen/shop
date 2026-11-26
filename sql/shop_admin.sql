-- =====================================================================
-- 商城后台管理系统初始化脚本（MySQL 8.0+）
-- 注意：脚本会先删除已有业务表，请仅在全新环境或确认可重置时执行。
-- 默认超级管理员：admin / admin123
-- 默认普通管理员：operator / admin123
-- 内置动态菜单与角色菜单权限，普通管理员默认不含系统管理菜单
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `shop_admin`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE `shop_admin`;

SET NAMES utf8mb4;

DROP TABLE IF EXISTS `shop_order_item`;
DROP TABLE IF EXISTS `shop_order`;
DROP TABLE IF EXISTS `shop_user`;
DROP TABLE IF EXISTS `sys_setting`;
DROP TABLE IF EXISTS `shop_coupon`;
DROP TABLE IF EXISTS `shop_product_spec`;
DROP TABLE IF EXISTS `shop_gallery_image`;
DROP TABLE IF EXISTS `shop_gallery_category`;
DROP TABLE IF EXISTS `shop_product`;
DROP TABLE IF EXISTS `shop_product_category`;
DROP TABLE IF EXISTS `sys_role_menu`;
DROP TABLE IF EXISTS `sys_menu`;
DROP TABLE IF EXISTS `sys_admin`;

CREATE TABLE `sys_admin`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`        VARCHAR(20)  NOT NULL COMMENT '登录用户名',
    `password`        VARCHAR(100) NOT NULL COMMENT 'BCrypt 密码',
    `nickname`        VARCHAR(30)  NOT NULL COMMENT '昵称',
    `avatar`          VARCHAR(255)          DEFAULT NULL COMMENT '头像地址',
    `phone`           VARCHAR(20)           DEFAULT NULL COMMENT '手机号',
    `email`           VARCHAR(100)          DEFAULT NULL COMMENT '邮箱',
    `role_code`       VARCHAR(20)  NOT NULL COMMENT '角色编码：SUPER_ADMIN/ADMIN',
    `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0 禁用，1 启用',
    `last_login_time` DATETIME              DEFAULT NULL COMMENT '最近登录时间',
    `remark`          VARCHAR(200)          DEFAULT NULL COMMENT '备注',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除，1 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_admin_username` (`username`),
    KEY `idx_sys_admin_role_status` (`role_code`, `status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '管理员表';

CREATE TABLE `sys_menu`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `parent_id`       BIGINT       NOT NULL DEFAULT 0 COMMENT '父菜单编号，0 表示顶级菜单',
    `menu_name`       VARCHAR(50)  NOT NULL COMMENT '菜单名称',
    `menu_type`       VARCHAR(20)  NOT NULL COMMENT '类型：CATALOG 目录，MENU 菜单',
    `route_path`      VARCHAR(100) NOT NULL COMMENT '前端路由地址',
    `component`       VARCHAR(150)          DEFAULT NULL COMMENT '前端组件路径',
    `icon`            VARCHAR(50)           DEFAULT NULL COMMENT '菜单图标',
    `permission_code` VARCHAR(100)          DEFAULT NULL COMMENT '接口权限编码',
    `sort`            INT          NOT NULL DEFAULT 0 COMMENT '排序值',
    `visible`         TINYINT      NOT NULL DEFAULT 1 COMMENT '是否显示：0 隐藏，1 显示',
    `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0 禁用，1 启用',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除，1 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_menu_route_path` (`route_path`),
    UNIQUE KEY `uk_sys_menu_permission_code` (`permission_code`),
    KEY `idx_sys_menu_parent_sort` (`parent_id`, `sort`),
    KEY `idx_sys_menu_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '后台菜单表';

CREATE TABLE `sys_role_menu`
(
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `role_code`   VARCHAR(20) NOT NULL COMMENT '角色编码',
    `menu_id`     BIGINT      NOT NULL COMMENT '菜单编号',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_role_menu_role_menu` (`role_code`, `menu_id`),
    KEY `idx_sys_role_menu_menu_id` (`menu_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '角色菜单关联表';

CREATE TABLE `shop_product_category`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        VARCHAR(30)  NOT NULL COMMENT '分类名称',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序值',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0 禁用，1 启用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除，1 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_product_category_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '商品分类表';

CREATE TABLE `shop_product`
(
    `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `product_code`   VARCHAR(32)   NOT NULL COMMENT '商品编码',
    `category_id`    BIGINT        NOT NULL COMMENT '分类编号',
    `name`           VARCHAR(100)  NOT NULL COMMENT '商品名称',
    `subtitle`       VARCHAR(150)           DEFAULT NULL COMMENT '副标题',
    `main_image`     VARCHAR(255)  NOT NULL COMMENT '主图地址',
    `image_urls`     TEXT                   DEFAULT NULL COMMENT '附图地址 JSON 数组',
    `price`          DECIMAL(10, 2) NOT NULL COMMENT '销售价',
    `original_price` DECIMAL(10, 2)          DEFAULT NULL COMMENT '划线原价',
    `stock`          INT           NOT NULL DEFAULT 0 COMMENT '库存',
    `sales_volume`   BIGINT        NOT NULL DEFAULT 0 COMMENT '累计销量',
    `status`         TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0 下架，1 上架',
    `sort`           INT           NOT NULL DEFAULT 0 COMMENT '排序值',
    `detail`         TEXT                   DEFAULT NULL COMMENT '商品详情',
    `create_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除，1 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_shop_product_code` (`product_code`),
    KEY `idx_shop_product_category` (`category_id`),
    KEY `idx_shop_product_status` (`status`),
    KEY `idx_shop_product_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '商品表';

CREATE TABLE `shop_product_spec`
(
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        VARCHAR(50) NOT NULL COMMENT '规格名称',
    `spec_values` TEXT        NOT NULL COMMENT '规格值 JSON 数组',
    `status`      TINYINT     NOT NULL DEFAULT 1 COMMENT '状态：0 禁用，1 启用',
    `sort`        INT         NOT NULL DEFAULT 0 COMMENT '排序值',
    `remark`      VARCHAR(200)         DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除，1 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_shop_product_spec_name` (`name`),
    KEY `idx_shop_product_spec_status_sort` (`status`, `sort`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '商品规格表';

CREATE TABLE `shop_coupon`
(
    `id`                BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `coupon_code`       VARCHAR(32)    NOT NULL COMMENT '优惠券编码',
    `name`              VARCHAR(100)   NOT NULL COMMENT '优惠券名称',
    `coupon_type`       VARCHAR(20)    NOT NULL COMMENT '类型：AMOUNT 满减券，DISCOUNT 折扣券',
    `discount_value`    DECIMAL(10, 2) NOT NULL COMMENT '满减金额或折扣值',
    `threshold_amount`  DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '使用门槛，0 表示无门槛',
    `total_count`       INT            NOT NULL DEFAULT 0 COMMENT '发行总量',
    `received_count`    INT            NOT NULL DEFAULT 0 COMMENT '已领取数量',
    `used_count`        INT            NOT NULL DEFAULT 0 COMMENT '已使用数量',
    `valid_start_time`  DATETIME       NOT NULL COMMENT '有效期开始时间',
    `valid_end_time`    DATETIME       NOT NULL COMMENT '有效期结束时间',
    `status`            TINYINT        NOT NULL DEFAULT 1 COMMENT '状态：0 禁用，1 启用',
    `description`       VARCHAR(500)            DEFAULT NULL COMMENT '使用说明',
    `create_time`       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           TINYINT        NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除，1 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_shop_coupon_code` (`coupon_code`),
    KEY `idx_shop_coupon_type_status` (`coupon_type`, `status`),
    KEY `idx_shop_coupon_valid_end` (`valid_end_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '优惠券表';

CREATE TABLE `shop_gallery_category`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `category_code` VARCHAR(30)  NOT NULL COMMENT '分类编码，图片以此编码关联分类',
    `category_name` VARCHAR(30)  NOT NULL COMMENT '分类名称',
    `sort`          INT          NOT NULL DEFAULT 0 COMMENT '排序值，数值越小越靠前',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0 禁用，1 启用',
    `remark`        VARCHAR(200) DEFAULT NULL COMMENT '备注',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除，1 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_gallery_category_code` (`category_code`),
    KEY `idx_gallery_category_sort` (`status`, `sort`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '图库分类表';

CREATE TABLE `shop_gallery_image`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `file_name`     VARCHAR(100) NOT NULL COMMENT '服务器存储文件名',
    `original_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
    `url`           VARCHAR(500) NOT NULL COMMENT '访问地址',
    `relative_path` VARCHAR(255) NOT NULL COMMENT '相对上传根目录的路径',
    `content_type`  VARCHAR(50)  NOT NULL COMMENT '媒体类型',
    `file_size`     BIGINT       NOT NULL DEFAULT 0 COMMENT '文件大小（字节）',
    `width`         INT          NOT NULL DEFAULT 0 COMMENT '宽度',
    `height`        INT          NOT NULL DEFAULT 0 COMMENT '高度',
    `image_group`   VARCHAR(20)  NOT NULL DEFAULT 'OTHER' COMMENT '分组：PRODUCT/BANNER/AVATAR/OTHER',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除，1 已删除',
    PRIMARY KEY (`id`),
    KEY `idx_gallery_group` (`image_group`),
    KEY `idx_gallery_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '图库图片表';

CREATE TABLE `shop_user`
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

CREATE TABLE `sys_setting`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `config_key`   VARCHAR(64)  NOT NULL COMMENT '配置键，全局唯一',
    `config_value` TEXT                  DEFAULT NULL COMMENT '配置值',
    `config_group` VARCHAR(20)  NOT NULL COMMENT '配置分组：BASIC/CONTACT/TRADE/PAYMENT',
    `config_name`  VARCHAR(100) NOT NULL COMMENT '配置显示名称',
    `value_type`   VARCHAR(20)  NOT NULL DEFAULT 'STRING' COMMENT '值类型：STRING/TEXT/NUMBER/BOOLEAN',
    `sort`         INT          NOT NULL DEFAULT 0 COMMENT '排序值',
    `remark`       VARCHAR(255)          DEFAULT NULL COMMENT '备注说明',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除，1 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_setting_key` (`config_key`),
    KEY `idx_sys_setting_group_sort` (`config_group`, `sort`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '系统设置表';

CREATE TABLE `shop_order`
(
    `id`               BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_no`         VARCHAR(32)    NOT NULL COMMENT '订单号',
    `user_id`          BIGINT                  DEFAULT NULL COMMENT '会员编号',
    `receiver_name`    VARCHAR(50)    NOT NULL COMMENT '收货人',
    `receiver_phone`   VARCHAR(20)    NOT NULL COMMENT '收货手机号',
    `receiver_address` VARCHAR(200)   NOT NULL COMMENT '收货地址',
    `total_amount`     DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT '商品总额',
    `freight_amount`   DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT '运费',
    `discount_amount`  DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
    `pay_amount`       DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT '应付金额',
    `pay_type`         VARCHAR(20)             DEFAULT NULL COMMENT '支付方式：WECHAT/ALIPAY/BALANCE',
    `status`           TINYINT        NOT NULL DEFAULT 0 COMMENT '状态：0 待付款，1 待发货，2 待收货，3 已完成，4 已取消',
    `item_count`       INT            NOT NULL DEFAULT 0 COMMENT '商品种类数',
    `total_quantity`   INT            NOT NULL DEFAULT 0 COMMENT '商品总数量',
    `pay_time`         DATETIME                DEFAULT NULL COMMENT '支付时间',
    `ship_time`        DATETIME                DEFAULT NULL COMMENT '发货时间',
    `finish_time`      DATETIME                DEFAULT NULL COMMENT '完成时间',
    `cancel_time`      DATETIME                DEFAULT NULL COMMENT '取消时间',
    `remark`           VARCHAR(500)            DEFAULT NULL COMMENT '订单备注',
    `create_time`      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          TINYINT        NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除，1 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_shop_order_no` (`order_no`),
    KEY `idx_shop_order_status_create` (`status`, `create_time`),
    KEY `idx_shop_order_receiver_phone` (`receiver_phone`),
    KEY `idx_shop_order_user_id` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '订单表';

CREATE TABLE `shop_order_item`
(
    `id`            BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_id`      BIGINT         NOT NULL COMMENT '订单编号',
    `product_id`    BIGINT                  DEFAULT NULL COMMENT '商品编号',
    `product_name`  VARCHAR(100)   NOT NULL COMMENT '商品名称快照',
    `product_image` VARCHAR(500)            DEFAULT NULL COMMENT '商品图片快照',
    `price`         DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品单价',
    `quantity`      INT            NOT NULL DEFAULT 1 COMMENT '购买数量',
    `total_amount`  DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT '小计金额',
    `create_time`   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT        NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除，1 已删除',
    PRIMARY KEY (`id`),
    KEY `idx_shop_order_item_order_id` (`order_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '订单明细表';

INSERT INTO `sys_admin` (`id`, `username`, `password`, `nickname`, `avatar`, `phone`, `email`,
                         `role_code`, `status`, `remark`, `create_time`, `update_time`, `deleted`)
VALUES (1, 'admin', '$2a$10$fvHa9fA/yAX9xPj5u02y/unJGH9JKQu91B.F7.ztBkUdbtBbitR3C',
        '超级管理员', NULL, '13800138000', 'admin@example.com',
        'SUPER_ADMIN', 1, '系统初始化超级管理员', NOW(), NOW(), 0),
       (2, 'operator', '$2a$10$fvHa9fA/yAX9xPj5u02y/unJGH9JKQu91B.F7.ztBkUdbtBbitR3C',
        '运营管理员', NULL, '13900139000', 'operator@example.com',
        'ADMIN', 1, '商品与图库运营账号', NOW(), NOW(), 0);

INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `route_path`, `component`,
                        `icon`, `permission_code`, `sort`, `visible`, `status`,
                        `create_time`, `update_time`, `deleted`)
VALUES (1, 0, '数据概览', 'MENU', '/dashboard', 'dashboard/index',
        'DataLine', 'stats:view', 1, 1, 1, NOW(), NOW(), 0),
       (2, 0, '商品管理', 'CATALOG', '/product', 'Layout',
        'Goods', NULL, 2, 1, 1, NOW(), NOW(), 0),
       (3, 2, '商品列表', 'MENU', '/product/list', 'product/list/index',
        NULL, 'product:view', 1, 1, 1, NOW(), NOW(), 0),
       (4, 2, '商品分类', 'MENU', '/product/category', 'product/category/index',
        NULL, 'product:category', 2, 1, 1, NOW(), NOW(), 0),
       (5, 0, '图库管理', 'MENU', '/gallery', 'gallery/index',
        'Picture', 'gallery:view', 3, 1, 1, NOW(), NOW(), 0),
       (11, 0, '订单管理', 'MENU', '/order', 'order/index',
        'List', 'order:view', 4, 1, 1, NOW(), NOW(), 0),
       (12, 0, '用户管理', 'MENU', '/user', 'user/index',
        'User', 'user:view', 5, 1, 1, NOW(), NOW(), 0),
       (6, 0, '系统管理', 'CATALOG', '/system', 'Layout',
        'Setting', NULL, 6, 1, 1, NOW(), NOW(), 0),
       (7, 6, '管理员管理', 'MENU', '/system/admin', 'system/admin/index',
        NULL, 'system:admin', 1, 1, 1, NOW(), NOW(), 0),
       (8, 6, '菜单权限', 'MENU', '/system/menu', 'system/menu/index',
        NULL, 'system:menu', 2, 1, 1, NOW(), NOW(), 0),
       (13, 6, '系统设置', 'MENU', '/system/setting', 'system/setting/index',
        NULL, 'system:setting', 3, 1, 1, NOW(), NOW(), 0),
       (9, 2, '规格管理', 'MENU', '/product/spec', 'product/spec/index',
        NULL, 'product:spec', 3, 1, 1, NOW(), NOW(), 0),
       (10, 2, '优惠券管理', 'MENU', '/product/coupon', 'product/coupon/index',
        NULL, 'product:coupon', 4, 1, 1, NOW(), NOW(), 0);

INSERT INTO `sys_role_menu` (`role_code`, `menu_id`, `create_time`, `update_time`)
SELECT 'SUPER_ADMIN', `id`, NOW(), NOW()
FROM `sys_menu`
WHERE `deleted` = 0;

INSERT INTO `sys_role_menu` (`role_code`, `menu_id`, `create_time`, `update_time`)
VALUES ('ADMIN', 1, NOW(), NOW()),
       ('ADMIN', 2, NOW(), NOW()),
       ('ADMIN', 3, NOW(), NOW()),
       ('ADMIN', 4, NOW(), NOW()),
       ('ADMIN', 5, NOW(), NOW()),
       ('ADMIN', 9, NOW(), NOW()),
       ('ADMIN', 10, NOW(), NOW()),
       ('ADMIN', 11, NOW(), NOW()),
       ('ADMIN', 12, NOW(), NOW());

INSERT INTO `shop_product_category` (`id`, `name`, `sort`, `status`, `create_time`, `update_time`, `deleted`)
VALUES (1, '数码家电', 1, 1, NOW(), NOW(), 0),
       (2, '服饰鞋包', 2, 1, NOW(), NOW(), 0),
       (3, '家居生活', 3, 1, NOW(), NOW(), 0),
       (4, '美妆个护', 4, 1, NOW(), NOW(), 0);

INSERT INTO `shop_product` (`id`, `product_code`, `category_id`, `name`, `subtitle`, `main_image`,
                            `image_urls`, `price`, `original_price`, `stock`, `sales_volume`,
                            `status`, `sort`, `detail`, `create_time`, `update_time`, `deleted`)
VALUES (1, 'SP20260001', 1, '无线蓝牙耳机', '主动降噪，续航 30 小时',
        '/uploads/demo/product-1.jpg', '["/uploads/demo/product-1.jpg"]',
        299.00, 399.00, 120, 156, 1, 1,
        '支持主动降噪与通透模式，单次续航 8 小时，配合充电盒可达 30 小时。',
        DATE_SUB(NOW(), INTERVAL 12 DAY), NOW(), 0),
       (2, 'SP20260002', 1, '智能运动手表', '全天候健康监测，50 米防水',
        '/uploads/demo/product-2.jpg', '["/uploads/demo/product-2.jpg"]',
        599.00, 799.00, 80, 92, 1, 2,
        '支持心率、血氧与睡眠监测，内置多种运动模式。',
        DATE_SUB(NOW(), INTERVAL 10 DAY), NOW(), 0),
       (3, 'SP20260003', 2, '轻薄羽绒服', '90% 白鸭绒，蓬松保暖',
        '/uploads/demo/product-3.jpg', '["/uploads/demo/product-3.jpg"]',
        399.00, 599.00, 60, 210, 1, 3,
        '轻量面料，收纳便携，适合通勤与旅行。',
        DATE_SUB(NOW(), INTERVAL 8 DAY), NOW(), 0),
       (4, 'SP20260004', 3, '香薰加湿器', '静音雾化，三档调节',
        '/uploads/demo/product-4.jpg', '["/uploads/demo/product-4.jpg"]',
        129.00, 199.00, 8, 75, 1, 4,
        '超声波雾化，夜间氛围灯，缺水自动断电。',
        DATE_SUB(NOW(), INTERVAL 5 DAY), NOW(), 0),
       (5, 'SP20260005', 4, '保湿精华液', '玻尿酸补水，清爽不黏腻',
        '/uploads/demo/product-5.jpg', '["/uploads/demo/product-5.jpg"]',
        199.00, 299.00, 0, 49, 0, 5,
        '多重保湿成分，适合日常补水护理。',
        DATE_SUB(NOW(), INTERVAL 3 DAY), NOW(), 0),
       (6, 'SP20260006', 3, '便携胶囊咖啡机', '一键萃取，兼容胶囊',
        '/uploads/demo/product-6.jpg', '["/uploads/demo/product-6.jpg"]',
        459.00, 599.00, 35, 38, 1, 6,
        '20 巴高压萃取，小巧便携，适合办公室与旅行。',
        NOW(), NOW(), 0);

INSERT INTO `shop_product_spec` (`id`, `name`, `spec_values`, `status`, `sort`, `remark`, `create_time`,
                                 `update_time`, `deleted`)
VALUES (1, '颜色', '["黑色","白色","蓝色"]', 1, 1, '商品颜色规格', NOW(), NOW(), 0),
       (2, '尺寸', '["S","M","L","XL"]', 1, 2, '服装尺寸规格', NOW(), NOW(), 0),
       (3, '容量', '["128GB","256GB","512GB"]', 1, 3, '数码产品容量规格', NOW(), NOW(), 0);

INSERT INTO `shop_coupon` (`id`, `coupon_code`, `name`, `coupon_type`, `discount_value`,
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

INSERT INTO `shop_gallery_category` (`id`, `category_code`, `category_name`, `sort`, `status`, `remark`,
                                     `create_time`, `update_time`, `deleted`)
VALUES (1, 'PRODUCT', '商品图', 1, 1, '商品主图与详情页图片', NOW(), NOW(), 0),
       (2, 'BANNER', '轮播图', 2, 1, '首页与活动轮播图', NOW(), NOW(), 0),
       (3, 'AVATAR', '头像', 3, 1, '用户与管理员头像', NOW(), NOW(), 0),
       (4, 'OTHER', '其他', 4, 1, '未归类的图片', NOW(), NOW(), 0);

INSERT INTO `shop_gallery_image` (`file_name`, `original_name`, `url`, `relative_path`, `content_type`,
                                  `file_size`, `width`, `height`, `image_group`, `create_time`, `update_time`,
                                  `deleted`)
VALUES ('product-1.jpg', 'product-1.jpg', '/uploads/demo/product-1.jpg', 'demo/product-1.jpg',
        'image/jpeg', 46108, 800, 800, 'PRODUCT', NOW(), NOW(), 0),
       ('product-2.jpg', 'product-2.jpg', '/uploads/demo/product-2.jpg', 'demo/product-2.jpg',
        'image/jpeg', 47642, 800, 800, 'PRODUCT', NOW(), NOW(), 0),
       ('product-3.jpg', 'product-3.jpg', '/uploads/demo/product-3.jpg', 'demo/product-3.jpg',
        'image/jpeg', 46753, 800, 800, 'PRODUCT', NOW(), NOW(), 0),
       ('product-4.jpg', 'product-4.jpg', '/uploads/demo/product-4.jpg', 'demo/product-4.jpg',
        'image/jpeg', 47486, 800, 800, 'PRODUCT', NOW(), NOW(), 0),
       ('product-5.jpg', 'product-5.jpg', '/uploads/demo/product-5.jpg', 'demo/product-5.jpg',
        'image/jpeg', 45280, 800, 800, 'PRODUCT', NOW(), NOW(), 0),
       ('product-6.jpg', 'product-6.jpg', '/uploads/demo/product-6.jpg', 'demo/product-6.jpg',
        'image/jpeg', 44920, 800, 800, 'PRODUCT', NOW(), NOW(), 0);

INSERT INTO `shop_user` (`id`, `user_no`, `username`, `nickname`, `phone`, `email`,
                         `avatar`, `gender`, `birthday`, `status`, `last_login_time`,
                         `remark`, `create_time`, `update_time`, `deleted`)
VALUES (1001, 'U20260001', 'zhangwei', '张伟', '13800000001', 'zhangwei@example.com',
        NULL, 'MALE', '1992-04-15', 1, DATE_SUB(NOW(), INTERVAL 1 DAY), '老客户',
        DATE_SUB(NOW(), INTERVAL 90 DAY), NOW(), 0),
       (1002, 'U20260002', 'lina', '李娜', '13800000002', 'lina@example.com',
        NULL, 'FEMALE', '1995-08-22', 1, DATE_SUB(NOW(), INTERVAL 2 DAY), NULL,
        DATE_SUB(NOW(), INTERVAL 80 DAY), NOW(), 0),
       (1003, 'U20260003', 'wangqiang', '王强', '13800000003', 'wangqiang@example.com',
        NULL, 'MALE', '1988-11-03', 1, DATE_SUB(NOW(), INTERVAL 3 DAY), NULL,
        DATE_SUB(NOW(), INTERVAL 70 DAY), NOW(), 0),
       (1004, 'U20260004', 'zhaomin', '赵敏', '13800000004', 'zhaomin@example.com',
        NULL, 'FEMALE', '1997-02-14', 1, DATE_SUB(NOW(), INTERVAL 4 DAY), NULL,
        DATE_SUB(NOW(), INTERVAL 60 DAY), NOW(), 0),
       (1005, 'U20260005', 'chenjing', '陈静', '13800000005', 'chenjing@example.com',
        NULL, 'FEMALE', '1993-07-30', 1, DATE_SUB(NOW(), INTERVAL 5 DAY), NULL,
        DATE_SUB(NOW(), INTERVAL 50 DAY), NOW(), 0),
       (1006, 'U20260006', 'liuyang', '刘洋', '13800000006', 'liuyang@example.com',
        NULL, 'MALE', '1990-12-09', 1, DATE_SUB(NOW(), INTERVAL 6 DAY), NULL,
        DATE_SUB(NOW(), INTERVAL 40 DAY), NOW(), 0),
       (1007, 'U20260007', 'zhoujie', '周杰', '13800000007', 'zhoujie@example.com',
        NULL, 'MALE', '1986-05-18', 1, NULL, NULL,
        DATE_SUB(NOW(), INTERVAL 30 DAY), NOW(), 0),
       (1008, 'U20260008', 'wuxue', '吴雪', '13800000008', 'wuxue@example.com',
        NULL, 'FEMALE', '1998-09-25', 1, NULL, NULL,
        DATE_SUB(NOW(), INTERVAL 25 DAY), NOW(), 0),
       (1009, 'U20260009', 'zhengkai', '郑凯', '13800000009', 'zhengkai@example.com',
        NULL, 'MALE', '1991-03-12', 1, NULL, NULL,
        DATE_SUB(NOW(), INTERVAL 20 DAY), NOW(), 0),
       (1010, 'U20260010', 'sunyue', '孙悦', '13800000010', 'sunyue@example.com',
        NULL, 'FEMALE', '1996-06-08', 1, NULL, NULL,
        DATE_SUB(NOW(), INTERVAL 15 DAY), NOW(), 0),
       (1011, 'U20260011', 'mali', '马丽', '13800000011', 'mali@example.com',
        NULL, 'FEMALE', '1994-10-17', 1, NULL, NULL,
        DATE_SUB(NOW(), INTERVAL 10 DAY), NOW(), 0),
       (1012, 'U20260012', 'heping', '何平', '13800000012', 'heping@example.com',
        NULL, 'UNKNOWN', NULL, 0, NULL, '长期未登录，已禁用',
        DATE_SUB(NOW(), INTERVAL 9 DAY), NOW(), 0);

INSERT INTO `sys_setting` (`config_key`, `config_value`, `config_group`, `config_name`,
                           `value_type`, `sort`, `remark`, `create_time`, `update_time`, `deleted`)
VALUES ('site.name', '云商城', 'BASIC', '站点名称', 'STRING', 1, '前台展示的站点名称', NOW(), NOW(), 0),
       ('site.logo', '/uploads/demo/logo.png', 'BASIC', '站点 Logo', 'STRING', 2, NULL, NOW(), NOW(), 0),
       ('site.icp', '京ICP备2026000000号', 'BASIC', 'ICP 备案号', 'STRING', 3, NULL, NOW(), NOW(), 0),
       ('site.description', '一个简洁好用的商城后台管理系统', 'BASIC', '站点简介', 'TEXT', 4, NULL, NOW(), NOW(), 0),
       ('contact.phone', '400-888-6666', 'CONTACT', '客服电话', 'STRING', 1, NULL, NOW(), NOW(), 0),
       ('contact.email', 'service@example.com', 'CONTACT', '客服邮箱', 'STRING', 2, NULL, NOW(), NOW(), 0),
       ('contact.address', '北京市朝阳区建国路 88 号', 'CONTACT', '联系地址', 'STRING', 3, NULL, NOW(), NOW(), 0),
       ('contact.work_time', '周一至周日 09:00-21:00', 'CONTACT', '工作时间', 'STRING', 4, NULL, NOW(), NOW(), 0),
       ('trade.freight', '10.00', 'TRADE', '默认运费', 'NUMBER', 1, '单位：元', NOW(), NOW(), 0),
       ('trade.free_shipping_amount', '99.00', 'TRADE', '包邮门槛', 'NUMBER', 2, '订单满此金额免运费', NOW(), NOW(), 0),
       ('trade.auto_cancel_minutes', '30', 'TRADE', '未付款自动取消', 'NUMBER', 3, '单位：分钟', NOW(), NOW(), 0),
       ('payment.methods', 'WECHAT,ALIPAY', 'PAYMENT', '支持的支付方式', 'STRING', 1, '逗号分隔', NOW(), NOW(), 0),
       ('payment.agreement', '付款即表示同意《用户支付协议》', 'PAYMENT', '支付协议提示', 'TEXT', 2, NULL, NOW(), NOW(), 0);

INSERT INTO `shop_order` (`id`, `order_no`, `user_id`, `receiver_name`, `receiver_phone`,
                          `receiver_address`, `total_amount`, `freight_amount`, `discount_amount`,
                          `pay_amount`, `pay_type`, `status`, `item_count`, `total_quantity`,
                          `pay_time`, `ship_time`, `finish_time`, `cancel_time`, `remark`,
                          `create_time`, `update_time`, `deleted`)
VALUES (1, 'SO20261109001', 1001, '张伟', '13800000001', '北京市朝阳区建国路 88 号',
        299.00, 0.00, 0.00, 299.00, 'WECHAT', 1, 1, 1,
        NOW(), NULL, NULL, NULL, '工作日送货', NOW(), NOW(), 0),
       (2, 'SO20261109002', 1002, '李娜', '13800000002', '上海市浦东新区世纪大道 100 号',
        599.00, 0.00, 0.00, 599.00, 'ALIPAY', 2, 1, 1,
        NOW(), NOW(), NULL, NULL, NULL, NOW(), NOW(), 0),
       (3, 'SO20261109003', 1003, '王强', '13800000003', '广州市天河区体育西路 66 号',
        129.00, 0.00, 0.00, 129.00, 'BALANCE', 3, 1, 1,
        DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), NULL, NULL,
        DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), 0),
       (4, 'SO20261109004', 1004, '赵敏', '13800000004', '杭州市西湖区文三路 90 号',
        399.00, 0.00, 0.00, 399.00, 'WECHAT', 3, 1, 1,
        DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), NULL, NULL,
        DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), 0),
       (5, 'SO20261109005', 1005, '陈静', '13800000005', '成都市武侯区天府大道 188 号',
        199.00, 0.00, 0.00, 199.00, 'ALIPAY', 3, 1, 1,
        DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), NULL, NULL,
        DATE_SUB(NOW(), INTERVAL 2 DAY), NOW(), 0),
       (6, 'SO20261109006', 1006, '刘洋', '13800000006', '深圳市南山区科技园路 1 号',
        459.00, 0.00, 0.00, 459.00, 'WECHAT', 2, 1, 1,
        DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, NULL, NULL,
        DATE_SUB(NOW(), INTERVAL 2 DAY), NOW(), 0),
       (7, 'SO20261109007', 1007, '周杰', '13800000007', '武汉市洪山区珞喻路 129 号',
        299.00, 0.00, 0.00, 299.00, 'BALANCE', 3, 1, 1,
        DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY),
        DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, NULL,
        DATE_SUB(NOW(), INTERVAL 3 DAY), NOW(), 0),
       (8, 'SO20261109008', 1008, '吴雪', '13800000008', '南京市鼓楼区中山北路 100 号',
        599.00, 0.00, 0.00, 599.00, 'ALIPAY', 3, 1, 1,
        DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY),
        DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, NULL,
        DATE_SUB(NOW(), INTERVAL 4 DAY), NOW(), 0),
       (9, 'SO20261109009', 1009, '郑凯', '13800000009', '西安市雁塔区科技路 50 号',
        129.00, 0.00, 0.00, 129.00, 'WECHAT', 1, 1, 1,
        DATE_SUB(NOW(), INTERVAL 5 DAY), NULL, NULL, NULL, NULL,
        DATE_SUB(NOW(), INTERVAL 5 DAY), NOW(), 0),
       (10, 'SO20261109010', 1010, '孙悦', '13800000010', '重庆市渝中区解放碑 88 号',
        399.00, 0.00, 0.00, 399.00, 'WECHAT', 3, 1, 1,
        DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY),
        DATE_SUB(NOW(), INTERVAL 4 DAY), NULL, NULL,
        DATE_SUB(NOW(), INTERVAL 6 DAY), NOW(), 0),
       (11, 'SO20261109011', 1011, '马丽', '13800000011', '苏州市姑苏区人民路 200 号',
        199.00, 0.00, 0.00, 199.00, 'ALIPAY', 3, 1, 1,
        DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY),
        DATE_SUB(NOW(), INTERVAL 6 DAY), NULL, NULL,
        DATE_SUB(NOW(), INTERVAL 8 DAY), NOW(), 0),
       (12, 'SO20261109012', 1012, '何平', '13800000012', '长沙市岳麓区麓山南路 2 号',
        459.00, 0.00, 0.00, 459.00, NULL, 0, 1, 1,
        NULL, NULL, NULL, NULL, '等待用户付款',
        DATE_SUB(NOW(), INTERVAL 9 DAY), NOW(), 0),
       -- 订单统计时间窗口演示数据：近24小时
       (13, 'SO20261119013', 1001, '张伟', '13800000001', '北京市朝阳区建国路 88 号',
        128.00, 0.00, 0.00, 128.00, 'WECHAT', 3, 1, 1,
        NOW(), NOW(), NOW(), NULL, '近24小时-已完成',
        NOW(), NOW(), 0),
       (14, 'SO20261119014', 1002, '李娜', '13800000002', '上海市浦东新区世纪大道 100 号',
        256.00, 0.00, 0.00, 256.00, 'ALIPAY', 2, 1, 1,
        NOW(), NOW(), NULL, NULL, '近24小时-待收货',
        NOW(), NOW(), 0),
       -- 近一周（2~6 天前）
       (15, 'SO20261119015', 1003, '王强', '13800000003', '广州市天河区体育西路 66 号',
        199.00, 0.00, 0.00, 199.00, 'BALANCE', 3, 1, 1,
        DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY),
        DATE_SUB(NOW(), INTERVAL 2 DAY), NULL, '近一周-已完成',
        DATE_SUB(NOW(), INTERVAL 2 DAY), NOW(), 0),
       (16, 'SO20261119016', 1004, '赵敏', '13800000004', '杭州市西湖区文三路 90 号',
        399.00, 0.00, 0.00, 399.00, 'WECHAT', 1, 1, 1,
        DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY),
        NULL, NULL, '近一周-待发货',
        DATE_SUB(NOW(), INTERVAL 4 DAY), NOW(), 0),
       (17, 'SO20261119017', 1005, '陈静', '13800000005', '成都市武侯区天府大道 188 号',
        599.00, 0.00, 0.00, 599.00, 'ALIPAY', 3, 1, 1,
        DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY),
        DATE_SUB(NOW(), INTERVAL 5 DAY), NULL, '近一周-已完成',
        DATE_SUB(NOW(), INTERVAL 6 DAY), NOW(), 0),
       -- 近一个月但超过一周（8~29 天前）
       (18, 'SO20261119018', 1006, '刘洋', '13800000006', '深圳市南山区科技园路 1 号',
        899.00, 0.00, 0.00, 899.00, 'WECHAT', 3, 1, 1,
        DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY),
        DATE_SUB(NOW(), INTERVAL 11 DAY), NULL, '近一月-已完成',
        DATE_SUB(NOW(), INTERVAL 12 DAY), NOW(), 0),
       (19, 'SO20261119019', 1007, '周杰', '13800000007', '武汉市洪山区珞喻路 129 号',
        129.00, 0.00, 0.00, 129.00, 'BALANCE', 2, 1, 1,
        DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY),
        NULL, NULL, '近一月-待收货',
        DATE_SUB(NOW(), INTERVAL 22 DAY), NOW(), 0),
       -- 超过一个月（边界，不应计入近一月窗口）
       (20, 'SO20261119020', 1008, '吴雪', '13800000008', '南京市鼓楼区中山北路 100 号',
        999.00, 0.00, 0.00, 999.00, 'ALIPAY', 3, 1, 1,
        DATE_SUB(NOW(), INTERVAL 35 DAY), DATE_SUB(NOW(), INTERVAL 35 DAY),
        DATE_SUB(NOW(), INTERVAL 34 DAY), NULL, '超过一月-已完成',
        DATE_SUB(NOW(), INTERVAL 35 DAY), NOW(), 0);

INSERT INTO `shop_order_item` (`id`, `order_id`, `product_id`, `product_name`, `product_image`,
                               `price`, `quantity`, `total_amount`, `create_time`, `update_time`,
                               `deleted`)
VALUES (1, 1, 1, '无线蓝牙耳机', '/uploads/demo/product-1.jpg', 299.00, 1, 299.00, NOW(), NOW(), 0),
       (2, 2, 2, '智能运动手表', '/uploads/demo/product-2.jpg', 599.00, 1, 599.00, NOW(), NOW(), 0),
       (3, 3, 4, '香薰加湿器', '/uploads/demo/product-4.jpg', 129.00, 1, 129.00,
        DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), 0),
       (4, 4, 3, '轻薄羽绒服', '/uploads/demo/product-3.jpg', 399.00, 1, 399.00,
        DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), 0),
       (5, 5, 5, '保湿精华液', '/uploads/demo/product-5.jpg', 199.00, 1, 199.00,
        DATE_SUB(NOW(), INTERVAL 2 DAY), NOW(), 0),
       (6, 6, 6, '便携胶囊咖啡机', '/uploads/demo/product-6.jpg', 459.00, 1, 459.00,
        DATE_SUB(NOW(), INTERVAL 2 DAY), NOW(), 0),
       (7, 7, 1, '无线蓝牙耳机', '/uploads/demo/product-1.jpg', 299.00, 1, 299.00,
        DATE_SUB(NOW(), INTERVAL 3 DAY), NOW(), 0),
       (8, 8, 2, '智能运动手表', '/uploads/demo/product-2.jpg', 599.00, 1, 599.00,
        DATE_SUB(NOW(), INTERVAL 4 DAY), NOW(), 0),
       (9, 9, 4, '香薰加湿器', '/uploads/demo/product-4.jpg', 129.00, 1, 129.00,
        DATE_SUB(NOW(), INTERVAL 5 DAY), NOW(), 0),
       (10, 10, 3, '轻薄羽绒服', '/uploads/demo/product-3.jpg', 399.00, 1, 399.00,
        DATE_SUB(NOW(), INTERVAL 6 DAY), NOW(), 0),
       (11, 11, 5, '保湿精华液', '/uploads/demo/product-5.jpg', 199.00, 1, 199.00,
        DATE_SUB(NOW(), INTERVAL 8 DAY), NOW(), 0),
       (12, 12, 6, '便携胶囊咖啡机', '/uploads/demo/product-6.jpg', 459.00, 1, 459.00,
        DATE_SUB(NOW(), INTERVAL 9 DAY), NOW(), 0),
       (13, 13, 1, '无线蓝牙耳机', '/uploads/demo/product-1.jpg', 128.00, 1, 128.00,
        NOW(), NOW(), 0),
       (14, 14, 2, '智能运动手表', '/uploads/demo/product-2.jpg', 256.00, 1, 256.00,
        NOW(), NOW(), 0),
       (15, 15, 4, '香薰加湿器', '/uploads/demo/product-4.jpg', 199.00, 1, 199.00,
        DATE_SUB(NOW(), INTERVAL 2 DAY), NOW(), 0),
       (16, 16, 3, '轻薄羽绒服', '/uploads/demo/product-3.jpg', 399.00, 1, 399.00,
        DATE_SUB(NOW(), INTERVAL 4 DAY), NOW(), 0),
       (17, 17, 2, '智能运动手表', '/uploads/demo/product-2.jpg', 599.00, 1, 599.00,
        DATE_SUB(NOW(), INTERVAL 6 DAY), NOW(), 0),
       (18, 18, 1, '无线蓝牙耳机', '/uploads/demo/product-1.jpg', 899.00, 1, 899.00,
        DATE_SUB(NOW(), INTERVAL 12 DAY), NOW(), 0),
       (19, 19, 4, '香薰加湿器', '/uploads/demo/product-4.jpg', 129.00, 1, 129.00,
        DATE_SUB(NOW(), INTERVAL 22 DAY), NOW(), 0),
       (20, 20, 5, '保湿精华液', '/uploads/demo/product-5.jpg', 999.00, 1, 999.00,
        DATE_SUB(NOW(), INTERVAL 35 DAY), NOW(), 0);
