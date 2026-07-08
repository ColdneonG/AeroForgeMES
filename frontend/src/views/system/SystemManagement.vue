<template>
  <CrudBoard
    eyebrow="系统管理"
    title="用户、角色、菜单与权限"
    description="维护登录用户、角色授权、菜单按钮权限、接口权限、数据范围和关键审计记录。"
    list-title="用户列表"
    :rows="rows"
    :columns="columns"
    row-key="account"
    :primary-actions="primaryActions"
    :row-actions="rowActions"
  />
</template>

<script setup>
import CrudBoard from '../../components/CrudBoard.vue'
import { systemUsers } from '../../mock/mesExtendedData'

const rows = systemUsers
const columns = [
  { key: 'account', label: '账号' },
  { key: 'name', label: '姓名' },
  { key: 'roles', label: '角色' },
  { key: 'scope', label: '数据范围' },
  { key: 'status', label: '状态' }
]
const primaryActions = [
  { label: '新增用户', action: 'create' },
  { label: '角色授权', action: 'edit' },
  { label: '权限导出', action: 'export' }
]
const rowActions = [
  { label: '编辑', action: 'edit' },
  { label: '启停', action: 'close' },
  { label: '审计', action: 'audit' }
]
</script>
