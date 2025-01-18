<template>
  <div class="system-settings">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>系统设置</span>
        </div>
      </template>
      
      <el-form :model="settingsForm" label-width="120px">
        <!-- 基本设置 -->
        <div class="section-title">基本设置</div>
        <el-form-item label="系统名称">
          <el-input v-model="settingsForm.systemName" />
        </el-form-item>
        <el-form-item label="系统描述">
          <el-input
            v-model="settingsForm.systemDesc"
            type="textarea"
            :rows="2"
            placeholder="请输入系统描述"
          />
        </el-form-item>
        
        <!-- 安全设置 -->
        <div class="section-title">安全设置</div>
        <el-form-item label="密码最小长度">
          <el-input-number
            v-model="settingsForm.passwordMinLength"
            :min="6"
            :max="20"
          />
        </el-form-item>
        <el-form-item label="登录失败锁定">
          <el-switch
            v-model="settingsForm.loginLockEnabled"
            active-text="启用"
            inactive-text="禁用"
          />
          <span class="setting-tip" v-if="settingsForm.loginLockEnabled">
            （连续失败5次将锁定账户30分钟）
          </span>
        </el-form-item>
        
        <!-- 日志设置 -->
        <div class="section-title">日志设置</div>
        <el-form-item label="操作日志">
          <el-switch
            v-model="settingsForm.operationLogEnabled"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
        <el-form-item label="登录日志">
          <el-switch
            v-model="settingsForm.loginLogEnabled"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
        
        <!-- 其他设置 -->
        <div class="section-title">其他设置</div>
        <el-form-item label="首页显示欢迎语">
          <el-switch
            v-model="settingsForm.showWelcome"
            active-text="显示"
            inactive-text="隐藏"
          />
        </el-form-item>
        
        <!-- 主题设置 -->
        <div class="section-title">主题设置</div>
        <el-form-item label="系统主题">
          <el-radio-group v-model="settingsForm.theme" @change="handleThemeChange">
            <el-radio-button label="light">浅色主题</el-radio-button>
            <el-radio-button label="dark">深色主题</el-radio-button>
            <el-radio-button label="blue">蓝色主题</el-radio-button>
          </el-radio-group>
        </el-form-item>
        
        <!-- 按钮组 -->
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">保存设置</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useSettingsStore } from '../stores/settings'

const settingsStore = useSettingsStore()
const settingsForm = ref({
  id: null,
  systemName: '',
  systemDesc: '',
  passwordMinLength: 6,
  loginLockEnabled: false,
  operationLogEnabled: true,
  loginLogEnabled: true,
  showWelcome: true,
  theme: 'light'
})

// 获取设置
const getSettings = async () => {
  try {
    const res = await fetch('http://localhost:8000/api/settings', {
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    })
    const data = await res.json()
    if (data.code === 0) {
      Object.assign(settingsForm.value, data.data)
      // 更新Pinia状态
      settingsStore.updateSettings(data.data)
    } else {
      ElMessage.error(data.message || '获取设置失败')
    }
  } catch (error) {
    console.error('获取设置失败:', error)
    ElMessage.error('获取设置失败')
  }
}

// 处理主题变更
const handleThemeChange = (theme) => {
  settingsStore.updateTheme(theme)
}

// 保存设置
const handleSubmit = async () => {
  try {
    const res = await fetch('http://localhost:8000/api/settings', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include',
      body: JSON.stringify(settingsForm.value)
    })
    
    const data = await res.json()
    if (data.code === 0) {
      ElMessage.success('保存成功')
      // 更新Pinia状态
      settingsStore.updateSettings(settingsForm.value)
    } else {
      ElMessage.error(data.message || '保存失败')
    }
  } catch (error) {
    console.error('保存设置失败:', error)
    ElMessage.error('保存失败')
  }
}

// 重置设置
const handleReset = () => {
  getSettings()
}

onMounted(() => {
  getSettings()
})
</script>

<style scoped>
.system-settings {
  padding: 20px;
}

.box-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  margin: 20px 0 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--border-color);
}

.setting-tip {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}

.el-form-item {
  margin-bottom: 22px;
}
</style> 