import request from '@/utils/request'

// 获取通知列表
export function getNoticeList(params) {
  return request({
    url: '/api/notice/list',
    method: 'get',
    params
  })
}

// 获取通知详情
export function getNoticeById(id) {
  return request({
    url: `/api/notice/${id}`,
    method: 'get'
  })
}

// 创建通知
export function createNotice(data) {
  return request({
    url: '/api/notice',
    method: 'post',
    data
  })
}

// 更新通知
export function updateNotice(id, data) {
  return request({
    url: `/api/notice/${id}`,
    method: 'put',
    data
  })
}

// 删除通知
export function deleteNotice(id) {
  return request({
    url: `/api/notice/${id}`,
    method: 'delete'
  })
} 