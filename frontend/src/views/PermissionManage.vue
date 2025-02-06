<template>
  <div class="permission-manage">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>权限管理</span>
          <div class="header-buttons">
            <el-button 
              type="danger" 
              :disabled="!selectedPermissions.length" 
              @click="handleBatchDelete"
              v-permission="'MANAGE_PERMISSIONS'"
            >批量删除</el-button>
            <el-button 
              type="primary" 
              @click="handleAdd"
              v-permission="'MANAGE_PERMISSIONS'"
            >添加权限</el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="权限名称">
          <el-input v-model="searchForm.permissionName" placeholder="请输入权限名称" clearable />
        </el-form-item>
        <el-form-item label="权限编码">
          <el-input v-model="searchForm.permissionCode" placeholder="请输入权限编码" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="true" />
            <el-option label="禁用" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 权限列表 -->
      <el-tabs v-model="activeTab" class="permission-tabs">
        <el-tab-pane 
          v-for="type in permissionTypes" 
          :key="type.value"
          :label="type.label"
          :name="type.value"
        >
          <el-table 
            :data="groupedPermissions[type.value]" 
            style="width: 100%"
            @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" />
            <el-table-column prop="permissionName" label="权限名称" />
            <el-table-column prop="permissionCode" label="权限编码" />
            <el-table-column prop="description" label="描述" />
            <el-table-column prop="typeDescription" label="类型描述" />
            <el-table-column prop="order" label="排序" width="80" />
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="scope.row.status ? 'success' : 'danger'">
                  {{ scope.row.status ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button 
                  size="small" 
                  @click="handleEdit(scope.row)"
                  v-permission="'MANAGE_PERMISSIONS'"
                >编辑</el-button>
                <el-button 
                  size="small" 
                  type="danger" 
                  @click="handleDelete(scope.row)"
                  v-permission="'MANAGE_PERMISSIONS'"
                >删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑权限对话框 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="dialogVisible" 
      width="500px">
      <el-form 
        :model="permissionForm"
        :rules="rules" 
        ref="permissionFormRef"
        label-width="100px">
        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="permissionForm.permissionName" />
        </el-form-item>
        <el-form-item label="权限编码" prop="permissionCode">
          <el-input v-model="permissionForm.permissionCode" />
        </el-form-item>
        <el-form-item label="权限类型" prop="type">
          <el-select 
            v-model="permissionForm.type" 
            placeholder="请选择权限类型"
            @change="handleTypeChange">
            <el-option 
              v-for="type in permissionTypes"
              :key="type.value"
              :label="type.label"
              :value="type.value">
              <span>{{ type.label }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">
                {{ type.description }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="父权限" prop="parentId">
          <el-select 
            v-model="permissionForm.parentId" 
            placeholder="请选择父权限"
            clearable>
            <el-option
              v-for="perm in permissionStore.permissions"
              :key="perm.id"
              :label="perm.permissionName"
              :value="perm.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="order">
          <el-input-number 
            v-model="permissionForm.order"
            :min="0"
            :max="999"
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input 
            v-model="permissionForm.description" 
            type="textarea" 
            :rows="2" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="permissionForm.status" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { usePermissionStore } from '../stores/permission'
import { hasPermission } from '../utils/auth'

// 获取权限store
const permissionStore = usePermissionStore()
// 获取router实例
const router = useRouter()

// 权限列表数据
const selectedPermissions = ref([])
const currentPage = ref(1)
const pageSize = ref(10)

// 搜索表单数据
const searchForm = ref({
  permissionName: '',
  permissionCode: '',
  status: ''
})

// 对话框相关数据
const dialogVisible = ref(false)
const dialogTitle = ref('添加权限')
const permissionFormRef = ref(null)

// 权限类型定义
const permissionTypes = [
  { value: 'menu', label: '菜单权限', description: '控制菜单项的显示和访问' },
  { value: 'operation', label: '操作权限', description: '控制按钮等操作的权限' },
  { value: 'data', label: '数据权限', description: '控制数据的访问和修改权限' },
  { value: 'api', label: 'API权限', description: '控制接口的访问权限' }
]

// 修改表单数据
const permissionForm = ref({
  id: null,
  permissionName: '',
  permissionCode: '',
  type: 'menu',
  typeDescription: '',
  description: '',
  status: true,
  parentId: null, // 父权限ID，用于权限继承
  order: 0 // 显示顺序
})

// 修改验证规则
const rules = {
  permissionName: [
    { required: true, message: '请输入权限名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  permissionCode: [
    { required: true, message: '请输入权限编码', trigger: 'blur' },
    { pattern: /^[A-Z_]+$/, message: '权限编码只能包含大写字母和下划线', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择权限类型', trigger: 'change' }
  ],
  description: [
    { max: 200, message: '描述不能超过200个字符', trigger: 'blur' }
  ],
  order: [
    { type: 'number', message: '请输入正确的排序号', trigger: 'blur' }
  ]
}

// 计算过滤后的权限列表
const filteredPermissions = computed(() => {
  let result = permissionStore.permissions

  if (searchForm.value.permissionName) {
    result = result.filter(item => 
      item.permissionName.toLowerCase().includes(searchForm.value.permissionName.toLowerCase())
    )
  }

  if (searchForm.value.permissionCode) {
    result = result.filter(item =>
      item.permissionCode.toLowerCase().includes(searchForm.value.permissionCode.toLowerCase())
    )
  }

  if (searchForm.value.status !== '') {
    result = result.filter(item => item.status === searchForm.value.status)
  }

  return result.slice(
    (currentPage.value - 1) * pageSize.value,
    currentPage.value * pageSize.value
  )
})

// 计算总数
const total = computed(() => {
  let result = permissionStore.permissions

  if (searchForm.value.permissionName) {
    result = result.filter(item => 
      item.permissionName.toLowerCase().includes(searchForm.value.permissionName.toLowerCase())
    )
  }

  if (searchForm.value.permissionCode) {
    result = result.filter(item =>
      item.permissionCode.toLowerCase().includes(searchForm.value.permissionCode.toLowerCase())
    )
  }

  if (searchForm.value.status !== '') {
    result = result.filter(item => item.status === searchForm.value.status)
  }

  return result.length
})

// 搜索
const handleSearch = () => {
  currentPage.value = 1
}

// 重置搜索
const handleReset = () => {
  searchForm.value = {
    permissionName: '',
    permissionCode: '',
    status: ''
  }
  currentPage.value = 1
}

// 处理多选
const handleSelectionChange = (selection) => {
  selectedPermissions.value = selection
}

// 批量删除
const handleBatchDelete = () => {
  if (!selectedPermissions.value.length) {
    ElMessage.warning('请选择要删除的权限')
    return
  }
  
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedPermissions.value.length} 个权限吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    selectedPermissions.value.forEach(permission => {
      permissionStore.removePermission(permission.permissionCode)
    })
    ElMessage.success('删除成功')
  })
}

// 添加权限
const handleAdd = () => {
  permissionForm.value = {
    id: null,
    permissionName: '',
    permissionCode: '',
    type: 'menu',
    typeDescription: '',
    description: '',
    status: true,
    parentId: null,
    order: 0
  }
  dialogTitle.value = '添加权限'
  dialogVisible.value = true
}

// 编辑权限
const handleEdit = (row) => {
  permissionForm.value = { ...row }
  dialogTitle.value = '编辑权限'
  dialogVisible.value = true
}

// 删除权限
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除该权限吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    permissionStore.removePermission(row.permissionCode)
    ElMessage.success('删除成功')
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!permissionFormRef.value) return
  
  try {
    await permissionFormRef.value.validate()
    
    if (permissionForm.value.id) {
      // 编辑权限
      const index = permissionStore.permissions.findIndex(p => p.id === permissionForm.value.id)
      if (index > -1) {
        permissionStore.permissions[index] = { ...permissionForm.value }
      }
    } else {
      // 添加权限
      permissionStore.addPermission({
        id: Date.now(), // 生成临时ID
        ...permissionForm.value
      })
    }
    
    ElMessage.success(permissionForm.value.id ? '更新成功' : '添加成功')
    dialogVisible.value = false
  } catch (error) {
    if (error.name === 'ValidationError') {
      ElMessage.error('请检查表单填写是否正确')
    } else {
      console.error('提交表单失败:', error)
      ElMessage.error('操作失败，请重试')
    }
  }
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

// 当前页改变
const handleCurrentChange = (val) => {
  currentPage.value = val
}

// 检查权限
const checkPermission = (permission) => {
  return hasPermission(permission)
}

// 按类型分组的权限列表
const groupedPermissions = computed(() => {
  const groups = {}
  permissionTypes.forEach(type => {
    groups[type.value] = filteredPermissions.value.filter(p => p.type === type.value)
  })
  return groups
})

// 更新权限类型时的处理
const handleTypeChange = (value) => {
  const selectedType = permissionTypes.find(t => t.value === value)
  if (selectedType) {
    permissionForm.value.typeDescription = selectedType.description
  }
}

onMounted(() => {
  // 检查是否有权限访问
  if (!checkPermission('VIEW_PERMISSIONS')) {
    ElMessage.error('您没有权限访问此页面')
    router.push('/403')
    return
  }
})
</script>

<style scoped>
.permission-manage {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.search-form {
  margin-bottom: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.permission-tabs {
  margin-top: 20px;
}

.permission-tabs :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

.permission-type-description {
  color: #8492a6;
  font-size: 13px;
  margin-top: 5px;
}
</style> 