package com.fanmes.auth.mapper;

import com.fanmes.auth.domain.entity.SysUser;
import com.fanmes.auth.domain.vo.RoleVO;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AuthMapper {
    SysUser findUserByUsername(@Param("username") String username);
    SysUser findUserProfileByUsername(@Param("username") String username);
    List<String> findRoleCodes(@Param("userId") Long userId);
    List<String> findPermissionCodes(@Param("userId") Long userId);
    List<SysUser> findAllUsers();
    int updateProfile(@Param("userId") Long userId, @Param("displayName") String displayName, @Param("mobile") String mobile);
    int updatePassword(@Param("userId") Long userId, @Param("password") String password);
    List<SysUser> findUsers(@Param("keyword") String keyword, @Param("status") String status,
                            @Param("orgId") Long orgId, @Param("teamId") Long teamId,
                            @Param("roleId") Long roleId, @Param("offset") int offset, @Param("pageSize") int pageSize);
    long countUsers(@Param("keyword") String keyword, @Param("status") String status,
                    @Param("orgId") Long orgId, @Param("teamId") Long teamId, @Param("roleId") Long roleId);
    SysUser findUserById(@Param("userId") Long userId);
    List<RoleVO> findRolesByUserId(@Param("userId") Long userId);
    List<RoleVO> findEnabledRoles();
    int insertUser(@Param("user") SysUser user);
    int updateUser(@Param("user") SysUser user);
    int updateUserStatus(@Param("userId") Long userId, @Param("status") String status);
    int deleteUserRoles(@Param("userId") Long userId);
    int insertUserRole(@Param("id") Long id, @Param("userId") Long userId, @Param("roleId") Long roleId);
    int countEnabledRoles(@Param("roleIds") Set<Long> roleIds);
    int countUsersWithRole(@Param("roleId") Long roleId, @Param("excludeUserId") Long excludeUserId);
}
