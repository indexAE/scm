<template>
  <div class="inbound-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>采购入库管理</span>
          <el-button type="primary" @click="handleAdd">新建入库</el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="入库单号">
          <el-input v-model="searchForm.code" placeholder="请输入入库单号" clearable></el-input>
        </el-form-item>
        <el-form-item label="关联订单">
          <el-input v-model="searchForm.orderCode" placeholder="请输入订单编号" clearable></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待入库" value="pending"></el-option>
            <el-option label="已入库" value="completed"></el-option>
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
        <el-table-column prop="code" label="入库单号" width="180"></el-table-column>
        <el-table-column prop="orderCode" label="关联订单" width="180"></el-table-column>
        <el-table-column prop="supplier" label="供应商"></el-table-column>
        <el-table-column prop="warehouse" label="入库仓库"></el-table-column>
        <el-table-column prop="handler" label="经办人"></el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间"></el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="scope">
            <div class="operation-buttons">
              <el-button size="small" @click="handleView(scope.row)">查看</el-button>
              <el-button 
                size="small" 
                type="primary" 
                @click="handleEdit(scope.row)" 
                v-if="scope.row.status === 'pending'"
              >编辑</el-button>
              <el-button 
                size="small" 
                type="success" 
                @click="handleComplete(scope.row)"
                v-if="scope.row.status === 'pending'"
              >入库</el-button>
              <el-button 
                size="small" 
                type="danger" 
                @click="handleCancel(scope.row)"
                v-if="scope.row.status === 'pending'"
              >取消</el-button>
            </div>
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
  orderCode: '',
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
    completed: 'success',
    cancelled: 'danger'
  }
  return map[status]
}

// 获取状态文本
const getStatusText = (status) => {
  const map = {
    pending: '待入库',
    completed: '已入库',
    cancelled: '已取消'
  }
  return map[status]
}

// 处理查询
const handleSearch = async () => {
  loading.value = true
  try {
    const offset = (currentPage.value - 1) * pageSize.value
    const params = new URLSearchParams({
      code: searchForm.code,
      orderCode: searchForm.orderCode,
      status: searchForm.status,
      offset: offset.toString(),
      limit: pageSize.value.toString()
    })
    
    const res = await fetch(`http://127.0.0.1:8080/api/purchase/inbounds?${params.toString()}`, {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    console.log('入库单列表API响应:', res)
    const data = await res.json()
    console.log('入库单列表数据:', data)
    
    if (data.code === 0) {
      tableData.value = data.data?.list || []
      total.value = data.data?.total || 0
    } else {
      console.error('获取入库单列表失败:', data.message)
      ElMessage.error(data.message || '获取入库单列表失败')
    }
  } catch (error) {
    console.error('获取入库单列表失败:', error)
    ElMessage.error('获取入库单列表失败')
  } finally {
    loading.value = false
  }
}

// 重置查询
const resetSearch = () => {
  searchForm.code = ''
  searchForm.orderCode = ''
  searchForm.status = ''
  handleSearch()
}

// 处理新增
const handleAdd = () => {
  router.push('/dashboard/purchase/inbound/form')
}

// 处理查看
const handleView = (row) => {
  router.push({
    path: '/dashboard/purchase/inbound/form',
    query: {
      id: row.id,
      mode: 'view'
    }
  })
}

// 处理编辑
const handleEdit = (row) => {
  router.push({
    path: '/dashboard/purchase/inbound/form',
    query: {
      id: row.id,
      mode: 'edit'
    }
  })
}

// 处理确认入库
const handleComplete = (row) => {
  ElMessageBox.confirm(
    `确认入库单 ${row.code}？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await fetch(`http://127.0.0.1:8080/api/purchase/inbounds/${row.id}/status`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify({ status: 'completed' })
      })
      console.log('入库确认API响应:', res)
      const data = await res.json()
      console.log('入库确认结果:', data)
      
      if (data.code === 0) {
        ElMessage.success('入库成功')
        handleSearch()
      } else {
        console.error('入库失败:', data.message)
        ElMessage.error(data.message || '入库失败')
      }
    } catch (error) {
      console.error('入库失败:', error)
      ElMessage.error('入库失败')
    }
  }).catch(() => {})
}

// 处理取消
const handleCancel = (row) => {
  ElMessageBox.confirm(
    `确认取消入库单 ${row.code}？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await fetch(`http://127.0.0.1:8080/api/purchase/inbounds/${row.id}/status`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify({ status: 'cancelled' })
      })
      console.log('取消入库API响应:', res)
      const data = await res.json()
      console.log('取消入库结果:', data)
      
      if (data.code === 0) {
        ElMessage.success('入库单已取消')
        handleSearch()
      } else {
        console.error('取消失败:', data.message)
        ElMessage.error(data.message || '取消失败')
      }
    } catch (error) {
      console.error('取消入库单失败:', error)
      ElMessage.error('取消失败')
    }
  }).catch(() => {})
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

// 在组件挂载时获取数据
onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.inbound-container {
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

.operation-buttons {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
}

.operation-buttons .el-button {
  margin-left: 0;
}
</style> 