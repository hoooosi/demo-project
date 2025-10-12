package io.github.hoooosi.meeting.common.exception;

import io.github.hoooosi.meeting.common.model.vo.Resp;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Resp<?> businessExceptionHandler(BusinessException e) {
        return Resp.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(PSQLException.class)
    public Resp<?> PSQLExceptionHandler(PSQLException e) {
        return Resp.error(45000, e.getMessage());
    }
}
