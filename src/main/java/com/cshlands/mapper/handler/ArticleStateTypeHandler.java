package com.cshlands.mapper.handler;

import com.cshlands.pojo.ArticleState;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes({ArticleState.class})
public class ArticleStateTypeHandler extends BaseTypeHandler<ArticleState> {
    // Java → 数据库：写入时取中文值
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ArticleState parameter, JdbcType jdbcType) throws SQLException, SQLException {
        ps.setString(i, parameter.getValue());
    }

    // 数据库 → Java：按列名读取
    @Override
    public ArticleState getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : ArticleState.fromValue(value);
    }

    // 数据库 → Java：按列序号读取
    @Override
    public ArticleState getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : ArticleState.fromValue(value);
    }

    // 存储过程用，基本用不到，照写即可
    @Override
    public ArticleState getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : ArticleState.fromValue(value);
    }
}
