<template>
  <div class="product-info-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>商品信息</span>
          <el-button type="primary" @click="handleAdd">新增商品</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" class="search-form" inline>
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.name" placeholder="请输入商品名称" clearable />
        </el-form-item>
        <el-form-item label="商品编码">
          <el-input v-model="searchForm.code" placeholder="请输入商品编码" clearable />
        </el-form-item>
        <el-form-item label="商品分类">
          <el-select v-model="searchForm.categoryId" placeholder="请选择分类" clearable>
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" value="enabled" />
            <el-option label="禁用" value="disabled" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        style="width: 100%"
      >
        <el-table-column prop="code" label="商品编码" width="120" />
        <el-table-column prop="name" label="商品名称" width="200" />
        <el-table-column prop="categoryName" label="商品分类" width="120" />
        <el-table-column prop="spec" label="规格" width="120" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="price" label="价格" width="120">
          <template #default="scope">
            {{ formatAmount(scope.row.price) }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag
              :type="scope.row.status === 'enabled' ? 'success' : 'danger'"
              disable-transitions
            >
              {{ scope.row.status === 'enabled' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              link
              @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              type="primary"
              link
              @click="handleView(scope.row)"
            >
              查看
            </el-button>
            <el-button
              :type="scope.row.status === 'enabled' ? 'danger' : 'success'"
              link
              @click="handleToggleStatus(scope.row)"
            >
              {{ scope.row.status === 'enabled' ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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
  code: '',
  categoryId: '',
  status: ''
})

// 表格数据
const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 商品分类列表
const categories = ref([])

// 获取商品分类列表
const getCategories = async () => {
  try {
    const response = await fetch('/api/product/categories')
    const result = await response.json()
    if (result.code === 0) {
      categories.value = result.data
    }
  } catch (error) {
    console.error('获取商品分类列表失败:', error)
  }
}

// 格式化金额
const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return Number(amount).toFixed(2)
}

// 处理查询
const handleSearch = async () => {
  loading.value = true
  try {
    const offset = (currentPage.value - 1) * pageSize.value
    const params = new URLSearchParams()
    if (searchForm.name) params.append('name', searchForm.name)
    if (searchForm.code) params.append('code', searchForm.code)
    if (searchForm.categoryId) params.append('categoryId', searchForm.categoryId)
    if (searchForm.status) params.append('status', searchForm.status)
    params.append('offset', offset.toString())
    params.append('limit', pageSize.value.toString())

    console.log('Request params:', {
      name: searchForm.name,
      code: searchForm.code,
      categoryId: searchForm.categoryId,
      status: searchForm.status,
      offset,
      limit: pageSize.value
    })

    const response = await fetch(`/api/products?${params.toString()}`)
    const result = await response.json()
    console.log('Response:', result)

    if (result.code === 0) {
      tableData.value = result.data?.list || []
      total.value = result.data?.total || 0
    } else {
      ElMessage.error(result.message || '查询失败')
    }
  } catch (error) {
    console.error('查询商品列表失败:', error)
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

// 重置查询
const resetSearch = () => {
  searchForm.name = ''
  searchForm.code = ''
  searchForm.categoryId = ''
  searchForm.status = ''
  handleSearch()
}

// 处理新增
const handleAdd = () => {
  router.push('/dashboard/product/info/form')
}

// 处理编辑
const handleEdit = (row) => {
  router.push(`/dashboard/product/info/form?id=${row.id}`)
}

// 处理查看
const handleView = (row) => {
  router.push(`/dashboard/product/info/detail?id=${row.id}`)
}

// 处理状态切换
const handleToggleStatus = (row) => {
  const action = row.status === 'enabled' ? '禁用' : '启用'
  ElMessageBox.confirm(
    `确认${action}该商品？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const response = await fetch(`/api/products/${row.id}/status`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          status: row.status === 'enabled' ? 'disabled' : 'enabled'
        })
      })
      const result = await response.json()
      if (result.code === 0) {
        ElMessage.success(`${action}成功`)
        handleSearch()
      } else {
        ElMessage.error(result.message || `${action}失败`)
      }
    } catch (error) {
      console.error(`${action}商品失败:`, error)
      ElMessage.error(`${action}失败`)
    }
  }).catch(() => {})
}

// 处理分页大小变化
const handleSizeChange = (val) => {
  pageSize.value = val
  handleSearch()
}

// 处理页码变化
const handleCurrentChange = (val) => {
  currentPage.value = val
  handleSearch()
}

onMounted(() => {
  getCategories()
  handleSearch()
})
</script>

<style scoped>
.product-info-container {
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