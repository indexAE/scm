<template>
  <div class="pricing-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>定价管理</span>
          <el-button type="primary" @click="handleAdd">新增定价</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" class="search-form" inline>
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.productName" placeholder="请输入商品名称" clearable />
        </el-form-item>
        <el-form-item label="商品分类">
          <el-cascader
            v-model="searchForm.categoryId"
            :options="categories"
            :props="{
              checkStrictly: true,
              label: 'name',
              value: 'id',
              emitPath: false
            }"
            clearable
            placeholder="请选择商品分类"
          />
        </el-form-item>
        <el-form-item label="价格类型">
          <el-select v-model="searchForm.priceType" placeholder="请选择价格类型" clearable>
            <el-option label="零售价" value="retail" />
            <el-option label="批发价" value="wholesale" />
            <el-option label="会员价" value="member" />
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
        <el-table-column prop="productCode" label="商品编码" width="120" />
        <el-table-column prop="productName" label="商品名称" width="200" />
        <el-table-column prop="categoryName" label="商品分类" width="120" />
        <el-table-column prop="spec" label="规格" width="120" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column label="零售价" width="120">
          <template #default="scope">
            {{ formatAmount(scope.row.retailPrice) }}
          </template>
        </el-table-column>
        <el-table-column label="批发价" width="120">
          <template #default="scope">
            {{ formatAmount(scope.row.wholesalePrice) }}
          </template>
        </el-table-column>
        <el-table-column label="会员价" width="120">
          <template #default="scope">
            {{ formatAmount(scope.row.memberPrice) }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
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
              @click="handleHistory(scope.row)"
            >
              历史
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

    <!-- 新增/编辑定价对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增定价' : '编辑定价'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="商品" prop="productId">
          <el-select
            v-model="form.productId"
            placeholder="请选择商品"
            filterable
            remote
            :remote-method="handleProductSearch"
            :loading="productLoading"
            @change="handleProductChange"
          >
            <el-option
              v-for="item in products"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
              <span>{{ item.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">
                {{ item.code }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="零售价" prop="retailPrice">
          <el-input-number
            v-model="form.retailPrice"
            :precision="2"
            :min="0"
            :step="0.01"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="批发价" prop="wholesalePrice">
          <el-input-number
            v-model="form.wholesalePrice"
            :precision="2"
            :min="0"
            :step="0.01"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="会员价" prop="memberPrice">
          <el-input-number
            v-model="form.memberPrice"
            :precision="2"
            :min="0"
            :step="0.01"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 价格历史对话框 -->
    <el-dialog
      v-model="historyDialogVisible"
      title="价格调整历史"
      width="800px"
    >
      <el-table
        :data="historyData"
        border
        style="width: 100%"
      >
        <el-table-column prop="retailPrice" label="零售价" width="120">
          <template #default="scope">
            {{ formatAmount(scope.row.retailPrice) }}
          </template>
        </el-table-column>
        <el-table-column prop="wholesalePrice" label="批发价" width="120">
          <template #default="scope">
            {{ formatAmount(scope.row.wholesalePrice) }}
          </template>
        </el-table-column>
        <el-table-column prop="memberPrice" label="会员价" width="120">
          <template #default="scope">
            {{ formatAmount(scope.row.memberPrice) }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="调整原因" />
        <el-table-column prop="createTime" label="调整时间" width="180" />
        <el-table-column prop="operator" label="操作人" width="120" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

// 搜索表单数据
const searchForm = reactive({
  productName: '',
  categoryId: null,
  priceType: ''
})

// 表格数据
const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 商品分类列表
const categories = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add') // add or edit
const formRef = ref(null)
const form = reactive({
  productId: '',
  retailPrice: 0,
  wholesalePrice: 0,
  memberPrice: 0,
  remark: ''
})

// 表单验证规则
const rules = {
  productId: [
    { required: true, message: '请选择商品', trigger: 'change' }
  ],
  retailPrice: [
    { required: true, message: '请输入零售价', trigger: 'blur' }
  ],
  wholesalePrice: [
    { required: true, message: '请输入批发价', trigger: 'blur' }
  ],
  memberPrice: [
    { required: true, message: '请输入会员价', trigger: 'blur' }
  ]
}

// 商品列表数据
const products = ref([])
const productLoading = ref(false)

// 价格历史相关
const historyDialogVisible = ref(false)
const historyData = ref([])

// 获取列表数据
const getList = async () => {
  try {
    loading.value = true
    const params = new URLSearchParams({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      hideSuccessMessage: true
    })
    
    if (searchForm.productName) {
      params.append('productName', searchForm.productName)
    }
    if (searchForm.categoryId) {
      params.append('categoryId', searchForm.categoryId)
    }
    if (searchForm.priceType) {
      params.append('priceType', searchForm.priceType)
    }
    
    const response = await fetch(`http://127.0.0.1:8000/api/product/prices?${params}`)
    const result = await response.json()
    
    if (result.code === 0) {
      tableData.value = result.data?.list || []
      total.value = result.data?.total || 0
      if (!result.hideMessage && result.message) {
        ElMessage.success(result.message)
      }
    } else {
      ElMessage.error(result.message || '获取列表失败')
    }
  } catch (error) {
    console.error('获取列表失败:', error)
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

// 获取商品分类树
const getCategoryTree = async () => {
  try {
    const response = await fetch('http://127.0.0.1:8080/api/product/categories/tree', {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    
    if (result.code === 0) {
      categories.value = result.data || []
    } else {
      ElMessage.error(result.message || '获取商品分类失败')
    }
  } catch (error) {
    console.error('获取商品分类失败:', error)
    ElMessage.error('获取商品分类失败')
  }
}

// 格式化金额
const formatAmount = (amount) => {
  if (!amount && amount !== 0) return '0.00'
  return Number(amount).toFixed(2)
}

// 搜索商品
const handleProductSearch = async (query) => {
  if (query === '') {
    products.value = []
    return
  }

  productLoading.value = true
  try {
    const res = await fetch(`http://127.0.0.1:8080/api/products?name=${encodeURIComponent(query)}`, {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const data = await res.json()
    if (data.code === 0) {
      products.value = data.data.list || []
    } else {
      ElMessage.error(data.message || '搜索商品失败')
    }
  } catch (error) {
    console.error('搜索商品失败:', error)
    ElMessage.error('搜索商品失败')
  } finally {
    productLoading.value = false
  }
}

// 商品选择改变时的处理
const handleProductChange = async (productId) => {
  if (!productId) return
  
  try {
    const res = await fetch(`http://127.0.0.1:8080/api/products/${productId}`, {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const data = await res.json()
    if (data.code === 0) {
      const product = data.data
      // 如果商品已有定价，则填充到表单中
      if (product.pricing) {
        form.retailPrice = product.pricing.retailPrice
        form.wholesalePrice = product.pricing.wholesalePrice
        form.memberPrice = product.pricing.memberPrice
      } else {
        // 如果商品没有定价，则清空表单
        form.retailPrice = 0
        form.wholesalePrice = 0
        form.memberPrice = 0
      }
    }
  } catch (error) {
    console.error('获取商品详情失败:', error)
    ElMessage.error('获取商品详情失败')
  }
}

// 初始化时加载商品列表
const initProducts = async () => {
  productLoading.value = true
  try {
    const res = await fetch('http://127.0.0.1:8080/api/products', {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const data = await res.json()
    if (data.code === 0) {
      products.value = data.data.list || []
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
  } finally {
    productLoading.value = false
  }
}

// 处理查询
const handleSearch = async () => {
  loading.value = true
  try {
    const params = new URLSearchParams({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      hideSuccessMessage: true
    })
    
    if (searchForm.productName) {
      params.append('productName', searchForm.productName)
    }
    if (searchForm.categoryId) {
      params.append('categoryId', searchForm.categoryId)
    }
    if (searchForm.priceType) {
      params.append('priceType', searchForm.priceType)
    }
    
    const response = await fetch(`http://127.0.0.1:8000/api/product/prices?${params}`)
    const result = await response.json()
    
    if (result.code === 0) {
      tableData.value = result.data?.list || []
      total.value = result.data?.total || 0
    }
  } catch (error) {
    console.error('获取列表失败:', error)
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

// 重置查询
const resetSearch = () => {
  searchForm.productName = ''
  searchForm.categoryId = null
  searchForm.priceType = ''
  handleSearch()
}

// 处理新增
const handleAdd = () => {
  dialogType.value = 'add'
  form.id = undefined
  form.productId = ''
  form.retailPrice = 0
  form.wholesalePrice = 0
  form.memberPrice = 0
  form.remark = ''
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row) => {
  dialogType.value = 'edit'
  form.id = row.id
  form.productId = row.productId
  form.retailPrice = row.retailPrice
  form.wholesalePrice = row.wholesalePrice
  form.memberPrice = row.memberPrice
  form.remark = ''
  dialogVisible.value = true
}

// 处理查看历史
const handleHistory = async (row) => {
  try {
    const response = await fetch(`http://127.0.0.1:8000/api/product/prices/${row.productId}/history`)
    const result = await response.json()
    if (result.code === 0) {
      historyData.value = result.data.map(item => ({
        ...item,
        operator: item.createBy // TODO: 需要后端返回操作人姓名
      }))
      historyDialogVisible.value = true
    } else {
      ElMessage.error(result.message || '获取价格历史失败')
    }
  } catch (error) {
    console.error('获取价格历史失败:', error)
    ElMessage.error('获取价格历史失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const url = dialogType.value === 'add' 
          ? 'http://127.0.0.1:8000/api/product/prices'
          : `http://127.0.0.1:8000/api/product/prices/${form.id}`
        
        const method = dialogType.value === 'add' ? 'POST' : 'PUT'
        const response = await fetch(url, {
          method,
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(form)
        })
        
        const result = await response.json()
        if (result.code === 0) {
          ElMessage.success(dialogType.value === 'add' ? '新增成功' : '更新成功')
          dialogVisible.value = false
          getList() // 改用getList而不是handleSearch
        } else {
          ElMessage.error(result.message || (dialogType.value === 'add' ? '新增失败' : '更新失败'))
        }
      } catch (error) {
        console.error(dialogType.value === 'add' ? '新增失败:' : '更新失败:', error)
        ElMessage.error(dialogType.value === 'add' ? '新增失败' : '更新失败')
      }
    }
  })
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

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return ''
  const date = new Date(datetime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  })
}

onMounted(async () => {
  await Promise.all([
    initProducts(),
    getCategoryTree(),
    getList()
  ])
})
</script>

<style scoped>
.pricing-container {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 