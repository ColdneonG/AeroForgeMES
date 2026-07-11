# 电子 SOP 模块整合说明

## 我改了什么

- 后端：在 `mes-production` 新增电子 SOP 控制器、服务、仓储、领域对象和 DTO。
- 数据库：新增 SOP 主档、版本、步骤、附件、绑定、模型版本、任务快照、执行记录、步骤确认记录表。
- 前端：替换 `frontend/src/views/shopfloor/ElectronicSop.vue`，新增 `frontend/src/api/sop.js` 和 `frontend/src/components/sop3d/Sop3dViewer.vue`。
- 依赖：前端新增 `three`，用于 GLB 模型在线预览。

## 数据库怎么处理

团队整合建议使用独立脚本，不要手工从大 SQL 里摘：

1. 先应用基础库脚本。
2. 执行 `database/mysql/mes_production/03_electronic_sop.sql`。
3. 如需演示数据和测试账号权限，执行 `database/mysql/05_electronic_sop_seed.sql`。

我也把同样的表结构追加到了 `database/mysql/mes_production/02_tables.sql`，把演示数据追加到了 `database/mysql/04_minimal_integration_test_data.sql`。如果团队使用全量初始化脚本，走这两个文件即可；如果是已有库增量升级，走上面两个独立脚本。

## 文件存储

第一版使用本地文件存储适配层，不把 GLB/PDF/视频写入 MySQL。默认目录来自后端配置：

```yaml
fanmes:
  sop:
    storage-root: ${user.dir}/sop-files
    max-file-size: 104857600
```

生产整合时可以把 `storage-root` 改到共享磁盘；后续接 MinIO 时只替换 `SopFileStorageService`，表结构里的 `object_key`、`file_url`、`sha256` 可以继续复用。

## 关键接口

- `GET /api/production/sop/documents`
- `POST /api/production/sop/documents`
- `POST /api/production/sop/documents/{id}/versions`
- `POST /api/production/sop/versions/{id}/submit`
- `POST /api/production/sop/versions/{id}/approve`
- `POST /api/production/sop/versions/{id}/publish`
- `POST /api/production/sop/versions/{id}/attachments`
- `POST /api/production/sop/versions/{id}/models`
- `POST /api/production/sop/versions/{id}/bindings`
- `GET /api/production/sop/tasks/{taskId}`
- `POST /api/production/sop/snapshots/{snapshotId}/steps/{stepId}/confirm`
- `GET /api/production/sop/tasks/{taskId}/report-validation`

## 整合注意

- 已发布版本不可编辑，修改必须复制新版本。
- 任务开始/打开 SOP 时会生成 `sop_task_snapshot`，历史任务继续读锁定版本。
- 当前权限注解只是挂标识，项目现有网关主要做 token 检查；细粒度 API 鉴权如果后续补 AOP，可直接识别这些权限字符串。
- 前端电子 SOP 页面现在在原路由 `/shopfloor/sop`。
