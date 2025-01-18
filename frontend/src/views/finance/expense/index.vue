<template>
  <div class="finance-expense">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>支出管理</span>
          <el-button type="primary" @click="handleAdd">新增支出</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" class="search-form" inline>
        <el-form-item label="支出编号">
          <el-input v-model="searchForm.expenseNo" placeholder="请输入支出编号" clearable />
        </el-form-item>
        <el-form-item label="支出类型">
          <el-select v-model="searchForm.type" placeholder="请选择支出类型" clearable>
            <el-option label="采购支出" value="purchase" />
            <el-option label="其他支出" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="支出状态">
          <el-select v-model="searchForm.status" placeholder="请选择支出状态" clearable>
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
      <el-table v-loading="loading" :data="tableData" border style="width: 100%">
        <el-table-column prop="expenseNo" label="支出编号" width="180" />
        <el-table-column prop="type" label="支出类型" width="120">
          <template #default="{ row }">
            {{ row.type === 'purchase' ? '采购支出' : '其他支出' }}
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="支出金额" width="120">
          <template #default="{ row }">
            ¥{{ row.amount }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-if="row.status === 'pending'" link type="primary" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button v-if="row.status === 'pending'" link type="success" @click="handleConfirm(row)">
              确认
            </el-button>
            <el-button v-if="row.status === 'pending'" link type="danger" @click="handleCancel(row)">
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="limit"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
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
  expenseNo: '',
  type: '',
  status: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const page = ref(1)
const limit = ref(10)
const total = ref(0)

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      limit: limit.value,
      expenseNo: searchForm.expenseNo,
      type: searchForm.type,
      status: searchForm.status
    }
    const response = await fetch(`http://127.0.0.1:8080/api/finance/expenses?${new URLSearchParams(params)}`, {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0) {
      tableData.value = result.data.list
      total.value = result.data.total
    } else {
      ElMessage.error(result.message || '获取支出列表失败')
    }
  } catch (error) {
    console.error('获取支出列表失败:', error)
    ElMessage.error('获取支出列表失败')
  } finally {
    loading.value = false
  }
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    pending: 'warning',
    confirmed: 'success',
    cancelled: 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    pending: '待确认',
    confirmed: '已确认',
    cancelled: '已取消'
  }
  return texts[status] || status
}

// 搜索
const handleSearch = () => {
  page.value = 1
  getList()
}

// 重置
const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  router.push('/dashboard/finance/expense/form')
}

// 查看
const handleView = (row) => {
  router.push(`/dashboard/finance/expense/form?id=${row.id}&mode=view`)
}

// 编辑
const handleEdit = (row) => {
  router.push(`/dashboard/finance/expense/form?id=${row.id}&mode=edit`)
}

// 确认
const handleConfirm = async (row) => {
  try {
    await ElMessageBox.confirm('确认要确认这笔支出吗？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await fetch(`http://127.0.0.1:8080/api/finance/expenses/${row.id}/status?status=confirmed`, {
      method: 'PUT',
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    
    const result = await response.json()
    if (result.code === 0) {
      ElMessage.success('支出已确认')
      getList()
    } else {
      ElMessage.error(result.message || '确认支出失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认支出失败:', error)
      ElMessage.error('确认支出失败')
    }
  }
}

// 取消
const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确认要取消这笔支出吗？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await fetch(`http://127.0.0.1:8080/api/finance/expenses/${row.id}/status?status=cancelled`, {
      method: 'PUT',
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    
    const result = await response.json()
    if (result.code === 0) {
      ElMessage.success('支出已取消')
      getList()
    } else {
      ElMessage.error(result.message || '取消支出失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消支出失败:', error)
      ElMessage.error('取消支出失败')
    }
  }
}

// 分页大小改变
const handleSizeChange = (val) => {
  limit.value = val
  page.value = 1
  getList()
}

// 当前页改变
const handleCurrentChange = (val) => {
  page.value = val
  getList()
}

// 初始化
onMounted(() => {
  getList()
})
</script>

<style scoped>
.finance-expense {
  padding: 20px;
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