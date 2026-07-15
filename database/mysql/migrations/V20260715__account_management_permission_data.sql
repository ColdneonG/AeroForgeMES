-- Account-management permission data only. This migration intentionally contains no DDL.
-- The existing MES_TEST_OPERATOR role is the development administrator role seeded by the project.
USE `mes_auth`;

INSERT IGNORE INTO `sys_permission` (`id`, `permission_code`, `permission_name`, `permission_type`, `module_code`) VALUES
  (1000991, 'system:user:view', '用户管理查看', 'BUTTON', 'system'),
  (1000992, 'system:user:create', '创建账号', 'BUTTON', 'system'),
  (1000993, 'system:user:edit', '编辑账号资料', 'BUTTON', 'system'),
  (1000994, 'system:user:status', '启停账号', 'BUTTON', 'system'),
  (1000995, 'system:user:assign-role', '分配账号角色', 'BUTTON', 'system'),
  (1000996, 'system:user:reset-password', '重置账号密码', 'BUTTON', 'system'),
  (1000997, 'system:role:view', '查看角色', 'BUTTON', 'system');

INSERT IGNORE INTO `sys_role_permission` (`id`, `role_id`, `permission_id`)
SELECT 1000991, r.id, 1000991 FROM `sys_role` r WHERE r.role_code = 'MES_TEST_OPERATOR'
UNION ALL SELECT 1000992, r.id, 1000992 FROM `sys_role` r WHERE r.role_code = 'MES_TEST_OPERATOR'
UNION ALL SELECT 1000993, r.id, 1000993 FROM `sys_role` r WHERE r.role_code = 'MES_TEST_OPERATOR'
UNION ALL SELECT 1000994, r.id, 1000994 FROM `sys_role` r WHERE r.role_code = 'MES_TEST_OPERATOR'
UNION ALL SELECT 1000995, r.id, 1000995 FROM `sys_role` r WHERE r.role_code = 'MES_TEST_OPERATOR'
UNION ALL SELECT 1000996, r.id, 1000996 FROM `sys_role` r WHERE r.role_code = 'MES_TEST_OPERATOR'
UNION ALL SELECT 1000997, r.id, 1000997 FROM `sys_role` r WHERE r.role_code = 'MES_TEST_OPERATOR';
