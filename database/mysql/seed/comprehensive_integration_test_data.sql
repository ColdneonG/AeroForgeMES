-- Fan MES 综合联调测试数据（2026-07-13）
--
-- 用途：独立初始化全量基础数据，并提供一组可重复执行的跨模块场景数据。
--       再写入本文件的 TEST-20260713 综合场景。该脚本不删除任何非 TEST 数据。
-- 执行：先 cd 到本目录，再运行：
--       mysql -uroot -p123456 --default-character-set=utf8mb4 < 09_comprehensive_integration_test_data.sql
-- 注意：SOURCE 是 mysql 客户端指令，因此请使用上述命令或支持 SOURCE 的客户端执行。

-- 以下为 2026-07-13 新增的综合联调数据。
-- 注意：本脚本只准备“初始态/对照态”数据；状态流转请优先经前端或 API 完成，避免绕过业务校验。

SET NAMES utf8mb4;
SET SQL_SAFE_UPDATES = 0;
START TRANSACTION;

-- 0. 清理本文件上一次写入的 TEST-20260713 数据（严格按子表到父表顺序）。
--    仅使用本脚本专属 ID 段，不影响其他历史或业务数据。
DELETE FROM `mes_report`.`rpt_report_dataset` WHERE id BETWEEN 9138001 AND 9138099;
DELETE FROM `mes_report`.`rpt_metric_snapshot` WHERE id BETWEEN 9138101 AND 9138199;
DELETE FROM `mes_report`.`rpt_dashboard_line` WHERE id BETWEEN 9138201 AND 9138299;
DELETE FROM `mes_report`.`rpt_dashboard_metric` WHERE id BETWEEN 9138301 AND 9138399;
DELETE FROM `mes_report`.`rpt_dashboard_stock` WHERE id BETWEEN 9138401 AND 9138499;

DELETE FROM `mes_integration`.`int_response_log` WHERE id BETWEEN 9137001 AND 9137099;
DELETE FROM `mes_integration`.`int_request_log` WHERE id BETWEEN 9137101 AND 9137199;
DELETE FROM `mes_integration`.`int_sync_log` WHERE id BETWEEN 9137201 AND 9137299;

DELETE FROM `mes_equipment`.`iot_collect_record` WHERE id BETWEEN 9136001 AND 9136099;
DELETE FROM `mes_equipment`.`iot_count_report` WHERE id BETWEEN 9136101 AND 9136199;
DELETE FROM `mes_equipment`.`iot_debug_log` WHERE id BETWEEN 9136201 AND 9136299;
DELETE FROM `mes_equipment`.`eqp_oee_snapshot` WHERE id BETWEEN 9136301 AND 9136399;
DELETE FROM `mes_equipment`.`eqp_maintenance_task` WHERE id BETWEEN 9136401 AND 9136499;
DELETE FROM `mes_equipment`.`eqp_repair_order` WHERE id BETWEEN 9136501 AND 9136599;

DELETE FROM `mes_quality`.`qc_inspection_result` WHERE inspection_id BETWEEN 9135001 AND 9135099;
DELETE FROM `mes_quality`.`qc_defect_record` WHERE id BETWEEN 9135101 AND 9135199;
DELETE FROM `mes_quality`.`qc_inspection_order` WHERE id BETWEEN 9135001 AND 9135099;

