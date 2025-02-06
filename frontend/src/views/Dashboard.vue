<template>
  <el-container class="layout-container">
    <!-- 顶部导航栏 -->
    <el-header>
      <div class="header-left">
        <el-icon 
          class="collapse-btn"
          @click="isCollapse = !isCollapse"
        >
          <fold v-if="!isCollapse"/>
          <expand v-else/>
        </el-icon>
        <h2>{{ settingsStore.systemName }}</h2>
        <el-breadcrumb separator="/" class="breadcrumb">
          <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item v-for="(item, index) in breadcrumbItems" :key="index">
            {{ item.title }}
          </el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="header-right">
        <el-dropdown @command="handleCommand">
          <span class="el-dropdown-link">
            <el-avatar 
              :size="32"
              :src="userAvatar"
              class="user-avatar"
            />
            {{ username }}<el-icon class="el-icon--right"><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    
    <!-- 标签页导航 -->
    <div class="tabs-container">
      <el-tabs
        v-model="activeTab"
        type="card"
        closable
        @tab-click="handleTabClick"
        @tab-remove="handleTabRemove"
        class="nav-tabs"
      >
        <el-tab-pane
          v-for="item in visitedViews"
          :key="item.path"
          :label="item.title"
          :name="item.path"
          :closable="item.path !== '/dashboard'"
        >
        </el-tab-pane>
      </el-tabs>
    </div>

    <el-container>
      <!-- 左侧菜单栏 -->
      <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar-container">
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical"
          @select="handleSelect"
          :router="true"
          :collapse="isCollapse"
        >
          <el-menu-item index="/dashboard">
            <el-icon><house /></el-icon>
            <template #title>首页</template>
          </el-menu-item>

          <el-sub-menu index="2">
            <template #title>
              <el-icon><user /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/dashboard/users">用户列表</el-menu-item>
            <el-menu-item index="/dashboard/roles">角色管理</el-menu-item>
            <el-menu-item index="/dashboard/permissions">权限管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="3">
            <template #title>
              <el-icon><bell /></el-icon>
              <span>公告通知</span>
            </template>
            <el-menu-item index="/dashboard/notice/list">通知列表</el-menu-item>
            <el-menu-item index="/dashboard/notice/publish">发布通知</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="4">
            <template #title>
              <el-icon><box /></el-icon>
              <span>基础数据</span>
            </template>
            <el-menu-item index="/dashboard/basic/supplier">供应商管理</el-menu-item>
            <el-menu-item index="/dashboard/basic/customer">客户管理</el-menu-item>
            <el-menu-item index="/dashboard/basic/dealers">经销商管理</el-menu-item>
            <el-menu-item index="/dashboard/basic/retailers">零售商管理</el-menu-item>
            <el-menu-item index="/dashboard/basic/purchaser">采购员管理</el-menu-item>
            <el-menu-item index="/dashboard/basic/handler">经办人管理</el-menu-item>
          </el-sub-menu>

          <!-- 商品管理 -->
          <el-sub-menu index="5">
            <template #title>
              <el-icon><goods /></el-icon>
              <span>商品管理</span>
            </template>
            <el-menu-item index="/dashboard/product/info">商品信息</el-menu-item>
            <el-menu-item index="/dashboard/product/category">分类管理</el-menu-item>
            <el-menu-item index="/dashboard/product/pricing">定价管理</el-menu-item>
          </el-sub-menu>

          <!-- 采购管理 -->
          <el-sub-menu index="6">
            <template #title>
              <el-icon><shopping-cart /></el-icon>
              <span>采购管理</span>
            </template>
            <el-menu-item index="/dashboard/purchase/requisition">采购申请</el-menu-item>
            <el-menu-item index="/dashboard/purchase/order">采购订单</el-menu-item>
            <el-menu-item index="/dashboard/purchase/inbound">采购入库</el-menu-item>
          </el-sub-menu>

          <!-- 销售管理 -->
          <el-sub-menu index="7">
            <template #title>
              <el-icon><Sell /></el-icon>
              <span>销售管理</span>
            </template>
            <el-menu-item index="/dashboard/sale/order">
              <el-icon><Document /></el-icon>
              <span>销售订单</span>
            </el-menu-item>
            <el-menu-item index="/dashboard/sale/return">
              <el-icon><RefreshRight /></el-icon>
              <span>退货管理</span>
            </el-menu-item>
            <el-menu-item index="/dashboard/sale/variance">
              <el-icon><Warning /></el-icon>
              <span>销售差异</span>
            </el-menu-item>
          </el-sub-menu>

          <!-- 仓库管理 -->
          <el-sub-menu index="8">
            <template #title>
              <el-icon><house /></el-icon>
              <span>仓库管理</span>
            </template>
            <el-menu-item index="/dashboard/warehouse/info">仓库信息</el-menu-item>
            <el-menu-item index="/dashboard/warehouse/stock">库存管理</el-menu-item>
          </el-sub-menu>

          <!-- 资金管理 -->
          <el-sub-menu index="9">
            <template #title>
              <el-icon><money /></el-icon>
              <span>资金管理</span>
            </template>
            <el-menu-item index="/dashboard/finance/income">收入管理</el-menu-item>
            <el-menu-item index="/dashboard/finance/expense">支出管理</el-menu-item>
            <el-menu-item index="/dashboard/finance/account">账户管理</el-menu-item>
            <el-menu-item index="/dashboard/finance/report">财务报表</el-menu-item>
          </el-sub-menu>

          <!-- 物流管理 -->
          <el-sub-menu index="11">
            <template #title>
              <el-icon><van /></el-icon>
              <span>物流管理</span>
            </template>
            <el-menu-item index="/dashboard/logistics/shipment">发货管理</el-menu-item>
            <el-menu-item index="/dashboard/logistics/tracking">物流跟踪</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="10">
            <template #title>
              <el-icon><document-icon /></el-icon>
              <span>日志管理</span>
            </template>
            <el-menu-item index="/dashboard/operation-log">操作日志</el-menu-item>
            <el-menu-item index="/dashboard/login-log">登录日志</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/dashboard/settings">
            <el-icon><setting /></el-icon>
            <span>系统设置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <!-- 主内容区域 -->
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, watch, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { 
  ArrowDown, 
  House, 
  User, 
  Setting, 
  Document as DocumentIcon, 
  Box,
  ShoppingCart,
  Goods,
  Sell,
  Document,
  RefreshRight,
  Warning,
  Fold,
  Expand,
  Bell,
  Money,
  Van
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useSettingsStore } from '../stores/settings'

