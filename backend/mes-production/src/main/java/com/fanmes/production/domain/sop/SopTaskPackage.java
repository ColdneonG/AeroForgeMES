package com.fanmes.production.domain.sop;

import java.util.List;

public record SopTaskPackage(
        SopDocument document,
        SopVersion version,
        SopTaskSnapshot snapshot,
        SopExecutionRecord execution,
        List<SopStep> steps,
        List<SopAttachment> attachments,
        List<SopStepExecutionRecord> stepRecords
) {
}
