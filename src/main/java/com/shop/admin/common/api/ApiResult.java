package com.shop.admin.common.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一接口响应结构。
 *
 * @param <T> 业务数据类型
 * @author shop-admin
 */
@Getter
@Schema(description = "统一接口响应")
public class ApiResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 业务状态码，200 表示成功。
     */
    @Schema(description = "业务状态码，200 表示成功", example = "200")
    private final int code;

    /**
     * 响应提示信息。
     */
    @Schema(description = "响应提示信息", example = "操作成功")
    private final String message;

    /**
     * 业务数据。
     */
    @Schema(description = "业务数据")
    private final T data;

    private ApiResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 构造带数据的成功响应。
     *
     * @param data 业务数据
     * @param <T>  业务数据类型
     * @return 成功响应
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(200, "操作成功", data);
    }

    /**
     * 构造无数据的成功响应。
     *
     * @return 成功响应
     */
    public static ApiResult<Void> success() {
        return new ApiResult<>(200, "操作成功", null);
    }

    /**
     * 构造失败响应。
     *
     * @param code    业务状态码
     * @param message 提示信息
     * @param <T>     业务数据类型
     * @return 失败响应
     */
    public static <T> ApiResult<T> failure(int code, String message) {
        return new ApiResult<>(code, message, null);
    }
}
