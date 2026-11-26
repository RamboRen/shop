-- =====================================================================
-- 系统设置增量升级脚本（MySQL 8.0+）
-- 适用场景：已有包含用户管理的数据库，需要新增系统设置表、系统设置菜单与接口权限。
--           脚本可重复执行，表、菜单、配置项与角色关联均使用幂等写法。
-- 使用前请备份数据库。
-- =====================================================================

USE `shop_admin`;

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `sys_setting`
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

-- 系统设置菜单放在系统管理（/system）下，仅超管默认可见
INSERT IGNORE INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `route_path`, `component`,
                               `icon`, `permission_code`, `sort`, `visible`, `status`,
                               `create_time`, `update_time`, `deleted`)
VALUES (13, 6, '系统设置', 'MENU', '/system/setting', 'system/setting/index',
        NULL, 'system:setting', 3, 1, 1, NOW(), NOW(), 0);

-- 超级管理员自动获得该菜单权限
INSERT IGNORE INTO `sys_role_menu` (`role_code`, `menu_id`, `create_time`, `update_time`)
SELECT 'SUPER_ADMIN', `id`, NOW(), NOW()
FROM `sys_menu`
WHERE `route_path` = '/system/setting'
  AND `deleted` = 0;

-- 默认配置项，使用 INSERT IGNORE 避免重复执行时覆盖管理员已修改的值
INSERT IGNORE INTO `sys_setting` (`config_key`, `config_value`, `config_group`, `config_name`,
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
