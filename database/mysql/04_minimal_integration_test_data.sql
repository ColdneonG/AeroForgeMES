INSERT IGNORE INTO `mes_system`.`md_org` (`id`, `parent_id`, `org_code`, `org_name`, `org_type`, `status`) VALUES
(9000001, NULL, 'ORG-FAN-MES', '风扇制造事业部', '工厂', '启用');

INSERT IGNORE INTO `mes_system`.`md_workshop` (`id`, `workshop_code`, `workshop_name`, `org_id`, `status`) VALUES
(9100001, 'WS-ASSY', '总装车间', 9000001, '启用'),
(9100002, 'WS-QC', '质量车间', 9000001, '启用');

INSERT IGNORE INTO `mes_system`.`md_team` (`id`, `team_code`, `team_name`, `workshop_id`, `leader_id`, `status`) VALUES
(9200001, 'TEAM-A', '总装一班', 9100001, 1000001, '启用');

INSERT IGNORE INTO `mes_system`.`md_unit` (`id`, `unit_code`, `unit_name`, `precision_scale`, `status`) VALUES
(9300001, 'PCS', '件', 0, '启用'),
(9300002, 'SET', '套', 0, '启用');

INSERT IGNORE INTO `mes_system`.`md_item` (`id`, `item_code`, `item_name`, `item_type`, `spec_model`, `unit_id`, `status`) VALUES
(9400001, 'FG-FAN-500', 'FS-500 落地扇', '产品', '500mm', 9300001, '启用'),
(9400002, 'FG-FAN-230', 'TF-230 台扇', '产品', '230mm', 9300001, '启用'),
(9400003, 'MAT-MOTOR-65', '65W 电机', '物料', '65W', 9300001, '启用'),
(9400004, 'MAT-GUARD-500', '500mm 网罩套件', '物料', '500mm', 9300002, '启用');

INSERT IGNORE INTO `mes_system`.`md_production_line` (`id`, `line_code`, `line_name`, `workshop_id`, `capacity_per_hour`, `status`) VALUES
(9500001, 'LINE-ASSY-01', '总装一线', 9100001, 60.000000, '启用'),
(9500002, 'LINE-ASSY-02', '总装二线', 9100001, 45.000000, '启用');

INSERT IGNORE INTO `mes_system`.`md_workstation` (`id`, `station_code`, `station_name`, `line_id`, `process_id`, `status`) VALUES
(9600001, 'ST-ASSY-01', '装配工位一', 9500001, 9700001, '启用'),
(9600002, 'ST-QC-01', '检验工位一', 9500001, 9700002, '启用');

INSERT IGNORE INTO `mes_system`.`md_customer` (`id`, `customer_code`, `customer_name`, `status`) VALUES
(9800001, 'CUS-DEMO', '联调测试客户', '启用');

INSERT IGNORE INTO `mes_production`.`route_process` (`id`, `process_code`, `process_name`, `process_type`, `standard_time`, `status`) VALUES
(9700001, 'PROC-ASSY', '整机装配', '离散', 12.000000, '启用'),
(9700002, 'PROC-QC', '整机检验', '检验', 6.000000, '启用');

INSERT IGNORE INTO `mes_production`.`route_defect_reason` (`id`, `reason_code`, `reason_name`, `process_id`, `status`) VALUES
(9700101, 'DEF-LOOK', '外观划伤', 9700002, '启用'),
(9700102, 'DEF-NOISE', '运行异响', 9700002, '启用');

INSERT IGNORE INTO `mes_production`.`route_header` (`id`, `route_code`, `route_name`, `version_no`, `is_default`, `status`) VALUES
(9700201, 'ROUTE-FAN-ASSY', '风扇总装检验路线', 'V1.0', 1, '启用');

INSERT IGNORE INTO `mes_production`.`route_step` (`id`, `route_id`, `process_id`, `step_no`, `previous_step_id`, `next_step_id`, `workstation_id`, `sop_id`, `quality_required`) VALUES
(9700301, 9700201, 9700001, 10, NULL, 9700302, 9600001, NULL, 0),
(9700302, 9700201, 9700002, 20, 9700301, NULL, 9600002, NULL, 1);

INSERT IGNORE INTO `mes_production`.`route_product` (`id`, `product_id`, `route_id`, `is_default`, `effective_from`, `effective_to`, `status`) VALUES
(9700401, 9400001, 9700201, 1, '2026-07-01', NULL, '启用'),
(9700402, 9400002, 9700201, 1, '2026-07-01', NULL, '启用');

INSERT IGNORE INTO `mes_auth`.`sys_role` (`id`, `role_code`, `role_name`, `status`) VALUES
(1000001, 'MES_TEST_OPERATOR', '联调测试操作员', '启用');

INSERT IGNORE INTO `mes_auth`.`sys_user` (`id`, `username`, `password`, `display_name`, `mobile`, `employee_no`, `org_id`, `team_id`, `status`) VALUES
(1000001, 'test_operator', '123456', '联调测试员', '13800000001', 'EMP-T001', 9000001, 9200001, '启用');

INSERT IGNORE INTO `mes_auth`.`sys_user_role` (`id`, `user_id`, `role_id`) VALUES
(1000001, 1000001, 1000001);

INSERT IGNORE INTO `mes_auth`.`sys_permission` (`id`, `permission_code`, `permission_name`, `permission_type`, `module_code`) VALUES
(1000101, 'dashboard:view', '生产驾驶舱查看', 'MENU', 'dashboard'),
(1000102, 'production:work-order:view', '生产工单查看', 'MENU', 'production'),
(1000103, 'production:kitting:view', '齐套欠料查看', 'MENU', 'production'),
(1000104, 'production:dispatch:view', '派工单查看', 'MENU', 'production'),
(1000105, 'shopfloor:task:view', '生产任务单查看', 'MENU', 'shopfloor'),
(1000106, 'equipment:view', '设备监控查看', 'MENU', 'equipment'),
(1000107, 'equipment:ledger:view', '设备台账查看', 'MENU', 'equipment'),
(1000108, 'quality:view', '质量检验查看', 'MENU', 'quality'),
(1000109, 'quality:first-last:view', '首末件检验单查看', 'MENU', 'quality'),
(1000110, 'quality:patrol:view', '巡检单查看', 'MENU', 'quality'),
(1000111, 'quality:inbound:view', '成品入库检验单查看', 'MENU', 'quality'),
(1000112, 'quality:outbound:view', '成品发货检验单查看', 'MENU', 'quality');

