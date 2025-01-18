<template>
  <div class="notice-list">
    <div class="header">
      <h3>通知列表</h3>
      <el-button type="primary" @click="$router.push('/dashboard/notice/publish')">发布通知</el-button>
    </div>

    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="请输入通知标题"></el-input>
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          ></el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 通知列表 -->
    <el-table :data="noticeList" v-loading="loading" border style="width: 100%">
      <el-table-column prop="title" label="标题" min-width="200"></el-table-column>
      <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip></el-table-column>
      <el-table-column label="附件" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.attachments && JSON.parse(scope.row.attachments).length > 0" type="info">
            {{ JSON.parse(scope.row.attachments).length }}个文件
          </el-tag>
          <span v-else>无</span>
        </template>
      </el-table-column>
      <el-table-column prop="publisherName" label="发布人" width="120"></el-table-column>
      <el-table-column prop="createTime" label="发布时间" width="180">
        <template #default="scope">
          {{ new Date(scope.row.createTime).toLocaleString() }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'published' ? 'success' : 'info'">
            {{ scope.row.status === 'published' ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="showDetail(scope.row)">查看</el-button>
          <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      ></el-pagination>
    </div>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="通知详情"
      width="800px"
      destroy-on-close
    >
      <div class="notice-detail">
        <h2>{{ currentNotice?.title }}</h2>
        <div class="notice-info">
          <span>发布人：{{ currentNotice?.publisherName }}</span>
          <span>发布时间：{{ currentNotice?.createTime ? new Date(currentNotice.createTime).toLocaleString() : '' }}</span>
        </div>
        <div class="notice-content" v-html="currentNotice?.content"></div>
        <div class="notice-files" v-if="currentNotice?.attachments && JSON.parse(currentNotice.attachments).length > 0">
          <h4>附件：</h4>
          <ul>
            <li v-for="file in JSON.parse(currentNotice.attachments || '[]')" :key="file.name" class="attachment-item">
              <span class="file-name">{{ file.name }}</span>
              <span class="file-size">{{ formatFileSize(file.size) }}</span>
              <el-button link type="primary" @click="downloadFile(file)">下载</el-button>
            </li>
          </ul>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNoticeList, deleteNotice } from '@/api/notice'

// 搜索表单
const searchForm = ref({
  title: '',
  dateRange: []
})

// 表格数据
const noticeList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 详情对话框
const detailVisible = ref(false)
const currentNotice = ref(null)

// 获取通知列表
const fetchNoticeList = async () => {
  loading.value = true
  try {
    const params = {
      title: searchForm.value.title,
      startTime: searchForm.value.dateRange?.[0],
      endTime: searchForm.value.dateRange?.[1],
      page: currentPage.value,
      size: pageSize.value
    }
    const { records, total: totalCount } = await getNoticeList(params)
    noticeList.value = records
    total.value = totalCount
  } catch (error) {
    console.error('获取通知列表失败:', error)
    ElMessage.error('获取通知列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchNoticeList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    title: '',
    dateRange: []
  }
  handleSearch()
}

// 显示详情
const showDetail = (notice) => {
  currentNotice.value = notice
  detailVisible.value = true
}

// 删除通知
const handleDelete = (notice) => {
  ElMessageBox.confirm(
    '确定要删除该通知吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteNotice(notice.id)
      ElMessage.success('删除成功')
      fetchNoticeList()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 分页相关
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchNoticeList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchNoticeList()
}

// 格式化文件大小
const formatFileSize = (size) => {
  if (size < 1024) {
    return size + ' B'
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + ' KB'
  } else {
    return (size / (1024 * 1024)).toFixed(2) + ' MB'
  }
}

// 下载文件
const downloadFile = async (file) => {
  try {
    const response = await downloadFile(file.fileId)
    const blob = new Blob([response])
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = file.name
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('下载成功')
  } catch (error) {
    console.error('下载失败:', error)
    ElMessage.error('下载失败')
  }
}

onMounted(() => {
  fetchNoticeList()
})
</script>

<style scoped>
.notice-list {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 20px;
  background: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.notice-detail {
  padding: 0 20px;
}

.notice-detail h2 {
  text-align: center;
  margin-bottom: 20px;
}

.notice-info {
  display: flex;
  justify-content: space-between;
  color: #666;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.notice-content {
  line-height: 1.6;
  white-space: pre-wrap;
}

.notice-files {
  margin-top: 20px;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

.notice-files h4 {
  margin-bottom: 10px;
}

.notice-files ul {
  list-style: none;
  padding: 0;
}

.notice-files li {
  line-height: 24px;
  color: #666;
}

.attachment-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px dashed #eee;
}

.attachment-item:last-child {
  border-bottom: none;
}

.file-name {
  flex: 1;
  margin-right: 10px;
}

.file-size {
  color: #999;
  margin-right: 10px;
  font-size: 12px;
}
</style> 