-- =====================================================================
-- 订单管理增量升级脚本（MySQL 8.0+）
-- 适用场景：已有 v1.3.1 数据库，需要保留原订单数据并补充订单管理字段、
--           订单明细表、订单菜单与接口权限。脚本可重复执行，缺列和缺索引
--           会自动补充，菜单与角色关联使用 IGNORE 保证幂等。
-- 使用前请备份数据库。
-- =====================================================================

USE `shop_admin`;

SET NAMES utf8mb4;

DROP PROCEDURE IF EXISTS `upgrade_order_management_columns`;

DELIMITER $$

CREATE PROCEDURE `upgrade_order_management_columns`()
BEGIN
    IF (SELECT COUNT(*)
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'shop_order'
          AND column_name = 'user_id') = 0 THEN
        ALTER TABLE `shop_order`
            ADD COLUMN `user_id` BIGINT DEFAULT NULL COMMENT '会员编号' AFTER `order_no`;
    END IF;

    IF (SELECT COUNT(*)
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'shop_order'
          AND column_name = 'receiver_name') = 0 THEN
        ALTER TABLE `shop_order`
            ADD COLUMN `receiver_name` VARCHAR(50) NOT NULL DEFAULT '待补充'
                COMMENT '收货人' AFTER `user_id`;
    END IF;

    IF (SELECT COUNT(*)
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'shop_order'
          AND column_name = 'receiver_phone') = 0 THEN
        ALTER TABLE `shop_order`
            ADD COLUMN `receiver_phone` VARCHAR(20) NOT NULL DEFAULT ''
                COMMENT '收货手机号' AFTER `receiver_name`;
    END IF;

    IF (SELECT COUNT(*)
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'shop_order'
          AND column_name = 'receiver_address') = 0 THEN
        ALTER TABLE `shop_order`
            ADD COLUMN `receiver_address` VARCHAR(200) NOT NULL DEFAULT ''
                COMMENT '收货地址' AFTER `receiver_phone`;
    END IF;

    IF (SELECT COUNT(*)
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'shop_order'
          AND column_name = 'freight_amount') = 0 THEN
        ALTER TABLE `shop_order`
            ADD COLUMN `freight_amount` DECIMAL(12, 2) NOT NULL DEFAULT 0.00
                COMMENT '运费' AFTER `total_amount`;
    END IF;

    IF (SELECT COUNT(*)
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'shop_order'
          AND column_name = 'discount_amount') = 0 THEN
        ALTER TABLE `shop_order`
            ADD COLUMN `discount_amount` DECIMAL(12, 2) NOT NULL DEFAULT 0.00
                COMMENT '优惠金额' AFTER `freight_amount`;
    END IF;

    IF (SELECT COUNT(*)
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'shop_order'
          AND column_name = 'pay_amount') = 0 THEN
        ALTER TABLE `shop_order`
            ADD COLUMN `pay_amount` DECIMAL(12, 2) NOT NULL DEFAULT 0.00
                COMMENT '应付金额' AFTER `discount_amount`;
        UPDATE `shop_order`
        SET `pay_amount` = `total_amount`
        WHERE `total_amount` > 0;
    END IF;

    IF (SELECT COUNT(*)
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'shop_order'
          AND column_name = 'pay_type') = 0 THEN
        ALTER TABLE `shop_order`
            ADD COLUMN `pay_type` VARCHAR(20) DEFAULT NULL
                COMMENT '支付方式：WECHAT/ALIPAY/BALANCE' AFTER `pay_amount`;
    END IF;

    IF (SELECT COUNT(*)
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'shop_order'
          AND column_name = 'item_count') = 0 THEN
        ALTER TABLE `shop_order`
            ADD COLUMN `item_count` INT NOT NULL DEFAULT 0
                COMMENT '商品种类数' AFTER `pay_type`;
    END IF;

    IF (SELECT COUNT(*)
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'shop_order'
          AND column_name = 'total_quantity') = 0 THEN
        ALTER TABLE `shop_order`
            ADD COLUMN `total_quantity` INT NOT NULL DEFAULT 0
                COMMENT '商品总数量' AFTER `item_count`;
    END IF;

    IF (SELECT COUNT(*)
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'shop_order'
          AND column_name = 'pay_time') = 0 THEN
        ALTER TABLE `shop_order`
            ADD COLUMN `pay_time` DATETIME DEFAULT NULL
                COMMENT '支付时间' AFTER `total_quantity`;
    END IF;

    IF (SELECT COUNT(*)
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'shop_order'
          AND column_name = 'ship_time') = 0 THEN
        ALTER TABLE `shop_order`
            ADD COLUMN `ship_time` DATETIME DEFAULT NULL
                COMMENT '发货时间' AFTER `pay_time`;
    END IF;

    IF (SELECT COUNT(*)
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'shop_order'
          AND column_name = 'finish_time') = 0 THEN
        ALTER TABLE `shop_order`
            ADD COLUMN `finish_time` DATETIME DEFAULT NULL
                COMMENT '完成时间' AFTER `ship_time`;
    END IF;

    IF (SELECT COUNT(*)
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'shop_order'
          AND column_name = 'cancel_time') = 0 THEN
        ALTER TABLE `shop_order`
            ADD COLUMN `cancel_time` DATETIME DEFAULT NULL
                COMMENT '取消时间' AFTER `finish_time`;
    END IF;

    IF (SELECT COUNT(*)
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'shop_order'
          AND column_name = 'remark') = 0 THEN
        ALTER TABLE `shop_order`
            ADD COLUMN `remark` VARCHAR(500) DEFAULT NULL
                COMMENT '订单备注' AFTER `cancel_time`;
    END IF;

    IF (SELECT COUNT(*)
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = 'shop_order'
          AND index_name = 'idx_shop_order_receiver_phone') = 0 THEN
        ALTER TABLE `shop_order`
            ADD INDEX `idx_shop_order_receiver_phone` (`receiver_phone`);
    END IF;

    IF (SELECT COUNT(*)
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = 'shop_order'
          AND index_name = 'idx_shop_order_user_id') = 0 THEN
        ALTER TABLE `shop_order`
            ADD INDEX `idx_shop_order_user_id` (`user_id`);
    END IF;

    IF (SELECT COUNT(*)
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = 'shop_order'
          AND index_name = 'idx_shop_order_status_create') = 0 THEN
        ALTER TABLE `shop_order`
            ADD INDEX `idx_shop_order_status_create` (`status`, `create_time`);
    END IF;
