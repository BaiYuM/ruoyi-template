<template>
  <div class="data-overview page-wrapper">
    <el-card class="header-card" shadow="never">
      <div class="header-row">
        <div class="title">数据总览</div>
        <div class="controls">
          <el-date-picker
            v-model="range"
            type="daterange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            range-separator="→"
            unlink-panels
            :picker-options="pickerOptions"
            @change="onRangeChange"
          />
          <el-button size="small" @click="quickRange(0)">今天</el-button>
          <el-button size="small" @click="quickRange(1)">昨天</el-button>
          <el-button size="small" type="primary" @click="quickRange(7)">近7天</el-button>
          <el-button size="small" @click="quickRange(30)">近30天</el-button>
          <el-button size="small" icon="el-icon-refresh" @click="refresh">刷新</el-button>
        </div>
      </div>
    </el-card>

    <el-row :gutter="16">
      <el-col :span="18">
        <el-card class="stats-grid-card" shadow="never">
          <div class="stats-grid">
            <el-card v-for="(item, idx) in statsCards" :key="idx" class="stat-card" shadow="never">
              <div class="stat-title">{{ item.title }}</div>
              <div class="stat-value">{{ item.value }}</div>
            </el-card>
          </div>
        </el-card>

        <el-card class="chart-card" shadow="never" style="margin-top:16px">
          <div ref="chartRef" class="chart"></div>
        </el-card>

        <!-- 明细列表已移除，页面仅保留统计卡片与图表 -->
      </el-col>

      <el-col :span="6">
        <el-card class="robot-card" shadow="never">
          <div class="robot-title">机器人统计</div>
          <div class="robot-body">暂无机器人数据</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
if (typeof window !== 'undefined' && !window.echarts) {
  window.echarts = echarts
}
import { getOverview } from '@/api/dataOverview'

const range = ref([])
const chartRef = ref(null)
let chart = null

const statsCards = reactive([
  { title: '接待人数', value: 0 },
  { title: '消息处理数', value: 0 },
  { title: '转接人数', value: 0 },
  { title: '处理好友申请', value: 0 },
  { title: '曝光私信', value: 0 },
  { title: '发布评论', value: 0 },
  { title: '发布视频', value: 0 },
  { title: '添加好友', value: 0 }
])

const metricNames = ['接待人数', '消息处理数', '转接人数', '处理好友申请', '曝光私信', '发布评论', '发布视频', '添加好友']


const pickerOptions = {
  shortcuts: [
    { text: '近7天', onClick(picker) { const end = new Date(); const start = new Date(); start.setDate(end.getDate() - 6); picker.$emit('pick', [start, end]) } },
    { text: '近30天', onClick(picker) { const end = new Date(); const start = new Date(); start.setDate(end.getDate() - 29); picker.$emit('pick', [start, end]) } }
  ]
}

