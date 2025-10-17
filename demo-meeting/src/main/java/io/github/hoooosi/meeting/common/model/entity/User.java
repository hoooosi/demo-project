package io.github.hoooosi.meeting.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@TableName(value = "tb_user")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
public class User extends TimeField {
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    private Long userId;
    private String email;
    private String nickName;
    private Integer sex;
    @JsonIgnore
    private String password;
    private Integer status;
    private Long personalMeetingNo;
    private Long lastLoginTime;
    private Long lastOffTime;
}
