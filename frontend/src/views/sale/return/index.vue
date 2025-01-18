<template>
  <div class="sale-return">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>退货管理</span>
          <el-button type="primary" @click="handleAdd">新增退货</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" class="search-form" inline>
        <el-form-item label="退货单号">
          <el-input v-model="searchForm.returnNo" placeholder="请输入退货单号" clearable />
        </el-form-item>
        <el-form-item label="客户名称">
          <el-input v-model="searchForm.customerName" placeholder="请输入客户名称" clearable />
        </el-form-item>
        <el-form-item label="退货状态">
          <el-select v-model="searchForm.status" placeholder="请选择退货状态" clearable>
            <el-option label="待审核" value="pending" />
            <el-option label="已审核" value="approved" />
            <el-option label="已入库" value="received" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="returnNo" label="退货单号" width="180" />
        <el-table-column prop="customerName" label="客户名称" width="180" />
        <el-table-column prop="totalAmount" label="退货金额" width="150">
          <template #default="scope">
            {{ scope.row.totalAmount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="退货状态" width="100">
          <template #default="scope">
            <el-tag :type="getReturnStatusType(scope.row.status)">
              {{ getReturnStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="250">
          <template #default="scope">
            <el-button link type="primary" @click="handleView(scope.row)">查看</el-button>
            <el-button v-if="scope.row.status === 'pending'" link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button v-if="scope.row.status === 'pending'" link type="primary" @click="handleApprove(scope.row)">审核</el-button>
            <el-button v-if="scope.row.status === 'approved'" link type="primary" @click="handleReceive(scope.row)">入库</el-button>
            <el-button v-if="scope.row.status === 'pending'" link type="danger" @click="handleCancel(scope.row)">取消</el-button>
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
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
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
  returnNo: '',
  customerName: '',
  status: ''
})

// 表格数据
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 获取退货单列表
const getReturnList = async () => {
  try {
    const offset = (currentPage.value - 1) * pageSize.value
    const params = new URLSearchParams({
      offset: offset.toString(),
      limit: pageSize.value.toString(),
      returnNo: searchForm.returnNo || '',
      customerName: searchForm.customerName || '',
      status: searchForm.status || ''
    })
    
    const response = await fetch(`/api/sale/returns?${params.toString()}`, {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0) {
      tableData.value = result.data.list || []
      total.value = result.data.total || 0
    } else {
      ElMessage.error(result.message || '获取退货单列表失败')
    }
  } catch (error) {
    console.error('获取退货单列表失败:', error)
    ElMessage.error('获取退货单列表失败')
  }
}

// 获取退货状态类型
const getReturnStatusType = (status) => {
  const types = {
    pending: 'warning',
    approved: 'success',
    received: 'primary',
    completed: 'success',
    cancelled: 'info'
  }
  return types[status] || 'info'
}

// 获取退货状态标签
const getReturnStatusLabel = (status) => {
  const labels = {
    pending: '待审核',
    approved: '已审核',
    received: '已入库',
    completed: '已完成',
    cancelled: '已取消'
  }
  return labels[status] || '未知'
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getReturnList()
}

// 重置搜索
const handleReset = () => {
  searchForm.returnNo = ''
  searchForm.customerName = ''
  searchForm.status = ''
  handleSearch()
}

// 新增退货
const handleAdd = () => {
  router.push('/dashboard/sale/return/form')
}

// 查看退货单
const handleView = (row) => {
  router.push({
    path: '/dashboard/sale/return/form',
    query: { id: row.id, mode: 'view' }
  })
}

// 编辑退货单
const handleEdit = (row) => {
  router.push({
    path: '/dashboard/sale/return/form',
    query: { id: row.id, mode: 'edit' }
  })
}

// 审核退货单
const handleApprove = async (row) => {
  try {
    const params = new URLSearchParams({ status: 'approved' })
    const response = await fetch(`/api/sale/returns/${row.id}/status?${params.toString()}`, {
      method: 'PUT',
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0) {
      ElMessage.success('退货单审核成功')
      getReturnList()
    } else {
      ElMessage.error(result.message || '退货单审核失败')
    }
  } catch (error) {
    console.error('退货单审核失败:', error)
    ElMessage.error('退货单审核失败')
  }
}

// 入库
const handleReceive = async (row) => {
  try {
    const params = new URLSearchParams({ status: 'received' })
    const response = await fetch(`/api/sale/returns/${row.id}/status?${params.toString()}`, {
      method: 'PUT',
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0) {
      ElMessage.success('退货入库成功')
      getReturnList()
    } else {
      ElMessage.error(result.message || '退货入库失败')
    }
  } catch (error) {
    console.error('退货入库失败:', error)
    ElMessage.error('退货入库失败')
  }
}

// 取消退货单
const handleCancel = (row) => {
  ElMessageBox.confirm('确认取消该退货单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const params = new URLSearchParams({ status: 'cancelled' })
      const response = await fetch(`/api/sale/returns/${row.id}/status?${params.toString()}`, {
        method: 'PUT',
        headers: {
          'Accept': 'application/json'
        },
        credentials: 'include'
      })
      const result = await response.json()
      if (result.code === 0) {
        ElMessage.success('退货单已取消')
        getReturnList()
      } else {
        ElMessage.error(result.message || '取消退货单失败')
      }
    } catch (error) {
      console.error('取消退货单失败:', error)
      ElMessage.error('取消退货单失败')
    }
  }).catch(() => {
    // 用户点击取消按钮
  })
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  getReturnList()
}

// 当前页改变
const handleCurrentChange = (val) => {
  currentPage.value = val
  getReturnList()
}

// 初始化
onMounted(() => {
  getReturnList()
})
</script>

<style scoped>
.sale-return {
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