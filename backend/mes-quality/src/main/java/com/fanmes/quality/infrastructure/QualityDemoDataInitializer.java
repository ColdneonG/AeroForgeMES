package com.fanmes.quality.infrastructure;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(20)
public class QualityDemoDataInitializer implements ApplicationRunner {
    private final JdbcTemplate jdbc;

    public QualityDemoDataInitializer(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedInspectionStandards();
        seedInspectionOrders();
        seedResultsAndDefects();
    }

    private void seedInspectionStandards() {
        jdbc.update("""
                insert into qc_item_category (id, category_code, category_name, status)
                select 910400000001, 'QC-CAT-APPEARANCE', '外观检验', '启用'
                where not exists (select 1 from qc_item_category where category_code = 'QC-CAT-APPEARANCE')
                """);
        jdbc.update("""
                insert into qc_item_category (id, category_code, category_name, status)
                select 910400000002, 'QC-CAT-SIZE', '尺寸检验', '启用'
                where not exists (select 1 from qc_item_category where category_code = 'QC-CAT-SIZE')
                """);
        jdbc.update("""
                insert into qc_item (id, category_id, item_code, item_name, value_type, standard_value, upper_limit, lower_limit, unit_id, status)
                select 910400100001, 910400000001, 'QC-ITEM-SCRATCH', '表面划伤', '枚举', '无明显划伤', null, null, null, '启用'
                where not exists (select 1 from qc_item where item_code = 'QC-ITEM-SCRATCH')
                """);
        jdbc.update("""
                insert into qc_item (id, category_id, item_code, item_name, value_type, standard_value, upper_limit, lower_limit, unit_id, status)
                select 910400100002, 910400000002, 'QC-ITEM-LENGTH', '长度', '数值', '100.00', 100.50, 99.50, 1, '启用'
                where not exists (select 1 from qc_item where item_code = 'QC-ITEM-LENGTH')
                """);
        jdbc.update("""
                insert into qc_item (id, category_id, item_code, item_name, value_type, standard_value, upper_limit, lower_limit, unit_id, status)
                select 910400100003, 910400000002, 'QC-ITEM-WIDTH', '宽度', '数值', '35.00', 35.30, 34.70, 1, '启用'
                where not exists (select 1 from qc_item where item_code = 'QC-ITEM-WIDTH')
                """);
        jdbc.update("""
                insert into qc_plan (id, plan_code, plan_name, product_id, customer_id, is_default, status)
                select 910400200001, 'QC-PLAN-P1001-DEFAULT', 'P1001 默认检验方案', 1001, null, 1, '启用'
                where not exists (select 1 from qc_plan where plan_code = 'QC-PLAN-P1001-DEFAULT')
                """);
        jdbc.update("""
                insert into qc_plan_item (id, plan_id, qc_item_id, sample_qty, standard_value, upper_limit, lower_limit, required_flag)
                select 910400300001, 910400200001, 910400100001, 5, '无明显划伤', null, null, 1
                where not exists (select 1 from qc_plan_item where id = 910400300001)
                """);
        jdbc.update("""
                insert into qc_plan_item (id, plan_id, qc_item_id, sample_qty, standard_value, upper_limit, lower_limit, required_flag)
                select 910400300002, 910400200001, 910400100002, 5, '100.00', 100.50, 99.50, 1
                where not exists (select 1 from qc_plan_item where id = 910400300002)
                """);
        jdbc.update("""
                insert into qc_plan_item (id, plan_id, qc_item_id, sample_qty, standard_value, upper_limit, lower_limit, required_flag)
                select 910400300003, 910400200001, 910400100003, 5, '35.00', 35.30, 34.70, 1
                where not exists (select 1 from qc_plan_item where id = 910400300003)
                """);
    }

