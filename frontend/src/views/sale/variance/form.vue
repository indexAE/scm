<template>
  <div class="variance-form">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ formTitle }}</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="variance-form"
      >
        <!-- 基本信息 -->
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="差异单号" prop="varianceNo">
              <el-input v-model="form.varianceNo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="关联订单号" prop="orderId">
              <el-select
                v-model="form.orderId"
                placeholder="请选择关联订单"
                :disabled="isView || !!id"
                @change="handleOrderChange"
              >
                <el-option
                  v-for="order in orders"
                  :key="order.id"
                  :label="order.orderNo"
                  :value="order.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="客户名称" prop="customerName">
              <el-input
                v-model="form.customerName"
                disabled
                placeholder="选择订单后自动填充"
                :class="{ 'readonly-input': true }"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="商品名称" prop="productId">
              <el-select
                v-model="form.productId"
                placeholder="请选择商品"
                :disabled="isView || !!id"
                @change="handleProductChange"
              >
                <el-option
                  v-for="product in products"
                  :key="product.id"
                  :label="product.name"
                  :value="product.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="订单数量" prop="orderQuantity">
              <el-input-number
                v-model="form.orderQuantity"
                :min="0"
                :disabled="true"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="实际数量" prop="actualQuantity">
              <el-input-number
                v-model="form.actualQuantity"
                :min="0"
                :disabled="isView"
                @change="calculateVariance"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="差异数量" prop="varianceQuantity">
              <el-input-number
                v-model="form.varianceQuantity"
                :disabled="true"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单价" prop="unitPrice">
              <el-input-number
                v-model="form.unitPrice"
                :precision="2"
                :disabled="true"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="差异金额" prop="amount">
              <el-input-number
                v-model="form.amount"
                :precision="2"
                :disabled="true"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="差异原因" prop="reason">
          <el-input
            v-model="form.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入差异原因"
            :disabled="isView"
          />
        </el-form-item>

        <el-form-item label="处理方案" prop="solution">
          <el-input
            v-model="form.solution"
            type="textarea"
            :rows="3"
            placeholder="请输入处理方案"
            :disabled="isView || form.status !== 'pending'"
          />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
            :disabled="isView"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            v-if="!isView"
            type="primary"
            @click="handleSubmit"
            :loading="submitting"
          >
            保存
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)

// 获取路由参数
const id = route.query.id
const mode = route.query.mode
const isView = computed(() => mode === 'view')

// 表单标题
const formTitle = computed(() => {
  if (isView.value) return '查看销售差异'
  return id ? '编辑销售差异' : '新增销售差异'
})

// 生成差异单号
const generateVarianceNo = () => {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0')
  return `CY${year}${month}${day}${random}`
}

// 表单数据
const form = reactive({
  varianceNo: generateVarianceNo(),  // 初始化时生成差异单号
  orderId: '',
  orderNo: '',
  customerId: '',
  customerName: '',
  productId: '',
  productName: '',
  orderQuantity: 0,
  actualQuantity: 0,
  varianceQuantity: 0,
  unitPrice: 0,
  amount: 0,
  type: 'quantity',
  reason: '',
  solution: '',
  remark: '',
  status: 'pending'
})

