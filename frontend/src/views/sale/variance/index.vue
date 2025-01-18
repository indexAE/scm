<template>
  <div class="sale-variance">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>销售差异</span>
          <el-button type="primary" @click="handleAdd">新增差异</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" class="search-form" inline>
        <el-form-item label="差异单号">
          <el-input v-model="searchForm.varianceNo" placeholder="请输入差异单号" clearable />
        </el-form-item>
        <el-form-item label="客户名称">
          <el-input v-model="searchForm.customerName" placeholder="请输入客户名称" clearable />
        </el-form-item>
        <el-form-item label="差异类型">
          <el-select v-model="searchForm.type" placeholder="请选择差异类型" clearable>
            <el-option label="数量差异" value="quantity" />
            <el-option label="金额差异" value="amount" />
            <el-option label="其他差异" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="searchForm.status" placeholder="请选择处理状态" clearable>
            <el-option label="待处理" value="pending" />
            <el-option label="处理中" value="processing" />
            <el-option label="已处理" value="completed" />
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
        <el-table-column prop="varianceNo" label="差异单号" width="180" />
        <el-table-column prop="customerName" label="客户名称" width="180" />
        <el-table-column prop="type" label="差异类型" width="100">
          <template #default="scope">
            <el-tag :type="getVarianceTypeType(scope.row.type)">
              {{ getVarianceTypeLabel(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="差异金额" width="150">
          <template #default="scope">
            {{ scope.row.amount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="处理状态" width="100">
          <template #default="scope">
            <el-tag :type="getVarianceStatusType(scope.row.status)">
              {{ getVarianceStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="250">
          <template #default="scope">
            <el-button link type="primary" @click="handleView(scope.row)">查看</el-button>
            <el-button v-if="scope.row.status === 'pending'" link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button v-if="scope.row.status === 'pending'" link type="primary" @click="handleProcess(scope.row)">处理</el-button>
            <el-button v-if="scope.row.status === 'processing'" link type="success" @click="handleComplete(scope.row)">完成</el-button>
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
  varianceNo: '',
  customerName: '',
  type: '',
  status: ''
})

// 表格数据
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 获取差异单列表
const getVarianceList = async () => {
  try {
    const offset = (currentPage.value - 1) * pageSize.value
    const params = new URLSearchParams({
      offset: offset.toString(),
      limit: pageSize.value.toString(),
      varianceNo: searchForm.varianceNo || '',
      customerName: searchForm.customerName || '',
      type: searchForm.type || '',
      status: searchForm.status || ''
    })
    
    const response = await fetch(`http://127.0.0.1:8080/api/sale/variances?${params.toString()}`, {
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
      ElMessage.error(result.message || '获取差异单列表失败')
    }
  } catch (error) {
    console.error('获取差异单列表失败:', error)
    ElMessage.error('获取差异单列表失败')
  }
}

// 获取差异类型样式
const getVarianceTypeType = (type) => {
  const types = {
    quantity: 'warning',
    amount: 'danger',
    other: 'info'
  }
  return types[type] || 'info'
}

// 获取差异类型标签
const getVarianceTypeLabel = (type) => {
  const labels = {
    quantity: '数量差异',
    amount: '金额差异',
    other: '其他差异'
  }
  return labels[type] || '未知'
}

// 获取处理状态样式
const getVarianceStatusType = (status) => {
  const types = {
    pending: 'warning',
    processing: 'primary',
    completed: 'success',
    cancelled: 'info'
  }
  return types[status] || 'info'
}

// 获取处理状态标签
const getVarianceStatusLabel = (status) => {
  const labels = {
    pending: '待处理',
    processing: '处理中',
    completed: '已处理',
    cancelled: '已取消'
  }
  return labels[status] || '未知'
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getVarianceList()
}

// 重置搜索
const handleReset = () => {
  searchForm.varianceNo = ''
  searchForm.customerName = ''
  searchForm.type = ''
  searchForm.status = ''
  handleSearch()
}

// 处理新增
const handleAdd = () => {
  router.push('/dashboard/sale/variance/form')
}

// 处理查看
const handleView = (row) => {
  router.push({
    path: '/dashboard/sale/variance/form',
    query: {
      id: row.id,
      mode: 'view'
    }
  })
}

// 处理编辑
const handleEdit = (row) => {
  router.push({
    path: '/dashboard/sale/variance/form',
    query: {
      id: row.id,
      mode: 'edit'
    }
  })
}

// 处理差异
const handleProcess = async (row) => {
  try {
    const params = new URLSearchParams({ status: 'processing' })
    const response = await fetch(`http://127.0.0.1:8080/api/sale/variances/${row.id}/status?${params.toString()}`, {
      method: 'PUT',
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0) {
      ElMessage.success('差异单处理成功')
      getVarianceList()
    } else {
      ElMessage.error(result.message || '差异单处理失败')
    }
  } catch (error) {
    console.error('差异单处理失败:', error)
    ElMessage.error('差异单处理失败')
  }
}

// 完成处理
const handleComplete = async (row) => {
  try {
    const params = new URLSearchParams({ status: 'completed' })
    const response = await fetch(`http://127.0.0.1:8080/api/sale/variances/${row.id}/status?${params.toString()}`, {
      method: 'PUT',
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0) {
      ElMessage.success('差异单处理完成')
      getVarianceList()
    } else {
      ElMessage.error(result.message || '差异单处理完成失败')
    }
  } catch (error) {
    console.error('差异单处理完成失败:', error)
    ElMessage.error('差异单处理完成失败')
  }
}

// 取消差异单
const handleCancel = (row) => {
  ElMessageBox.confirm('确认取消该差异单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const params = new URLSearchParams({ status: 'cancelled' })
      const response = await fetch(`http://127.0.0.1:8080/api/sale/variances/${row.id}/status?${params.toString()}`, {
        method: 'PUT',
        headers: {
          'Accept': 'application/json'
        },
        credentials: 'include'
      })
      const result = await response.json()
      if (result.code === 0) {
        ElMessage.success('差异单取消成功')
        getVarianceList()
      } else {
        ElMessage.error(result.message || '差异单取消失败')
      }
    } catch (error) {
      console.error('差异单取消失败:', error)
      ElMessage.error('差异单取消失败')
    }
  }).catch(() => {})
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  getVarianceList()
}

// 当前页改变
const handleCurrentChange = (val) => {
  currentPage.value = val
  getVarianceList()
}

// 初始化
onMounted(() => {
  getVarianceList()
})
</script>

<style scoped>
.sale-variance {
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