const router = useRouter()
const route = useRoute()
const settingsStore = useSettingsStore()
const username = ref(localStorage.getItem('username') || '用户')
const userAvatar = ref(localStorage.getItem('userAvatar') || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png')
const isCollapse = ref(false)
const activeTab = ref('/dashboard')
const visitedViews = ref([
  { path: '/dashboard', title: '首页' }
])

// 路由路径与中文标题的映射
const routeTitleMap = {
  'users': '用户管理',
  'roles': '角色管理',
  'permissions': '权限管理',
  'notice': '公告通知',
  'list': '通知列表',
  'publish': '发布通知',
  'basic': '基础数据',
  'supplier': '供应商管理',
  'customer': '客户管理',
  'dealers': '经销商管理',
  'retailers': '零售商管理',
  'purchaser': '采购员管理',
  'handler': '经办人管理',
  'product': '商品管理',
  'info': '商品信息',
  'category': '分类管理',
  'pricing': '定价管理',
  'logistics': '物流管理',
  'shipment': '发货管理',
  'tracking': '物流跟踪',
  'carrier': '承运商管理',
  'purchase': '采购管理',
  'requisition': '采购申请',
  'order': '订单管理',
  'inbound': '采购入库',
  'sale': '销售管理',
  'return': '退货管理',
  'variance': '销售差异',
  'warehouse': '仓库管理',
  'stock': '库存管理',
  'finance': '资金管理',
  'income': '收入管理',
  'expense': '支出管理',
  'account': '账户管理',
  'report': '财务报表',
  'operation-log': '操作日志',
  'login-log': '登录日志',
  'settings': '系统设置',
  'profile': '个人信息'
}

// 计算面包屑项
const breadcrumbItems = computed(() => {
  const paths = route.path.split('/').filter(Boolean)
  paths.shift() // 移除 'dashboard'
  
  return paths.map(path => ({
    path: `/${paths.slice(0, paths.indexOf(path) + 1).join('/')}`,
    title: routeTitleMap[path] || path
  }))
})

// 计算当前激活的菜单项
const activeMenu = computed(() => route.path)

// 处理下拉菜单命令
const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('username')
    localStorage.removeItem('userId')
    router.push('/login')
    ElMessage.success('已退出登录')
  } else if (command === 'profile') {
    router.push('/dashboard/profile')
  }
}

// 处理菜单选择
const handleSelect = (index) => {
  router.push(index)
}

// 添加新的标签页
const addView = (route) => {
  const { path } = route
  if (visitedViews.value.some(v => v.path === path)) return
  
  const title = routeTitleMap[path.split('/').pop()] || '未知页面'
  visitedViews.value.push({
    path,
    title
  })
  activeTab.value = path
}

// 关闭标签页
const handleTabRemove = (targetPath) => {
  const tabs = visitedViews.value
  
  if (activeTab.value === targetPath) {
    tabs.forEach((tab, index) => {
      if (tab.path === targetPath) {
        const nextTab = tabs[index + 1] || tabs[index - 1]
        if (nextTab) {
          activeTab.value = nextTab.path
          router.push(nextTab.path)
        }
      }
    })
  }
  
  visitedViews.value = tabs.filter(tab => tab.path !== targetPath)
}

// 点击标签页
const handleTabClick = (tab) => {
  router.push(tab.props.name)
}

