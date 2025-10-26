package io.github.hoooosi.agentplus.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.Getter;


@Data
public class BaseEntity {

    @TableField(fill = FieldFill.INSERT)
    protected Long createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected Long updatedAt;

    @TableLogic
    protected Long deletedAt;

    @TableField(exist = false)
    private Operator operatedBy = Operator.USER;

    public boolean needCheckUserId() {
        return this.operatedBy == Operator.USER;
    }
}
