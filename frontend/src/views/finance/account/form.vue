<template>
  <div class="finance-account-form">
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
        class="account-form"
        :disabled="mode === 'view'"
      >
        <el-form-item label="账户名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入账户名称" />
        </el-form-item>

        <el-form-item label="账户类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择账户类型">
            <el-option label="现金" value="cash" />
            <el-option label="银行卡" value="bank" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item label="初始余额" prop="balance">
          <el-input-number
            v-model="form.balance"
            :precision="2"
            :step="0.1"
            :min="0"
            placeholder="请输入初始余额"
          />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            placeholder="请输入备注信息"
            :rows="3"
          />
        </el-form-item>

        <el-form-item v-if="mode !== 'view'">
          <el-button type="primary" @click="handleSubmit">保存</el-button>
          <el-button @click="handleBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

// 表单引用
const formRef = ref(null)

// 表单模式：add-新增，edit-编辑，view-查看
const mode = computed(() => route.query.mode || 'add')

// 表单标题
const formTitle = computed(() => {
  const titles = {
    add: '新增账户',
    edit: '编辑账户',
    view: '查看账户'
  }
  return titles[mode.value] || '新增账户'
})

// 表单数据
const form = reactive({
  name: '',
  type: '',
  balance: 0,
  remark: ''
})

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入账户名称', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择账户类型', trigger: 'change' }
  ],
  balance: [
    { required: true, message: '请输入初始余额', trigger: 'blur' }
  ]
}

// 获取账户详情
const getAccountDetail = async (id) => {
  try {
    const response = await fetch(`http://127.0.0.1:8080/api/finance/accounts/${id}`, {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0 && result.data) {
      Object.assign(form, result.data)
    } else {
      ElMessage.error(result.message || '获取账户详情失败')
    }
  } catch (error) {
    console.error('获取账户详情失败:', error)
    ElMessage.error('获取账户详情失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    
    const url = mode.value === 'edit' 
      ? `http://127.0.0.1:8080/api/finance/accounts/${route.query.id}`
      : 'http://127.0.0.1:8080/api/finance/accounts'
    
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
      ElMessage.success(mode.value === 'edit' ? '账户更新成功' : '账户创建成功')
      handleBack()
    } else {
      ElMessage.error(result.message || (mode.value === 'edit' ? '账户更新失败' : '账户创建失败'))
    }
  } catch (error) {
    console.error(mode.value === 'edit' ? '账户更新失败:' : '账户创建失败:', error)
    ElMessage.error(mode.value === 'edit' ? '账户更新失败' : '账户创建失败')
  }
}

// 返回列表页
const handleBack = () => {
  router.push('/dashboard/finance/account')
}

// 初始化
onMounted(async () => {
  const { id } = route.query
  if (id && mode.value !== 'add') {
    await getAccountDetail(id)
  }
})
</script>

<style scoped>
.finance-account-form {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.account-form {
  max-width: 600px;
  margin: 0 auto;
}
</style> 