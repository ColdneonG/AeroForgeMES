package com.fanmes.system.service.impl;

import com.fanmes.common.exception.BusinessException;
import com.fanmes.system.domain.dto.OperationLogCreateDTO;
import com.fanmes.system.domain.dto.SequenceNextRequest;
import com.fanmes.system.domain.entity.SequenceRule;
import com.fanmes.system.domain.entity.SysMenu;
import com.fanmes.system.repository.SystemRepository;
import com.fanmes.system.service.SystemService;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SystemServiceImpl implements SystemService {
    private final SystemRepository systemRepository;

    @Override
    public List<SysMenu> menus() {
        return systemRepository.findMenus();
    }

    @Override
    public String nextCode(SequenceNextRequest request) {
        SequenceRule rule = systemRepository.findEnabledRule(request.getRuleCode());
        if (rule == null) {
            throw new BusinessException("编号规则不存在或已停用");
        }
        String datePattern = rule.getDatePattern() == null ? "yyyyMMdd" : rule.getDatePattern();
        String periodKey = request.getBizDate().format(DateTimeFormatter.ofPattern(datePattern));
        Long currentValue = systemRepository.findCurrentValue(rule.getId(), periodKey);
        long next = currentValue == null ? 1L : currentValue + 1L;
        if (currentValue == null) {
            systemRepository.insertSequenceValue(System.currentTimeMillis(), rule.getId(), periodKey, next);
        } else {
            systemRepository.updateSequenceValue(rule.getId(), periodKey, next);
        }
        String prefix = rule.getPrefix() == null ? "" : rule.getPrefix();
        int serialLength = rule.getSerialLength() == null ? 4 : rule.getSerialLength();
        return prefix + periodKey + String.format("%0" + serialLength + "d", next);
    }

    @Override
    public void saveOperationLog(OperationLogCreateDTO dto) {
        systemRepository.saveOperationLog(System.currentTimeMillis(), dto);
    }
}
