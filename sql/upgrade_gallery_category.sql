-- =====================================================================
-- 图库分类增量升级脚本（MySQL 8.0+）
-- 适用：已有图库模块（shop_gallery_image）的数据库，需要新增图库分类表。
--       分类管理复用图库管理权限（gallery:view），无需新增菜单与角色关联。
--       脚本可重复执行：建表使用 IF NOT EXISTS，默认分类使用 INSERT IGNORE，
--       不会覆盖管理员已修改的分类名称、排序或状态。
-- 使用前请备份数据库。
-- =====================================================================

USE `shop_admin`;

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `shop_gallery_category`
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

-- 内置默认分类，与原有图库分组编码保持一致，确保历史图片分组可正常展示名称。
-- INSERT IGNORE 保证重复执行时不覆盖管理员对名称、排序、状态的修改。
INSERT IGNORE INTO `shop_gallery_category` (`category_code`, `category_name`, `sort`, `status`, `remark`,
                                            `create_time`, `update_time`, `deleted`)
VALUES ('PRODUCT', '商品图', 1, 1, '商品主图与详情页图片', NOW(), NOW(), 0),
       ('BANNER', '轮播图', 2, 1, '首页与活动轮播图', NOW(), NOW(), 0),
       ('AVATAR', '头像', 3, 1, '用户与管理员头像', NOW(), NOW(), 0),
       ('OTHER', '其他', 4, 1, '未归类的图片', NOW(), NOW(), 0);
