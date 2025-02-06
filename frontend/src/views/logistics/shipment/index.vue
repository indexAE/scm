<template>
  <div class="logistics-shipment">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>发货管理</span>
          <el-button type="primary" @click="handleAdd">新增发货</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" class="search-form" inline>
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待发货" value="pending" />
            <el-option label="已发货" value="shipped" />
            <el-option label="已送达" value="delivered" />
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
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="consignee" label="收货人" width="120" />
        <el-table-column prop="consigneePhone" label="联系电话" width="140" />
        <el-table-column prop="consigneeAddress" label="收货地址" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-if="row.status === 'pending'" link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 'pending'" link type="primary" @click="handleShip(row)">发货</el-button>
            <el-button v-if="row.status === 'pending'" link type="danger" @click="handleCancel(row)">取消</el-button>
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
  orderNo: '',
  status: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const page = ref(1)
const limit = ref(10)
const total = ref(0)

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    pending: 'info',
    shipped: 'primary',
    delivered: 'success',
    cancelled: 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    pending: '待发货',
    shipped: '已发货',
    delivered: '已送达',
    cancelled: '已取消'
  }
  return texts[status] || status
}

// 获取发货单列表
const getList = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      limit: limit.value,
      ...searchForm
    }
    const response = await fetch(`/api/logistics/shipment/page?${new URLSearchParams(params)}`, {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0) {
      tableData.value = result.data.records
      total.value = result.data.total
    } else {
      ElMessage.error(result.message || '获取发货单列表失败')
    }
  } catch (error) {
    console.error('获取发货单列表失败:', error)
    ElMessage.error('获取发货单列表失败')
  } finally {
    loading.value = false
  }
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

// 新增发货
const handleAdd = () => {
  router.push('/dashboard/logistics/shipment/form?mode=add')
}

// 查看详情
const handleView = (row) => {
  router.push(`/dashboard/logistics/shipment/form?mode=view&id=${row.id}`)
}

// 编辑
const handleEdit = (row) => {
  router.push(`/dashboard/logistics/shipment/form?mode=edit&id=${row.id}`)
}

// 发货
const handleShip = async (row) => {
  try {
    const response = await fetch(`/api/logistics/shipment/${row.id}/ship`, {
      method: 'POST',
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0) {
      ElMessage.success('发货成功')
      getList()
    } else {
      ElMessage.error(result.message || '发货失败')
    }
  } catch (error) {
    console.error('发货失败:', error)
    ElMessage.error('发货失败')
  }
}

// 取消发货单
const handleCancel = (row) => {
  ElMessageBox.confirm('确定要取消该发货单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await fetch(`/api/logistics/shipment/${row.id}/cancel`, {
        method: 'POST',
        headers: {
          'Accept': 'application/json'
        },
        credentials: 'include'
      })
      const result = await response.json()
      if (result.code === 0) {
        ElMessage.success('取消成功')
        getList()
      } else {
        ElMessage.error(result.message || '取消失败')
      }
    } catch (error) {
      console.error('取消失败:', error)
      ElMessage.error('取消失败')
    }
  })
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
.logistics-shipment {
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