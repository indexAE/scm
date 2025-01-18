<template>
  <div class="finance-account">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>账户管理</span>
          <el-button type="primary" @click="handleAdd">新增账户</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" class="search-form" inline>
        <el-form-item label="账户名称">
          <el-input v-model="searchForm.name" placeholder="请输入账户名称" clearable />
        </el-form-item>
        <el-form-item label="账户类型">
          <el-select v-model="searchForm.type" placeholder="请选择账户类型" clearable>
            <el-option label="现金" value="cash" />
            <el-option label="银行卡" value="bank" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="账户状态">
          <el-select v-model="searchForm.status" placeholder="请选择账户状态" clearable>
            <el-option label="正常" value="normal" />
            <el-option label="冻结" value="frozen" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="tableData" border style="width: 100%">
        <el-table-column prop="name" label="账户名称" />
        <el-table-column prop="type" label="账户类型" width="120">
          <template #default="{ row }">
            {{ getAccountType(row.type) }}
          </template>
        </el-table-column>
        <el-table-column prop="balance" label="账户余额" width="150">
          <template #default="{ row }">
            ¥{{ row.balance }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
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
            <el-button v-if="row.status === 'normal'" link type="primary" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button v-if="row.status === 'normal'" link type="danger" @click="handleFreeze(row)">
              冻结
            </el-button>
            <el-button v-if="row.status === 'frozen'" link type="success" @click="handleUnfreeze(row)">
              解冻
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
  name: '',
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
      name: searchForm.name,
      type: searchForm.type,
      status: searchForm.status
    }
    const response = await fetch(`http://127.0.0.1:8080/api/finance/accounts?${new URLSearchParams(params)}`, {
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
      ElMessage.error(result.message || '获取账户列表失败')
    }
  } catch (error) {
    console.error('获取账户列表失败:', error)
    ElMessage.error('获取账户列表失败')
  } finally {
    loading.value = false
  }
}

// 获取账户类型文本
const getAccountType = (type) => {
  const types = {
    cash: '现金',
    bank: '银行卡',
    other: '其他'
  }
  return types[type] || type
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    normal: 'success',
    frozen: 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    normal: '正常',
    frozen: '冻结'
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
  router.push('/dashboard/finance/account/form')
}

// 查看
const handleView = (row) => {
  router.push(`/dashboard/finance/account/form?id=${row.id}&mode=view`)
}

// 编辑
const handleEdit = (row) => {
  router.push(`/dashboard/finance/account/form?id=${row.id}&mode=edit`)
}

// 冻结
const handleFreeze = async (row) => {
  try {
    await ElMessageBox.confirm('确认要冻结该账户吗？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await fetch(`http://127.0.0.1:8080/api/finance/accounts/${row.id}/status?status=frozen`, {
      method: 'PUT',
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    
    const result = await response.json()
    if (result.code === 0) {
      ElMessage.success('账户已冻结')
      getList()
    } else {
      ElMessage.error(result.message || '冻结账户失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('冻结账户失败:', error)
      ElMessage.error('冻结账户失败')
    }
  }
}

// 解冻
const handleUnfreeze = async (row) => {
  try {
    await ElMessageBox.confirm('确认要解冻该账户吗？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await fetch(`http://127.0.0.1:8080/api/finance/accounts/${row.id}/status?status=normal`, {
      method: 'PUT',
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    
    const result = await response.json()
    if (result.code === 0) {
      ElMessage.success('账户已解冻')
      getList()
    } else {
      ElMessage.error(result.message || '解冻账户失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('解冻账户失败:', error)
      ElMessage.error('解冻账户失败')
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
.finance-account {
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