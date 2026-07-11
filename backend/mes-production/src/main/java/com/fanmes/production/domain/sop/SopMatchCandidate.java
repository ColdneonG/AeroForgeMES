package com.fanmes.production.domain.sop;

public record SopMatchCandidate(
        SopDocument document,
        SopVersion version,
        SopBinding binding
) {
}