INSERT IGNORE INTO `mes_auth`.`sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES
(1000201, 1000001, 1000101),
(1000202, 1000001, 1000102),
(1000203, 1000001, 1000103),
(1000204, 1000001, 1000104),
(1000205, 1000001, 1000105),
(1000206, 1000001, 1000106),
(1000207, 1000001, 1000107),
(1000208, 1000001, 1000108),
(1000209, 1000001, 1000109),
(1000210, 1000001, 1000110),
(1000211, 1000001, 1000111),
(1000212, 1000001, 1000112);

INSERT IGNORE INTO `mes_equipment`.`eqp_category` (`id`, `category_code`, `category_name`, `status`) VALUES
(2000001, 'EQC-ASSEMBLY', '装配设备', 'ENABLED'),
(2000002, 'EQC-TEST', '检测设备', 'ENABLED');

INSERT IGNORE INTO `mes_equipment`.`eqp_manufacturer` (`id`, `manufacturer_code`, `manufacturer_name`, `contact`, `status`) VALUES
(2000101, 'MFR-DEMO', '联调设备制造商', 'service@example.com', 'ENABLED');

INSERT IGNORE INTO `mes_equipment`.`eqp_equipment` (`id`, `equipment_code`, `equipment_name`, `category_id`, `manufacturer_id`, `line_id`, `station_id`, `model`, `serial_no`, `purchase_date`, `equipment_status`, `status`) VALUES
(2000201, 'EQ-ASSY-01', '自动锁螺丝机 1#', 2000001, 2000101, 9500001, 9600001, 'AS-500', 'SN-AS-202607-001', '2026-01-10', '正常', 'ENABLED'),
(2000202, 'EQ-ASSY-02', '自动锁螺丝机 2#', 2000001, 2000101, 9500002, 9600001, 'AS-500', 'SN-AS-202607-002', '2026-01-12', '待保养', 'ENABLED'),
(2000203, 'EQ-QC-01', '整机测试台 1#', 2000002, 2000101, 9500001, 9600002, 'QC-900', 'SN-QC-202607-001', '2026-02-08', '正常', 'ENABLED'),
(2000204, 'EQ-QC-02', '老化测试架 1#', 2000002, 2000101, 9500002, 9600002, 'AGING-48', 'SN-AG-202607-001', '2026-02-15', '正常', 'ENABLED');

INSERT IGNORE INTO `mes_quality`.`qc_item_category` (`id`, `category_code`, `category_name`, `status`) VALUES
(3000001, 'QCC-APPEARANCE', '外观检验', '启用'),
(3000002, 'QCC-PERFORMANCE', '性能检验', '启用');

INSERT IGNORE INTO `mes_quality`.`qc_item` (`id`, `category_id`, `item_code`, `item_name`, `value_type`, `standard_value`, `upper_limit`, `lower_limit`, `unit_id`, `status`) VALUES
(3000101, 3000001, 'QCI-SCRATCH', '外观划伤', '文本', '无明显划伤', NULL, NULL, NULL, '启用'),
(3000102, 3000002, 'QCI-NOISE', '运行噪声', '数值', NULL, 55.000000, 0.000000, NULL, '启用'),
(3000103, 3000002, 'QCI-SPEED', '风速档位', '数值', NULL, 3.000000, 1.000000, NULL, '启用');

INSERT IGNORE INTO `mes_quality`.`qc_plan` (`id`, `plan_code`, `plan_name`, `product_id`, `customer_id`, `is_default`, `status`) VALUES
(3000201, 'QCP-FAN-STD', '风扇整机通用检验方案', 9400001, 9800001, 1, '启用');

INSERT IGNORE INTO `mes_quality`.`qc_plan_item` (`id`, `plan_id`, `qc_item_id`, `sample_qty`, `standard_value`, `upper_limit`, `lower_limit`, `required_flag`) VALUES
(3000301, 3000201, 3000101, 1.000000, '无明显划伤', NULL, NULL, 1),
(3000302, 3000201, 3000102, 1.000000, NULL, 55.000000, 0.000000, 1),
(3000303, 3000201, 3000103, 1.000000, NULL, 3.000000, 1.000000, 1);

INSERT IGNORE INTO `mes_production`.`prod_work_order` (`id`, `work_order_no`, `external_no`, `source_type`, `product_id`, `plan_qty`, `completed_qty`, `qualified_qty`, `defective_qty`, `unit_id`, `planned_start_at`, `planned_end_at`, `delivery_date`, `line_id`, `route_id`, `status`) VALUES
(4000001, 'WO20260708001', 'ERP-WO-20260708-001', 'ERP', 9400001, 120.000000, 30.000000, 28.000000, 2.000000, 9300001, '2026-07-08 08:00:00', '2026-07-08 18:00:00', '2026-07-10', 9500001, 9700201, '生产中'),
(4000002, 'WO20260708002', 'ERP-WO-20260708-002', 'ERP', 9400002, 80.000000, 0.000000, 0.000000, 0.000000, 9300001, '2026-07-08 09:00:00', '2026-07-08 17:00:00', '2026-07-11', 9500002, 9700201, '已下发'),
(4000003, 'WO20260708003', 'API-WO-20260708-003', 'API', 9400001, 60.000000, 60.000000, 59.000000, 1.000000, 9300001, '2026-07-07 08:00:00', '2026-07-07 16:00:00', '2026-07-09', 9500001, 9700201, '已完成'),
(4000004, 'WO20260708004', 'MAN-WO-20260708-004', '手工', 9400002, 100.000000, 0.000000, 0.000000, 0.000000, 9300001, '2026-07-09 08:00:00', '2026-07-09 18:00:00', '2026-07-12', 9500002, 9700201, '待下发');

INSERT IGNORE INTO `mes_production`.`prod_dispatch_order` (`id`, `dispatch_no`, `work_order_id`, `line_id`, `station_id`, `team_id`, `plan_qty`, `planned_start_at`, `planned_end_at`, `status`) VALUES
(4100001, 'DISP20260708001', 4000001, 9500001, 9600001, 9200001, 60.000000, '2026-07-08 08:00:00', '2026-07-08 12:00:00', '生产中'),
(4100002, 'DISP20260708002', 4000002, 9500002, 9600001, 9200001, 80.000000, '2026-07-08 13:00:00', '2026-07-08 17:00:00', '已下发');

INSERT IGNORE INTO `mes_production`.`shop_task` (`id`, `task_no`, `work_order_id`, `dispatch_id`, `product_id`, `route_id`, `line_id`, `team_id`, `plan_qty`, `started_at`, `ended_at`, `status`) VALUES
(4200001, 'TASK20260708001', 4000001, 4100001, 9400001, 9700201, 9500001, 9200001, 60.000000, '2026-07-08 08:10:00', NULL, '生产中'),
(4200002, 'TASK20260708002', 4000002, 4100002, 9400002, 9700201, 9500002, 9200001, 80.000000, NULL, NULL, '已下发');

INSERT IGNORE INTO `mes_production`.`shop_operation_task` (`id`, `operation_task_no`, `task_id`, `route_step_id`, `process_id`, `station_id`, `device_id`, `operator_id`, `plan_qty`, `reported_qty`, `status`) VALUES
(4300001, 'OPT20260708001', 4200001, 9700301, 9700001, 9600001, 2000201, 1000001, 60.000000, 30.000000, '生产中'),
(4300002, 'OPT20260708002', 4200001, 9700302, 9700002, 9600002, 2000203, 1000001, 60.000000, 28.000000, '生产中');

INSERT IGNORE INTO `mes_production`.`prod_kitting_analysis` (`id`, `analysis_no`, `work_order_id`, `task_id`, `analysis_time`, `kitting_status`, `missing_count`, `status`) VALUES
(4400001, 'KIT20260708001', 4000001, 4200001, '2026-07-08 07:40:00', '齐套', 0, '已完成'),
(4400002, 'KIT20260708002', 4000002, 4200002, '2026-07-08 08:30:00', '缺料', 1, '已完成');

INSERT IGNORE INTO `mes_production`.`prod_kitting_missing_item` (`id`, `analysis_id`, `material_id`, `required_qty`, `available_qty`, `missing_qty`, `expected_arrival_at`) VALUES
(4400101, 4400001, 9400003, 60.000000, 80.000000, 0.000000, NULL),
(4400102, 4400002, 9400004, 80.000000, 50.000000, 30.000000, '2026-07-08 14:00:00');

INSERT IGNORE INTO `mes_quality`.`qc_inspection_order` (`id`, `inspection_no`, `inspection_type`, `plan_id`, `work_order_id`, `task_id`, `operation_task_id`, `product_id`, `barcode_id`, `inspector_id`, `inspection_at`, `final_result`, `status`) VALUES
(5000001, 'QC20260708001', '首件', 3000201, 4000001, 4200001, 4300001, 9400001, NULL, 1000001, '2026-07-08 08:30:00', '合格', '合格'),
(5000002, 'QC20260708002', '巡检', 3000201, 4000001, 4200001, 4300002, 9400001, NULL, 1000001, '2026-07-08 10:00:00', '不合格', '不合格'),
(5000003, 'QC20260708003', '成品入库', 3000201, 4000003, NULL, NULL, 9400001, NULL, 1000001, '2026-07-07 16:20:00', '合格', '合格'),
(5000004, 'QC20260708004', '末件', 3000201, 4000002, 4200002, NULL, 9400002, NULL, 1000001, NULL, NULL, '待检');

INSERT IGNORE INTO `mes_quality`.`qc_inspection_result` (`id`, `inspection_id`, `qc_item_id`, `measured_value`, `result`, `defect_reason_id`, `remark`) VALUES
(5000101, 5000001, 3000101, '无明显划伤', '合格', NULL, '首件外观正常'),
(5000102, 5000001, 3000102, '48', '合格', NULL, '噪声正常'),
(5000103, 5000002, 3000101, '网罩轻微划伤', '不合格', 9700101, '巡检发现外观异常');

INSERT IGNORE INTO `mes_quality`.`qc_defect_record` (`id`, `source_type`, `source_id`, `product_id`, `barcode_id`, `process_id`, `defect_reason_id`, `defect_qty`, `handle_method`, `rework_order_id`, `status`) VALUES
(5000201, '检验', 5000002, 9400001, NULL, 9700002, 9700101, 2.000000, '返修', NULL, '待处理');

INSERT IGNORE INTO `mes_auth`.`sys_data_scope` (`id`, `role_id`, `scope_type`, `scope_value`) VALUES
(1000401, 1000001, '全部工厂', 'ALL'),
(1000402, 1000001, '车间', '9100001');

INSERT IGNORE INTO `mes_auth`.`sys_permission` (`id`, `permission_code`, `permission_name`, `permission_type`, `module_code`) VALUES
(1000121, 'production:view', '生产订单模块查看', 'MENU', 'production'),
(1000122, 'barcode:view', '条码应用模块查看', 'MENU', 'barcode'),
(1000123, 'barcode:type:view', '条码类型查看', 'MENU', 'barcode'),
(1000124, 'barcode:rule:view', '条码规则查看', 'MENU', 'barcode'),
(1000125, 'barcode:template:view', '条码模板查看', 'MENU', 'barcode'),
(1000126, 'barcode:application-rule:view', '条码应用规则查看', 'MENU', 'barcode'),
(1000127, 'barcode:generate:view', '条码生成查看', 'MENU', 'barcode'),
(1000128, 'barcode:trace:view', '扫码追溯查看', 'MENU', 'barcode'),
(1000129, 'shopfloor:view', '现场管理模块查看', 'MENU', 'shopfloor'),
(1000130, 'shopfloor:config:view', '现场参数配置查看', 'MENU', 'shopfloor'),
(1000131, 'shopfloor:dispatch:view', '现场派工查看', 'MENU', 'shopfloor'),
(1000132, 'shopfloor:product-status:view', '产品生产状态查看', 'MENU', 'shopfloor'),
(1000133, 'shopfloor:report:view', '生产报工查看', 'MENU', 'shopfloor'),
(1000134, 'shopfloor:completion:view', '生产完工单查看', 'MENU', 'shopfloor'),
(1000135, 'shopfloor:repair-order:view', '返修工单查看', 'MENU', 'shopfloor'),
(1000136, 'shopfloor:tablet-task:view', '平板生产任务单查看', 'MENU', 'shopfloor'),
(1000137, 'shopfloor:operation:view', '工序作业台查看', 'MENU', 'shopfloor'),
(1000138, 'shopfloor:sop:view', '电子 SOP 查看', 'MENU', 'shopfloor'),
(1000139, 'shopfloor:tablet-trace:view', '平板产品追溯查看', 'MENU', 'shopfloor'),
(1000140, 'equipment:category:view', '设备类别查看', 'MENU', 'equipment'),
(1000141, 'equipment:maker:view', '设备制造商查看', 'MENU', 'equipment'),
(1000142, 'equipment:fault-reason:view', '故障原因查看', 'MENU', 'equipment'),
(1000143, 'equipment:inspection:view', '设备点检查看', 'MENU', 'equipment'),
(1000144, 'equipment:maintenance-task:view', '保养任务查看', 'MENU', 'equipment'),
(1000145, 'equipment:maintenance:view', '点检保养维修查看', 'MENU', 'equipment'),
(1000146, 'equipment:repair:view', '报修任务查看', 'MENU', 'equipment'),
(1000147, 'equipment:mobile-status:view', '移动设备状态查看', 'MENU', 'equipment'),
(1000148, 'equipment:oee:view', '设备 OEE 查看', 'MENU', 'equipment'),
(1000149, 'equipment:energy:view', '能源分析查看', 'MENU', 'equipment'),
(1000150, 'equipment-integration:view', '设备对接查看', 'MENU', 'equipment-integration'),
(1000151, 'wage:view', '计件工资查看', 'MENU', 'wage'),
(1000152, 'process:view', '工艺管理查看', 'MENU', 'process'),
(1000153, 'quality:item-category:view', '检验项目分类查看', 'MENU', 'quality'),
(1000154, 'quality:standard-plan:view', '检验标准方案查看', 'MENU', 'quality'),
(1000155, 'andon:view', '安灯管理查看', 'MENU', 'andon'),
(1000156, 'andon:type:view', '安灯类型查看', 'MENU', 'andon'),
(1000157, 'andon:config:view', '异常配置查看', 'MENU', 'andon'),
(1000158, 'andon:reason:view', '异常原因查看', 'MENU', 'andon'),
(1000159, 'andon:exception:view', '现场生产异常查看', 'MENU', 'andon'),
(1000160, 'andon:tablet:view', '平板异常安灯查看', 'MENU', 'andon'),
(1000161, 'andon:mobile-launch:view', '移动异常发起查看', 'MENU', 'andon'),
(1000162, 'andon:mobile-handle:view', '移动异常处理查看', 'MENU', 'andon'),
(1000163, 'miniapp:dashboard:view', '小程序实时看板查看', 'MENU', 'miniapp'),
(1000164, 'miniapp:analysis:view', '小程序生产分析查看', 'MENU', 'miniapp'),
(1000165, 'miniapp:trace:view', '小程序产品追溯查看', 'MENU', 'miniapp'),
(1000166, 'report:view', '报表分析查看', 'MENU', 'report'),
(1000167, 'report:material-trace:view', '关键物料追溯查看', 'MENU', 'report'),
(1000168, 'report:product-trace:view', '产品追溯报表查看', 'MENU', 'report'),
(1000169, 'report:output:view', '产量报表查看', 'MENU', 'report'),
(1000170, 'report:realtime:view', '生产实时信息查看', 'MENU', 'report'),
(1000171, 'report:defects:view', '不良查询查看', 'MENU', 'report'),
(1000172, 'report:workshop-period:view', '车间生产时段报表查看', 'MENU', 'report'),
(1000173, 'board:line:view', '产线看板查看', 'MENU', 'board'),
(1000174, 'board:workshop:view', '车间看板查看', 'MENU', 'board'),
(1000175, 'board:control-center:view', '中控看板查看', 'MENU', 'board'),
(1000176, 'erp:view', 'ERP 接口模块查看', 'MENU', 'erp'),
(1000177, 'erp:tasks-read:view', 'ERP 生产任务读取查看', 'MENU', 'erp'),
(1000178, 'erp:routes-read:view', 'ERP 工艺读取查看', 'MENU', 'erp'),
(1000179, 'standard-api:view', '标准 API 模块查看', 'MENU', 'standard-api'),
(1000180, 'standard-api:units-write:view', '计量单位写入查看', 'MENU', 'standard-api'),
(1000181, 'standard-api:work-orders-write:view', '工单写入查看', 'MENU', 'standard-api'),
(1000182, 'standard-api:tasks-write:view', '任务单写入查看', 'MENU', 'standard-api'),
(1000183, 'standard-api:device-count-write:view', '设备计数报工写入查看', 'MENU', 'standard-api'),
(1000184, 'standard-api:completions-read:view', '完工单读取查看', 'MENU', 'standard-api'),
(1000185, 'system:view', '系统管理查看', 'MENU', 'system'),
(1000186, '/api/auth/**', '认证接口访问', 'API', 'auth'),
(1000187, '/api/system/**', '系统接口访问', 'API', 'system'),
(1000188, '/api/production/**', '生产接口访问', 'API', 'production'),
(1000189, '/api/quality/**', '质量接口访问', 'API', 'quality'),
(1000190, '/api/equipment/**', '设备接口访问', 'API', 'equipment'),
(1000191, '/api/report/**', '报表接口访问', 'API', 'report'),
(1000192, '/api/integration/**', '集成接口访问', 'API', 'integration');

INSERT IGNORE INTO `mes_auth`.`sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES
(1000301, 1000001, 1000121),
(1000302, 1000001, 1000122),
(1000303, 1000001, 1000123),
(1000304, 1000001, 1000124),
(1000305, 1000001, 1000125),
(1000306, 1000001, 1000126),
(1000307, 1000001, 1000127),
(1000308, 1000001, 1000128),
(1000309, 1000001, 1000129),
(1000310, 1000001, 1000130),
(1000311, 1000001, 1000131),
(1000312, 1000001, 1000132),
(1000313, 1000001, 1000133),
(1000314, 1000001, 1000134),
(1000315, 1000001, 1000135),
(1000316, 1000001, 1000136),
(1000317, 1000001, 1000137),
(1000318, 1000001, 1000138),
(1000319, 1000001, 1000139),
(1000320, 1000001, 1000140),
(1000321, 1000001, 1000141),
(1000322, 1000001, 1000142),
(1000323, 1000001, 1000143),
(1000324, 1000001, 1000144),
(1000325, 1000001, 1000145),
(1000326, 1000001, 1000146),
(1000327, 1000001, 1000147),
(1000328, 1000001, 1000148),
(1000329, 1000001, 1000149),
(1000330, 1000001, 1000150),
(1000331, 1000001, 1000151),
(1000332, 1000001, 1000152),
(1000333, 1000001, 1000153),
(1000334, 1000001, 1000154),
(1000335, 1000001, 1000155),
(1000336, 1000001, 1000156),
(1000337, 1000001, 1000157),
(1000338, 1000001, 1000158),
(1000339, 1000001, 1000159),
(1000340, 1000001, 1000160),
(1000341, 1000001, 1000161),
(1000342, 1000001, 1000162),
(1000343, 1000001, 1000163),
(1000344, 1000001, 1000164),
(1000345, 1000001, 1000165),
(1000346, 1000001, 1000166),
(1000347, 1000001, 1000167),
(1000348, 1000001, 1000168),
(1000349, 1000001, 1000169),
(1000350, 1000001, 1000170),
(1000351, 1000001, 1000171),
(1000352, 1000001, 1000172),
(1000353, 1000001, 1000173),
(1000354, 1000001, 1000174),
(1000355, 1000001, 1000175),
(1000356, 1000001, 1000176),
(1000357, 1000001, 1000177),
(1000358, 1000001, 1000178),
(1000359, 1000001, 1000179),
(1000360, 1000001, 1000180),
(1000361, 1000001, 1000181),
(1000362, 1000001, 1000182),
(1000363, 1000001, 1000183),
(1000364, 1000001, 1000184),
(1000365, 1000001, 1000185),
(1000366, 1000001, 1000186),
(1000367, 1000001, 1000187),
(1000368, 1000001, 1000188),
(1000369, 1000001, 1000189),
(1000370, 1000001, 1000190),
(1000371, 1000001, 1000191),
(1000372, 1000001, 1000192);

