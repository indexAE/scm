import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useSettingsStore = defineStore('settings', () => {
  const systemName = ref('')
  const systemDesc = ref('')
  const passwordMinLength = ref(6)
  const loginLockEnabled = ref(false)
  const showWelcome = ref(true)
  const theme = ref('light')
  
  // 获取系统设置
  const fetchSettings = async () => {
    try {
      const res = await fetch('/api/settings')
      const data = await res.json()
      if (data.code === 200) {
        systemName.value = data.data.systemName
        systemDesc.value = data.data.systemDesc
        passwordMinLength.value = data.data.passwordMinLength
        loginLockEnabled.value = data.data.loginLockEnabled
        showWelcome.value = data.data.showWelcome
        theme.value = data.data.theme || 'light'
        // 更新页面标题
        document.title = systemName.value
        // 应用主题
        applyTheme(theme.value)
      }
    } catch (error) {
      console.error('获取系统设置失败:', error)
    }
  }

  // 更新系统名称
  const updateSystemName = (name) => {
    systemName.value = name
    document.title = name
  }

  // 更新系统描述
  const updateSystemDesc = (desc) => {
    systemDesc.value = desc
  }

  // 更新密码最小长度
  const updatePasswordMinLength = (length) => {
    passwordMinLength.value = length
  }

  // 更新登录锁定状态
  const updateLoginLockEnabled = (enabled) => {
    loginLockEnabled.value = enabled
  }

  // 更新欢迎语显示状态
  const updateShowWelcome = (show) => {
    showWelcome.value = show
  }

  // 更新主题
  const updateTheme = (newTheme) => {
    theme.value = newTheme
    applyTheme(newTheme)
  }

  // 应用主题
  const applyTheme = (themeName) => {
    const themes = {
      light: {
        '--background-color': '#ffffff',
        '--text-color': '#2c3e50',
        '--header-background': '#409eff',
        '--menu-background': '#ffffff',
        '--border-color': '#e6e6e6',
        '--menu-active-background': '#d9ecff',
        '--menu-active-text-color': '#409eff',
        '--menu-text-color': '#2c3e50'
      },
      dark: {
        '--background-color': '#1a1a1a',
        '--text-color': '#e6e6e6',
        '--header-background': '#2c3e50',
        '--menu-background': '#2c3e50',
        '--border-color': '#4c4c4c',
        '--menu-text-color': '#ffffff',
        '--menu-active-background': '#1e90ff33',
        '--menu-active-text-color': '#409eff'
      },
      blue: {
        '--background-color': '#f0f8ff',
        '--text-color': '#2c3e50',
        '--header-background': '#1e90ff',
        '--menu-background': '#ffffff',
        '--border-color': '#b8d4ff',
        '--menu-active-background': '#c6e2ff',
        '--menu-active-text-color': '#1e90ff',
        '--menu-text-color': '#2c3e50'
      }
    }

    const root = document.documentElement
    const themeColors = themes[themeName] || themes.light

    // 设置CSS变量
    Object.entries(themeColors).forEach(([property, value]) => {
      root.style.setProperty(property, value)
    })

    // 设置data-theme属性
    root.setAttribute('data-theme', themeName)
  }

  // 更新所有系统设置
  const updateSettings = (settings) => {
    systemName.value = settings.systemName
    systemDesc.value = settings.systemDesc
    passwordMinLength.value = settings.passwordMinLength
    loginLockEnabled.value = settings.loginLockEnabled
    showWelcome.value = settings.showWelcome
    theme.value = settings.theme || 'light'
    document.title = settings.systemName
    applyTheme(theme.value)
  }

  return {
    systemName,
    systemDesc,
    passwordMinLength,
    loginLockEnabled,
    showWelcome,
    theme,
    fetchSettings,
    updateSystemName,
    updateSystemDesc,
    updatePasswordMinLength,
    updateLoginLockEnabled,
    updateShowWelcome,
    updateTheme,
    updateSettings
  }
}) 