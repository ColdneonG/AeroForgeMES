USE `mes_auth`;

-- 电子 SOP 权限及默认管理员角色授权。
-- Electronic SOP demo seed and permission patch.
-- Run after base schema/data. Safe to run repeatedly because INSERT IGNORE is used.

INSERT IGNORE INTO `mes_auth`.`sys_permission` (`id`, `permission_code`, `permission_name`, `permission_type`, `module_code`) VALUES
(1000901, 'process:sop:list', '电子 SOP 列表', 'BUTTON', 'process'),
(1000902, 'process:sop:query', '电子 SOP 查询', 'BUTTON', 'process'),
(1000903, 'process:sop:add', '电子 SOP 新增', 'BUTTON', 'process'),
(1000904, 'process:sop:edit', '电子 SOP 编辑', 'BUTTON', 'process'),
(1000905, 'process:sop:copyVersion', '电子 SOP 复制版本', 'BUTTON', 'process'),
(1000906, 'process:sop:submit', '电子 SOP 提交审核', 'BUTTON', 'process'),
(1000907, 'process:sop:review', '电子 SOP 审核', 'BUTTON', 'process'),
(1000908, 'process:sop:approve', '电子 SOP 批准', 'BUTTON', 'process'),
(1000909, 'process:sop:publish', '电子 SOP 发布', 'BUTTON', 'process'),
(1000910, 'process:sop:disable', '电子 SOP 停用', 'BUTTON', 'process'),
(1000911, 'process:sop:void', '电子 SOP 作废', 'BUTTON', 'process'),
(1000912, 'process:sop:binding', '电子 SOP 绑定', 'BUTTON', 'process'),
(1000913, 'process:sopModel:upload', '电子 SOP 三维模型上传', 'BUTTON', 'process'),
(1000914, 'production:sop:view', '生产 SOP 查看', 'BUTTON', 'shopfloor'),
(1000915, 'production:sop:execute', '生产 SOP 执行', 'BUTTON', 'shopfloor'),
(1000916, 'production:sop:confirm', '生产 SOP 步骤确认', 'BUTTON', 'shopfloor'),
(1000917, 'production:sop:executionList', '生产 SOP 执行记录', 'BUTTON', 'shopfloor');

INSERT IGNORE INTO `mes_auth`.`sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES
(1000901, 1000001, 1000901),
(1000902, 1000001, 1000902),
(1000903, 1000001, 1000903),
(1000904, 1000001, 1000904),
(1000905, 1000001, 1000905),
(1000906, 1000001, 1000906),
(1000907, 1000001, 1000907),
(1000908, 1000001, 1000908),
(1000909, 1000001, 1000909),
(1000910, 1000001, 1000910),
(1000911, 1000001, 1000911),
(1000912, 1000001, 1000912),
(1000913, 1000001, 1000913),
(1000914, 1000001, 1000914),
(1000915, 1000001, 1000915),
(1000916, 1000001, 1000916),
(1000917, 1000001, 1000917);

-- 条码应用权限及默认管理员角色授权。
INSERT IGNORE INTO `sys_permission`
(`id`, `permission_code`, `permission_name`, `permission_type`, `module_code`) VALUES
(1000951, 'barcode:rule:add', '条码规则新增', 'BUTTON', 'barcode'),
(1000952, 'barcode:rule:edit', '条码规则编辑和启停', 'BUTTON', 'barcode'),
(1000953, 'barcode:rule:delete', '条码规则删除', 'BUTTON', 'barcode'),
(1000954, 'barcode:application-rule:add', '条码应用规则新增', 'BUTTON', 'barcode'),
(1000955, 'barcode:application-rule:edit', '条码应用规则编辑', 'BUTTON', 'barcode'),
(1000956, 'barcode:application-rule:delete', '条码应用规则删除', 'BUTTON', 'barcode'),
(1000957, 'barcode:generate', '条码批量生成', 'BUTTON', 'barcode'),
(1000958, 'barcode:print', '条码打印与补打', 'BUTTON', 'barcode'),
(1000959, 'barcode:scan', '条码扫码登记', 'BUTTON', 'barcode'),
(1000960, 'barcode:bind', '条码包装关系绑定', 'BUTTON', 'barcode'),
(1000961, 'barcode:close', '条码关闭', 'BUTTON', 'barcode'),
(1000962, 'barcode:void', '条码作废', 'BUTTON', 'barcode');

INSERT IGNORE INTO `sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES
(1000951, 1000001, 1000951),
(1000952, 1000001, 1000952),
(1000953, 1000001, 1000953),
(1000954, 1000001, 1000954),
(1000955, 1000001, 1000955),
(1000956, 1000001, 1000956),
(1000957, 1000001, 1000957),
(1000958, 1000001, 1000958),
(1000959, 1000001, 1000959),
(1000960, 1000001, 1000960),
(1000961, 1000001, 1000961),
(1000962, 1000001, 1000962);
