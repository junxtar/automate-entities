package com.example.junxtar.javaparser.service;

import com.example.junxtar.javaparser.domain.Table;
import com.example.junxtar.javaparser.domain.vo.ColumnData;
import com.example.junxtar.javaparser.dto.ParserDto;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import io.micrometer.common.util.StringUtils;

import javassist.bytecode.ClassFile;
import lombok.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

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
@RequiredArgsConstructor
public class ParserService {

//    private final NamedParameterJdbcTemplate amisNamedParameterJdbcTemplate;
//
    public ResponseEntity<Resource> parsing(ParserDto parserDto) {
//        List<ClassFile> classFileList = new ArrayList<>();
//        for (String tableName : parserDto.getTableNameList()) {
//            List<Map<String, Object>> columnMaps = amisNamedParameterJdbcTemplate.query(Queries.SelectOracleTableColumnWithComment, Map.of("schema", "AMISDBA", "name", tableName), new OracleColumnRowMapper());
//            List<ColumnData> columnDataList = columnMaps.stream()
//                    .map(rowMap -> {
//                        String row = rowMap.get("column_id").toString();
//                        String dataType = rowMap.get("data_type").toString();
//                        String columnName = rowMap.get("column_name").toString();
//                        String comment = rowMap.get("column_comment").toString();
//                        return new ColumnData(row, dataType, convertSnakeToCamelCase(columnName), comment);
//                    })
//                    .sorted(Comparator.comparing(ColumnData::getRow))
//                    .distinct()
//                    .toList();
//            List<String> pkList = amisNamedParameterJdbcTemplate.queryForList(Queries.SelectPrimaryKey, Map.of("schema", "AMISDBA", "name", tableName)).stream().map(value -> convertSnakeToCamelCase(value.get("COLUMN_NAME").toString())).distinct().toList();
//            Table table = new Table(capitalize(tableName.toLowerCase()), columnDataList, pkList);
//            classFileList.add(genEntityClass(table));
//            classFileList.add(genCdoClass(table));
//        }
//        classFileList.sort(Comparator.comparingInt(o -> o.getFileName().length()));
//        return createZipFile(classFileList);
        return null;
    }
//
//    private ClassFile genEntityClass(Table table) {
//        String tableClassName = table.getTableName();
//        String tableName = table.getTableName().toLowerCase();
//        CompilationUnit cu = new CompilationUnit();
//        ClassOrInterfaceDeclaration entityClass = cu.addClass(tableClassName).setPublic(true);
//        //annotation
//        entityClass.tryAddImportToParentCompilationUnit(Getter.class);
//        entityClass.addAnnotation(new MarkerAnnotationExpr(Getter.class.getSimpleName()));
//        entityClass.tryAddImportToParentCompilationUnit(Setter.class);
//        entityClass.addAnnotation(new MarkerAnnotationExpr(Setter.class.getSimpleName()));
//        entityClass.tryAddImportToParentCompilationUnit(NoArgsConstructor.class);
//        entityClass.addAnnotation(new MarkerAnnotationExpr(NoArgsConstructor.class.getSimpleName()));
//        //extends
//        entityClass.addExtendedType("StageEntity");
//        //import
//        cu.addImport("io.vizend.accent.domain.entity.StageEntity");
//        cu.addImport("io.vizend.accent.domain.tenant.ActorKey");
//        cu.addImport("io.vizend.accent.util.json.JsonUtil");
//        cu.addImport("io.vizend.accent.domain.type.NameValueList");
//        cu.addImport("io.vizend.accent.domain.type.NameValue");
//        cu.addImport("io.vizend.accent.domain.annotation.FieldImmutable");
//        cu.addImport("org.springframework.beans.BeanUtils");
//        List<String> typeList = table.getColumnDataList().stream().map(ColumnData::getDataType).toList();
//        if (typeList.contains("LocalDateTime")) {
//            cu.addImport("java.time.LocalDateTime");
//        }
//        //enum
//        List<String> columnNames = table.getColumnDataList().stream().map(ColumnData::getColumnName).distinct().toList();
//        EnumDeclaration enumDeclaration = new EnumDeclaration();
//        enumDeclaration.setPublic(true);
//        enumDeclaration.setName(tableClassName + "Field");
//        enumDeclaration.addEntry(new EnumConstantDeclaration("id"));
//        columnNames.forEach(column -> enumDeclaration.addEntry(new EnumConstantDeclaration(column)));
//        entityClass.addMember(enumDeclaration);
//        //field
//        List<String> pkList = table.getPrimaryKeyList();
//        for (ColumnData columnData : table.getColumnDataList()) {
//            FieldDeclaration fieldDeclaration = entityClass.addPrivateField(columnData.getDataType(), columnData.getColumnName()).setPrivate(true);
//            fieldDeclaration.setLineComment(columnData.getColumnComment());
//            if (pkList.contains(columnData.getColumnName())) {
//                fieldDeclaration.addAnnotation(new MarkerAnnotationExpr("FieldImmutable"));
//            }
//        }
//        //constructor
//        ConstructorDeclaration constructor1 = entityClass.addConstructor(Modifier.Keyword.PUBLIC);
//        constructor1.setName(tableClassName);
//        constructor1.addParameter("String", "id");
//        constructor1.addParameter("ActorKey", "requesterKey");
//        constructor1.getBody().addStatement("super(id, requesterKey);");
//        ConstructorDeclaration constructor2 = entityClass.addConstructor(Modifier.Keyword.PUBLIC);
//        constructor2.setName(tableClassName);
//        constructor2.addParameter(tableClassName + "Cdo", tableName + "Cdo");
//        constructor2.getBody().addStatement("super(" + tableName + "Cdo.genId(), " + tableName + "Cdo.getRequesterKey());");
//        constructor2.getBody().addStatement("BeanUtils.copyProperties(" + tableName + "Cdo, this);");
//        //method
//        //1. toString
//        MethodDeclaration toStringMethod = entityClass.addMethod("toString", Modifier.Keyword.PUBLIC);
//        toStringMethod.setType("String");
//        toStringMethod.addAnnotation(new MarkerAnnotationExpr(Override.class.getSimpleName()));
//        toStringMethod.getBody().ifPresent(body -> {
//            body.addStatement("return toJson();");
//        });
//        //2. fromJson
//        MethodDeclaration fromJsonMethod = entityClass.addMethod("fromJson", Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);
//        fromJsonMethod.setType(tableClassName);
//        fromJsonMethod.addParameter("String", "json");
//        fromJsonMethod.getBody().ifPresent(body -> {
//            body.addStatement("return JsonUtil.fromJson(json, " + tableClassName + ".class);");
//        });
//        //3. modifyAttributes
//        MethodDeclaration modifyAttributesMethod = entityClass.addMethod("modifyAttributes", Modifier.Keyword.PROTECTED);
//        modifyAttributesMethod.setType("void"); // 반환 타입 설정
//        modifyAttributesMethod.addAnnotation(new MarkerAnnotationExpr(Override.class.getSimpleName()));
//        modifyAttributesMethod.addParameter("NameValueList", "nameValues");
//        modifyAttributesMethod.getBody().ifPresent(body -> {
//            String forLoopCode = """
//                    for (NameValue nameValue : nameValues.list()) {
//                        String value = nameValue.getValue();
//                        switch(nameValue.getName()) {
//                            default:
//                                throw new IllegalArgumentException("Update not allowed: " + nameValue);
//                        }
//                    }
//                    """;
//            body.addStatement(forLoopCode);
//        });
//        //4. sample 메서드 추가
//        MethodDeclaration sampleMethod = entityClass.addMethod("sample", Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);
//        sampleMethod.setType(tableClassName); // 반환 타입 설정
//        sampleMethod.getBody().ifPresent(body -> {
//            body.addStatement("return new " + tableClassName + "(" + tableClassName + "Cdo.sample());");
//        });
//        //5. main 메서드 추가
//        MethodDeclaration mainMethod = entityClass.addMethod("main", Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);
//        mainMethod.addParameter("String[]", "args");
//        mainMethod.getBody().ifPresent(body -> {
//            body.addStatement("System.out.println(sample().toPrettyJson());");
//        });
//        return new ClassFile(tableClassName + ".java", cu.toString(), "");
//    }
//
//    private ClassFile genCdoClass(Table table) {
//        String tableClassName = table.getTableName() + "Cdo";
//        CompilationUnit cu = new CompilationUnit();
//        ClassOrInterfaceDeclaration cdoClass = cu.addClass(tableClassName).setPublic(true);
//        //annotation
//        cdoClass.tryAddImportToParentCompilationUnit(Getter.class);
//        cdoClass.addAnnotation(new MarkerAnnotationExpr(Getter.class.getSimpleName()));
//        cdoClass.tryAddImportToParentCompilationUnit(Setter.class);
//        cdoClass.addAnnotation(new MarkerAnnotationExpr(Setter.class.getSimpleName()));
//        cdoClass.tryAddImportToParentCompilationUnit(Builder.class);
//        cdoClass.addAnnotation(new MarkerAnnotationExpr(Builder.class.getSimpleName()));
//        cdoClass.tryAddImportToParentCompilationUnit(NoArgsConstructor.class);
//        cdoClass.addAnnotation(new MarkerAnnotationExpr(NoArgsConstructor.class.getSimpleName()));
//        cdoClass.tryAddImportToParentCompilationUnit(AllArgsConstructor.class);
//        cdoClass.addAnnotation(new MarkerAnnotationExpr(AllArgsConstructor.class.getSimpleName()));
//        //extends
//        cdoClass.addExtendedType("CreationDataObject");
//        //import
//        cu.addImport("io.vizend.accent.domain.entity.CreationDataObject");
//        cu.addImport("io.vizend.accent.util.json.JsonUtil");
//        List<String> typeList = table.getColumnDataList().stream().map(ColumnData::getDataType).toList();
//        if (typeList.contains("LocalDateTime")) {
//            cu.addImport("java.time.LocalDateTime");
//        }
//        //field
//        for (ColumnData columnData : table.getColumnDataList()) {
//            FieldDeclaration fieldDeclaration = cdoClass.addPrivateField(columnData.getDataType(), columnData.getColumnName()).setPrivate(true);
//            fieldDeclaration.setLineComment(columnData.getColumnComment());
//        }
//        //method
//        //1. genId
//        cdoClass.addMethod("genId", Modifier.Keyword.PUBLIC)
//                .setType("String")
//                .addAnnotation(new MarkerAnnotationExpr(Override.class.getSimpleName()))
//                .setBody(new BlockStmt().addStatement(new ReturnStmt(
//                        new MethodCallExpr(new NameExpr("super"), "genId")
//                )));
//
//        //2. toString
//        MethodDeclaration toStringMethod = cdoClass.addMethod("toString", Modifier.Keyword.PUBLIC)
//                .setType("String")
//                .addAnnotation(new MarkerAnnotationExpr(Override.class.getSimpleName()));
//        toStringMethod.getBody().ifPresent(body -> {
//            body.addStatement("return toJson();");
//        });
//        //3. fromJson
//        MethodDeclaration fromJsonMethod = cdoClass.addMethod("fromJson", Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);
//        fromJsonMethod.setType(tableClassName);
//        fromJsonMethod.addParameter("String", "json");
//        fromJsonMethod.getBody().ifPresent(body -> {
//            body.addStatement("return JsonUtil.fromJson(json, " + tableClassName + ".class);");
//        });
//        //4. sample
//        MethodDeclaration modifyAttributesMethod = cdoClass.addMethod("sample", Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);
//        modifyAttributesMethod.setType(tableClassName); // 반환 타입 설정
//        modifyAttributesMethod.getBody().ifPresent(body -> {
//            body.addStatement("return new " + tableClassName + "();");
//        });
//        //5. main 메서드 추가
//        MethodDeclaration mainMethod = cdoClass.addMethod("main", Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);
//        mainMethod.addParameter("String[]", "args");
//        mainMethod.getBody().ifPresent(body -> {
//            body.addStatement("System.out.println(sample().toPrettyJson());");
//        });
//        return new ClassFile("sdo/" + tableClassName + ".java", cu.toString(), "cdo");
//    }
//
//    private ResponseEntity<Resource> createZipFile(List<ClassFile> classFiles) {
//        ByteArrayOutputStream byteArrayOutputStream = getByteArrayOutputStream(classFiles);
//        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentDispositionFormData("attachment", "migration_table.zip");
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        headers.setContentLength(byteArrayOutputStream.size());
//
//        InputStreamResource resource = new InputStreamResource(byteArrayInputStream);
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .contentLength(byteArrayOutputStream.size())
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(resource);
//    }
//
//    private ByteArrayOutputStream getByteArrayOutputStream(List<ClassFile> classFiles) {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
//            for (ClassFile classFile : classFiles) {
//                ZipEntry zipEntry = new ZipEntry(classFile.getFileName());
//                zipEntry.setComment(classFile.getComment());
//                zipOutputStream.putNextEntry(zipEntry);
//                byte[] data = classFile.getSource().getBytes(StandardCharsets.UTF_8);
//                zipOutputStream.write(data, 0, data.length);
//                zipOutputStream.closeEntry();
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Error creating zip file", e);
//        }
//        return byteArrayOutputStream;
//    }
//
//
//    private String capitalize(String input) {
//        if (input == null || input.isEmpty()) {
//            return input;
//        }
//        return input.substring(0, 1).toUpperCase() + input.substring(1);
//    }
//
//    private String convertSnakeToCamelCase(String input) {
//        if (StringUtils.isBlank(input)) {
//            return input;
//        }
//        String[] parts = input.toLowerCase().split("_");
//        StringBuilder camelCase = new StringBuilder(parts[0]);
//        for (int i = 1; i < parts.length; i++) {
//            camelCase.append(capitalize(parts[i]));
//        }
//        return camelCase.toString();
//    }
}
