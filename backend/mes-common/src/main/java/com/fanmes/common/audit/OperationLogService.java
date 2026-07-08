package com.fanmes.common.audit;

import com.fanmes.common.id.IdGenerator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OperationLogService {
    private final NamedParameterJdbcTemplate jdbc;

    public OperationLogService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordStatusChange(
            String moduleName,
            String targetTable,
            Long targetId,
            String actionName,
            String oldStatus,
            String newStatus,
            Long operatorId,
            String remark
    ) {
        jdbc.update("""
                insert into mes_operation_log (
                    id, module_name, target_table, target_id, action_name,
                    old_status, new_status, operator_id, remark, operated_at
                )
                values (
                    :id, :moduleName, :targetTable, :targetId, :actionName,
                    :oldStatus, :newStatus, :operatorId, :remark, :operatedAt
                )
                """, new MapSqlParameterSource()
                .addValue("id", IdGenerator.nextId())
                .addValue("moduleName", moduleName)
                .addValue("targetTable", targetTable)
                .addValue("targetId", targetId)
                .addValue("actionName", actionName)
                .addValue("oldStatus", oldStatus)
                .addValue("newStatus", newStatus)
                .addValue("operatorId", operatorId)
                .addValue("remark", trim(remark))
                .addValue("operatedAt", LocalDateTime.now()));
    }

    private String trim(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
