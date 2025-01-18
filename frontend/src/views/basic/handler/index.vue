<template>
  <div class="handler-list">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>经办人管理</span>
          <el-button type="primary" @click="handleAdd">新增经办人</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="编号">
          <el-input v-model="searchForm.code" placeholder="请输入编号" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" value="enabled" />
            <el-option label="禁用" value="disabled" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="code" label="编号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="phone" label="联系电话" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'enabled' ? 'success' : 'danger'">
              {{ scope.row.status === 'enabled' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button
              v-if="scope.row.status === 'disabled'"
              type="success"
              size="small"
              @click="handleStatusChange(scope.row)"
            >
              启用
            </el-button>
            <el-button
              v-else
              type="danger"
              size="small"
              @click="handleStatusChange(scope.row)"
            >
              禁用
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑经办人' : '新增经办人'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="2"
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

// 搜索表单数据
const searchForm = reactive({
  code: '',
  name: '',
  status: ''
})

// 表格数据
const tableData = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

// 获取列表数据
const getList = async () => {
  try {
    const offset = (page.value - 1) * size.value
    const params = new URLSearchParams({
      code: searchForm.code,
      name: searchForm.name,
      status: searchForm.status,
      offset: offset.toString(),
      limit: size.value.toString()
    })
    
    console.log('Request params:', {
      code: searchForm.code,
      name: searchForm.name,
      status: searchForm.status,
      offset,
      limit: size.value
    })
    
    const response = await fetch(`/api/handlers?${params}`)
    const result = await response.json()
    console.log('Response:', result)
    
    if (result.code === 0) {
      tableData.value = result.data?.list || []
      total.value = result.data?.total || 0
    } else {
      ElMessage.error(result.message || '获取列表失败')
    }
  } catch (error) {
    console.error('获取列表失败:', error)
    ElMessage.error('获取列表失败')
  }
}

// 搜索
const handleSearch = () => {
  page.value = 1
  getList()
}

// 重置搜索
const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  page.value = 1
  getList()
}

// 分页大小变化
const handleSizeChange = (val) => {
  size.value = val
  getList()
}

// 当前页变化
const handleCurrentChange = (val) => {
  page.value = val
  getList()
}

// 表单数据
const formRef = ref(null)
const form = reactive({
  name: '',
  phone: '',
  email: '',
  remark: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 对话框显示状态
const dialogVisible = ref(false)
const isEdit = ref(false)

// 新增
const handleAdd = () => {
  isEdit.value = false
  Object.keys(form).forEach(key => {
    form[key] = ''
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  Object.keys(form).forEach(key => {
    form[key] = row[key]
  })
  form.id = row.id
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const url = isEdit.value ? `/api/handlers/${form.id}` : '/api/handlers'
        const method = isEdit.value ? 'PUT' : 'POST'
        const response = await fetch(url, {
          method,
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(form)
        })
        
        const result = await response.json()
        if (result.code === 0) {
          ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
          dialogVisible.value = false
          getList()
        } else {
          ElMessage.error(result.message || (isEdit.value ? '更新失败' : '创建失败'))
        }
      } catch (error) {
        console.error(isEdit.value ? '更新失败:' : '创建失败:', error)
        ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
      }
    }
  })
}

// 修改状态
const handleStatusChange = async (row) => {
  const newStatus = row.status === 'enabled' ? 'disabled' : 'enabled'
  try {
    const response = await fetch(`/api/handlers/${row.id}/status?status=${newStatus}`, {
      method: 'PUT'
    })
    const result = await response.json()
    if (result.code === 0) {
      ElMessage.success(`状态${newStatus === 'enabled' ? '启用' : '禁用'}成功`)
      row.status = newStatus
      await getList()
    }
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error('更新状态失败')
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.handler-list {
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