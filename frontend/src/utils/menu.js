import { hasPermission } from '../stores/auth'

export const filterRoutesByPermission = (routes) =>
  routes
    .filter((route) => !route.meta?.hideInMenu && hasPermission(route.meta?.permission))
    .map((route) => ({
      ...route,
      children: route.children ? filterRoutesByPermission(route.children) : undefined
    }))
    .filter((route) => !route.children || route.children.length > 0)
