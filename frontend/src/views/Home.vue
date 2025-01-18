<template>
  <div class="home">
    <!-- 欢迎语 -->
    <div class="welcome-section" v-if="settingsStore.showWelcome">
      <h2>欢迎回来, {{ username }}</h2>
      <p class="system-desc">{{ settingsStore.systemDesc }}</p>
    </div>

    <!-- 公告通知面板 -->
    <div class="notice-section">
      <el-card class="notice-card">
        <template #header>
          <div class="notice-header">
            <span>最新公告</span>
            <el-button link type="primary" @click="router.push('/dashboard/notice/list')">
              查看更多
            </el-button>
          </div>
        </template>
        <div class="notice-list" v-loading="noticeLoading">
          <div v-if="notices.length === 0" class="no-data">
            暂无公告
          </div>
          <div v-else v-for="notice in notices" :key="notice.id" class="notice-item">
            <div class="notice-title" @click="showNoticeDetail(notice)">
              {{ notice.title }}
            </div>
            <div class="notice-info">
              <span class="publisher">{{ notice.publisherName }}</span>
              <span class="time">{{ formatTime(notice.createTime) }}</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 通知详情对话框 -->
    <el-dialog
      v-model="noticeDetailVisible"
      title="通知详情"
      width="600px"
      destroy-on-close
    >
      <div class="notice-detail" v-if="currentNotice">
        <h3>{{ currentNotice.title }}</h3>
        <div class="notice-meta">
          <span>发布人：{{ currentNotice.publisherName }}</span>
          <span>发布时间：{{ formatTime(currentNotice.createTime) }}</span>
        </div>
        <div class="notice-content">{{ currentNotice.content }}</div>
        <div class="notice-attachments" v-if="currentNotice.attachments">
          <h4>附件：</h4>
          <div v-for="file in JSON.parse(currentNotice.attachments)" :key="file.fileId" class="file-item">
            <span>{{ file.name }}</span>
            <el-button link type="primary" @click="handleDownload(file)">下载</el-button>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 核心统计数据 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="gauge-card">
          <div class="gauge-title">待处理采购订单</div>
          <div ref="purchaseGauge" class="gauge-chart"></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="gauge-card">
          <div class="gauge-title">待处理销售订单</div>
          <div ref="saleGauge" class="gauge-chart"></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="gauge-card">
          <div class="gauge-title">待处理退货</div>
          <div ref="returnGauge" class="gauge-chart"></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="gauge-card">
          <div class="gauge-title">库存预警商品</div>
          <div ref="stockGauge" class="gauge-chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 模块卡片 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 基础数据 -->
      <el-col :span="8">
        <el-card class="module-card" shadow="hover">
          <template #header>
            <div class="module-header">
              <el-icon><box /></el-icon>
              <span>基础数据</span>
            </div>
          </template>
          <div class="module-content">
            <el-row :gutter="10">
              <el-col :span="8">
                <div class="quick-entry" @click="router.push('/dashboard/basic/supplier')">
                  <el-icon><user /></el-icon>
                  <span>供应商管理</span>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="quick-entry" @click="router.push('/dashboard/basic/customer')">
                  <el-icon><user /></el-icon>
                  <span>客户管理</span>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="quick-entry" @click="router.push('/dashboard/basic/handler')">
                  <el-icon><user /></el-icon>
                  <span>经办人管理</span>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>

      <!-- 商品管理 -->
      <el-col :span="8">
        <el-card class="module-card" shadow="hover">
          <template #header>
            <div class="module-header">
              <el-icon><goods /></el-icon>
              <span>商品管理</span>
            </div>
          </template>
          <div class="module-content">
            <el-row :gutter="10">
              <el-col :span="8">
                <div class="quick-entry" @click="router.push('/dashboard/product/info')">
                  <el-icon><Document /></el-icon>
                  <span>商品信息</span>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="quick-entry" @click="router.push('/dashboard/product/category')">
                  <el-icon><folder /></el-icon>
                  <span>分类管理</span>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="quick-entry" @click="router.push('/dashboard/product/pricing')">
                  <el-icon><money /></el-icon>
                  <span>定价管理</span>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>

      <!-- 采购管理 -->
      <el-col :span="8">
        <el-card class="module-card" shadow="hover">
          <template #header>
            <div class="module-header">
              <el-icon><shopping-cart /></el-icon>
              <span>采购管理</span>
            </div>
          </template>
          <div class="module-content">
            <el-row :gutter="10">
              <el-col :span="8">
                <div class="quick-entry" @click="router.push('/dashboard/purchase/requisition')">
                  <el-icon><Document /></el-icon>
                  <span>采购申请</span>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="quick-entry" @click="router.push('/dashboard/purchase/order')">
                  <el-icon><Document /></el-icon>
                  <span>采购订单</span>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="quick-entry" @click="router.push('/dashboard/purchase/inbound')">
                  <el-icon><box /></el-icon>
                  <span>采购入库</span>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 销售管理 -->
      <el-col :span="8">
        <el-card class="module-card" shadow="hover">
          <template #header>
            <div class="module-header">
              <el-icon><sell /></el-icon>
              <span>销售管理</span>
            </div>
          </template>
          <div class="module-content">
            <el-row :gutter="10">
              <el-col :span="8">
                <div class="quick-entry" @click="router.push('/dashboard/sale/order')">
                  <el-icon><Document /></el-icon>
                  <span>销售订单</span>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="quick-entry" @click="router.push('/dashboard/sale/return')">
                  <el-icon><refresh-right /></el-icon>
                  <span>退货管理</span>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="quick-entry" @click="router.push('/dashboard/sale/variance')">
                  <el-icon><warning /></el-icon>
                  <span>销售差异</span>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>

      <!-- 仓库管理 -->
      <el-col :span="8">
        <el-card class="module-card" shadow="hover">
          <template #header>
            <div class="module-header">
              <el-icon><house /></el-icon>
              <span>仓库管理</span>
            </div>
          </template>
          <div class="module-content">
            <el-row :gutter="10">
              <el-col :span="12">
                <div class="quick-entry" @click="router.push('/dashboard/warehouse/info')">
                  <el-icon><Document /></el-icon>
                  <span>仓库信息</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="quick-entry" @click="router.push('/dashboard/warehouse/stock')">
                  <el-icon><box /></el-icon>
                  <span>库存管理</span>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>

      <!-- 系统管理 -->
      <el-col :span="8">
        <el-card class="module-card" shadow="hover">
          <template #header>
            <div class="module-header">
              <el-icon><setting /></el-icon>
              <span>系统管理</span>
            </div>
          </template>
          <div class="module-content">
            <el-row :gutter="10">
              <el-col :span="8">
                <div class="quick-entry" @click="router.push('/dashboard/users')">
                  <el-icon><user /></el-icon>
                  <span>用户管理</span>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="quick-entry" @click="router.push('/dashboard/operation-log')">
                  <el-icon><Document /></el-icon>
                  <span>操作日志</span>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="quick-entry" @click="router.push('/dashboard/settings')">
                  <el-icon><setting /></el-icon>
                  <span>系统设置</span>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 趋势图表 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <div class="chart-title">近30天订单趋势</div>
          <div ref="trendChart" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div class="chart-title">销售额环比增长</div>
          <div ref="growthChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 更多图表 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <div class="chart-title">商品销量占比</div>
          <div ref="productChart" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div class="chart-title">业务金额对比</div>
          <div ref="amountChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card shadow="hover">
          <div class="chart-title">库存预警商品TOP5</div>
          <div ref="warningChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import axios from 'axios'
