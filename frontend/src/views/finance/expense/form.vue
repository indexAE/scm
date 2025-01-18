<template>
  <div class="finance-expense-form">
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
        class="expense-form"
        :disabled="mode === 'view'"
      >
        <el-form-item label="支出编号" prop="expenseNo">
          <el-input v-model="form.expenseNo" placeholder="系统自动生成" disabled />
        </el-form-item>

        <el-form-item label="支出类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择支出类型">
            <el-option label="采购支出" value="purchase" />
            <el-option label="其他支出" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item label="支出金额" prop="amount">
          <el-input-number
            v-model="form.amount"
            :precision="2"
            :step="0.1"
            :min="0"
            placeholder="请输入支出金额"
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
    add: '新增支出',
    edit: '编辑支出',
    view: '查看支出'
  }
  return titles[mode.value] || '新增支出'
})

// 表单数据
const form = reactive({
  expenseNo: '',
  type: '',
  amount: 0,
  remark: ''
})

// 表单校验规则
const rules = {
  type: [
    { required: true, message: '请选择支出类型', trigger: 'change' }
  ],
  amount: [
    { required: true, message: '请输入支出金额', trigger: 'blur' }
  ]
}

// 生成支出编号
const generateExpenseNo = () => {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0')
  return `ZC${year}${month}${day}${random}`
}

// 获取支出详情
const getExpenseDetail = async (id) => {
  try {
    const response = await fetch(`http://127.0.0.1:8080/api/finance/expenses/${id}`, {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0 && result.data) {
      Object.assign(form, result.data)
    } else {
      ElMessage.error(result.message || '获取支出详情失败')
    }
  } catch (error) {
    console.error('获取支出详情失败:', error)
    ElMessage.error('获取支出详情失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    
    const url = mode.value === 'edit' 
      ? `http://127.0.0.1:8080/api/finance/expenses/${route.query.id}`
      : 'http://127.0.0.1:8080/api/finance/expenses'
    
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
      ElMessage.success(mode.value === 'edit' ? '支出更新成功' : '支出创建成功')
      handleBack()
    } else {
      ElMessage.error(result.message || (mode.value === 'edit' ? '支出更新失败' : '支出创建失败'))
    }
  } catch (error) {
    console.error(mode.value === 'edit' ? '支出更新失败:' : '支出创建失败:', error)
    ElMessage.error(mode.value === 'edit' ? '支出更新失败' : '支出创建失败')
  }
}

// 返回列表页
const handleBack = () => {
  router.push('/dashboard/finance/expense')
}

// 初始化
onMounted(async () => {
  const { id } = route.query
  if (mode.value === 'add') {
    form.expenseNo = generateExpenseNo()
  } else if (id) {
    await getExpenseDetail(id)
  }
})
</script>

<style scoped>
.finance-expense-form {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.expense-form {
  max-width: 600px;
  margin: 0 auto;
}
</style> 