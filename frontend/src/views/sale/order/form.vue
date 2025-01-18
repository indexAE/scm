<template>
  <div class="sale-order-form">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="订单编号">
        <el-input v-model="form.orderNo" disabled />
      </el-form-item>
      
      <el-form-item label="客户" prop="customerId">
        <el-select v-model="form.customerId" placeholder="请选择客户" @change="handleCustomerChange">
          <el-option
            v-for="item in customers"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      
      <el-form-item label="经办人" prop="handlerId">
        <el-select v-model="form.handlerId" placeholder="请选择经办人" @change="handleHandlerChange">
          <el-option
            v-for="item in handlers"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      
      <el-form-item label="订单商品" prop="items">
        <div v-for="(item, index) in form.items" :key="index" class="order-item">
          <el-select
            v-model="item.productId"
            placeholder="请选择商品"
            @change="(val) => handleProductChange(val, index)"
            style="width: 200px"
          >
            <el-option
              v-for="product in products"
              :key="product.id"
              :label="product.name"
              :value="product.id"
            />
          </el-select>
          
          <el-input v-model="item.spec" placeholder="规格" disabled style="width: 120px" />
          <el-input v-model="item.unit" placeholder="单位" disabled style="width: 80px" />
          
          <el-input-number
            v-model="item.quantity"
            :min="1"
            @change="() => calculateItemAmount(index)"
            style="width: 120px"
          />
          
          <el-input-number
            v-model="item.unitPrice"
            :min="0"
            :precision="2"
            @change="() => calculateItemAmount(index)"
            style="width: 120px"
          />
          
          <el-input
            v-model="item.totalPrice"
            disabled
            style="width: 120px"
          />
          
          <el-button type="danger" @click="handleDeleteItem(index)">删除</el-button>
        </div>
        
        <el-button type="primary" @click="handleAddItem">添加商品</el-button>
      </el-form-item>
      
      <el-form-item label="总金额">
        <el-input v-model="form.totalAmount" disabled />
      </el-form-item>
      
      <el-form-item label="备注">
        <el-input
          v-model="form.remark"
          type="textarea"
          :rows="3"
          placeholder="请输入备注信息"
        />
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ id ? '更新' : '创建' }}
        </el-button>
        <el-button @click="router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)

// 获取路由参数
const id = route.query.id

// 表单数据
const form = reactive({
  orderNo: '',
  customerId: '',
  customerName: '',
  handlerId: '',
  handlerName: '',
  totalAmount: 0,
  remark: '',
  items: []
})

// 表单验证规则
const rules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  handlerId: [{ required: true, message: '请选择经办人', trigger: 'change' }],
  items: [{ required: true, type: 'array', message: '请添加订单商品', trigger: 'change' }]
}

// 客户列表
const customers = ref([])
// 经办人列表
const handlers = ref([])
// 商品列表
const products = ref([])

// 获取客户列表
const getCustomers = async () => {
  try {
    const res = await fetch('/api/customers', {
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    })
    const data = await res.json()
    if (data.code === 0) {
      customers.value = data.data || []
      console.log('客户列表数据:', customers.value)
    } else {
      ElMessage.error(data.message || '获取客户列表失败')
    }
  } catch (error) {
    console.error('获取客户列表失败:', error)
    ElMessage.error('获取客户列表失败')
  }
}

// 获取经办人列表
const getHandlers = async () => {
  try {
    const res = await fetch('/api/handlers', {
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    })
    const data = await res.json()
    if (data.code === 0) {
      handlers.value = data.data?.list || []
      console.log('经办人列表数据:', handlers.value)
    } else {
      ElMessage.error(data.message || '获取经办人列表失败')
    }
  } catch (error) {
    console.error('获取经办人列表失败:', error)
    ElMessage.error('获取经办人列表失败')
  }
}

