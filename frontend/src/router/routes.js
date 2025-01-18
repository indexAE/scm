const routes = [
    {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/layout/index.vue'),
        children: [
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
                    }
                ]
            },
            {
                path: 'sale',
                name: 'Sale',
                component: () => import('@/views/sale/index.vue'),
                meta: { title: '销售管理', icon: 'Sell' },
                children: [
                    {
                        path: 'order',
                        name: 'SaleOrder',
                        component: () => import('@/views/sale/order/index.vue'),
                        meta: { title: '销售订单' }
                    },
                    {
                        path: 'return',
                        name: 'SaleReturn',
                        component: () => import('@/views/sale/return/index.vue'),
                        meta: { title: '退货管理' }
                    },
                    {
                        path: 'variance',
                        name: 'SaleVariance',
                        component: () => import('@/views/sale/variance/index.vue'),
                        meta: { title: '销售差异' }
                    }
                ]
            },
            {
                path: 'notice',
                name: 'Notice',
                redirect: '/dashboard/notice/list',
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
            }
        ]
    }
];

export default routes; 