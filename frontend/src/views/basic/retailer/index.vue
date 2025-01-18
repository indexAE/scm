<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>零售商管理</span>
          <el-button type="primary" @click="handleAdd">新增零售商</el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="零售商名称">
          <el-input v-model="queryParams.name" placeholder="请输入零售商名称" clearable />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="queryParams.contact" placeholder="请输入联系人" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="true" />
            <el-option label="禁用" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格区域 -->
      <el-table v-loading="loading" :data="retailerList" style="width: 100%">
        <el-table-column type="index" label="序号" width="50" />
        <el-table-column prop="name" label="零售商名称" />
        <el-table-column prop="contact" label="联系人" />
        <el-table-column prop="phone" label="联系电话" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="businessType" label="经营类型" />
        <el-table-column prop="scale" label="规模" />
        <el-table-column prop="creditLimit" label="信用额度" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status ? 'success' : 'danger'">
              {{ scope.row.status ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="primary" link @click="handleStatus(scope.row)">
              {{ scope.row.status ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页区域 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 30, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        ref="retailerFormRef"
        :model="retailerForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="零售商名称" prop="name">
          <el-input v-model="retailerForm.name" placeholder="请输入零售商名称" />
        </el-form-item>
        <el-form-item label="联系人" prop="contact">
          <el-input v-model="retailerForm.contact" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="retailerForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="retailerForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="retailerForm.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="经营类型" prop="businessType">
          <el-select v-model="retailerForm.businessType" placeholder="请选择经营类型">
            <el-option label="综合零售" value="综合零售" />
            <el-option label="专业零售" value="专业零售" />
            <el-option label="连锁零售" value="连锁零售" />
          </el-select>
        </el-form-item>
        <el-form-item label="规模" prop="scale">
          <el-select v-model="retailerForm.scale" placeholder="请选择规模">
            <el-option label="大型" value="大型" />
            <el-option label="中型" value="中型" />
            <el-option label="小型" value="小型" />
          </el-select>
        </el-form-item>
        <el-form-item label="信用额度" prop="creditLimit">
          <el-input-number v-model="retailerForm.creditLimit" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="retailerForm.remark"
            type="textarea"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialog.visible = false">取 消</el-button>
          <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  contact: '',
  status: undefined
})

// 零售商列表数据
const retailerList = ref([])
const total = ref(0)
const loading = ref(false)

// 对话框数据
const dialog = reactive({
  title: '',
  visible: false
})

// 表单数据
const retailerFormRef = ref(null)
const retailerForm = reactive({
  id: undefined,
  name: '',
  contact: '',
  phone: '',
  email: '',
  address: '',
  businessType: '',
  scale: '',
  creditLimit: 0,
  remark: ''
})

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入零售商名称', trigger: 'blur' }],
  contact: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  businessType: [{ required: true, message: '请选择经营类型', trigger: 'change' }],
  scale: [{ required: true, message: '请选择规模', trigger: 'change' }]
}

// 获取零售商列表
const getList = async () => {
  loading.value = true
  try {
    const response = await fetch('/api/retailers')
    const result = await response.json()
    if (result.code === 0) {
      retailerList.value = result.data
      total.value = result.data.length
    } else {
      ElMessage.error(result.message || '获取零售商列表失败')
    }
  } catch (error) {
    console.error('获取零售商列表失败:', error)
    ElMessage.error('获取零售商列表失败')
  } finally {
    loading.value = false
  }
}

// 查询按钮点击事件
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置按钮点击事件
const resetQuery = () => {
  queryParams.name = ''
  queryParams.contact = ''
  queryParams.status = undefined
  handleQuery()
}

// 新增按钮点击事件
const handleAdd = () => {
  dialog.title = '新增零售商'
  dialog.visible = true
  Object.assign(retailerForm, {
    id: undefined,
    name: '',
    contact: '',
    phone: '',
    email: '',
    address: '',
    businessType: '',
    scale: '',
    creditLimit: 0,
    remark: ''
  })
}

// 编辑按钮点击事件
const handleEdit = (row) => {
  dialog.title = '编辑零售商'
  dialog.visible = true
  Object.assign(retailerForm, row)
}

// 提交表单
const handleSubmit = async () => {
  if (!retailerFormRef.value) return
  
  await retailerFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const url = retailerForm.id ? `/api/retailers/${retailerForm.id}` : '/api/retailers'
        const method = retailerForm.id ? 'PUT' : 'POST'
        
        const response = await fetch(url, {
          method,
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(retailerForm)
        })
        const result = await response.json()
        if (result.code === 0) {
          ElMessage.success(`${retailerForm.id ? '修改' : '新增'}成功`)
          dialog.visible = false
          getList()
        } else {
          ElMessage.error(result.message || '保存失败')
        }
      } catch (error) {
        console.error('保存失败:', error)
        ElMessage.error('保存失败')
      }
    }
  })
}

// 删除按钮点击事件
const handleDelete = (row) => {
  ElMessageBox.confirm('确认要删除该零售商吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await fetch(`/api/retailers/${row.id}`, {
        method: 'DELETE'
      })
      const result = await response.json()
      if (result.code === 0) {
        ElMessage.success('删除成功')
        getList()
      } else {
        ElMessage.error(result.message || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 状态切换事件
const handleStatus = async (row) => {
  try {
    const response = await fetch(`/api/retailers/${row.id}/status?status=${!row.status}`, {
      method: 'PUT'
    })
    const result = await response.json()
    if (result.code === 0) {
      ElMessage.success(`${row.status ? '禁用' : '启用'}成功`)
      getList()
    } else {
      ElMessage.error(result.message || '操作失败')
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 分页大小改变
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

// 页码改变
const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}

// 对话框关闭事件
const handleDialogClose = () => {
  if (retailerFormRef.value) {
    retailerFormRef.value.resetFields()
  }
}

// 页面加载时获取列表数据
onMounted(() => {
  getList()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style> 