// 修改 ResizeObserver 原型以防止循环错误
if (typeof window !== 'undefined' && window.ResizeObserver) {
  const _ResizeObserver = window.ResizeObserver
  window.ResizeObserver = class extends _ResizeObserver {
    constructor(callback) {
      super((entries, observer) => {
        window.requestAnimationFrame(() => {
          callback(entries, observer)
        })
      })
    }
  }
}

// 在最开始处理 ResizeObserver 错误
const originalError = console.error
console.error = (...args) => {
  if (args[0]?.includes?.('ResizeObserver') || args[0]?.message?.includes?.('ResizeObserver')) {
    return
  }
  originalError.apply(console, args)
}

// 添加全局错误处理
if (typeof window !== 'undefined') {
  const originalOnError = window.onerror
  window.onerror = function (msg) {
    if (msg.includes('ResizeObserver')) {
      return false
    }
    if (originalOnError) {
      return originalOnError.apply(this, arguments)
    }
    return false
  }

  // 处理 Promise 错误
  window.addEventListener('unhandledrejection', event => {
    if (event.reason?.message?.includes('ResizeObserver')) {
      event.preventDefault()
      event.stopPropagation()
    }
  })
}

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import { usePermissionStore } from './stores/permission'
import './mock/logistics' // 引入物流跟踪mock数据

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(ElementPlus)
app.use(router)

// 初始化权限store
const permissionStore = usePermissionStore()
permissionStore.init()

app.mount('#app') 