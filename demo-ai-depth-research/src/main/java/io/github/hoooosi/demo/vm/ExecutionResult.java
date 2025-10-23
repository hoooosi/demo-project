package io.github.hoooosi.demo.vm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 命令执行结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionResult {

    /**
     * 执行的命令
     */
    private String command;

    /**
     * 标准输出内容
     */
    private String stdout;

    /**
     * 错误输出内容
     */
    private String stderr;

    /**
     * 退出码（0 表示成功）
     */
    private Integer exitCode;

    /**
     * 是否执行成功
     */
    private Boolean success;
}
