<template>
  <div class="tracking-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>物流跟踪</span>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline>
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
      <el-table :data="tableData" border stripe>
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="consignee" label="收货人" width="100" />
        <el-table-column prop="consigneePhone" label="联系电话" width="120" />
        <el-table-column prop="consigneeAddress" label="收货地址" width="200" show-overflow-tooltip />
        <el-table-column prop="location" label="当前位置" width="150" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.location || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="物流描述" width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.description || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="trackingTime" label="跟踪时间" width="180">
          <template #default="{ row }">
            {{ row.trackingTime ? formatDateTime(row.trackingTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="limit"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { formatDateTime } from '@/utils/format'
import request from '@/utils/request'

const router = useRouter()

// 搜索表单数据
const searchForm = ref({
  orderNo: '',
  status: ''
})

// 表格数据
const tableData = ref([])
const page = ref(1)
const limit = ref(10)
const total = ref(0)

// 获取物流跟踪列表
const getTrackingList = async () => {
  try {
    const params = {
      page: page.value,
      limit: limit.value,
      orderNo: searchForm.value.orderNo,
      status: searchForm.value.status
    }
    const res = await request.get('/api/logistics/tracking/page', { params })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('获取物流跟踪列表失败')
  }
}

// 查询
const handleSearch = () => {
  page.value = 1
  getTrackingList()
}

// 重置
const handleReset = () => {
  searchForm.value = {
    orderNo: '',
    status: ''
  }
  handleSearch()
}

// 查看详情
const handleView = (row) => {
  router.push(`/dashboard/logistics/tracking/detail?orderNo=${row.orderNo}`)
}

// 分页大小改变
const handleSizeChange = (val) => {
  limit.value = val
  getTrackingList()
}

// 当前页改变
const handleCurrentChange = (val) => {
  page.value = val
  getTrackingList()
}

// 获取状态类型
const getStatusType = (status) => {
  const map = {
    pending: 'warning',
    shipped: 'primary',
    delivered: 'success',
    cancelled: 'info'
  }
  return map[status] || ''
}

// 获取状态文本
const getStatusText = (status) => {
  const map = {
    pending: '待发货',
    shipped: '已发货',
    delivered: '已送达',
    cancelled: '已取消'
  }
  return map[status] || ''
}

onMounted(() => {
  getTrackingList()
})
</script>

<style scoped>
.tracking-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 