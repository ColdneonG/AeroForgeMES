package com.fanmes.production.domain.sop;

import java.util.Set;

public final class SopStatuses {
    public static final String ENABLED = "ENABLED";
    public static final String DISABLED = "DISABLED";
    public static final String VOID = "VOID";

    public static final String DRAFT = "DRAFT";
    public static final String PENDING_REVIEW = "PENDING_REVIEW";
    public static final String REJECTED = "REJECTED";
    public static final String APPROVED = "APPROVED";
    public static final String PENDING_EFFECTIVE = "PENDING_EFFECTIVE";
    public static final String EFFECTIVE = "EFFECTIVE";

    public static final String ACTIVE = "ACTIVE";
    public static final String COMPLETED = "COMPLETED";

    private static final Set<String> EDITABLE_VERSION_STATUSES = Set.of(DRAFT, REJECTED);
    private static final Set<String> RELEASED_VERSION_STATUSES = Set.of(APPROVED, PENDING_EFFECTIVE, EFFECTIVE, DISABLED, VOID);

    private SopStatuses() {
    }

    public static boolean isEditableVersion(String status) {
        return EDITABLE_VERSION_STATUSES.contains(status);
    }

    public static boolean isReleasedVersion(String status) {
        return RELEASED_VERSION_STATUSES.contains(status);
    }
}
