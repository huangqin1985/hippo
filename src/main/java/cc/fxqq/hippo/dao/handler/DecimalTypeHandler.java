package cc.fxqq.hippo.dao.handler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BigDecimalTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

@MappedJdbcTypes({JdbcType.DECIMAL})
public class DecimalTypeHandler extends BigDecimalTypeHandler {

    @Override
    public BigDecimal getNullableResult(ResultSet rs, String columnName) throws SQLException {
        BigDecimal result =super.getNullableResult(rs, columnName)==null?null:super.getNullableResult(rs, columnName).setScale(2, RoundingMode.HALF_UP);
        return  result;
    }

    @Override
    public BigDecimal getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return super.getNullableResult(rs, columnIndex)==null?null:super.getNullableResult(rs, columnIndex).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return super.getNullableResult(cs, columnIndex)==null?null:super.getNullableResult(cs, columnIndex).setScale(2, RoundingMode.HALF_UP);
    }
}
