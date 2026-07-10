package com.fanmes.equipment.domain;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public record TableSpec(
        String resource,
        String table,
        List<String> writableColumns,
        List<String> requiredColumns,
        List<String> filterColumns,
        String statusColumn,
        String defaultStatus,
        String fromClause,
        String columnPrefix,
        String selectExpression
) {
    public TableSpec(String resource, String table,
                     List<String> writableColumns,
                     List<String> requiredColumns,
                     List<String> filterColumns,
                     String statusColumn, String defaultStatus) {
        this(resource, table, writableColumns, requiredColumns, filterColumns,
                statusColumn, defaultStatus, null, null, null);
    }

    public TableSpec(String resource, String table,
                     List<String> writableColumns,
                     List<String> requiredColumns,
                     List<String> filterColumns,
                     String statusColumn, String defaultStatus,
                     String fromClause, String columnPrefix) {
        this(resource, table, writableColumns, requiredColumns, filterColumns,
                statusColumn, defaultStatus, fromClause, columnPrefix, null);
    }

    public Set<String> writableSet() {
        return new LinkedHashSet<>(writableColumns);
    }

    public Set<String> filterSet() {
        return new LinkedHashSet<>(filterColumns);
    }

    public boolean hasStatus() {
        return statusColumn != null && !statusColumn.isBlank();
    }
}
