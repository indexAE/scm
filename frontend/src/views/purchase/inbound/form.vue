<template>
  <div class="inbound-form">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>{{ getPageTitle }}</span>
        </div>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" :disabled="isView">
        <!-- 基本信息 -->
        <el-form-item label="关联订单" prop="orderId">
          <el-select 
            v-model="form.orderId" 
            placeholder="请选择采购订单"
            @change="handleOrderChange"
            style="width: 100%">
            <el-option
              v-for="item in orderOptions"
              :key="item.id"
              :label="item.code"
              :value="item.id">
              <span>订单编号: {{ item.code }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">
                供应商: {{ item.supplier }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="入库仓库" prop="warehouseId">
          <el-select v-model="form.warehouseId" placeholder="请选择仓库" @change="handleWarehouseChange">
            <el-option
              v-for="item in warehouses"
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

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注信息"
          />
        </el-form-item>

        <!-- 商品明细 -->
        <div class="items-section">
          <div class="items-header">
            <h3>商品明细</h3>
          </div>

          <el-table :data="form.items" border style="width: 100%">
            <el-table-column prop="productName" label="商品名称" width="200" />
            <el-table-column prop="productSpec" label="规格" width="150" />
            <el-table-column prop="unit" label="单位" width="150" />
            <el-table-column prop="orderQuantity" label="订单数量" width="150" />
            <el-table-column label="入库数量" width="200">
              <template #default="scope">
                <el-input-number 
                  v-model="scope.row.quantity" 
                  :min="1"
                  :max="scope.row.orderQuantity"
                />
              </template>
            </el-table-column>
            <el-table-column prop="price" label="单价" width="150">
              <template #default="scope">
                {{ formatAmount(scope.row.price) }}
              </template>
            </el-table-column>
            <el-table-column prop="amount" label="金额" width="150">
              <template #default="scope">
                {{ formatAmount(scope.row.price * scope.row.quantity) }}
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 合计金额 -->
        <div class="total-amount">
          合计金额：{{ formatAmount(totalAmount) }}
        </div>

        <!-- 表单操作按钮 -->
        <div class="form-actions">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="handleSubmit">提交</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)

// 页面模式
const mode = computed(() => route.query.mode || 'add')
const isEdit = computed(() => mode.value === 'edit')
const isView = computed(() => mode.value === 'view')

// 页面标题
const getPageTitle = computed(() => {
  const titles = {
    add: '新建入库单',
    edit: '编辑入库单',
    view: '查看入库单'
  }
  return titles[mode.value] || titles.add
})

// 表单数据
const form = reactive({
  orderId: '',
  orderCode: '',
  warehouseId: '',
  warehouse: '',
  handlerId: '',
  handler: '',
  remark: '',
  items: []
})

// 表单验证规则
const rules = {
  orderId: [
    { required: true, message: '请选择采购订单', trigger: 'change' }
  ],
  warehouseId: [
    { required: true, message: '请选择入库仓库', trigger: 'change' }
  ],
  handlerId: [
    { required: true, message: '请选择经办人', trigger: 'change' }
  ]
}

// 仓库列表
const warehouses = ref([])
// 经办人列表
const handlers = ref([])
// 可选的采购订单列表
const orderOptions = ref([])

// 获取仓库列表
const getWarehouses = async () => {
  try {
    const res = await fetch('http://127.0.0.1:8080/api/warehouses?status=normal', {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    console.log('仓库列表API响应:', res)
    const data = await res.json()
    console.log('仓库列表数据:', data)
    
    if (data.code === 0) {
      warehouses.value = data.data?.list || []
    } else {
      console.error('获取仓库列表失败:', data.message)
      ElMessage.error(data.message || '获取仓库列表失败')
    }
  } catch (error) {
    console.error('获取仓库列表失败:', error)
    ElMessage.error('获取仓库列表失败')
  }
}

// 获取经办人列表
const getHandlers = async () => {
  try {
    const res = await fetch('http://127.0.0.1:8080/api/handlers?status=enabled', {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    console.log('经办人列表API响应:', res)
    const data = await res.json()
    console.log('经办人列表数据:', data)
    
    if (data.code === 0) {
      handlers.value = data.data?.list || []
    } else {
      console.error('获取经办人列表失败:', data.message)
      ElMessage.error(data.message || '获取经办人列表失败')
    }
  } catch (error) {
    console.error('获取经办人列表失败:', error)
    ElMessage.error('获取经办人列表失败')
  }
}

// 获取可入库的采购订单列表
const getAvailableOrders = async () => {
  try {
    console.log('开始获取可入库的采购订单列表')
    const res = await fetch('http://127.0.0.1:8080/api/purchase/orders/available-for-inbound', {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    console.log('采购订单API响应:', res)
    const data = await res.json()
    console.log('采购订单数据:', data)
    
    if (data.code === 0) {
      orderOptions.value = data.data || []
      console.log('采购订单列表设置完成:', orderOptions.value)
      if (orderOptions.value.length === 0) {
        ElMessage.warning('没有可入库的采购订单')
      }
    } else {
      console.error('获取采购订单列表失败:', data.message)
      ElMessage.error(data.message || '获取采购订单列表失败')
    }
  } catch (error) {
    console.error('获取采购订单列表失败:', error)
    ElMessage.error('获取采购订单列表失败')
  }
}

// 选择采购订单时的处理
const handleOrderChange = async (orderId) => {
  if (!orderId) return
  try {
    console.log('开始获取订单明细, orderId:', orderId)
    const res = await fetch(`http://127.0.0.1:8080/api/purchase/orders/${orderId}`, {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    console.log('订单明细API响应:', res)
    const data = await res.json()
    console.log('订单明细数据:', data)
    
    if (data.code === 0) {
      const order = data.data
      console.log('订单数据:', order)
      form.orderCode = order.code
      // 确保items存在且是数组
      const items = order.items || []
      form.items = items.map(item => ({
        ...item,
        orderQuantity: item.quantity,
        quantity: item.quantity,
        productName: item.productName || item.name
      }))
    } else {
      console.error('获取订单明细失败:', data.message)
      ElMessage.error(data.message || '获取订单明细失败')
    }
  } catch (error) {
    console.error('获取订单明细失败:', error)
    ElMessage.error('获取订单明细失败')
  }
}

// 处理仓库选择变化
const handleWarehouseChange = (warehouseId) => {
  const warehouse = warehouses.value.find(item => item.id === warehouseId)
  if (warehouse) {
    form.warehouse = warehouse.name
  }
}

// 处理经办人选择变化
const handleHandlerChange = (handlerId) => {
  const handler = handlers.value.find(item => item.id === handlerId)
  if (handler) {
    form.handler = handler.name
  }
}

// 格式化金额
const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return Number(amount).toFixed(2)
}

// 计算总金额
const totalAmount = computed(() => {
  return form.items.reduce((sum, item) => sum + (item.price * item.quantity || 0), 0)
})

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.items.length === 0) {
        ElMessage.warning('请选择采购订单')
        return
      }

      try {
        // 构造提交的数据
        const submitData = {
          orderId: form.orderId,
          orderCode: form.orderCode,
          warehouseId: form.warehouseId,
          warehouse: form.warehouse,
          handlerId: form.handlerId,
          handler: form.handler,
          remark: form.remark,
          items: form.items.map(item => ({
            orderId: form.orderId,
            orderItemId: item.id,
            productId: item.productId,
            productName: item.productName,
            productSpec: item.productSpec,
            unit: item.unit,
            orderQuantity: item.orderQuantity,
            quantity: item.quantity,
            price: Number(item.price),
            amount: Number((item.price * item.quantity).toFixed(2))
          })),
          amount: Number(totalAmount.value.toFixed(2))
        }

        console.log('准备提交入库单数据:', submitData)
        
        const url = isEdit.value 
          ? `http://127.0.0.1:8080/api/purchase/inbounds/${route.query.id}`
          : 'http://127.0.0.1:8080/api/purchase/inbounds'
        
        const method = isEdit.value ? 'PUT' : 'POST'
        const res = await fetch(url, {
          method,
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          },
          credentials: 'include',
          body: JSON.stringify(submitData)
        })

        console.log('入库单提交响应:', res)
        const data = await res.json()
        console.log('入库单提交结果:', data)

        if (data.code === 0) {
          ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
          router.push('/dashboard/purchase/inbound')
        } else {
          console.error('提交失败:', data)
          ElMessage.error(data.message || (isEdit.value ? '更新失败' : '创建失败'))
        }
      } catch (error) {
        console.error(isEdit.value ? '更新入库单失败:' : '创建入库单失败:', error)
        ElMessage.error(error.message || (isEdit.value ? '更新失败' : '创建失败'))
      }
    }
  })
}

// 取消
const handleCancel = () => {
  ElMessageBox.confirm(
    '确认放弃当前编辑的内容？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    router.push('/dashboard/purchase/inbound')
  }).catch(() => {})
}

// 获取入库单详情
const getInboundDetail = async (id) => {
  try {
    console.log('开始获取入库单详情, id:', id)
    const res = await fetch(`http://127.0.0.1:8080/api/purchase/inbounds/${id}`, {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    console.log('入库单详情API响应:', res)
    const data = await res.json()
    console.log('入库单详情数据:', data)
    
    if (data.code === 0) {
      const inbound = data.data
      console.log('入库单数据:', inbound)
      
      // 填充表单数据
      form.orderId = inbound.orderId
      form.orderCode = inbound.orderCode
      form.warehouseId = inbound.warehouseId
      form.warehouse = inbound.warehouse
      form.handlerId = inbound.handlerId
      form.handler = inbound.handler
      form.remark = inbound.remark
      
      // 添加空值检查
      if (inbound.items && Array.isArray(inbound.items)) {
        form.items = inbound.items.map(item => ({
          ...item,
          orderQuantity: item.orderQuantity || item.quantity,
          quantity: item.quantity,
          amount: item.price * item.quantity
        }))
      } else {
        form.items = []
        console.warn('入库单商品明细为空')
      }
      
      console.log('表单数据设置完成:', form)
    } else {
      console.error('获取入库单详情失败:', data.message)
      ElMessage.error(data.message || '获取入库单详情失败')
    }
  } catch (error) {
    console.error('获取入库单详情失败:', error)
    ElMessage.error('获取入库单详情失败')
  }
}

// 在组件挂载时获取数据
onMounted(async () => {
  const id = route.query.id
  if (id && (isEdit.value || isView.value)) {
    await getInboundDetail(id)
  }
  await Promise.all([
    getWarehouses(),
    getHandlers(),
    getAvailableOrders()
  ])
})
</script>

<style scoped>
.inbound-form {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.items-section {
  margin: 20px 0;
}

.items-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.total-amount {
  text-align: right;
  margin: 20px 0;
  font-size: 16px;
  font-weight: bold;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}
</style> 