// 获取商品列表
const getProducts = async () => {
  try {
    const params = new URLSearchParams({
      offset: '0',
      limit: '1000'  // 获取足够多的商品
    })
    
    const res = await fetch(`/api/products?${params}`, {
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    })
    const data = await res.json()
    if (data.code === 0) {
      products.value = data.data?.list || []
      console.log('商品列表数据:', products.value)
    } else {
      console.error('获取商品列表失败, 返回数据:', data)
      ElMessage.error(data.message || '获取商品列表失败')
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败')
  }
}

// 获取订单详情
const getOrderDetail = async () => {
  try {
    const response = await fetch(`/api/sale/orders/${id}`, {
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0) {
      Object.assign(form, result.data)
    } else {
      ElMessage.error(result.message || '获取订单详情失败')
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取订单详情失败')
  }
}

// 处理客户选择变化
const handleCustomerChange = (customerId) => {
  const customer = customers.value.find(c => c.id === customerId)
  if (customer) {
    form.customerName = customer.name
  }
}

// 处理经办人选择变化
const handleHandlerChange = (handlerId) => {
  const handler = handlers.value.find(h => h.id === handlerId)
  if (handler) {
    form.handlerName = handler.name
  }
}

// 处理商品选择变化
const handleProductChange = (productId, index) => {
  console.log('选择的商品ID:', productId)
  console.log('当前商品列表:', products.value)
  const product = products.value.find(p => p.id === productId)
  console.log('找到的商品:', product)
  if (product) {
    form.items[index] = {
      ...form.items[index],
      productId: product.id,
      productName: product.name,
      spec: product.spec,
      unit: product.unit,
      unitPrice: product.price || 0,
      quantity: 1,
      totalPrice: product.price || 0
    }
    calculateItemAmount(index)
  }
}

// 添加商品明细
const handleAddItem = () => {
  form.items.push({
    productId: '',
    productName: '',
    spec: '',  // 修改字段名以匹配后端
    unit: '',
    quantity: 1,
    unitPrice: 0,
    totalPrice: 0
  })
}

// 删除商品明细
const handleDeleteItem = (index) => {
  form.items.splice(index, 1)
}

// 计算商品金额
const calculateItemAmount = (index) => {
  const item = form.items[index]
  item.totalPrice = item.quantity * item.unitPrice
  // 计算总金额
  form.totalAmount = form.items.reduce((sum, item) => sum + (item.totalPrice || 0), 0)
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.items.length === 0) {
        ElMessage.warning('请添加订单商品')
        return
      }

      submitting.value = true
      try {
        const url = id
          ? `/api/sale/orders/${id}`
          : '/api/sale/orders'
        const method = id ? 'PUT' : 'POST'
        
        // 构造提交的数据
        const submitData = {
          ...form,
          status: 'pending',  // 添加默认状态
          customerId: form.customerId,
          customerName: form.customerName,
          handlerId: form.handlerId,
          handlerName: form.handlerName,
          items: form.items.map(item => ({
            productId: item.productId,
            productName: item.productName,
            spec: item.spec,
            unit: item.unit,
            quantity: item.quantity,
            unitPrice: item.unitPrice,
            totalPrice: item.totalPrice
          }))
        }
        
        console.log('提交的数据:', submitData)
        
        const response = await fetch(url, {
          method,
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          },
          credentials: 'include',
          body: JSON.stringify(submitData)
        })
        
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        
        const result = await response.json()
        if (result.code === 0) {
          ElMessage.success(id ? '更新成功' : '创建成功')
          router.back()
        } else {
          ElMessage.error(result.message || (id ? '更新失败' : '创建失败'))
        }
      } catch (error) {
        console.error(id ? '更新订单失败:' : '创建订单失败:', error)
        ElMessage.error(id ? '更新失败' : '创建失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 初始化
onMounted(() => {
  getCustomers()
  getHandlers()
  getProducts()
  if (id) {
    getOrderDetail()
  }
})
</script>

<style scoped>
.order-form {
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

.table-footer {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style> 