DELETE FROM `mes_production`.`wage_piece_detail` WHERE id BETWEEN 9134601 AND 9134699;
DELETE FROM `mes_production`.`wage_piece_summary` WHERE id BETWEEN 9134701 AND 9134799;
DELETE FROM `mes_production`.`andon_handle_record` WHERE exception_id BETWEEN 9134501 AND 9134599;
DELETE FROM `mes_production`.`andon_notify_record` WHERE exception_id BETWEEN 9134501 AND 9134599;
DELETE FROM `mes_production`.`andon_exception_order` WHERE id BETWEEN 9134501 AND 9134599;
DELETE FROM `mes_production`.`trace_event` WHERE id BETWEEN 9134401 AND 9134499;
DELETE FROM `mes_production`.`shop_report_material` WHERE report_id BETWEEN 9134301 AND 9134399;
DELETE FROM `mes_production`.`shop_completion_order` WHERE id BETWEEN 9134351 AND 9134399;
DELETE FROM `mes_production`.`shop_rework_order` WHERE id BETWEEN 9134391 AND 9134399;
DELETE FROM `mes_production`.`shop_report` WHERE id BETWEEN 9134301 AND 9134349;
DELETE FROM `mes_production`.`shop_product_status` WHERE id BETWEEN 9134251 AND 9134299;
DELETE FROM `mes_production`.`bc_print_record` WHERE id BETWEEN 9134151 AND 9134199;
DELETE FROM `mes_production`.`bc_barcode` WHERE id BETWEEN 9134101 AND 9134149;
DELETE FROM `mes_production`.`prod_kitting_missing_item` WHERE analysis_id BETWEEN 9134051 AND 9134099;
DELETE FROM `mes_production`.`prod_kitting_analysis` WHERE id BETWEEN 9134051 AND 9134099;
DELETE FROM `mes_production`.`shop_operation_task` WHERE id BETWEEN 9134001 AND 9134049;
DELETE FROM `mes_production`.`shop_task` WHERE id BETWEEN 9133901 AND 9133949;
DELETE FROM `mes_production`.`prod_dispatch_order` WHERE id BETWEEN 9133801 AND 9133849;
DELETE FROM `mes_production`.`prod_work_order` WHERE id BETWEEN 9133701 AND 9133749;

DELETE FROM `mes_system`.`sys_status_log` WHERE id BETWEEN 9139001 AND 9139099;
DELETE FROM `mes_system`.`sys_operation_log` WHERE id BETWEEN 9139101 AND 9139199;

-- 1. 生产主线：正常生产、欠料待产、已完工，以及待下发的状态流转样例。
INSERT INTO `mes_production`.`prod_work_order`
(`id`,`work_order_no`,`external_no`,`source_type`,`product_id`,`plan_qty`,`completed_qty`,`qualified_qty`,`defective_qty`,`unit_id`,`planned_start_at`,`planned_end_at`,`delivery_date`,`line_id`,`route_id`,`status`) VALUES
(9133701,'TEST-WO-20260713-001','ERP-TEST-20260713-001','ERP',9400001,100,42,40,2,9300001,'2026-07-13 08:00:00','2026-07-13 17:00:00','2026-07-14',9500001,9700201,'生产中'),
(9133702,'TEST-WO-20260713-002','ERP-TEST-20260713-002','ERP',9400002,80,0,0,0,9300001,'2026-07-13 09:00:00','2026-07-13 18:00:00','2026-07-15',9500002,9700201,'已下发'),
(9133703,'TEST-WO-20260713-003','API-TEST-20260713-003','API',9400001,60,60,59,1,9300001,'2026-07-12 08:00:00','2026-07-12 16:00:00','2026-07-13',9500001,9700201,'已完成'),
(9133704,'TEST-WO-20260713-004','MAN-TEST-20260713-004','手工',9400002,30,0,0,0,9300001,'2026-07-13 13:00:00','2026-07-13 17:00:00','2026-07-16',9500002,9700201,'待下发');

INSERT INTO `mes_production`.`prod_dispatch_order`
(`id`,`dispatch_no`,`work_order_id`,`line_id`,`station_id`,`team_id`,`plan_qty`,`planned_start_at`,`planned_end_at`,`status`) VALUES
(9133801,'TEST-DISP-20260713-001',9133701,9500001,9600001,9200001,100,'2026-07-13 08:00:00','2026-07-13 17:00:00','生产中'),
(9133802,'TEST-DISP-20260713-002',9133702,9500002,9600001,9200001,80,'2026-07-13 09:00:00','2026-07-13 18:00:00','已下发');

INSERT INTO `mes_production`.`shop_task`
(`id`,`task_no`,`work_order_id`,`dispatch_id`,`product_id`,`product_name`,`route_id`,`line_id`,`line_name`,`team_id`,`plan_qty`,`started_at`,`ended_at`,`status`) VALUES
(9133901,'TEST-TASK-20260713-001',9133701,9133801,9400001,'FS-500 落地扇',9700201,9500001,'总装一线',9200001,100,'2026-07-13 08:05:00',NULL,'生产中'),
(9133902,'TEST-TASK-20260713-002',9133702,9133802,9400002,'TF-230 台扇',9700201,9500002,'总装二线',9200001,80,NULL,NULL,'已下发');