// 表单验证规则
const rules = {
  orderId: [{ required: true, message: '请选择关联订单', trigger: 'change' }],
  productId: [{ required: true, message: '请选择商品', trigger: 'change' }],
  actualQuantity: [{ required: true, message: '请输入实际数量', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入差异原因', trigger: 'blur' }]
}

// 订单列表
const orders = ref([])
// 商品列表
const products = ref([])

// 获取订单列表
const getOrders = async () => {
  try {
    const response = await fetch('http://127.0.0.1:8080/api/sale/orders?status=completed', {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0) {
      orders.value = result.data.list || []
    } else {
      ElMessage.error(result.message || '获取订单列表失败')
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  }
}

// 获取商品列表
const getProducts = async () => {
  try {
    const response = await fetch('http://127.0.0.1:8080/api/products', {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0) {
      products.value = result.data.list || []
    } else {
      ElMessage.error(result.message || '获取商品列表失败')
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败')
  }
}

// 处理订单选择变化
const handleOrderChange = async (orderId) => {
  try {
    const response = await fetch(`http://127.0.0.1:8080/api/sale/orders/${orderId}`, {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0) {
      const order = result.data
      form.orderNo = order.orderNo
      form.customerId = order.customerId
      form.customerName = order.customerName
      
      // 清空之前选择的商品
      form.productId = ''
      form.productName = ''
      form.orderQuantity = 0
      form.actualQuantity = 0
      form.varianceQuantity = 0
      form.unitPrice = 0
      form.amount = 0
      
      // 获取订单的商品列表
      try {
        const itemsResponse = await fetch(`http://127.0.0.1:8080/api/sale/orders/${orderId}/items`, {
          headers: {
            'Accept': 'application/json'
          },
          credentials: 'include'
        })
        const itemsResult = await itemsResponse.json()
        if (itemsResult.code === 0) {
          // 更新商品列表，只显示订单中的商品
          const orderItems = itemsResult.data.list || []
          products.value = products.value.filter(p => 
            orderItems.some(item => item.productId === p.id)
          )
        }
      } catch (error) {
        console.error('获取订单商品列表失败:', error)
        ElMessage.error('获取订单商品列表失败')
      }
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取订单详情失败')
  }
}

// 处理商品选择变化
const handleProductChange = async (productId) => {
  const product = products.value.find(p => p.id === productId)
  if (product) {
    form.productName = product.name
    form.unitPrice = product.price
    // 获取订单中该商品的数量
    if (form.orderId) {
      try {
        const response = await fetch(`http://127.0.0.1:8080/api/sale/orders/${form.orderId}/items?productId=${productId}`, {
          headers: {
            'Accept': 'application/json'
          },
          credentials: 'include'
        })
        const result = await response.json()
        if (result.code === 0) {
          form.orderQuantity = result.data.quantity || 0
          calculateVariance()
        }
      } catch (error) {
        console.error('获取订单商品数量失败:', error)
        ElMessage.error('获取订单商品数量失败')
      }
    }
  }
}

// 计算差异
const calculateVariance = () => {
  form.varianceQuantity = form.orderQuantity - form.actualQuantity
  form.amount = form.varianceQuantity * form.unitPrice
  // 根据差异数量自动设置差异类型
  if (form.varianceQuantity !== 0) {
    form.type = 'quantity'  // 数量差异
  } else if (form.amount !== 0) {
    form.type = 'amount'   // 金额差异
  } else {
    form.type = 'other'    // 其他差异
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const url = id
          ? `http://127.0.0.1:8080/api/sale/variances/${id}`
          : 'http://127.0.0.1:8080/api/sale/variances'
        const method = id ? 'PUT' : 'POST'
        
        const response = await fetch(url, {
          method,
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          },
          credentials: 'include',
          body: JSON.stringify(form)
        })
        
        const result = await response.json()
        if (result.code === 0) {
          ElMessage.success(id ? '更新成功' : '创建成功')
          router.back()
        } else {
          ElMessage.error(result.message || (id ? '更新失败' : '创建失败'))
        }
      } catch (error) {
        console.error(id ? '更新差异单失败:' : '创建差异单失败:', error)
        ElMessage.error(id ? '更新失败' : '创建失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 初始化
onMounted(async () => {
  // 先获取订单和商品列表
  await Promise.all([getOrders(), getProducts()])
  
  // 如果是编辑或查看模式，获取差异单详情
  if (id) {
    try {
      const response = await fetch(`http://127.0.0.1:8080/api/sale/variances/${id}`, {
        headers: {
          'Accept': 'application/json'
        },
        credentials: 'include'
      })
      const result = await response.json()
      if (result.code === 0) {
        const data = result.data
        // 先设置订单ID，触发订单相关数据的加载
        form.orderId = data.orderId
        await handleOrderChange(data.orderId)
        
        // 再设置商品ID
        form.productId = data.productId
        // 根据商品ID获取商品详情
        const product = products.value.find(p => p.id === data.productId)
        if (product) {
          data.productName = product.name
          data.unitPrice = product.price
        }
        // 最后设置其他数据
        Object.assign(form, data)
      } else {
        ElMessage.error(result.message || '获取差异单详情失败')
      }
    } catch (error) {
      console.error('获取差异单详情失败:', error)
      ElMessage.error('获取差异单详情失败')
    }
  }
})
</script>

<style scoped>
.variance-form {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-form {
  max-width: 1200px;
  margin: 0 auto;
}

.readonly-input :deep(.el-input__inner) {
  background-color: var(--el-input-disabled-bg);
  color: var(--el-text-color-regular);
  cursor: not-allowed;
}
</style> 