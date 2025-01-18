<template>
  <div class="sale-return-form">
    <div class="page-header">
      <h2>{{ id ? '编辑退货单' : '新增退货单' }}</h2>
    </div>

    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="退货单号">
        <el-input v-model="form.returnNo" disabled />
      </el-form-item>
      
      <el-form-item label="关联订单" prop="orderId">
        <el-select 
          v-model="form.orderId" 
          placeholder="请选择关联订单" 
          @change="handleOrderChange"
          style="width: 100%"
        >
          <el-option
            v-for="item in orderList"
            :key="item.id"
            :label="item.orderNo"
            :value="item.id"
          >
            <span>{{ item.orderNo }}</span>
            <span style="float: right; color: #8492a6; font-size: 13px">
              {{ item.customerName }}
            </span>
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="客户名称">
        <el-input v-model="form.customerName" disabled />
      </el-form-item>

      <el-form-item label="经办人" prop="handlerId">
        <el-select 
          v-model="form.handlerId" 
          placeholder="请选择经办人"
          @change="handleHandlerChange"
          style="width: 100%"
        >
          <el-option
            v-for="item in handlerList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          >
            <span>{{ item.name }}</span>
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="退货商品" prop="items">
        <div v-for="(item, index) in form.items" :key="index" class="return-item">
          <el-select
            v-model="item.productId"
            placeholder="请选择商品"
            @change="(val) => handleProductChange(val, index)"
            style="width: 200px"
          >
            <el-option
              v-for="product in productList"
              :key="product.id"
              :label="product.name"
              :value="product.id"
            >
              <span>{{ product.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">
                {{ product.spec }}
              </span>
            </el-option>
          </el-select>
          
          <el-input v-model="item.spec" placeholder="规格" disabled style="width: 120px" />
          <el-input v-model="item.unit" placeholder="单位" disabled style="width: 80px" />
          
          <el-input-number
            v-model="item.quantity"
            :min="1"
            :max="item.maxQuantity"
            @change="() => calculateItemAmount(index)"
            style="width: 120px"
          />
          
          <el-input-number
            v-model="item.unitPrice"
            :min="0"
            :precision="2"
            disabled
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
      
      <el-form-item label="退货说明" prop="remark">
        <el-input
          v-model="form.remark"
          type="textarea"
          :rows="3"
          placeholder="请输入退货说明"
        />
      </el-form-item>

      <el-form-item label="总金额">
        <el-input v-model="form.totalAmount" disabled />
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ id ? '更新' : '提交' }}
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
  returnNo: '',
  orderId: '',
  orderNo: '',
  customerId: '',
  customerName: '',
  totalAmount: 0,
  remark: '',
  handlerId: '',
  handlerName: '',
  items: []
})

// 表单验证规则
const rules = {
  orderId: [{ required: true, message: '请选择关联订单', trigger: 'change' }],
  handlerId: [{ required: true, message: '请选择经办人', trigger: 'change' }],
  remark: [{ required: true, message: '请输入退货说明', trigger: 'blur' }],
  items: [{ required: true, type: 'array', message: '请添加退货商品', trigger: 'change' }]
}

// 订单列表
const orderList = ref([])
// 商品列表
const productList = ref([])
// 经办人列表
const handlerList = ref([])

