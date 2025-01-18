import { createRouter, createWebHashHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Dashboard from '../views/Dashboard.vue'
import Home from '../views/Home.vue'
import UserManage from '../views/UserManage.vue'
import Profile from '../views/Profile.vue'
import SystemSettings from '../views/SystemSettings.vue'
import RoleManage from '../views/RoleManage.vue'
import OperationLog from '../views/OperationLog.vue'
import LoginLog from '../views/LoginLog.vue'
import SupplierManage from '../views/SupplierManage.vue'
import CustomerManage from '../views/CustomerManage.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    children: [
      {
        path: '',
        name: 'Home',
        component: Home
      },
      {
        path: 'users',
        name: 'UserManage',
        component: UserManage
      },
      {
        path: 'roles',
        name: 'RoleManage',
        component: RoleManage
      },
      {
        path: 'profile',
        name: 'Profile',
        component: Profile
      },
      {
        path: 'settings',
        name: 'SystemSettings',
        component: SystemSettings
      },
      {
        path: 'operation-log',
        name: 'OperationLog',
        component: OperationLog
      },
      {
        path: 'login-log',
        name: 'LoginLog',
        component: LoginLog
      },
      {
        path: 'suppliers',
        name: 'SupplierManage',
        component: SupplierManage
      },
      {
        path: 'customers',
        name: 'CustomerManage',
        component: CustomerManage
      },
      {
        path: 'basic',
        name: 'Basic',
        component: () => import('@/views/basic/index.vue'),
        meta: { title: '基础数据', icon: 'DataLine' },
        children: [
          {
            path: 'dealers',
            name: 'Dealers',
            component: () => import('@/views/basic/dealer/index.vue'),
            meta: { title: '经销商管理' }
          },
          {
            path: 'retailers',
            name: 'Retailers',
            component: () => import('@/views/basic/retailer/index.vue'),
            meta: { title: '零售商管理' }
          },
          {
            path: 'supplier',
            component: SupplierManage,
            meta: { title: '供应商管理' }
          },
          {
            path: 'customer',
            component: CustomerManage,
            meta: { title: '客户管理' }
          },
          {
            path: 'purchaser',
            component: () => import('@/views/basic/purchaser/index.vue'),
            meta: { title: '采购员管理' }
          },
          {
            path: 'handler',
            name: 'Handler',
            component: () => import('@/views/basic/handler/index.vue'),
            meta: { title: '经办人管理' }
          }
        ]
      },
      {
        path: 'product',
        name: 'Product',
        component: () => import('@/views/product/index.vue'),
        meta: { title: '商品管理', icon: 'Goods' },
        children: [
          {
            path: 'info',
            name: 'ProductInfo',
            component: () => import('@/views/product/info/index.vue'),
            meta: { title: '商品信息' }
          },
          {
            path: 'info/form',
            name: 'ProductForm',
            component: () => import('@/views/product/info/form.vue'),
            meta: { title: '商品表单' }
          },
          {
            path: 'info/detail',
            name: 'ProductDetail',
            component: () => import('@/views/product/info/detail.vue'),
            meta: { title: '商品详情' }
          },
          {
            path: 'category',
            name: 'ProductCategory',
            component: () => import('@/views/product/category/index.vue'),
            meta: { title: '分类管理' }
          },
          {
            path: 'pricing',
            name: 'ProductPricing',
            component: () => import('@/views/product/pricing/index.vue'),
            meta: { title: '定价管理' }
          }
        ]
      },
      {
        path: 'purchase',
        name: 'Purchase',
        component: () => import('@/views/purchase/index.vue'),
        meta: { title: '采购管理', icon: 'ShoppingCart' },
        children: [
          {
            path: 'requisition',
            name: 'PurchaseRequisition',
            component: () => import('@/views/purchase/requisition/index.vue'),
            meta: { title: '采购申请' }
          },
          {
            path: 'requisition/form',
            name: 'PurchaseRequisitionForm',
            component: () => import('@/views/purchase/requisition/form.vue'),
            meta: { title: '采购申请表单' }
          },
          {
            path: 'order',
            name: 'PurchaseOrder',
            component: () => import('@/views/purchase/order/index.vue'),
            meta: { title: '采购订单' }
          },
          {
            path: 'order/form',
            name: 'PurchaseOrderForm',
            component: () => import('@/views/purchase/order/form.vue'),
            meta: { title: '采购订单表单' }
          },
          {
            path: 'inbound',
            name: 'PurchaseInbound',
            component: () => import('@/views/purchase/inbound/index.vue'),
            meta: { title: '采购入库' }
          },
          {
            path: 'inbound/form',
            name: 'PurchaseInboundForm',
            component: () => import('@/views/purchase/inbound/form.vue'),
            meta: { title: '采购入库表单' }
          }
        ]
      },
      {
        path: 'warehouse',
        name: 'Warehouse',
        meta: { title: '仓库管理', icon: 'el-icon-house' },
        children: [
          {
            path: 'info',
            component: () => import('@/views/warehouse/info/index.vue'),
            name: 'WarehouseInfo',
            meta: { title: '仓库信息', icon: 'el-icon-office-building' }
          },
          {
            path: 'stock',
            component: () => import('@/views/warehouse/stock/index.vue'),
            name: 'WarehouseStock',
            meta: { title: '库存管理', icon: 'el-icon-box' }
          },
          {
            path: 'stock/detail',
            component: () => import('@/views/warehouse/stock/detail/index.vue'),
            name: 'WarehouseStockDetail',
            meta: { title: '库存明细', icon: 'el-icon-document' }
          }
        ]
      },
      {
        path: 'sale',
        name: 'Sale',
        component: () => import('../views/sale/index.vue'),
        meta: { title: '销售管理', icon: 'Sell' },
        children: [
          {
            path: 'order',
            name: 'SaleOrder',
            component: () => import('../views/sale/order/index.vue'),
            meta: { title: '销售订单' }
          },
          {
            path: 'order/form',
            name: 'SaleOrderForm',
            component: () => import('../views/sale/order/form.vue'),
            meta: { title: '销售订单表单' }
          },
          {
            path: 'return',
            name: 'SaleReturn',
            component: () => import('../views/sale/return/index.vue'),
            meta: { title: '退货管理' }
          },
          {
            path: 'return/form',
            name: 'SaleReturnForm',
            component: () => import('../views/sale/return/form.vue'),
            meta: { title: '退货表单' }
          },
          {
            path: 'variance',
            name: 'SaleVariance',
            component: () => import('../views/sale/variance/index.vue'),
            meta: { title: '销售差异' }
          },
          {
            path: 'variance/form',
            name: 'SaleVarianceForm',
            component: () => import('../views/sale/variance/form.vue'),
            meta: { title: '差异表单' }
          }
        ]
      },
      {
        path: 'notice',
        name: 'Notice',
        component: () => import('@/views/notice/index.vue'),
        redirect: 'notice/list',
        meta: { title: '通知管理', icon: 'Bell' },
        children: [
          {
            path: 'list',
            name: 'NoticeList',
            component: () => import('@/views/notice/NoticeList.vue'),
            meta: { title: '通知列表' }
          },
          {
            path: 'publish',
            name: 'PublishNotice',
            component: () => import('@/views/notice/PublishNotice.vue'),
            meta: { title: '发布通知' }
          }
        ]
      },
      {
        path: 'finance',
        name: 'Finance',
        component: () => import('@/views/finance/index.vue'),
        redirect: 'finance/income',
        meta: { title: '资金管理', icon: 'Money' },
        children: [
          {
            path: 'income',
            name: 'Income',
            component: () => import('@/views/finance/income/index.vue'),
            meta: { title: '收入管理' }
          },
          {
            path: 'income/form',
            name: 'IncomeForm',
            component: () => import('@/views/finance/income/form.vue'),
            meta: { title: '收入表单' }
          },
          {
            path: 'expense',
            name: 'Expense',
            component: () => import('@/views/finance/expense/index.vue'),
            meta: { title: '支出管理' }
          },
          {
            path: 'expense/form',
            name: 'ExpenseForm',
            component: () => import('@/views/finance/expense/form.vue'),
            meta: { title: '支出表单' }
          },
          {
            path: 'account',
            name: 'Account',
            component: () => import('@/views/finance/account/index.vue'),
            meta: { title: '账户管理' }
          },
          {
            path: 'account/form',
            name: 'AccountForm',
            component: () => import('@/views/finance/account/form.vue'),
            meta: { title: '账户表单' }
          },
          {
            path: 'report',
            name: 'FinanceReport',
            component: () => import('@/views/finance/report/index.vue'),
            meta: { title: '财务报表' }
          }
        ]
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router 