END$$

DELIMITER ;

CALL `upgrade_order_management_columns`();

DROP PROCEDURE IF EXISTS `upgrade_order_management_columns`;

CREATE TABLE IF NOT EXISTS `shop_order_item`
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

INSERT IGNORE INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `route_path`, `component`,
                               `icon`, `permission_code`, `sort`, `visible`, `status`,
                               `create_time`, `update_time`, `deleted`)
SELECT 0, '订单管理', 'MENU', '/order', 'order/index', 'List', 'order:view', 4, 1, 1,
       NOW(), NOW(), 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1
                  FROM `sys_menu`
                  WHERE `route_path` = '/order');

UPDATE `sys_menu`
SET `sort` = 5
WHERE `route_path` = '/system'
  AND `deleted` = 0;

UPDATE `sys_menu`
SET `sort` = 4
WHERE `route_path` = '/order'
  AND `deleted` = 0;

INSERT IGNORE INTO `sys_role_menu` (`role_code`, `menu_id`, `create_time`, `update_time`)
SELECT 'SUPER_ADMIN', `id`, NOW(), NOW()
FROM `sys_menu`
WHERE `deleted` = 0;

INSERT IGNORE INTO `sys_role_menu` (`role_code`, `menu_id`, `create_time`, `update_time`)
SELECT 'ADMIN', `id`, NOW(), NOW()
FROM `sys_menu`
WHERE `route_path` = '/order'
  AND `deleted` = 0;
