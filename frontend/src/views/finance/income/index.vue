<template>
  <div class="finance-income">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>收入管理</span>
          <el-button type="primary" @click="handleAdd">新增收入</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" class="search-form" inline>
        <el-form-item label="收入编号">
          <el-input v-model="searchForm.incomeNo" placeholder="请输入收入编号" clearable />
        </el-form-item>
        <el-form-item label="收入类型">
          <el-select v-model="searchForm.type" placeholder="请选择收入类型" clearable>
            <el-option label="销售收入" value="sale" />
            <el-option label="其他收入" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待确认" value="pending" />
            <el-option label="已确认" value="confirmed" />
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
        <el-table-column prop="incomeNo" label="收入编号" width="180" />
        <el-table-column prop="type" label="收入类型" width="120">
          <template #default="scope">
            <el-tag :type="getIncomeTypeType(scope.row.type)">
              {{ getIncomeTypeLabel(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="150">
          <template #default="scope">
            {{ scope.row.amount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getIncomeStatusType(scope.row.status)">
              {{ getIncomeStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="scope">
            <el-button link type="primary" @click="handleView(scope.row)">查看</el-button>
            <el-button v-if="scope.row.status === 'pending'" link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button v-if="scope.row.status === 'pending'" link type="success" @click="handleConfirm(scope.row)">确认</el-button>
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

// 搜索表单数据
const searchForm = reactive({
  incomeNo: '',
  type: '',
  status: ''
})

// 表格数据
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 获取收入类型样式
const getIncomeTypeType = (type) => {
  const types = {
    sale: 'success',
    other: 'info'
  }
  return types[type] || 'info'
}

// 获取收入类型标签
const getIncomeTypeLabel = (type) => {
  const labels = {
    sale: '销售收入',
    other: '其他收入'
  }
  return labels[type] || '未知'
}

// 获取状态样式
const getIncomeStatusType = (status) => {
  const types = {
    pending: 'warning',
    confirmed: 'success',
    cancelled: 'info'
  }
  return types[status] || 'info'
}

// 获取状态标签
const getIncomeStatusLabel = (status) => {
  const labels = {
    pending: '待确认',
    confirmed: '已确认',
    cancelled: '已取消'
  }
  return labels[status] || '未知'
}

// 获取收入列表
const getIncomeList = async () => {
  try {
    const offset = (currentPage.value - 1) * pageSize.value
    const params = new URLSearchParams({
      offset: offset.toString(),
      limit: pageSize.value.toString(),
      incomeNo: searchForm.incomeNo || '',
      type: searchForm.type || '',
      status: searchForm.status || ''
    })
    
    const response = await fetch(`http://127.0.0.1:8080/api/finance/incomes?${params.toString()}`, {
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
      ElMessage.error(result.message || '获取收入列表失败')
    }
  } catch (error) {
    console.error('获取收入列表失败:', error)
    ElMessage.error('获取收入列表失败')
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getIncomeList()
}

// 重置搜索
const handleReset = () => {
  searchForm.incomeNo = ''
  searchForm.type = ''
  searchForm.status = ''
  handleSearch()
}

// 处理新增
const handleAdd = () => {
  router.push('/dashboard/finance/income/form')
}

// 处理查看
const handleView = (row) => {
  router.push({
    path: '/dashboard/finance/income/form',
    query: {
      id: row.id,
      mode: 'view'
    }
  })
}

// 处理编辑
const handleEdit = (row) => {
  router.push({
    path: '/dashboard/finance/income/form',
    query: {
      id: row.id,
      mode: 'edit'
    }
  })
}

// 处理确认
const handleConfirm = async (row) => {
  try {
    const params = new URLSearchParams({ status: 'confirmed' })
    const response = await fetch(`http://127.0.0.1:8080/api/finance/incomes/${row.id}/status?${params.toString()}`, {
      method: 'PUT',
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0) {
      ElMessage.success('收入确认成功')
      getIncomeList()
    } else {
      ElMessage.error(result.message || '收入确认失败')
    }
  } catch (error) {
    console.error('收入确认失败:', error)
    ElMessage.error('收入确认失败')
  }
}

// 处理取消
const handleCancel = (row) => {
  ElMessageBox.confirm('确认取消该收入记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const params = new URLSearchParams({ status: 'cancelled' })
      const response = await fetch(`http://127.0.0.1:8080/api/finance/incomes/${row.id}/status?${params.toString()}`, {
        method: 'PUT',
        headers: {
          'Accept': 'application/json'
        },
        credentials: 'include'
      })
      const result = await response.json()
      if (result.code === 0) {
        ElMessage.success('收入取消成功')
        getIncomeList()
      } else {
        ElMessage.error(result.message || '收入取消失败')
      }
    } catch (error) {
      console.error('收入取消失败:', error)
      ElMessage.error('收入取消失败')
    }
  })
}

// 处理分页大小变化
const handleSizeChange = (val) => {
  pageSize.value = val
  getIncomeList()
}

// 处理页码变化
const handleCurrentChange = (val) => {
  currentPage.value = val
  getIncomeList()
}

// 初始化
onMounted(() => {
  getIncomeList()
})
</script>

<style scoped>
.finance-income {
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