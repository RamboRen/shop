package com.shop.admin.common.exception;

import lombok.Getter;

/**
 * 业务异常。
 *
 * @author shop-admin
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final int DEFAULT_CODE = 400;

    /**
     * 业务状态码。
     */
    private final int code;

    /**
     * 使用默认 400 状态码构造业务异常。
     *
     * @param message 异常提示
     */
    public BusinessException(String message) {
        this(DEFAULT_CODE, message);
    }

    /**
     * 构造指定状态码的业务异常。
     *
     * @param code    业务状态码
     * @param message 异常提示
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
