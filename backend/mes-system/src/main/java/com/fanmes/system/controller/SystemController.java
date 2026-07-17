package com.fanmes.system.controller;

import com.fanmes.common.api.ApiResponse;
import com.fanmes.system.domain.dto.OperationLogCreateDTO;
import com.fanmes.system.domain.dto.SequenceNextRequest;
import com.fanmes.system.domain.entity.SysMenu;
import com.fanmes.system.domain.entity.MasterOrg;
import com.fanmes.system.domain.entity.MasterTeam;
import com.fanmes.system.domain.entity.MasterWorkshop;
import com.fanmes.common.security.RequirePermission;
import com.fanmes.system.service.SystemService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemController {
    private final SystemService systemService;

    @GetMapping("/menus/tree")
    public ApiResponse<List<SysMenu>> menus() {
        return ApiResponse.ok(systemService.menus());
    }

    @PostMapping("/sequences/next")
    public ApiResponse<String> nextCode(@RequestBody SequenceNextRequest request) {
        return ApiResponse.ok(systemService.nextCode(request));
    }

    @PostMapping("/operation-logs")
    public ApiResponse<Void> saveOperationLog(@RequestBody OperationLogCreateDTO dto) {
        systemService.saveOperationLog(dto);
        return ApiResponse.ok();
    }

    @GetMapping("/orgs/tree")
    @RequirePermission("system:user:view")
    public ApiResponse<List<MasterOrg>> orgTree() {
        return ApiResponse.ok(systemService.orgTree());
    }

    @GetMapping("/workshops")
    @RequirePermission("system:user:view")
    public ApiResponse<List<MasterWorkshop>> workshops(@RequestParam(required = false) Long orgId) {
        return ApiResponse.ok(systemService.workshops(orgId));
    }

    @GetMapping("/teams")
    @RequirePermission("system:user:view")
    public ApiResponse<List<MasterTeam>> teams(@RequestParam(required = false) Long workshopId) {
        return ApiResponse.ok(systemService.teams(workshopId));
    }
}
