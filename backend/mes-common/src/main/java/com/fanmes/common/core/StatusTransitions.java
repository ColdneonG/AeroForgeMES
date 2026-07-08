package com.fanmes.common.core;

import com.fanmes.common.exception.BadRequestException;

import java.util.Map;
import java.util.Set;

public final class StatusTransitions {
    private StatusTransitions() {
    }

    public static void ensureEditable(String status, String objectName) {
        if (CommonStatuses.isTerminal(status)) {
            throw new BadRequestException(objectName + " is closed or voided");
        }
    }

    public static void ensureTransition(
            String currentStatus,
            String targetStatus,
            Map<String, Set<String>> transitions,
            String objectName
    ) {
        Set<String> allowedSources = transitions.get(targetStatus);
        if (allowedSources == null || !allowedSources.contains(currentStatus)) {
            throw new BadRequestException(objectName + " cannot change status from " + currentStatus + " to " + targetStatus);
        }
    }
}
