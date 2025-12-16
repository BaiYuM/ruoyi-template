<template>
  <div class="page-wrapper">
    <PageHeader>
      <template #actions>
      </template>
    </PageHeader>

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
            <label class="cell-label">配置名称/账号</label>
            <el-input v-model="filters.keyword" placeholder="请输入配置名称或账号，支持模糊搜索" clearable />
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
           <div style="margin-right: 8px;">曝光统计</div>
          <el-button type="primary" :icon="Plus" @click="openCreate">添加配置</el-button>
        </div>
        <div class="toolbar-right"></div>
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
                <el-button size="small" class="ml-2" @click.stop="manualTriggerLink(row)" :loading="triggeringIds.has && triggeringIds.has(row.id)">立即触发</el-button>
                <el-button size="small" class="ml-2" @click.stop="showTodayCountLink(row)">今日计数</el-button>
              </div>
            </template>
        </MyTable>
      </div>
    </el-card>

    <LinkConfigDrawer v-model:visible="drawerVisible" :config="editingData" :platform-options="platformOptions" :is-editing="isEditing" :saving="bulkSaving" @save="onConfigSave" />
  </div>
</template>
<script setup>
import { reactive, ref, onMounted } from 'vue'
import { getDirectionalList, saveDirectionalConfig, triggerAutoExposure, getTodayCount } from '@/api/exposure'
import MyTable from '@/components/myTable/MyTable.vue'
import LinkConfigDrawer from './LinkConfigDrawer.vue'
import PageHeader from '@/components/PageHeader'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const filters = reactive({ platform: '', keyword: '' })
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
      const opt = platformOptions.find(p => p.value === value)
      return opt ? opt.label : (value || '')
    } },
  { prop: 'lastStopReason', label: '上次停止原因' },
  { prop: 'commentKeywords', label: '评论匹配关键词' },
  { prop: 'commentTime', label: '评论时间匹配' },
  { prop: 'commentRegion', label: '评论地区匹配' },
  { prop: 'status', label: '状态', formatter: (row, column, value) => {
      // 后端可能返回 0/1 表示显示/隐藏，也可能直接返回启用/禁用文字
      if (value === 0 || value === '0') return '可见'
      if (value === 1 || value === '1') return '隐藏'
      return value ?? ''
    } },
  { prop: 'operation', label: '操作' }
]

function fetchList(params) {
  loading.value = true
  const page = params?.page ?? pagination.currentPage
  const size = params?.pageSize ?? pagination.pageSize
  const payload = {
    pageNum: page,
    pageSize: size,
    platform: filters.platform,
    keyword: filters.keyword
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
  filters.keyword = ''
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
  bulkSaving.value = true
  saveDirectionalConfig(data).then(() => {
    bulkSaving.value = false
    ElMessage.success('提交成功')
    fetchList({ page: pagination.currentPage, pageSize: pagination.pageSize })
  }).catch(() => {
    bulkSaving.value = false
    ElMessage.error('提交失败')
  })
}

function manualTriggerLink(row) {
  if (!row || !row.id) return
  const id = row.id
  if (triggeringIds.value.has(id)) {
    ElMessage.warning('该配置正在触发中，请勿重复操作')
    return
  }
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
  }).finally(() => triggeringIds.value.delete(id))
}

function showTodayCountLink(row) {
  if (!row || !row.id) return
  getTodayCount(row.id).then((res) => {
    const cnt = (res && res.todayCount) || 0
    ElMessage.info(`今日曝光：${cnt}`)
  }).catch(() => ElMessage.error('获取失败'))
}

onMounted(() => fetchList({ page: 1, pageSize: pagination.pageSize }))
</script>



<style scoped>
@import '@/assets/styles/page-common.css';
.toolbar-left {
  display: flex;
  justify-content: flex-end;
  text-align: right;
  margin-bottom: 5px;
}
</style>
