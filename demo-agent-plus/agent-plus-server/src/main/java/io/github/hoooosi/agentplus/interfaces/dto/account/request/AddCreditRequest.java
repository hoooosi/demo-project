package io.github.hoooosi.agentplus.interfaces.dto.account.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/** 增加信用额度请求 */
@Data
public class AddCreditRequest {

    /** 增加的信用额度 */
    @NotNull(message = "信用额度不能为空")
    @DecimalMin(value = "0.01", message = "信用额度必须大于0.01")
    private BigDecimal amount;

    /** 备注信息 */
    private String remark;
}