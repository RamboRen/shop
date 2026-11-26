package com.shop.admin.common.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结构。
 *
 * @param <T> 记录类型
 * @author shop-admin
 */
@Getter
@Schema(description = "分页响应")
public class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 总记录数。
     */
    @Schema(description = "总记录数", example = "128")
    private final long total;

    /**
     * 总页数。
     */
    @Schema(description = "总页数", example = "13")
    private final long pages;

    /**
     * 当前页码。
     */
    @Schema(description = "当前页码", example = "1")
    private final long pageNum;

    /**
     * 每页条数。
     */
    @Schema(description = "每页条数", example = "10")
    private final long pageSize;

    /**
     * 当前页数据。
     */
    @Schema(description = "当前页数据")
    private final List<T> records;

    private PageResult(long total, long pages, long pageNum, long pageSize, List<T> records) {
        this.total = total;
        this.pages = pages;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.records = records;
    }

    /**
     * 创建分页响应。
     *
     * @param total    总记录数
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @param records  当前页数据
     * @param <T>      记录类型
     * @return 分页响应
     */
    public static <T> PageResult<T> of(long total, long pageNum, long pageSize, List<T> records) {
        long safePageSize = pageSize <= 0 ? 1 : pageSize;
        long pages = (total + safePageSize - 1) / safePageSize;
        return new PageResult<>(total, pages, pageNum, pageSize, records);
    }
}
