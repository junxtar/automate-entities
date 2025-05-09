package com.example.junxtar.javaparser.domain;

import com.example.junxtar.javaparser.domain.vo.ColumnData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Table {
    private String tableName;
    private List<ColumnData> columnDataList;
    private List<String> primaryKeyList;
}
