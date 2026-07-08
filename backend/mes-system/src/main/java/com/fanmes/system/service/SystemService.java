package com.fanmes.system.service;

import com.fanmes.system.domain.dto.OperationLogCreateDTO;
import com.fanmes.system.domain.dto.SequenceNextRequest;
import com.fanmes.system.domain.entity.SysMenu;
import java.util.List;

public interface SystemService {
    List<SysMenu> menus();
    String nextCode(SequenceNextRequest request);
    void saveOperationLog(OperationLogCreateDTO dto);
}
