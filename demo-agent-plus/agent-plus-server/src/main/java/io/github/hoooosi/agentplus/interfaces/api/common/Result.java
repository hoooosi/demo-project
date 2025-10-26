package io.github.hoooosi.agentplus.interfaces.api.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 通用API响应结果
 *
 * @param <T> 数据类型 */
@Data
public class Result<T> {
    /** 状态码 */
    private Integer code;

    /** 响应消息 */
    private String message;

    /** 响应数据 */
    private T data;

    /** 时间戳 */
    private Long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public Result(Integer code, String message) {
        this(code, message, null);
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public Result<T> message(String message) {
        this.message = message;
        return this;
    }

    /** 成功响应（无数据）
     *
     * @return 响应结果 */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功");
    }

    /** 成功响应（有数据）
     *
     * @param data 响应数据
     * @return 响应结果 */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /** 成功响应（自定义消息和数据）
     *
     * @param message 响应消息
     * @param data 响应数据
     * @return 响应结果 */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /** 失败响应
     *
     * @param code 状态码
     * @param message 响应消息
     * @return 响应结果 */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message);
    }

    /** 服务器内部错误
     *
     * @param message 错误消息
     * @return 响应结果 */
    public static <T> Result<T> serverError(String message) {
        return error(500, message);
    }

    /** 参数错误
     *
     * @param message 错误消息
     * @return 响应结果 */
    public static <T> Result<T> badRequest(String message) {
        return error(400, message);
    }

    /** 未授权
     *
     * @param message 错误消息
     * @return 响应结果 */
    public static <T> Result<T> unauthorized(String message) {
        return error(401, message);
    }

    /** 禁止访问
     *
     * @param message 错误消息
     * @return 响应结果 */
    public static <T> Result<T> forbidden(String message) {
        return error(403, message);
    }

    /** 资源不存在
     *
     * @param message 错误消息
     * @return 响应结果 */
    public static <T> Result<T> notFound(String message) {
        return error(404, message);
    }
}
