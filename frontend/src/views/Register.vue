<template>
  <div class="register-container">
    <div class="register-box">
      <h2>供应链管理平台</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-item">
          <input
            v-model="registerForm.username"
            type="text"
            placeholder="用户名"
            required
          />
        </div>
        <div class="form-item">
          <input
            v-model="registerForm.password"
            type="password"
            placeholder="密码"
            required
          />
        </div>
        <div class="form-item">
          <input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="确认密码"
            required
          />
        </div>
        <div class="form-item">
          <button type="submit">注册</button>
        </div>
        <div class="form-item">
          <router-link to="/login" class="login-link">已有账号？去登录</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useSettingsStore } from '../stores/settings'

const router = useRouter()
const settingsStore = useSettingsStore()

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const handleRegister = async () => {
  // 验证密码长度
  if (registerForm.password.length < settingsStore.passwordMinLength) {
    ElMessage.error(`密码长度不能小于${settingsStore.passwordMinLength}位`)
    return
  }
  
  // 验证两次密码是否一致
  if (registerForm.password !== registerForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }

  try {
    const res = await fetch('http://localhost:8000/api/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: registerForm.username,
        password: registerForm.password
      })
    })
    const data = await res.json()
    if (data.code === 0) {
      ElMessage.success('注册成功')
      router.push('/login')
    } else {
      ElMessage.error(data.message)
    }
  } catch (error) {
    console.error('注册失败:', error)
    ElMessage.error('注册失败,请重试')
  }
}
</script>

<style scoped>
.register-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
}

.register-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.register-box h2 {
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

.login-link {
  display: block;
  text-align: center;
  color: #409eff;
  text-decoration: none;
}

.login-link:hover {
  color: #66b1ff;
}
</style> 