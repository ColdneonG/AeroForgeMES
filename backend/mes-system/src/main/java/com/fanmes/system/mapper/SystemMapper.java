package com.fanmes.system.mapper;

import com.fanmes.system.domain.dto.OperationLogCreateDTO;
import com.fanmes.system.domain.entity.SequenceRule;
import com.fanmes.system.domain.entity.SysMenu;
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
}
