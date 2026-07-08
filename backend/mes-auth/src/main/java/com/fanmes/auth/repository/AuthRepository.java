package com.fanmes.auth.repository;

import com.fanmes.auth.domain.entity.SysUser;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AuthRepository {

    @Select("""
            select id, username, password, display_name, mobile, employee_no, org_id, team_id, status
            from sys_user
            where username = #{username}
            """)
    SysUser findUserByUsername(@Param("username") String username);

    @Select("""
            select r.role_code
            from sys_role r
            join sys_user_role ur on ur.role_id = r.id
            where ur.user_id = #{userId}
            """)
    List<String> findRoleCodes(@Param("userId") Long userId);

    @Select("""
            select distinct p.permission_code
            from sys_permission p
            join sys_role_permission rp on rp.permission_id = p.id
            join sys_user_role ur on ur.role_id = rp.role_id
            where ur.user_id = #{userId}
            """)
    List<String> findPermissionCodes(@Param("userId") Long userId);
}
