import { defineStore } from 'pinia'

// 定义权限状态管理
export const usePermissionStore = defineStore('permission', {
  state: () => ({
    // 所有权限列表
    permissions: [],
    // 角色权限映射
    rolePermissions: {
      admin: ['*'], // 管理员拥有所有权限
      user: [
        'VIEW_DASHBOARD',
        'VIEW_PROFILE',
        'VIEW_PERMISSIONS', // 添加权限管理的查看权限
        'VIEW_USERS',
        'VIEW_ROLES'
      ], // 普通用户基础权限
    },
    // 用户权限映射
    userPermissions: {},
    // 当前用户权限
    currentUserPermissions: [],
    // 当前用户角色
    currentUserRole: '',
    // 权限继承关系
    permissionInheritance: {},
  }),

  getters: {
    // 检查是否有指定权限
    hasPermission: (state) => (permission) => {
      if (state.currentUserRole === 'admin') return true
      
      // 检查用户特定权限
      const userId = localStorage.getItem('userId')
      if (userId && state.userPermissions[userId]?.includes(permission)) {
        return true
      }
      
      // 检查角色权限
      return state.currentUserPermissions.includes(permission)
    },
    
    // 检查是否有指定权限中的任意一个
    hasAnyPermission: (state) => (permissions) => {
      if (state.currentUserRole === 'admin') return true
      
      // 检查用户特定权限
      const userId = localStorage.getItem('userId')
      if (userId) {
        const userPerms = state.userPermissions[userId] || []
        if (permissions.some(p => userPerms.includes(p))) {
          return true
        }
      }
      
      return permissions.some(permission => state.currentUserPermissions.includes(permission))
    },
    
    // 检查是否有指定的所有权限
    hasAllPermissions: (state) => (permissions) => {
      if (state.currentUserRole === 'admin') return true
      
      // 检查用户特定权限
      const userId = localStorage.getItem('userId')
      if (userId) {
        const userPerms = state.userPermissions[userId] || []
        if (permissions.every(p => userPerms.includes(p))) {
          return true
        }
      }
      
      return permissions.every(permission => state.currentUserPermissions.includes(permission))
    },

    // 获取用户的所有权限（包括继承的权限）
    getAllUserPermissions: (state) => (userId) => {
      const userPerms = new Set(state.userPermissions[userId] || [])
      const rolePerms = new Set(state.rolePermissions[state.currentUserRole] || [])
      
      // 添加继承的权限
      const addInheritedPermissions = (permission) => {
        const inherited = state.permissionInheritance[permission] || []
        inherited.forEach(p => {
          userPerms.add(p)
          addInheritedPermissions(p)
        })
      }
      
      // 处理用户权限的继承
      Array.from(userPerms).forEach(addInheritedPermissions)
      // 处理角色权限的继承
      Array.from(rolePerms).forEach(addInheritedPermissions)
      
      return Array.from(userPerms)
    }
  },

  actions: {
    // 初始化权限数据
    init() {
      // 从localStorage恢复数据
      this.restoreFromStorage()
      
      // 如果没有权限数据，设置默认数据
      if (!this.permissions.length) {
        // 添加基础权限数据
        const defaultPermissions = [
          {
            id: 1,
            permissionName: '查看权限',
            permissionCode: 'VIEW_PERMISSIONS',
            type: 'menu',
            description: '允许查看权限列表',
            status: true
          },
          {
            id: 2,
            permissionName: '管理权限',
            permissionCode: 'MANAGE_PERMISSIONS',
            type: 'operation',
            description: '允许添加、编辑、删除权限',
            status: true
          },
          {
            id: 3,
            permissionName: '查看用户',
            permissionCode: 'VIEW_USERS',
            type: 'menu',
            description: '允许查看用户列表',
            status: true
          },
          {
            id: 4,
            permissionName: '查看角色',
            permissionCode: 'VIEW_ROLES',
            type: 'menu',
            description: '允许查看角色列表',
            status: true
          },
          {
            id: 5,
            permissionName: '管理角色',
            permissionCode: 'MANAGE_ROLES',
            type: 'operation',
            description: '允许添加、编辑、删除角色',
            status: true
          },
          {
            id: 6,
            permissionName: '分配权限',
            permissionCode: 'ASSIGN_PERMISSIONS',
            type: 'operation',
            description: '允许为角色分配权限',
            status: true
          }
        ]
        
        defaultPermissions.forEach(permission => {
          this.addPermission(permission)
        })

        // 设置默认角色权限
        this.rolePermissions = {
          admin: ['*'], // 管理员拥有所有权限
          user: [
            'VIEW_DASHBOARD',
            'VIEW_PROFILE',
            'VIEW_PERMISSIONS',
            'VIEW_USERS',
            'VIEW_ROLES'
          ],
          guest: [
            'VIEW_DASHBOARD',
            'VIEW_PROFILE'
          ]
        }
      }
      
      // 初始化当前用户权限
      this.initPermissions()
    },

    // 初始化权限数据
    initPermissions() {
      // 从localStorage获取用户信息
      const userId = localStorage.getItem('userId')
      const role = localStorage.getItem('userRole') || 'user'
      this.currentUserRole = role
      
      // 如果是管理员，直接赋予所有权限
      if (role === 'admin') {
        this.currentUserPermissions = ['*']
        localStorage.setItem('userPermissions', JSON.stringify(this.currentUserPermissions))
        return
      }
      
      // 获取角色对应的权限
      const rolePermissions = this.rolePermissions[role] || []
      
      // 获取用户特定权限
      const userPermissions = userId ? (this.userPermissions[userId] || []) : []
      
      // 合并权限并处理继承
      const allPermissions = new Set([...rolePermissions, ...userPermissions])
      
      // 添加继承的权限
      const addInheritedPermissions = (permission) => {
        const inherited = this.permissionInheritance[permission] || []
        inherited.forEach(p => {
          allPermissions.add(p)
          addInheritedPermissions(p)
        })
      }
      
      Array.from(allPermissions).forEach(addInheritedPermissions)
      
      // 更新当前用户权限
      this.currentUserPermissions = Array.from(allPermissions)
      
      // 保存到localStorage
      localStorage.setItem('userPermissions', JSON.stringify(this.currentUserPermissions))
    },

    // 更新用户角色和权限
    updateUserRole(userId, role) {
      this.currentUserRole = role
      
      // 更新localStorage
      localStorage.setItem('userRole', role)
      
      // 重新初始化权限
      this.initPermissions()
    },

    // 更新用户特定权限
    updateUserPermissions(userId, permissions) {
      this.userPermissions[userId] = permissions
      
      // 如果是当前用户，重新初始化权限
      if (userId === localStorage.getItem('userId')) {
        this.initPermissions()
      }
      
      // 保存到localStorage
      localStorage.setItem('userPermissionsMap', JSON.stringify(this.userPermissions))
    },

    // 更新角色权限
    updateRolePermissions(roleCode, permissions) {
      // 如果是管理员角色，始终保持所有权限
      if (roleCode === 'admin') {
        this.rolePermissions[roleCode] = ['*']
      } else {
        this.rolePermissions[roleCode] = permissions
      }
      
      // 保存到localStorage
      localStorage.setItem('rolePermissions', JSON.stringify(this.rolePermissions))
      
      // 如果是当前用户的角色，重新初始化权限
      if (roleCode === this.currentUserRole) {
        this.initPermissions()
      }
    },

    // 获取角色权限
    getRolePermissions(roleCode) {
      return this.rolePermissions[roleCode] || []
    },

    // 检查角色是否有指定权限
    checkRolePermission(roleCode, permission) {
      const permissions = this.getRolePermissions(roleCode)
      return permissions.includes('*') || permissions.includes(permission)
    },

    // 添加角色
    addRole(roleCode, initialPermissions = []) {
      if (!this.rolePermissions[roleCode]) {
        this.rolePermissions[roleCode] = initialPermissions
        localStorage.setItem('rolePermissions', JSON.stringify(this.rolePermissions))
      }
    },

    // 删除角色
    removeRole(roleCode) {
      if (roleCode !== 'admin') { // 防止删除管理员角色
        delete this.rolePermissions[roleCode]
        localStorage.setItem('rolePermissions', JSON.stringify(this.rolePermissions))
      }
    },

    // 添加权限继承关系
    addPermissionInheritance(permission, inheritedPermission) {
      if (!this.permissionInheritance[permission]) {
        this.permissionInheritance[permission] = []
      }
      
      if (!this.permissionInheritance[permission].includes(inheritedPermission)) {
        this.permissionInheritance[permission].push(inheritedPermission)
        
        // 保存到localStorage
        localStorage.setItem('permissionInheritance', JSON.stringify(this.permissionInheritance))
        
        // 重新初始化权限以应用新的继承关系
        this.initPermissions()
      }
    },

    // 移除权限继承关系
    removePermissionInheritance(permission, inheritedPermission) {
      if (this.permissionInheritance[permission]) {
        const index = this.permissionInheritance[permission].indexOf(inheritedPermission)
        if (index > -1) {
          this.permissionInheritance[permission].splice(index, 1)
          
          // 保存到localStorage
          localStorage.setItem('permissionInheritance', JSON.stringify(this.permissionInheritance))
          
          // 重新初始化权限以应用新的继承关系
          this.initPermissions()
        }
      }
    },

    // 添加权限
    addPermission(permission) {
      if (!this.permissions.find(p => p.permissionCode === permission.permissionCode)) {
        this.permissions.push(permission)
        
        // 保存到localStorage
        localStorage.setItem('permissions', JSON.stringify(this.permissions))
      }
    },

    // 移除权限
    removePermission(permissionCode) {
      const index = this.permissions.findIndex(p => p.permissionCode === permissionCode)
      if (index > -1) {
        this.permissions.splice(index, 1)
        
        // 从继承关系中移除
        Object.keys(this.permissionInheritance).forEach(key => {
          const inheritedIndex = this.permissionInheritance[key].indexOf(permissionCode)
          if (inheritedIndex > -1) {
            this.permissionInheritance[key].splice(inheritedIndex, 1)
          }
        })
        
        // 保存到localStorage
        localStorage.setItem('permissions', JSON.stringify(this.permissions))
        localStorage.setItem('permissionInheritance', JSON.stringify(this.permissionInheritance))
        
        // 重新初始化权限
        this.initPermissions()
      }
    },

    // 从localStorage恢复数据
    restoreFromStorage() {
      const permissions = localStorage.getItem('permissions')
      if (permissions) {
        this.permissions = JSON.parse(permissions)
      }
      
      const rolePermissions = localStorage.getItem('rolePermissions')
      if (rolePermissions) {
        this.rolePermissions = JSON.parse(rolePermissions)
      }
      
      const userPermissions = localStorage.getItem('userPermissionsMap')
      if (userPermissions) {
        this.userPermissions = JSON.parse(userPermissions)
      }
      
      const permissionInheritance = localStorage.getItem('permissionInheritance')
      if (permissionInheritance) {
        this.permissionInheritance = JSON.parse(permissionInheritance)
      }
      
      // 初始化当前用户权限
      this.initPermissions()
    },

    // 清除权限数据
    clearPermissions() {
      this.permissions = []
      this.currentUserPermissions = []
      this.currentUserRole = 'user'
      this.userPermissions = {}
      this.permissionInheritance = {}
      
      localStorage.removeItem('userRole')
      localStorage.removeItem('userPermissions')
      localStorage.removeItem('permissions')
      localStorage.removeItem('rolePermissions')
      localStorage.removeItem('userPermissionsMap')
      localStorage.removeItem('permissionInheritance')
    }
  }
}) 