import { useSettingsStore } from '../stores/settings'
import { getNoticeList } from '@/api/notice'
import { downloadFile } from '@/api/upload'
import { ElMessage } from 'element-plus'
import {
  ShoppingCart,
  Sell,
  RefreshRight,
  Warning,
  Box,
  User,
  Document,
  Folder,
  Money,
  House,
  Setting
} from '@element-plus/icons-vue'

const router = useRouter()
const username = ref(localStorage.getItem('username') || '用户')
const settingsStore = useSettingsStore()

// 公告相关数据
const notices = ref([])
const noticeLoading = ref(false)
const noticeDetailVisible = ref(false)
const currentNotice = ref(null)

// 获取最新公告
const fetchNotices = async () => {
  noticeLoading.value = true
  try {
    const { records } = await getNoticeList({
      page: 1,
      size: 5,
      status: 'published'
    })
    notices.value = records
  } catch (error) {
    console.error('获取公告列表失败:', error)
  } finally {
    noticeLoading.value = false
  }
}

// 显示通知详情
const showNoticeDetail = (notice) => {
  currentNotice.value = notice
  noticeDetailVisible.value = true
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString()
}

// 下载附件
const handleDownload = async (file) => {
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

const stats = ref({
  pendingPurchaseOrders: 0,
  pendingSaleOrders: 0,
  pendingReturns: 0,
  stockWarnings: 0
})

const trendChart = ref(null)
const purchaseGauge = ref(null)
const saleGauge = ref(null)
const returnGauge = ref(null)
const stockGauge = ref(null)
const growthChart = ref(null)
const productChart = ref(null)
const amountChart = ref(null)
const warningChart = ref(null)

// 初始化仪表盘
const initGauges = () => {
  if (!purchaseGauge.value || !saleGauge.value || !returnGauge.value || !stockGauge.value) {
    return {
      purchaseChart: null,
      saleChart: null,
      returnChart: null,
      stockChart: null,
      getGaugeOption: () => ({})
    }
  }

  const purchaseChart = echarts.init(purchaseGauge.value)
  const saleChart = echarts.init(saleGauge.value)
  const returnChart = echarts.init(returnGauge.value)
  const stockChart = echarts.init(stockGauge.value)
  
  // 设置仪表盘基础配置
  const getGaugeOption = (value, max, color) => ({
    series: [{
      type: 'gauge',
      startAngle: 180,
      endAngle: 0,
      min: 0,
      max: max,
      radius: '95%',
      splitNumber: 8,
      axisLine: {
        lineStyle: {
          width: 10,
          color: [
            [0.3, '#67e0e3'],
            [0.7, '#37a2da'],
            [1, color]
          ]
        }
      },
      pointer: {
        icon: 'path://M12.8,0.7l12,40.1H0.7L12.8,0.7z',
        length: '12%',
        width: 12,
        offsetCenter: [0, '-60%'],
        itemStyle: {
          color: 'auto'
        }
      },
      axisTick: {
        length: 8,
        lineStyle: {
          color: 'inherit',
          width: 2
        }
      },
      splitLine: {
        length: 12,
        lineStyle: {
          color: 'inherit',
          width: 3
        }
      },
      axisLabel: {
        color: '#464646',
        fontSize: 14,
        distance: -50,
        formatter: function (value) {
          return value.toFixed(value % 1 === 0 ? 0 : 1);
        }
      },
      title: {
        offsetCenter: [0, '-20%'],
        fontSize: 16
      },
      detail: {
        valueAnimation: true,
        fontSize: 36,
        offsetCenter: [0, '30%'],
        formatter: '{value}',
        color: 'inherit'
      },
      data: [{
        value: value,
        name: ''
      }]
    }]
  })

  return {
    purchaseChart,
    saleChart,
    returnChart,
    stockChart,
    getGaugeOption
  }
}

// 更新销售额环比增长图表
const updateGrowthChart = (chart, data) => {
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: function(params) {
        const param = params[0]
        return `${param.name}<br/>${param.seriesName}: ${param.value.toFixed(2)}%`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.dates,
      axisLabel: {
        rotate: 45,
        interval: 2
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}%'
      },
      splitLine: {
        lineStyle: {
          type: 'dashed'
        }
      }
    },
    series: [
      {
        name: '环比增长率',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        data: data.growthRates,
        lineStyle: {
          width: 3
        },
        itemStyle: {
          color: function(params) {
            return params.value >= 0 ? '#91cc75' : '#ee6666'
          },
          borderWidth: 2
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            {
              offset: 0,
              color: 'rgba(145, 204, 117, 0.3)'
            },
            {
              offset: 1,
              color: 'rgba(255, 255, 255, 0.2)'
            }
          ])
        },
        markLine: {
          symbol: ['none', 'none'],
          silent: true,
          data: [
            {
              yAxis: 0,
              lineStyle: {
                color: '#999',
                type: 'dashed'
              },
              label: {
                show: true,
                position: 'end',
                formatter: '0%'
              }
            }
          ]
        }
      }
    ]
  }
  chart.setOption(option)
}

