<template>
  <div class="user-manage">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="handleAdd">添加用户</el-button>
        </div>
      </template>
      
      <!-- 用户列表 -->
      <el-table :data="users" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column label="角色" width="200">
          <template #default="scope">
            <el-tag
              v-for="role in scope.row.roles"
              :key="role.id"
              class="role-tag"
              type="info">
              {{ role.roleName }}
            </el-tag>
            <el-button 
              link 
              type="primary" 
              size="small"
              @click="handleAssignRole(scope.row)">
              分配角色
            </el-button>
          </template>
        </el-table-column>
        <el-table-column label="角色状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.roles.length > 0 ? 'success' : 'warning'">
              {{ scope.row.roles.length > 0 ? '已分配' : '未分配' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column prop="updateTime" label="更新时间" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑用户对话框 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="dialogVisible" 
      width="500px">
      <el-form 
        :model="userForm" 
        label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="userForm.username" />
        </el-form-item>
        <el-form-item label="密码" v-if="!userForm.id">
          <el-input v-model="userForm.password" type="password" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="userForm.roles" multiple placeholder="请选择角色">
            <el-option
              v-for="role in allRoles"
              :key="role.id"
              :label="role.roleName"
              :value="role.id">
              {{ role.roleName }}
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 分配角色对话框 -->
    <el-dialog
      title="分配角色"
      v-model="roleDialogVisible"
      width="500px">
      <el-form>
        <el-form-item label="选择角色">
          <el-checkbox-group v-model="selectedRoles">
            <el-checkbox
              v-for="role in allRoles"
              :key="role.id"
              :value="role.id">
              {{ role.roleName }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="roleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleRoleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useSettingsStore } from '../stores/settings'

// 用户列表数据
const users = ref([])
const allRoles = ref([]) // 所有角色列表
const selectedRoles = ref([]) // 选中的角色ID列表
const currentUserId = ref(null) // 当前操作的用户ID
const settingsStore = useSettingsStore()

// 对话框相关数据
const dialogVisible = ref(false)
const roleDialogVisible = ref(false)
const dialogTitle = ref('添加用户')
const userForm = ref({
  id: null,
  username: '',
  password: '',
  roles: [] // 添加角色字段
})

// 获取用户列表
const getUsers = async () => {
  try {
    const res = await fetch('http://127.0.0.1:8080/api/users', {
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const data = await res.json()
    if (data.code === 0) {
      // 获取每个用户的角色信息
      const usersWithRoles = await Promise.all(
        data.data.map(async (user) => {
          try {
            const rolesRes = await fetch(`http://127.0.0.1:8080/api/roles/user/${user.id}`, {
              headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
              },
              credentials: 'include'
            })
            const rolesData = await rolesRes.json()
            console.log(`User ${user.id} roles:`, rolesData) // 添加日志
            
            // 确保返回的角色数据是数组
            let roles = []
            if (rolesData.code === 0) {
              // 如果返回的是分页数据，使用list字段
              if (rolesData.data && Array.isArray(rolesData.data.list)) {
                roles = rolesData.data.list
              }
              // 如果直接返回的是数组
              else if (Array.isArray(rolesData.data)) {
                roles = rolesData.data
              }
            }
            
            return {
              ...user,
              roles: roles
            }
          } catch (error) {
            console.error(`获取用户 ${user.id} 的角色失败:`, error)
            return {
              ...user,
              roles: []
            }
          }
        })
      )
      console.log('Users with roles:', usersWithRoles) // 添加日志
      users.value = usersWithRoles
    } else {
      ElMessage.error(data.message || '获取用户列表失败')
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  }
}

// 获取所有角色
const getAllRoles = async () => {
  try {
    console.log('开始获取角色列表...') // 添加日志
    const res = await fetch('http://127.0.0.1:8080/api/roles', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    
    console.log('角色列表API响应状态:', res.status) // 添加日志
    
    if (!res.ok) {
      throw new Error(`HTTP error! status: ${res.status}`)
    }
    
    const data = await res.json()
    console.log('获取角色列表响应:', data) // 添加日志
    
    if (data.code === 0) {
      // 使用data.data.list获取角色列表
      allRoles.value = data.data.list || []
      console.log('设置角色列表:', allRoles.value) // 添加日志
      if (allRoles.value.length === 0) {
        console.warn('获取到的角色列表为空') // 添加警告日志
      }
    } else {
      console.error('获取角色列表失败:', data.message)
      ElMessage.error(data.message || '获取角色列表失败')
    }
  } catch (error) {
    console.error('获取角色列表失败:', error)
    ElMessage.error('获取角色列表失败: ' + error.message)
  }
}

// 添加用户
const handleAdd = async () => {
  userForm.value = {
    id: null,
    username: '',
    password: '',
    roles: []
  }
  dialogTitle.value = '添加用户'
  await getAllRoles() // 获取所有角色列表
  dialogVisible.value = true
}

// 编辑用户
const handleEdit = async (row) => {
  userForm.value = {
    id: row.id,
    username: row.username,
    roles: row.roles.map(role => role.id)
  }
  dialogTitle.value = '编辑用户'
  await getAllRoles() // 获取所有角色列表
  dialogVisible.value = true
}

// 删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除该用户吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await fetch(`http://localhost:8000/api/users/${row.id}`, {
        method: 'DELETE',
        credentials: 'include'
      })
      const data = await res.json()
      if (data.code === 0) {
        ElMessage.success('删除成功')
        getUsers()
      } else {
        ElMessage.error(data.message || '删除失败')
      }
    } catch (error) {
      console.error('删除用户失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 分配角色
const handleAssignRole = async (user) => {
  currentUserId.value = user.id
  selectedRoles.value = user.roles.map(role => role.id)
  await getAllRoles() // 获取所有角色列表
  roleDialogVisible.value = true
}

// 提交角色分配
const handleRoleSubmit = async () => {
  try {
    // 获取用户当前的角色
    const currentRoles = users.value.find(u => u.id === currentUserId.value)?.roles || []
    const currentRoleIds = currentRoles.map(r => r.id)
    
    // 找出需要添加和删除的角色
    const rolesToAdd = selectedRoles.value.filter(id => !currentRoleIds.includes(id))
    const rolesToRemove = currentRoleIds.filter(id => !selectedRoles.value.includes(id))
    
    // 执行添加操作
    for (const roleId of rolesToAdd) {
      try {
        const res = await fetch(`http://127.0.0.1:8080/api/roles/assign?userId=${currentUserId.value}&roleId=${roleId}`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          },
          credentials: 'include'
        })
        const data = await res.json()
        if (data.code !== 0) {
          // 如果是重复分配的错误，跳过这个角色
          if (data.message && data.message.includes('Duplicate entry')) {
            console.warn(`角色 ${roleId} 已经分配给用户 ${currentUserId.value}，跳过`)
            continue
          }
          throw new Error(data.message || '分配角色失败')
        }
      } catch (error) {
        if (error.message && error.message.includes('Duplicate entry')) {
          console.warn(`角色 ${roleId} 已经分配给用户 ${currentUserId.value}，跳过`)
          continue
        }
        throw error
      }
    }
    
    // 执行删除操作
    for (const roleId of rolesToRemove) {
      const res = await fetch(`http://127.0.0.1:8080/api/roles/remove?userId=${currentUserId.value}&roleId=${roleId}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        credentials: 'include'
      })
      const data = await res.json()
      if (data.code !== 0) {
        throw new Error(data.message || '移除角色失败')
      }
    }
    
    ElMessage.success('角色分配成功')
    roleDialogVisible.value = false
    await getUsers() // 刷新用户列表
  } catch (error) {
    console.error('分配角色失败:', error)
    ElMessage.error(error.message || '分配角色失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    // 如果是添加用户，验证密码长度
    if (!userForm.value.id && userForm.value.password.length < settingsStore.passwordMinLength) {
      ElMessage.error(`密码长度不能小于${settingsStore.passwordMinLength}位`)
      return
    }

    const url = userForm.value.id 
      ? `http://localhost:8080/api/users/${userForm.value.id}`
      : 'http://localhost:8080/api/register'
    const method = userForm.value.id ? 'PUT' : 'POST'
    
    // 创建/更新用户的请求体
    const requestBody = {
      username: userForm.value.username
    }
    
    // 只在添加用户时包含密码字段
    if (!userForm.value.id) {
      requestBody.password = userForm.value.password
    }
    
    // 创建/更新用户
    const res = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      credentials: 'include',
      body: JSON.stringify(requestBody)
    })
    
    const data = await res.json()
    if (data.code === 0) {
      // 如果是添加用户，需要分配角色
      if (!userForm.value.id && userForm.value.roles.length > 0) {
        const userId = data.data.id
        // 为新用户分配角色
        for (const roleId of userForm.value.roles) {
          await fetch(`http://127.0.0.1:8080/api/roles/assign?userId=${userId}&roleId=${roleId}`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'Accept': 'application/json'
            },
            credentials: 'include'
          })
        }
      }
      // 如果是编辑用户，更新角色
      else if (userForm.value.id) {
        const userId = userForm.value.id
        const currentRoles = users.value
          .find(user => user.id === userId)
          ?.roles.map(role => role.id) || []
        
        // 需要添加的角色
        const rolesToAdd = userForm.value.roles.filter(roleId => !currentRoles.includes(roleId))
        // 需要移除的角色
        const rolesToRemove = currentRoles.filter(roleId => !userForm.value.roles.includes(roleId))

        // 添加新角色
        for (const roleId of rolesToAdd) {
          await fetch(`http://127.0.0.1:8080/api/roles/assign?userId=${userId}&roleId=${roleId}`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'Accept': 'application/json'
            },
            credentials: 'include'
          })
        }

        // 移除旧角色
        for (const roleId of rolesToRemove) {
          await fetch(`http://127.0.0.1:8080/api/roles/remove?userId=${userId}&roleId=${roleId}`, {
            method: 'DELETE',
            headers: {
              'Content-Type': 'application/json',
              'Accept': 'application/json'
            },
            credentials: 'include'
          })
        }
      }

      ElMessage.success(userForm.value.id ? '更新成功' : '添加成功')
      dialogVisible.value = false
      // 立即重新获取用户列表以更新角色信息
      await getUsers()
    } else {
      ElMessage.error(data.message || (userForm.value.id ? '更新失败' : '添加失败'))
    }
  } catch (error) {
    console.error('提交表单失败:', error)
    ElMessage.error(userForm.value.id ? '更新失败' : '添加失败')
  }
}

onMounted(async () => {
  await Promise.all([
    getUsers(),
    getAllRoles() // 在组件挂载时获取角色列表
  ])
})
</script>

<style scoped>
.user-manage {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.role-tag {
  margin-right: 5px;
}
</style> 