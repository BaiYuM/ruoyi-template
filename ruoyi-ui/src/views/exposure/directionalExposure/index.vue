<script setup>
import { reactive, ref, onMounted } from 'vue'
import { getDirectionalList, saveDirectionalConfig, uploadDirectionalFileAsync, downloadDirectionalTemplate, triggerAutoExposure, getTodayCount } from '@/api/exposure'
import MyTable from '@/components/myTable/MyTable.vue'
import DirectionalConfigDrawer from './DirectionalConfigDrawer.vue'
import PageHeader from '@/components/PageHeader'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const filters = reactive({ platform: '', configType: '' })
const platformOptions = [
  { label: '全部', value: '' },
  { label: '抖音', value: 'douyin' },
  { label: '快手', value: 'kuaishou' },
  { label: '微博', value: 'weibo' }
]

const pageConfig = reactive({ tableData: [] })
const pagination = reactive({ pageSize: 10, currentPage: 1, pageSizes: [10, 20, 50], total: 0 })
const loading = ref(false)

// drawer
const drawerVisible = ref(false)
const editingData = reactive({})
const isEditing = ref(false)
const bulkSaving = ref(false)
const triggeringIds = ref(new Set())

const columns = [
  { prop: 'name', label: '配置名称' },
  { prop: 'account', label: '平台账号' },
  { prop: 'platform', label: '平台', formatter: (row, column, value) => {
      // map stored value (e.g. 'douyin') to human label (e.g. '抖音')
      const opt = platformOptions.find(p => p.value === value)
      return opt ? opt.label : (value || '')
    } },
  { prop: 'configType', label: '配置类型' },
  { prop: 'dailyLimit', label: '每天次数限制' },
  { prop: 'startTime', label: '启动时间' },
  {
    prop: "status",
    label: "状态",
    formatter: (row, column, value) => {
      const v = typeof value === "string" ? Number(value) : value;
      return v === 0 ? "启用" : "禁用";
    },
  },
  { prop: 'operation', label: '操作' }
]

