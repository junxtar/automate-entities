package com.example.junxtar.javaparser.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColumnData {
    //
    private String row;
    private String dataType;
    private String columnName;
    private String columnComment;
}