INSERT INTO `mes_production`.`shop_operation_task`
(`id`,`operation_task_no`,`task_id`,`route_step_id`,`process_id`,`station_id`,`device_id`,`operator_id`,`plan_qty`,`reported_qty`,`status`) VALUES
(9134001,'TEST-OPT-20260713-001',9133901,9700301,9700001,9600001,2000201,1000001,100,42,'生产中'),
(9134002,'TEST-OPT-20260713-002',9133901,9700302,9700002,9600002,2000203,1000001,100,40,'生产中');

-- 2. 齐套/欠料：一个齐套、一个欠料，供缺料看板和异常闭环测试。
INSERT INTO `mes_production`.`prod_kitting_analysis`
(`id`,`analysis_no`,`work_order_id`,`task_id`,`analysis_time`,`kitting_status`,`missing_count`,`status`) VALUES
(9134051,'TEST-KIT-20260713-001',9133701,9133901,'2026-07-13 07:35:00','齐套',0,'已完成'),
(9134052,'TEST-KIT-20260713-002',9133702,9133902,'2026-07-13 08:40:00','缺料',1,'已完成');
INSERT INTO `mes_production`.`prod_kitting_missing_item`
(`id`,`analysis_id`,`material_id`,`required_qty`,`available_qty`,`missing_qty`,`expected_arrival_at`) VALUES
(9134061,9134051,9400003,100,120,0,NULL),
(9134062,9134051,9400004,100,130,0,NULL),
(9134063,9134052,9400004,80,50,30,'2026-07-13 14:00:00');

-- 3. 条码、报工、物料消耗、追溯和返修。报工数量与工单汇总严格一致：40 + 2 = 42。
INSERT INTO `mes_production`.`bc_barcode`
(`id`,`barcode_value`,`type_id`,`app_rule_id`,`item_id`,`batch_no`,`work_order_id`,`task_id`,`parent_barcode_id`,`print_count`,`source_type`,`status`) VALUES
(9134101,'TEST-SN-20260713-001',7100001,7100301,9400001,'TEST-BATCH-0713A',9133701,9133901,NULL,1,'生成','已扫码'),
(9134102,'TEST-SN-20260713-002',7100001,7100301,9400001,'TEST-BATCH-0713A',9133701,9133901,NULL,1,'生成','已完工'),
(9134103,'TEST-MAT-MOTOR-20260713-A',7100002,7100302,9400003,'TEST-MOTOR-0713A',9133701,9133901,NULL,0,'外部导入','已生成'),
(9134104,'TEST-CTN-20260713-001',7100003,7100303,9400001,'TEST-BATCH-0713A',9133701,9133901,NULL,1,'生成','已打印');
INSERT INTO `mes_production`.`bc_print_record`
(`id`,`barcode_id`,`template_id`,`print_by`,`print_at`,`print_count`,`printer_name`) VALUES
(9134151,9134101,7100201,1000001,'2026-07-13 08:00:00',1,'TEST-LABEL-01'),
(9134152,9134104,7100203,1000001,'2026-07-13 11:20:00',1,'TEST-LABEL-02');
INSERT INTO `mes_production`.`shop_report`
(`id`,`report_no`,`report_type`,`task_id`,`operation_task_id`,`product_id`,`barcode_id`,`process_id`,`station_id`,`device_id`,`operator_id`,`report_qty`,`qualified_qty`,`defective_qty`,`report_at`,`status`) VALUES
(9134301,'TEST-RPT-20260713-001','普通',9133901,9134001,9400001,9134101,9700001,9600001,2000201,1000001,42,42,0,'2026-07-13 09:30:00','已完成'),
(9134302,'TEST-RPT-20260713-002','检测',9133901,9134002,9400001,9134102,9700002,9600002,2000203,1000001,42,40,2,'2026-07-13 10:20:00','已完成');
INSERT INTO `mes_production`.`shop_report_material` (`id`,`report_id`,`material_id`,`material_barcode_id`,`qty`) VALUES
(9134311,9134301,9400003,9134103,42),(9134312,9134301,9400004,NULL,42);
INSERT INTO `mes_production`.`shop_product_status`
(`id`,`barcode_id`,`task_id`,`operation_task_id`,`current_process_id`,`current_station_id`,`production_status`,`last_report_id`,`updated_at`) VALUES
(9134251,9134101,9133901,9134001,9700001,9600001,'生产中',9134301,'2026-07-13 09:30:00'),
(9134252,9134102,9133901,9134002,9700002,9600002,'返修',9134302,'2026-07-13 10:20:00');
INSERT INTO `mes_production`.`shop_completion_order`
(`id`,`completion_no`,`work_order_id`,`task_id`,`product_id`,`completed_qty`,`qualified_qty`,`defective_qty`,`completed_at`,`confirmed_by`,`status`) VALUES
(9134351,'TEST-COMP-20260713-001',9133703,NULL,9400001,60,59,1,'2026-07-12 16:20:00',1000001,'已完成');
INSERT INTO `mes_production`.`trace_event`
(`id`,`barcode_id`,`barcode_value`,`event_type`,`biz_type`,`biz_id`,`work_order_id`,`task_id`,`process_id`,`station_id`,`device_id`,`operator_id`,`event_at`) VALUES
(9134401,9134101,'TEST-SN-20260713-001','生成','BARCODE',9134101,9133701,9133901,NULL,NULL,NULL,1000001,'2026-07-13 08:00:00'),
(9134402,9134101,'TEST-SN-20260713-001','扫码','SHOP_FLOOR',9133901,9133701,9133901,9700001,9600001,2000201,1000001,'2026-07-13 08:10:00'),
(9134403,9134101,'TEST-SN-20260713-001','报工','REPORT',9134301,9133701,9133901,9700001,9600001,2000201,1000001,'2026-07-13 09:30:00'),
(9134404,9134102,'TEST-SN-20260713-002','质检不合格','QUALITY',9135002,9133701,9133901,9700002,9600002,2000203,1000001,'2026-07-13 10:20:00');

