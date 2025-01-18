<template>
  <router-view></router-view>
</template>

<script setup>
import { onMounted } from 'vue'
import { useSettingsStore } from './stores/settings'

const settingsStore = useSettingsStore()

// 只在应用初始化时加载一次系统设置
onMounted(() => {
  const settingsLoaded = sessionStorage.getItem('settingsLoaded')
  if (!settingsLoaded) {
    settingsStore.fetchSettings()
    sessionStorage.setItem('settingsLoaded', 'true')
  }
})
</script>

<style>
:root {
  --background-color: #ffffff;
  --text-color: #2c3e50;
  --header-background: #409eff;
  --menu-background: #ffffff;
  --border-color: #e6e6e6;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: var(--text-color);
  height: 100vh;
  background-color: var(--background-color);
}

/* 全局主题样式 */
.el-header {
  background-color: var(--header-background) !important;
}

.el-aside {
  background-color: var(--menu-background) !important;
  border-color: var(--border-color) !important;
}

.el-menu {
  background-color: var(--menu-background) !important;
  border-color: var(--border-color) !important;
}

.el-menu-item,
.el-sub-menu__title {
  color: var(--menu-text-color, var(--text-color)) !important;
}

.el-menu-item.is-active {
  background-color: var(--menu-active-background) !important;
  color: var(--menu-active-text-color) !important;
}

.el-menu-item:hover,
.el-sub-menu__title:hover {
  background-color: var(--menu-active-background) !important;
}

.el-sub-menu.is-active .el-sub-menu__title {
  color: var(--menu-active-text-color) !important;
}

.el-menu--popup {
  background-color: var(--menu-background) !important;
}

.el-menu--popup .el-menu-item {
  color: var(--menu-text-color, var(--text-color)) !important;
}

.el-menu--popup .el-menu-item.is-active {
  background-color: var(--menu-active-background) !important;
  color: var(--menu-active-text-color) !important;
}

.el-card {
  background-color: var(--card-background, var(--background-color)) !important;
  border-color: var(--border-color) !important;
  color: var(--text-color) !important;
}

.el-table {
  background-color: var(--background-color) !important;
  color: var(--text-color) !important;
}

.el-table th,
.el-table td {
  background-color: var(--background-color) !important;
  border-color: var(--border-color) !important;
  color: var(--text-color) !important;
}

.el-dialog {
  background-color: var(--card-background, var(--background-color)) !important;
}

.el-dialog__title {
  color: var(--text-color) !important;
}

.el-form-item__label {
  color: var(--text-color) !important;
}

.el-input__inner {
  background-color: var(--background-color) !important;
  color: var(--text-color) !important;
  border-color: var(--border-color) !important;
}

.el-button {
  border-color: var(--border-color) !important;
}

/* 深色主题下的特殊处理 */
[data-theme='dark'] {
  .el-button--default {
    background-color: var(--card-background) !important;
    color: var(--text-color) !important;
  }
  
  .el-input__inner::placeholder {
    color: #909399 !important;
  }
  
  .el-table__empty-text {
    color: var(--text-color) !important;
  }
  
  .el-pagination {
    color: var(--text-color) !important;
    
    .el-pagination__total,
    .el-pagination__jump {
      color: var(--text-color) !important;
    }
  }
}
</style> 