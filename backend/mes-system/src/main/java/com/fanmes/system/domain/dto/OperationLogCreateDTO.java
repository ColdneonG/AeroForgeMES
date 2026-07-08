package com.fanmes.system.domain.dto;

import lombok.Data;

@Data
public class OperationLogCreateDTO {
    private String bizType;
    private Long bizId;
    private String action;
    private String requestBody;
    private Long operatorId;
    private String result;
    private String errorMessage;
}