INSERT IGNORE INTO `mes_system`.`sys_menu` (`id`, `parent_id`, `menu_code`, `menu_name`, `module_code`, `path`, `sort_no`) VALUES
(6000001, NULL, 'MENU-PRODUCTION', '生产订单', 'production', '/production/orders', 10),
(6000002, NULL, 'MENU-BARCODE', '条码应用', 'barcode', '/barcode/rules', 20),
(6000003, NULL, 'MENU-SHOPFLOOR', '现场管理', 'shopfloor', '/shopfloor/tasks', 30),
(6000004, NULL, 'MENU-EQUIPMENT', '设备管理', 'equipment', '/equipment/ledger', 40),
(6000005, NULL, 'MENU-QUALITY', '质量管理', 'quality', '/quality/inspection', 50),
(6000006, NULL, 'MENU-ANDON', '安灯管理', 'andon', '/andon', 60),
(6000007, NULL, 'MENU-REPORT', '报表分析', 'report', '/report', 70),
(6000008, NULL, 'MENU-BOARD', '电子看板', 'board', '/boards/control-center', 80),
(6000009, NULL, 'MENU-INTEGRATION', '接口集成', 'integration', '/integration', 90),
(6000010, NULL, 'MENU-SYSTEM', '系统管理', 'system', '/system', 100);

