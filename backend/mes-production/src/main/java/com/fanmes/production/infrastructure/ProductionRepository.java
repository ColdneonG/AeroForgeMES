package com.fanmes.production.infrastructure;

import com.fanmes.production.domain.DispatchOrder;
import com.fanmes.production.domain.KittingAnalysis;
import com.fanmes.production.domain.KittingMissingItem;
import com.fanmes.production.domain.ShopTask;
import com.fanmes.production.domain.WorkOrder;
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
public class ProductionRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public ProductionRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<WorkOrder> listWorkOrders(
            String keyword,
            String status,
            Long productId,
            Long lineId,
            String sourceType,
            String externalNo
    ) {
        StringBuilder sql = new StringBuilder("""
                select id, work_order_no, external_no, source_type, product_id, plan_qty,
                       completed_qty, qualified_qty, defective_qty, unit_id, planned_start_at,
                       planned_end_at, delivery_date, line_id, route_id, status
                from prod_work_order
                where 1 = 1
                """);
        MapSqlParameterSource params = new MapSqlParameterSource();
        appendKeyword(sql, params, keyword, "work_order_no", "external_no");
        appendEquals(sql, params, "status", status);
        appendEquals(sql, params, "product_id", productId);
        appendEquals(sql, params, "line_id", lineId);
        appendEquals(sql, params, "source_type", sourceType);
        appendEquals(sql, params, "external_no", externalNo);
        sql.append(" order by id desc");
        return jdbc.query(sql.toString(), params, workOrderMapper());
    }

    public Optional<WorkOrder> findWorkOrderById(Long id) {
        return findOne("""
                select id, work_order_no, external_no, source_type, product_id, plan_qty,
                       completed_qty, qualified_qty, defective_qty, unit_id, planned_start_at,
                       planned_end_at, delivery_date, line_id, route_id, status
                from prod_work_order
                where id = :id
                """, Map.of("id", id), workOrderMapper());
    }

    public Optional<WorkOrder> findWorkOrderByExternalNo(String externalNo) {
        if (!StringUtils.hasText(externalNo)) {
            return Optional.empty();
        }
        return findOne("""
                select id, work_order_no, external_no, source_type, product_id, plan_qty,
                       completed_qty, qualified_qty, defective_qty, unit_id, planned_start_at,
                       planned_end_at, delivery_date, line_id, route_id, status
                from prod_work_order
                where external_no = :externalNo
                """, Map.of("externalNo", externalNo.trim()), workOrderMapper());
    }

    public void insertWorkOrder(WorkOrder order) {
        jdbc.update("""
                insert into prod_work_order (
                    id, work_order_no, external_no, source_type, product_id, plan_qty,
                    completed_qty, qualified_qty, defective_qty, unit_id, planned_start_at,
                    planned_end_at, delivery_date, line_id, route_id, status
                )
                values (
                    :id, :workOrderNo, :externalNo, :sourceType, :productId, :planQty,
                    :completedQty, :qualifiedQty, :defectiveQty, :unitId, :plannedStartAt,
                    :plannedEndAt, :deliveryDate, :lineId, :routeId, :status
                )
                """, workOrderParams(order));
    }

    public int updateWorkOrder(WorkOrder order) {
        return jdbc.update("""
                update prod_work_order
                set work_order_no = :workOrderNo,
                    external_no = :externalNo,
                    source_type = :sourceType,
                    product_id = :productId,
                    plan_qty = :planQty,
                    completed_qty = :completedQty,
                    qualified_qty = :qualifiedQty,
                    defective_qty = :defectiveQty,
                    unit_id = :unitId,
                    planned_start_at = :plannedStartAt,
                    planned_end_at = :plannedEndAt,
                    delivery_date = :deliveryDate,
                    line_id = :lineId,
                    route_id = :routeId,
                    status = :status
                where id = :id
                """, workOrderParams(order));
    }

    public int updateWorkOrderStatus(Long id, String oldStatus, String newStatus) {
        return updateStatus("prod_work_order", id, oldStatus, newStatus);
    }

    public int completeWorkOrder(Long id, String oldStatus, WorkOrder order) {
        return jdbc.update("""
                update prod_work_order
                set completed_qty = :completedQty,
                    qualified_qty = :qualifiedQty,
                    defective_qty = :defectiveQty,
                    status = :status
                where id = :id and status = :oldStatus
                """, params()
                .addValue("id", id)
                .addValue("oldStatus", oldStatus)
                .addValue("completedQty", order.completedQty())
                .addValue("qualifiedQty", order.qualifiedQty())
                .addValue("defectiveQty", order.defectiveQty())
                .addValue("status", order.status()));
    }

    public int deleteById(String tableName, Long id) {
        return jdbc.update("delete from " + tableName + " where id = :id", Map.of("id", id));
    }

    public int countKittingByWorkOrder(Long workOrderId) {
        return count("prod_kitting_analysis", "work_order_id", workOrderId);
    }

    public int countDispatchByWorkOrder(Long workOrderId) {
        return count("prod_dispatch_order", "work_order_id", workOrderId);
    }

    public int countTaskByDispatch(Long dispatchId) {
        return count("shop_task", "dispatch_id", dispatchId);
    }

    public List<KittingAnalysis> listKittingAnalyses(String keyword, String status, Long workOrderId, Long taskId) {
        StringBuilder sql = new StringBuilder("""
                select id, analysis_no, work_order_id, task_id, analysis_time,
                       kitting_status, missing_count, status
                from prod_kitting_analysis
                where 1 = 1
                """);
        MapSqlParameterSource params = new MapSqlParameterSource();
        appendKeyword(sql, params, keyword, "analysis_no");
        appendEquals(sql, params, "status", status);
        appendEquals(sql, params, "work_order_id", workOrderId);
        appendEquals(sql, params, "task_id", taskId);
        sql.append(" order by id desc");
        return jdbc.query(sql.toString(), params, kittingAnalysisMapper());
    }

    public Optional<KittingAnalysis> findKittingAnalysisById(Long id) {
        return findOne("""
                select id, analysis_no, work_order_id, task_id, analysis_time,
                       kitting_status, missing_count, status
                from prod_kitting_analysis
                where id = :id
                """, Map.of("id", id), kittingAnalysisMapper());
    }

    public void insertKittingAnalysis(KittingAnalysis analysis) {
        jdbc.update("""
                insert into prod_kitting_analysis (
                    id, analysis_no, work_order_id, task_id, analysis_time,
                    kitting_status, missing_count, status
                )
                values (
                    :id, :analysisNo, :workOrderId, :taskId, :analysisTime,
                    :kittingStatus, :missingCount, :status
                )
                """, kittingAnalysisParams(analysis));
    }

    public int updateKittingAnalysis(KittingAnalysis analysis) {
        return jdbc.update("""
                update prod_kitting_analysis
                set analysis_no = :analysisNo,
                    work_order_id = :workOrderId,
                    task_id = :taskId,
                    analysis_time = :analysisTime,
                    kitting_status = :kittingStatus,
                    missing_count = :missingCount,
                    status = :status
                where id = :id
                """, kittingAnalysisParams(analysis));
    }

    public int updateKittingAnalysisStatus(Long id, String oldStatus, String newStatus) {
        return updateStatus("prod_kitting_analysis", id, oldStatus, newStatus);
    }

    public List<KittingMissingItem> listMissingItems(Long analysisId, Long materialId) {
        StringBuilder sql = new StringBuilder("""
                select id, analysis_id, material_id, required_qty, available_qty,
                       missing_qty, expected_arrival_at
                from prod_kitting_missing_item
                where 1 = 1
                """);
        MapSqlParameterSource params = new MapSqlParameterSource();
        appendEquals(sql, params, "analysis_id", analysisId);
        appendEquals(sql, params, "material_id", materialId);
        sql.append(" order by id desc");
        return jdbc.query(sql.toString(), params, missingItemMapper());
    }

    public Optional<KittingMissingItem> findMissingItemById(Long id) {
        return findOne("""
                select id, analysis_id, material_id, required_qty, available_qty,
                       missing_qty, expected_arrival_at
                from prod_kitting_missing_item
                where id = :id
                """, Map.of("id", id), missingItemMapper());
    }

    public List<KittingMissingItem> listMissingBoard() {
        return jdbc.query("""
                select mi.id, mi.analysis_id, mi.material_id, mi.required_qty, mi.available_qty,
                       mi.missing_qty, mi.expected_arrival_at
                from prod_kitting_missing_item mi
                join prod_kitting_analysis ka on ka.id = mi.analysis_id
                where mi.missing_qty > 0
                  and (ka.status is null or ka.status not in ('已关闭', '作废'))
                order by mi.expected_arrival_at asc, mi.id desc
                """, missingItemMapper());
    }

    public void insertMissingItem(KittingMissingItem item) {
        jdbc.update("""
                insert into prod_kitting_missing_item (
                    id, analysis_id, material_id, required_qty, available_qty,
                    missing_qty, expected_arrival_at
                )
                values (
                    :id, :analysisId, :materialId, :requiredQty, :availableQty,
                    :missingQty, :expectedArrivalAt
                )
                """, missingItemParams(item));
    }

    public int updateMissingItem(KittingMissingItem item) {
        return jdbc.update("""
                update prod_kitting_missing_item
                set analysis_id = :analysisId,
                    material_id = :materialId,
                    required_qty = :requiredQty,
                    available_qty = :availableQty,
                    missing_qty = :missingQty,
                    expected_arrival_at = :expectedArrivalAt
                where id = :id
                """, missingItemParams(item));
    }

    public int countMissingByAnalysis(Long analysisId) {
        return count("prod_kitting_missing_item", "analysis_id", analysisId);
    }

    public int countPositiveMissingByAnalysis(Long analysisId) {
        Integer count = jdbc.queryForObject("""
                select count(*)
                from prod_kitting_missing_item
                where analysis_id = :analysisId and missing_qty > 0
                """, Map.of("analysisId", analysisId), Integer.class);
        return count == null ? 0 : count;
    }

    public List<DispatchOrder> listDispatchOrders(
            String keyword,
            String status,
            Long workOrderId,
            Long lineId,
            Long teamId
    ) {
        StringBuilder sql = new StringBuilder("""
                select id, dispatch_no, work_order_id, line_id, station_id, team_id,
                       plan_qty, planned_start_at, planned_end_at, status
                from prod_dispatch_order
                where 1 = 1
                """);
        MapSqlParameterSource params = new MapSqlParameterSource();
        appendKeyword(sql, params, keyword, "dispatch_no");
        appendEquals(sql, params, "status", status);
        appendEquals(sql, params, "work_order_id", workOrderId);
        appendEquals(sql, params, "line_id", lineId);
        appendEquals(sql, params, "team_id", teamId);
        sql.append(" order by id desc");
        return jdbc.query(sql.toString(), params, dispatchOrderMapper());
    }

    public Optional<DispatchOrder> findDispatchOrderById(Long id) {
        return findOne("""
                select id, dispatch_no, work_order_id, line_id, station_id, team_id,
                       plan_qty, planned_start_at, planned_end_at, status
                from prod_dispatch_order
                where id = :id
                """, Map.of("id", id), dispatchOrderMapper());
    }

    public void insertDispatchOrder(DispatchOrder dispatch) {
        jdbc.update("""
                insert into prod_dispatch_order (
                    id, dispatch_no, work_order_id, line_id, station_id, team_id,
                    plan_qty, planned_start_at, planned_end_at, status
                )
                values (
                    :id, :dispatchNo, :workOrderId, :lineId, :stationId, :teamId,
                    :planQty, :plannedStartAt, :plannedEndAt, :status
                )
                """, dispatchOrderParams(dispatch));
    }

    public int updateDispatchOrder(DispatchOrder dispatch) {
        return jdbc.update("""
                update prod_dispatch_order
                set dispatch_no = :dispatchNo,
                    work_order_id = :workOrderId,
                    line_id = :lineId,
                    station_id = :stationId,
                    team_id = :teamId,
                    plan_qty = :planQty,
                    planned_start_at = :plannedStartAt,
                    planned_end_at = :plannedEndAt,
                    status = :status
                where id = :id
                """, dispatchOrderParams(dispatch));
    }

    public int updateDispatchStatus(Long id, String oldStatus, String newStatus) {
        return updateStatus("prod_dispatch_order", id, oldStatus, newStatus);
    }

    public void insertShopTask(ShopTask task) {
        jdbc.update("""
                insert into shop_task (
                    id, task_no, work_order_id, dispatch_id, product_id, product_name, route_id,
                    line_id, line_name, team_id, plan_qty, started_at, ended_at, status
                )
                values (
                    :id, :taskNo, :workOrderId, :dispatchId, :productId, :productName, :routeId,
                    :lineId, :lineName, :teamId, :planQty, :startedAt, :endedAt, :status
                )
                """, shopTaskParams(task));
    }

    public List<ShopTask> listShopTasks(
            String keyword,
            String status,
            Long workOrderId,
            Long dispatchId,
            Long lineId,
            Long teamId
    ) {
        StringBuilder sql = new StringBuilder("""
                select st.id, st.task_no, st.work_order_id, st.dispatch_id, st.product_id, st.route_id,
                       st.line_id, st.team_id, st.plan_qty, st.started_at, st.ended_at, st.status,
                       st.product_name, st.line_name,
                       wo.work_order_no,
                       rh.route_name
                from shop_task st
                left join prod_work_order wo on wo.id = st.work_order_id
                left join route_header rh on rh.id = st.route_id
                where 1 = 1
                """);
        MapSqlParameterSource params = new MapSqlParameterSource();
        appendKeyword(sql, params, keyword, "st.task_no", "wo.work_order_no");
        appendEquals(sql, params, "st.status", status);
        appendEquals(sql, params, "st.work_order_id", workOrderId);
        appendEquals(sql, params, "st.dispatch_id", dispatchId);
        appendEquals(sql, params, "st.line_id", lineId);
        appendEquals(sql, params, "st.team_id", teamId);
        sql.append(" order by st.id desc");
        return jdbc.query(sql.toString(), params, shopTaskWithNamesMapper());
    }

    public Optional<ShopTask> findShopTaskById(Long id) {
        return findOne("""
                select id, task_no, work_order_id, dispatch_id, product_id, route_id,
                       line_id, team_id, plan_qty, started_at, ended_at, status
                from shop_task
                where id = :id
                """, Map.of("id", id), shopTaskMapper());
    }

    public Optional<ShopTask> findTaskByDispatchId(Long dispatchId) {
        return findOne("""
                select id, task_no, work_order_id, dispatch_id, product_id, route_id,
                       line_id, team_id, plan_qty, started_at, ended_at, status
                from shop_task
                where dispatch_id = :dispatchId
                """, Map.of("dispatchId", dispatchId), shopTaskMapper());
    }

    public int updateTaskStatusByDispatch(Long dispatchId, String newStatus) {
        return jdbc.update("""
                update shop_task
                set status = :status
                where dispatch_id = :dispatchId
                """, Map.of("dispatchId", dispatchId, "status", newStatus));
    }

    public int updateShopTaskStatus(Long id, String oldStatus, String newStatus) {
        return updateStatus("shop_task", id, oldStatus, newStatus);
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

    private <T> Optional<T> findOne(String sql, Map<String, ?> params, RowMapper<T> mapper) {
        List<T> rows = jdbc.query(sql, params, mapper);
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
        sql.append(" and (").append(firstColumn).append(" like :keyword");
        for (String column : otherColumns) {
            sql.append(" or ").append(column).append(" like :keyword");
        }
        sql.append(")");
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

    private MapSqlParameterSource workOrderParams(WorkOrder order) {
        return params()
                .addValue("id", order.id())
                .addValue("workOrderNo", order.workOrderNo())
                .addValue("externalNo", order.externalNo())
                .addValue("sourceType", order.sourceType())
                .addValue("productId", order.productId())
                .addValue("planQty", order.planQty())
                .addValue("completedQty", order.completedQty())
                .addValue("qualifiedQty", order.qualifiedQty())
                .addValue("defectiveQty", order.defectiveQty())
                .addValue("unitId", order.unitId())
                .addValue("plannedStartAt", order.plannedStartAt())
                .addValue("plannedEndAt", order.plannedEndAt())
                .addValue("deliveryDate", order.deliveryDate())
                .addValue("lineId", order.lineId())
                .addValue("routeId", order.routeId())
                .addValue("status", order.status());
    }

    private MapSqlParameterSource kittingAnalysisParams(KittingAnalysis analysis) {
        return params()
                .addValue("id", analysis.id())
                .addValue("analysisNo", analysis.analysisNo())
                .addValue("workOrderId", analysis.workOrderId())
                .addValue("taskId", analysis.taskId())
                .addValue("analysisTime", analysis.analysisTime())
                .addValue("kittingStatus", analysis.kittingStatus())
                .addValue("missingCount", analysis.missingCount())
                .addValue("status", analysis.status());
    }

    private MapSqlParameterSource missingItemParams(KittingMissingItem item) {
        return params()
                .addValue("id", item.id())
                .addValue("analysisId", item.analysisId())
                .addValue("materialId", item.materialId())
                .addValue("requiredQty", item.requiredQty())
                .addValue("availableQty", item.availableQty())
                .addValue("missingQty", item.missingQty())
                .addValue("expectedArrivalAt", item.expectedArrivalAt());
    }

    private MapSqlParameterSource dispatchOrderParams(DispatchOrder dispatch) {
        return params()
                .addValue("id", dispatch.id())
                .addValue("dispatchNo", dispatch.dispatchNo())
                .addValue("workOrderId", dispatch.workOrderId())
                .addValue("lineId", dispatch.lineId())
                .addValue("stationId", dispatch.stationId())
                .addValue("teamId", dispatch.teamId())
                .addValue("planQty", dispatch.planQty())
                .addValue("plannedStartAt", dispatch.plannedStartAt())
                .addValue("plannedEndAt", dispatch.plannedEndAt())
                .addValue("status", dispatch.status());
    }

    private MapSqlParameterSource shopTaskParams(ShopTask task) {
        return params()
                .addValue("id", task.id())
                .addValue("taskNo", task.taskNo())
                .addValue("workOrderId", task.workOrderId())
                .addValue("dispatchId", task.dispatchId())
                .addValue("productId", task.productId())
                .addValue("productName", task.productName())
                .addValue("routeId", task.routeId())
                .addValue("lineId", task.lineId())
                .addValue("lineName", task.lineName())
                .addValue("teamId", task.teamId())
                .addValue("planQty", task.planQty())
                .addValue("startedAt", task.startedAt())
                .addValue("endedAt", task.endedAt())
                .addValue("status", task.status());
    }

    private RowMapper<WorkOrder> workOrderMapper() {
        return (rs, rowNum) -> new WorkOrder(
                rs.getLong("id"),
                rs.getString("work_order_no"),
                rs.getString("external_no"),
                rs.getString("source_type"),
                getLong(rs, "product_id"),
                rs.getBigDecimal("plan_qty"),
                rs.getBigDecimal("completed_qty"),
                rs.getBigDecimal("qualified_qty"),
                rs.getBigDecimal("defective_qty"),
                getLong(rs, "unit_id"),
                rs.getTimestamp("planned_start_at") == null ? null : rs.getTimestamp("planned_start_at").toLocalDateTime(),
                rs.getTimestamp("planned_end_at") == null ? null : rs.getTimestamp("planned_end_at").toLocalDateTime(),
                rs.getDate("delivery_date") == null ? null : rs.getDate("delivery_date").toLocalDate(),
                getLong(rs, "line_id"),
                getLong(rs, "route_id"),
                rs.getString("status")
        );
    }

    private RowMapper<KittingAnalysis> kittingAnalysisMapper() {
        return (rs, rowNum) -> new KittingAnalysis(
                rs.getLong("id"),
                rs.getString("analysis_no"),
                getLong(rs, "work_order_id"),
                getLong(rs, "task_id"),
                rs.getTimestamp("analysis_time") == null ? null : rs.getTimestamp("analysis_time").toLocalDateTime(),
                rs.getString("kitting_status"),
                getInteger(rs, "missing_count"),
                rs.getString("status")
        );
    }

    private RowMapper<KittingMissingItem> missingItemMapper() {
        return (rs, rowNum) -> new KittingMissingItem(
                rs.getLong("id"),
                getLong(rs, "analysis_id"),
                getLong(rs, "material_id"),
                rs.getBigDecimal("required_qty"),
                rs.getBigDecimal("available_qty"),
                rs.getBigDecimal("missing_qty"),
                rs.getTimestamp("expected_arrival_at") == null ? null : rs.getTimestamp("expected_arrival_at").toLocalDateTime()
        );
    }

    private RowMapper<DispatchOrder> dispatchOrderMapper() {
        return (rs, rowNum) -> new DispatchOrder(
                rs.getLong("id"),
                rs.getString("dispatch_no"),
                getLong(rs, "work_order_id"),
                getLong(rs, "line_id"),
                getLong(rs, "station_id"),
                getLong(rs, "team_id"),
                rs.getBigDecimal("plan_qty"),
                rs.getTimestamp("planned_start_at") == null ? null : rs.getTimestamp("planned_start_at").toLocalDateTime(),
                rs.getTimestamp("planned_end_at") == null ? null : rs.getTimestamp("planned_end_at").toLocalDateTime(),
                rs.getString("status")
        );
    }

    private RowMapper<ShopTask> shopTaskMapper() {
        return (rs, rowNum) -> new ShopTask(
                rs.getLong("id"),
                rs.getString("task_no"),
                getLong(rs, "work_order_id"),
                getLong(rs, "dispatch_id"),
                getLong(rs, "product_id"),
                getLong(rs, "route_id"),
                getLong(rs, "line_id"),
                getLong(rs, "team_id"),
                rs.getBigDecimal("plan_qty"),
                rs.getTimestamp("started_at") == null ? null : rs.getTimestamp("started_at").toLocalDateTime(),
                rs.getTimestamp("ended_at") == null ? null : rs.getTimestamp("ended_at").toLocalDateTime(),
                rs.getString("status"),
                null, null, null, null
        );
    }

    private RowMapper<ShopTask> shopTaskWithNamesMapper() {
        return (rs, rowNum) -> new ShopTask(
                rs.getLong("id"),
                rs.getString("task_no"),
                getLong(rs, "work_order_id"),
                getLong(rs, "dispatch_id"),
                getLong(rs, "product_id"),
                getLong(rs, "route_id"),
                getLong(rs, "line_id"),
                getLong(rs, "team_id"),
                rs.getBigDecimal("plan_qty"),
                rs.getTimestamp("started_at") == null ? null : rs.getTimestamp("started_at").toLocalDateTime(),
                rs.getTimestamp("ended_at") == null ? null : rs.getTimestamp("ended_at").toLocalDateTime(),
                rs.getString("status"),
                rs.getString("work_order_no"),
                rs.getString("product_name"),
                rs.getString("route_name"),
                rs.getString("line_name")
        );
    }

    private Long getLong(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : value;
    }

    private Integer getInteger(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : value;
    }
}
