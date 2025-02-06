import { usePermissionStore } from '../stores/permission'

// 检查用户是否登录
export function isLoggedIn() {
  return !!localStorage.getItem('token')
}

// 获取用户角色
export function getUserRole() {
  return localStorage.getItem('userRole') || 'user'
}

// 检查是否是管理员
export function isAdmin() {
  return getUserRole() === 'admin'
}

// 检查是否有指定权限
export function hasPermission(permission) {
  const permissionStore = usePermissionStore()
  return permissionStore.hasPermission(permission)
}

// 检查是否有指定权限中的任意一个
export function hasAnyPermission(permissions) {
  const permissionStore = usePermissionStore()
  return permissionStore.hasAnyPermission(permissions)
}

// 检查是否有指定的所有权限
export function hasAllPermissions(permissions) {
  const permissionStore = usePermissionStore()
  return permissionStore.hasAllPermissions(permissions)
}

// 获取用户权限列表
export function getUserPermissions() {
  const permissionsStr = localStorage.getItem('userPermissions')
  return permissionsStr ? JSON.parse(permissionsStr) : []
}

// 保存用户权限
export function setUserPermissions(permissions) {
  localStorage.setItem('userPermissions', JSON.stringify(permissions))
}

// 保存用户角色
export function setUserRole(role) {
  localStorage.setItem('userRole', role)
}

// 清除权限数据
export function clearAuthData() {
  localStorage.removeItem('token')
  localStorage.removeItem('userRole')
  localStorage.removeItem('userPermissions')
  localStorage.removeItem('userId')
  localStorage.removeItem('username')
}

// 初始化权限数据
export function initAuthData(userData) {
  const { token, role, permissions, id, username } = userData
  
  localStorage.setItem('token', token)
  localStorage.setItem('userRole', role)
  localStorage.setItem('userPermissions', JSON.stringify(permissions))
  localStorage.setItem('userId', id)
  localStorage.setItem('username', username)
  
  const permissionStore = usePermissionStore()
  permissionStore.initPermissions()
}

// 检查路由是否需要权限
export function checkRoutePermission(route, permissionMap) {
  // 如果是白名单路由，直接返回true
  if (['/login', '/register', '/404', '/403'].includes(route)) {
    return true
  }
  
  // 如果是首页，直接返回true
  if (route === '/dashboard') {
    return true
  }
  
  // 如果是管理员，直接返回true
  if (isAdmin()) {
    return true
  }
  
  // 获取路由所需权限
  const requiredPermissions = permissionMap[route]
  
  // 如果路由不需要权限，返回true
  if (!requiredPermissions) {
    return true
  }
  
  // 检查是否有权限
  return hasAnyPermission(requiredPermissions)
}

// 过滤菜单项
export function filterMenuItems(menuItems, permissionMap) {
  return menuItems.filter(item => {
    // 如果有子菜单，递归过滤
    if (item.children) {
      item.children = filterMenuItems(item.children, permissionMap)
      return item.children.length > 0
    }
    
    // 检查当前菜单项是否有权限访问
    return checkRoutePermission(item.path, permissionMap)
  })
} 