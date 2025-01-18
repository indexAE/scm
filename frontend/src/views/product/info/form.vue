<template>
  <div class="product-form">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑商品' : '新增商品' }}</span>
        </div>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="商品编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入商品编码" />
        </el-form-item>

        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>

        <el-form-item label="商品分类" prop="categoryId">
          <el-cascader
            v-model="form.categoryId"
            :options="categories"
            :props="{
              checkStrictly: true,
              label: 'name',
              value: 'id',
              emitPath: false
            }"
            placeholder="请选择商品分类"
          />
        </el-form-item>

        <el-form-item label="规格" prop="spec">
          <el-input v-model="form.spec" placeholder="请输入商品规格" />
        </el-form-item>

        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="请输入商品单位" />
        </el-form-item>

        <el-form-item label="价格" prop="price">
          <el-input-number 
            v-model="form.price" 
            :precision="2" 
            :min="0"
            :step="0.01"
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="enabled">启用</el-radio>
            <el-radio label="disabled">禁用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注信息"
          />
        </el-form-item>

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

// 是否为编辑模式
const isEdit = computed(() => route.query.id !== undefined)

// 表单数据
const form = reactive({
  code: '',
  name: '',
  categoryId: null,
  spec: '',
  unit: '',
  price: 0,
  status: 'enabled',
  remark: ''
})

// 表单验证规则
const rules = {
  code: [
    { required: true, message: '请输入商品编码', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入商品名称', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ]
}

// 商品分类列表
const categories = ref([])

// 获取商品分类列表
const getCategories = async () => {
  try {
    console.log('开始获取商品分类列表...') // 添加日志
    const response = await fetch('http://127.0.0.1:8080/api/product/categories/tree', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    
    console.log('商品分类API响应状态:', response.status) // 添加日志
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    
    const result = await response.json()
    console.log('获取商品分类响应:', result) // 添加日志
    
    if (result.code === 0) {
      categories.value = result.data || []
      console.log('设置商品分类列表:', categories.value) // 添加日志
      if (categories.value.length === 0) {
        console.warn('获取到的商品分类列表为空') // 添加警告日志
      }
    } else {
      console.error('获取商品分类列表失败:', result.message)
      ElMessage.error(result.message || '获取商品分类列表失败')
      categories.value = [] // 清空分类列表
    }
  } catch (error) {
    console.error('获取商品分类列表失败:', error)
    ElMessage.error('获取商品分类列表失败: ' + error.message)
    categories.value = [] // 清空分类列表
  }
}

// 检查商品编码是否重复
const checkCode = async () => {
  try {
    const response = await fetch(`http://127.0.0.1:8080/api/products/check-code?code=${form.code}&excludeId=${route.query.id || ''}`, {
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code !== 0) {
      ElMessage.error(result.message || '商品编码已存在')
      return false
    }
    return true
  } catch (error) {
    console.error('检查商品编码失败:', error)
    ElMessage.error('检查商品编码失败')
    return false
  }
}

// 获取商品详情
const getProductDetail = async () => {
  try {
    const response = await fetch(`http://127.0.0.1:8080/api/products/${route.query.id}`)
    const result = await response.json()
    if (result.code === 0) {
      const { code, name, categoryId, spec, unit, price, status, remark } = result.data
      Object.assign(form, { code, name, categoryId, spec, unit, price, status, remark })
    } else {
      ElMessage.error(result.message || '获取商品详情失败')
    }
  } catch (error) {
    console.error('获取商品详情失败:', error)
    ElMessage.error('获取商品详情失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      // 检查商品编码
      if (!(await checkCode())) {
        return
      }

      try {
        const url = isEdit.value 
          ? `http://127.0.0.1:8080/api/products/${route.query.id}`
          : 'http://127.0.0.1:8080/api/products'
        
        const method = isEdit.value ? 'PUT' : 'POST'
        const response = await fetch(url, {
          method,
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          },
          credentials: 'include',
          body: JSON.stringify({
            ...form,
            categoryId: Number(form.categoryId),
            price: form.price.toString(),
            updateBy: 1,
            createBy: isEdit.value ? undefined : 1
          })
        })

        const result = await response.json()
        if (result.code === 0) {
          ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
          router.push('/dashboard/product/info')
        } else {
          ElMessage.error(result.message || (isEdit.value ? '更新失败' : '创建失败'))
        }
      } catch (error) {
        console.error(isEdit.value ? '更新商品失败:' : '创建商品失败:', error)
        ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
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
    router.push('/dashboard/product/info')
  }).catch(() => {})
}

onMounted(async () => {
  await getCategories() // 在组件挂载时获取商品分类列表
  if (route.query.id) {
    await getProductDetail()
  }
})
</script>

<style scoped>
.product-form {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}
</style> 