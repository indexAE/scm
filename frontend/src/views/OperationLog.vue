<template>
  <div class="operation-log">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>操作日志</span>
          <div class="header-right">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :shortcuts="shortcuts"
              value-format="YYYY-MM-DD"
              @change="handleDateChange"
            />
            <el-button type="primary" @click="fetchLogs">查询</el-button>
          </div>
        </div>
      </template>
      
      <!-- 日志列表 -->
      <el-table :data="logs" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="operation" label="操作内容" />
        <el-table-column prop="method" label="请求方法" width="100" />
        <el-table-column prop="params" label="请求参数" width="200">
          <template #default="scope">
            <el-tooltip
              class="box-item"
              effect="dark"
              :content="scope.row.params"
              placement="top-start"
            >
              <span class="params-text">{{ scope.row.params }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="ip" label="IP地址" width="120" />
        <el-table-column prop="operationTime" label="操作时间" width="180" />
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
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
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

// 日期快捷选项
const shortcuts = [
  {
    text: '最近一周',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start, end]
    },
  },
  {
    text: '最近一个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      return [start, end]
    },
  },
  {
    text: '最近三个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
      return [start, end]
    },
  },
]

// 响应式数据
const logs = ref([])
const dateRange = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 获取日志列表
const fetchLogs = async () => {
  try {
    const params = new URLSearchParams({
      page: currentPage.value,
      size: pageSize.value,
    })
    
    if (dateRange.value && dateRange.value.length === 2) {
      params.append('startDate', dateRange.value[0])
      params.append('endDate', dateRange.value[1])
    }
    
    const res = await fetch(`http://localhost:8000/api/logs/operation?${params}`, {
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    })
    
    const data = await res.json()
    if (data.code === 0) {
      logs.value = data.data.records
      total.value = data.data.total
    } else {
      ElMessage.error(data.message || '获取日志失败')
    }
  } catch (error) {
    console.error('获取日志失败:', error)
    ElMessage.error('获取日志失败')
  }
}

// 处理日期变化
const handleDateChange = () => {
  currentPage.value = 1
  fetchLogs()
}

// 处理每页数量变化
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchLogs()
}

// 处理页码变化
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchLogs()
}

onMounted(() => {
  fetchLogs()
})
</script>

<style scoped>
.operation-log {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-right {
  display: flex;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.params-text {
  display: inline-block;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style> 