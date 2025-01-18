<template>
  <div class="role-manage">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <div class="header-buttons">
            <el-button type="danger" :disabled="!selectedRoles.length" @click="handleBatchDelete">批量删除</el-button>
            <el-button type="primary" @click="handleAdd">添加角色</el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="角色名称">
          <el-input v-model="searchForm.roleName" placeholder="请输入角色名称" clearable />
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input v-model="searchForm.roleCode" placeholder="请输入角色编码" clearable />
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
      
      <!-- 角色列表 -->
      <el-table 
        :data="roles" 
        style="width: 100%"
        @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="roleCode" label="角色编码" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status ? 'success' : 'danger'">
              {{ scope.row.status ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
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

    <!-- 添加/编辑角色对话框 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="dialogVisible" 
      width="500px">
      <el-form 
        :model="roleForm"
        :rules="rules" 
        ref="roleFormRef"
        label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="roleForm.roleCode" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input 
            v-model="roleForm.description" 
            type="textarea" 
            :rows="2" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="roleForm.status" />
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 角色列表数据
const roles = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const selectedRoles = ref([])

// 搜索表单数据
const searchForm = ref({
  roleName: '',
  roleCode: '',
  status: ''
})

// 对话框相关数据
const dialogVisible = ref(false)
const dialogTitle = ref('添加角色')
const roleFormRef = ref(null)
const roleForm = ref({
  id: null,
  roleName: '',
  roleCode: '',
  description: '',
  status: true
})

// 表单验证规则
const rules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { pattern: /^[A-Z_]+$/, message: '角色编码只能包含大写字母和下划线', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '描述不能超过200个字符', trigger: 'blur' }
  ]
}

// 获取角色列表
const getRoles = async () => {
  try {
    // 构建查询参数
    const params = new URLSearchParams({
      page: currentPage.value,
      size: pageSize.value
    })
    
    if (searchForm.value.roleName) {
      params.append('roleName', searchForm.value.roleName)
    }
    if (searchForm.value.roleCode) {
      params.append('roleCode', searchForm.value.roleCode)
    }
    if (searchForm.value.status !== '') {
      params.append('status', searchForm.value.status)
    }
    
    const res = await fetch(`http://localhost:8000/api/roles?${params.toString()}`, {
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    })
    const data = await res.json()
    if (data.code === 0) {
      roles.value = data.data.list || []
      total.value = data.data.total || 0
    } else {
      ElMessage.error(data.message || '获取角色列表失败')
    }
  } catch (error) {
    console.error('获取角色列表失败:', error)
    ElMessage.error('网络错误,请稍后重试')
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getRoles()
}

// 重置搜索
const handleReset = () => {
  searchForm.value = {
    roleName: '',
    roleCode: '',
    status: ''
  }
  currentPage.value = 1
  getRoles()
}

// 处理多选
const handleSelectionChange = (selection) => {
  selectedRoles.value = selection
}

// 批量删除
const handleBatchDelete = () => {
  if (!selectedRoles.value.length) {
    ElMessage.warning('请选择要删除的角色')
    return
  }
  
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedRoles.value.length} 个角色吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const deletePromises = selectedRoles.value.map(role => 
        fetch(`http://localhost:8000/api/roles/${role.id}`, {
          method: 'DELETE',
          credentials: 'include'
        }).then(res => res.json())
      )
      
      const results = await Promise.all(deletePromises)
      const hasError = results.some(result => result.code !== 0)
      
      if (hasError) {
        ElMessage.warning('部分角色删除失败')
      } else {
        ElMessage.success('删除成功')
      }
      getRoles()
    } catch (error) {
      console.error('批量删除失败:', error)
      ElMessage.error('网络错误,请稍后重试')
    }
  })
}

// 添加角色
const handleAdd = () => {
  roleForm.value = {
    id: null,
    roleName: '',
    roleCode: '',
    description: '',
    status: true
  }
  dialogTitle.value = '添加角色'
  dialogVisible.value = true
}

// 编辑角色
const handleEdit = (row) => {
  roleForm.value = { ...row }
  dialogTitle.value = '编辑角色'
  dialogVisible.value = true
}

// 删除角色
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除该角色吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await fetch(`http://localhost:8000/api/roles/${row.id}`, {
        method: 'DELETE',
        credentials: 'include'
      })
      const data = await res.json()
      if (data.code === 0) {
        ElMessage.success('删除成功')
        getRoles()
      } else {
        ElMessage.error(data.message || '删除失败')
      }
    } catch (error) {
      console.error('删除角色失败:', error)
      ElMessage.error('网络错误,请稍后重试')
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!roleFormRef.value) return
  
  try {
    await roleFormRef.value.validate()
    
    const url = roleForm.value.id 
      ? `http://localhost:8000/api/roles/${roleForm.value.id}`
      : 'http://localhost:8000/api/roles'
    const method = roleForm.value.id ? 'PUT' : 'POST'
    
    const res = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include',
      body: JSON.stringify(roleForm.value)
    })
    
    const data = await res.json()
    if (data.code === 0) {
      ElMessage.success(roleForm.value.id ? '更新成功' : '添加成功')
      dialogVisible.value = false
      getRoles()
    } else {
      ElMessage.error(data.message || (roleForm.value.id ? '更新失败' : '添加失败'))
    }
  } catch (error) {
    if (error.name === 'ValidationError') {
      ElMessage.error('请检查表单填写是否正确')
    } else {
      console.error('提交表单失败:', error)
      ElMessage.error('网络错误,请稍后重试')
    }
  }
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  getRoles()
}

// 当前页改变
const handleCurrentChange = (val) => {
  currentPage.value = val
  getRoles()
}

onMounted(() => {
  getRoles()
})
</script>

<style scoped>
.role-manage {
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
</style> 