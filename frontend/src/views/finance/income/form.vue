<template>
  <div class="income-form">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ formTitle }}</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="income-form"
      >
        <!-- 基本信息 -->
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="收入编号" prop="incomeNo">
              <el-input v-model="form.incomeNo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="收入类型" prop="type">
              <el-select
                v-model="form.type"
                placeholder="请选择收入类型"
                :disabled="isView"
              >
                <el-option label="销售收入" value="sale" />
                <el-option label="其他收入" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="金额" prop="amount">
              <el-input-number
                v-model="form.amount"
                :precision="2"
                :step="0.01"
                :min="0"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
            :disabled="isView"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            v-if="!isView"
            type="primary"
            @click="handleSubmit"
            :loading="submitting"
          >
            保存
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
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

// 获取路由参数
const id = route.query.id
const mode = route.query.mode
const isView = computed(() => mode === 'view')

// 表单标题
const formTitle = computed(() => {
  if (isView.value) return '查看收入'
  return id ? '编辑收入' : '新增收入'
})

// 生成收入编号
const generateIncomeNo = () => {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0')
  return `SR${year}${month}${day}${random}`
}

// 表单数据
const form = reactive({
  incomeNo: generateIncomeNo(),  // 初始化时生成收入编号
  type: '',
  amount: 0,
  remark: '',
  status: 'pending'
})

// 表单验证规则
const rules = {
  type: [{ required: true, message: '请选择收入类型', trigger: 'change' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }]
}

// 获取收入详情
const getIncomeDetail = async () => {
  try {
    const response = await fetch(`http://127.0.0.1:8080/api/finance/incomes/${id}`, {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0) {
      Object.assign(form, result.data)
    } else {
      ElMessage.error(result.message || '获取收入详情失败')
    }
  } catch (error) {
    console.error('获取收入详情失败:', error)
    ElMessage.error('获取收入详情失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const url = id
          ? `http://127.0.0.1:8080/api/finance/incomes/${id}`
          : 'http://127.0.0.1:8080/api/finance/incomes'
        const method = id ? 'PUT' : 'POST'
        
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
          ElMessage.success(id ? '更新成功' : '创建成功')
          router.back()
        } else {
          ElMessage.error(result.message || (id ? '更新失败' : '创建失败'))
        }
      } catch (error) {
        console.error(id ? '更新收入失败:' : '创建收入失败:', error)
        ElMessage.error(id ? '更新失败' : '创建失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 初始化
onMounted(async () => {
  if (id) {
    await getIncomeDetail()
  }
})
</script>

<style scoped>
.income-form {
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
</style> 