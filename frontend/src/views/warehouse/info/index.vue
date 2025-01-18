<template>
  <div class="warehouse-info">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>仓库信息管理</span>
          <el-button type="primary" @click="handleAdd">新增仓库</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="仓库名称">
          <el-input v-model="searchForm.name" placeholder="请输入仓库名称" clearable />
        </el-form-item>
        <el-form-item label="仓库状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" value="normal" />
            <el-option label="停用" value="disabled" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="code" label="仓库编号" width="120" />
        <el-table-column prop="name" label="仓库名称" width="150" />
        <el-table-column prop="address" label="仓库地址" />
        <el-table-column prop="manager" label="仓库管理员" width="120" />
        <el-table-column prop="contact" label="联系电话" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'normal' ? 'success' : 'danger'">
              {{ scope.row.status === 'normal' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="primary" link @click="handleDelete(scope.row)">删除</el-button>
            <el-button 
              type="primary" 
              link 
              @click="handleStatusChange(scope.row)"
            >
              {{ scope.row.status === 'normal' ? '停用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>

      <!-- 新增仓库对话框 -->
      <el-dialog
        v-model="dialogVisible"
        :title="isEdit ? '编辑仓库' : '新增仓库'"
        width="500px"
      >
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
        >
          <el-form-item label="仓库编号" prop="code">
            <el-input v-model="form.code" placeholder="请输入仓库编号" :disabled="isEdit" />
          </el-form-item>
          <el-form-item label="仓库名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入仓库名称" />
          </el-form-item>
          <el-form-item label="仓库地址" prop="address">
            <el-input v-model="form.address" placeholder="请输入仓库地址" />
          </el-form-item>
          <el-form-item label="管理员" prop="manager">
            <el-input v-model="form.manager" placeholder="请输入管理员姓名" />
          </el-form-item>
          <el-form-item label="联系电话" prop="contact">
            <el-input v-model="form.contact" placeholder="请输入联系电话" />
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input
              v-model="form.remark"
              type="textarea"
              :rows="3"
              placeholder="请输入备注信息"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSubmit">确定</el-button>
          </span>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 搜索表单数据
const searchForm = reactive({
  name: '',
  status: ''
})

// 表格数据
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框显示控制
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

// 表单数据
const form = reactive({
  code: '',
  name: '',
  address: '',
  manager: '',
  contact: '',
  remark: '',
  status: 'normal'
})

// 表单验证规则
const rules = {
  code: [
    { required: true, message: '请输入仓库编号', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9]+$/, message: '仓库编号只能包含字母和数字', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入仓库名称', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入仓库地址', trigger: 'blur' }
  ],
  manager: [
    { required: true, message: '请输入管理员姓名', trigger: 'blur' }
  ],
  contact: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

// 获取仓库列表
const getWarehouseList = async () => {
  try {
    const offset = (currentPage.value - 1) * pageSize.value
    const params = new URLSearchParams({
      offset: offset.toString(),
      limit: pageSize.value.toString(),
      name: searchForm.name,
      status: searchForm.status
    })
    
    const res = await fetch(`http://127.0.0.1:8080/api/warehouses?${params.toString()}`, {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    console.log('仓库列表API响应:', res)
    const data = await res.json()
    console.log('仓库列表数据:', data)
    
    if (data.code === 0) {
      tableData.value = data.data?.list || []
      total.value = data.data?.total || 0
    } else {
      console.error('获取仓库列表失败:', data.message)
      ElMessage.error(data.message || '获取仓库列表失败')
    }
  } catch (error) {
    console.error('获取仓库列表失败:', error)
    ElMessage.error('获取仓库列表失败')
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getWarehouseList()
}

// 重置搜索
const handleReset = () => {
  searchForm.name = ''
  searchForm.status = ''
  handleSearch()
}

// 处理新增按钮点击
const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  form.code = ''
  form.name = ''
  form.address = ''
  form.manager = ''
  form.contact = ''
  form.remark = ''
  form.status = 'normal'
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const url = isEdit.value 
          ? `http://127.0.0.1:8080/api/warehouses/${form.id}`
          : 'http://127.0.0.1:8080/api/warehouses'
        
        const method = isEdit.value ? 'PUT' : 'POST'
        const res = await fetch(url, {
          method,
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          },
          credentials: 'include',
          body: JSON.stringify(form)
        })
        console.log('提交仓库表单API响应:', res)
        const data = await res.json()
        console.log('提交仓库表单结果:', data)

        if (data.code === 0) {
          ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
          dialogVisible.value = false
          getWarehouseList()
        } else {
          console.error(isEdit.value ? '更新失败:' : '创建失败:', data.message)
          ElMessage.error(data.message || (isEdit.value ? '更新失败' : '创建失败'))
        }
      } catch (error) {
        console.error(isEdit.value ? '更新仓库失败:' : '创建仓库失败:', error)
        ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
      }
    }
  })
}

// 处理编辑按钮点击
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

// 处理删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确认删除仓库 ${row.name}？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await fetch(`http://127.0.0.1:8080/api/warehouses/${row.id}`, {
        method: 'DELETE',
        headers: {
          'Accept': 'application/json'
        },
        credentials: 'include'
      })
      console.log('删除仓库API响应:', res)
      const data = await res.json()
      console.log('删除仓库结果:', data)
      
      if (data.code === 0) {
        ElMessage.success('删除成功')
        getWarehouseList()
      } else {
        console.error('删除失败:', data.message)
        ElMessage.error(data.message || '删除失败')
      }
    } catch (error) {
      console.error('删除仓库失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 处理状态变更
const handleStatusChange = (row) => {
  const newStatus = row.status === 'normal' ? 'disabled' : 'normal'
  const actionName = newStatus === 'normal' ? '启用' : '停用'
  
  ElMessageBox.confirm(
    `确认${actionName}仓库 ${row.name}？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      console.log('准备更新仓库状态:', {
        warehouseId: row.id,
        currentStatus: row.status,
        newStatus: newStatus
      })
      
      const res = await fetch(`http://127.0.0.1:8080/api/warehouses/${row.id}/status?status=${newStatus}`, {
        method: 'PUT',
        headers: {
          'Accept': 'application/json'
        },
        credentials: 'include'
      })
      console.log('更新仓库状态API响应:', res)
      const data = await res.json()
      console.log('更新仓库状态结果:', data)
      
      if (data.code === 0) {
        ElMessage.success(`${actionName}成功`)
        getWarehouseList()
      } else {
        console.error(`${actionName}失败:`, data.message)
        ElMessage.error(data.message || `${actionName}失败`)
      }
    } catch (error) {
      console.error(`${actionName}仓库失败:`, error)
      ElMessage.error(`${actionName}失败`)
    }
  }).catch(() => {})
}

// 处理分页大小变化
const handleSizeChange = (val) => {
  pageSize.value = val
  getWarehouseList()
}

// 处理页码变化
const handleCurrentChange = (val) => {
  currentPage.value = val
  getWarehouseList()
}

// 在组件挂载时获取数据
onMounted(() => {
  getWarehouseList()
})
</script>

<style scoped>
.warehouse-info {
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 