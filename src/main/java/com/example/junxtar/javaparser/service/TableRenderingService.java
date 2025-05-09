package com.example.junxtar.javaparser.service;

import com.example.junxtar.javaparser.dto.TableDto;
import com.example.junxtar.javaparser.query.Queries;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Service
public class TableRenderingService {

    private DataSource dataSource;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public TableRenderingService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<TableDto> getTables() {
        return namedParameterJdbcTemplate.queryForList(
                        Queries.SelectTableComment,
                        Map.of("schema", "public")
                ).stream()
                .map(map -> new TableDto(
                        map.get("TABLE_NAME").toString(),
                        map.get("TABLE_COMMENT") == null ? "" : map.get("TABLE_COMMENT").toString()
                )).toList();
    }
}
