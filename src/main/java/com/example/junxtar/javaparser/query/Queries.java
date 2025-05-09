package com.example.junxtar.javaparser.query;

public class Queries {
    public static String SelectTableComment =
            """
            SELECT c.relname AS table_name,
            obj_description(c.oid) AS table_comment
            FROM pg_class c
            JOIN pg_namespace n ON n.oid = c.relnamespace
            WHERE c.relkind = 'r'  -- 'r' = ordinary table
            AND n.nspname = :schema;
            """;
}
