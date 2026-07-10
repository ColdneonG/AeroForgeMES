package com.fanmes.report.domain.vo;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportDatasetVO {
    private String metricCode;
    private List<ColumnVO> columns;
    private List<Map<String, Object>> rows;

    @Data
    @AllArgsConstructor
    public static class ColumnVO {
        private String key;
        private String label;
    }
}
