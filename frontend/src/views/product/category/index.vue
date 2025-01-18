<template>
  <div class="category-manage">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>商品分类管理</span>
          <div>
            <el-button type="primary" @click="handleAdd">新增分类</el-button>
            <el-button type="success" @click="createDefaultCategories">添加测试数据</el-button>
          </div>
        </div>
      </template>

      <!-- 分类树形表格 -->
      <el-table
        :data="categoryData"
        row-key="id"
        border
        :tree-props="{ children: 'children' }"
      >
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="code" label="分类编码" />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'enabled' ? 'success' : 'danger'">
              {{ scope.row.status === 'enabled' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300">
          <template #default="scope">
            <el-button link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button
              :type="scope.row.status === 'enabled' ? 'danger' : 'success'"
              link
              @click="handleToggleStatus(scope.row)"
            >
              {{ scope.row.status === 'enabled' ? '禁用' : '启用' }}
            </el-button>
            <el-button 
              type="primary" 
              link 
              @click="handleSetParent(scope.row)"
              v-if="!scope.row.parentId"
            >
              设为上级分类
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 分类表单对话框 -->
    <el-dialog
      :title="isEdit ? '编辑分类' : '新增分类'"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级分类">
          <el-cascader
            v-model="form.parentId"
            :options="isEdit ? filterCategories(categoryData.value, form.id) : categoryData"
            :props="{
              checkStrictly: true,
              label: 'name',
              value: 'id',
              emitPath: false,
              expandTrigger: 'hover'
            }"
            clearable
            placeholder="请选择上级分类"
          />
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入分类编码" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 设置上级分类对话框 -->
    <el-dialog
      title="设置上级分类"
      v-model="setParentDialogVisible"
      width="500px"
    >
      <el-form label-width="100px">
        <el-form-item label="当前分类">
          <el-input :value="currentCategory?.name" disabled />
        </el-form-item>
        <el-form-item label="上级分类">
          <el-cascader
            v-model="selectedParentId"
            :options="categoryData"
            :props="{
              checkStrictly: true,
              label: 'name',
              value: 'id',
              emitPath: false,
              expandTrigger: 'hover'
            }"
            clearable
            placeholder="请选择上级分类"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="setParentDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSetParentSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 分类数据
const categoryData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

// 表单数据
const form = reactive({
  parentId: null,
  name: '',
  code: '',
  sort: 0
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入分类编码', trigger: 'blur' }
  ]
}

// 获取分类数据
const getCategoryData = async () => {
  try {
    const response = await fetch('/api/product/categories/tree')
    const result = await response.json()
    console.log('Response:', result)
    
    if (result.code === 0) {
      categoryData.value = result.data || []
    } else {
      ElMessage.error(result.message || '获取分类数据失败')
    }
  } catch (error) {
    console.error('获取分类数据失败:', error)
    ElMessage.error('获取分类数据失败')
  }
}

// 新增分类
const handleAdd = () => {
  isEdit.value = false
  form.parentId = null
  form.name = ''
  form.code = ''
  form.sort = 0
  dialogVisible.value = true
}

// 编辑分类
const handleEdit = (row) => {
  isEdit.value = true
  form.id = row.id
  form.parentId = row.parentId
  form.name = row.name
  form.code = row.code
  form.sort = row.sort
  dialogVisible.value = true
}