-- 4. 质量：合格首件、不合格巡检、待检末件；不合格自动关联返修候选。
INSERT INTO `mes_quality`.`qc_inspection_order`
(`id`,`inspection_no`,`inspection_type`,`plan_id`,`work_order_id`,`task_id`,`operation_task_id`,`product_id`,`product_name`,`work_order_no`,`barcode_id`,`inspector_id`,`inspection_at`,`final_result`,`status`) VALUES
(9135001,'TEST-QC-20260713-001','首件',3000201,9133701,9133901,9134001,9400001,'FS-500 落地扇','TEST-WO-20260713-001',9134101,1000001,'2026-07-13 08:25:00','合格','合格'),
(9135002,'TEST-QC-20260713-002','巡检',3000201,9133701,9133901,9134002,9400001,'FS-500 落地扇','TEST-WO-20260713-001',9134102,1000001,'2026-07-13 10:10:00','不合格','不合格'),
(9135003,'TEST-QC-20260713-003','末件',3000201,9133702,9133902,NULL,9400002,'TF-230 台扇','TEST-WO-20260713-002',NULL,1000001,NULL,NULL,'待检');
INSERT INTO `mes_quality`.`qc_inspection_result`
(`id`,`inspection_id`,`qc_item_id`,`measured_value`,`result`,`defect_reason_id`,`remark`) VALUES
(9135011,9135001,3000101,'无明显划伤','合格',NULL,'首件外观正常'),
(9135012,9135001,3000102,'48','合格',NULL,'噪声低于 55dB'),
(9135013,9135002,3000101,'网罩划伤','不合格',9700101,'巡检发现外观划伤'),
(9135014,9135002,3000102,'58','不合格',9700102,'噪声超上限');
INSERT INTO `mes_quality`.`qc_defect_record`
(`id`,`source_type`,`source_id`,`product_id`,`barcode_id`,`process_id`,`defect_reason_id`,`defect_reason_name`,`defect_qty`,`handle_method`,`rework_order_id`,`status`) VALUES
(9135101,'检验',9135002,9400001,9134102,9700002,9700101,'外观划伤',2,'返修',9134391,'处理中');
INSERT INTO `mes_production`.`shop_rework_order`
(`id`,`rework_no`,`source_type`,`source_id`,`barcode_id`,`product_id`,`route_id`,`reason_id`,`status`) VALUES
(9134391,'TEST-RWK-20260713-001','质量检验',9135002,9134102,9400001,9700201,9700101,'处理中');

