import Mock from 'mockjs'

// 生成订单号
const generateOrderNo = () => {
  return Mock.mock({
    'string|12': /[A-Z0-9]/
  }).string
}

// 生成手机号
const generatePhone = () => {
  return Mock.mock(/^1[3-9]\d{9}$/)
}

// 生成物流跟踪数据
const generateTrackingData = (options) => {
  // 解析查询参数
  const url = new URL(options.url, 'http://localhost')
  const params = Object.fromEntries(url.searchParams)
  const page = parseInt(params.page) || 1
  const limit = parseInt(params.limit) || 10
  const orderNo = params.orderNo
  const status = params.status

  // 生成列表数据
  const list = []
  const statuses = ['pending', 'shipped', 'delivered', 'cancelled']
  const locations = ['广州市仓库', '深圳市转运中心', '上海市配送中心', '北京市配送站', '杭州市仓储中心']
  const descriptions = [
    '快件已到达仓库，等待发货',
    '快件已从仓库发出，正在运输中',
    '快件已到达转运中心，准备发往下一站',
    '快件正在派送中，请保持电话畅通',
    '快件已签收，感谢您的使用'
  ]

  // 生成当前页的数据
  for (let i = 0; i < limit; i++) {
    const currentStatus = status || statuses[Mock.Random.integer(0, 3)]
    const currentOrderNo = orderNo || generateOrderNo()
    const createTime = Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    const trackingTime = currentStatus === 'pending' ? null : Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    
    list.push({
      id: Mock.Random.id(),
      orderNo: currentOrderNo,
      status: currentStatus,
      consignee: Mock.Random.cname(),
      consigneePhone: generatePhone(),
      consigneeAddress: Mock.Random.county(true),
      location: currentStatus === 'pending' ? null : Mock.Random.pick(locations),
      description: currentStatus === 'pending' ? null : Mock.Random.pick(descriptions),
      createTime,
      trackingTime
    })
  }

  // 过滤数据
  let filteredList = list
  if (orderNo) {
    filteredList = filteredList.filter(item => item.orderNo.includes(orderNo))
  }
  if (status) {
    filteredList = filteredList.filter(item => item.status === status)
  }

  return {
    code: 0,
    message: 'success',
    data: {
      records: filteredList,
      total: 100,
      size: limit,
      current: page
    }
  }
}

// 生成物流跟踪详情
const generateTrackingDetail = (orderNo) => {
  const status = Mock.Random.pick(['pending', 'shipped', 'delivered', 'cancelled'])
  
  return {
    code: 0,
    message: 'success',
    data: {
      id: Mock.Random.id(),
      orderNo,
      status,
      consignee: Mock.Random.cname(),
      consigneePhone: generatePhone(),
      consigneeAddress: Mock.Random.county(true),
      createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }
  }
}

// 生成物流跟踪记录列表
const generateTrackingList = (orderNo) => {
  const count = Mock.Random.integer(1, 5)
  const list = []
  const locations = ['广州市仓库', '深圳市转运中心', '上海市配送中心', '北京市配送站', '杭州市仓储中心']
  const descriptions = [
    '快件已到达仓库，等待发货',
    '快件已从仓库发出，正在运输中',
    '快件已到达转运中心，准备发往下一站',
    '快件正在派送中，请保持电话畅通',
    '快件已签收，感谢您的使用'
  ]

  for (let i = 0; i < count; i++) {
    list.push({
      id: Mock.Random.id(),
      shipmentId: Mock.Random.id(),
      orderNo,
      location: Mock.Random.pick(locations),
      description: Mock.Random.pick(descriptions),
      createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    })
  }

  return {
    code: 0,
    message: 'success',
    data: list
  }
}

// 添加物流跟踪信息
const addTracking = (options) => {
  const body = JSON.parse(options.body)
  return {
    code: 0,
    message: 'success',
    data: {
      ...body,
      id: Mock.Random.id(),
      createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }
  }
}

// 注册 Mock 接口
Mock.mock(/\/api\/logistics\/tracking\/page(\?.+)?$/, 'get', generateTrackingData)
Mock.mock(/\/api\/logistics\/shipment\/\w+$/, 'get', (options) => {
  const orderNo = options.url.split('/').pop()
  return generateTrackingDetail(orderNo)
})
Mock.mock(/\/api\/logistics\/tracking\/list\/\w+$/, 'get', (options) => {
  const orderNo = options.url.split('/').pop()
  return generateTrackingList(orderNo)
})
Mock.mock('/api/logistics/tracking', 'post', addTracking)

export default {
  generateTrackingData,
  generateTrackingDetail,
  generateTrackingList,
  addTracking
} 