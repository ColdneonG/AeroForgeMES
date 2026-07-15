package com.fanmes.system.service.impl;

import com.fanmes.common.exception.BusinessException;
import com.fanmes.system.domain.dto.OperationLogCreateDTO;
import com.fanmes.system.domain.dto.SequenceNextRequest;
import com.fanmes.system.domain.entity.SequenceRule;
import com.fanmes.system.domain.entity.SysMenu;
import com.fanmes.system.domain.entity.MasterOrg;
import com.fanmes.system.domain.entity.MasterTeam;
import com.fanmes.system.domain.entity.MasterWorkshop;
import com.fanmes.system.mapper.SystemMapper;
import com.fanmes.system.service.SystemService;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SystemServiceImpl implements SystemService {
    private final SystemMapper systemMapper;

    @Override
    public List<SysMenu> menus() {
        return systemMapper.findMenus();
    }

    @Override
    public String nextCode(SequenceNextRequest request) {
        SequenceRule rule = systemMapper.findEnabledRule(request.getRuleCode());
        if (rule == null) {
            throw new BusinessException("编号规则不存在或已停用");
        }
        String datePattern = rule.getDatePattern() == null ? "yyyyMMdd" : rule.getDatePattern();
        String periodKey = request.getBizDate().format(DateTimeFormatter.ofPattern(datePattern));
        Long currentValue = systemMapper.findCurrentValue(rule.getId(), periodKey);
        long next = currentValue == null ? 1L : currentValue + 1L;
        if (currentValue == null) {
            systemMapper.insertSequenceValue(System.currentTimeMillis(), rule.getId(), periodKey, next);
        } else {
            systemMapper.updateSequenceValue(rule.getId(), periodKey, next);
        }
        String prefix = rule.getPrefix() == null ? "" : rule.getPrefix();
        int serialLength = rule.getSerialLength() == null ? 4 : rule.getSerialLength();
        return prefix + periodKey + String.format("%0" + serialLength + "d", next);
    }

    @Override
    public void saveOperationLog(OperationLogCreateDTO dto) {
        systemMapper.saveOperationLog(System.currentTimeMillis(), dto);
    }

    @Override
    public List<MasterOrg> orgTree() {
        List<MasterOrg> orgs = systemMapper.findEnabledOrgs();
        Map<Long, MasterOrg> byId = new LinkedHashMap<>();
        orgs.forEach(org -> byId.put(org.getId(), org));
        List<MasterOrg> roots = new java.util.ArrayList<>();
        orgs.forEach(org -> {
            MasterOrg parent = org.getParentId() == null ? null : byId.get(org.getParentId());
            if (parent == null) roots.add(org); else parent.getChildren().add(org);
        });
        return roots;
    }

    @Override
    public List<MasterWorkshop> workshops(Long orgId) {
        return systemMapper.findEnabledWorkshops(orgId);
    }

    @Override
    public List<MasterTeam> teams(Long workshopId) {
        return systemMapper.findEnabledTeams(workshopId);
    }
}
