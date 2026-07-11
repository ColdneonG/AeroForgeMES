package com.fanmes.production.infrastructure;

import com.fanmes.production.domain.barcode.BarcodeApplicationRule;
import com.fanmes.production.domain.barcode.BarcodeItemOption;
import com.fanmes.production.domain.barcode.BarcodePrintRecord;
import com.fanmes.production.domain.barcode.BarcodeRecord;
import com.fanmes.production.domain.barcode.BarcodeRule;
import com.fanmes.production.domain.barcode.BarcodeTemplate;
import com.fanmes.production.domain.barcode.BarcodeTraceEvent;
import com.fanmes.production.domain.barcode.BarcodeType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class BarcodeRepository {
    private static final String RULE_SELECT = """
            select r.id, r.rule_code, r.rule_name, r.type_id,
                   t.type_code, t.type_name, r.rule_expression, r.serial_length, r.status,
                   ar.id application_rule_id, ar.app_rule_code application_rule_code,
                   ar.item_id, item.item_code, item.item_name,
                   ar.template_id, tpl.template_code, tpl.template_name,
                   ar.barcode_mode, ar.source_type
            from bc_rule r
            left join bc_type t on t.id = r.type_id
            left join bc_application_rule ar on ar.id = (
                select min(ar2.id) from bc_application_rule ar2
                where ar2.rule_id = r.id and coalesce(ar2.status, '') <> '作废'
            )
            left join bc_template tpl on tpl.id = ar.template_id
            left join mes_system.md_item item on item.id = ar.item_id
            """;

    private static final String APPLICATION_SELECT = """
            select ar.id, ar.app_rule_code, ar.item_id, item.item_code, item.item_name,
                   ar.type_id, t.type_code, t.type_name,
                   ar.rule_id, r.rule_code, r.rule_name, r.rule_expression, r.serial_length,
                   ar.template_id, tpl.template_code, tpl.template_name,
                   ar.barcode_mode, ar.source_type, ar.status
            from bc_application_rule ar
            left join mes_system.md_item item on item.id = ar.item_id
            left join bc_type t on t.id = ar.type_id
            left join bc_rule r on r.id = ar.rule_id
            left join bc_template tpl on tpl.id = ar.template_id
            """;

    private static final String BARCODE_SELECT = """
            select b.id, b.barcode_value, b.type_id, t.type_code, t.type_name,
                   b.app_rule_id, ar.app_rule_code,
                   b.item_id, item.item_code, item.item_name, b.batch_no,
                   b.work_order_id, wo.work_order_no, b.task_id, task.task_no,
                   b.parent_barcode_id, parent.barcode_value parent_barcode_value,
                   coalesce(b.print_count, 0) print_count, b.source_type, b.status,
                   (select min(te.event_at) from trace_event te
                    where te.barcode_id = b.id and te.event_type = '生成') generated_at
            from bc_barcode b
            left join bc_type t on t.id = b.type_id
            left join bc_application_rule ar on ar.id = b.app_rule_id
            left join mes_system.md_item item on item.id = b.item_id
            left join prod_work_order wo on wo.id = b.work_order_id
            left join shop_task task on task.id = b.task_id
            left join bc_barcode parent on parent.id = b.parent_barcode_id
            """;

    private final NamedParameterJdbcTemplate jdbc;

    public BarcodeRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<BarcodeType> listTypes(String status) {
        StringBuilder sql = new StringBuilder("""
                select id, type_code, type_name, unique_scope, status
                from bc_type where 1 = 1
                """);
        MapSqlParameterSource params = new MapSqlParameterSource();
        appendEquals(sql, params, "status", status);
        sql.append(" order by type_code");
        return jdbc.query(sql.toString(), params, typeMapper());
    }

    public List<BarcodeItemOption> listItems(String keyword, String status) {
        StringBuilder sql = new StringBuilder("""
                select id, item_code, item_name, item_type, status
                from mes_system.md_item where 1 = 1
                """);
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (StringUtils.hasText(keyword)) {
            sql.append(" and (item_code like :keyword or item_name like :keyword)");
            params.addValue("keyword", "%" + keyword.trim() + "%");
        }
        appendEquals(sql, params, "status", status);
        sql.append(" order by item_type, item_code");
        return jdbc.query(sql.toString(), params, (rs, rowNum) -> new BarcodeItemOption(
                rs.getLong("id"), rs.getString("item_code"), rs.getString("item_name"),
                rs.getString("item_type"), rs.getString("status")
        ));
    }

    public Optional<BarcodeType> findTypeById(Long id) {
        return findOne("""
                select id, type_code, type_name, unique_scope, status
                from bc_type where id = :id
                """, Map.of("id", id), typeMapper());
    }

    public List<BarcodeTemplate> listTemplates(Long typeId, String status) {
        StringBuilder sql = new StringBuilder("""
                select tpl.id, tpl.template_code, tpl.template_name, tpl.type_id,
                       t.type_code, t.type_name, tpl.template_content,
                       tpl.paper_width, tpl.paper_height, tpl.status
                from bc_template tpl
                left join bc_type t on t.id = tpl.type_id
                where 1 = 1
                """);
        MapSqlParameterSource params = new MapSqlParameterSource();
        appendEquals(sql, params, "tpl.type_id", "typeId", typeId);
        appendEquals(sql, params, "tpl.status", "status", status);
        sql.append(" order by tpl.template_code");
        return jdbc.query(sql.toString(), params, templateMapper());
    }

    public Optional<BarcodeTemplate> findTemplateById(Long id) {
        return findOne("""
                select tpl.id, tpl.template_code, tpl.template_name, tpl.type_id,
                       t.type_code, t.type_name, tpl.template_content,
                       tpl.paper_width, tpl.paper_height, tpl.status
                from bc_template tpl
                left join bc_type t on t.id = tpl.type_id
                where tpl.id = :id
                """, Map.of("id", id), templateMapper());
    }

    public List<BarcodeRule> listRules(String keyword, Long typeId, String status) {
        StringBuilder sql = new StringBuilder(RULE_SELECT).append(" where 1 = 1");
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (StringUtils.hasText(keyword)) {
            sql.append(" and (r.rule_code like :keyword or r.rule_name like :keyword or r.rule_expression like :keyword)");
            params.addValue("keyword", "%" + keyword.trim() + "%");
        }
        appendEquals(sql, params, "r.type_id", "typeId", typeId);
        appendEquals(sql, params, "r.status", "status", status);
        sql.append(" order by r.id desc");
        return jdbc.query(sql.toString(), params, ruleMapper());
    }

    public Optional<BarcodeRule> findRuleById(Long id) {
        return findOne(RULE_SELECT + " where r.id = :id", Map.of("id", id), ruleMapper());
    }

    public void insertRule(BarcodeRule rule) {
        jdbc.update("""
                insert into bc_rule (id, rule_code, rule_name, type_id, rule_expression, serial_length, status)
                values (:id, :ruleCode, :ruleName, :typeId, :ruleExpression, :serialLength, :status)
                """, ruleParams(rule));
    }

    public int updateRule(BarcodeRule rule) {
        return jdbc.update("""
                update bc_rule
                set rule_code = :ruleCode, rule_name = :ruleName, type_id = :typeId,
                    rule_expression = :ruleExpression, serial_length = :serialLength, status = :status
                where id = :id
                """, ruleParams(rule));
    }

    public int updateRuleStatus(Long id, String oldStatus, String newStatus) {
        return jdbc.update("""
                update bc_rule set status = :newStatus
                where id = :id and status = :oldStatus
                """, Map.of("id", id, "oldStatus", oldStatus, "newStatus", newStatus));
    }

    public int deleteRule(Long id) {
        return jdbc.update("delete from bc_rule where id = :id", Map.of("id", id));
    }

    public int countApplicationRulesByRule(Long ruleId) {
        return count("select count(*) from bc_application_rule where rule_id = :id", ruleId);
    }

    public int countBarcodesByRule(Long ruleId) {
        return count("""
                select count(*) from bc_barcode b
                join bc_application_rule ar on ar.id = b.app_rule_id
                where ar.rule_id = :id
                """, ruleId);
    }

    public List<BarcodeApplicationRule> listApplicationRules(
            String keyword,
            Long itemId,
            Long typeId,
            String status
    ) {
        StringBuilder sql = new StringBuilder(APPLICATION_SELECT).append(" where 1 = 1");
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (StringUtils.hasText(keyword)) {
            sql.append(" and (ar.app_rule_code like :keyword or item.item_code like :keyword or item.item_name like :keyword)");
            params.addValue("keyword", "%" + keyword.trim() + "%");
        }
        appendEquals(sql, params, "ar.item_id", "itemId", itemId);
        appendEquals(sql, params, "ar.type_id", "typeId", typeId);
        appendEquals(sql, params, "ar.status", "status", status);
        sql.append(" order by ar.id desc");
        return jdbc.query(sql.toString(), params, applicationMapper());
    }

    public Optional<BarcodeApplicationRule> findApplicationRuleById(Long id) {
        return findOne(APPLICATION_SELECT + " where ar.id = :id", Map.of("id", id), applicationMapper());
    }

    public Optional<BarcodeApplicationRule> findEnabledApplicationByRuleId(Long ruleId) {
        return findOne(APPLICATION_SELECT + """
                where ar.rule_id = :ruleId and ar.status = '启用'
                order by ar.id limit 1
                """, Map.of("ruleId", ruleId), applicationMapper());
    }

    public void insertApplicationRule(BarcodeApplicationRule rule) {
        jdbc.update("""
                insert into bc_application_rule (
                    id, app_rule_code, item_id, type_id, rule_id, template_id,
                    barcode_mode, source_type, status
                ) values (
                    :id, :applicationRuleCode, :itemId, :typeId, :ruleId, :templateId,
                    :barcodeMode, :sourceType, :status
                )
                """, applicationParams(rule));
    }

    public int updateApplicationRule(BarcodeApplicationRule rule) {
        return jdbc.update("""
                update bc_application_rule
                set app_rule_code = :applicationRuleCode, item_id = :itemId, type_id = :typeId,
                    rule_id = :ruleId, template_id = :templateId, barcode_mode = :barcodeMode,
                    source_type = :sourceType, status = :status
                where id = :id
                """, applicationParams(rule));
    }

    public int deleteApplicationRule(Long id) {
        return jdbc.update("delete from bc_application_rule where id = :id", Map.of("id", id));
    }

    public int countBarcodesByApplicationRule(Long id) {
        return count("select count(*) from bc_barcode where app_rule_id = :id", id);
    }

    public Lookup findItem(Long id) {
        if (id == null) {
            return null;
        }
        return findOne("""
                select id, item_code code, item_name name
                from mes_system.md_item where id = :id
                """, Map.of("id", id), lookupMapper()).orElse(null);
    }

    public Lookup findWorkOrder(Long id) {
        if (id == null) {
            return null;
        }
        return findOne("""
                select id, work_order_no code, work_order_no name
                from prod_work_order where id = :id
                """, Map.of("id", id), lookupMapper()).orElse(null);
    }

    public Lookup findTask(Long id) {
        if (id == null) {
            return null;
        }
        return findOne("""
                select id, task_no code, task_no name
                from shop_task where id = :id
                """, Map.of("id", id), lookupMapper()).orElse(null);
    }

    public long reserveSequence(Long ruleId, String sequenceKey, int quantity) {
        jdbc.update("""
                insert into bc_rule_sequence (rule_id, sequence_key, current_value, updated_at)
                values (:ruleId, :sequenceKey, :quantity, :updatedAt)
                on duplicate key update
                    current_value = current_value + :quantity,
                    updated_at = :updatedAt
                """, new MapSqlParameterSource()
                .addValue("ruleId", ruleId)
                .addValue("sequenceKey", sequenceKey)
                .addValue("quantity", quantity)
                .addValue("updatedAt", LocalDateTime.now()));
        Long current = jdbc.queryForObject("""
                select current_value from bc_rule_sequence
                where rule_id = :ruleId and sequence_key = :sequenceKey
                """, Map.of("ruleId", ruleId, "sequenceKey", sequenceKey), Long.class);
        return current == null ? quantity : current;
    }

    public List<BarcodeRecord> listBarcodes(
            String keyword,
            String status,
            Long typeId,
            Long workOrderId,
            Long taskId,
            String batchNo
    ) {
        StringBuilder sql = new StringBuilder(BARCODE_SELECT).append(" where 1 = 1");
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (StringUtils.hasText(keyword)) {
            sql.append("""
                     and (b.barcode_value like :keyword or b.batch_no like :keyword
                          or item.item_code like :keyword or item.item_name like :keyword
                          or wo.work_order_no like :keyword or task.task_no like :keyword)
                    """);
            params.addValue("keyword", "%" + keyword.trim() + "%");
        }
        appendEquals(sql, params, "b.status", "status", status);
        appendEquals(sql, params, "b.type_id", "typeId", typeId);
        appendEquals(sql, params, "b.work_order_id", "workOrderId", workOrderId);
        appendEquals(sql, params, "b.task_id", "taskId", taskId);
        appendEquals(sql, params, "b.batch_no", "batchNo", batchNo);
        sql.append(" order by b.id desc");
        return jdbc.query(sql.toString(), params, barcodeMapper());
    }

    public Optional<BarcodeRecord> findBarcodeById(Long id) {
        return findOne(BARCODE_SELECT + " where b.id = :id", Map.of("id", id), barcodeMapper());
    }

    public Optional<BarcodeRecord> findBarcodeByValue(String value) {
        return findOne(BARCODE_SELECT + " where b.barcode_value = :value", Map.of("value", value), barcodeMapper());
    }

    public void insertBarcode(BarcodeRecord barcode) {
        jdbc.update("""
                insert into bc_barcode (
                    id, barcode_value, type_id, app_rule_id, item_id, batch_no,
                    work_order_id, task_id, parent_barcode_id, print_count, source_type, status
                ) values (
                    :id, :barcodeValue, :typeId, :applicationRuleId, :itemId, :batchNo,
                    :workOrderId, :taskId, :parentBarcodeId, :printCount, :sourceType, :status
                )
                """, barcodeParams(barcode));
    }

    public int addPrintCount(Long id, int copies) {
        return jdbc.update("""
                update bc_barcode
                set print_count = coalesce(print_count, 0) + :copies, status = '已打印'
                where id = :id and coalesce(status, '') not in ('已关闭', '作废')
                """, Map.of("id", id, "copies", copies));
    }

    public int updateBarcodeStatus(Long id, String status) {
        return jdbc.update("""
                update bc_barcode set status = :status
                where id = :id and coalesce(status, '') <> '作废'
                """, Map.of("id", id, "status", status));
    }

    public int updateParent(Long id, Long parentId) {
        return jdbc.update("update bc_barcode set parent_barcode_id = :parentId where id = :id",
                new MapSqlParameterSource().addValue("id", id).addValue("parentId", parentId));
    }

    public void insertPrintRecord(BarcodePrintRecord record) {
        jdbc.update("""
                insert into bc_print_record (
                    id, barcode_id, template_id, print_by, print_at, print_count, printer_name
                ) values (
                    :id, :barcodeId, :templateId, :printBy, :printAt, :printCount, :printerName
                )
                """, new MapSqlParameterSource()
                .addValue("id", record.id())
                .addValue("barcodeId", record.barcodeId())
                .addValue("templateId", record.templateId())
                .addValue("printBy", record.printBy())
                .addValue("printAt", record.printAt())
                .addValue("printCount", record.printCount())
                .addValue("printerName", record.printerName()));
    }

    public List<BarcodePrintRecord> listPrintRecords(Long barcodeId) {
        return jdbc.query("""
                select pr.id, pr.barcode_id, b.barcode_value, pr.template_id, tpl.template_name,
                       pr.print_by, u.display_name print_by_name, pr.print_at,
                       pr.print_count, pr.printer_name
                from bc_print_record pr
                left join bc_barcode b on b.id = pr.barcode_id
                left join bc_template tpl on tpl.id = pr.template_id
                left join mes_auth.sys_user u on u.id = pr.print_by
                where pr.barcode_id = :barcodeId
                order by pr.print_at desc, pr.id desc
                """, Map.of("barcodeId", barcodeId), printRecordMapper());
    }

    public void insertTraceEvent(BarcodeTraceEvent event) {
        jdbc.update("""
                insert into trace_event (
                    id, barcode_id, barcode_value, event_type, biz_type, biz_id,
                    work_order_id, task_id, process_id, station_id, device_id, operator_id, event_at
                ) values (
                    :id, :barcodeId, :barcode, :eventType, :bizType, :bizId,
                    :workOrderId, :taskId, :processId, :stationId, :deviceId, :operatorId, :eventAt
                )
                """, new MapSqlParameterSource()
                .addValue("id", event.id())
                .addValue("barcodeId", event.barcodeId())
                .addValue("barcode", event.barcode())
                .addValue("eventType", event.eventType())
                .addValue("bizType", event.bizType())
                .addValue("bizId", event.bizId())
                .addValue("workOrderId", event.workOrderId())
                .addValue("taskId", event.taskId())
                .addValue("processId", event.processId())
                .addValue("stationId", event.stationId())
                .addValue("deviceId", event.deviceId())
                .addValue("operatorId", event.operatorId())
                .addValue("eventAt", event.eventAt()));
    }

    public List<BarcodeTraceEvent> listTraceEvents(List<Long> barcodeIds) {
        if (barcodeIds == null || barcodeIds.isEmpty()) {
            return List.of();
        }
        return jdbc.query("""
                select te.id, te.barcode_id, te.barcode_value, te.event_type, te.biz_type, te.biz_id,
                       te.work_order_id, wo.work_order_no, te.task_id, task.task_no,
                       te.process_id, process.process_name, te.station_id, station.station_name,
                       te.device_id, equipment.equipment_name,
                       te.operator_id, operator.display_name operator_name, te.event_at,
                       '成功' result
                from trace_event te
                left join prod_work_order wo on wo.id = te.work_order_id
                left join shop_task task on task.id = te.task_id
                left join route_process process on process.id = te.process_id
                left join mes_system.md_workstation station on station.id = te.station_id
                left join mes_equipment.eqp_equipment equipment on equipment.id = te.device_id
                left join mes_auth.sys_user operator on operator.id = te.operator_id
                where te.barcode_id in (:barcodeIds)
                order by te.event_at, te.id
                """, Map.of("barcodeIds", barcodeIds), traceEventMapper());
    }

    public List<BarcodeRecord> findBarcodesForTrace(String mode, String keyword) {
        String normalized = mode == null ? "SN" : mode.trim().toUpperCase();
        String where = switch (normalized) {
            case "WORK_ORDER" -> " where wo.work_order_no = :keyword or cast(b.work_order_id as char) = :keyword";
            case "BATCH" -> " where b.batch_no = :keyword";
            default -> " where b.barcode_value = :keyword";
        };
        return jdbc.query(BARCODE_SELECT + where + " order by b.id", Map.of("keyword", keyword), barcodeMapper());
    }

    public List<BarcodeRecord> findChildren(Long parentId) {
        return jdbc.query(BARCODE_SELECT + " where b.parent_barcode_id = :parentId order by b.id",
                Map.of("parentId", parentId), barcodeMapper());
    }

    public List<BarcodeRecord> findMaterialBarcodes(List<Long> productBarcodeIds) {
        if (productBarcodeIds == null || productBarcodeIds.isEmpty()) {
            return List.of();
        }
        return jdbc.query(BARCODE_SELECT + """
                join shop_report_material rm on rm.material_barcode_id = b.id
                join shop_report report on report.id = rm.report_id
                where report.barcode_id in (:ids)
                order by b.id
                """, Map.of("ids", productBarcodeIds), barcodeMapper());
    }

    public List<BarcodeRecord> findProductBarcodes(List<Long> materialBarcodeIds) {
        if (materialBarcodeIds == null || materialBarcodeIds.isEmpty()) {
            return List.of();
        }
        return jdbc.query(BARCODE_SELECT + """
                join shop_report report on report.barcode_id = b.id
                join shop_report_material rm on rm.report_id = report.id
                where rm.material_barcode_id in (:ids)
                order by b.id
                """, Map.of("ids", materialBarcodeIds), barcodeMapper());
    }

    private MapSqlParameterSource ruleParams(BarcodeRule rule) {
        return new MapSqlParameterSource()
                .addValue("id", rule.id())
                .addValue("ruleCode", rule.ruleCode())
                .addValue("ruleName", rule.ruleName())
                .addValue("typeId", rule.typeId())
                .addValue("ruleExpression", rule.ruleExpression())
                .addValue("serialLength", rule.serialLength())
                .addValue("status", rule.status());
    }

    private MapSqlParameterSource applicationParams(BarcodeApplicationRule rule) {
        return new MapSqlParameterSource()
                .addValue("id", rule.id())
                .addValue("applicationRuleCode", rule.applicationRuleCode())
                .addValue("itemId", rule.itemId())
                .addValue("typeId", rule.typeId())
                .addValue("ruleId", rule.ruleId())
                .addValue("templateId", rule.templateId())
                .addValue("barcodeMode", rule.barcodeMode())
                .addValue("sourceType", rule.sourceType())
                .addValue("status", rule.status());
    }

    private MapSqlParameterSource barcodeParams(BarcodeRecord barcode) {
        return new MapSqlParameterSource()
                .addValue("id", barcode.id())
                .addValue("barcodeValue", barcode.barcodeValue())
                .addValue("typeId", barcode.typeId())
                .addValue("applicationRuleId", barcode.applicationRuleId())
                .addValue("itemId", barcode.itemId())
                .addValue("batchNo", barcode.batchNo())
                .addValue("workOrderId", barcode.workOrderId())
                .addValue("taskId", barcode.taskId())
                .addValue("parentBarcodeId", barcode.parentBarcodeId())
                .addValue("printCount", barcode.printCount())
                .addValue("sourceType", barcode.sourceType())
                .addValue("status", barcode.status());
    }

    private RowMapper<BarcodeType> typeMapper() {
        return (rs, rowNum) -> new BarcodeType(
                rs.getLong("id"), rs.getString("type_code"), rs.getString("type_name"),
                rs.getString("unique_scope"), rs.getString("status")
        );
    }

    private RowMapper<BarcodeTemplate> templateMapper() {
        return (rs, rowNum) -> new BarcodeTemplate(
                rs.getLong("id"), rs.getString("template_code"), rs.getString("template_name"),
                getLong(rs, "type_id"), rs.getString("type_code"), rs.getString("type_name"),
                rs.getString("template_content"), rs.getBigDecimal("paper_width"),
                rs.getBigDecimal("paper_height"), rs.getString("status")
        );
    }

    private RowMapper<BarcodeRule> ruleMapper() {
        return (rs, rowNum) -> new BarcodeRule(
                rs.getLong("id"), rs.getString("rule_code"), rs.getString("rule_name"),
                getLong(rs, "type_id"), rs.getString("type_code"), rs.getString("type_name"),
                rs.getString("rule_expression"), getInteger(rs, "serial_length"), rs.getString("status"),
                getLong(rs, "application_rule_id"), rs.getString("application_rule_code"),
                getLong(rs, "item_id"), rs.getString("item_code"), rs.getString("item_name"),
                getLong(rs, "template_id"), rs.getString("template_code"), rs.getString("template_name"),
                rs.getString("barcode_mode"), rs.getString("source_type")
        );
    }

    private RowMapper<BarcodeApplicationRule> applicationMapper() {
        return (rs, rowNum) -> new BarcodeApplicationRule(
                rs.getLong("id"), rs.getString("app_rule_code"),
                getLong(rs, "item_id"), rs.getString("item_code"), rs.getString("item_name"),
                getLong(rs, "type_id"), rs.getString("type_code"), rs.getString("type_name"),
                getLong(rs, "rule_id"), rs.getString("rule_code"), rs.getString("rule_name"),
                rs.getString("rule_expression"), getInteger(rs, "serial_length"),
                getLong(rs, "template_id"), rs.getString("template_code"), rs.getString("template_name"),
                rs.getString("barcode_mode"), rs.getString("source_type"), rs.getString("status")
        );
    }

    private RowMapper<BarcodeRecord> barcodeMapper() {
        return (rs, rowNum) -> new BarcodeRecord(
                rs.getLong("id"), rs.getString("barcode_value"),
                getLong(rs, "type_id"), rs.getString("type_code"), rs.getString("type_name"),
                getLong(rs, "app_rule_id"), rs.getString("app_rule_code"),
                getLong(rs, "item_id"), rs.getString("item_code"), rs.getString("item_name"),
                rs.getString("batch_no"), getLong(rs, "work_order_id"), rs.getString("work_order_no"),
                getLong(rs, "task_id"), rs.getString("task_no"),
                getLong(rs, "parent_barcode_id"), rs.getString("parent_barcode_value"),
                getInteger(rs, "print_count"), rs.getString("source_type"), rs.getString("status"),
                getDateTime(rs, "generated_at")
        );
    }

    private RowMapper<BarcodePrintRecord> printRecordMapper() {
        return (rs, rowNum) -> new BarcodePrintRecord(
                rs.getLong("id"), getLong(rs, "barcode_id"), rs.getString("barcode_value"),
                getLong(rs, "template_id"), rs.getString("template_name"),
                getLong(rs, "print_by"), rs.getString("print_by_name"),
                getDateTime(rs, "print_at"), getInteger(rs, "print_count"), rs.getString("printer_name")
        );
    }

    private RowMapper<BarcodeTraceEvent> traceEventMapper() {
        return (rs, rowNum) -> new BarcodeTraceEvent(
                rs.getLong("id"), getLong(rs, "barcode_id"), rs.getString("barcode_value"),
                rs.getString("event_type"), rs.getString("biz_type"), getLong(rs, "biz_id"),
                getLong(rs, "work_order_id"), rs.getString("work_order_no"),
                getLong(rs, "task_id"), rs.getString("task_no"),
                getLong(rs, "process_id"), rs.getString("process_name"),
                getLong(rs, "station_id"), rs.getString("station_name"),
                getLong(rs, "device_id"), rs.getString("equipment_name"),
                getLong(rs, "operator_id"), rs.getString("operator_name"),
                getDateTime(rs, "event_at"), rs.getString("result")
        );
    }

    private RowMapper<Lookup> lookupMapper() {
        return (rs, rowNum) -> new Lookup(rs.getLong("id"), rs.getString("code"), rs.getString("name"));
    }

    private int count(String sql, Long id) {
        Integer value = jdbc.queryForObject(sql, Map.of("id", id), Integer.class);
        return value == null ? 0 : value;
    }

    private <T> Optional<T> findOne(String sql, Map<String, ?> params, RowMapper<T> mapper) {
        List<T> rows = jdbc.query(sql, params, mapper);
        return rows.stream().findFirst();
    }

    private void appendEquals(StringBuilder sql, MapSqlParameterSource params, String column, Object value) {
        appendEquals(sql, params, column, column.replace(".", "").replace("_", ""), value);
    }

    private void appendEquals(
            StringBuilder sql,
            MapSqlParameterSource params,
            String column,
            String parameter,
            Object value
    ) {
        if (value == null || value instanceof String text && !StringUtils.hasText(text)) {
            return;
        }
        sql.append(" and ").append(column).append(" = :").append(parameter);
        params.addValue(parameter, value instanceof String text ? text.trim() : value);
    }

    private Long getLong(ResultSet rs, String column) throws SQLException {
        long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }

    private Integer getInteger(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }

    private LocalDateTime getDateTime(ResultSet rs, String column) throws SQLException {
        return rs.getTimestamp(column) == null ? null : rs.getTimestamp(column).toLocalDateTime();
    }

    public record Lookup(Long id, String code, String name) {
    }
}
