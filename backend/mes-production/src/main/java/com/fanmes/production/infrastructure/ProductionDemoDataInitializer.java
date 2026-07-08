package com.fanmes.production.infrastructure;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(20)
public class ProductionDemoDataInitializer implements ApplicationRunner {
    private final JdbcTemplate jdbc;

    public ProductionDemoDataInitializer(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedWorkOrders();
        seedKitting();
        seedDispatchAndTasks();
    }

    private void seedWorkOrders() {
        jdbc.update("""
                insert into prod_work_order (
                    id, work_order_no, external_no, source_type, product_id, plan_qty,
                    completed_qty, qualified_qty, defective_qty, unit_id, planned_start_at,
                    planned_end_at, delivery_date, line_id, route_id, status
                )
                select 910300000001, 'WO-ERP-202607-001', 'ERP-20260707-001', 'ERP', 1001, 120,
                       0, 0, 0, 1, '2026-07-07 08:00:00',
                       '2026-07-07 20:00:00', '2026-07-09', 1, 5001, '已下发'
                where not exists (select 1 from prod_work_order where work_order_no = 'WO-ERP-202607-001')
                """);
        jdbc.update("""
                insert into prod_work_order (
                    id, work_order_no, external_no, source_type, product_id, plan_qty,
                    completed_qty, qualified_qty, defective_qty, unit_id, planned_start_at,
                    planned_end_at, delivery_date, line_id, route_id, status
                )
                select 910300000002, 'WO-API-202607-002', 'API-20260707-002', 'API', 1002, 80,
                       24, 23, 1, 1, '2026-07-07 09:00:00',
                       '2026-07-08 18:00:00', '2026-07-10', 2, 5002, '生产中'
                where not exists (select 1 from prod_work_order where work_order_no = 'WO-API-202607-002')
                """);
        jdbc.update("""
                insert into prod_work_order (
                    id, work_order_no, external_no, source_type, product_id, plan_qty,
                    completed_qty, qualified_qty, defective_qty, unit_id, planned_start_at,
                    planned_end_at, delivery_date, line_id, route_id, status
                )
                select 910300000003, 'WO-MAN-202607-003', null, '手工', 1003, 60,
                       0, 0, 0, 1, '2026-07-08 08:30:00',
                       '2026-07-08 17:30:00', '2026-07-12', 1, 5001, '草稿'
                where not exists (select 1 from prod_work_order where work_order_no = 'WO-MAN-202607-003')
                """);
        jdbc.update("""
                insert into prod_work_order (
                    id, work_order_no, external_no, source_type, product_id, plan_qty,
                    completed_qty, qualified_qty, defective_qty, unit_id, planned_start_at,
                    planned_end_at, delivery_date, line_id, route_id, status
                )
                select 910300000004, 'WO-MAN-202607-004', null, '手工', 1004, 90,
                       36, 35, 1, 1, '2026-07-06 08:00:00',
                       '2026-07-07 16:00:00', '2026-07-08', 3, 5003, '暂停'
                where not exists (select 1 from prod_work_order where work_order_no = 'WO-MAN-202607-004')
                """);
    }

    private void seedKitting() {
        jdbc.update("""
                insert into prod_kitting_analysis (
                    id, analysis_no, work_order_id, task_id, analysis_time,
                    kitting_status, missing_count, status
                )
                select 910300100001, 'KA-202607-001', 910300000001, null, '2026-07-07 07:30:00',
                       '欠料', 2, '草稿'
                where not exists (select 1 from prod_kitting_analysis where analysis_no = 'KA-202607-001')
                """);
        jdbc.update("""
                insert into prod_kitting_analysis (
                    id, analysis_no, work_order_id, task_id, analysis_time,
                    kitting_status, missing_count, status
                )
                select 910300100002, 'KA-202607-002', 910300000002, 910300300002, '2026-07-07 08:40:00',
                       '齐套', 0, '已完成'
                where not exists (select 1 from prod_kitting_analysis where analysis_no = 'KA-202607-002')
                """);
        jdbc.update("""
                insert into prod_kitting_missing_item (
                    id, analysis_id, material_id, required_qty, available_qty, missing_qty, expected_arrival_at
                )
                select 910300200001, 910300100001, 200101, 120, 96, 24, '2026-07-07 14:00:00'
                where not exists (select 1 from prod_kitting_missing_item where id = 910300200001)
                """);
        jdbc.update("""
                insert into prod_kitting_missing_item (
                    id, analysis_id, material_id, required_qty, available_qty, missing_qty, expected_arrival_at
                )
                select 910300200002, 910300100001, 200205, 240, 180, 60, '2026-07-08 10:00:00'
                where not exists (select 1 from prod_kitting_missing_item where id = 910300200002)
                """);
    }

    private void seedDispatchAndTasks() {
        jdbc.update("""
                insert into prod_dispatch_order (
                    id, dispatch_no, work_order_id, line_id, station_id, team_id,
                    plan_qty, planned_start_at, planned_end_at, status
                )
                select 910300300001, 'DO-202607-001', 910300000001, 1, 101, 301,
                       60, '2026-07-07 10:00:00', '2026-07-07 16:00:00', '已下发'
                where not exists (select 1 from prod_dispatch_order where dispatch_no = 'DO-202607-001')
                """);
        jdbc.update("""
                insert into prod_dispatch_order (
                    id, dispatch_no, work_order_id, line_id, station_id, team_id,
                    plan_qty, planned_start_at, planned_end_at, status
                )
                select 910300300002, 'DO-202607-002', 910300000002, 2, 201, 302,
                       80, '2026-07-07 09:30:00', '2026-07-08 18:00:00', '生产中'
                where not exists (select 1 from prod_dispatch_order where dispatch_no = 'DO-202607-002')
                """);
        jdbc.update("""
                insert into shop_task (
                    id, task_no, work_order_id, dispatch_id, product_id, route_id,
                    line_id, team_id, plan_qty, started_at, ended_at, status
                )
                select 910300400001, 'TASK-DO-202607-002', 910300000002, 910300300002, 1002, 5002,
                       2, 302, 80, '2026-07-07 09:45:00', null, '生产中'
                where not exists (select 1 from shop_task where task_no = 'TASK-DO-202607-002')
                """);
    }
}