    private void seedInspectionOrders() {
        jdbc.update("""
                insert into qc_inspection_order (
                    id, inspection_no, inspection_type, plan_id, work_order_id, task_id,
                    operation_task_id, product_id, barcode_id, inspector_id, inspection_at,
                    final_result, status
                )
                select 910400400001, 'QC-FI-202607-001', '首件', 910400200001, 910300000001, null,
                       null, 1001, 600100001, 8001, '2026-07-07 08:10:00',
                       null, '待检'
                where not exists (select 1 from qc_inspection_order where inspection_no = 'QC-FI-202607-001')
                """);
        jdbc.update("""
                insert into qc_inspection_order (
                    id, inspection_no, inspection_type, plan_id, work_order_id, task_id,
                    operation_task_id, product_id, barcode_id, inspector_id, inspection_at,
                    final_result, status
                )
                select 910400400002, 'QC-LI-202607-001', '末件', 910400200001, 910300000002, 910300400001,
                       null, 1002, 600100002, 8002, '2026-07-07 16:20:00',
                       null, '检验中'
                where not exists (select 1 from qc_inspection_order where inspection_no = 'QC-LI-202607-001')
                """);
        jdbc.update("""
                insert into qc_inspection_order (
                    id, inspection_no, inspection_type, plan_id, work_order_id, task_id,
                    operation_task_id, product_id, barcode_id, inspector_id, inspection_at,
                    final_result, status
                )
                select 910400400003, 'QC-PI-202607-001', '巡检', 910400200001, 910300000002, 910300400001,
                       null, 1002, 600100003, 8002, '2026-07-07 11:30:00',
                       '合格', '合格'
                where not exists (select 1 from qc_inspection_order where inspection_no = 'QC-PI-202607-001')
                """);
        jdbc.update("""
                insert into qc_inspection_order (
                    id, inspection_no, inspection_type, plan_id, work_order_id, task_id,
                    operation_task_id, product_id, barcode_id, inspector_id, inspection_at,
                    final_result, status
                )
                select 910400400004, 'QC-FG-IN-202607-001', '成品入库', 910400200001, 910300000004, null,
                       null, 1004, 600100004, 8003, '2026-07-07 15:00:00',
                       '不合格', '不合格'
                where not exists (select 1 from qc_inspection_order where inspection_no = 'QC-FG-IN-202607-001')
                """);
        jdbc.update("""
                insert into qc_inspection_order (
                    id, inspection_no, inspection_type, plan_id, work_order_id, task_id,
                    operation_task_id, product_id, barcode_id, inspector_id, inspection_at,
                    final_result, status
                )
                select 910400400005, 'QC-SHIP-202607-001', '成品发货', 910400200001, 910300000002, null,
                       null, 1002, 600100005, 8003, '2026-07-07 17:30:00',
                       '合格', '已关闭'
                where not exists (select 1 from qc_inspection_order where inspection_no = 'QC-SHIP-202607-001')
                """);
    }

    private void seedResultsAndDefects() {
        jdbc.update("""
                insert into qc_inspection_result (id, inspection_id, qc_item_id, measured_value, result, defect_reason_id, remark)
                select 910400500001, 910400400003, 910400100002, '100.10', '合格', null, '巡检尺寸正常'
                where not exists (select 1 from qc_inspection_result where id = 910400500001)
                """);
        jdbc.update("""
                insert into qc_inspection_result (id, inspection_id, qc_item_id, measured_value, result, defect_reason_id, remark)
                select 910400500002, 910400400004, 910400100001, '明显划伤', '不合格', 7001001, '入库抽检发现外观缺陷'
                where not exists (select 1 from qc_inspection_result where id = 910400500002)
                """);
        jdbc.update("""
                insert into qc_inspection_result (id, inspection_id, qc_item_id, measured_value, result, defect_reason_id, remark)
                select 910400500003, 910400400005, 910400100003, '35.02', '合格', null, '发货抽检合格'
                where not exists (select 1 from qc_inspection_result where id = 910400500003)
                """);
        jdbc.update("""
                insert into qc_defect_record (
                    id, source_type, source_id, product_id, barcode_id, process_id,
                    defect_reason_id, defect_qty, handle_method, rework_order_id, status
                )
                select 910400600001, '检验', 910400400004, 1004, 600100004, 4001,
                       7001001, 2, '返修', null, '待处理'
                where not exists (select 1 from qc_defect_record where id = 910400600001)
                """);
    }
}
