package com.example.junxtar.javaparser.service;

import com.example.junxtar.javaparser.domain.Table;
import com.example.junxtar.javaparser.domain.vo.ClassFile;
import com.example.junxtar.javaparser.domain.vo.ColumnData;
import com.example.junxtar.javaparser.dto.ParserDto;
import com.example.junxtar.javaparser.mapper.OracleColumnRowMapper;
import com.example.junxtar.javaparser.query.Queries;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import io.micrometer.common.util.StringUtils;

import lombok.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ParserService {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ParserService(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public ResponseEntity<Resource> parsing(ParserDto parserDto) {
        List<ClassFile> classFileList = new ArrayList<>();
        for (String tableName : parserDto.getTableNameList()) {
            List<Map<String, Object>> columnMaps = namedParameterJdbcTemplate.query(Queries.SelectOracleTableColumnWithComment, Map.of("schema", "public", "name", tableName), new OracleColumnRowMapper());
            List<ColumnData> columnDataList = columnMaps.stream()
                    .map(rowMap -> {
                        String row = rowMap.get("column_id").toString();
                        String dataType = rowMap.get("data_type").toString();
                        String columnName = rowMap.get("column_name").toString();
                        String comment = rowMap.get("column_comment").toString();
                        return new ColumnData(row, dataType, convertSnakeToCamelCase(columnName), comment);
                    })
                    .sorted(Comparator.comparing(ColumnData::getRow))
                    .distinct()
                    .toList();
            List<String> pkList = namedParameterJdbcTemplate.queryForList(Queries.SelectPrimaryKey, Map.of("schema", "public", "name", tableName)).stream().map(value -> convertSnakeToCamelCase(value.get("COLUMN_NAME").toString())).distinct().toList();
            Table table = new Table(capitalize(tableName.toLowerCase()), columnDataList, pkList);
            classFileList.add(genEntityClass(table));
        }
        classFileList.sort(Comparator.comparingInt(o -> o.getFileName().length()));
        return createZipFile(classFileList);
    }

    private ClassFile genEntityClass(Table table) {
        String tableClassName = table.getTableName();
        CompilationUnit cu = new CompilationUnit();
        ClassOrInterfaceDeclaration entityClass = cu.addClass(tableClassName).setPublic(true);
        //annotation
        entityClass.tryAddImportToParentCompilationUnit(Getter.class);
        entityClass.addAnnotation(new MarkerAnnotationExpr(Getter.class.getSimpleName()));
        entityClass.tryAddImportToParentCompilationUnit(Setter.class);
        entityClass.addAnnotation(new MarkerAnnotationExpr(Setter.class.getSimpleName()));
        entityClass.tryAddImportToParentCompilationUnit(NoArgsConstructor.class);
        entityClass.addAnnotation(new MarkerAnnotationExpr(NoArgsConstructor.class.getSimpleName()));

        //extends
        entityClass.addExtendedType("BaseEntity");

        //import
        cu.addImport("org.springframework.beans.BeanUtils");
        List<String> typeList = table.getColumnDataList().stream().map(ColumnData::getDataType).toList();
        if (typeList.contains("LocalDateTime")) {
            cu.addImport("java.time.LocalDateTime");
        }

        //field
        for (ColumnData columnData : table.getColumnDataList()) {
            FieldDeclaration fieldDeclaration = entityClass.addPrivateField(columnData.getDataType(), columnData.getColumnName()).setPrivate(true);
            fieldDeclaration.setLineComment(columnData.getColumnComment());
        }

        //method
        //1. toString
        MethodDeclaration toStringMethod = entityClass.addMethod("toString", Modifier.Keyword.PUBLIC);
        toStringMethod.setType("String");
        toStringMethod.addAnnotation(new MarkerAnnotationExpr(Override.class.getSimpleName()));
        toStringMethod.getBody().ifPresent(body -> {
            body.addStatement("return toJson();");
        });
        //2. fromJson
        MethodDeclaration fromJsonMethod = entityClass.addMethod("fromJson", Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);
        fromJsonMethod.setType(tableClassName);
        fromJsonMethod.addParameter("String", "json");
        fromJsonMethod.getBody().ifPresent(body -> {
            body.addStatement("return JsonUtil.fromJson(json, " + tableClassName + ".class);");
        });

        return new ClassFile(tableClassName + ".java", cu.toString(), "");
    }

    private ResponseEntity<Resource> createZipFile(List<ClassFile> classFiles) {
        ByteArrayOutputStream byteArrayOutputStream = getByteArrayOutputStream(classFiles);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "migration_table.zip");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(byteArrayOutputStream.size());

        InputStreamResource resource = new InputStreamResource(byteArrayInputStream);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(byteArrayOutputStream.size())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    private ByteArrayOutputStream getByteArrayOutputStream(List<ClassFile> classFiles) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            for (ClassFile classFile : classFiles) {
                ZipEntry zipEntry = new ZipEntry(classFile.getFileName());
                zipEntry.setComment(classFile.getComment());
                zipOutputStream.putNextEntry(zipEntry);
                byte[] data = classFile.getSource().getBytes(StandardCharsets.UTF_8);
                zipOutputStream.write(data, 0, data.length);
                zipOutputStream.closeEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error creating zip file", e);
        }
        return byteArrayOutputStream;
    }


    private String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    private String convertSnakeToCamelCase(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }
        String[] parts = input.toLowerCase().split("_");
        StringBuilder camelCase = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            camelCase.append(capitalize(parts[i]));
        }
        return camelCase.toString();
    }
}
