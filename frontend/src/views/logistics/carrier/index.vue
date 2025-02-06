<template>
  <div class="logistics-carrier">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>承运商管理</span>
          <el-button type="primary" @click="handleAdd">新增承运商</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" class="search-form" inline>
        <el-form-item label="承运商名称">
          <el-input v-model="searchForm.name" placeholder="请输入承运商名称" clearable />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="searchForm.contact" placeholder="请输入联系人" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" value="normal" />
            <el-option label="禁用" value="disabled" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="tableData" border style="width: 100%">
        <el-table-column prop="name" label="承运商名称" />
        <el-table-column prop="code" label="承运商编码" width="120" />
        <el-table-column prop="contact" label="联系人" width="120" />
        <el-table-column prop="phone" label="联系电话" width="120" />
        <el-table-column prop="address" label="地址" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'normal' ? 'success' : 'danger'">
              {{ row.status === 'normal' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button
              v-if="row.status === 'normal'"
              link
              type="danger"
              @click="handleDisable(row)"
            >
              禁用
            </el-button>
            <el-button
              v-else
              link
              type="success"
              @click="handleEnable(row)"
            >
              启用
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="limit"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

// 搜索表单数据
const searchForm = reactive({
  name: '',
  contact: '',
  status: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const page = ref(1)
const limit = ref(10)
const total = ref(0)

// 获取承运商列表
const getList = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      limit: limit.value,
      ...searchForm
    }
    const response = await fetch(`http://127.0.0.1:8080/api/logistics/carriers?${new URLSearchParams(params)}`, {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    const result = await response.json()
    if (result.code === 0) {
      tableData.value = result.data.list
      total.value = result.data.total
    } else {
      ElMessage.error(result.message || '获取承运商列表失败')
    }
  } catch (error) {
    console.error('获取承运商列表失败:', error)
    ElMessage.error('获取承运商列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  page.value = 1
  getList()
}

// 重置
const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  router.push('/dashboard/logistics/carrier/form')
}

// 查看
const handleView = (row) => {
  router.push(`/dashboard/logistics/carrier/form?id=${row.id}&mode=view`)
}

// 编辑
const handleEdit = (row) => {
  router.push(`/dashboard/logistics/carrier/form?id=${row.id}&mode=edit`)
}

// 禁用
const handleDisable = async (row) => {
  try {
    await ElMessageBox.confirm('确认要禁用该承运商吗？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await fetch(`http://127.0.0.1:8080/api/logistics/carriers/${row.id}/disable`, {
      method: 'PUT',
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    
    const result = await response.json()
    if (result.code === 0) {
      ElMessage.success('禁用成功')
      getList()
    } else {
      ElMessage.error(result.message || '禁用失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('禁用失败:', error)
      ElMessage.error('禁用失败')
    }
  }
}

// 启用
const handleEnable = async (row) => {
  try {
    await ElMessageBox.confirm('确认要启用该承运商吗？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await fetch(`http://127.0.0.1:8080/api/logistics/carriers/${row.id}/enable`, {
      method: 'PUT',
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    
    const result = await response.json()
    if (result.code === 0) {
      ElMessage.success('启用成功')
      getList()
    } else {
      ElMessage.error(result.message || '启用失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('启用失败:', error)
      ElMessage.error('启用失败')
    }
  }
}

// 分页大小改变
const handleSizeChange = (val) => {
  limit.value = val
  page.value = 1
  getList()
}

// 当前页改变
const handleCurrentChange = (val) => {
  page.value = val
  getList()
}

// 初始化
onMounted(() => {
  getList()
})
</script>

<style scoped>
.logistics-carrier {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 