// 更新商品销量占比图表
const updateProductChart = (chart, data) => {
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'middle',
      itemWidth: 10,
      itemHeight: 10,
      icon: 'circle',
      formatter: (name) => {
        const item = data.find(d => d.name === name)
        return `${name}: ${item.sales}件`
      }
    },
    series: [
      {
        name: '商品销量',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['40%', '50%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '14',
            fontWeight: 'bold',
            formatter: '{b}: {c}件\n{d}%'
          },
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        data: data.map(item => ({
          value: item.sales,
          name: item.name
        }))
      }
    ],
    color: [
      '#5470c6',
      '#91cc75',
      '#fac858',
      '#ee6666',
      '#73c0de',
      '#3ba272',
      '#fc8452',
      '#9a60b4'
    ]
  }
  chart.setOption(option)
}

// 更新业务金额对比图表
const updateAmountChart = (chart, data) => {
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: function(params) {
        let result = params[0].axisValue + '<br/>'
        params.forEach(param => {
          result += `${param.seriesName}: ${param.value.toLocaleString('zh-CN', { style: 'currency', currency: 'CNY' })}<br/>`
        })
        return result
      }
    },
    legend: {
      data: ['采购金额', '销售金额', '退货金额'],
      top: 'bottom'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      axisLabel: {
        formatter: function(value) {
          return (value / 10000).toLocaleString('zh-CN') + '万'
        }
      }
    },
    yAxis: {
      type: 'category',
      data: ['本月', '上月'],
      axisLabel: {
        margin: 20
      }
    },
    series: [
      {
        name: '采购金额',
        type: 'bar',
        stack: 'total',
        label: {
          show: true,
          formatter: function(params) {
            return (params.value / 10000).toLocaleString('zh-CN') + '万'
          }
        },
        emphasis: {
          focus: 'series'
        },
        data: [data.currentMonth.purchase, data.lastMonth.purchase],
        itemStyle: {
          color: '#5470c6'
        }
      },
      {
        name: '销售金额',
        type: 'bar',
        stack: 'total',
        label: {
          show: true,
          formatter: function(params) {
            return (params.value / 10000).toLocaleString('zh-CN') + '万'
          }
        },
        emphasis: {
          focus: 'series'
        },
        data: [data.currentMonth.sale, data.lastMonth.sale],
        itemStyle: {
          color: '#91cc75'
        }
      },
      {
        name: '退货金额',
        type: 'bar',
        stack: 'total',
        label: {
          show: true,
          formatter: function(params) {
            return (params.value / 10000).toLocaleString('zh-CN') + '万'
          }
        },
        emphasis: {
          focus: 'series'
        },
        data: [data.currentMonth.return, data.lastMonth.return],
        itemStyle: {
          color: '#ee6666'
        }
      }
    ]
  }
  chart.setOption(option)
}

