import { usePermissionStore } from '../stores/permission'

// 创建权限指令
export const permission = {
  mounted(el, binding) {
    const permissionStore = usePermissionStore()
    const { value } = binding
    
    if (value) {
      const hasPermission = Array.isArray(value)
        ? permissionStore.hasAnyPermission(value)
        : permissionStore.hasPermission(value)
        
      if (!hasPermission) {
        // 如果没有权限，移除元素
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error('需要指定权限值')
    }
  }
}

// 创建所有权限指令
export const allPermissions = {
  mounted(el, binding) {
    const permissionStore = usePermissionStore()
    const { value } = binding
    
    if (value && Array.isArray(value)) {
      const hasPermission = permissionStore.hasAllPermissions(value)
      
      if (!hasPermission) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error('需要指定权限数组')
    }
  }
}

// 导出所有指令
export default {
  install(app) {
    // 注册 v-permission 指令
    app.directive('permission', permission)
    // 注册 v-all-permissions 指令
    app.directive('all-permissions', allPermissions)
  }
} 