const fileInputRef = ref(null)

  async function downloadTemplate() {
    const staticCsv = '/templates/directional_template.csv'
    const staticXls = '/templates/directional_template.xls'
    // try client-side xlsx generation first (preferred), then static CSV, then xls
    const headers = ['name', 'account', 'platform', 'configType', 'dailyLimit', 'startTime', 'status']
    const sample = ['示例配置', 'example_account', 'douyin', '评论', '10', '08:00', '1']
    
    // Try to use xlsx library (CDN or installed)
    let XLSX = window.XLSX
    if (!XLSX) {
      try {
        // Try to load from CDN dynamically
        await new Promise((resolve, reject) => {
          if (document.querySelector('script[src*="xlsx"]')) {
            resolve()
            return
          }
          const script = document.createElement('script')
          script.src = 'https://cdn.jsdelivr.net/npm/xlsx@0.18.5/dist/xlsx.full.min.js'
          script.onload = resolve
          script.onerror = reject
          document.head.appendChild(script)
        })
        XLSX = window.XLSX
      } catch (e) {
        // CDN load failed, skip xlsx generation
      }
    }
    
    if (XLSX) {
      try {
        const aoa = [headers, sample]
        const ws = XLSX.utils.aoa_to_sheet(aoa)
        const wb = XLSX.utils.book_new()
        XLSX.utils.book_append_sheet(wb, ws, 'DirectionalTemplate')
        const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'array' })
        const blob = new Blob([wbout], { type: 'application/octet-stream' })
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = 'directional_template.xlsx'
        document.body.appendChild(a)
        a.click()
        a.remove()
        URL.revokeObjectURL(url)
        return
      } catch (e) {
        // xlsx generation failed — fall back to static assets
      }
    }
    // try GET static CSV first (most compatible), then xls
    try {
      const res = await fetch(staticCsv)
      if (res && res.ok) {
        const blob = await res.blob()
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = 'directional_template.csv'
        document.body.appendChild(a)
        a.click()
        a.remove()
        URL.revokeObjectURL(url)
        return
      }
    } catch (e) {
      // ignore and try xls
    }

    try {
      const res2 = await fetch(staticXls)
      if (res2 && res2.ok) {
        const blob = await res2.blob()
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = 'directional_template.xls'
        document.body.appendChild(a)
        a.click()
        a.remove()
        URL.revokeObjectURL(url)
        return
      }
    } catch (e) {
      // ignore
    }

    // fallback: generate CSV client-side
    const csv = [headers.join(','), sample.map(s => `"${String(s).replace(/"/g, '""')}"`).join(',')].join('\n')
    const blob = new Blob(["\uFEFF" + csv], { type: 'text/csv;charset=utf-8;' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'directional_template.csv'
    document.body.appendChild(a)
    a.click()
    a.remove()
    URL.revokeObjectURL(url)

  }

function openImport() {
  if (fileInputRef.value) fileInputRef.value.click()
}

function handleFileChange(e) {
  const file = e.target.files && e.target.files[0]
  if (!file) return
  const formData = new FormData()
  formData.append('file', file)
  // use async upload to let backend parse & return task id
  uploadDirectionalFileAsync(formData)
    .then(res => {
      ElMessage.success(res?.message || '导入已提交')
      // if taskId returned, show info
      if (res && res.taskId) ElMessage.info('任务ID: ' + res.taskId)
      // refresh list
      fetchList({ page: pagination.currentPage, pageSize: pagination.pageSize })
    })
    .catch(() => ElMessage.error('导入失败'))
  // clear input value to allow re-upload same file
  e.target.value = ''
}

function fetchList(params) {
  loading.value = true
  const page = params?.page ?? pagination.currentPage
  const size = params?.pageSize ?? pagination.pageSize
  const payload = {
    pageNum: page,
    pageSize: size,
    platform: filters.platform,
    configType: filters.configType
  }
  getDirectionalList(payload).then(res => {
    loading.value = false
    pageConfig.tableData = res.rows || []
    pagination.total = res.total || (res.rows ? res.rows.length : 0)
    pagination.currentPage = page
  }).catch(() => { loading.value = false })
}

function resetSearch() {
  filters.platform = ''
  filters.configType = ''
  pagination.currentPage = 1
  fetchList({ page: 1, pageSize: pagination.pageSize })
}

function openCreate() {
  isEditing.value = false
  Object.keys(editingData).forEach(k => delete editingData[k])
  drawerVisible.value = true
}

function handleEdit(row) {
  isEditing.value = true
  Object.keys(editingData).forEach(k => delete editingData[k])
  Object.assign(editingData, row)
  drawerVisible.value = true
}

function onConfigSave(data) {
  if (bulkSaving.value) {
    ElMessage.warning('数据正在处理，请勿重复提交')
    return
  }
  // 如果 targetAccounts 包含多行（用户通过换行输入多个目标账号），则为每个账号单独创建一条配置
  try {
    const t = data.targetAccounts || ''
    const lines = Array.isArray(t) ? t : String(t).split(/\r?\n/).map(s => s.trim()).filter(Boolean)
    if (lines.length > 1) {
      // 已迁移到后端表的 target_accounts 字段：将多账号作为单条配置保存到 target_accounts
      bulkSaving.value = true
      const payload = { ...data }
      payload.targetAccounts = lines.join('\n')
      // 保持 account 字段为空以避免歧义（单条记录里使用 targetAccounts 存储多个目标）
      payload.account = null
      saveDirectionalConfig(payload).then(() => {
        bulkSaving.value = false
        ElMessage.success('提交成功（多账号已保存）')
        fetchList({ page: pagination.currentPage, pageSize: pagination.pageSize })
      }).catch(() => {
        bulkSaving.value = false
        ElMessage.error('提交失败，请重试')
      })
      return
    }
  } catch (e) {
    // 忽略解析错误，继续按单条保存
  }

  bulkSaving.value = true
  saveDirectionalConfig(data).then(() => {
    ElMessage.success('提交成功')
    fetchList({ page: pagination.currentPage, pageSize: pagination.pageSize })
  }).catch(() => ElMessage.error('保存失败')).finally(() => { bulkSaving.value = false })
}

// 重试失败账号的保存
function retryFailedAccounts(failedAccounts) {
  if (!failedAccounts || !failedAccounts.length) return
  if (bulkSaving.value) {
    ElMessage.warning('当前有保存任务进行中，请稍后重试')
    return
  }
  bulkSaving.value = true
  const tasks = failedAccounts.map(acc => {
    // 构建 payload：使用 editingData 作基础并覆盖 account
    const payload = Object.assign({}, editingData)
    payload.account = acc
    delete payload.targetAccounts
    return saveDirectionalConfig(payload)
  })
  Promise.allSettled(tasks).then(results => {
    bulkSaving.value = false
    const rejected = results.filter(r => r.status === 'rejected')
    if (rejected.length === 0) {
      ElMessage.success('重试成功')
    } else if (rejected.length === results.length) {
      ElMessage.error('重试全部失败，请稍后人工检查')
    } else {
      ElMessage.error(`重试部分失败：${rejected.length} / ${results.length}`)
    }
    fetchList({ page: pagination.currentPage, pageSize: pagination.pageSize })
  }).catch(() => {
    bulkSaving.value = false
    ElMessage.error('重试发生错误，请稍后重试')
  })
}

function manualTriggerDirectional(row) {
  if (!row || !row.id) return
  const id = row.id
  if (triggeringIds.value.has(id)) {
    ElMessage.warning('该配置正在触发中，请勿重复操作')
    return
  }
  ElMessageBox.confirm('确定立即触发一次曝光？', '提示', { type: 'warning' })
    .then(() => {
      triggeringIds.value.add(id)
      triggerAutoExposure(id).then((res) => {
        if (res && res.success) {
          ElMessage.success('触发成功')
          fetchList({ page: pagination.currentPage, pageSize: pagination.pageSize })
        } else {
          ElMessage.error('触发失败: ' + (res && res.message ? res.message : '未知错误'))
        }
      }).catch(() => {
        ElMessage.error('触发失败')
      }).finally(() => {
        triggeringIds.value.delete(id)
      })
    }).catch(() => {})
}

function showTodayCountDirectional(row) {
  if (!row || !row.id) return
  getTodayCount(row.id).then((res) => {
    const cnt = (res && res.todayCount) || 0
    ElMessage.info(`今日曝光：${cnt}`)
  }).catch(() => {
    ElMessage.error('获取失败')
  })
}

onMounted(() => fetchList({ page: 1, pageSize: pagination.pageSize }))
</script>

<template>
  <div class="page-wrapper">
    <PageHeader />

    <el-card shadow="never" class="search-card mb-4">
      <div class="search-row">
        <div class="search-grid compact">
          <div class="search-cell platform-cell">
            <label class="cell-label">平台账号</label>
            <el-select v-model="filters.platform" placeholder="请选择平台" clearable class="select-control">
              <el-option v-for="item in platformOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </div>
          <div class="search-cell keyword-cell">
            <label class="cell-label">配置类型</label>
            <el-select v-model="filters.configType" placeholder="请选择配置类型" clearable>
              <el-option label="全部" value="" />
              <el-option label="评论" value="评论" />
              <el-option label="私信" value="私信" />
            </el-select>
          </div>

          <div class="search-actions compact-actions">
            <el-button type="primary" @click="fetchList">查询</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="table-card">
      <div class="table-toolbar flex items-center mb-4">
        <div class="toolbar-left">
          <el-button type="primary" :icon="Plus" @click="openCreate">添加配置</el-button>
          <el-button class="ml-2" @click="downloadTemplate">下载模板</el-button>
          <el-button class="ml-2" @click="openImport">导入模板</el-button>
          <input ref="fileInputRef" type="file" style="display:none" accept=".csv,.xlsx" @change="handleFileChange" />
        </div>
      </div>
      <div class="table-body">
        <MyTable
          v-model="pageConfig.tableData"
          :loading="loading"
          :columns="columns"
          :pagination="pagination"
          :fetch-data="fetchList"
          :handle-edit="handleEdit"
          :rowClickable="false"
        >
          <template #customOperation="{ row }">
              <div class="flex flex-nowrap">
                <el-button type="primary" size="small" @click.stop="handleEdit(row)">编辑</el-button>
                <el-button size="small" class="ml-2" @click.stop="manualTriggerDirectional(row)" :loading="triggeringIds.has(row.id)">立即触发</el-button>
                <el-button size="small" class="ml-2" @click.stop="showTodayCountDirectional(row)">今日计数</el-button>
              </div>
          </template>
        </MyTable>
      </div>
    </el-card>

    <DirectionalConfigDrawer v-model:visible="drawerVisible" :config="editingData" :platform-options="platformOptions" :is-editing="isEditing" :saving="bulkSaving" @save="onConfigSave" />
  </div>
</template>

<style scoped>
@import '@/assets/styles/page-common.css';
</style>
