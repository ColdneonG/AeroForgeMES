package com.fanmes.production.domain;

import java.util.Set;

public final class ProductionStatuses {
    public static final String DRAFT = "草稿";
    public static final String ENABLED = "启用";
    public static final String DISABLED = "停用";
    public static final String WAIT_ISSUE = "待下发";
    public static final String ISSUED = "已下发";
    public static final String RUNNING = "生产中";
    public static final String PAUSED = "暂停";
    public static final String COMPLETED = "已完成";
    public static final String CLOSED = "已关闭";
    public static final String VOIDED = "作废";

    private ProductionStatuses() {
    }

    public static boolean isTerminal(String status) {
        return Set.of(CLOSED, VOIDED).contains(status);
    }
}