INSERT IGNORE INTO `mes_system`.`sys_sequence_rule` (`id`, `rule_code`, `biz_type`, `prefix`, `date_pattern`, `serial_length`, `reset_cycle`, `status`) VALUES
(6000101, 'SEQ-WO', 'WORK_ORDER', 'WO', 'yyyyMMdd', 4, 'DAY', '启用'),
(6000102, 'SEQ-DISP', 'DISPATCH_ORDER', 'DISP', 'yyyyMMdd', 4, 'DAY', '启用'),
(6000103, 'SEQ-QC', 'INSPECTION_ORDER', 'QC', 'yyyyMMdd', 4, 'DAY', '启用'),
(6000104, 'SEQ-ANDON', 'ANDON_EXCEPTION', 'ANDON', 'yyyyMMdd', 4, 'DAY', '启用');

INSERT IGNORE INTO `mes_system`.`sys_sequence_value` (`id`, `rule_id`, `period_key`, `current_value`) VALUES
(6000201, 6000101, '20260708', 4),
(6000202, 6000102, '20260708', 2),
(6000203, 6000103, '20260708', 4),
(6000204, 6000104, '20260708', 1);

INSERT IGNORE INTO `mes_system`.`md_supplier` (`id`, `supplier_code`, `supplier_name`, `status`) VALUES
(9800101, 'SUP-MOTOR', '联调电机供应商', '启用'),
(9800102, 'SUP-PACK', '联调包装供应商', '启用');