-- 5. 设备、安灯和设备数据采集：包含一个处理中异常和一个已关闭异常。
INSERT INTO `mes_equipment`.`eqp_maintenance_task`
(`id`,`maintenance_no`,`equipment_id`,`plan_at`,`assigned_to`,`completed_at`,`result`,`status`) VALUES
(9136401,'TEST-MNT-20260713-001',2000202,'2026-07-13 14:00:00',1000001,NULL,NULL,'待处理');
INSERT INTO `mes_equipment`.`eqp_repair_order`
(`id`,`repair_no`,`equipment_id`,`fault_reason_id`,`reported_by`,`reported_at`,`assigned_to`,`repair_start_at`,`repair_end_at`,`repair_result`,`status`) VALUES
(9136501,'TEST-RPR-20260713-001',2000201,2000301,1000001,'2026-07-13 10:35:00',1000001,'2026-07-13 10:45:00',NULL,NULL,'维修中');
INSERT INTO `mes_equipment`.`iot_collect_record` (`id`,`equipment_id`,`point_id`,`collect_value`,`collect_at`,`quality_flag`) VALUES
(9136001,2000201,2000701,'42','2026-07-13 09:30:00','GOOD'),
(9136002,2000201,2000701,'58','2026-07-13 10:10:00','WARN');
INSERT INTO `mes_equipment`.`iot_count_report` (`id`,`equipment_id`,`task_id`,`operation_task_id`,`count_qty`,`report_at`,`sync_log_id`) VALUES
(9136101,2000201,9133901,9134001,42,'2026-07-13 09:30:00',9137203);
INSERT INTO `mes_equipment`.`iot_debug_log` (`id`,`equipment_id`,`request_payload`,`response_payload`,`result`,`debug_at`) VALUES
(9136201,2000201,'{"countQty":42}','{"accepted":true}','SUCCESS','2026-07-13 09:30:01');
INSERT INTO `mes_equipment`.`eqp_oee_snapshot`
(`id`,`equipment_id`,`line_id`,`stat_date`,`availability`,`performance`,`quality_rate`,`oee`) VALUES
(9136301,2000201,9500001,'2026-07-13',0.920000,0.880000,0.952381,0.771429),
(9136302,2000203,9500001,'2026-07-13',0.960000,0.910000,0.952381,0.831429);
INSERT INTO `mes_production`.`andon_exception_order`
(`id`,`exception_no`,`type_id`,`reason_id`,`work_order_id`,`task_id`,`operation_task_id`,`line_id`,`station_id`,`equipment_id`,`reported_by`,`reported_at`,`assigned_to`,`closed_by`,`closed_at`,`status`) VALUES
(9134501,'TEST-ANDON-20260713-001',7600001,7600201,9133701,9133901,9134001,9500001,9600001,2000201,1000001,'2026-07-13 10:35:00',1000001,NULL,NULL,'处理中'),
(9134502,'TEST-ANDON-20260713-002',7600002,7600202,9133702,9133902,NULL,9500002,9600001,NULL,1000001,'2026-07-13 08:45:00',1000001,1000001,'2026-07-13 09:20:00','已关闭');
INSERT INTO `mes_production`.`andon_handle_record` (`id`,`exception_id`,`handler_id`,`handle_action`,`handle_content`,`handled_at`,`result`) VALUES
(9134511,9134501,1000001,'接单','已联系设备维护人员','2026-07-13 10:40:00','处理中'),
(9134512,9134502,1000001,'关闭','物料已补齐，恢复生产','2026-07-13 09:20:00','成功');

