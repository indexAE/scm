<template>
  <div class="login-container">
    <div class="login-box">
      <h2>供应链管理平台</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-item">
          <input
            v-model="loginForm.username"
            type="text"
            placeholder="用户名"
            required
          />
        </div>
        <div class="form-item">
          <input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            required
          />
        </div>
        <div class="form-item">
          <button type="submit">登录</button>
        </div>
        <div class="form-item">
          <router-link to="/register" class="register-link">没有账号？去注册</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useSettingsStore } from '../stores/settings'

const router = useRouter()
const settingsStore = useSettingsStore()

const loginForm = reactive({
  username: '',
  password: ''
})

// 检查账户是否被锁定
const checkAccountLock = (username) => {
  if (!settingsStore.loginLockEnabled) return false
  
  const lockInfo = JSON.parse(localStorage.getItem(`loginLock_${username}`) || '{"count": 0, "lastFailTime": 0}')
  
  // 如果失败次数达到5次
  if (lockInfo.count >= 5) {
    const lockDuration = 30 * 60 * 1000 // 30分钟的毫秒数
    const now = Date.now()
    
    // 如果在锁定时间内
    if (now - lockInfo.lastFailTime < lockDuration) {
      const remainingMinutes = Math.ceil((lockDuration - (now - lockInfo.lastFailTime)) / (60 * 1000))
      ElMessage.error(`账户已被锁定，请${remainingMinutes}分钟后再试`)
      return true
    } else {
      // 锁定时间已过，重置计数
      localStorage.removeItem(`loginLock_${username}`)
      return false
    }
  }
  
  return false
}

// 记录登录失败
const recordLoginFailure = (username) => {
  if (!settingsStore.loginLockEnabled) return
  
  const lockInfo = JSON.parse(localStorage.getItem(`loginLock_${username}`) || '{"count": 0, "lastFailTime": 0}')
  lockInfo.count++
  lockInfo.lastFailTime = Date.now()
  localStorage.setItem(`loginLock_${username}`, JSON.stringify(lockInfo))
  
  const remainingAttempts = 5 - lockInfo.count
  if (remainingAttempts > 0) {
    ElMessage.error(`登录失败，还剩${remainingAttempts}次尝试机会`)
  } else {
    ElMessage.error('登录失败次数过多，账户已被锁定30分钟')
  }
}

// 清除登录失败记录
const clearLoginFailure = (username) => {
  localStorage.removeItem(`loginLock_${username}`)
}

const handleLogin = async () => {
  // 检查账户是否被锁定
  if (checkAccountLock(loginForm.username)) {
    return
  }

  try {
    const res = await fetch('http://localhost:8000/api/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include',
      body: JSON.stringify(loginForm)
    })
    
    const data = await res.json()
    if (data.code === 0) {
      localStorage.setItem('username', data.data.username)
      localStorage.setItem('userId', data.data.id)
      
      // 登录成功，清除失败记录
      clearLoginFailure(loginForm.username)
      
      ElMessage.success('登录成功')
      router.push('/dashboard')
    } else {
      // 记录登录失败
      recordLoginFailure(loginForm.username)
      ElMessage.error(data.message)
    }
  } catch (error) {
    console.error('登录失败:', error)
    // 记录登录失败
    recordLoginFailure(loginForm.username)
    ElMessage.error('登录失败')
  }
}

// 在组件挂载时获取系统设置
onMounted(() => {
  settingsStore.fetchSettings()
})
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-box h2 {
  text-align: center;
  margin-bottom: 30px;
}

.form-item {
  margin-bottom: 20px;
}

.form-item input {
  width: 100%;
  padding: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  outline: none;
}

.form-item button {
  width: 100%;
  padding: 12px 0;
  background-color: #409eff;
  border: none;
  border-radius: 4px;
  color: white;
  cursor: pointer;
}

.form-item button:hover {
  background-color: #66b1ff;
}

.register-link {
  display: block;
  text-align: center;
  color: #409eff;
  text-decoration: none;
}

.register-link:hover {
  color: #66b1ff;
}
</style> 