package com.example.junxtar.javaparser.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClassFile {
    private String fileName;
    private String source;
    private String comment;
}
