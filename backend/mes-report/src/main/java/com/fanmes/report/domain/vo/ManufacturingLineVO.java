package com.fanmes.report.domain.vo;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class ManufacturingLineVO {
    private Long lineId;
    private String lineCode;
    private String lineName;
    private String batchNo;
    private String workOrderNo;
    private String productName;
    private BigDecimal plannedQty;
    private BigDecimal completedQty;
    private BigDecimal goodQty;
    private BigDecimal defectQty;
    private BigDecimal completionRate;
    private BigDecimal oee;
    private BigDecimal performance;
    private List<Integer> outputTrend;
    private Integer runningOrderCount;
    private boolean active;
}
