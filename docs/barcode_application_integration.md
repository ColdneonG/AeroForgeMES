# 条码应用模块整合说明

## 覆盖范围

本模块补齐了条码应用的规则维护、应用绑定、批量生成、外部码导入、标签预览、打印/补打审计、扫码登记、上下级包装绑定、关闭/作废、SN/工单/批次追溯。

前端入口：

- `/barcode/rules`：条码规则与应用绑定
- `/barcode/generate`：条码生成、导入、打印、包装绑定
- `/traceability`：扫码追溯与谱系

后端入口统一在网关下：

- `/api/production/barcodes/**`
- `/api/production/trace/{keyword}`

## 数据库处置

在已有 `mes_production` 库基础上执行：

```bash
cmd.exe /d /c "mysql --default-character-set=utf8mb4 -uroot -p123456 < database\mysql\mes_production\04_barcode_application.sql"
cmd.exe /d /c "mysql --default-character-set=utf8mb4 -uroot -p123456 < database\mysql\06_barcode_permissions.sql"
```

演示数据可选，适合本地联调和验收：

```bash
cmd.exe /d /c "mysql --default-character-set=utf8mb4 -uroot -p123456 < database\mysql\07_barcode_demo_seed.sql"
```

注意：必须带 `--default-character-set=utf8mb4`。之前本地库里出现过中文状态被导成 `??` 的情况，会导致 `启用`、`已打印` 等状态判断失效；`07_barcode_demo_seed.sql` 做了可重复 upsert，可用于修复演示数据的中文字段。

新增/调整的核心表：

- `bc_rule_sequence`：按规则和日期维护流水号，生成时原子递增。
- `bc_type`、`bc_rule`、`bc_template`、`bc_application_rule`：条码类型、编码规则、标签模板、产品绑定。
- `bc_barcode`、`bc_print_record`、`trace_event`：条码实例、打印审计、追溯事件。

跨库读取依赖：

- `mes_system.md_item`
- `mes_system.md_workstation`
- `mes_auth.sys_user`
- `mes_equipment.eqp_equipment`

如果生产服务使用独立数据库账号，需要给该账号授予这些表的 `SELECT` 权限，否则追溯详情、物料名称、工位、设备、操作人会查不到。

## 权限

已在 `database/mysql/06_barcode_permissions.sql` 补齐菜单/按钮权限。测试账号需要至少拥有：

- `barcode:rule:view/add/edit/delete`
- `barcode:application-rule:view/add/edit/delete`
- `barcode:generate:view`
- `barcode:generate`
- `barcode:print`
- `barcode:scan`
- `barcode:bind`
- `barcode:close`
- `barcode:void`
- `barcode:trace:view`

当前测试账号 `test_operator / 123456` 已具备这些权限。

## 运行方式

后端至少需要：

```bash
java -jar backend/mes-auth/target/mes-auth-0.1.0-SNAPSHOT.jar
java -jar backend/mes-production/target/mes-production-0.1.0-SNAPSHOT.jar
java -jar backend/mes-gateway/target/mes-gateway-0.1.0-SNAPSHOT.jar
```

前端：

```bash
cd frontend
npm run dev
```

本地访问：

- `http://127.0.0.1:5173/barcode/rules`
- `http://127.0.0.1:5173/barcode/generate`
- `http://127.0.0.1:5173/traceability`

## 集成边界

条码模块当前负责条码自身生命周期，不直接调用真实打印机。前端使用浏览器打印窗口输出标签；后续接入标签机时，可以在 `/api/production/barcodes/{id}/print` 或 `/api/production/barcodes/print` 后追加打印服务适配，但不要跳过 `bc_print_record`，否则补打审计会断。

追溯目前读取 `bc_barcode` 自身父子关系，并关联 `shop_report` / `shop_report_material` 里的关键物料关系。和报工模块整合时，只要报工写入 `trace_event`，并维护 `shop_report.barcode_id`、`shop_report_material.material_barcode_id`，条码追溯页会自动展示生命周期和关键物料。

## 验证

可复跑：

```bash
mvn -o "-Dmaven.repo.local=C:\Users\Supdouble\.m2\repository" -f backend/pom.xml -pl mes-production -am test
cd frontend
npm run build
D:\Anaconda\envs\test\python.exe ..\tests\barcode_visual_smoke.py
```

视觉冒烟会截图：

- `.codex-barcode-rules.png`
- `.codex-barcode-generate.png`
- `.codex-barcode-trace.png`
- `.codex-barcode-generate-dialog.png`
