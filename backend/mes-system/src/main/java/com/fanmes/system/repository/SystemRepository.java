package com.fanmes.system.repository;

import com.fanmes.system.domain.dto.OperationLogCreateDTO;
import com.fanmes.system.domain.entity.SequenceRule;
import com.fanmes.system.domain.entity.SysMenu;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SystemRepository {

    @Select("""
            select id, parent_id, menu_code, menu_name, module_code, path, sort_no
            from sys_menu
            order by coalesce(sort_no, 0), id
            """)
    List<SysMenu> findMenus();

    @Select("""
            select id, rule_code, biz_type, prefix, date_pattern, serial_length, reset_cycle, status
            from sys_sequence_rule
            where rule_code = #{ruleCode}
              and coalesce(status, '启用') <> '停用'
            """)
    SequenceRule findEnabledRule(@Param("ruleCode") String ruleCode);

    @Select("""
            select current_value
            from sys_sequence_value
            where rule_id = #{ruleId}
              and period_key = #{periodKey}
            """)
    Long findCurrentValue(@Param("ruleId") Long ruleId, @Param("periodKey") String periodKey);

    @Insert("""
            insert into sys_sequence_value(id, rule_id, period_key, current_value)
            values(#{id}, #{ruleId}, #{periodKey}, #{currentValue})
            """)
    int insertSequenceValue(@Param("id") Long id,
                            @Param("ruleId") Long ruleId,
                            @Param("periodKey") String periodKey,
                            @Param("currentValue") Long currentValue);

    @Update("""
            update sys_sequence_value
            set current_value = #{currentValue}
            where rule_id = #{ruleId}
              and period_key = #{periodKey}
            """)
    int updateSequenceValue(@Param("ruleId") Long ruleId,
                            @Param("periodKey") String periodKey,
                            @Param("currentValue") Long currentValue);

    @Insert("""
            insert into sys_operation_log(
                id, biz_type, biz_id, action, request_body,
                operator_id, operated_at, result, error_message
            )
            values(
                #{id}, #{dto.bizType}, #{dto.bizId}, #{dto.action}, #{dto.requestBody},
                #{dto.operatorId}, now(), #{dto.result}, #{dto.errorMessage}
            )
            """)
    int saveOperationLog(@Param("id") Long id, @Param("dto") OperationLogCreateDTO dto);
}
