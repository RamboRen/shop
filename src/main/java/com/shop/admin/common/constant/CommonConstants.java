package com.shop.admin.common.constant;

/**
 * 通用常量定义。
 *
 * @author shop-admin
 */
public final class CommonConstants {

    /**
     * 默认页码。
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 默认每页条数。
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 单页最大条数。
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 批量操作最大条数。
     */
    public static final int MAX_BATCH_SIZE = 50;

    /**
     * 低库存预警阈值。
     */
    public static final int LOW_STOCK_THRESHOLD = 10;

    /**
     * 逻辑删除标识：未删除。
     */
    public static final int NOT_DELETED = 0;

    /**
     * 逻辑删除标识：已删除。
     */
    public static final int DELETED = 1;

    private CommonConstants() {
    }
}