// 更新库存预警图表
const updateWarningChart = (chart, data) => {
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: function(params) {
        const currentStock = params[0].value;
        const minStock = params[1].value;
        const warningLevel = minStock - currentStock;
        return `${params[0].name}<br/>
当前库存: ${currentStock}<br/>
最小库存: ${minStock}<br/>
库存差额: <span style="color: ${warningLevel > 0 ? '#ee6666' : '#91cc75'}">${warningLevel > 0 ? '-' : '+'}${Math.abs(warningLevel)}</span>`;
      }
    },
    legend: {
      data: ['当前库存', '最小库存'],
      bottom: '0%'
    },
    grid: {
      top: '3%',
      left: '3%',
      right: '4%',
      bottom: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      boundaryGap: [0, 0.01],
      splitLine: {
        lineStyle: {
          type: 'dashed'
        }
      }
    },
    yAxis: {
      type: 'category',
      data: data.map(item => item.name),
      axisLabel: {
        width: 120,
        overflow: 'break',
        interval: 0
      }
    },
    series: [
      {
        name: '当前库存',
        type: 'bar',
        data: data.map(item => ({
          value: item.stock,
          itemStyle: {
            color: (() => {
              const warningLevel = item.threshold - item.stock;
              if (warningLevel >= 10) return '#ee6666';
              if (warningLevel >= 5) return '#fac858';
              return '#91cc75';
            })()
          }
        })),
        label: {
          show: true,
          position: 'right',
          formatter: '{c}'
        }
      },
      {
        name: '最小库存',
        type: 'line',
        symbol: 'circle',
        symbolSize: 8,
        data: data.map(item => item.threshold),
        itemStyle: {
          color: '#409EFF'
        },
        label: {
          show: true,
          position: 'right',
          formatter: '{c}',
          offset: [10, 0]
        }
      }
    ]
  }
  chart.setOption(option)
}

