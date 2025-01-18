export default [
  {
    path: '/dashboard',
    meta: { title: '首页', icon: 'House' }
  },
  {
    path: '/dashboard/basic',
    meta: { title: '基础数据', icon: 'DataLine' },
    children: [
      {
        path: 'supplier',
        meta: { title: '供应商管理' }
      },
      {
        path: 'customer',
        meta: { title: '客户管理' }
      },
      {
        path: 'dealers',
        meta: { title: '经销商管理' }
      },
      {
        path: 'retailers',
        meta: { title: '零售商管理' }
      },
      {
        path: 'purchaser',
        meta: { title: '采购员管理' }
      },
      {
        path: 'handler',
        meta: { title: '经办人管理' }
      }
    ]
  },
  {
    path: '/dashboard/product',
    meta: { title: '商品管理', icon: 'Goods' },
    children: [
      {
        path: 'category',
        meta: { title: '商品分类' }
      },
      {
        path: 'list',
        meta: { title: '商品信息' }
      },
      {
        path: 'pricing',
        meta: { title: '商品定价' }
      }
    ]
  },
  {
    path: '/dashboard/purchase',
    meta: { title: '采购管理', icon: 'ShoppingCart' },
    children: [
      {
        path: 'requisition',
        meta: { title: '采购申请' }
      },
      {
        path: 'order',
        meta: { title: '采购订单' }
      },
      {
        path: 'inbound',
        meta: { title: '采购入库' }
      }
    ]
  },
  {
    path: '/dashboard/sale',
    meta: { title: '销售管理', icon: 'Sell' },
    children: [
      {
        path: 'order',
        meta: { title: '销售订单' }
      },
      {
        path: 'return',
        meta: { title: '退货管理' }
      },
      {
        path: 'variance',
        meta: { title: '销售差异' }
      }
    ]
  },
  // 仓库管理
  {
    path: '/dashboard/warehouse',
    name: 'Warehouse',
    meta: { title: '仓库管理', icon: 'el-icon-house' },
    children: [
      {
        path: '/dashboard/warehouse/info',
        name: 'WarehouseInfo',
        meta: { title: '仓库信息', icon: 'el-icon-office-building' }
      },
      {
        path: '/dashboard/warehouse/stock',
        name: 'WarehouseStock',
        meta: { title: '库存管理', icon: 'el-icon-box' }
      }
    ]
  },
  // 资金管理
  {
    path: '/dashboard/finance',
    meta: { title: '资金管理', icon: 'Money' },
    children: [
      {
        path: 'income',
        meta: { title: '收入管理' }
      },
      {
        path: 'expense',
        meta: { title: '支出管理' }
      },
      {
        path: 'account',
        meta: { title: '账户管理' }
      },
      {
        path: 'report',
        meta: { title: '财务报表' }
      }
    ]
  },
  // 通知管理
  {
    path: '/dashboard/notice',
    meta: { title: '通知管理', icon: 'Bell' },
    children: [
      {
        path: 'list',
        meta: { title: '通知列表' }
      },
      {
        path: 'publish',
        meta: { title: '发布通知' }
      }
    ]
  }
] 