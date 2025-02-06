<template>
  <div class="logistics-shipment-form">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ formTitle }}</span>
          <el-button @click="handleBack">返回</el-button>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="shipment-form"
        :disabled="mode === 'view'"
      >
        <el-form-item label="订单号" prop="orderNo">
          <el-input v-model="form.orderNo" placeholder="请输入订单号" />
        </el-form-item>

        <el-form-item label="收货人" prop="consignee">
          <el-input v-model="form.consignee" placeholder="请输入收货人姓名" />
        </el-form-item>

        <el-form-item label="联系电话" prop="consigneePhone">
          <el-input v-model="form.consigneePhone" placeholder="请输入联系电话" />
        </el-form-item>

        <el-form-item label="收货地址" prop="consigneeAddress">
          <el-input
            v-model="form.consigneeAddress"
            type="textarea"
            :rows="3"
            placeholder="请输入收货地址"
          />
        </el-form-item>

        <el-form-item label="发货人" prop="shipper">
          <el-input v-model="form.shipper" placeholder="请输入发货人姓名" />
        </el-form-item>

        <el-form-item label="发货电话" prop="shipperPhone">
          <el-input v-model="form.shipperPhone" placeholder="请输入发货人电话" />
        </el-form-item>

        <el-form-item label="发货地址" prop="shipperAddress">
          <el-input
            v-model="form.shipperAddress"
            type="textarea"
            :rows="3"
            placeholder="请输入发货地址"
          />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            v-if="mode !== 'view'"
            type="primary"
            :loading="submitting"
            @click="handleSubmit"
          >
            {{ mode === 'edit' ? '保存' : '创建' }}
          </el-button>
          <el-button @click="handleBack">取消</el-button>
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

// 表单模式：add-新增，edit-编辑，view-查看
const mode = computed(() => route.query.mode || 'add')

// 表单标题
const formTitle = computed(() => {
  const titles = {
    add: '新增发货',
    edit: '编辑发货',
    view: '查看发货'
  }
  return titles[mode.value]
})

// 表单数据
const form = reactive({
  orderNo: '',
  consignee: '',
  consigneePhone: '',
  consigneeAddress: '',
  shipper: '',
  shipperPhone: '',
  shipperAddress: '',
  remark: ''
})

// 表单验证规则
const rules = {
  orderNo: [
    { required: true, message: '请输入订单号', trigger: 'blur' }
  ],
  consignee: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' }
  ],
  consigneePhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  consigneeAddress: [
    { required: true, message: '请输入收货地址', trigger: 'blur' }
  ],
  shipper: [
    { required: true, message: '请输入发货人姓名', trigger: 'blur' }
  ],
  shipperPhone: [
    { required: true, message: '请输入发货人电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  shipperAddress: [
    { required: true, message: '请输入发货地址', trigger: 'blur' }
  ]
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid, fields) => {
    if (valid) {
      submitting.value = true
      try {
        const url = mode.value === 'add' 
          ? '/api/logistics/shipment'
          : `/api/logistics/shipment/${route.query.id}`
        const method = mode.value === 'add' ? 'POST' : 'PUT'
        const response = await fetch(url, {
          method,
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(form),
          credentials: 'include'
        })
        const result = await response.json()
        if (result.code === 0) {
          ElMessage.success(mode.value === 'add' ? '创建成功' : '保存成功')
          handleBack()
        } else {
          ElMessage.error(result.message || (mode.value === 'add' ? '创建失败' : '保存失败'))
        }
      } catch (error) {
        console.error('提交表单失败:', error)
        ElMessage.error(mode.value === 'add' ? '创建失败' : '保存失败')
      } finally {
        submitting.value = false
      }
    } else {
      console.log('表单校验失败:', fields)
    }
  })
}

// 返回列表页
const handleBack = () => {
  router.push('/dashboard/logistics/shipment')
}

// 获取详情
const getDetail = async (id) => {
  try {
    const response = await fetch(`/api/logistics/shipment/${id}`, {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0) {
      Object.assign(form, result.data)
    } else {
      ElMessage.error(result.message || '获取详情失败')
    }
  } catch (error) {
    console.error('获取详情失败:', error)
    ElMessage.error('获取详情失败')
  }
}

// 初始化
onMounted(() => {
  const { id } = route.query
  if (id && mode.value !== 'add') {
    getDetail(id)
  }
})
</script>

<style scoped>
.logistics-shipment-form {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.shipment-form {
  max-width: 800px;
  margin: 0 auto;
}
</style> 