// 切换状态
const handleToggleStatus = async (row) => {
  const action = row.status === 'enabled' ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(
      `确认${action}该分类吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await fetch(`/api/product/categories/${row.id}/status`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        status: row.status === 'enabled' ? 'disabled' : 'enabled'
      })
    })
    const result = await response.json()
    if (result.code === 0) {
      ElMessage.success('状态更新成功')
      await getCategoryData()
    } else {
      ElMessage.error(result.message || '状态更新失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新状态失败:', error)
      ElMessage.error('更新状态失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const url = isEdit.value 
          ? `/api/product/categories/${form.id}`
          : '/api/product/categories'
        
        const method = isEdit.value ? 'PUT' : 'POST'
        const response = await fetch(url, {
          method,
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(form)
        })

        const result = await response.json()
        if (result.code === 0) {
          ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
          dialogVisible.value = false
          await getCategoryData()
        } else {
          ElMessage.error(result.message || (isEdit.value ? '更新失败' : '创建失败'))
        }
      } catch (error) {
        console.error(isEdit.value ? '更新分类失败:' : '创建分类失败:', error)
        ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
      }
    }
  })
}

// 过滤分类选项，排除当前分类及其子分类
const filterCategories = (categories, currentId) => {
  if (!categories) return []
  
  // 递归查找所有子分类ID
  const findChildrenIds = (category) => {
    let ids = [category.id]
    if (category.children) {
      category.children.forEach(child => {
        ids = ids.concat(findChildrenIds(child))
      })
    }
    return ids
  }

  // 深拷贝分类数据
  const cloneCategories = JSON.parse(JSON.stringify(categories))
  
  // 获取当前分类及其所有子分类的ID
  const excludeIds = currentId ? cloneCategories
    .filter(item => item.id === currentId)
    .reduce((ids, item) => ids.concat(findChildrenIds(item)), [])
    : []

  // 过滤掉当前分类及其子分类
  return cloneCategories.filter(item => !excludeIds.includes(item.id))
}

// 设置上级分类对话框
const setParentDialogVisible = ref(false)
const currentCategory = ref(null)
const selectedParentId = ref(null)

// 处理设置上级分类
const handleSetParent = (row) => {
  currentCategory.value = row
  selectedParentId.value = null
  setParentDialogVisible.value = true
}

// 提交设置上级分类
const handleSetParentSubmit = async () => {
  if (!currentCategory.value || !selectedParentId.value) return
  
  try {
    const response = await fetch(`/api/product/categories/${currentCategory.value.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        id: currentCategory.value.id,
        parentId: selectedParentId.value,
        name: currentCategory.value.name,
        code: currentCategory.value.code,
        sort: currentCategory.value.sort,
        status: currentCategory.value.status,
        updateBy: 1 // 暂时hardcode为1,实际应该从登录用户信息中获取
      })
    })

    const result = await response.json()
    if (result.code === 0) {
      ElMessage.success('设置上级分类成功')
      setParentDialogVisible.value = false
      await getCategoryData()
    } else {
      ElMessage.error(result.message || '设置上级分类失败')
    }
  } catch (error) {
    console.error('设置上级分类失败:', error)
    ElMessage.error('设置上级分类失败')
  }
}

// 创建默认分类数据
const createDefaultCategories = async () => {
  try {
    // 创建电子产品分类
    const electronics = {
      name: '电子产品',
      code: 'ELE001',
      sort: 1,
      status: 'enabled'
    }
    const electronicsResponse = await fetch('/api/product/categories', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(electronics)
    })
    const electronicsResult = await electronicsResponse.json()
    
    if (electronicsResult.code !== 0) {
      throw new Error(electronicsResult.message || '创建电子产品分类失败')
    }

    // 获取最新的分类列表
    await getCategoryData()
    const electronicsCategory = categoryData.value.find(c => c.code === 'ELE001')
    if (!electronicsCategory) {
      throw new Error('无法找到刚创建的电子产品分类')
    }

    // 创建电子产品的子分类
    const phones = {
      name: '手机',
      code: 'ELE002',
      parentId: electronicsCategory.id,
      sort: 1,
      status: 'enabled'
    }
    const phonesResponse = await fetch('/api/product/categories', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(phones)
    })
    const phonesResult = await phonesResponse.json()
    if (phonesResult.code !== 0) {
      throw new Error(phonesResult.message || '创建手机分类失败')
    }

    // 创建服装分类
    const clothing = {
      name: '服装',
      code: 'CLO001',
      sort: 2,
      status: 'enabled'
    }
    const clothingResponse = await fetch('/api/product/categories', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(clothing)
    })
    const clothingResult = await clothingResponse.json()
    
    if (clothingResult.code !== 0) {
      throw new Error(clothingResult.message || '创建服装分类失败')
    }

    // 获取最新的分类列表
    await getCategoryData()
    const clothingCategory = categoryData.value.find(c => c.code === 'CLO001')
    if (!clothingCategory) {
      throw new Error('无法找到刚创建的服装分类')
    }

    // 创建服装的子分类
    const shirts = {
      name: '上衣',
      code: 'CLO002',
      parentId: clothingCategory.id,
      sort: 1,
      status: 'enabled'
    }
    const shirtsResponse = await fetch('/api/product/categories', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(shirts)
    })
    const shirtsResult = await shirtsResponse.json()
    if (shirtsResult.code !== 0) {
      throw new Error(shirtsResult.message || '创建上衣分类失败')
    }

    ElMessage.success('测试数据添加成功')
    await getCategoryData()
  } catch (error) {
    console.error('添加测试数据失败:', error)
    ElMessage.error(error.message || '添加测试数据失败')
  }
}

onMounted(() => {
  getCategoryData()
})
</script>

<style scoped>
.category-manage {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 