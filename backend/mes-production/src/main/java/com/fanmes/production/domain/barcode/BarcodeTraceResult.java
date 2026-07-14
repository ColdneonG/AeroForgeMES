package com.fanmes.production.domain.barcode;

import java.util.List;
import java.util.Map;

public record BarcodeTraceResult(
        String root,
        String queryMode,
        List<BarcodeTraceNode> nodes,
        Map<String, Object> details,
        List<BarcodeTraceEvent> events
) {
}