INSERT IGNORE INTO `mes_system`.`md_bom` (`id`, `bom_code`, `product_id`, `version_no`, `status`) VALUES
(9800201, 'BOM-FAN-500-V1', 9400001, 'V1.0', '启用'),
(9800202, 'BOM-FAN-230-V1', 9400002, 'V1.0', '启用');

INSERT IGNORE INTO `mes_system`.`md_bom_item` (`id`, `bom_id`, `material_id`, `qty`, `loss_rate`) VALUES
(9800301, 9800201, 9400003, 1.000000, 0.010000),
(9800302, 9800201, 9400004, 1.000000, 0.020000),
(9800303, 9800202, 9400003, 1.000000, 0.010000),
(9800304, 9800202, 9400004, 1.000000, 0.020000);

INSERT IGNORE INTO `mes_system`.`sys_status_log` (`id`, `biz_type`, `biz_id`, `biz_code`, `from_status`, `to_status`, `action`, `operator_id`, `operated_at`, `remark`) VALUES
(6000401, 'WORK_ORDER', 4000001, 'WO20260708001', '已下发', '生产中', 'start', 1000001, '2026-07-08 08:10:00', '联调开工'),
(6000402, 'QC_INSPECTION', 5000002, 'QC20260708002', '检验中', '不合格', 'confirm', 1000001, '2026-07-08 10:30:00', '联调不良确认'),
(6000403, 'ANDON_EXCEPTION', 7600001, 'ANDON20260708001', '待处理', '处理中', 'handle', 1000001, '2026-07-08 10:45:00', '联调异常处理');

INSERT IGNORE INTO `mes_system`.`sys_operation_log` (`id`, `biz_type`, `biz_id`, `action`, `request_body`, `before_data`, `after_data`, `operator_id`, `operated_at`, `result`, `error_message`) VALUES
(6000501, 'WORK_ORDER', 4000001, 'start', '{"status":"生产中"}', '{"status":"已下发"}', '{"status":"生产中"}', 1000001, '2026-07-08 08:10:00', '成功', NULL),
(6000502, 'INTEGRATION', 7000102, 'retry', '{"syncLogId":7000102}', '{"sync_status":"FAILED"}', '{"sync_status":"PENDING"}', 1000001, '2026-07-08 11:00:00', '成功', NULL);

INSERT IGNORE INTO `mes_production`.`sop_file` (`id`, `sop_code`, `sop_name`, `process_id`, `file_type`, `file_url`, `display_order`, `auto_play_flag`, `status`) VALUES
(9700501, 'SOP-ASSY-001', '风扇整机装配 SOP', 9700001, '电子文件', '/sop/fan-assembly.pdf', 1, 1, '启用'),
(9700502, 'SOP-QC-001', '风扇整机检验 SOP', 9700002, '图片', '/sop/fan-qc.png', 2, 0, '启用');

INSERT IGNORE INTO `mes_production`.`bc_type` (`id`, `type_code`, `type_name`, `unique_scope`, `status`) VALUES
(7100001, 'PRODUCT_SN', '产品唯一码', '全局唯一', '启用'),
(7100002, 'MATERIAL_BATCH', '材料批次码', '批次唯一', '启用'),
(7100003, 'CARTON_CODE', '外箱码', '全局唯一', '启用');

INSERT IGNORE INTO `mes_production`.`bc_rule` (`id`, `rule_code`, `rule_name`, `type_id`, `rule_expression`, `serial_length`, `status`) VALUES
(7100101, 'BCR-PRODUCT-SN', '产品 SN 规则', 7100001, 'SN-${yyyyMMdd}-${####}', 4, '启用'),
(7100102, 'BCR-MATERIAL-BATCH', '材料批次规则', 7100002, 'MAT-${itemCode}-${yyyyMMdd}', 3, '启用'),
(7100103, 'BCR-CARTON', '外箱码规则', 7100003, 'CTN-${yyyyMMdd}-${####}', 4, '启用');

INSERT IGNORE INTO `mes_production`.`bc_template` (`id`, `template_code`, `template_name`, `type_id`, `template_content`, `paper_width`, `paper_height`, `status`) VALUES
(7100201, 'BCT-PRODUCT', '产品标签模板', 7100001, '{"fields":["barcode","item","workOrder"]}', 60.00, 40.00, '启用'),
(7100202, 'BCT-MATERIAL', '物料标签模板', 7100002, '{"fields":["batch","material","supplier"]}', 80.00, 50.00, '启用'),
(7100203, 'BCT-CARTON', '外箱标签模板', 7100003, '{"fields":["carton","qty","product"]}', 100.00, 70.00, '启用');

INSERT IGNORE INTO `mes_production`.`bc_application_rule` (`id`, `app_rule_code`, `item_id`, `type_id`, `rule_id`, `template_id`, `barcode_mode`, `source_type`, `status`) VALUES
(7100301, 'APP-FAN500-SN', 9400001, 7100001, 7100101, 7100201, '唯一码', '规则生成', '启用'),
(7100302, 'APP-MOTOR-BATCH', 9400003, 7100002, 7100102, 7100202, '批次码', '外部导入', '启用'),
(7100303, 'APP-FAN-CARTON', 9400001, 7100003, 7100103, 7100203, '唯一码', '规则生成', '启用');

INSERT IGNORE INTO `mes_production`.`bc_barcode` (`id`, `barcode_value`, `type_id`, `app_rule_id`, `item_id`, `batch_no`, `work_order_id`, `task_id`, `parent_barcode_id`, `print_count`, `source_type`, `status`) VALUES
(7100401, 'SN202607080001', 7100001, 7100301, 9400001, 'BATCH-FAN-0708A', 4000001, 4200001, NULL, 1, '生成', '已打印'),
(7100402, 'SN202607080002', 7100001, 7100301, 9400001, 'BATCH-FAN-0708A', 4000001, 4200001, NULL, 1, '生成', '已扫码'),
(7100403, 'MAT-MOTOR-20260708-A', 7100002, 7100302, 9400003, 'MOTOR-0708A', 4000001, 4200001, NULL, 0, '外部导入', '已生成'),
(7100404, 'CTN202607080001', 7100003, 7100303, 9400001, 'BATCH-FAN-0708A', 4000001, 4200001, NULL, 1, '生成', '已打印');

INSERT IGNORE INTO `mes_production`.`bc_print_record` (`id`, `barcode_id`, `template_id`, `print_by`, `print_at`, `print_count`, `printer_name`) VALUES
(7100501, 7100401, 7100201, 1000001, '2026-07-08 08:05:00', 1, 'LABEL-01'),
(7100502, 7100404, 7100203, 1000001, '2026-07-08 11:30:00', 1, 'LABEL-02');

INSERT IGNORE INTO `mes_production`.`shop_product_status` (`id`, `barcode_id`, `task_id`, `operation_task_id`, `current_process_id`, `current_station_id`, `production_status`, `last_report_id`, `updated_at`) VALUES
(7200001, 7100401, 4200001, 4300001, 9700001, 9600001, '生产中', 7200101, '2026-07-08 09:00:00'),
(7200002, 7100402, 4200001, 4300002, 9700002, 9600002, '已完工', 7200102, '2026-07-08 10:20:00');

