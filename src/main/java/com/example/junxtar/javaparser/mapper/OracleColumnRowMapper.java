package com.example.junxtar.javaparser.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

public class OracleColumnRowMapper implements RowMapper<Map<String, Object>> {

    public OracleColumnRowMapper() {
    }

    @Override
    public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        Map<String, Object> mapOfColumnValues = this.createColumnMap(columnCount);

        for (int i = 1; i <= columnCount; ++i) {
            String column = JdbcUtils.lookupColumnName(rsmd, i);
            mapOfColumnValues.putIfAbsent(this.getColumnKey(column), this.getColumnValue(rs, i));
        }

        return mapOfColumnValues;
    }

    protected Map<String, Object> createColumnMap(int columnCount) {
        return new LinkedCaseInsensitiveMap<>(columnCount);
    }

    protected String getColumnKey(String columnName) {
        return columnName.toLowerCase();
    }

    @Nullable
    protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
        int columnType = rs.getMetaData().getColumnType(index);

        if (columnType == java.sql.Types.TIMESTAMP) {
            Timestamp timestamp = rs.getTimestamp(index);
            return timestamp != null ? timestamp.toLocalDateTime() : null;
        }

        return JdbcUtils.getResultSetValue(rs, index);
    }
}