function formatDate(d) {
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

function quickRange(days) {
  const end = new Date()
  let start = new Date()
  if (days === 0) start = end
  else if (days === 1) start.setDate(end.getDate() - 1)
  else start.setDate(end.getDate() - (days - 1))
  range.value = [start, end]
}

async function loadData() {
  console.debug('dataOverview: loadData start')
  if (!range.value || range.value.length !== 2) {
    ElMessage.warning('请选择日期范围（默认近7天）')
    quickRange(7)
  }
  const params = {
    startDate: formatDate(range.value[0]),
    endDate: formatDate(range.value[1])
  }
  console.debug('dataOverview: loadData params', params)
  try {
    const res = await getOverview(params)
    console.debug('dataOverview: getOverview returned', res)
    // expect res to be { cards: [...], series: { dates: [], lines: [{name,data},...] } }
    if (res && res.cards) {
      for (let i = 0; i < statsCards.length; i++) statsCards[i].value = res.cards[i] ?? 0
    } else {
      // clear stats if none
      for (let i = 0; i < statsCards.length; i++) statsCards[i].value = 0
    }

    // prepare series: if backend didn't return series, build empty series for date range
    let series = null
    if (res && res.series && Array.isArray(res.series.lines) && Array.isArray(res.series.dates)) {
      // ensure we have exactly 8 lines in correct order — fill missing
      const dates = res.series.dates
      const linesMap = {}
      const normalize = (n) => (n || '').toString().replace(/[~～]/g, '').trim()
      (res.series.lines || []).forEach(l => { linesMap[normalize(l.name)] = l.data || [] })
      const lines = metricNames.map((mName, idx) => ({ name: mName + '~', data: (linesMap[normalize(mName)] && linesMap[normalize(mName)].slice(0, dates.length)) || new Array(dates.length).fill(0) }))
      series = { dates, lines }
    } else {
      // build dates between start and end
      const sd = new Date(params.startDate)
      const ed = new Date(params.endDate)
      const dates = []
      for (let d = new Date(sd); d <= ed; d.setDate(d.getDate() + 1)) {
        dates.push(formatDate(new Date(d)))
      }
      const lines = metricNames.map((m) => ({ name: m + '~', data: new Array(dates.length).fill(0) }))
      series = { dates, lines }
    }

    if (chart) {
      try {
        console.debug('dataOverview: series prepared', { dates: series.dates && series.dates.length, lines: series.lines && series.lines.length })
        const opt = buildChartOption(series)
        chart.clear()
        chart.setOption(opt)
        chart.resize()
        console.debug('dataOverview: chart updated, dates=', series.dates && series.dates.length)
      } catch (e) {
        console.error('dataOverview chart setOption error', e)
      }
    } else {
      console.warn('dataOverview: chart instance not initialized')
    }
    // chart data handled below; no table rows (user requested chart-only)
  } catch (e) {
    ElMessage.error('加载失败')
  }
}

function buildChartOption(series) {
  const dates = (series && series.dates) || []
  const lines = (series && series.lines) || []
  const colors = ['#ff6b9a', '#4fb6ff', '#ffa94d', '#9b5cf6', '#63e6be', '#ffd6a5', '#b197fc', '#ff8787']
  return {
    color: colors,
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross', label: { backgroundColor: '#6a7985' } }
    },
    legend: {
      data: lines.map(l => l.name),
      top: 8,
      left: 'center'
    },
    grid: { left: 20, right: 20, top: 50, bottom: 40 },
    xAxis: {
      type: 'category',
      data: dates,
      boundaryGap: false,
      axisLine: { lineStyle: { color: '#eee' } },
      axisLabel: { rotate: 0 }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f3f3f3' } }
    },
    series: lines.map((l, idx) => ({
      name: l.name,
      type: 'line',
      smooth: true,
      showSymbol: false,
      symbol: 'circle',
      symbolSize: 6,
      areaStyle: { opacity: 0.06 },
      lineStyle: { width: 2 },
      data: l.data,
      emphasis: { focus: 'series' }
    }))
  }
}

function refresh() { loadData() }

function onRangeChange() { loadData() }


onMounted(() => {
  // default near 7 days
  quickRange(7)
  nextTick(() => {
    try {
      console.debug('dataOverview: chartRef=', chartRef.value)
      console.debug('dataOverview: echarts object=', typeof echarts !== 'undefined')
      if (chartRef.value) {
        // ensure container has height in case CSS collapsed it
        try { chartRef.value.style.height = chartRef.value.style.height || '360px' } catch (e) {}
        chart = echarts.init(chartRef.value)
        console.debug('dataOverview: echarts version=', echarts?.version || 'unknown')
      } else {
        console.warn('dataOverview: chartRef not available at onMounted')
      }
    } catch (e) {
      console.error('echarts init error', e)
    }
    loadData()
  })
  const onResize = () => { chart && chart.resize() }
  window.addEventListener('resize', onResize)
  onUnmounted(() => {
    try { window.removeEventListener('resize', onResize) } catch (e) {}
  })
})
// no table rows — chart only
</script>

<style scoped>
.data-overview .header-row { display:flex; justify-content:space-between; align-items:center }
.data-overview .controls { display:flex; gap:8px; align-items:center }
.stats-grid { display:grid; grid-template-columns:repeat(4,1fr); gap:16px }
.stat-card { text-align:center; padding:18px }
.stat-title { color:#666 }
.stat-value { font-size:28px; color:#ff3b7a; margin-top:8px }
.chart-card { padding:0 }
.chart { width:100%; height:360px }
.robot-card, .robot-panel { padding:18px }
.robot-title { font-weight:600; margin-bottom:8px }
.list-card .list-title { font-weight:600; margin-bottom:8px }
.stats-grid-card { padding:12px }
.list-card { padding:12px }
</style>
