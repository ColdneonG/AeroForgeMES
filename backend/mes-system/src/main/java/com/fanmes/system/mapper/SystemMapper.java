package com.fanmes.system.mapper;

import com.fanmes.system.domain.dto.OperationLogCreateDTO;
import com.fanmes.system.domain.entity.SequenceRule;
import com.fanmes.system.domain.entity.SysMenu;
import com.fanmes.system.domain.entity.MasterOrg;
import com.fanmes.system.domain.entity.MasterTeam;
import com.fanmes.system.domain.entity.MasterWorkshop;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SystemMapper {
    List<SysMenu> findMenus();
    SequenceRule findEnabledRule(@Param("ruleCode") String ruleCode);
    Long findCurrentValue(@Param("ruleId") Long ruleId, @Param("periodKey") String periodKey);
    int insertSequenceValue(@Param("id") Long id, @Param("ruleId") Long ruleId,
                            @Param("periodKey") String periodKey, @Param("currentValue") Long currentValue);
    int updateSequenceValue(@Param("ruleId") Long ruleId, @Param("periodKey") String periodKey,
                            @Param("currentValue") Long currentValue);
    int saveOperationLog(@Param("id") Long id, @Param("dto") OperationLogCreateDTO dto);
    List<MasterOrg> findEnabledOrgs();
    List<MasterWorkshop> findEnabledWorkshops(@Param("orgId") Long orgId);
    List<MasterTeam> findEnabledTeams(@Param("workshopId") Long workshopId);
}