-- 6. 计件、集成和报表数据。同步日志保留失败态，供重试按钮验证。
INSERT INTO `mes_production`.`wage_piece_detail`
(`id`,`report_id`,`operator_id`,`process_id`,`qualified_qty`,`rate`,`amount`,`calc_at`,`status`) VALUES
(9134601,9134301,1000001,9700001,42,1.20,50.40,'2026-07-13 10:30:00','已完成'),
(9134602,9134302,1000001,9700002,40,0.80,32.00,'2026-07-13 10:30:00','已完成');
INSERT INTO `mes_production`.`wage_piece_summary`
(`id`,`operator_id`,`period_key`,`total_qty`,`total_amount`,`confirmed_by`,`confirmed_at`,`status`) VALUES
(9134701,1000001,'2026-07',82,82.40,NULL,NULL,'待确认');
INSERT INTO `mes_integration`.`int_sync_log`
(`id`,`system_id`,`interface_code`,`direction`,`biz_type`,`biz_id`,`external_no`,`idempotent_key`,`request_payload`,`response_payload`,`sync_status`,`retry_count`,`error_message`,`started_at`,`finished_at`) VALUES
(9137201,7000001,'ERP_PRODUCTION_TASK_READ','IN','PRODUCTION_TASK',9133901,'ERP-TEST-TASK-0713-001','test-erp-task-0713-001','{"externalNo":"ERP-TEST-TASK-0713-001"}','{"success":true}','SUCCESS',0,NULL,'2026-07-13 07:50:00','2026-07-13 07:50:01'),
(9137202,7000001,'ERP_ROUTE_READ','IN','ROUTE',9700201,'ERP-TEST-ROUTE-0713','test-erp-route-0713','{"routeCode":"ROUTE-FAN-ASSY"}','{"success":false}','FAILED',1,'模拟 ERP 工艺版本冲突','2026-07-13 07:55:00','2026-07-13 07:55:02'),
(9137203,7000003,'OPENAPI_DEVICE_COUNT_REPORT_WRITE','IN','DEVICE_COUNT_REPORT',9136101,'TEST-DEVICE-COUNT-0713-001','test-device-count-0713-001','{"equipmentCode":"EQ-ASSY-01","countQty":42}','{"success":true}','SUCCESS',0,NULL,'2026-07-13 09:30:00','2026-07-13 09:30:01'),
(9137204,7000002,'OPENAPI_COMPLETION_ORDER_READ','OUT','COMPLETION_ORDER',9134351,'TEST-COMP-20260713-001','test-completion-0713-001','{"completionNo":"TEST-COMP-20260713-001"}','{"completedQty":60,"qualifiedQty":59}','SUCCESS',0,NULL,'2026-07-13 11:30:00','2026-07-13 11:30:01');
INSERT INTO `mes_integration`.`int_request_log` (`id`,`sync_log_id`,`method`,`url`,`headers_json`,`body`,`requested_at`) VALUES
(9137101,9137202,'POST','/api/integration/erp/routes/read','{"Authorization":"Bearer test"}','{"routeCode":"ROUTE-FAN-ASSY"}','2026-07-13 07:55:00'),
(9137102,9137203,'POST','/api/openapi/device-count-reports/write','{"X-App-Key":"test"}','{"countQty":42}','2026-07-13 09:30:00');
INSERT INTO `mes_integration`.`int_response_log` (`id`,`sync_log_id`,`http_status`,`body`,`responded_at`,`elapsed_ms`) VALUES
(9137001,9137202,409,'{"success":false,"message":"模拟 ERP 工艺版本冲突"}','2026-07-13 07:55:02',180),
(9137002,9137203,200,'{"success":true}','2026-07-13 09:30:01',80);

