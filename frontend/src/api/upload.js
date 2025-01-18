import request from '@/utils/request'

// 上传文件
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/api/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 下载文件
export function downloadFile(fileId) {
  return request({
    url: `/api/download/${fileId}`,
    method: 'get',
    responseType: 'blob'
  })
} 