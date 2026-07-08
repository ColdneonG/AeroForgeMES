package com.fanmes.common.idempotency;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IdempotencyService {
    private final NamedParameterJdbcTemplate jdbc;

    public IdempotencyService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<String> findBusinessId(String moduleName, String actionName, String key) {
        if (!StringUtils.hasText(key)) {
            return Optional.empty();
        }
        List<String> rows = jdbc.queryForList("""
                select business_id
                from mes_idempotency_record
                where module_name = :moduleName
                  and action_name = :actionName
                  and idempotency_key = :idempotencyKey
                """, params(moduleName, actionName, key, null), String.class);
        return rows.stream().findFirst();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean record(String moduleName, String actionName, String key, String businessId) {
        if (!StringUtils.hasText(key)) {
            return true;
        }
        try {
            jdbc.update("""
                    insert into mes_idempotency_record (
                        module_name, action_name, idempotency_key, business_id, created_at
                    )
                    values (
                        :moduleName, :actionName, :idempotencyKey, :businessId, :createdAt
                    )
                    """, params(moduleName, actionName, key, businessId)
                    .addValue("createdAt", LocalDateTime.now()));
            return true;
        } catch (DuplicateKeyException ignored) {
            return false;
        }
    }

    private MapSqlParameterSource params(String moduleName, String actionName, String key, String businessId) {
        return new MapSqlParameterSource()
                .addValue("moduleName", moduleName)
                .addValue("actionName", actionName)
                .addValue("idempotencyKey", key.trim())
                .addValue("businessId", businessId);
    }
}
