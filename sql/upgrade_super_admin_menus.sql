-- =====================================================================
-- 超级管理员全量菜单权限修复脚本（MySQL 8.0+）
-- 适用场景：已有数据库中的 sys_role_menu 未覆盖全部菜单，执行后
--           将当前所有未删除菜单（含隐藏、禁用菜单）重新关联到
--           SUPER_ADMIN，保证超级管理员拥有全部菜单权限。
-- 脚本可重复执行，不会产生重复关联。使用前请备份数据库。
-- =====================================================================

USE `shop_admin`;

SET NAMES utf8mb4;

INSERT IGNORE INTO `sys_role_menu` (`role_code`, `menu_id`, `create_time`, `update_time`)
SELECT 'SUPER_ADMIN', `id`, NOW(), NOW()
FROM `sys_menu`
WHERE `deleted` = 0;
