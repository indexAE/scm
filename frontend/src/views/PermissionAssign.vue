<template>
  <div class="permission-assign">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>权限分配</span>
        </div>
      </template>

      <!-- 分配类型选择 -->
      <el-radio-group v-model="assignType" class="assign-type">
        <el-radio-button label="role">角色权限</el-radio-button>
        <el-radio-button label="user">用户权限</el-radio-button>
      </el-radio-group>

      <div class="assign-container">
        <!-- 左侧列表 -->
        <div class="left-panel">
          <div class="panel-header">
            {{ assignType === 'role' ? '角色列表' : '用户列表' }}
          </div>
          <div class="panel-content">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索"
              prefix-icon="Search"
              clearable
              @clear="handleSearch"
              @input="handleSearch"
            />
            <el-scrollbar height="500px">
              <el-menu
                :default-active="selectedId"
                @select="handleSelect"
              >
                <el-menu-item
                  v-for="item in filteredList"
                  :key="item.id"
                  :index="item.id"
                >
                  <span>{{ item.name }}</span>
                </el-menu-item>
              </el-menu>
            </el-scrollbar>
          </div>
        </div>

        <!-- 右侧权限树 -->
        <div class="right-panel">
          <div class="panel-header">
            <span>权限配置</span>
            <div class="header-actions">
              <el-button 
                type="primary" 
                size="small"
                @click="handleSave"
                v-permission="'MANAGE_PERMISSIONS'"
              >保存</el-button>
            </div>
          </div>
          <div class="panel-content">
            <el-scrollbar height="500px">
              <el-tree
                ref="permissionTree"
                :data="permissionTree"
                :props="defaultProps"
                show-checkbox
                node-key="id"
                :default-checked-keys="checkedPermissions"
                @check="handleCheckChange"
              >
                <template #default="{ node, data }">
                  <span class="custom-tree-node">
                    <span>{{ data.permissionName }}</span>
                    <span class="permission-code">{{ data.permissionCode }}</span>
                  </span>
                </template>
              </el-tree>
            </el-scrollbar>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { usePermissionStore } from '../stores/permission'
import { hasPermission } from '../utils/auth'

// 获取权限store
const permissionStore = usePermissionStore()

// 分配类型
const assignType = ref('role')
const searchKeyword = ref('')
const selectedId = ref('')

// 模拟角色和用户数据
const roleList = ref([
  { id: '1', name: '管理员' },
  { id: '2', name: '普通用户' },
  { id: '3', name: '访客' }
])

const userList = ref([
  { id: '1', name: '张三' },
  { id: '2', name: '李四' },
  { id: '3', name: '王五' }
])

// 过滤列表
const filteredList = computed(() => {
  const list = assignType.value === 'role' ? roleList.value : userList.value
  if (!searchKeyword.value) return list
  
  return list.filter(item => 
    item.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
  )
})

// 权限树配置
const defaultProps = {
  children: 'children',
  label: 'permissionName'
}

// 构建权限树
const buildPermissionTree = (permissions) => {
  const tree = []
  const map = {}
  
  // 首先创建所有节点的映射
  permissions.forEach(permission => {
    map[permission.id] = {
      ...permission,
      children: []
    }
  })
  
  // 构建树结构
  permissions.forEach(permission => {
    const node = map[permission.id]
    if (permission.parentId) {
      const parent = map[permission.parentId]
      if (parent) {
        parent.children.push(node)
      } else {
        tree.push(node)
      }
    } else {
      tree.push(node)
    }
  })
  
  return tree
}

// 计算权限树
const permissionTree = computed(() => {
  return buildPermissionTree(permissionStore.permissions)
})

// 当前选中项的权限
const checkedPermissions = ref([])

// 处理搜索
const handleSearch = () => {
  // 实现搜索逻辑
}

// 处理选择
const handleSelect = (id) => {
  selectedId.value = id
  // 获取选中项的权限
  const permissions = assignType.value === 'role' 
    ? permissionStore.rolePermissions[id] || []
    : permissionStore.userPermissions
  checkedPermissions.value = permissions
}

// 处理权限变更
const handleCheckChange = (data, checked) => {
  // 更新选中的权限
  const tree = ref(null)
  const checkedNodes = tree.value.getCheckedNodes()
  checkedPermissions.value = checkedNodes.map(node => node.id)
}

// 保存权限配置
const handleSave = () => {
  if (!selectedId.value) {
    ElMessage.warning('请先选择要配置的对象')
    return
  }
  
  if (assignType.value === 'role') {
    // 保存角色权限
    permissionStore.updateRolePermissions(selectedId.value, checkedPermissions.value)
  } else {
    // 保存用户权限
    permissionStore.updateUserPermissions(selectedId.value, checkedPermissions.value)
  }
  
  ElMessage.success('保存成功')
}

// 监听分配类型变化
watch(assignType, () => {
  selectedId.value = ''
  checkedPermissions.value = []
})
</script>

<style scoped>
.permission-assign {
  padding: 20px;
}

.assign-type {
  margin-bottom: 20px;
}

.assign-container {
  display: flex;
  gap: 20px;
  margin-top: 20px;
}

.left-panel,
.right-panel {
  flex: 1;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
}

.panel-header {
  padding: 12px;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #f5f7fa;
}

.panel-content {
  padding: 12px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.custom-tree-node {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.permission-code {
  color: #909399;
  font-size: 12px;
}

:deep(.el-scrollbar__wrap) {
  overflow-x: hidden;
}

:deep(.el-menu) {
  border-right: none;
}

:deep(.el-menu-item) {
  height: 40px;
  line-height: 40px;
}

:deep(.el-input) {
  margin-bottom: 12px;
}
</style> 