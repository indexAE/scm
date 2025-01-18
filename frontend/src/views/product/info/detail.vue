<template>
  <div class="product-detail">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>商品详情</span>
          <el-button @click="handleBack">返回</el-button>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="商品编码">{{ detail.code }}</el-descriptions-item>
        <el-descriptions-item label="商品名称">{{ detail.name }}</el-descriptions-item>
        <el-descriptions-item label="商品分类">{{ detail.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="规格">{{ detail.spec }}</el-descriptions-item>
        <el-descriptions-item label="单位">{{ detail.unit }}</el-descriptions-item>
        <el-descriptions-item label="价格">{{ formatAmount(detail.price) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detail.status === 'enabled' ? 'success' : 'danger'" disable-transitions>
            {{ detail.status === 'enabled' ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="库存">{{ detail.stock }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="2">{{ detail.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

// 商品详情数据
const detail = ref({
  code: '',
  name: '',
  categoryName: '',
  spec: '',
  unit: '',
  price: 0,
  stock: 0,
  status: '',
  remark: '',
  createTime: '',
  updateTime: ''
})

// 格式化金额
const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return Number(amount).toFixed(2)
}

// 获取商品详情
const getDetail = async (id) => {
  try {
    const response = await fetch(`/api/products/${id}`)
    const result = await response.json()
    if (result.code === 0) {
      detail.value = result.data
    } else {
      ElMessage.error(result.message || '获取商品详情失败')
    }
  } catch (error) {
    console.error('获取商品详情失败:', error)
    ElMessage.error('获取商品详情失败')
  }
}

// 返回
const handleBack = () => {
  router.push('/dashboard/product/info')
}

onMounted(() => {
  if (route.query.id) {
    getDetail(route.query.id)
  }
})
</script>

<style scoped>
.product-detail {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 