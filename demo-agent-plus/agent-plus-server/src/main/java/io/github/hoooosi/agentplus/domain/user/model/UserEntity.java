package io.github.hoooosi.agentplus.domain.user.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.github.hoooosi.agentplus.infrastructure.entity.BaseEntity;
import io.github.hoooosi.agentplus.infrastructure.exception.BusinessException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@TableName("users")
@Data
@Accessors(chain = true)
public class UserEntity extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String nickname;
    private String email;
    private String phone;
    private String password;
    private String githubId;
    private String githubLogin;
    private String avatarUrl;
    private String loginPlatform;
    private Boolean isAdmin;

    public void valid() {
        if (StringUtils.isEmpty(email) && StringUtils.isEmpty(phone) && StringUtils.isEmpty(githubId)) {
            throw new BusinessException("必须使用邮箱、手机号或GitHub账号来作为账号");
        }
    }
}