// 获取数据
const fetchData = async () => {
  try {
    // 获取统计数据
    const statsRes = await axios.get('http://localhost:8080/api/dashboard/scm-stats')
    if (statsRes.data.code === 0) {
      stats.value = statsRes.data.data
      
      // 确保DOM元素已经渲染完成
      nextTick(() => {
        // 更新仪表盘数据
        if (purchaseGauge.value && saleGauge.value && returnGauge.value && stockGauge.value) {
          const { purchaseChart, saleChart, returnChart, stockChart, getGaugeOption } = initGauges()
          
          purchaseChart.setOption(getGaugeOption(stats.value.pendingPurchaseOrders, 10, '#fac858'))
          saleChart.setOption(getGaugeOption(stats.value.pendingSaleOrders, 10, '#91cc75'))
          returnChart.setOption(getGaugeOption(stats.value.pendingReturns, 20, '#ee6666'))
          stockChart.setOption(getGaugeOption(stats.value.stockWarnings, 10, '#fc8452'))
        }
      })
    }

    // 获取订单趋势数据
    const trendRes = await axios.get('http://localhost:8080/api/dashboard/trend')
    if (trendRes.data.code === 0) {
      nextTick(() => {
        if (trendChart.value) {
          const trendChartInstance = echarts.init(trendChart.value)
          updateTrendChart(trendChartInstance, trendRes.data.data)
        }
      })
    }

    // 获取销售额环比数据
    const growthRes = await axios.get('http://localhost:8080/api/dashboard/growth-trend')
    if (growthRes.data.code === 0) {
      nextTick(() => {
        if (growthChart.value) {
          const growthChartInstance = echarts.init(growthChart.value)
          updateGrowthChart(growthChartInstance, growthRes.data.data)
        }
      })
    }

    // 获取商品销量占比数据
    const productRes = await axios.get('http://localhost:8080/api/dashboard/product-sales')
    if (productRes.data.code === 0) {
      nextTick(() => {
        if (productChart.value) {
          const productChartInstance = echarts.init(productChart.value)
          updateProductChart(productChartInstance, productRes.data.data)
        }
      })
    }

    // 获取业务金额对比数据
    const amountRes = await axios.get('http://localhost:8080/api/dashboard/amount-compare')
    if (amountRes.data.code === 0) {
      nextTick(() => {
        if (amountChart.value) {
          const amountChartInstance = echarts.init(amountChart.value)
          updateAmountChart(amountChartInstance, amountRes.data.data)
          
          // 监听窗口大小变化
          window.addEventListener('resize', () => {
            amountChartInstance.resize()
          })
        }
      })
    }

    // 获取库存预警TOP5数据
    const warningRes = await axios.get('http://localhost:8080/api/dashboard/stock-warning-top5')
    if (warningRes.data.code === 0) {
      nextTick(() => {
        if (warningChart.value) {
          const warningChartInstance = echarts.init(warningChart.value)
          updateWarningChart(warningChartInstance, warningRes.data.data)
        }
      })
    }
  } catch (error) {
    console.error('获取数据失败:', error)
  }
}

// 更新订单趋势图表
const updateTrendChart = (chart, data) => {
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['采购订单', '销售订单', '退货订单']
    },
    xAxis: {
      type: 'category',
      data: data.dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '采购订单',
        type: 'line',
        data: data.purchaseOrders
      },
      {
        name: '销售订单',
        type: 'line',
        data: data.saleOrders
      },
      {
        name: '退货订单',
        type: 'line',
        data: data.returnOrders
      }
    ]
  }
  chart.setOption(option)
}

