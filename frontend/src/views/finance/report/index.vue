<template>
  <div class="finance-report">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>财务报表</span>
          <div class="header-right">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :shortcuts="dateShortcuts"
              @change="handleDateChange"
            />
            <el-button type="primary" @click="handleExport">导出报表</el-button>
          </div>
        </div>
      </template>

      <!-- 统计卡片 -->
      <el-row :gutter="20" class="statistics-cards">
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>总收入</span>
                <el-tag type="success">{{ dateRangeText }}</el-tag>
              </div>
            </template>
            <div class="card-amount">
              <span class="amount">¥{{ totalIncome }}</span>
              <span class="trend" :class="{ up: incomeTrend > 0, down: incomeTrend < 0 }">
                {{ Math.abs(incomeTrend) }}%
                <el-icon><ArrowUp v-if="incomeTrend > 0" /><ArrowDown v-if="incomeTrend < 0" /></el-icon>
              </span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>总支出</span>
                <el-tag type="danger">{{ dateRangeText }}</el-tag>
              </div>
            </template>
            <div class="card-amount">
              <span class="amount">¥{{ totalExpense }}</span>
              <span class="trend" :class="{ up: expenseTrend > 0, down: expenseTrend < 0 }">
                {{ Math.abs(expenseTrend) }}%
                <el-icon><ArrowUp v-if="expenseTrend > 0" /><ArrowDown v-if="expenseTrend < 0" /></el-icon>
              </span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>净收入</span>
                <el-tag type="info">{{ dateRangeText }}</el-tag>
              </div>
            </template>
            <div class="card-amount">
              <span class="amount" :class="{ 'negative': netIncome < 0 }">¥{{ netIncome }}</span>
              <span class="trend" :class="{ up: netIncomeTrend > 0, down: netIncomeTrend < 0 }">
                {{ Math.abs(netIncomeTrend) }}%
                <el-icon><ArrowUp v-if="netIncomeTrend > 0" /><ArrowDown v-if="netIncomeTrend < 0" /></el-icon>
              </span>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 图表区域 -->
      <div class="charts-container">
        <!-- 收支趋势图 -->
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>收支趋势</span>
              <el-radio-group v-model="trendType" size="small">
                <el-radio-button label="day">日</el-radio-button>
                <el-radio-button label="week">周</el-radio-button>
                <el-radio-button label="month">月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="trendChartRef" class="chart"></div>
        </el-card>

        <!-- 收入构成 -->
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>收入构成</span>
            </div>
          </template>
          <div ref="incomeChartRef" class="chart"></div>
        </el-card>

        <!-- 支出构成 -->
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>支出构成</span>
            </div>
          </template>
          <div ref="expenseChartRef" class="chart"></div>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

// 日期范围快捷选项
const dateShortcuts = [
  {
    text: '最近一周',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start, end]
    }
  },
  {
    text: '最近一个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setMonth(start.getMonth() - 1)
      return [start, end]
    }
  },
  {
    text: '最近三个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setMonth(start.getMonth() - 3)
      return [start, end]
    }
  }
]

// 数据
const dateRange = ref([])
const trendType = ref('day')
const loading = ref(false)
const totalIncome = ref(0)
const totalExpense = ref(0)
const incomeTrend = ref(0)
const expenseTrend = ref(0)

// 图表引用
const trendChartRef = ref(null)
const incomeChartRef = ref(null)
const expenseChartRef = ref(null)
let trendChart = null
let incomeChart = null
let expenseChart = null

// 添加定时器
let refreshTimer = null

// 计算属性
const netIncome = computed(() => totalIncome.value - totalExpense.value)
const netIncomeTrend = computed(() => {
  if (totalIncome.value === 0 && totalExpense.value === 0) return 0
  return ((totalIncome.value - totalExpense.value) / Math.max(totalIncome.value, totalExpense.value)) * 100
})

const dateRangeText = computed(() => {
  if (!dateRange.value || !dateRange.value[0] || !dateRange.value[1]) return ''
  const start = dateRange.value[0].toLocaleDateString()
  const end = dateRange.value[1].toLocaleDateString()
  return `${start} 至 ${end}`
})

// 获取财务数据
const getFinanceData = async () => {
  if (!dateRange.value || !dateRange.value[0] || !dateRange.value[1]) return
  
  loading.value = true
  try {
    const params = {
      startDate: dateRange.value[0].toISOString().split('T')[0],
      endDate: dateRange.value[1].toISOString().split('T')[0],
      type: trendType.value
    }
    
    const response = await fetch(`http://127.0.0.1:8080/api/finance/report?${new URLSearchParams(params)}`, {
      headers: {
        'Accept': 'application/json'
      },
      credentials: 'include'
    })
    
    const result = await response.json()
    if (result.code === 0) {
      const data = result.data
      totalIncome.value = data.totalIncome
      totalExpense.value = data.totalExpense
      incomeTrend.value = data.incomeTrend
      expenseTrend.value = data.expenseTrend
      
      // 更新图表
      updateTrendChart(data.trend)
      updateIncomeChart(data.incomeComposition)
      updateExpenseChart(data.expenseComposition)
    } else {
      ElMessage.error(result.message || '获取财务数据失败')
    }
  } catch (error) {
    console.error('获取财务数据失败:', error)
    ElMessage.error('获取财务数据失败')
  } finally {
    loading.value = false
  }
}

