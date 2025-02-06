<template>
  <div class="logistics-carrier-form">
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
        class="carrier-form"
        :disabled="mode === 'view'"
      >
        <el-form-item label="承运商名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入承运商名称" />
        </el-form-item>

        <el-form-item label="承运商编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入承运商编码" />
        </el-form-item>

        <el-form-item label="联系人" prop="contact">
          <el-input v-model="form.contact" placeholder="请输入联系人姓名" />
        </el-form-item>

        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>

        <el-form-item label="地址" prop="address">
          <el-input
            v-model="form.address"
            type="textarea"
            :rows="3"
            placeholder="请输入地址"
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
    add: '新增承运商',
    edit: '编辑承运商',
    view: '查看承运商'
  }
  return titles[mode.value]
})

// 表单数据
const form = reactive({
  name: '',
  code: '',
  contact: '',
  phone: '',
  address: '',
  remark: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入承运商名称', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入承运商编码', trigger: 'blur' }
  ],
  contact: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入地址', trigger: 'blur' }
  ]
}

// 获取承运商详情
const getDetail = async (id) => {
  try {
    const response = await fetch(`http://127.0.0.1:8080/api/logistics/carriers/${id}`, {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0 && result.data) {
      Object.assign(form, result.data)
    } else {
      ElMessage.error(result.message || '获取承运商详情失败')
    }
  } catch (error) {
    console.error('获取承运商详情失败:', error)
    ElMessage.error('获取承运商详情失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    
    submitting.value = true
    const url = mode.value === 'edit' 
      ? `http://127.0.0.1:8080/api/logistics/carriers/${route.query.id}`
      : 'http://127.0.0.1:8080/api/logistics/carriers'
    
    const method = mode.value === 'edit' ? 'PUT' : 'POST'
    
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
      ElMessage.success(mode.value === 'edit' ? '承运商信息更新成功' : '承运商创建成功')
      handleBack()
    } else {
      ElMessage.error(result.message || (mode.value === 'edit' ? '承运商信息更新失败' : '承运商创建失败'))
    }
  } catch (error) {
    console.error(mode.value === 'edit' ? '承运商信息更新失败:' : '承运商创建失败:', error)
    ElMessage.error(mode.value === 'edit' ? '承运商信息更新失败' : '承运商创建失败')
  } finally {
    submitting.value = false
  }
}

// 返回列表页
const handleBack = () => {
  router.push('/dashboard/logistics/carrier')
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
.logistics-carrier-form {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.carrier-form {
  max-width: 800px;
  margin: 0 auto;
}
</style> 