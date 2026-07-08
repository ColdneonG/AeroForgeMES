package com.fanmes.system.domain.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class SequenceNextRequest {
    private String ruleCode;
    private LocalDate bizDate = LocalDate.now();
}
