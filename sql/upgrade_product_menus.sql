-- =====================================================================
-- 商品管理子菜单增量升级脚本（MySQL 8.0+）
-- 适用场景：已执行 v1.1.0 初始化/升级脚本，需要保留原数据添加
--           “规格管理”“优惠券管理”两个商品子菜单。
-- 脚本可重复执行，不会覆盖已有菜单和角色分配。
-- 使用前请备份数据库。
-- =====================================================================

USE `shop_admin`;

SET NAMES utf8mb4;

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
