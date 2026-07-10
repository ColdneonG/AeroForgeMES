package com.fanmes.report.domain.vo;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class DashboardStockVO {
    private String materialCode;
    private String materialName;
    private String unitName;
    private BigDecimal requiredQty;
    private BigDecimal actualQty;
    private String stockStatus;
}
