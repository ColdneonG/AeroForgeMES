package com.fanmes.auth.mapper;

import com.fanmes.auth.domain.entity.SysUser;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AuthMapper {
    SysUser findUserByUsername(@Param("username") String username);
    List<String> findRoleCodes(@Param("userId") Long userId);
    List<String> findPermissionCodes(@Param("userId") Long userId);
    List<SysUser> findAllUsers();
}