// 获取订单列表
const getOrders = async () => {
  try {
    const response = await fetch('/api/sale/orders?status=approved', {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0) {
      orderList.value = result.data.list || []
      // 打印订单列表数据,用于调试
      console.log('订单列表:', orderList.value)
    } else {
      ElMessage.error(result.message || '获取订单列表失败')
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  }
}

// 获取订单详情
const getOrderDetail = async (orderId) => {
  try {
    console.log('获取订单详情,orderId:', orderId)
    const response = await fetch(`/api/sale/orders/${orderId}`, {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0) {
      const order = result.data
      if (!order) {
        ElMessage.error('获取订单详情失败: 订单不存在')
        return
      }
      
      console.log('订单原始数据:', order)
      
      form.orderNo = order.orderNo
      form.customerId = order.customerId
      form.customerName = order.customerName
      
      // 不再覆盖productList
      console.log('商品列表:', productList.value)
    } else {
      ElMessage.error(result.message || '获取订单详情失败')
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取订单详情失败')
  }
}

// 处理订单选择变化
const handleOrderChange = (orderId) => {
  if (orderId) {
    getOrderDetail(orderId)
    // 清空已选商品
    form.items = []
  }
}

// 处理商品选择变化
const handleProductChange = (productId, index) => {
  console.log('选择商品:', productId, '索引:', index)
  console.log('当前商品列表:', productList.value)
  
  const product = productList.value.find(p => p.id === productId)
  console.log('找到的商品:', product)
  
  if (product) {
    form.items[index] = {
      ...form.items[index],
      productId: product.id,
      productName: product.name,
      spec: product.spec,
      unit: product.unit,
      unitPrice: product.price,
      quantity: 1
    }
    calculateItemAmount(index)
  }
}

// 计算商品金额
const calculateItemAmount = (index) => {
  const item = form.items[index]
  if (item.quantity && item.unitPrice) {
    item.totalPrice = (item.quantity * item.unitPrice).toFixed(2)
  }
  calculateTotalAmount()
}

// 计算总金额
const calculateTotalAmount = () => {
  form.totalAmount = form.items.reduce((sum, item) => {
    return sum + (parseFloat(item.totalPrice) || 0)
  }, 0).toFixed(2)
}

// 添加商品
const handleAddItem = () => {
  form.items.push({
    productId: '',
    productName: '',
    spec: '',
    unit: '',
    quantity: 1,
    unitPrice: 0,
    totalPrice: 0
  })
}

// 删除商品
const handleDeleteItem = (index) => {
  form.items.splice(index, 1)
  calculateTotalAmount()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const url = id ? `/api/sale/returns/${id}` : '/api/sale/returns'
        const method = id ? 'PUT' : 'POST'
        
        // 准备提交数据
        const submitData = {
          ...form,
          customerId: form.customerId,
          totalAmount: parseFloat(form.totalAmount),
          handlerId: form.handlerId,
          handlerName: form.handlerName,
          status: 'pending', // 设置初始状态
          items: form.items.map(item => ({
            orderId: form.orderId,
            productId: item.productId,
            productName: item.productName,
            spec: item.spec,
            unit: item.unit,
            quantity: parseInt(item.quantity),
            unitPrice: parseFloat(item.unitPrice),
            totalPrice: parseFloat(item.totalPrice)
          }))
        }
        
        // 删除returnNo字段
        delete submitData.returnNo
        
        // 打印提交的数据用于调试
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
        
        const result = await response.json()
        if (result.code === 0) {
          ElMessage.success(id ? '更新成功' : '创建成功')
          router.back()
        } else {
          ElMessage.error(result.message || (id ? '更新失败' : '创建失败'))
        }
      } catch (error) {
        console.error(id ? '更新退货单失败:' : '创建退货单失败:', error)
        ElMessage.error(id ? '更新失败' : '创建失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 获取退货单详情
const getDetail = async () => {
  try {
    const response = await fetch(`/api/sale/returns/${id}`, {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0) {
      Object.assign(form, result.data)
      // 获取关联订单的商品列表
      if (form.orderId) {
        await getOrderDetail(form.orderId)
      }
    } else {
      ElMessage.error(result.message || '获取退货单详情失败')
    }
  } catch (error) {
    console.error('获取退货单详情失败:', error)
    ElMessage.error('获取退货单详情失败')
  }
}

// 获取商品列表
const getProducts = async () => {
  try {
    const response = await fetch('/api/products', {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0) {
      productList.value = result.data.list || []
      console.log('商品列表:', productList.value)
    } else {
      ElMessage.error(result.message || '获取商品列表失败')
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败')
  }
}

// 获取经办人列表
const getHandlers = async () => {
  try {
    const response = await fetch('/api/handlers', {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0) {
      handlerList.value = result.data.list || []
      console.log('经办人列表数据:', handlerList.value)
    } else {
      ElMessage.error(result.message || '获取经办人列表失败')
    }
  } catch (error) {
    console.error('获取经办人列表失败:', error)
    ElMessage.error('获取经办人列表失败')
  }
}

// 处理经办人选择变化
const handleHandlerChange = (handlerId) => {
  if (handlerId) {
    const handler = handlerList.value.find(h => h.id === handlerId)
    if (handler) {
      form.handlerName = handler.name
    }
  }
}

onMounted(async () => {
  // 获取商品列表
  await getProducts()
  // 获取订单列表
  await getOrders()
  // 获取经办人列表
  await getHandlers()
  
  // 如果是编辑模式，获取退货单详情
  if (id) {
    await getDetail()
  }
})
</script>

<style scoped>
.sale-return-form {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.return-item {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  align-items: center;
}
</style> 