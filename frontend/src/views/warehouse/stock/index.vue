<template>
  <div class="warehouse-stock">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>库存管理</span>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="仓库">
          <el-select v-model="searchForm.warehouseId" placeholder="请选择仓库" clearable>
            <el-option
              v-for="item in warehouses"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.productName" placeholder="请输入商品名称" clearable />
        </el-form-item>
        <el-form-item label="库存状态">
          <el-select v-model="searchForm.stockStatus" placeholder="请选择库存状态" clearable>
            <el-option label="正常" value="normal" />
            <el-option label="积压" value="overstock" />
            <el-option label="短缺" value="shortage" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="warehouse" label="仓库" width="150" />
        <el-table-column prop="productCode" label="商品编号" width="120" />
        <el-table-column prop="productName" label="商品名称" width="150" />
        <el-table-column prop="productSpec" label="规格" width="120" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="quantity" label="当前库存" width="100" />
        <el-table-column prop="minStock" label="最小库存" width="100" />
        <el-table-column prop="maxStock" label="最大库存" width="100" />
        <el-table-column prop="stockStatus" label="库存状态" width="100">
          <template #default="scope">
            <el-tag :type="getStockStatusType(scope.row.stockStatus)">
              {{ getStockStatusLabel(scope.row.stockStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleStockAdjust(scope.row)">库存调整</el-button>
            <el-button type="primary" link @click="handleStockDetail(scope.row)">库存明细</el-button>
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
          layout="total, sizes, prev, pager, next, jumper"
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
  warehouseId: '',
  productName: '',
  stockStatus: ''
})

// 表格数据
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const warehouses = ref([])

// 获取仓库列表
const getWarehouses = async () => {
  try {
    const response = await fetch('http://127.0.0.1:8000/api/warehouses?status=normal')
    const result = await response.json()
    if (result.code === 0) {
      warehouses.value = result.data.list || []
    } else {
      ElMessage.error(result.message || '获取仓库列表失败')
    }
  } catch (error) {
    console.error('获取仓库列表失败:', error)
    ElMessage.error('获取仓库列表失败')
  }
}

// 获取库存列表
const getStockList = async () => {
  try {
    const offset = (currentPage.value - 1) * pageSize.value
    const params = new URLSearchParams({
      offset: offset.toString(),
      limit: pageSize.value.toString(),
      warehouseId: searchForm.warehouseId || '',
      productName: searchForm.productName || '',
      stockStatus: searchForm.stockStatus || ''
    })
    
    const response = await fetch(`http://127.0.0.1:8000/api/warehouse/stocks?${params.toString()}`)
    const result = await response.json()
    if (result.code === 0) {
      tableData.value = result.data.list || []
      total.value = result.data.total || 0
    } else {
      ElMessage.error(result.message || '获取库存列表失败')
    }
  } catch (error) {
    console.error('获取库存列表失败:', error)
    ElMessage.error('获取库存列表失败')
  }
}

// 获取库存状态类型
const getStockStatusType = (status) => {
  const types = {
    normal: 'success',
    overstock: 'warning',
    shortage: 'danger'
  }
  return types[status] || 'info'
}

// 获取库存状态标签
const getStockStatusLabel = (status) => {
  const labels = {
    normal: '正常',
    overstock: '积压',
    shortage: '短缺'
  }
  return labels[status] || '未知'
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getStockList()
}

// 重置搜索
const handleReset = () => {
  searchForm.warehouseId = ''
  searchForm.productName = ''
  searchForm.stockStatus = ''
  handleSearch()
}

// 库存调整
const handleStockAdjust = (row) => {
  ElMessageBox.prompt('请输入调整数量（正数增加，负数减少）:', '库存调整', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^-?\d+$/,
    inputErrorMessage: '请输入正确的数字'
  }).then(async ({ value }) => {
    try {
      const quantity = parseInt(value)
      console.log('调整库存，行数据:', row)
      console.log('调整数量:', quantity)
      
      const res = await fetch(`http://127.0.0.1:8080/api/warehouse/stocks/${row.id}/adjust`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify({ quantity })
      })
      console.log('库存调整API响应:', res)
      const data = await res.json()
      console.log('库存调整结果:', data)
      
      if (data.code === 0) {
        ElMessage.success('库存调整成功')
        getStockList()
      } else {
        console.error('库存调整失败:', data.message)
        ElMessage.error(data.message || '库存调整失败')
      }
    } catch (error) {
      console.error('库存调整失败:', error)
      ElMessage.error('库存调整失败')
    }
  }).catch(() => {})
}

// 库存明细
const handleStockDetail = (row) => {
  console.log('跳转库存明细，行数据:', row)
  router.push({
    path: '/dashboard/warehouse/stock/detail',
    query: {
      id: row.id,
      warehouseId: row.warehouseId,
      productId: row.productId,
      warehouseName: row.warehouse,
      productName: row.productName,
      quantity: row.quantity
    }
  })
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  getStockList()
}

// 当前页改变
const handleCurrentChange = (val) => {
  currentPage.value = val
  getStockList()
}

// 初始化
onMounted(async () => {
  await getWarehouses()
  getStockList()
})
</script>

<style scoped>
.warehouse-stock {
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