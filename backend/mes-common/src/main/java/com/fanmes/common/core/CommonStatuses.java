package com.fanmes.common.core;

import java.util.Set;

public final class CommonStatuses {
    public static final String DRAFT = "草稿";
    public static final String ENABLED = "启用";
    public static final String DISABLED = "停用";
    public static final String WAIT_ISSUE = "待下发";
    public static final String ISSUED = "已下发";
    public static final String RUNNING = "生产中";
    public static final String PAUSED = "暂停";
    public static final String WAIT_INSPECTION = "待检";
    public static final String INSPECTING = "检验中";
    public static final String QUALIFIED = "合格";
    public static final String UNQUALIFIED = "不合格";
    public static final String PENDING = "待处理";
    public static final String PROCESSING = "处理中";
    public static final String COMPLETED = "已完成";
    public static final String CLOSED = "已关闭";
    public static final String VOIDED = "作废";

    private static final Set<String> TERMINAL = Set.of(CLOSED, VOIDED);

    private CommonStatuses() {
    }

    public static boolean isTerminal(String status) {
        return TERMINAL.contains(status);
    }
}
