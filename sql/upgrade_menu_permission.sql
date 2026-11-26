-- =====================================================================
-- 商城后台管理系统菜单权限增量升级脚本（MySQL 8.0+）
-- 适用场景：已执行过 v1.0.0 初始化脚本，需要保留原业务数据再升级。
-- 使用前请备份数据库，并在低峰期执行。
-- =====================================================================

USE `shop_admin`;

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `sys_menu`
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

CREATE TABLE IF NOT EXISTS `sys_role_menu`
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

INSERT IGNORE INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `route_path`, `component`,
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
       (6, 0, '系统管理', 'CATALOG', '/system', 'Layout',
        'Setting', NULL, 4, 1, 1, NOW(), NOW(), 0),
       (7, 6, '管理员管理', 'MENU', '/system/admin', 'system/admin/index',
        NULL, 'system:admin', 1, 1, 1, NOW(), NOW(), 0),
       (8, 6, '菜单权限', 'MENU', '/system/menu', 'system/menu/index',
        NULL, 'system:menu', 2, 1, 1, NOW(), NOW(), 0),
       (9, 2, '规格管理', 'MENU', '/product/spec', 'product/spec/index',
        NULL, 'product:spec', 3, 1, 1, NOW(), NOW(), 0),
       (10, 2, '优惠券管理', 'MENU', '/product/coupon', 'product/coupon/index',
        NULL, 'product:coupon', 4, 1, 1, NOW(), NOW(), 0);

INSERT IGNORE INTO `sys_role_menu` (`role_code`, `menu_id`, `create_time`, `update_time`)
SELECT 'SUPER_ADMIN', `id`, NOW(), NOW()
FROM `sys_menu`
WHERE `deleted` = 0;

INSERT IGNORE INTO `sys_role_menu` (`role_code`, `menu_id`, `create_time`, `update_time`)
VALUES ('ADMIN', 1, NOW(), NOW()),
       ('ADMIN', 2, NOW(), NOW()),
       ('ADMIN', 3, NOW(), NOW()),
       ('ADMIN', 4, NOW(), NOW()),
       ('ADMIN', 5, NOW(), NOW()),
       ('ADMIN', 9, NOW(), NOW()),
       ('ADMIN', 10, NOW(), NOW());
