<template>
  <div class="warehouse-stock-detail">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>库存明细</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>

      <div class="info-section">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="仓库">{{ route.query.warehouseName }}</el-descriptions-item>
          <el-descriptions-item label="商品">{{ route.query.productName }}</el-descriptions-item>
          <el-descriptions-item label="当前库存">{{ currentStock }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <div class="table-section">
        <el-table :data="tableData" style="width: 100%" border>
          <el-table-column prop="operation_type" label="操作类型" width="120">
            <template #default="{ row }">
              {{ getOperationType(row.operation_type) }}
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="变动数量" width="120">
            <template #default="{ row }">
              <span :class="{ 'text-red': row.quantity < 0, 'text-green': row.quantity > 0 }">
                {{ row.quantity > 0 ? '+' + row.quantity : row.quantity }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="before_quantity" label="变动前数量" width="120" />
          <el-table-column prop="after_quantity" label="变动后数量" width="120" />
          <el-table-column prop="operator_name" label="操作人" width="120" />
          <el-table-column prop="operation_time" label="操作时间" width="180" />
          <el-table-column prop="remark" label="备注" />
        </el-table>

        <div class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const currentStock = ref(0)

// 获取操作类型显示文本
const getOperationType = (type) => {
  const types = {
    'adjust': '调整',
    'inbound': '入库',
    'outbound': '出库'
  }
  return types[type] || type
}

// 获取库存明细记录
const getRecords = async () => {
  try {
    const offset = (currentPage.value - 1) * pageSize.value
    const response = await fetch(`http://127.0.0.1:8080/api/warehouse/stocks/${route.query.id}/records?offset=${offset}&limit=${pageSize.value}`, {
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
      ElMessage.error(result.message || '获取库存明细记录失败')
    }
  } catch (error) {
    console.error('获取库存明细记录失败:', error)
    ElMessage.error('获取库存明细记录失败')
  }
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  getRecords()
}

// 当前页改变
const handleCurrentChange = (val) => {
  currentPage.value = val
  getRecords()
}

// 初始化
onMounted(() => {
  currentStock.value = route.query.quantity || 0
  getRecords()
})
</script>

<style scoped>
.warehouse-stock-detail {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-section {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.text-red {
  color: #f56c6c;
}

.text-green {
  color: #67c23a;
}
</style> 