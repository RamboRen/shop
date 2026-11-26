package com.shop.admin.common.exception;

import com.shop.admin.common.api.ApiResult;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器，统一输出接口错误信息。
 *
 * @author shop-admin
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常。
     *
     * @param exception 业务异常
     * @return 统一错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResult<Void>> handleBusinessException(BusinessException exception) {
        HttpStatus status = HttpStatus.resolve(exception.getCode());
        HttpStatus responseStatus = status == null ? HttpStatus.BAD_REQUEST : status;
        log.warn("业务异常: code={}, message={}", exception.getCode(), exception.getMessage());
        return buildResponse(responseStatus, exception.getMessage());
    }

    /**
     * 处理请求体参数校验异常。
     *
     * @param exception 参数校验异常
     * @return 统一错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .orElse(null);
        String message = fieldError == null
                ? "请求参数校验失败"
                : fieldError.getField() + ": " + fieldError.getDefaultMessage();
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 处理查询对象绑定校验异常。
     *
     * @param exception 绑定异常
     * @return 统一错误响应
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResult<Void>> handleBindException(BindException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .orElse(null);
        String message = fieldError == null
                ? "请求参数校验失败"
                : fieldError.getField() + ": " + fieldError.getDefaultMessage();
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 处理路径参数或查询参数校验异常。
     *
     * @param exception 约束校验异常
     * @return 统一错误响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResult<Void>> handleConstraintViolation(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("；"));
        if (message.isEmpty()) {
            message = "请求参数校验失败";
        }
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 处理请求体不可读异常。
     *
     * @param exception 请求体解析异常
     * @return 统一错误响应
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResult<Void>> handleMessageNotReadable(HttpMessageNotReadableException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, "请求体格式错误");
    }

    /**
     * 处理缺少请求参数异常。
     *
     * @param exception 缺少参数异常
     * @return 统一错误响应
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResult<Void>> handleMissingParameter(MissingServletRequestParameterException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, "缺少请求参数: " + exception.getParameterName());
    }

    /**
     * 处理参数类型不匹配异常。
     *
     * @param exception 类型不匹配异常
     * @return 统一错误响应
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResult<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, "请求参数类型错误: " + exception.getName());
    }

    /**
     * 处理不支持的请求方法异常。
     *
     * @param exception 请求方法异常
     * @return 统一错误响应
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResult<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException exception) {
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "不支持的请求方法: " + exception.getMethod());
    }

    /**
     * 处理不支持的媒体类型异常。
     *
     * @param exception 媒体类型异常
     * @return 统一错误响应
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResult<Void>> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException exception) {
        return buildResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "不支持的媒体类型");
    }

    /**
     * 处理上传文件超出限制异常。
     *
     * @param exception 上传大小异常
     * @return 统一错误响应
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResult<Void>> handleMaxUploadSize(MaxUploadSizeExceededException exception) {
        return buildResponse(HttpStatus.PAYLOAD_TOO_LARGE, "上传文件超过大小限制");
    }

    /**
     * 处理唯一索引冲突异常。
     *
     * @param exception 唯一键冲突异常
     * @return 统一错误响应
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiResult<Void>> handleDuplicateKey(DuplicateKeyException exception) {
        log.warn("唯一键冲突", exception);
        return buildResponse(HttpStatus.CONFLICT, "数据已存在，请勿重复提交");
    }

    /**
     * 处理数据完整性异常。
     *
     * @param exception 数据完整性异常
     * @return 统一错误响应
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResult<Void>> handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        log.warn("数据完整性校验失败", exception);
        return buildResponse(HttpStatus.CONFLICT, "数据关联冲突，操作未完成");
    }

    /**
     * 处理静态资源不存在异常。
     *
     * @param exception 资源不存在异常
     * @return 统一错误响应
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResult<Void>> handleNoResourceFound(NoResourceFoundException exception) {
        return buildResponse(HttpStatus.NOT_FOUND, "请求的资源不存在");
    }

    /**
     * 处理方法级权限校验拒绝异常。
     *
     * @param exception 权限拒绝异常
     * @return 统一错误响应
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResult<Void>> handleAccessDenied(AccessDeniedException exception) {
        log.warn("权限校验失败: {}", exception.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "无权访问该资源");
    }

    /**
     * 处理未预期异常，避免向客户端泄露内部实现细节。
     *
     * @param exception 未预期异常
     * @return 统一错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Void>> handleException(Exception exception) {
        log.error("系统异常", exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "系统繁忙，请稍后重试");
    }

    private ResponseEntity<ApiResult<Void>> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ApiResult.failure(status.value(), message));
    }
}
