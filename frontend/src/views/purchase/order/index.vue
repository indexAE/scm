<template>
  <div class="order-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>采购订单管理</span>
          <el-button type="primary" @click="handleAdd">新建订单</el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="订单编号">
          <el-input v-model="searchForm.code" placeholder="请输入订单编号" clearable></el-input>
        </el-form-item>
        <el-form-item label="供应商">
          <el-input v-model="searchForm.supplier" placeholder="请输入供应商" clearable></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待确认" value="pending"></el-option>
            <el-option label="已确认" value="confirmed"></el-option>
            <el-option label="已完成" value="completed"></el-option>
            <el-option label="已取消" value="cancelled"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格区域 -->
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="code" label="订单编号" width="180"></el-table-column>
        <el-table-column prop="supplier" label="供应商" width="180"></el-table-column>
        <el-table-column prop="amount" label="订单金额"></el-table-column>
        <el-table-column prop="purchaser" label="采购员"></el-table-column>
        <el-table-column prop="expectedDeliveryDate" label="预计交付日期"></el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间"></el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="scope">
            <el-button 
              type="primary" 
              plain 
              size="small" 
              @click="handleView(scope.row)"
            >查看</el-button>
            <el-button 
              v-if="scope.row.status === 'pending'"
              type="success" 
              plain 
              size="small" 
              @click="handleEdit(scope.row)"
            >编辑</el-button>
            <el-button 
              v-if="scope.row.status === 'pending'"
              type="warning" 
              plain 
              size="small" 
              @click="handleConfirm(scope.row)"
            >确认</el-button>
            <el-button 
              v-if="scope.row.status === 'pending'"
              type="danger" 
              plain 
              size="small" 
              @click="handleCancel(scope.row)"
            >取消</el-button>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()

// 搜索表单数据
const searchForm = reactive({
  code: '',
  supplier: '',
  status: ''
})

// 表格数据
const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 获取状态类型
const getStatusType = (status) => {
  const map = {
    pending: '',
    confirmed: 'success',
    completed: 'info',
    cancelled: 'danger'
  }
  return map[status]
}

// 获取状态文本
const getStatusText = (status) => {
  const map = {
    pending: '待确认',
    confirmed: '已确认',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[status]
}

// 获取采购订单列表
const handleSearch = async () => {
  loading.value = true
  try {
    const offset = (currentPage.value - 1) * pageSize.value
    const params = new URLSearchParams({
      code: searchForm.code,
      supplier: searchForm.supplier,
      status: searchForm.status,
      offset: offset.toString(),
      limit: pageSize.value.toString()
    })
    console.log('请求参数:', {
      code: searchForm.code,
      supplier: searchForm.supplier,
      status: searchForm.status,
      offset,
      limit: pageSize.value
    })
    
    const res = await fetch(`http://127.0.0.1:8080/api/purchase/orders?${params.toString()}`, {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    console.log('API响应状态:', res.status)
    const data = await res.json()
    console.log('API响应内容:', data)
    
    if (data.code === 0) {
      console.log('设置表格数据:', data.data.list)
      console.log('设置总数:', data.data.total)
      console.log('表格数据设置前:', tableData.value)
      tableData.value = data.data.list || []
      console.log('表格数据设置后:', tableData.value)
      total.value = data.data.total || 0
    } else {
      console.error('API返回错误:', data.message)
      ElMessage.error(data.message || '获取采购订单列表失败')
    }
  } catch (error) {
    console.error('获取采购订单列表失败:', error)
    ElMessage.error('获取采购订单列表失败')
  } finally {
    loading.value = false
  }
}

// 重置查询
const resetSearch = () => {
  searchForm.code = ''
  searchForm.supplier = ''
  searchForm.status = ''
  handleSearch()
}

// 处理新增
const handleAdd = () => {
  router.push('/dashboard/purchase/order/form')
}

// 处理查看
const handleView = (row) => {
  router.push({
    path: '/dashboard/purchase/order/form',
    query: {
      id: row.id,
      type: 'view'
    }
  })
}

// 处理编辑
const handleEdit = (row) => {
  router.push({
    path: '/dashboard/purchase/order/form',
    query: {
      id: row.id,
      type: 'edit'
    }
  })
}

// 处理确认
const handleConfirm = async (row) => {
  try {
    await ElMessageBox.confirm('确认要确认该采购订单吗？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await fetch(`http://127.0.0.1:8080/api/purchase/orders/${row.id}/status`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      credentials: 'include',
      body: JSON.stringify({ status: 'confirmed' })
    })
    
    const data = await res.json()
    if (data.code === 0) {
      ElMessage.success('订单已确认')
      handleSearch()
    } else {
      ElMessage.error(data.message || '确认失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认采购订单失败:', error)
      ElMessage.error('确认失败')
    }
  }
}

// 处理取消
const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确认要取消该采购订单吗？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await fetch(`http://127.0.0.1:8080/api/purchase/orders/${row.id}/status`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      credentials: 'include',
      body: JSON.stringify({ status: 'cancelled' })
    })
    
    const data = await res.json()
    if (data.code === 0) {
      ElMessage.success('订单已取消')
      handleSearch()
    } else {
      ElMessage.error(data.message || '取消失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消采购订单失败:', error)
      ElMessage.error('取消失败')
    }
  }
}

// 处理分页大小变化
const handleSizeChange = (val) => {
  pageSize.value = val
  handleSearch()
}

// 处理页码变化
const handleCurrentChange = (val) => {
  currentPage.value = val
  handleSearch()
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.order-container {
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
</style> 