<template>
  <div class="profile">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>
      
      <el-form :model="profileForm" label-width="100px">
        <el-form-item label="头像">
          <div class="avatar-upload">
            <el-avatar 
              :size="100" 
              :src="profileForm.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"
              class="preview-avatar"
            />
            <el-upload
              class="avatar-uploader"
              :auto-upload="false"
              :show-file-list="false"
              accept="image/jpeg,image/png,image/gif"
              :on-change="handleAvatarChange"
              :before-upload="beforeAvatarUpload"
            >
              <el-button type="primary" class="upload-btn">更换头像</el-button>
            </el-upload>
          </div>
        </el-form-item>

        <el-form-item label="用户名">
          <el-input v-model="profileForm.username" disabled />
        </el-form-item>
        
        <el-form-item label="角色类型">
          <el-input v-model="profileForm.roleName" disabled />
        </el-form-item>
        
        <el-form-item label="原密码">
          <el-input v-model="profileForm.oldPassword" type="password" placeholder="请输入原密码" />
        </el-form-item>
        
        <el-form-item label="新密码">
          <el-input v-model="profileForm.newPassword" type="password" placeholder="请输入新密码" />
        </el-form-item>
        
        <el-form-item label="确认新密码">
          <el-input v-model="profileForm.confirmPassword" type="password" placeholder="请再次输入新密码" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useSettingsStore } from '../stores/settings'

const profileForm = ref({
  username: '',
  roleName: '',
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
  avatar: localStorage.getItem('userAvatar') || ''
})

const router = useRouter()
const settingsStore = useSettingsStore()

// 获取个人信息
const getProfile = async () => {
  try {
    const userId = localStorage.getItem('userId')
    if (!userId) {
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }

    // 获取用户基本信息和角色信息
    const userRes = await fetch(`http://localhost:8080/api/users/${userId}`, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
    const userData = await userRes.json()
    console.log('用户数据:', userData)
    
    if (userData.code === 0) {
      profileForm.value.username = userData.data.username
      profileForm.value.roleName = userData.data.role || userData.data.roleName || '管理员'
      
      // 如果后端返回了头像 URL，更新头像
      if (userData.data.avatar) {
        const avatarUrl = userData.data.avatar.startsWith('http') 
          ? userData.data.avatar 
          : `http://localhost:8080${userData.data.avatar}`
        profileForm.value.avatar = avatarUrl
        localStorage.setItem('userAvatar', avatarUrl)
      }
    } else {
      ElMessage.error(userData.message || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取个人信息失败:', error)
    ElMessage.error('获取个人信息失败')
  }
}

// 提交修改
const handleSubmit = async () => {
  // 表单验证
  if (!profileForm.value.oldPassword) {
    ElMessage.error('请输入原密码')
    return
  }
  if (!profileForm.value.newPassword) {
    ElMessage.error('请输入新密码')
    return
  }
  if (!profileForm.value.confirmPassword) {
    ElMessage.error('请确认新密码')
    return
  }
  
  // 验证新密码长度
  if (profileForm.value.newPassword.length < settingsStore.passwordMinLength) {
    ElMessage.error(`新密码长度不能小于${settingsStore.passwordMinLength}位`)
    return
  }
  
  // 验证新密码
  if (profileForm.value.newPassword !== profileForm.value.confirmPassword) {
    ElMessage.error('两次输入的新密码不一致')
    return
  }

  try {
    const userId = localStorage.getItem('userId')
    if (!userId) {
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }

    const res = await fetch(`http://localhost:8080/api/users/${userId}/password`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        oldPassword: profileForm.value.oldPassword,
        newPassword: profileForm.value.newPassword
      })
    })
    
    const data = await res.json()
    if (data.code === 0) {
      ElMessage.success('密码修改成功')
      // 清空表单
      profileForm.value.oldPassword = ''
      profileForm.value.newPassword = ''
      profileForm.value.confirmPassword = ''
    } else {
      ElMessage.error(data.message || '修改密码失败')
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error('修改密码失败')
  }
}

// 头像上传前的验证
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('上传头像图片只能是图片格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 头像变更处理
const handleAvatarChange = (file) => {
  if (!beforeAvatarUpload(file.raw)) {
    return
  }

  const reader = new FileReader()
  reader.readAsDataURL(file.raw)
  reader.onload = (e) => {
    const base64Url = e.target.result
    profileForm.value.avatar = base64Url
    localStorage.setItem('userAvatar', base64Url)
    
    // 触发更新其他组件的头像显示
    const event = new Event('storage')
    window.dispatchEvent(event)
    
    ElMessage.success('头像更新成功')
  }
}

onMounted(() => {
  getProfile()
})
</script>

<style scoped>
.profile {
  padding: 20px;
}

.box-card {
  max-width: 600px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar-upload {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.preview-avatar {
  border: 2px solid #dcdfe6;
}

.upload-btn {
  margin-top: 8px;
}
</style> 