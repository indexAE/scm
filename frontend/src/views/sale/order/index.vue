<template>
  <div class="sale-order">
    <div class="page-header">
      <h2>销售订单管理</h2>
      <el-button type="primary" @click="handleAdd">新增订单</el-button>
    </div>

    <!-- 搜索表单 -->
    <el-form :inline="true" :model="queryParams" class="search-form">
      <el-form-item label="订单编号">
        <el-input v-model="queryParams.orderNo" placeholder="请输入订单编号" clearable />
      </el-form-item>
      <el-form-item label="客户名称">
        <el-input v-model="queryParams.customerName" placeholder="请输入客户名称" clearable />
      </el-form-item>
      <el-form-item label="订单状态">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="待审核" value="pending" />
          <el-option label="已审核" value="approved" />
          <el-option label="已完成" value="completed" />
          <el-option label="已取消" value="cancelled" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 数据表格 -->
    <el-table :data="orderList" border style="width: 100%">
      <el-table-column prop="orderNo" label="订单编号" />
      <el-table-column prop="customerName" label="客户名称" />
      <el-table-column prop="totalAmount" label="订单金额">
        <template #default="scope">
          {{ formatAmount(scope.row.totalAmount) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="订单状态">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column label="操作" width="280">
        <template #default="scope">
          <el-button link type="primary" @click="handleView(scope.row)">查看</el-button>
          <el-button link type="primary" @click="handleEdit(scope.row)" v-if="scope.row.status === 'pending'">编辑</el-button>
          <el-button link type="success" @click="handleApprove(scope.row, 'approved')" v-if="scope.row.status === 'pending'">审核通过</el-button>
          <el-button link type="danger" @click="handleApprove(scope.row, 'rejected')" v-if="scope.row.status === 'pending'">审核拒绝</el-button>
          <el-button link type="danger" @click="handleDelete(scope.row)" v-if="scope.row.status === 'pending'">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

// 查询参数
const queryParams = reactive({
  orderNo: '',
  customerName: '',
  status: '',
  pageNum: 1,
  pageSize: 10
})

const orderList = ref([])
const total = ref(0)

// 获取订单列表
const getList = async () => {
  try {
    const response = await fetch(`http://127.0.0.1:8080/api/sale/orders?offset=${(queryParams.pageNum - 1) * queryParams.pageSize}&limit=${queryParams.pageSize}&orderNo=${queryParams.orderNo}&customerName=${queryParams.customerName}&status=${queryParams.status}`, {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0) {
      orderList.value = result.data.list
      total.value = result.data.total
    } else {
      ElMessage.error(result.message || '获取订单列表失败')
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  }
}

// 处理查询
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 处理重置
const handleReset = () => {
  queryParams.orderNo = ''
  queryParams.customerName = ''
  queryParams.status = ''
  handleQuery()
}

// 处理新增
const handleAdd = () => {
  router.push('/dashboard/sale/order/form')
}

// 处理查看
const handleView = (row) => {
  router.push(`/dashboard/sale/order/form?id=${row.id}&mode=view`)
}

// 处理编辑
const handleEdit = (row) => {
  router.push(`/dashboard/sale/order/form?id=${row.id}`)
}

// 处理删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确认要删除该订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await fetch(`http://127.0.0.1:8080/api/sale/orders/${row.id}`, {
        method: 'DELETE',
        headers: {
          'Accept': 'application/json'
        },
        credentials: 'include'
      })
      const result = await response.json()
      if (result.code === 0) {
        ElMessage.success('删除成功')
        getList()
      } else {
        ElMessage.error(result.message || '删除失败')
      }
    } catch (error) {
      console.error('删除订单失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 处理分页大小变化
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

// 处理页码变化
const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}

// 格式化金额
const formatAmount = (amount) => {
  return `¥${amount.toFixed(2)}`
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger',
    completed: 'success',
    cancelled: 'info'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝',
    completed: '已完成',
    cancelled: '已取消'
  }
  return texts[status] || status
}

// 处理审核
const handleApprove = (row, approveStatus) => {
  const actionText = approveStatus === 'approved' ? '通过' : '拒绝'
  ElMessageBox.prompt(`请输入${actionText}原因`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPlaceholder: '请输入审核意见'
  }).then(async ({ value: remark }) => {
    try {
      const response = await fetch(`/api/sale/orders/${row.id}/approve?approveStatus=${approveStatus}&remark=${remark || ''}`, {
        method: 'PUT',
        headers: {
          'Accept': 'application/json'
        },
        credentials: 'include'
      })
      const result = await response.json()
      if (result.code === 0) {
        ElMessage.success('审核成功')
        getList()
      } else {
        ElMessage.error(result.message || '审核失败')
      }
    } catch (error) {
      console.error('审核失败:', error)
      ElMessage.error('审核失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.sale-order {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
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