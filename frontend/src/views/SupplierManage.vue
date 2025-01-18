<template>
  <div class="supplier-manage">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>供应商管理</span>
          <el-button type="primary" @click="handleAdd">添加供应商</el-button>
        </div>
      </template>
      
      <!-- 供应商列表 -->
      <el-table :data="suppliers" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="供应商名称" />
        <el-table-column prop="contact" label="联系人" />
        <el-table-column prop="phone" label="联系电话" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="address" label="地址" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              @change="(val) => handleStatusChange(scope.row.id, val)"
            />
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
    </el-card>

    <!-- 添加/编辑供应商对话框 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="dialogVisible" 
      width="500px">
      <el-form 
        :model="supplierForm" 
        label-width="100px">
        <el-form-item label="供应商名称">
          <el-input v-model="supplierForm.name" />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="supplierForm.contact" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="supplierForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="supplierForm.email" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input 
            v-model="supplierForm.address" 
            type="textarea" 
            :rows="2" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input 
            v-model="supplierForm.remark" 
            type="textarea" 
            :rows="2" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="supplierForm.status" />
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

// 供应商列表数据
const suppliers = ref([])

// 对话框相关数据
const dialogVisible = ref(false)
const dialogTitle = ref('添加供应商')
const supplierForm = ref({
  id: null,
  name: '',
  contact: '',
  phone: '',
  email: '',
  address: '',
  remark: '',
  status: true
})

// 获取供应商列表
const getSuppliers = async () => {
  try {
    const res = await fetch('http://localhost:8000/api/suppliers', {
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    })
    const data = await res.json()
    if (data.code === 0) {
      suppliers.value = data.data
    } else {
      ElMessage.error(data.message || '获取供应商列表失败')
    }
  } catch (error) {
    console.error('获取供应商列表失败:', error)
    ElMessage.error('获取供应商列表失败')
  }
}

// 添加供应商
const handleAdd = () => {
  supplierForm.value = {
    id: null,
    name: '',
    contact: '',
    phone: '',
    email: '',
    address: '',
    remark: '',
    status: true
  }
  dialogTitle.value = '添加供应商'
  dialogVisible.value = true
}

// 编辑供应商
const handleEdit = (row) => {
  supplierForm.value = { ...row }
  dialogTitle.value = '编辑供应商'
  dialogVisible.value = true
}

// 删除供应商
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除该供应商吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await fetch(`http://localhost:8000/api/suppliers/${row.id}`, {
        method: 'DELETE',
        credentials: 'include'
      })
      const data = await res.json()
      if (data.code === 0) {
        ElMessage.success('删除成功')
        getSuppliers()
      } else {
        ElMessage.error(data.message || '删除失败')
      }
    } catch (error) {
      console.error('删除供应商失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 更新供应商状态
const handleStatusChange = async (id, status) => {
  try {
    const res = await fetch(`http://localhost:8000/api/suppliers/${id}/status?status=${status}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    })
    const data = await res.json()
    if (data.code === 0) {
      ElMessage.success('状态更新成功')
    } else {
      ElMessage.error(data.message || '状态更新失败')
      // 更新失败时恢复状态
      getSuppliers()
    }
  } catch (error) {
    console.error('更新供应商状态失败:', error)
    ElMessage.error('状态更新失败')
    // 更新失败时恢复状态
    getSuppliers()
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    const url = supplierForm.value.id 
      ? `http://localhost:8000/api/suppliers/${supplierForm.value.id}`
      : 'http://localhost:8000/api/suppliers'
    const method = supplierForm.value.id ? 'PUT' : 'POST'
    
    const res = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include',
      body: JSON.stringify(supplierForm.value)
    })
    
    const data = await res.json()
    if (data.code === 0) {
      ElMessage.success(supplierForm.value.id ? '更新成功' : '添加成功')
      dialogVisible.value = false
      getSuppliers()
    } else {
      ElMessage.error(data.message || (supplierForm.value.id ? '更新失败' : '添加失败'))
    }
  } catch (error) {
    console.error('提交表单失败:', error)
    ElMessage.error(supplierForm.value.id ? '更新失败' : '添加失败')
  }
}

onMounted(() => {
  getSuppliers()
})
</script>

<style scoped>
.supplier-manage {
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
</style> 