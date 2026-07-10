package com.fanmes.quality.infrastructure;

import com.fanmes.quality.domain.QcDefectRecord;
import com.fanmes.quality.domain.QcInspectionOrder;
import com.fanmes.quality.domain.QcInspectionResult;
import com.fanmes.quality.domain.QcItem;
import com.fanmes.quality.domain.QcItemCategory;
import com.fanmes.quality.domain.QcPlan;
import com.fanmes.quality.domain.QcPlanItem;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class QualityRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public QualityRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<QcItemCategory> listItemCategories(String keyword, String status) {
        StringBuilder sql = new StringBuilder("""
                select id, category_code, category_name, status
                from qc_item_category
                where 1 = 1
                """);
        MapSqlParameterSource params = new MapSqlParameterSource();
        appendKeyword(sql, params, keyword, "category_code", "category_name");
        appendEquals(sql, params, "status", status);
        sql.append(" order by id desc");
        return jdbc.query(sql.toString(), params, itemCategoryMapper());
    }

    public Optional<QcItemCategory> findItemCategoryById(Long id) {
        return findOne("""
                select id, category_code, category_name, status
                from qc_item_category
                where id = :id
                """, Map.of("id", id), itemCategoryMapper());
    }

    public void insertItemCategory(QcItemCategory item) {
        jdbc.update("""
                insert into qc_item_category (id, category_code, category_name, status)
                values (:id, :categoryCode, :categoryName, :status)
                """, params()
                .addValue("id", item.id())
                .addValue("categoryCode", item.categoryCode())
                .addValue("categoryName", item.categoryName())
                .addValue("status", item.status()));
    }

    public int updateItemCategory(QcItemCategory item) {
        return jdbc.update("""
                update qc_item_category
                set category_code = :categoryCode,
                    category_name = :categoryName,
                    status = :status
                where id = :id
                """, params()
                .addValue("id", item.id())
                .addValue("categoryCode", item.categoryCode())
                .addValue("categoryName", item.categoryName())
                .addValue("status", item.status()));
    }

    public int updateItemCategoryStatus(Long id, String oldStatus, String newStatus) {
        return updateStatus("qc_item_category", id, oldStatus, newStatus);
    }

    public int countItemsByCategory(Long categoryId) {
        return count("qc_item", "category_id", categoryId);
    }

    public List<QcItem> listItems(String keyword, Long categoryId, String status) {
        StringBuilder sql = new StringBuilder("""
                select id, category_id, item_code, item_name, value_type, standard_value,
                       upper_limit, lower_limit, unit_id, status
                from qc_item
                where 1 = 1
                """);
        MapSqlParameterSource params = new MapSqlParameterSource();
        appendKeyword(sql, params, keyword, "item_code", "item_name");
        appendEquals(sql, params, "category_id", categoryId);
        appendEquals(sql, params, "status", status);
        sql.append(" order by id desc");
        return jdbc.query(sql.toString(), params, itemMapper());
    }

    public Optional<QcItem> findItemById(Long id) {
        return findOne("""
                select id, category_id, item_code, item_name, value_type, standard_value,
                       upper_limit, lower_limit, unit_id, status
                from qc_item
                where id = :id
                """, Map.of("id", id), itemMapper());
    }

    public void insertItem(QcItem item) {
        jdbc.update("""
                insert into qc_item (
                    id, category_id, item_code, item_name, value_type, standard_value,
                    upper_limit, lower_limit, unit_id, status
                )
                values (
                    :id, :categoryId, :itemCode, :itemName, :valueType, :standardValue,
                    :upperLimit, :lowerLimit, :unitId, :status
                )
                """, itemParams(item));
    }

    public int updateItem(QcItem item) {
        return jdbc.update("""
                update qc_item
                set category_id = :categoryId,
                    item_code = :itemCode,
                    item_name = :itemName,
                    value_type = :valueType,
                    standard_value = :standardValue,
                    upper_limit = :upperLimit,
                    lower_limit = :lowerLimit,
                    unit_id = :unitId,
                    status = :status
                where id = :id
                """, itemParams(item));
    }

    public int updateItemStatus(Long id, String oldStatus, String newStatus) {
        return updateStatus("qc_item", id, oldStatus, newStatus);
    }

    public int countPlanItemsByItem(Long itemId) {
        return count("qc_plan_item", "qc_item_id", itemId);
    }

    public int countResultsByItem(Long itemId) {
        return count("qc_inspection_result", "qc_item_id", itemId);
    }

    public List<QcPlan> listPlans(String keyword, Long productId, Long customerId, String status) {
        StringBuilder sql = new StringBuilder("""
                select id, plan_code, plan_name, product_id, customer_id, is_default, status
                from qc_plan
                where 1 = 1
                """);
        MapSqlParameterSource params = new MapSqlParameterSource();
        appendKeyword(sql, params, keyword, "plan_code", "plan_name");
        appendEquals(sql, params, "product_id", productId);
        appendEquals(sql, params, "customer_id", customerId);
        appendEquals(sql, params, "status", status);
        sql.append(" order by id desc");
        return jdbc.query(sql.toString(), params, planMapper());
    }

    public Optional<QcPlan> findPlanById(Long id) {
        return findOne("""
                select id, plan_code, plan_name, product_id, customer_id, is_default, status
                from qc_plan
                where id = :id
                """, Map.of("id", id), planMapper());
    }

    public void insertPlan(QcPlan plan) {
        jdbc.update("""
                insert into qc_plan (id, plan_code, plan_name, product_id, customer_id, is_default, status)
                values (:id, :planCode, :planName, :productId, :customerId, :defaultFlag, :status)
                """, planParams(plan));
    }

    public int updatePlan(QcPlan plan) {
        return jdbc.update("""
                update qc_plan
                set plan_code = :planCode,
                    plan_name = :planName,
                    product_id = :productId,
                    customer_id = :customerId,
                    is_default = :defaultFlag,
                    status = :status
                where id = :id
                """, planParams(plan));
    }

    public int updatePlanStatus(Long id, String oldStatus, String newStatus) {
        return updateStatus("qc_plan", id, oldStatus, newStatus);
    }

    public int countPlanItemsByPlan(Long planId) {
        return count("qc_plan_item", "plan_id", planId);
    }

    public int countInspectionOrdersByPlan(Long planId) {
        return count("qc_inspection_order", "plan_id", planId);
    }

    public List<QcPlanItem> listPlanItems(Long planId, Long qcItemId) {
        StringBuilder sql = new StringBuilder("""
                select id, plan_id, qc_item_id, sample_qty, standard_value,
                       upper_limit, lower_limit, required_flag
                from qc_plan_item
                where 1 = 1
                """);
        MapSqlParameterSource params = new MapSqlParameterSource();
        appendEquals(sql, params, "plan_id", planId);
        appendEquals(sql, params, "qc_item_id", qcItemId);
        sql.append(" order by id desc");
        return jdbc.query(sql.toString(), params, planItemMapper());
    }

    public Optional<QcPlanItem> findPlanItemById(Long id) {
        return findOne("""
                select id, plan_id, qc_item_id, sample_qty, standard_value,
                       upper_limit, lower_limit, required_flag
                from qc_plan_item
                where id = :id
                """, Map.of("id", id), planItemMapper());
    }

    public void insertPlanItem(QcPlanItem item) {
        jdbc.update("""
                insert into qc_plan_item (
                    id, plan_id, qc_item_id, sample_qty, standard_value,
                    upper_limit, lower_limit, required_flag
                )
                values (
                    :id, :planId, :qcItemId, :sampleQty, :standardValue,
                    :upperLimit, :lowerLimit, :requiredFlag
                )
                """, planItemParams(item));
    }

    public int updatePlanItem(QcPlanItem item) {
        return jdbc.update("""
                update qc_plan_item
                set plan_id = :planId,
                    qc_item_id = :qcItemId,
                    sample_qty = :sampleQty,
                    standard_value = :standardValue,
                    upper_limit = :upperLimit,
                    lower_limit = :lowerLimit,
                    required_flag = :requiredFlag
                where id = :id
                """, planItemParams(item));
    }

    public int deleteById(String tableName, Long id) {
        return jdbc.update("delete from " + tableName + " where id = :id", Map.of("id", id));
    }

    public int updateInspectionOrderStatus(Long id, String oldStatus, String newStatus, String finalResult) {
        return jdbc.update("""
                update qc_inspection_order
                set status = :newStatus,
                    final_result = :finalResult
                where id = :id and status = :oldStatus
                """, params()
                .addValue("id", id)
                .addValue("oldStatus", oldStatus)
                .addValue("newStatus", newStatus)
                .addValue("finalResult", finalResult));
    }

    public int countResultsByInspection(Long inspectionId) {
        return count("qc_inspection_result", "inspection_id", inspectionId);
    }

    public int countDefectsByInspection(Long inspectionId) {
        return countBySource("qc_defect_record", "检验", inspectionId);
    }

    public List<QcInspectionOrder> listInspectionOrders(
            String keyword,
            String inspectionType,
            String status,
            Long planId,
            Long taskId,
            Long operationTaskId,
            Long barcodeId
    ) {
        StringBuilder sql = new StringBuilder("""
                select id, inspection_no, inspection_type, plan_id, work_order_id,
                       work_order_no, task_id, operation_task_id, product_id,
                       product_name, barcode_id, inspector_id,
                       inspection_at, final_result, status
                from qc_inspection_order
                where 1 = 1
                """);
        MapSqlParameterSource params = new MapSqlParameterSource();
        appendKeyword(sql, params, keyword, "inspection_no");
        appendEquals(sql, params, "inspection_type", inspectionType);
        appendEquals(sql, params, "status", status);
        appendEquals(sql, params, "plan_id", planId);
        appendEquals(sql, params, "task_id", taskId);
        appendEquals(sql, params, "operation_task_id", operationTaskId);
        appendEquals(sql, params, "barcode_id", barcodeId);
        sql.append(" order by id desc");
        return jdbc.query(sql.toString(), params, inspectionOrderMapper());
    }

    public Optional<QcInspectionOrder> findInspectionOrderById(Long id) {
        return findOne("""
                select id, inspection_no, inspection_type, plan_id, work_order_id,
                       work_order_no, task_id, operation_task_id, product_id,
                       product_name, barcode_id, inspector_id,
                       inspection_at, final_result, status
                from qc_inspection_order
                where id = :id
                """, Map.of("id", id), inspectionOrderMapper());
    }

    public void insertInspectionOrder(QcInspectionOrder order) {
        jdbc.update("""
                insert into qc_inspection_order (
                    id, inspection_no, inspection_type, plan_id, work_order_id, task_id,
                    operation_task_id, product_id, product_name, work_order_no, barcode_id, inspector_id,
                    inspection_at, final_result, status
                )
                values (
                    :id, :inspectionNo, :inspectionType, :planId, :workOrderId, :taskId,
                    :operationTaskId, :productId, :productName, :workOrderNo, :barcodeId, :inspectorId,
                    :inspectionAt, :finalResult, :status
                )
                """, inspectionOrderParams(order));
    }

    public int updateInspectionOrder(QcInspectionOrder order) {
        return jdbc.update("""
                update qc_inspection_order
                set inspection_no = :inspectionNo,
                    inspection_type = :inspectionType,
                    plan_id = :planId,
                    work_order_id = :workOrderId,
                    task_id = :taskId,
                    operation_task_id = :operationTaskId,
                    product_id = :productId,
                    product_name = :productName,
                    work_order_no = :workOrderNo,
                    barcode_id = :barcodeId,
                    inspector_id = :inspectorId,
                    inspection_at = :inspectionAt,
                    final_result = :finalResult,
                    status = :status
                where id = :id
                """, inspectionOrderParams(order));
    }

    public List<QcInspectionResult> listInspectionResults(Long inspectionId, Long qcItemId, String result) {
        StringBuilder sql = new StringBuilder("""
                select id, inspection_id, qc_item_id, measured_value, result, defect_reason_id, remark
                from qc_inspection_result
                where 1 = 1
                """);
        MapSqlParameterSource params = new MapSqlParameterSource();
        appendEquals(sql, params, "inspection_id", inspectionId);
        appendEquals(sql, params, "qc_item_id", qcItemId);
        appendEquals(sql, params, "result", result);
        sql.append(" order by id desc");
        return jdbc.query(sql.toString(), params, inspectionResultMapper());
    }

    public Optional<QcInspectionResult> findInspectionResultById(Long id) {
        return findOne("""
                select id, inspection_id, qc_item_id, measured_value, result, defect_reason_id, remark
                from qc_inspection_result
                where id = :id
                """, Map.of("id", id), inspectionResultMapper());
    }

    public void insertInspectionResult(QcInspectionResult result) {
        jdbc.update("""
                insert into qc_inspection_result (
                    id, inspection_id, qc_item_id, measured_value, result, defect_reason_id, remark
                )
                values (
                    :id, :inspectionId, :qcItemId, :measuredValue, :result, :defectReasonId, :remark
                )
                """, inspectionResultParams(result));
    }

    public int updateInspectionResult(QcInspectionResult result) {
        return jdbc.update("""
                update qc_inspection_result
                set inspection_id = :inspectionId,
                    qc_item_id = :qcItemId,
                    measured_value = :measuredValue,
                    result = :result,
                    defect_reason_id = :defectReasonId,
                    remark = :remark
                where id = :id
                """, inspectionResultParams(result));
    }

    public List<QcDefectRecord> listDefectRecords(
            String sourceType,
            Long sourceId,
            Long productId,
            Long barcodeId,
            Long processId,
            String status
    ) {
        StringBuilder sql = new StringBuilder("""
                select id, source_type, source_id, product_id, barcode_id, process_id,
                       defect_reason_id, defect_reason_name, defect_qty,
                       handle_method, rework_order_id, status
                from qc_defect_record
                where 1 = 1
                """);
        MapSqlParameterSource params = new MapSqlParameterSource();
        appendEquals(sql, params, "source_type", sourceType);
        appendEquals(sql, params, "source_id", sourceId);
        appendEquals(sql, params, "product_id", productId);
        appendEquals(sql, params, "barcode_id", barcodeId);
        appendEquals(sql, params, "process_id", processId);
        appendEquals(sql, params, "status", status);
        sql.append(" order by id desc");
        return jdbc.query(sql.toString(), params, defectRecordMapper());
    }

    public Optional<QcDefectRecord> findDefectRecordById(Long id) {
        return findOne("""
                select id, source_type, source_id, product_id, barcode_id, process_id,
                       defect_reason_id, defect_reason_name, defect_qty,
                       handle_method, rework_order_id, status
                from qc_defect_record
                where id = :id
                """, Map.of("id", id), defectRecordMapper());
    }

    public void insertDefectRecord(QcDefectRecord record) {
        jdbc.update("""
                insert into qc_defect_record (
                    id, source_type, source_id, product_id, barcode_id, process_id,
                    defect_reason_id, defect_reason_name, defect_qty, handle_method, rework_order_id, status
                )
                values (
                    :id, :sourceType, :sourceId, :productId, :barcodeId, :processId,
                    :defectReasonId, :defectReasonName, :defectQty, :handleMethod, :reworkOrderId, :status
                )
                """, defectRecordParams(record));
    }

    public int updateDefectRecord(QcDefectRecord record) {
        return jdbc.update("""
                update qc_defect_record
                set source_type = :sourceType,
                    source_id = :sourceId,
                    product_id = :productId,
                    barcode_id = :barcodeId,
                    process_id = :processId,
                    defect_reason_id = :defectReasonId,
                    defect_reason_name = :defectReasonName,
                    defect_qty = :defectQty,
                    handle_method = :handleMethod,
                    rework_order_id = :reworkOrderId,
                    status = :status
                where id = :id
                """, defectRecordParams(record));
    }

    public int updateDefectStatus(Long id, String oldStatus, String newStatus) {
        return updateStatus("qc_defect_record", id, oldStatus, newStatus);
    }

    private int updateStatus(String tableName, Long id, String oldStatus, String newStatus) {
        return jdbc.update("update " + tableName + " set status = :newStatus where id = :id and status = :oldStatus",
                Map.of("id", id, "oldStatus", oldStatus, "newStatus", newStatus));
    }

    private int count(String tableName, String columnName, Long id) {
        if (id == null) {
            return 0;
        }
        Integer count = jdbc.queryForObject(
                "select count(*) from " + tableName + " where " + columnName + " = :id",
                Map.of("id", id),
                Integer.class
        );
        return count == null ? 0 : count;
    }

    private int countBySource(String tableName, String sourceType, Long sourceId) {
        if (sourceId == null) {
            return 0;
        }
        Integer count = jdbc.queryForObject(
                "select count(*) from " + tableName + " where source_type = :sourceType and source_id = :sourceId",
                Map.of("sourceType", sourceType, "sourceId", sourceId),
                Integer.class
        );
        return count == null ? 0 : count;
    }

    private <T> Optional<T> findOne(String sql, Map<String, ?> params, RowMapper<T> rowMapper) {
        List<T> rows = jdbc.query(sql, params, rowMapper);
        return rows.stream().findFirst();
    }

    private void appendKeyword(
            StringBuilder sql,
            MapSqlParameterSource params,
            String keyword,
            String firstColumn,
            String... otherColumns
    ) {
        if (!StringUtils.hasText(keyword)) {
            return;
        }

        StringBuilder condition = new StringBuilder(" and (");
        condition.append(firstColumn).append(" like :keyword");
        for (String column : otherColumns) {
            condition.append(" or ").append(column).append(" like :keyword");
        }
        condition.append(")");
        sql.append(condition);
        params.addValue("keyword", "%" + keyword.trim() + "%");
    }

    private void appendEquals(StringBuilder sql, MapSqlParameterSource params, String column, Object value) {
        if (value == null || value instanceof String text && !StringUtils.hasText(text)) {
            return;
        }

        String paramName = column.replace("_", "");
        sql.append(" and ").append(column).append(" = :").append(paramName);
        params.addValue(paramName, value instanceof String text ? text.trim() : value);
    }

    private MapSqlParameterSource params() {
        return new MapSqlParameterSource();
    }

    private MapSqlParameterSource itemParams(QcItem item) {
        return params()
                .addValue("id", item.id())
                .addValue("categoryId", item.categoryId())
                .addValue("itemCode", item.itemCode())
                .addValue("itemName", item.itemName())
                .addValue("valueType", item.valueType())
                .addValue("standardValue", item.standardValue())
                .addValue("upperLimit", item.upperLimit())
                .addValue("lowerLimit", item.lowerLimit())
                .addValue("unitId", item.unitId())
                .addValue("status", item.status());
    }

    private MapSqlParameterSource planParams(QcPlan plan) {
        return params()
                .addValue("id", plan.id())
                .addValue("planCode", plan.planCode())
                .addValue("planName", plan.planName())
                .addValue("productId", plan.productId())
                .addValue("customerId", plan.customerId())
                .addValue("defaultFlag", toTinyInt(plan.defaultFlag()))
                .addValue("status", plan.status());
    }

    private MapSqlParameterSource planItemParams(QcPlanItem item) {
        return params()
                .addValue("id", item.id())
                .addValue("planId", item.planId())
                .addValue("qcItemId", item.qcItemId())
                .addValue("sampleQty", item.sampleQty())
                .addValue("standardValue", item.standardValue())
                .addValue("upperLimit", item.upperLimit())
                .addValue("lowerLimit", item.lowerLimit())
                .addValue("requiredFlag", toTinyInt(item.requiredFlag()));
    }

    private MapSqlParameterSource inspectionOrderParams(QcInspectionOrder order) {
        return params()
                .addValue("id", order.id())
                .addValue("inspectionNo", order.inspectionNo())
                .addValue("inspectionType", order.inspectionType())
                .addValue("planId", order.planId())
                .addValue("workOrderId", order.workOrderId())
                .addValue("taskId", order.taskId())
                .addValue("operationTaskId", order.operationTaskId())
                .addValue("productId", order.productId())
                .addValue("productName", order.productName())
                .addValue("workOrderNo", order.workOrderNo())
                .addValue("barcodeId", order.barcodeId())
                .addValue("inspectorId", order.inspectorId())
                .addValue("inspectionAt", order.inspectionAt())
                .addValue("finalResult", order.finalResult())
                .addValue("status", order.status());
    }

    private MapSqlParameterSource inspectionResultParams(QcInspectionResult result) {
        return params()
                .addValue("id", result.id())
                .addValue("inspectionId", result.inspectionId())
                .addValue("qcItemId", result.qcItemId())
                .addValue("measuredValue", result.measuredValue())
                .addValue("result", result.result())
                .addValue("defectReasonId", result.defectReasonId())
                .addValue("remark", result.remark());
    }

    private MapSqlParameterSource defectRecordParams(QcDefectRecord record) {
        return params()
                .addValue("id", record.id())
                .addValue("sourceType", record.sourceType())
                .addValue("sourceId", record.sourceId())
                .addValue("productId", record.productId())
                .addValue("barcodeId", record.barcodeId())
                .addValue("processId", record.processId())
                .addValue("defectReasonId", record.defectReasonId())
                .addValue("defectReasonName", record.defectReasonName())
                .addValue("defectQty", record.defectQty())
                .addValue("handleMethod", record.handleMethod())
                .addValue("reworkOrderId", record.reworkOrderId())
                .addValue("status", record.status());
    }

    private RowMapper<QcItemCategory> itemCategoryMapper() {
        return (rs, rowNum) -> new QcItemCategory(
                rs.getLong("id"),
                rs.getString("category_code"),
                rs.getString("category_name"),
                rs.getString("status")
        );
    }

    private RowMapper<QcItem> itemMapper() {
        return (rs, rowNum) -> new QcItem(
                rs.getLong("id"),
                getLong(rs, "category_id"),
                rs.getString("item_code"),
                rs.getString("item_name"),
                rs.getString("value_type"),
                rs.getString("standard_value"),
                rs.getBigDecimal("upper_limit"),
                rs.getBigDecimal("lower_limit"),
                getLong(rs, "unit_id"),
                rs.getString("status")
        );
    }

    private RowMapper<QcPlan> planMapper() {
        return (rs, rowNum) -> new QcPlan(
                rs.getLong("id"),
                rs.getString("plan_code"),
                rs.getString("plan_name"),
                getLong(rs, "product_id"),
                getLong(rs, "customer_id"),
                getBoolean(rs, "is_default"),
                rs.getString("status")
        );
    }

    private RowMapper<QcPlanItem> planItemMapper() {
        return (rs, rowNum) -> new QcPlanItem(
                rs.getLong("id"),
                getLong(rs, "plan_id"),
                getLong(rs, "qc_item_id"),
                rs.getBigDecimal("sample_qty"),
                rs.getString("standard_value"),
                rs.getBigDecimal("upper_limit"),
                rs.getBigDecimal("lower_limit"),
                getBoolean(rs, "required_flag")
        );
    }

    private RowMapper<QcInspectionOrder> inspectionOrderMapper() {
        return (rs, rowNum) -> new QcInspectionOrder(
                rs.getLong("id"),
                rs.getString("inspection_no"),
                rs.getString("inspection_type"),
                getLong(rs, "plan_id"),
                getLong(rs, "work_order_id"),
                rs.getString("work_order_no"),
                getLong(rs, "task_id"),
                getLong(rs, "operation_task_id"),
                getLong(rs, "product_id"),
                rs.getString("product_name"),
                getLong(rs, "barcode_id"),
                getLong(rs, "inspector_id"),
                rs.getTimestamp("inspection_at") == null ? null : rs.getTimestamp("inspection_at").toLocalDateTime(),
                rs.getString("final_result"),
                rs.getString("status")
        );
    }

    private RowMapper<QcInspectionResult> inspectionResultMapper() {
        return (rs, rowNum) -> new QcInspectionResult(
                rs.getLong("id"),
                getLong(rs, "inspection_id"),
                getLong(rs, "qc_item_id"),
                rs.getString("measured_value"),
                rs.getString("result"),
                getLong(rs, "defect_reason_id"),
                rs.getString("remark")
        );
    }

    private RowMapper<QcDefectRecord> defectRecordMapper() {
        return (rs, rowNum) -> new QcDefectRecord(
                rs.getLong("id"),
                rs.getString("source_type"),
                getLong(rs, "source_id"),
                getLong(rs, "product_id"),
                getLong(rs, "barcode_id"),
                getLong(rs, "process_id"),
                getLong(rs, "defect_reason_id"),
                rs.getString("defect_reason_name"),
                rs.getBigDecimal("defect_qty"),
                rs.getString("handle_method"),
                getLong(rs, "rework_order_id"),
                rs.getString("status")
        );
    }

    private Long getLong(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : value;
    }

    private Boolean getBoolean(ResultSet rs, String columnName) throws SQLException {
        Object value = rs.getObject(columnName);
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }
        if (value instanceof Number numberValue) {
            return numberValue.intValue() != 0;
        }
        return Boolean.valueOf(value.toString());
    }

    private Integer toTinyInt(Boolean value) {
        return value == null ? null : value ? 1 : 0;
    }
}
