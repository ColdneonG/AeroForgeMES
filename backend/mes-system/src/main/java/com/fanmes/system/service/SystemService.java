package com.fanmes.system.service;

import com.fanmes.system.domain.dto.OperationLogCreateDTO;
import com.fanmes.system.domain.dto.SequenceNextRequest;
import com.fanmes.system.domain.entity.SysMenu;
import com.fanmes.system.domain.entity.MasterOrg;
import com.fanmes.system.domain.entity.MasterTeam;
import com.fanmes.system.domain.entity.MasterWorkshop;
import java.util.List;

public interface SystemService {
    List<SysMenu> menus();
    String nextCode(SequenceNextRequest request);
    void saveOperationLog(OperationLogCreateDTO dto);
    List<MasterOrg> orgTree();
    List<MasterWorkshop> workshops(Long orgId);
    List<MasterTeam> teams(Long workshopId);
}
