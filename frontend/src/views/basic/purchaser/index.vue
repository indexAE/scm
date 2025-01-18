<template>
  <div class="purchaser-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>采购员管理</span>
          <el-button type="primary" @click="handleAdd">新增采购员</el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable></el-input>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" value="enabled"></el-option>
            <el-option label="禁用" value="disabled"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格区域 -->
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="姓名" width="120"></el-table-column>
        <el-table-column prop="phone" label="手机号" width="150"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="200"></el-table-column>
        <el-table-column prop="department" label="部门" width="150"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'enabled' ? 'success' : 'danger'">
              {{ scope.row.status === 'enabled' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" type="primary" plain @click="handleEdit(scope.row)">编辑</el-button>
            <el-button 
              size="small" 
              :type="scope.row.status === 'enabled' ? 'danger' : 'success'"
              plain
              @click="handleToggleStatus(scope.row)"
            >
              {{ scope.row.status === 'enabled' ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页区域 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        >
        </el-pagination>
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogType === 'add' ? '新增采购员' : '编辑采购员'"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="部门" prop="department">
          <el-input v-model="form.department" placeholder="请输入部门"></el-input>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 搜索表单数据
const searchForm = reactive({
  name: '',
  phone: '',
  status: ''
})

// 表格数据
const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add') // 'add' 或 'edit'
const formRef = ref(null)
const form = reactive({
  id: undefined,
  name: '',
  phone: '',
  email: '',
  department: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请输入部门', trigger: 'blur' }
  ]
}

// 获取采购员列表
const getPurchaserList = async () => {
  loading.value = true
  try {
    const offset = (currentPage.value - 1) * pageSize.value
    const params = new URLSearchParams({
      name: searchForm.name,
      phone: searchForm.phone,
      status: searchForm.status,
      offset: offset.toString(),
      limit: pageSize.value.toString()
    })
    
    console.log('Request params:', {
      name: searchForm.name,
      phone: searchForm.phone,
      status: searchForm.status,
      offset: offset,
      limit: pageSize.value
    })
    
    const response = await fetch(`/api/purchasers?${params.toString()}`)
    const result = await response.json()
    console.log('Response:', result)
    
    if (result.code === 0) {
      tableData.value = result.data?.list || []
      total.value = result.data?.total || 0
    } else {
      ElMessage.error(result.message || '获取采购员列表失败')
    }
  } catch (error) {
    console.error('获取采购员列表失败:', error)
    ElMessage.error('获取采购员列表失败')
  } finally {
    loading.value = false
  }
}

// 处理查询
const handleSearch = () => {
  currentPage.value = 1
  getPurchaserList()
}

// 重置查询
const resetSearch = () => {
  searchForm.name = ''
  searchForm.phone = ''
  searchForm.status = ''
  handleSearch()
}

// 处理新增
const handleAdd = () => {
  dialogType.value = 'add'
  form.id = undefined
  form.name = ''
  form.phone = ''
  form.email = ''
  form.department = ''
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row) => {
  dialogType.value = 'edit'
  form.id = row.id
  form.name = row.name
  form.phone = row.phone
  form.email = row.email
  form.department = row.department
  dialogVisible.value = true
}

// 处理切换状态
const handleToggleStatus = (row) => {
  const newStatus = row.status === 'enabled' ? 'disabled' : 'enabled'
  const actionText = newStatus === 'enabled' ? '启用' : '禁用'
  
  ElMessageBox.confirm(
    `确认${actionText}该采购员？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const response = await fetch(`/api/purchasers/${row.id}/status`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ status: newStatus })
      })
      
      const result = await response.json()
      if (result.code === 0) {
        ElMessage.success(`${actionText}成功`)
        getPurchaserList()
      } else {
        ElMessage.error(result.message || `${actionText}失败`)
      }
    } catch (error) {
      console.error(`${actionText}采购员失败:`, error)
      ElMessage.error(`${actionText}失败`)
    }
  }).catch(() => {})
}

// 处理提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const url = dialogType.value === 'add' 
          ? '/api/purchasers'
          : `/api/purchasers/${form.id}`
        
        const method = dialogType.value === 'add' ? 'POST' : 'PUT'
        const response = await fetch(url, {
          method,
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(form)
        })

        const result = await response.json()
        if (result.code === 0) {
          ElMessage.success(dialogType.value === 'add' ? '新增成功' : '更新成功')
          dialogVisible.value = false
          getPurchaserList()
        } else {
          ElMessage.error(result.message || (dialogType.value === 'add' ? '新增失败' : '更新失败'))
        }
      } catch (error) {
        console.error(dialogType.value === 'add' ? '新增采购员失败:' : '更新采购员失败:', error)
        ElMessage.error(dialogType.value === 'add' ? '新增失败' : '更新失败')
      }
    }
  })
}

// 处理分页大小变化
const handleSizeChange = (val) => {
  pageSize.value = val
  getPurchaserList()
}

// 处理页码变化
const handleCurrentChange = (val) => {
  currentPage.value = val
  getPurchaserList()
}

onMounted(() => {
  getPurchaserList()
})
</script>

<style scoped>
.purchaser-container {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 