INSERT IGNORE INTO `mes_production`.`shop_report` (`id`, `report_no`, `report_type`, `task_id`, `operation_task_id`, `product_id`, `barcode_id`, `process_id`, `station_id`, `device_id`, `operator_id`, `report_qty`, `qualified_qty`, `defective_qty`, `report_at`, `status`) VALUES
(7200101, 'RPT20260708001', '普通', 4200001, 4300001, 9400001, 7100401, 9700001, 9600001, 2000201, 1000001, 30.000000, 30.000000, 0.000000, '2026-07-08 09:00:00', '已完成'),
(7200102, 'RPT20260708002', '检测', 4200001, 4300002, 9400001, 7100402, 9700002, 9600002, 2000203, 1000001, 30.000000, 28.000000, 2.000000, '2026-07-08 10:20:00', '已完成'),
(7200103, 'RPT20260708003', '设备计数', 4200001, 4300001, 9400001, NULL, 9700001, 9600001, 2000201, 1000001, 18.000000, 18.000000, 0.000000, '2026-07-08 11:00:00', '已完成');

INSERT IGNORE INTO `mes_production`.`shop_report_material` (`id`, `report_id`, `material_id`, `material_barcode_id`, `qty`) VALUES
(7200201, 7200101, 9400003, 7100403, 30.000000),
(7200202, 7200101, 9400004, NULL, 30.000000);

INSERT IGNORE INTO `mes_production`.`shop_completion_order` (`id`, `completion_no`, `work_order_id`, `task_id`, `product_id`, `completed_qty`, `qualified_qty`, `defective_qty`, `completed_at`, `confirmed_by`, `status`) VALUES
(7200301, 'COMP20260708001', 4000003, NULL, 9400001, 60.000000, 59.000000, 1.000000, '2026-07-07 16:30:00', 1000001, '已完成'),
(7200302, 'COMP20260708002', 4000001, 4200001, 9400001, 30.000000, 28.000000, 2.000000, '2026-07-08 10:30:00', 1000001, '已完成');

INSERT IGNORE INTO `mes_production`.`shop_rework_order` (`id`, `rework_no`, `source_type`, `source_id`, `barcode_id`, `product_id`, `route_id`, `reason_id`, `status`) VALUES
(7200401, 'RW20260708001', '质量检验', 5000201, 7100402, 9400001, 9700201, 9700101, '待处理');

INSERT IGNORE INTO `mes_production`.`trace_event` (`id`, `barcode_id`, `barcode_value`, `event_type`, `biz_type`, `biz_id`, `work_order_id`, `task_id`, `process_id`, `station_id`, `device_id`, `operator_id`, `event_at`) VALUES
(7300001, 7100401, 'SN202607080001', '生成', 'BARCODE', 7100401, 4000001, 4200001, NULL, NULL, NULL, 1000001, '2026-07-08 08:02:00'),
(7300002, 7100401, 'SN202607080001', '扫码', 'SHOP_OPERATION', 4300001, 4000001, 4200001, 9700001, 9600001, 2000201, 1000001, '2026-07-08 08:12:00'),
(7300003, 7100402, 'SN202607080002', '质检', 'QC_INSPECTION', 5000002, 4000001, 4200001, 9700002, 9600002, 2000203, 1000001, '2026-07-08 10:00:00'),
(7300004, 7100403, 'MAT-MOTOR-20260708-A', '关键物料', 'MATERIAL_REPORT', 7200201, 4000001, 4200001, 9700001, 9600001, 2000201, 1000001, '2026-07-08 09:05:00');

INSERT IGNORE INTO `mes_production`.`andon_type` (`id`, `type_code`, `type_name`, `response_minutes`, `status`) VALUES
(7600001, 'ANDON-EQP', '设备异常', 15, '启用'),
(7600002, 'ANDON-MATERIAL', '物料异常', 20, '启用'),
(7600003, 'ANDON-QUALITY', '质量异常', 10, '启用');

INSERT IGNORE INTO `mes_production`.`andon_config` (`id`, `type_id`, `workshop_id`, `line_id`, `notify_role_id`, `escalation_minutes`, `status`) VALUES
(7600101, 7600001, 9100001, 9500001, 1000001, 30, '启用'),
(7600102, 7600003, 9100002, 9500001, 1000001, 20, '启用');

INSERT IGNORE INTO `mes_production`.`andon_reason` (`id`, `reason_code`, `reason_name`, `type_id`, `status`) VALUES
(7600201, 'ANDON-EQP-STOP', '设备停机', 7600001, '启用'),
(7600202, 'ANDON-MAT-LACK', '关键物料短缺', 7600002, '启用'),
(7600203, 'ANDON-QC-NG', '批量质量异常', 7600003, '启用');

INSERT IGNORE INTO `mes_production`.`andon_exception_order` (`id`, `exception_no`, `type_id`, `reason_id`, `work_order_id`, `task_id`, `operation_task_id`, `line_id`, `station_id`, `equipment_id`, `reported_by`, `reported_at`, `assigned_to`, `closed_by`, `closed_at`, `status`) VALUES
(7600301, 'ANDON20260708001', 7600001, 7600201, 4000001, 4200001, 4300001, 9500001, 9600001, 2000201, 1000001, '2026-07-08 10:35:00', 1000001, NULL, NULL, '处理中'),
(7600302, 'ANDON20260708002', 7600002, 7600202, 4000002, 4200002, NULL, 9500002, 9600001, NULL, 1000001, '2026-07-08 08:45:00', 1000001, 1000001, '2026-07-08 09:30:00', '已关闭');

INSERT IGNORE INTO `mes_production`.`andon_notify_record` (`id`, `exception_id`, `notify_user_id`, `notify_channel`, `notify_at`, `read_at`, `result`) VALUES
(7600401, 7600301, 1000001, 'WEB', '2026-07-08 10:35:10', '2026-07-08 10:36:00', '已读'),
(7600402, 7600302, 1000001, 'MOBILE', '2026-07-08 08:45:10', '2026-07-08 08:46:00', '已读');

INSERT IGNORE INTO `mes_production`.`andon_handle_record` (`id`, `exception_id`, `handler_id`, `handle_action`, `handle_content`, `handled_at`, `result`) VALUES
(7600501, 7600301, 1000001, '接单', '设备维护人员已接单', '2026-07-08 10:45:00', '处理中'),
(7600502, 7600302, 1000001, '关闭', '物料已补齐并关闭', '2026-07-08 09:30:00', '已关闭');

INSERT IGNORE INTO `mes_production`.`wage_piece_rate` (`id`, `product_id`, `process_id`, `rate`, `effective_from`, `effective_to`, `status`) VALUES
(7700001, 9400001, 9700001, 1.200000, '2026-07-01', NULL, '启用'),
(7700002, 9400001, 9700002, 0.800000, '2026-07-01', NULL, '启用');

INSERT IGNORE INTO `mes_production`.`wage_piece_detail` (`id`, `report_id`, `operator_id`, `process_id`, `qualified_qty`, `rate`, `amount`, `calc_at`, `status`) VALUES
(7700101, 7200101, 1000001, 9700001, 30.000000, 1.200000, 36.000000, '2026-07-08 12:00:00', '已确认'),
(7700102, 7200102, 1000001, 9700002, 28.000000, 0.800000, 22.400000, '2026-07-08 12:00:00', '待审核');

INSERT IGNORE INTO `mes_production`.`wage_piece_summary` (`id`, `operator_id`, `period_key`, `total_qty`, `total_amount`, `confirmed_by`, `confirmed_at`, `status`) VALUES
(7700201, 1000001, '2026-07', 58.000000, 58.400000, 1000001, '2026-07-08 12:10:00', '已确认');

INSERT IGNORE INTO `mes_production`.`mes_idempotency_record` (`module_name`, `action_name`, `idempotency_key`, `business_id`, `created_at`) VALUES
('production', 'createWorkOrder', 'idem-wo-20260708001', '4000001', '2026-07-08 08:00:00'),
('integration', 'deviceCountReport', 'idem-device-count-20260708001', '7200103', '2026-07-08 11:00:00');

