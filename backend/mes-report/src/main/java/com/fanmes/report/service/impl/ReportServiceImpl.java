package com.fanmes.report.service.impl;

import com.fanmes.report.domain.dto.MetricQuery;
import com.fanmes.report.domain.entity.BoardConfig;
import com.fanmes.report.domain.entity.MetricDef;
import com.fanmes.report.domain.entity.MetricSnapshot;
import com.fanmes.report.domain.vo.DashboardMetricVO;
import com.fanmes.report.domain.vo.DashboardStockVO;
import com.fanmes.report.domain.vo.DailyOutputReportVO;
import com.fanmes.report.domain.vo.ManufacturingDashboardVO;
import com.fanmes.report.domain.vo.ManufacturingLineVO;
import com.fanmes.report.domain.vo.ReportDatasetVO;
import com.fanmes.report.mapper.ReportMapper;
import com.fanmes.report.service.ReportService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportMapper reportMapper;

    @Override
    public List<MetricDef> metricDefs(String metricType) {
        return reportMapper.findMetricDefs(metricType);
    }

    @Override
    public List<MetricSnapshot> metricSnapshots(MetricQuery query) {
        return reportMapper.findMetricSnapshots(query);
    }

    @Override
    public List<BoardConfig> boards(String boardType) {
        return reportMapper.findBoards(boardType);
    }

    @Override
    public List<DailyOutputReportVO> dailyOutputReport() {
        return reportMapper.findDailyOutputReport();
    }

    @Override
    public ReportDatasetVO reportDataset(String metricCode) {
        return new ReportDatasetVO(metricCode, reportColumns(metricCode), reportMapper.findReportDataset(metricCode));
    }

    @Override
    public ManufacturingDashboardVO manufacturingDashboard() {
        List<ManufacturingLineVO> lines = reportMapper.findManufacturingLines().stream()
                .map(this::toLine)
                .toList();
        return new ManufacturingDashboardVO(lines, oeeMetrics(lines), dashboardStock());
    }

    @Override
    public List<Map<String, Object>> lineBoard() {
        List<Map<String, Object>> rows = reportMapper.findManufacturingLines();
        if (!rows.isEmpty()) {
            return rows.stream().map(this::toLineBoardRow).toList();
        }
        rows = reportMapper.findReportDataset("BOARD-LINE-ASSY-01");
        if (rows.isEmpty()) {
            rows = reportMapper.findReportDataset("OUTPUT_QTY_DAY");
        }
        return rows.stream().map(this::toLineBoardRowFromDataset).toList();
    }

    @Override
    public List<Map<String, Object>> workshopBoard() {
        List<Map<String, Object>> rows = reportMapper.findReportDataset("BOARD-WORKSHOP-ASSY");
        if (rows.isEmpty()) {
            // 无车间区域数据集时，尝试从产线数据聚合为车间级别
            return aggregateWorkshopFromLines();
        }
        return rows.stream().map(this::toWorkshopRow).toList();
    }

    /**
     * 将产线级数据聚合为车间区域级数据。
     * 产线到车间的映射通过分组相同车间下的产线实现。
     */
    private List<Map<String, Object>> aggregateWorkshopFromLines() {
        List<Map<String, Object>> lineRows = lineBoard();
        if (lineRows.isEmpty()) {
            return List.of();
        }
        // 按产线名称前缀分组（总装一线、总装二线 → 总装车间）
        Map<String, List<Map<String, Object>>> grouped = new java.util.LinkedHashMap<>();
        for (Map<String, Object> line : lineRows) {
            String lineName = string(line.get("lineName"));
            String groupKey = extractWorkshopKey(lineName);
            grouped.computeIfAbsent(groupKey, k -> new ArrayList<>()).add(line);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        int areaIndex = 1;
        for (Map.Entry<String, List<Map<String, Object>>> entry : grouped.entrySet()) {
            List<Map<String, Object>> lines = entry.getValue();
            Map<String, Object> area = new LinkedHashMap<>();
            // 汇总指标
            BigDecimal totalDefects = lines.stream()
                    .map(l -> decimal(l.get("defectQty")))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalOutput = lines.stream()
                    .map(l -> decimal(l.get("completedQty")))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            int totalOrders = lines.stream()
                    .mapToInt(l -> ((Number) l.getOrDefault("runningOrderCount", 0)).intValue())
                    .sum();
            BigDecimal avgOee = average(lines.stream()
                    .map(l -> percentValue(l.get("equipment"), BigDecimal.ZERO))
                    .toList());
            boolean hasWarn = lines.stream()
                    .anyMatch(l -> "WARN".equalsIgnoreCase(string(l.get("status")))
                            || "warn".equalsIgnoreCase(string(l.get("tone"))));
            area.put("id", "AREA-" + areaIndex);
            area.put("areaCode", "AREA-" + areaIndex);
            area.put("areaName", entry.getKey());
            area.put("status", hasWarn ? "WARN" : "RUNNING");
            area.put("description", "含" + lines.size() + "条产线");
            area.put("orders", totalOrders);
            area.put("outputQty", totalOutput);
            area.put("equipment", avgOee.stripTrailingZeros().toPlainString() + "%");
            area.put("exceptions", totalDefects.intValue());
            area.put("tone", hasWarn ? "warn" : "running");
            result.add(area);
            areaIndex++;
        }
        return result;
    }

    /**
     * 从产线名称提取车间分组键。
     * 例如 "总装一线" → "总装车间", "包装一线" → "包装车间"
     */
    private String extractWorkshopKey(String lineName) {
        if (lineName == null || lineName.isBlank()) {
            return "未分类车间";
        }
        // 去掉末尾的 "一线"、"二线"、"三线"、"A线"、"B线" 等编号
        String key = lineName.replaceAll("[一二三四五六七八九十]+线$", "车间")
                .replaceAll("[A-Z]线$", "车间")
                .replaceAll("线$", "车间");
        // 如果替换后没有变化，加上"车间"后缀
        if (key.equals(lineName)) {
            key = lineName + "车间";
        }
        return key;
    }

    @Override
    public Map<String, Object> controlCenterBoard() {
        List<Map<String, Object>> lineRows = lineBoard();
        List<Map<String, Object>> centerRows = reportMapper.findReportDataset("BOARD-CONTROL-CENTER");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("outputTotal", lineRows.stream()
                .map(row -> decimal(row.get("completedQty")))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        result.put("kpis", controlKpis(centerRows));
        result.put("trend", controlTrend(lineRows));
        result.put("planTrend", controlPlanTrend(lineRows));
        result.put("lines", lineRows);
        result.put("alerts", controlAlerts(centerRows));
        result.put("workOrders", controlWorkOrders(lineRows));
        return result;
    }

    private List<ReportDatasetVO.ColumnVO> reportColumns(String metricCode) {
        return switch (metricCode) {
            case "OUTPUT_QTY_DAY" -> columns(
                    column("statDate", "统计日期"),
                    column("lineCode", "产线编码"),
                    column("lineName", "产线名称"),
                    column("workOrderNo", "工单号"),
                    column("productCode", "产品编码"),
                    column("productName", "产品名称"),
                    column("plannedQty", "计划数量"),
                    column("reportedQty", "报工数量"),
                    column("qualifiedQty", "合格数量"),
                    column("defectiveQty", "不良数量"),
                    column("reportCount", "报工次数"),
                    column("status", "状态")
            );
            case "DEFECT_QTY_DAY" -> columns(
                    column("statDate", "统计日期"),
                    column("lineCode", "产线编码"),
                    column("lineName", "产线名称"),
                    column("workOrderNo", "工单号"),
                    column("productCode", "产品编码"),
                    column("productName", "产品名称"),
                    column("businessNo", "来源单号"),
                    column("businessType", "来源类型"),
                    column("reasonName", "不良原因"),
                    column("defectiveQty", "不良数量"),
                    column("status", "状态"),
                    column("remark", "备注")
            );
            case "OEE_DAY" -> columns(
                    column("statDate", "统计日期"),
                    column("lineCode", "产线编码"),
                    column("lineName", "产线名称"),
                    column("businessNo", "设备编码"),
                    column("availability", "稼动率"),
                    column("performance", "性能"),
                    column("qualityRate", "质量率"),
                    column("oee", "OEE"),
                    column("status", "状态")
            );
            case "ANDON_OPEN" -> columns(
                    column("businessNo", "安灯单号"),
                    column("businessType", "异常类型"),
                    column("reasonName", "异常原因"),
                    column("lineCode", "产线编码"),
                    column("lineName", "产线名称"),
                    column("workOrderNo", "工单号"),
                    column("operatorName", "处理人"),
                    column("startedAt", "发起时间"),
                    column("status", "状态"),
                    column("remark", "备注")
            );
            case "WAGE_AMOUNT_MONTH" -> columns(
                    column("statPeriod", "统计周期"),
                    column("dimensionCode", "人员编码"),
                    column("operatorName", "人员名称"),
                    column("totalQty", "计件数量"),
                    column("totalAmount", "计件金额"),
                    column("endedAt", "确认时间"),
                    column("status", "状态")
            );
            case "BOARD-LINE-ASSY-01" -> columns(
                    column("statDate", "统计日期"),
                    column("lineCode", "产线编码"),
                    column("lineName", "产线名称"),
                    column("workOrderNo", "工单号"),
                    column("productName", "产品名称"),
                    column("plannedQty", "计划数量"),
                    column("reportedQty", "报工数量"),
                    column("qualifiedQty", "合格数量"),
                    column("defectiveQty", "不良数量"),
                    column("oee", "OEE"),
                    column("performance", "性能"),
                    column("reportCount", "运行工单数"),
                    column("status", "状态")
            );
            case "BOARD-WORKSHOP-ASSY" -> columns(
                    column("statDate", "统计日期"),
                    column("lineCode", "产线编码"),
                    column("lineName", "产线名称"),
                    column("qualifiedQty", "合格数量"),
                    column("defectiveQty", "不良数量"),
                    column("oee", "OEE"),
                    column("businessNo", "安灯单号"),
                    column("reasonName", "异常原因"),
                    column("status", "状态"),
                    column("remark", "备注")
            );
            case "BOARD-CONTROL-CENTER" -> columns(
                    column("dimensionType", "模块"),
                    column("dimensionName", "模块名称"),
                    column("status", "状态"),
                    column("qualifiedQty", "合格数量"),
                    column("defectiveQty", "不良数量"),
                    column("oee", "OEE"),
                    column("remark", "备注")
            );
            default -> columns(
                    column("statDate", "统计日期"),
                    column("statPeriod", "统计周期"),
                    column("dimensionType", "维度类型"),
                    column("dimensionCode", "维度编码"),
                    column("dimensionName", "维度名称"),
                    column("status", "状态")
            );
        };
    }

    private List<ReportDatasetVO.ColumnVO> columns(ReportDatasetVO.ColumnVO... columns) {
        return List.of(columns);
    }

    private ReportDatasetVO.ColumnVO column(String key, String label) {
        return new ReportDatasetVO.ColumnVO(key, label);
    }

    private ManufacturingLineVO toLine(Map<String, Object> row) {
        ManufacturingLineVO line = new ManufacturingLineVO();
        BigDecimal planned = decimal(row.get("plannedQty"));
        BigDecimal completed = decimal(row.get("completedQty"));
        BigDecimal good = decimal(row.get("goodQty"));
        BigDecimal defects = decimal(row.get("defectQty"));
        BigDecimal completionRate = percent(completed, planned);
        BigDecimal quality = percent(good, good.add(defects));
        BigDecimal performance = percentValue(row.get("performance"), completionRate);

        line.setLineId(asLong(row.get("lineId")));
        line.setLineCode(string(row.get("lineCode")));
        line.setLineName(string(row.get("lineName")));
        line.setBatchNo(string(row.get("batchNo")));
        line.setWorkOrderNo(string(row.get("workOrderNo")));
        line.setProductName(string(row.get("productName")));
        line.setPlannedQty(planned);
        line.setCompletedQty(completed);
        line.setGoodQty(good);
        line.setDefectQty(defects);
        line.setCompletionRate(completionRate);
        line.setOee(percentValue(row.get("oee"), completionRate.multiply(quality).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)));
        line.setPerformance(performance);
        line.setOutputTrend(parsedTrend(row.get("outputTrend")));
        line.setRunningOrderCount(((Number) row.getOrDefault("runningOrderCount", 0)).intValue());
        line.setActive(asLong(row.get("activeFlag")) == 1L || line.getRunningOrderCount() > 0);
        return line;
    }

    private List<DashboardMetricVO> oeeMetrics(List<ManufacturingLineVO> lines) {
        Map<String, Object> raw = reportMapper.findDashboardOeeMetrics();
        BigDecimal averageOee = average(lines.stream().map(ManufacturingLineVO::getOee).toList());
        BigDecimal averagePerformance = average(lines.stream().map(ManufacturingLineVO::getPerformance).toList());
        return List.of(
                new DashboardMetricVO("oee", percentValue(raw.get("oee"), averageOee)),
                new DashboardMetricVO("availability", percentValue(raw.get("availability"), BigDecimal.valueOf(100))),
                new DashboardMetricVO("performance", percentValue(raw.get("performance"), averagePerformance)),
                new DashboardMetricVO("quality", percentValue(raw.get("quality"), averageQuality(lines)))
        );
    }

    private List<DashboardStockVO> dashboardStock() {
        return reportMapper.findDashboardStock().stream().map(row -> {
            DashboardStockVO stock = new DashboardStockVO();
            BigDecimal required = decimal(row.get("requiredQty"));
            BigDecimal actual = decimal(row.get("actualQty"));
            stock.setMaterialCode(string(row.get("materialCode")));
            stock.setMaterialName(string(row.get("materialName")));
            stock.setUnitName(string(row.get("unitName")));
            stock.setRequiredQty(required);
            stock.setActualQty(actual);
            stock.setStockStatus(actual.compareTo(required) >= 0 ? "SUFFICIENT"
                    : actual.signum() == 0 ? "SHORTAGE" : "LOW");
            return stock;
        }).toList();
    }

    private Map<String, Object> toLineBoardRow(Map<String, Object> row) {
        ManufacturingLineVO line = toLine(row);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", line.getLineCode());
        result.put("lineCode", line.getLineCode());
        result.put("lineName", line.getLineName());
        result.put("productName", line.getProductName());
        result.put("workOrderNo", line.getWorkOrderNo());
        result.put("planQty", line.getPlannedQty());
        result.put("completedQty", line.getCompletedQty());
        result.put("defectQty", line.getDefectQty());
        result.put("rate", line.getCompletionRate());
        result.put("status", line.isActive() ? "RUNNING" : "WAITING");
        result.put("equipment", line.getOee().compareTo(BigDecimal.valueOf(75)) >= 0 ? "NORMAL" : "WARN");
        result.put("exception", line.getDefectQty().signum() > 0 ? "DEFECT " + line.getDefectQty().stripTrailingZeros().toPlainString() : "NONE");
        result.put("eta", line.getRunningOrderCount() > 0 ? "TODAY" : "-");
        result.put("tone", line.isActive() ? "running" : "warn");
        result.put("runningOrderCount", line.getRunningOrderCount());
        result.put("outputTrend", line.getOutputTrend());
        return result;
    }

    private Map<String, Object> toLineBoardRowFromDataset(Map<String, Object> row) {
        BigDecimal planned = decimal(row.get("plannedQty"));
        BigDecimal completed = decimal(row.get("reportedQty"));
        if (completed.signum() == 0) {
            completed = decimal(row.get("qualifiedQty"));
        }
        BigDecimal defects = decimal(row.get("defectiveQty"));
        BigDecimal rate = percent(completed, planned);
        String status = string(row.get("status"));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", string(row.get("lineCode")));
        result.put("lineCode", string(row.get("lineCode")));
        result.put("lineName", string(row.get("lineName")).isBlank() ? string(row.get("dimensionName")) : string(row.get("lineName")));
        result.put("productName", string(row.get("productName")));
        result.put("workOrderNo", string(row.get("workOrderNo")));
        result.put("planQty", planned);
        result.put("completedQty", completed);
        result.put("defectQty", defects);
        result.put("rate", rate);
        result.put("status", status.isBlank() ? "RUNNING" : status);
        result.put("equipment", percentText(row.get("oee")));
        result.put("exception", defects.signum() > 0 ? "DEFECT " + defects.stripTrailingZeros().toPlainString() : "NONE");
        result.put("eta", "-");
        result.put("tone", tone(status, defects));
        result.put("runningOrderCount", intValue(row.get("reportCount")));
        result.put("outputTrend", normalizedTrend(List.of(completed)));
        return result;
    }

    private Map<String, Object> toWorkshopRow(Map<String, Object> row) {
        BigDecimal defects = decimal(row.get("defectiveQty"));
        BigDecimal output = decimal(row.get("reportedQty"));
        if (output.signum() == 0) {
            output = decimal(row.get("qualifiedQty"));
        }
        String status = string(row.get("status"));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", string(row.get("dimensionCode")));
        result.put("areaCode", string(row.get("dimensionCode")));
        result.put("areaName", string(row.get("dimensionName")).isBlank() ? string(row.get("lineName")) : string(row.get("dimensionName")));
        result.put("status", status.isBlank() ? (defects.signum() > 0 ? "WARN" : "RUNNING") : status);
        result.put("description", string(row.get("remark")).isBlank() ? "Realtime workshop board data" : string(row.get("remark")));
        result.put("orders", intValue(row.get("reportCount")));
        result.put("outputQty", output);
        result.put("equipment", percentText(row.get("oee")));
        result.put("exceptions", defects.intValue());
        result.put("tone", tone(status, defects));
        return result;
    }

    private List<Map<String, Object>> controlKpis(List<Map<String, Object>> rows) {
        if (!rows.isEmpty()) {
            return rows.stream().map(row -> {
                Map<String, Object> result = new LinkedHashMap<>();
                String code = string(row.get("dimensionCode"));
                result.put("label", string(row.get("dimensionName")).isBlank() ? code : string(row.get("dimensionName")));
                result.put("value", controlKpiValue(row));
                result.put("note", string(row.get("remark")));
                result.put("status", string(row.get("status")));
                result.put("tone", tone(string(row.get("status")), decimal(row.get("defectiveQty"))));
                return result;
            }).toList();
        }
        return oeeMetrics(reportMapper.findManufacturingLines().stream().map(this::toLine).toList()).stream()
                .map(metric -> {
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("label", metric.getMetricKey());
                    result.put("value", metric.getValue());
                    result.put("note", "");
                    result.put("status", "RUNNING");
                    result.put("tone", "ok");
                    return result;
                }).toList();
    }

    private List<Integer> controlTrend(List<Map<String, Object>> lineRows) {
        List<BigDecimal> values = lineRows.stream()
                .map(row -> decimal(row.get("completedQty")))
                .toList();
        return normalizedTrend(values);
    }

    private List<Integer> controlPlanTrend(List<Map<String, Object>> lineRows) {
        List<BigDecimal> values = lineRows.stream()
                .map(row -> decimal(row.get("plannedQty")))
                .toList();
        return normalizedTrend(values);
    }

    private List<Map<String, Object>> controlAlerts(List<Map<String, Object>> rows) {
        return rows.stream()
                .filter(row -> !"RUNNING".equalsIgnoreCase(string(row.get("status"))))
                .map(row -> {
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("code", string(row.get("dimensionCode")));
                    result.put("text", string(row.get("remark")).isBlank() ? string(row.get("dimensionName")) : string(row.get("remark")));
                    result.put("status", string(row.get("status")));
                    result.put("tone", tone(string(row.get("status")), decimal(row.get("defectiveQty"))));
                    return result;
                }).toList();
    }

    private List<Map<String, Object>> controlWorkOrders(List<Map<String, Object>> lineRows) {
        return lineRows.stream().map(row -> {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("workOrderNo", row.get("workOrderNo"));
            result.put("productName", row.get("productName"));
            result.put("rate", row.get("rate"));
            result.put("warning", row.get("exception"));
            return result;
        }).toList();
    }

    private Object controlKpiValue(Map<String, Object> row) {
        BigDecimal qualified = decimal(row.get("qualifiedQty"));
        BigDecimal defective = decimal(row.get("defectiveQty"));
        BigDecimal oee = decimal(row.get("oee"));
        if (qualified.signum() > 0 && defective.signum() > 0) {
            return qualified.add(defective);
        }
        if (qualified.signum() > 0) return qualified;
        if (defective.signum() > 0) return defective;
        if (oee.signum() > 0) return percentValue(oee, BigDecimal.ZERO);
        return string(row.get("status"));
    }

    private String tone(String status, BigDecimal defects) {
        String text = status == null ? "" : status.toUpperCase();
        if (text.contains("ALARM") || text.contains("DANGER") || text.contains("ERROR") || text.contains("OPEN")) return "danger";
        if (text.contains("WARN") || text.contains("WAIT") || defects.signum() > 0) return "warn";
        return "running";
    }

    private String percentText(Object value) {
        BigDecimal percent = percentValue(value, BigDecimal.ZERO);
        return percent.stripTrailingZeros().toPlainString() + "%";
    }

    private List<Integer> normalizedTrend(List<BigDecimal> values) {
        if (values.isEmpty()) return List.of(0, 0, 0, 0, 0, 0, 0);
        BigDecimal max = values.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ONE);
        List<Integer> result = values.stream().limit(7)
                .map(value -> max.signum() == 0 ? 0 : value.multiply(BigDecimal.valueOf(100)).divide(max, 0, RoundingMode.HALF_UP).intValue())
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        while (result.size() < 7) result.add(0, 0);
        return result;
    }

    private List<Integer> parsedTrend(Object value) {
        String raw = string(value);
        if (raw.isBlank()) return List.of(0, 0, 0, 0, 0, 0, 0);
        List<Integer> result = new ArrayList<>();
        for (String part : raw.split(",")) {
            try {
                result.add(Integer.parseInt(part.trim()));
            } catch (NumberFormatException ignored) {
                result.add(0);
            }
        }
        while (result.size() < 7) result.add(0, 0);
        return result.stream().limit(7).toList();
    }

    private BigDecimal averageQuality(List<ManufacturingLineVO> lines) {
        return average(lines.stream().map(line -> percent(line.getGoodQty(), line.getGoodQty().add(line.getDefectQty()))).toList());
    }

    private BigDecimal average(List<BigDecimal> values) {
        if (values.isEmpty()) return BigDecimal.ZERO;
        return values.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(values.size()), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal percentValue(Object value, BigDecimal fallback) {
        BigDecimal decimal = decimal(value);
        return value == null ? fallback : (decimal.compareTo(BigDecimal.ONE) <= 0 ? decimal.multiply(BigDecimal.valueOf(100)) : decimal);
    }

    private BigDecimal percent(BigDecimal numerator, BigDecimal denominator) {
        return denominator.signum() == 0 ? BigDecimal.ZERO
                : numerator.multiply(BigDecimal.valueOf(100)).divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal decimal(Object value) {
        return value instanceof Number number ? new BigDecimal(number.toString()) : BigDecimal.ZERO;
    }

    private Long asLong(Object value) {
        return value instanceof Number number ? number.longValue() : 0L;
    }

    private Integer intValue(Object value) {
        return value instanceof Number number ? number.intValue() : 0;
    }

    private String string(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
