<template>
  <div class="publish-notice">
    <div class="header">
      <h3>发布通知</h3>
    </div>

    <el-form
      ref="formRef"
      :model="noticeForm"
      :rules="rules"
      label-width="100px"
      class="notice-form"
    >
      <el-form-item label="通知标题" prop="title">
        <el-input v-model="noticeForm.title" placeholder="请输入通知标题"></el-input>
      </el-form-item>

      <el-form-item label="接收范围" prop="scope">
        <el-select
          v-model="noticeForm.scope"
          placeholder="请选择接收范围"
          style="width: 100%"
        >
          <el-option label="全部用户" value="all"></el-option>
          <el-option label="指定角色" value="role"></el-option>
          <el-option label="指定部门" value="department"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item
        label="接收对象"
        prop="receivers"
        v-if="noticeForm.scope !== 'all'"
      >
        <el-select
          v-model="noticeForm.receivers"
          multiple
          placeholder="请选择接收对象"
          style="width: 100%"
        >
          <el-option
            v-for="item in receiverOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="通知内容" prop="content">
        <el-input
          v-model="noticeForm.content"
          type="textarea"
          :rows="4"
          placeholder="请输入通知内容"
        ></el-input>
      </el-form-item>

      <el-form-item label="附件">
        <el-upload
          class="upload-demo"
          action="#"
          :auto-upload="false"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          :file-list="fileList"
        >
          <template #trigger>
            <el-button type="primary">选择文件</el-button>
          </template>
          <template #tip>
            <div class="el-upload__tip">
              支持任意格式文件，单个文件不超过10MB
            </div>
          </template>
        </el-upload>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSubmit">发布通知</el-button>
        <el-button @click="handlePreview">预览</el-button>
        <el-button @click="$router.back()">取消</el-button>
      </el-form-item>
    </el-form>

    <!-- 预览对话框 -->
    <el-dialog
      v-model="previewVisible"
      title="通知预览"
      width="600px"
      destroy-on-close
    >
      <div class="notice-preview">
        <h2>{{ noticeForm.title }}</h2>
        <div class="notice-info">
          <span>发布人：{{ username }}</span>
          <span>发布时间：{{ currentTime }}</span>
        </div>
        <div class="notice-content">{{ noticeForm.content }}</div>
        <div class="notice-files" v-if="fileList.length > 0">
          <h4>附件：</h4>
          <ul>
            <li v-for="file in fileList" :key="file.name">
              {{ file.name }}
            </li>
          </ul>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { createNotice } from '@/api/notice'
import { uploadFile } from '@/api/upload'

const router = useRouter()
const formRef = ref(null)
const username = localStorage.getItem('username') || '未知用户'

// 表单数据
const noticeForm = ref({
  title: '',
  scope: '',
  receivers: [],
  content: ''
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入通知标题', trigger: 'blur' },
    { min: 2, max: 50, message: '标题长度在2-50个字符之间', trigger: 'blur' }
  ],
  scope: [
    { required: true, message: '请选择接收范围', trigger: 'change' }
  ],
  receivers: [
    { 
      required: true, 
      message: '请选择接收对象', 
      trigger: 'change',
      validator: (rule, value, callback) => {
        if (noticeForm.value.scope !== 'all' && (!value || value.length === 0)) {
          callback(new Error('请选择接收对象'))
        } else {
          callback()
        }
      }
    }
  ],
  content: [
    { required: true, message: '请输入通知内容', trigger: 'blur' },
    { min: 1, max: 2000, message: '内容长度在1-2000个字符之间', trigger: 'blur' }
  ]
}

// 接收对象选项（模拟数据）
const receiverOptions = [
  { label: '管理员', value: 'admin' },
  { label: '普通用户', value: 'user' },
  { label: '采购部', value: 'purchase' },
  { label: '销售部', value: 'sale' }
]

// 文件上传
const fileList = ref([])
const handleFileChange = async (file) => {
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.warning('文件大小不能超过10MB')
    return false
  }
  try {
    const formData = new FormData()
    formData.append('file', file.raw)
    const response = await uploadFile(formData)
    if (response.code === 200) {
      ElMessage.success('文件上传成功')
      fileList.value.push({
        name: file.name,
        size: file.size,
        type: file.type,
        url: response.data.url,
        fileId: response.data.fileId
      })
    } else {
      ElMessage.error('文件上传失败')
    }
  } catch (error) {
    console.error('文件上传失败:', error)
    ElMessage.error('文件上传失败')
  }
  return false // 阻止自动上传
}

// 移除文件
const handleFileRemove = (file) => {
  const index = fileList.value.findIndex(item => item.name === file.name)
  if (index !== -1) {
    fileList.value.splice(index, 1)
  }
}

// 预览
const previewVisible = ref(false)
const currentTime = new Date().toLocaleString()
const handlePreview = () => {
  previewVisible.value = true
}

// 提交表单
const handleSubmit = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        // 处理接收对象
        let receivers = noticeForm.value.receivers
        if (noticeForm.value.scope === 'all') {
          receivers = []
        }
        
        // 处理附件信息
        const attachments = fileList.value.map(file => ({
          name: file.name,
          size: file.size,
          type: file.type,
          url: file.url,
          fileId: file.fileId
        }))
        
        const formData = {
          ...noticeForm.value,
          receivers: JSON.stringify(receivers),
          attachments: JSON.stringify(attachments),
          publisherId: localStorage.getItem('userId') || '0',
          publisherName: username,
          status: 'published',
          createTime: new Date().toISOString(),
          updateTime: new Date().toISOString()
        }
        
        await createNotice(formData)
        ElMessage.success('发布成功')
        router.push('/dashboard/notice/list')
      } catch (error) {
        console.error('发布失败:', error)
        ElMessage.error(error.response?.data?.message || '发布失败')
      }
    }
  })
}
</script>

<style scoped>
.publish-notice {
  padding: 20px;
}

.header {
  margin-bottom: 20px;
}

.notice-form {
  max-width: 800px;
  margin: 0 auto;
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.notice-preview {
  padding: 20px;
}

.notice-preview h2 {
  text-align: center;
  margin-bottom: 20px;
}

.notice-info {
  display: flex;
  justify-content: space-between;
  color: #666;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.notice-content {
  line-height: 1.6;
  white-space: pre-wrap;
}

.notice-files {
  margin-top: 20px;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

.notice-files h4 {
  margin-bottom: 10px;
}

.notice-files ul {
  list-style: none;
  padding: 0;
}

.notice-files li {
  line-height: 24px;
  color: #666;
}
</style> 