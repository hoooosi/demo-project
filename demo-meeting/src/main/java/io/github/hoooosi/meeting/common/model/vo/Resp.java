package io.github.hoooosi.meeting.common.model.vo;

import lombok.Data;

@Data
public class Resp<T> {
    private int code = 200;
    private String message = "Ok";
    private T data;

    public static Resp<Void> success() {
        return new Resp<>();
    }

    public static <T> Resp<T> success(String msg) {
        Resp<T> resp = new Resp<>();
        resp.setMessage(msg);
        return resp;
    }

    public static <T> Resp<T> success(T data) {
        Resp<T> resp = new Resp<>();
        resp.setData(data);
        return resp;
    }

    public static Resp<?> error(int code, String message) {
        Resp<?> resp = new Resp<>();
        resp.setCode(code);
        resp.setMessage(message);
        return resp;
    }
}