// 获取系统设置
const fetchSettings = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/settings')
    if (res.data.code === 0) {
      settingsStore.$patch({
        systemName: res.data.data.systemName,
        systemDesc: res.data.data.systemDesc,
        showWelcome: res.data.data.showWelcome
      })
    }
  } catch (error) {
    console.error('获取系统设置失败:', error)
  }
}

onMounted(async () => {
  await fetchSettings()  // 先获取系统设置
  await fetchNotices()  // 获取公告数据
  await nextTick()  // 等待DOM更新
  await fetchData()  // 获取其他数据

  // 监听窗口大小变化，重绘图表
  window.addEventListener('resize', () => {
    nextTick(() => {
      if (purchaseGauge.value && saleGauge.value && returnGauge.value && stockGauge.value) {
        const { purchaseChart, saleChart, returnChart, stockChart } = initGauges()
        purchaseChart && purchaseChart.resize()
        saleChart && saleChart.resize()
        returnChart && returnChart.resize()
        stockChart && stockChart.resize()
      }
      
      if (trendChart.value) {
        const trendChartInstance = echarts.getInstanceByDom(trendChart.value)
        trendChartInstance && trendChartInstance.resize()
      }
      if (growthChart.value) {
        const growthChartInstance = echarts.getInstanceByDom(growthChart.value)
        growthChartInstance && growthChartInstance.resize()
      }
      if (productChart.value) {
        const productChartInstance = echarts.getInstanceByDom(productChart.value)
        productChartInstance && productChartInstance.resize()
      }
      if (amountChart.value) {
        const amountChartInstance = echarts.getInstanceByDom(amountChart.value)
        amountChartInstance && amountChartInstance.resize()
      }
      if (warningChart.value) {
        const warningChartInstance = echarts.getInstanceByDom(warningChart.value)
        warningChartInstance && warningChartInstance.resize()
      }
    })
  })
})
</script>

<style scoped>
.home {
  padding: 20px;
}

.welcome-section {
  text-align: left;
  margin-bottom: 20px;
}

.welcome-section h2 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 10px;
}

.system-desc {
  color: #666;
  font-size: 14px;
}

.notice-section {
  margin-bottom: 30px;
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notice-list {
  min-height: 200px;
}

.notice-item {
  padding: 12px 0;
  border-bottom: 1px solid #EBEEF5;
  cursor: pointer;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 8px;
}

.notice-title:hover {
  color: #409EFF;
}

.notice-info {
  font-size: 12px;
  color: #909399;
}

.notice-info .publisher {
  margin-right: 15px;
}

.no-data {
  text-align: center;
  color: #909399;
  padding: 30px 0;
}

.notice-detail h3 {
  margin: 0 0 20px 0;
  text-align: center;
}

.notice-meta {
  display: flex;
  justify-content: space-between;
  color: #909399;
  font-size: 14px;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #EBEEF5;
}

.notice-content {
  line-height: 1.6;
  margin-bottom: 20px;
}

.notice-attachments h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
}

.file-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px dashed #EBEEF5;
}

.file-item:last-child {
  border-bottom: none;
}

.data-card {
  height: 100px;
}

.data-item {
  display: flex;
  align-items: center;
  height: 100%;
}

.data-item .icon {
  font-size: 40px;
  color: #409EFF;
  margin-right: 15px;
}

.data-content {
  flex: 1;
}

.data-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.data-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}

.module-card {
  margin-bottom: 20px;
}

.module-header {
  display: flex;
  align-items: center;
  font-size: 16px;
}

.module-header .el-icon {
  margin-right: 8px;
  font-size: 18px;
}

.module-content {
  padding: 10px 0;
}

.quick-entry {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px 0;
  cursor: pointer;
  transition: all 0.3s;
}

.quick-entry:hover {
  background-color: #f5f7fa;
  border-radius: 4px;
}

.quick-entry .el-icon {
  font-size: 24px;
  color: #409EFF;
  margin-bottom: 8px;
}

.quick-entry span {
  font-size: 14px;
  color: #606266;
}

.chart-title {
  font-size: 16px;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

.gauge-card {
  height: 300px;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 20px;
}

.gauge-title {
  text-align: center;
  font-size: 18px;
  color: #303133;
  margin: 20px 0;
  font-weight: 500;
}

.gauge-chart {
  flex: 1;
  height: 260px;
  padding: 0 20px;
}
</style> 