INSERT IGNORE INTO `mes_production`.`mes_operation_log` (`id`, `module_name`, `target_table`, `target_id`, `action_name`, `old_status`, `new_status`, `operator_id`, `remark`, `operated_at`) VALUES
(7800001, 'production', 'prod_work_order', 4000001, 'start', '已下发', '生产中', 1000001, '联调生产开工', '2026-07-08 08:10:00'),
(7800002, 'andon', 'andon_exception_order', 7600301, 'handle', '待处理', '处理中', 1000001, '联调安灯接单', '2026-07-08 10:45:00');

INSERT IGNORE INTO `mes_equipment`.`eqp_fault_reason` (`id`, `reason_code`, `reason_name`, `category_id`, `status`) VALUES
(2000301, 'EQP-JAM', '机构卡滞', 2000001, 'ENABLED'),
(2000302, 'EQP-SENSOR', '传感器异常', 2000002, 'ENABLED');

INSERT IGNORE INTO `mes_equipment`.`eqp_maintenance_task` (`id`, `maintenance_no`, `equipment_id`, `plan_at`, `assigned_to`, `completed_at`, `result`, `status`) VALUES
(2000401, 'MNT20260708001', 2000202, '2026-07-08 14:00:00', 1000001, NULL, NULL, 'PENDING'),
(2000402, 'MNT20260707001', 2000201, '2026-07-07 16:00:00', 1000001, '2026-07-07 16:30:00', '正常', 'COMPLETED');

INSERT IGNORE INTO `mes_equipment`.`eqp_inspection_record` (`id`, `inspection_no`, `equipment_id`, `inspector_id`, `inspection_at`, `result`, `abnormal_desc`, `status`) VALUES
(2000501, 'EINS20260708001', 2000201, 1000001, '2026-07-08 07:50:00', '正常', NULL, 'COMPLETED'),
(2000502, 'EINS20260708002', 2000202, 1000001, '2026-07-08 07:55:00', '异常', '导轨润滑不足', 'COMPLETED');

INSERT IGNORE INTO `mes_equipment`.`eqp_repair_order` (`id`, `repair_no`, `equipment_id`, `fault_reason_id`, `reported_by`, `reported_at`, `assigned_to`, `repair_start_at`, `repair_end_at`, `repair_result`, `status`) VALUES
(2000601, 'RPR20260708001', 2000201, 2000301, 1000001, '2026-07-08 10:35:00', 1000001, '2026-07-08 10:45:00', NULL, NULL, 'PROCESSING'),
(2000602, 'RPR20260707001', 2000203, 2000302, 1000001, '2026-07-07 15:00:00', 1000001, '2026-07-07 15:10:00', '2026-07-07 15:40:00', '已修复', 'COMPLETED');

INSERT IGNORE INTO `mes_equipment`.`iot_device_point` (`id`, `equipment_id`, `point_code`, `point_name`, `data_type`, `unit_id`, `status`) VALUES
(2000701, 2000201, 'DP-EQ-ASSY-COUNT', '装配计数', 'NUMBER', 9300001, 'ENABLED'),
(2000702, 2000203, 'DP-EQ-QC-PASS', '检验合格计数', 'NUMBER', 9300001, 'ENABLED'),
(2000703, 2000204, 'DP-EQ-AGING-TEMP', '老化温度', 'NUMBER', NULL, 'ENABLED');

INSERT IGNORE INTO `mes_equipment`.`iot_collect_record` (`id`, `equipment_id`, `point_id`, `collect_value`, `collect_at`, `quality_flag`) VALUES
(2000801, 2000201, 2000701, '18', '2026-07-08 11:00:00', 'GOOD'),
(2000802, 2000203, 2000702, '28', '2026-07-08 10:20:00', 'GOOD'),
(2000803, 2000204, 2000703, '42.5', '2026-07-08 10:30:00', 'GOOD');

INSERT IGNORE INTO `mes_equipment`.`iot_count_report` (`id`, `equipment_id`, `task_id`, `operation_task_id`, `count_qty`, `report_at`, `sync_log_id`) VALUES
(2000901, 2000201, 4200001, 4300001, 18.000000, '2026-07-08 11:00:00', 7000104),
(2000902, 2000203, 4200001, 4300002, 28.000000, '2026-07-08 10:20:00', NULL);

INSERT IGNORE INTO `mes_equipment`.`iot_debug_log` (`id`, `equipment_id`, `request_payload`, `response_payload`, `result`, `debug_at`) VALUES
(2001001, 2000201, '{"point":"DP-EQ-ASSY-COUNT","value":18}', '{"accepted":true}', 'SUCCESS', '2026-07-08 11:00:01'),
(2001002, 2000204, '{"point":"DP-EQ-AGING-TEMP","value":42.5}', '{"accepted":true}', 'SUCCESS', '2026-07-08 10:30:01');

INSERT IGNORE INTO `mes_equipment`.`eqp_oee_snapshot` (`id`, `equipment_id`, `line_id`, `stat_date`, `availability`, `performance`, `quality_rate`, `oee`) VALUES
(2001101, 2000201, 9500001, '2026-07-08', 0.920000, 0.880000, 0.960000, 0.777216),
(2001102, 2000203, 9500001, '2026-07-08', 0.950000, 0.900000, 0.933333, 0.797999);

INSERT IGNORE INTO `mes_equipment`.`eqp_energy_record` (`id`, `equipment_id`, `energy_type`, `value`, `unit_id`, `record_at`) VALUES
(2001201, 2000201, '电', 38.500000, NULL, '2026-07-08 12:00:00'),
(2001202, 2000204, '电', 52.800000, NULL, '2026-07-08 12:00:00');

INSERT IGNORE INTO `mes_quality`.`qc_inspection_order` (`id`, `inspection_no`, `inspection_type`, `plan_id`, `work_order_id`, `task_id`, `operation_task_id`, `product_id`, `barcode_id`, `inspector_id`, `inspection_at`, `final_result`, `status`) VALUES
(5000005, 'QC20260708005', '成品发货', 3000201, 4000003, NULL, NULL, 9400001, 7100404, 1000001, '2026-07-08 13:00:00', '让步接收', '让步接收');

INSERT IGNORE INTO `mes_quality`.`qc_inspection_result` (`id`, `inspection_id`, `qc_item_id`, `measured_value`, `result`, `defect_reason_id`, `remark`) VALUES
(5000104, 5000003, 3000103, '3', '合格', NULL, '成品入库风速档位正常'),
(5000105, 5000005, 3000101, '外箱轻微压痕', '让步接收', 9700101, '客户确认让步接收');

INSERT IGNORE INTO `mes_quality`.`mes_operation_log` (`id`, `module_name`, `target_table`, `target_id`, `action_name`, `old_status`, `new_status`, `operator_id`, `remark`, `operated_at`) VALUES
(5000301, 'quality', 'qc_inspection_order', 5000002, 'confirm', '检验中', '不合格', 1000001, '联调质检确认', '2026-07-08 10:30:00'),
(5000302, 'quality', 'qc_defect_record', 5000201, 'create', NULL, '待处理', 1000001, '联调不良记录', '2026-07-08 10:31:00');