INSERT INTO `mes_report`.`rpt_dashboard_line`
(`id`,`line_id`,`line_code`,`line_name`,`batch_no`,`work_order_no`,`product_name`,`planned_qty`,`completed_qty`,`good_qty`,`defect_qty`,`oee`,`performance`,`output_trend`,`running_order_count`,`active_flag`) VALUES
(9138201,9500001,'LINE-ASSY-01','总装一线','TEST-BATCH-0713A','TEST-WO-20260713-001','FS-500 落地扇',100,42,40,2,0.771429,0.880000,'8,12,18,25,31,37,42',1,1),
(9138202,9500002,'LINE-ASSY-02','总装二线','TEST-BATCH-0713B','TEST-WO-20260713-002','TF-230 台扇',80,0,0,0,0.000000,0.000000,'0,0,0,0,0,0,0',1,1);
INSERT INTO `mes_report`.`rpt_dashboard_metric` (`id`,`metric_key`,`metric_value`) VALUES
(9138301,'oee',0.771429),(9138302,'availability',0.920000),(9138303,'performance',0.880000),(9138304,'quality',0.952381);
INSERT INTO `mes_report`.`rpt_dashboard_stock`
(`id`,`material_code`,`material_name`,`unit_name`,`required_qty`,`actual_qty`,`stock_status`) VALUES
(9138401,'MAT-MOTOR-65','65W 电机','PCS',100,120,'SUFFICIENT'),
(9138402,'MAT-GUARD-500','500mm 网罩套件','SET',80,50,'SHORTAGE');
INSERT INTO `mes_report`.`rpt_metric_snapshot`
(`id`,`metric_id`,`stat_dimension`,`dimension_id`,`stat_period`,`stat_time`,`metric_value`) VALUES
(9138101,8000001,'产线',9500001,'日','2026-07-13 12:00:00',42),(9138102,8000002,'产线',9500001,'日','2026-07-13 12:00:00',2),
(9138103,8000003,'设备',2000201,'日','2026-07-13 12:00:00',0.771429),(9138104,8000004,'车间',9100001,'日','2026-07-13 12:00:00',1),(9138105,8000005,'人员',1000001,'月','2026-07-13 12:00:00',82.40);
INSERT INTO `mes_report`.`rpt_report_dataset`
(`id`,`metric_code`,`row_no`,`stat_date`,`stat_period`,`dimension_type`,`dimension_code`,`dimension_name`,`line_code`,`line_name`,`work_order_no`,`product_code`,`product_name`,`business_no`,`business_type`,`reason_name`,`operator_name`,`planned_qty`,`reported_qty`,`qualified_qty`,`defective_qty`,`report_count`,`availability`,`performance`,`quality_rate`,`oee`,`total_qty`,`total_amount`,`started_at`,`ended_at`,`status`,`remark`) VALUES
(9138001,'OUTPUT_QTY_DAY',1,'2026-07-13','日','产线','LINE-ASSY-01','总装一线','LINE-ASSY-01','总装一线','TEST-WO-20260713-001','FG-FAN-500','FS-500 落地扇','TEST-RPT-20260713-002','生产报工',NULL,'test_operator',100,42,40,2,2,NULL,NULL,NULL,NULL,NULL,NULL,'2026-07-13 08:00:00','2026-07-13 10:20:00','RUNNING','正常生产中'),
(9138002,'DEFECT_QTY_DAY',1,'2026-07-13','日','产线','LINE-ASSY-01','总装一线','LINE-ASSY-01','总装一线','TEST-WO-20260713-001','FG-FAN-500','FS-500 落地扇','TEST-QC-20260713-002','质量检验','外观划伤','test_operator',NULL,NULL,NULL,2,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'WARN','已创建返修工单'),
(9138003,'OEE_DAY',1,'2026-07-13','日','设备','EQ-ASSY-01','自动锁螺丝机 1#','LINE-ASSY-01','总装一线',NULL,NULL,NULL,'EQ-ASSY-01','设备 OEE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0.92,0.88,0.952381,0.771429,NULL,NULL,NULL,NULL,'WARN','设备异常处理中'),
(9138004,'ANDON_OPEN',1,'2026-07-13','日','安灯','LINE-ASSY-01','总装一线','LINE-ASSY-01','总装一线','TEST-WO-20260713-001',NULL,NULL,'TEST-ANDON-20260713-001','设备异常','锁螺丝机扭矩异常','test_operator',NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,NULL,'2026-07-13 10:35:00',NULL,'OPEN','维修处理中'),
(9138005,'WAGE_AMOUNT_MONTH',1,'2026-07-13','月','人员','EMP-T001','test_operator',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'test_operator',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,82,82.40,NULL,NULL,'待确认','2026-07 计件汇总'),
(9138006,'BOARD-WORKSHOP-ASSY',1,'2026-07-13','日','车间','WS-ASSY','总装车间','LINE-ASSY-01','总装一线',NULL,NULL,NULL,'TEST-ANDON-20260713-001','设备异常','锁螺丝机扭矩异常',NULL,NULL,NULL,40,2,2,NULL,NULL,NULL,0.771429,NULL,NULL,NULL,NULL,'WARN','两条产线；一条待料；一个处理中安灯'),
(9138007,'BOARD-CONTROL-CENTER',1,'2026-07-13','实时','生产','PRODUCTION','生产执行',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,40,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'RUNNING','工单 TEST-WO-20260713-001 正在生产'),
(9138008,'BOARD-CONTROL-CENTER',2,'2026-07-13','实时','质量','QUALITY','质量预警',NULL,NULL,NULL,NULL,NULL,'TEST-QC-20260713-002','质量检验','外观划伤',NULL,NULL,NULL,0,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'WARN','2 件不良待返修'),
(9138009,'BOARD-CONTROL-CENTER',3,'2026-07-13','实时','设备','EQUIPMENT','设备预警',NULL,NULL,NULL,NULL,NULL,'TEST-ANDON-20260713-001','设备异常','锁螺丝机扭矩异常',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0.771429,NULL,NULL,NULL,NULL,NULL,'WARN','EQ-ASSY-01 维修处理中');

INSERT INTO `mes_system`.`sys_status_log`
(`id`,`biz_type`,`biz_id`,`biz_code`,`from_status`,`to_status`,`action`,`operator_id`,`operated_at`,`remark`) VALUES
(9139001,'WORK_ORDER',9133701,'TEST-WO-20260713-001','已下发','生产中','start',1000001,'2026-07-13 08:05:00','联调初始开工状态'),
(9139002,'QUALITY_INSPECTION',9135002,'TEST-QC-20260713-002','检验中','不合格','fail',1000001,'2026-07-13 10:20:00','联调不合格判定'),
(9139003,'ANDON_EXCEPTION',9134501,'TEST-ANDON-20260713-001','待处理','处理中','handle',1000001,'2026-07-13 10:40:00','联调安灯接单');

COMMIT;

-- 7. 数据验收：所有 check_result 应为 PASS；若为 FAIL，请勿开始 UI 联调。
SELECT 'PRECHECK-01 baseline master data' AS check_name,
       IF((SELECT COUNT(*) FROM `mes_system`.`md_item` WHERE id IN (9400001,9400002,9400003,9400004)) = 4, 'PASS', 'FAIL') AS check_result;
SELECT 'DATA-01 work orders' AS check_name,
       IF((SELECT COUNT(*) FROM `mes_production`.`prod_work_order` WHERE id BETWEEN 9133701 AND 9133704) = 4, 'PASS', 'FAIL') AS check_result;
SELECT 'DATA-02 work order quantity balance' AS check_name,
       IF((SELECT completed_qty = qualified_qty + defective_qty FROM `mes_production`.`prod_work_order` WHERE id = 9133701), 'PASS', 'FAIL') AS check_result;
SELECT 'DATA-03 trace chain' AS check_name,
       IF((SELECT COUNT(*) FROM `mes_production`.`trace_event` WHERE barcode_id = 9134101) = 3, 'PASS', 'FAIL') AS check_result;
SELECT 'DATA-04 quality and rework link' AS check_name,
       IF((SELECT COUNT(*) FROM `mes_quality`.`qc_defect_record` d JOIN `mes_production`.`shop_rework_order` r ON r.id=d.rework_order_id WHERE d.id=9135101) = 1, 'PASS', 'FAIL') AS check_result;
SELECT 'DATA-05 open andon' AS check_name,
       IF((SELECT status FROM `mes_production`.`andon_exception_order` WHERE id=9134501) = '处理中', 'PASS', 'FAIL') AS check_result;
SELECT 'DATA-06 integration retry case' AS check_name,
       IF((SELECT sync_status FROM `mes_integration`.`int_sync_log` WHERE id=9137202) = 'FAILED', 'PASS', 'FAIL') AS check_result;
SELECT 'DATA-07 dashboard source' AS check_name,
       IF((SELECT COUNT(*) FROM `mes_report`.`rpt_dashboard_line` WHERE id BETWEEN 9138201 AND 9138202) = 2, 'PASS', 'FAIL') AS check_result;
