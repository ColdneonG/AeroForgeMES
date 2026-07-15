package com.fanmes.system.controller;

import com.fanmes.common.api.Result;
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
    public Result<List<SysMenu>> menus() {
        return Result.success(systemService.menus());
    }

    @PostMapping("/sequences/next")
    public Result<String> nextCode(@RequestBody SequenceNextRequest request) {
        return Result.success(systemService.nextCode(request));
    }

    @PostMapping("/operation-logs")
    public Result<Void> saveOperationLog(@RequestBody OperationLogCreateDTO dto) {
        systemService.saveOperationLog(dto);
        return Result.success();
    }

    @GetMapping("/orgs/tree")
    @RequirePermission("system:user:view")
    public Result<List<MasterOrg>> orgTree() {
        return Result.success(systemService.orgTree());
    }

    @GetMapping("/workshops")
    @RequirePermission("system:user:view")
    public Result<List<MasterWorkshop>> workshops(@RequestParam(required = false) Long orgId) {
        return Result.success(systemService.workshops(orgId));
    }

    @GetMapping("/teams")
    @RequirePermission("system:user:view")
    public Result<List<MasterTeam>> teams(@RequestParam(required = false) Long workshopId) {
        return Result.success(systemService.teams(workshopId));
    }
}
