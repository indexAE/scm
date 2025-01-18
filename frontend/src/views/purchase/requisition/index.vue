<template>
  <div class="requisition-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>采购申请管理</span>
          <el-button type="primary" @click="handleAdd">新建申请</el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="申请编号">
          <el-input v-model="searchForm.code" placeholder="请输入申请编号" clearable></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待审核" value="pending"></el-option>
            <el-option label="已审核" value="approved"></el-option>
            <el-option label="已驳回" value="rejected"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格区域 -->
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="code" label="申请编号" width="180"></el-table-column>
        <el-table-column prop="dealer" label="经销商" width="180"></el-table-column>
        <el-table-column prop="amount" label="申请金额">
          <template #default="scope">
            {{ formatAmount(scope.row.amount) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="scope">
            <el-button type="primary" plain @click="handleView(scope.row)">查看</el-button>
            <el-button v-if="scope.row.status === 'pending'" type="success" plain @click="handleEdit(scope.row)">编辑</el-button>
            <el-button v-if="scope.row.status === 'pending'" type="warning" plain @click="handleApprove(scope.row)">审核</el-button>
            <el-button v-if="scope.row.status === 'pending'" type="danger" plain @click="handleReject(scope.row)">拒绝</el-button>
            <el-button v-if="scope.row.status === 'pending'" type="danger" plain @click="handleDelete(scope.row)">删除</el-button>
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

      <!-- 审核对话框 -->
      <el-dialog v-model="approveDialogVisible" :title="approveAction === 'approve' ? '审核通过' : '审核拒绝'">
        <el-form ref="approveFormRef" :model="approveForm">
          <el-form-item label="备注" prop="remark">
            <el-input v-model="approveForm.remark" type="textarea" rows="3" placeholder="请输入审核意见"></el-input>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="approveDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitApprove">确定</el-button>
        </template>
      </el-dialog>
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
  status: ''
})

// 表格数据
const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 审核相关
const approveDialogVisible = ref(false)
const approveAction = ref('')
const currentRequisition = ref(null)
const approveForm = reactive({
  remark: ''
})

// 获取状态类型
const getStatusType = (status) => {
  const map = {
    pending: '',
    approved: 'success',
    rejected: 'danger'
  }
  return map[status]
}

// 获取状态文本
const getStatusText = (status) => {
  const map = {
    pending: '待审核',
    approved: '已审核',
    rejected: '已驳回'
  }
  return map[status]
}

// 格式化金额
const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return Number(amount).toFixed(2)
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleString()
}

// 获取采购申请列表
const getRequisitionList = async () => {
  loading.value = true
  try {
    const offset = (currentPage.value - 1) * pageSize.value
    const params = new URLSearchParams({
      code: searchForm.code,
      status: searchForm.status,
      offset: offset,
      limit: pageSize.value
    })
    
    const response = await fetch(`/api/purchase/requisitions?${params.toString()}`)
    const result = await response.json()
    
    if (result.code === 0) {
      tableData.value = result.data.list
      total.value = result.data.total
    } else {
      ElMessage.error(result.message || '获取采购申请列表失败')
    }
  } catch (error) {
    console.error('获取采购申请列表失败:', error)
    ElMessage.error('获取采购申请列表失败')
  } finally {
    loading.value = false
  }
}

// 处理查询
const handleSearch = () => {
  currentPage.value = 1
  getRequisitionList()
}

// 重置查询
const resetSearch = () => {
  searchForm.code = ''
  searchForm.status = ''
  handleSearch()
}

// 处理新增
const handleAdd = () => {
  router.push('/dashboard/purchase/requisition/form')
}

// 处理查看
const handleView = (row) => {
  router.push({
    path: '/dashboard/purchase/requisition/form',
    query: {
      id: row.id,
      type: 'view'
    }
  })
}

// 处理编辑
const handleEdit = (row) => {
  router.push({
    path: '/dashboard/purchase/requisition/form',
    query: {
      id: row.id,
      type: 'edit'
    }
  })
}

// 处理删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确认删除采购申请 ${row.code}？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const response = await fetch(`/api/purchase/requisitions/${row.id}`, {
        method: 'DELETE'
      })
      const result = await response.json()
      
      if (result.code === 0) {
        ElMessage.success('删除成功')
        getRequisitionList()
      } else {
        ElMessage.error(result.message || '删除失败')
      }
    } catch (error) {
      console.error('删除采购申请失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 处理分页大小变化
const handleSizeChange = (val) => {
  pageSize.value = val
  getRequisitionList()
}

// 处理页码变化
const handleCurrentChange = (val) => {
  currentPage.value = val
  getRequisitionList()
}

const handleApprove = (row) => {
  approveAction.value = 'approve'
  currentRequisition.value = row
  approveDialogVisible.value = true
}

const handleReject = (row) => {
  approveAction.value = 'reject'
  currentRequisition.value = row
  approveDialogVisible.value = true
}

const submitApprove = async () => {
  try {
    const status = approveAction.value === 'approve' ? 'approved' : 'rejected';
    const response = await fetch(`/api/purchase/requisitions/${currentRequisition.value.id}/status`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        status: status,
        remark: approveForm.remark
      })
    })
    
    if (!response.ok) {
      const result = await response.json()
      ElMessage.error(result.message || '审核失败')
      return
    }
    
    ElMessage.success(status === 'approved' ? '审核通过成功' : '驳回成功')
    approveDialogVisible.value = false
    approveForm.remark = ''
    handleSearch() // 刷新列表
  } catch (error) {
    console.error('Error:', error)
    ElMessage.error('审核失败')
  }
}

// 页面加载时获取数据
onMounted(() => {
  getRequisitionList()
})
</script>

<style scoped>
.requisition-container {
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