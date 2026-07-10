package com.fanmes.report.domain.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class DailyOutputReportVO {
    private LocalDate statDate;
    private String lineCode;
    private String lineName;
    private String workOrderNo;
    private String productCode;
    private String productName;
    private BigDecimal plannedQty;
    private BigDecimal reportedQty;
    private BigDecimal qualifiedQty;
    private BigDecimal defectiveQty;
    private Integer reportCount;
}