INSERT IGNORE INTO `mes_report`.`rpt_metric_def` (`id`, `metric_code`, `metric_name`, `metric_type`, `calc_expression`, `status`) VALUES
(8000001, 'OUTPUT_QTY_DAY', '日产量', '产量', 'sum(shop_report.qualified_qty)', '启用'),
(8000002, 'DEFECT_QTY_DAY', '日不良数', '质量', 'sum(shop_report.defective_qty)', '启用'),
(8000003, 'OEE_DAY', '日 OEE', '设备', 'avg(eqp_oee_snapshot.oee)', '启用'),
(8000004, 'ANDON_OPEN', '未关闭安灯数', '安灯', 'count(andon_exception_order where status not closed)', '启用'),
(8000005, 'WAGE_AMOUNT_MONTH', '月计件金额', '工资', 'sum(wage_piece_summary.total_amount)', '启用');

INSERT IGNORE INTO `mes_report`.`rpt_metric_snapshot` (`id`, `metric_id`, `stat_dimension`, `dimension_id`, `stat_period`, `stat_time`, `metric_value`) VALUES
(8000101, 8000001, '产线', 9500001, '日', '2026-07-08 12:00:00', 76.000000),
(8000102, 8000002, '产线', 9500001, '日', '2026-07-08 12:00:00', 2.000000),
(8000103, 8000003, '设备', 2000201, '日', '2026-07-08 12:00:00', 0.777216),
(8000104, 8000004, '车间', 9100001, '日', '2026-07-08 12:00:00', 1.000000),
(8000105, 8000005, '人员', 1000001, '月', '2026-07-08 12:00:00', 58.400000);

INSERT IGNORE INTO `mes_report`.`rpt_query_condition` (`id`, `user_id`, `report_code`, `condition_name`, `condition_json`) VALUES
(8000201, 1000001, 'OUTPUT_REPORT', '总装一线今日产量', '{"lineId":9500001,"date":"2026-07-08"}'),
(8000202, 1000001, 'DEFECT_REPORT', '今日不良查询', '{"workshopId":9100002,"date":"2026-07-08"}');

INSERT IGNORE INTO `mes_report`.`board_config` (`id`, `board_code`, `board_name`, `board_type`, `refresh_seconds`, `config_json`, `status`) VALUES
(8000301, 'BOARD-LINE-ASSY-01', '总装一线看板', '产线看板', 30, '{"lineId":9500001,"metrics":["OUTPUT_QTY_DAY","DEFECT_QTY_DAY","OEE_DAY"]}', '启用'),
(8000302, 'BOARD-WORKSHOP-ASSY', '总装车间看板', '车间看板', 60, '{"workshopId":9100001,"metrics":["OUTPUT_QTY_DAY","ANDON_OPEN"]}', '启用'),
(8000303, 'BOARD-CONTROL-CENTER', '中控综合看板', '中控看板', 60, '{"modules":["production","quality","equipment","andon"]}', '启用');

INSERT IGNORE INTO `mes_report`.`board_data_cache` (`id`, `board_id`, `dimension_id`, `data_json`, `generated_at`, `expire_at`) VALUES
(8000401, 8000301, 9500001, '{"output":76,"defects":2,"oee":0.777216,"runningOrders":1}', '2026-07-08 12:00:00', '2026-07-08 12:01:00'),
(8000402, 8000302, 9100001, '{"lines":2,"openAndon":1,"completionRate":0.63}', '2026-07-08 12:00:00', '2026-07-08 12:02:00'),
(8000403, 8000303, 9000001, '{"production":"RUNNING","quality":"WARN","equipment":"WARN","andon":"OPEN"}', '2026-07-08 12:00:00', '2026-07-08 12:02:00');

INSERT IGNORE INTO `mes_integration`.`int_external_system` (`id`, `system_code`, `system_name`, `auth_type`, `status`) VALUES
(7000001, 'ERP-DEMO', '联调 ERP 系统', 'TOKEN', 'ENABLED'),
(7000002, 'OPENAPI-DEMO', '联调标准 API 客户端', 'APP_KEY', 'ENABLED'),
(7000003, 'DEVICE-GATEWAY', '设备采集网关', 'SIGN', 'ENABLED');

INSERT IGNORE INTO `mes_integration`.`int_sync_log` (`id`, `system_id`, `interface_code`, `direction`, `biz_type`, `biz_id`, `external_no`, `idempotent_key`, `request_payload`, `response_payload`, `sync_status`, `retry_count`, `error_message`, `started_at`, `finished_at`) VALUES
(7000101, 7000001, 'ERP_PRODUCTION_TASK_READ', 'IN', 'PRODUCTION_TASK', 4200001, 'ERP-TASK-20260708-001', 'erp-task-20260708-001', '{"externalNo":"ERP-TASK-20260708-001"}', '{"success":true,"taskId":4200001}', 'SUCCESS', 0, NULL, '2026-07-08 07:30:00', '2026-07-08 07:30:02'),
(7000102, 7000001, 'ERP_ROUTE_READ', 'IN', 'ROUTE', 9700201, 'ERP-ROUTE-FAN-ASSY', 'erp-route-fan-assy-v1', '{"routeCode":"ROUTE-FAN-ASSY"}', '{"success":false}', 'FAILED', 1, '工序编码重复', '2026-07-08 07:35:00', '2026-07-08 07:35:03'),
(7000103, 7000002, 'OPENAPI_WORK_ORDER_WRITE', 'IN', 'WORK_ORDER', 4000003, 'API-WO-20260708-003', 'api-wo-20260708-003', '{"workOrderNo":"WO20260708003"}', '{"success":true,"id":4000003}', 'SUCCESS', 0, NULL, '2026-07-07 08:00:00', '2026-07-07 08:00:01'),
(7000104, 7000003, 'OPENAPI_DEVICE_COUNT_REPORT_WRITE', 'IN', 'DEVICE_COUNT_REPORT', 2000901, 'DEVICE-COUNT-20260708-001', 'device-count-20260708-001', '{"equipmentCode":"EQ-ASSY-01","countQty":18}', '{"success":true}', 'SUCCESS', 0, NULL, '2026-07-08 11:00:00', '2026-07-08 11:00:01'),
(7000105, 7000002, 'OPENAPI_COMPLETION_ORDER_READ', 'OUT', 'COMPLETION_ORDER', 7200301, 'COMP20260708001', 'completion-read-20260708-001', '{"completionNo":"COMP20260708001"}', '{"completedQty":60,"qualifiedQty":59}', 'SUCCESS', 0, NULL, '2026-07-08 13:20:00', '2026-07-08 13:20:01');

INSERT IGNORE INTO `mes_integration`.`int_request_log` (`id`, `sync_log_id`, `method`, `url`, `headers_json`, `body`, `requested_at`) VALUES
(7000201, 7000101, 'POST', '/api/integration/erp/production-tasks/read', '{"Authorization":"Bearer demo"}', '{"externalNo":"ERP-TASK-20260708-001"}', '2026-07-08 07:30:00'),
(7000202, 7000104, 'POST', '/api/openapi/device-count-reports/write', '{"X-App-Key":"demo"}', '{"equipmentCode":"EQ-ASSY-01","countQty":18}', '2026-07-08 11:00:00');

INSERT IGNORE INTO `mes_integration`.`int_response_log` (`id`, `sync_log_id`, `http_status`, `body`, `responded_at`, `elapsed_ms`) VALUES
(7000301, 7000101, 200, '{"success":true,"taskId":4200001}', '2026-07-08 07:30:02', 120),
(7000302, 7000102, 400, '{"success":false,"message":"工序编码重复"}', '2026-07-08 07:35:03', 180),
(7000303, 7000104, 200, '{"success":true}', '2026-07-08 11:00:01', 80);
