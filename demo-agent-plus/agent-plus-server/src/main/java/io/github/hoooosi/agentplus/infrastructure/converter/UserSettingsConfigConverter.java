package io.github.hoooosi.agentplus.infrastructure.converter;

import io.github.hoooosi.agentplus.domain.user.service.model.config.UserSettingsConfig;
import io.github.hoooosi.agentplus.infrastructure.utils.JsonUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** 用户设置配置转换器 处理JSON存储的用户设置配置信息 */
@MappedTypes(UserSettingsConfig.class)
@MappedJdbcTypes({JdbcType.OTHER})
public class UserSettingsConfigConverter extends BaseTypeHandler<UserSettingsConfig> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UserSettingsConfig parameter, JdbcType jdbcType) throws SQLException {
        PGobject jsonObject = new PGobject();
        jsonObject.setType("jsonb");
        jsonObject.setValue(JsonUtils.toJsonString(parameter));
        ps.setObject(i, jsonObject);
    }

    @Override
    public UserSettingsConfig getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return parseJson(json);
    }

    @Override
    public UserSettingsConfig getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return parseJson(json);
    }

    @Override
    public UserSettingsConfig getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return parseJson(json);
    }

    private UserSettingsConfig parseJson(String json) throws SQLException {
        if (json == null || json.isEmpty()) {
            return new UserSettingsConfig();
        }
        UserSettingsConfig config = JsonUtils.parseObject(json, UserSettingsConfig.class);
        return config != null ? config : new UserSettingsConfig();
    }
}
