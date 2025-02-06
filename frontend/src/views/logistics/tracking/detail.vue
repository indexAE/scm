<template>
  <div class="tracking-detail">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>物流跟踪详情</span>
          <el-button link @click="handleBack">返回</el-button>
        </div>
      </template>

      <!-- 基本信息 -->
      <el-descriptions title="基本信息" :column="2" border>
        <el-descriptions-item label="订单号">{{ shipmentInfo.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(shipmentInfo.status)">{{ getStatusText(shipmentInfo.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="收货人">{{ shipmentInfo.consignee }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ shipmentInfo.consigneePhone }}</el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">{{ shipmentInfo.consigneeAddress }}</el-descriptions-item>
      </el-descriptions>

      <!-- 物流跟踪信息 -->
      <div class="tracking-info">
        <div class="tracking-title">物流跟踪信息</div>
        <el-timeline>
          <el-timeline-item
            v-for="(item, index) in trackingList"
            :key="index"
            :timestamp="formatDateTime(item.createTime)"
            :type="index === 0 ? 'primary' : ''"
          >
            <h4>{{ item.location }}</h4>
            <p>{{ item.description }}</p>
          </el-timeline-item>
        </el-timeline>
      </div>

      <!-- 添加物流信息表单 -->
      <div v-if="shipmentInfo.status === 'shipped'" class="add-tracking">
        <div class="tracking-title">添加物流信息</div>
        <el-form ref="formRef" :model="trackingForm" :rules="rules" label-width="100px">
          <el-form-item label="当前位置" prop="location">
            <el-input v-model="trackingForm.location" placeholder="请输入当前位置" />
          </el-form-item>
          <el-form-item label="物流描述" prop="description">
            <el-input
              v-model="trackingForm.description"
              type="textarea"
              :rows="3"
              placeholder="请输入物流描述"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSubmit">提交</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { formatDateTime } from '@/utils/format'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)

// 发货单信息
const shipmentInfo = ref({})
// 物流跟踪列表
const trackingList = ref([])
// 添加物流信息表单
const trackingForm = ref({
  location: '',
  description: ''
})

// 表单校验规则
const rules = {
  location: [{ required: true, message: '请输入当前位置', trigger: 'blur' }],
  description: [{ required: true, message: '请输入物流描述', trigger: 'blur' }]
}

// 获取发货单信息
const getShipmentInfo = async () => {
  try {
    const orderNo = route.query.orderNo
    const res = await request.get(`/api/logistics/shipment/${orderNo}`)
    shipmentInfo.value = res.data
  } catch (error) {
    ElMessage.error('获取发货单信息失败')
  }
}

// 获取物流跟踪列表
const getTrackingList = async () => {
  try {
    const orderNo = route.query.orderNo
    const res = await request.get(`/api/logistics/tracking/list/${orderNo}`)
    trackingList.value = res.data
  } catch (error) {
    ElMessage.error('获取物流跟踪列表失败')
  }
}

// 提交物流信息
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const data = {
          ...trackingForm.value,
          shipmentId: shipmentInfo.value.id
        }
        await request.post('/api/logistics/tracking', data)
        ElMessage.success('添加物流信息成功')
        handleReset()
        getTrackingList()
      } catch (error) {
        ElMessage.error('添加物流信息失败')
      }
    }
  })
}

// 重置表单
const handleReset = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 返回列表页
const handleBack = () => {
  router.back()
}

// 获取状态类型
const getStatusType = (status) => {
  const map = {
    pending: 'warning',
    shipped: 'primary',
    delivered: 'success',
    cancelled: 'info'
  }
  return map[status] || ''
}

// 获取状态文本
const getStatusText = (status) => {
  const map = {
    pending: '待发货',
    shipped: '已发货',
    delivered: '已送达',
    cancelled: '已取消'
  }
  return map[status] || ''
}

onMounted(() => {
  getShipmentInfo()
  getTrackingList()
})
</script>

<style scoped>
.tracking-detail {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tracking-info {
  margin-top: 20px;
}

.tracking-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 20px;
  padding-left: 10px;
  border-left: 4px solid var(--el-color-primary);
}

.add-tracking {
  margin-top: 20px;
}
</style> 