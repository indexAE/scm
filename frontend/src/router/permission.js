import router from './index'
import { usePermissionStore } from '../stores/permission'

// 定义需要权限的路由及其所需权限
const permissionMap = {
  '/dashboard/users': ['VIEW_USERS', 'MANAGE_USERS'],
  '/dashboard/roles': ['VIEW_ROLES', 'MANAGE_ROLES'],
  '/dashboard/permissions': ['VIEW_PERMISSIONS', 'MANAGE_PERMISSIONS'],
  '/dashboard/basic/supplier': ['VIEW_SUPPLIERS', 'MANAGE_SUPPLIERS'],
  '/dashboard/basic/customer': ['VIEW_CUSTOMERS', 'MANAGE_CUSTOMERS'],
  '/dashboard/basic/dealers': ['VIEW_DEALERS', 'MANAGE_DEALERS'],
  '/dashboard/basic/retailers': ['VIEW_RETAILERS', 'MANAGE_RETAILERS'],
  '/dashboard/basic/purchaser': ['VIEW_PURCHASERS', 'MANAGE_PURCHASERS'],
  '/dashboard/basic/handler': ['VIEW_HANDLERS', 'MANAGE_HANDLERS'],
  '/dashboard/product/info': ['VIEW_PRODUCTS', 'MANAGE_PRODUCTS'],
  '/dashboard/product/category': ['VIEW_PRODUCT_CATEGORIES', 'MANAGE_PRODUCT_CATEGORIES'],
  '/dashboard/product/pricing': ['VIEW_PRODUCT_PRICING', 'MANAGE_PRODUCT_PRICING'],
  '/dashboard/purchase/requisition': ['VIEW_PURCHASE_REQUISITIONS', 'MANAGE_PURCHASE_REQUISITIONS'],
  '/dashboard/purchase/order': ['VIEW_PURCHASE_ORDERS', 'MANAGE_PURCHASE_ORDERS'],
  '/dashboard/purchase/inbound': ['VIEW_PURCHASE_INBOUND', 'MANAGE_PURCHASE_INBOUND'],
  '/dashboard/sale/order': ['VIEW_SALE_ORDERS', 'MANAGE_SALE_ORDERS'],
  '/dashboard/sale/return': ['VIEW_SALE_RETURNS', 'MANAGE_SALE_RETURNS'],
  '/dashboard/sale/variance': ['VIEW_SALE_VARIANCES', 'MANAGE_SALE_VARIANCES'],
  '/dashboard/warehouse/info': ['VIEW_WAREHOUSE', 'MANAGE_WAREHOUSE'],
  '/dashboard/warehouse/stock': ['VIEW_STOCK', 'MANAGE_STOCK'],
  '/dashboard/finance/income': ['VIEW_FINANCE_INCOME', 'MANAGE_FINANCE_INCOME'],
  '/dashboard/finance/expense': ['VIEW_FINANCE_EXPENSE', 'MANAGE_FINANCE_EXPENSE'],
  '/dashboard/finance/account': ['VIEW_FINANCE_ACCOUNTS', 'MANAGE_FINANCE_ACCOUNTS'],
  '/dashboard/finance/report': ['VIEW_FINANCE_REPORTS'],
  '/dashboard/logistics/shipment': ['VIEW_LOGISTICS_SHIPMENT', 'MANAGE_LOGISTICS_SHIPMENT'],
  '/dashboard/logistics/tracking': ['VIEW_LOGISTICS_TRACKING'],
  '/dashboard/logistics/carrier': ['VIEW_LOGISTICS_CARRIER', 'MANAGE_LOGISTICS_CARRIER'],
  '/dashboard/notice/list': ['VIEW_NOTICES'],
  '/dashboard/notice/publish': ['MANAGE_NOTICES'],
  '/dashboard/operation-log': ['VIEW_OPERATION_LOGS'],
  '/dashboard/login-log': ['VIEW_LOGIN_LOGS'],
  '/dashboard/settings': ['MANAGE_SYSTEM_SETTINGS']
}

// 白名单路由 - 不需要权限验证
const whiteList = ['/login', '/register', '/404', '/403']

// 全局前置守卫
router.beforeEach((to, from, next) => {
  // 获取权限store
  const permissionStore = usePermissionStore()
  
  // 检查是否在白名单中
  if (whiteList.includes(to.path)) {
    next()
    return
  }
  
  // 检查是否登录
  const token = localStorage.getItem('token')
  if (!token) {
    next('/login')
    return
  }
  
  // 如果是首页，直接通过
  if (to.path === '/dashboard') {
    next()
    return
  }
  
  // 获取目标路由所需权限
  const requiredPermissions = permissionMap[to.path]
  
  // 如果路由不需要权限，直接通过
  if (!requiredPermissions) {
    next()
    return
  }
  
  // 检查是否有权限访问
  if (permissionStore.hasAnyPermission(requiredPermissions)) {
    next()
  } else {
    next('/403') // 跳转到无权限页面
  }
})

export default router 