// 监听路由变化
watch(
  () => route.path,
  (path) => {
    if (path === '/dashboard') {
      activeTab.value = path
      return
    }
    addView(route)
  },
  { immediate: true }
)

// 监听 localStorage 变化以更新头像
onMounted(() => {
  // settingsStore.fetchSettings()
  
  // 监听 storage 事件来更新头像
  window.addEventListener('storage', () => {
    userAvatar.value = localStorage.getItem('userAvatar') || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
  })
})

// 组件卸载时移除事件监听
onUnmounted(() => {
  window.removeEventListener('storage', () => {
    userAvatar.value = localStorage.getItem('userAvatar') || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
  })
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-header {
  background-color: var(--header-background);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.header-left {
  display: flex;
  align-items: center;
  flex: 1;
}

.header-left h2 {
  margin: 0;
  margin-right: 20px;
  white-space: nowrap;
  font-size: 20px;
  font-weight: bold;
}

.el-dropdown-link {
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

.el-aside {
  background-color: var(--menu-background);
  border-right: 1px solid var(--border-color);
  position: fixed;
  top: 60px;
  bottom: 0;
  left: 0;
  z-index: 900;
  overflow-y: auto;
  max-height: calc(100vh - 60px);
  transition: width 0.3s;
  
  /* 美化滚动条 */
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: rgba(144, 147, 153, 0.3);
    border-radius: 3px;
  }
  
  &::-webkit-scrollbar-track {
    background: transparent;
  }
  
  /* 平滑滚动 */
  scroll-behavior: smooth;
}

.el-menu {
  border-right: none;
  background-color: var(--menu-background);
}

.el-menu-vertical {
  height: 100%;
  overflow-y: auto;
  
  /* 美化滚动条 */
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: rgba(144, 147, 153, 0.3);
    border-radius: 3px;
  }
  
  &::-webkit-scrollbar-track {
    background: transparent;
  }
}

/* 确保子菜单可以滚动 */
:deep(.el-sub-menu .el-menu) {
  overflow-y: visible;
  max-height: none;
}

:deep(.el-menu-item), :deep(.el-sub-menu__title) {
  font-size: 16px !important;
  height: 50px !important;
  line-height: 50px !important;
}

:deep(.el-menu-item .el-icon), :deep(.el-sub-menu__title .el-icon) {
  font-size: 18px;
  margin-right: 10px;
}

:deep(.el-sub-menu .el-menu-item) {
  font-size: 15px !important;
  height: 45px !important;
  line-height: 45px !important;
  padding-left: 45px !important;
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 200px;
}

.el-menu--collapse {
  width: 64px;
}

.el-main {
  margin-left: 200px;
  margin-top: 60px;
  padding: 20px;
  background-color: var(--background-color);
}

.welcome {
  text-align: center;
  padding: 50px;
}

.system-desc {
  color: #666;
  margin-top: 20px;
}

.header-right {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding-left: 20px;
}

.breadcrumb {
  color: white;
  margin-left: 20px;
  font-size: 16px;
}

:deep(.el-breadcrumb__item) {
  color: white !important;
  font-size: 16px !important;
}

:deep(.el-breadcrumb__inner) {
  color: white !important;
  font-size: 16px !important;
  font-weight: normal !important;
}

:deep(.el-breadcrumb__inner.is-link:hover) {
  color: #e6e6e6 !important;
}

:deep(.el-breadcrumb__separator) {
  color: white !important;
  font-size: 16px !important;
  margin: 0 8px;
}

.tabs-container {
  position: fixed;
  top: 60px;
  left: v-bind(isCollapse ? '64px' : '200px');
  right: 0;
  z-index: 800;
  background: white;
  transition: left 0.3s;
  border-bottom: 1px solid #e6e6e6;
}

.nav-tabs {
  padding: 6px 4px 0;
}

.el-main {
  margin-top: 100px;  /* 60px header + 40px tabs */
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  margin-right: 16px;
}

.collapse-btn:hover {
  color: var(--el-color-primary);
}

.sidebar-container {
  transition: width 0.3s;
  overflow: hidden;
}

.breadcrumb {
  margin-bottom: 20px;
  padding: 8px 0;
  border-bottom: 1px solid #e6e6e6;
}

.nav-tabs {
  background: #fff;
  padding: 6px 4px 0;
  margin-bottom: 15px;
}

:deep(.el-tabs__header) {
  margin: 0;
}

:deep(.el-tabs__item) {
  height: 30px;
  line-height: 30px;
  padding: 0 15px !important;
}

:deep(.el-tabs__nav) {
  border: none;
}

.el-menu-item.is-active {
  background-color: var(--header-background);
  color: white;
}

/* 当菜单折叠时调整主内容区域的边距 */
.el-main.collapsed {
  margin-left: 64px;
}

.user-avatar {
  border: 2px solid rgba(255, 255, 255, 0.8);
}
</style> 