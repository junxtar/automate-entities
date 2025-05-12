package com.example.junxtar.javaparser.query;

public class Queries {
    public static String SelectTableComment =
            """
             SELECT A.TABLE_NAME
                   ,A.COMMENTS AS TABLE_COMMENT
                   FROM ALL_TAB_COMMENTS A
                   WHERE A.OWNER = UPPER(:schema)
            """;

    public static String SelectOracleTableColumnWithComment =
            """
                    SELECT A.COLUMN_ID
                         , A.COLUMN_NAME
                         ,
                         (CASE WHEN A.data_type = 'NUMBER' THEN
                              CASE WHEN A.data_scale > 0 THEN 'Double'
                                   WHEN A.data_precision <= 10 THEN 'Integer'
                                   ELSE 'Long'
                              END
                                WHEN A.data_type IN ('VARCHAR2', 'CHAR', 'CLOB') THEN 'String '
                                WHEN A.data_type = 'DATE' THEN 'LocalDateTime'
                                ELSE 'Object'
                              END) AS DATA_TYPE
                         , DECODE(NULLABLE, 'N', 'N') NULLABLE
                         , col_comments.COMMENTS AS COLUMN_COMMENT
                    FROM ALL_TAB_COLUMNS A
                    LEFT JOIN ALL_COL_COMMENTS col_comments
                        ON A.OWNER = col_comments.OWNER
                        AND A.TABLE_NAME = col_comments.TABLE_NAME
                        AND A.COLUMN_NAME = col_comments.COLUMN_NAME
                    WHERE A.OWNER = UPPER(:schema)
                      AND A.TABLE_NAME = UPPER(:name)
                    ORDER BY A.COLUMN_ID
                    """;

    public static String SelectPrimaryKey =
            """ 
                    SELECT A.TABLE_NAME
                        , A.CONSTRAINT_NAME
                        , B.COLUMN_NAME
                        , B.POSITION
                     FROM ALL_CONSTRAINTS  A
                        , ALL_CONS_COLUMNS B
                    WHERE A.OWNER           = :schema
                      AND A.TABLE_NAME      = :name
                      AND A.CONSTRAINT_TYPE = 'P'
                      AND A.OWNER           = B.OWNER
                      AND A.CONSTRAINT_NAME = B.CONSTRAINT_NAME
                    ORDER BY B.POSITION
                    """;


}