// 初始化趋势图
const initTrendChart = () => {
  trendChart = echarts.init(trendChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      }
    },
    legend: {
      data: ['收入', '支出', '净收入']
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
      data: []
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '收入',
        type: 'line',
        stack: 'Total',
        areaStyle: {},
        emphasis: {
          focus: 'series'
        },
        data: []
      },
      {
        name: '支出',
        type: 'line',
        stack: 'Total',
        areaStyle: {},
        emphasis: {
          focus: 'series'
        },
        data: []
      },
      {
        name: '净收入',
        type: 'line',
        emphasis: {
          focus: 'series'
        },
        data: []
      }
    ]
  }
  trendChart.setOption(option)
}

// 初始化收入构成图
const initIncomeChart = () => {
  incomeChart = echarts.init(incomeChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 10,
      data: ['销售收入', '其他收入']
    },
    series: [
      {
        name: '收入构成',
        type: 'pie',
        radius: ['50%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '20',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: []
      }
    ]
  }
  incomeChart.setOption(option)
}

// 初始化支出构成图
const initExpenseChart = () => {
  expenseChart = echarts.init(expenseChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 10,
      data: ['采购支出', '其他支出']
    },
    series: [
      {
        name: '支出构成',
        type: 'pie',
        radius: ['50%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '20',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: []
      }
    ]
  }
  expenseChart.setOption(option)
}

// 更新趋势图数据
const updateTrendChart = (data) => {
  const option = {
    xAxis: {
      data: data.dates
    },
    series: [
      {
        name: '收入',
        data: data.incomes
      },
      {
        name: '支出',
        data: data.expenses
      },
      {
        name: '净收入',
        data: data.netIncomes
      }
    ]
  }
  trendChart.setOption(option)
}

// 更新收入构成图数据
const updateIncomeChart = (data) => {
  const option = {
    series: [
      {
        data: [
          { value: data.sale, name: '销售收入' },
          { value: data.other, name: '其他收入' }
        ]
      }
    ]
  }
  incomeChart.setOption(option)
}

// 更新支出构成图数据
const updateExpenseChart = (data) => {
  const option = {
    series: [
      {
        data: [
          { value: data.purchase, name: '采购支出' },
          { value: data.other, name: '其他支出' }
        ]
      }
    ]
  }
  expenseChart.setOption(option)
}

// 处理日期变化
const handleDateChange = () => {
  getFinanceData()
}

// 导出报表
const handleExport = async () => {
  if (!dateRange.value || !dateRange.value[0] || !dateRange.value[1]) {
    ElMessage.warning('请先选择日期范围')
    return
  }
  
  try {
    const params = {
      startDate: dateRange.value[0].toISOString().split('T')[0],
      endDate: dateRange.value[1].toISOString().split('T')[0]
    }
    
    const response = await fetch(`http://127.0.0.1:8080/api/finance/report/export?${new URLSearchParams(params)}`, {
      headers: {
        'Accept': 'application/octet-stream'
      },
      credentials: 'include'
    })
    
    if (response.ok) {
      const blob = await response.blob()
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `财务报表_${params.startDate}_${params.endDate}.xlsx`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    } else {
      const error = await response.json()
      ElMessage.error(error.message || '导出报表失败')
    }
  } catch (error) {
    console.error('导出报表失败:', error)
    ElMessage.error('导出报表失败')
  }
}

// 监听窗口大小变化
const handleResize = () => {
  trendChart?.resize()
  incomeChart?.resize()
  expenseChart?.resize()
}

// 初始化
onMounted(() => {
  // 设置默认日期范围为最近30天
  const end = new Date()
  const start = new Date()
  start.setDate(start.getDate() - 30)
  dateRange.value = [start, end]
  
  // 初始化图表
  initTrendChart()
  initIncomeChart()
  initExpenseChart()
  
  // 初始加载数据
  getFinanceData()
  
  // 启动定时刷新
  refreshTimer = setInterval(() => {
    getFinanceData()
  }, 30000) // 每30秒刷新一次
  
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
})

// 组件卸载时清除定时器
onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  incomeChart?.dispose()
  expenseChart?.dispose()
})
</script>

<style scoped>
.finance-report {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-right {
  display: flex;
  gap: 16px;
  align-items: center;
}

.statistics-cards {
  margin-bottom: 20px;
}

.card-amount {
  display: flex;
  align-items: baseline;
  gap: 16px;
}

.amount {
  font-size: 24px;
  font-weight: bold;
}

.amount.negative {
  color: #f56c6c;
}

.trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
}

.trend.up {
  color: #67c23a;
}

.trend.down {
  color: #f56c6c;
}

.charts-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-top: 20px;
}

.chart-card:first-child {
  grid-column: 1 / -1;
}

.chart {
  height: 300px;
}
</style> 