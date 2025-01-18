<template>
  <div class="order-form">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>{{ isView ? '查看采购订单' : (isEdit ? '编辑采购订单' : '新建采购订单') }}</span>
        </div>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" :disabled="formDisabled">
        <!-- 基本信息 -->
        <el-form-item label="供应商" prop="supplierId">
          <template v-if="formDisabled">
            {{ form.supplier }}
          </template>
          <el-select v-else v-model="form.supplierId" placeholder="请选择供应商" @change="handleSupplierChange">
            <el-option
              v-for="item in suppliers"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="采购员" prop="purchaser">
          <template v-if="formDisabled">
            {{ form.purchaser }}
          </template>
          <el-select 
            v-else
            v-model="form.purchaserId" 
            placeholder="请选择采购员"
            :loading="purchaserLoading"
            @change="handlePurchaserChange"
          >
            <el-option
              v-for="item in purchasers"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="预计交付日期" prop="expectedDeliveryDate">
          <template v-if="formDisabled">
            {{ form.expectedDeliveryDate }}
          </template>
          <el-date-picker
            v-else
            v-model="form.expectedDeliveryDate"
            type="date"
            placeholder="选择日期"
            :disabled-date="disabledDate"
          />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <template v-if="formDisabled">
            {{ form.remark }}
          </template>
          <el-input
            v-else
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
            <el-button type="primary" @click="handleAddItem" v-if="!formDisabled">添加商品</el-button>
          </div>

          <el-table :data="form.items" border style="width: 100%">
            <el-table-column label="商品名称" width="220">
              <template #default="scope">
                <template v-if="formDisabled">
                  {{ scope.row.productName }}
                </template>
                <el-select 
                  v-else
                  v-model="scope.row.productId" 
                  placeholder="请选择商品"
                  @change="(val) => handleProductChange(val, scope.$index)"
                >
                  <el-option
                    v-for="item in products"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                  />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column prop="productSpec" label="规格" width="150" />
            <el-table-column prop="unit" label="单位" width="180" />
            <el-table-column label="数量" width="200">
              <template #default="scope">
                <template v-if="formDisabled">
                  {{ scope.row.quantity }}
                </template>
                <el-input-number 
                  v-else
                  v-model="scope.row.quantity" 
                  :min="1"
                  @change="calculateItemAmount(scope.$index)"
                />
              </template>
            </el-table-column>
            <el-table-column label="单价" width="200">
              <template #default="scope">
                <template v-if="formDisabled">
                  {{ formatAmount(scope.row.price) }}
                </template>
                <el-input-number 
                  v-else
                  v-model="scope.row.price" 
                  :min="0"
                  :precision="2"
                  @change="calculateItemAmount(scope.$index)"
                />
              </template>
            </el-table-column>
            <el-table-column prop="amount" label="金额" width="150">
              <template #default="scope">
                {{ formatAmount(scope.row.amount) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" v-if="!formDisabled">
              <template #default="scope">
                <el-button 
                  type="danger" 
                  circle
                  @click="handleDeleteItem(scope.$index)"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
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
          <template v-if="formDisabled">
            <el-button @click="handleBack">返回</el-button>
          </template>
          <template v-else>
            <el-button @click="handleCancel">取消</el-button>
            <el-button type="primary" @click="handleSubmit">提交</el-button>
          </template>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)

// 是否为编辑模式
const isEdit = computed(() => route.query.id !== undefined)

// 表单数据
const form = reactive({
  supplierId: '',
  supplier: '',
  purchaserId: '',
  purchaser: '',
  expectedDeliveryDate: '',
  remark: '',
  items: []
})

// 表单验证规则
const rules = {
  supplierId: [
    { required: true, message: '请选择供应商', trigger: 'change' }
  ],
  purchaserId: [
    { required: true, message: '请选择采购员', trigger: 'change' }
  ],
  expectedDeliveryDate: [
    { required: true, message: '请选择预计交付日期', trigger: 'change' }
  ]
}

// 供应商列表
const suppliers = ref([])
// 采购员列表
const purchasers = ref([])
const purchaserLoading = ref(false)
// 商品列表
const products = ref([])

// 获取供应商列表
const getSuppliers = async () => {
  try {
    console.log('开始获取供应商列表')
    const res = await fetch('http://127.0.0.1:8080/api/suppliers', {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    console.log('供应商API响应:', res)
    const data = await res.json()
    console.log('供应商数据:', data)
    
    if (data.code === 0) {
      suppliers.value = data.data || []
      console.log('供应商列表设置完成:', suppliers.value)
    } else {
      console.error('获取供应商列表失败:', data.message)
      ElMessage.error(data.message || '获取供应商列表失败')
    }
  } catch (error) {
    console.error('获取供应商列表失败:', error)
    ElMessage.error('获取供应商列表失败')
  }
}

// 获取采购员列表
const getPurchasers = async () => {
  purchaserLoading.value = true
  try {
    console.log('开始获取采购员列表')
    const params = new URLSearchParams({
      status: 'enabled'
    })
    const res = await fetch(`http://127.0.0.1:8080/api/purchasers?${params.toString()}`, {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    console.log('采购员API响应:', res)
    const data = await res.json()
    console.log('采购员数据:', data)
    
    if (data.code === 0) {
      purchasers.value = data.data.list || []
      console.log('采购员列表设置完成:', purchasers.value)
      if (purchasers.value.length === 0) {
        ElMessage.warning('没有可用的采购员，请先添加采购员')
      }
    } else {
      console.error('获取采购员列表失败:', data.message)
      ElMessage.error(data.message || '获取采购员列表失败')
    }
  } catch (error) {
    console.error('获取采购员列表失败:', error)
    ElMessage.error('获取采购员列表失败')
  } finally {
    purchaserLoading.value = false
  }
}

// 获取商品列表
const getProducts = async () => {
  try {
    console.log('开始获取商品列表')
    const res = await fetch('http://127.0.0.1:8080/api/products', {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    console.log('商品API响应:', res)
    const data = await res.json()
    console.log('商品数据:', data)
    
    if (data.code === 0) {
      products.value = data.data.list || []
      console.log('商品列表设置完成:', products.value)
    } else {
      console.error('获取商品列表失败:', data.message)
      ElMessage.error(data.message || '获取商品列表失败')
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败')
  }
}

// 处理供应商选择变化
const handleSupplierChange = (supplierId) => {
  const supplier = suppliers.value.find(item => item.id === supplierId)
  if (supplier) {
    form.supplier = supplier.name
  }
}

// 处理采购员选择变化
const handlePurchaserChange = (value) => {
  const purchaser = purchasers.value.find(p => p.id === value)
  if (purchaser) {
    form.purchaser = purchaser.name
  }
}

// 禁用今天之前的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

// 处理商品选择变化
const handleProductChange = (productId, index) => {
  const product = products.value.find(item => item.id === productId)
  if (product) {
    form.items[index] = {
      ...form.items[index],
      productName: product.name,
      productSpec: product.spec,
      unit: product.unit,
      price: product.price
    }
    calculateItemAmount(index)
  }
}

// 添加商品明细
const handleAddItem = () => {
  form.items.push({
    productId: '',
    productName: '',
    productSpec: '',
    unit: '',
    quantity: 1,
    price: 0,
    amount: 0
  })
}

// 删除商品明细
const handleDeleteItem = (index) => {
  form.items.splice(index, 1)
}

// 计算商品金额
const calculateItemAmount = (index) => {
  const item = form.items[index]
  item.amount = item.quantity * item.price
}

// 格式化金额
const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return Number(amount).toFixed(2)
}

// 计算总金额
const totalAmount = computed(() => {
  return form.items.reduce((sum, item) => sum + (item.amount || 0), 0)
})

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.items.length === 0) {
        ElMessage.warning('请至少添加一个商品')
        return
      }

      try {
        // 生成订单编号
        const timestamp = new Date().getTime()
        const code = `PO${timestamp}`

        const url = isEdit.value 
          ? `http://127.0.0.1:8080/api/purchase/orders/${route.query.id}`
          : 'http://127.0.0.1:8080/api/purchase/orders'
        
        const method = isEdit.value ? 'PUT' : 'POST'
        const requestData = {
          ...form,
          code,
          amount: totalAmount.value
        }
        console.log('提交数据:', requestData)
        
        const res = await fetch(url, {
          method,
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          },
          credentials: 'include',
          body: JSON.stringify(requestData)
        })
        console.log('提交response:', res)
        const data = await res.json()
        console.log('提交result:', data)
        
        if (data.code === 0) {
          ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
          router.push('/dashboard/purchase/order')
        } else {
          ElMessage.error(data.message || (isEdit.value ? '更新失败' : '创建失败'))
        }
      } catch (error) {
        console.error(isEdit.value ? '更新采购订单失败:' : '创建采购订单失败:', error)
        ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
      }
    } else {
      console.log('表单验证未通过')
      return false
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
    router.push('/dashboard/purchase/order')
  }).catch(() => {})
}

// 获取采购订单详情
const getOrderDetail = async (id) => {
  try {
    console.log('开始获取订单详情, id:', id)
    const res = await fetch(`http://127.0.0.1:8080/api/purchase/orders/${id}`, {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    console.log('订单详情API响应:', res)
    const data = await res.json()
    console.log('订单详情数据:', data)
    
    if (data.code === 0) {
      const { 
        code,
        supplierId, 
        supplier, 
        purchaserId, 
        purchaser, 
        expectedDeliveryDate, 
        remark, 
        items,
        status,
        amount 
      } = data.data
      
      // 格式化日期
      const formattedDate = expectedDeliveryDate ? new Date(expectedDeliveryDate).toISOString().split('T')[0] : ''
      
      // 处理商品明细数据
      const formattedItems = items.map(item => ({
        productId: item.productId,
        productName: item.productName,
        productSpec: item.productSpec,
        unit: item.unit,
        quantity: item.quantity,
        price: item.price,
        amount: item.amount
      }))

      // 更新表单数据
      Object.assign(form, { 
        code,
        supplierId, 
        supplier, 
        purchaserId, 
        purchaser, 
        expectedDeliveryDate: formattedDate, 
        remark, 
        items: formattedItems,
        status,
        amount
      })
      
      console.log('表单数据设置完成:', form)
    } else {
      console.error('获取订单详情失败:', data.message)
      ElMessage.error(data.message || '获取采购订单详情失败')
    }
  } catch (error) {
    console.error('获取采购订单详情失败:', error)
    ElMessage.error('获取采购订单详情失败')
  }
}

// 是否为查看模式
const isView = computed(() => route.query.type === 'view')

// 表单禁用状态
const formDisabled = computed(() => isView.value)

// 返回列表
const handleBack = () => {
  router.push('/dashboard/purchase/order')
}

onMounted(async () => {
  await Promise.all([getSuppliers(), getPurchasers(), getProducts()])
  if (isEdit.value) {
    await getOrderDetail(route.query.id)
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