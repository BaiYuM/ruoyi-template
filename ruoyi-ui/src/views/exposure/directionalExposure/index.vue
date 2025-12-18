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
          <div class="ml-2" style="margin-right: 20px;" @click="openStats">曝光统计</div>
          <el-button type="primary" :icon="Plus" @click="openCreate">添加配置</el-button>
          <!-- <el-button class="ml-2" @click="downloadTemplate">下载模板</el-button>
          <el-button class="ml-2" @click="openImport">导入模板</el-button> -->
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
    <ExposureStatsDialog v-model:visible="statsVisible" />
  
  </div>
</template>
<script setup>
import { reactive, ref, onMounted, getCurrentInstance, onBeforeUnmount } from 'vue'
import { getDirectionalList, saveDirectionalConfig, uploadDirectionalFileAsync, downloadDirectionalTemplate } from '@/api/exposure'
import MyTable from '@/components/myTable/MyTable.vue'
import DirectionalConfigDrawer from './DirectionalConfigDrawer.vue'
import PageHeader from '@/components/PageHeader'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import ExposureStatsDialog from "./ExposureStatsDialog.vue";

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
const statsVisible = ref(false);
const triggeringIds = ref(new Set());
// drawer
const drawerVisible = ref(false)
const editingData = reactive({})
const isEditing = ref(false)

const columns = [
  { prop: 'name', label: '配置名称' },
  { prop: 'account', label: '平台账号' },
  { prop: 'platform', label: '平台' },
  { prop: 'configType', label: '配置类型' },
  { prop: 'dailyLimit', label: '每天次数限制' },
  { prop: 'startTime', label: '启动时间' },
  { prop: 'status', label: '状态' },
  { prop: 'operation', label: '操作' }
]

const fileInputRef = ref(null)

let unmounted = false
onBeforeUnmount(() => (unmounted = true))

async function manualTriggerDirectional(row) {
  const id = row.id
  // 1. 保护性写法，防止组件已卸载
  if (unmounted) return
  triggeringIds.value.add(id)

  try {
    await apiTriggerDirectional(id)   // 你的接口
    ElMessage.success('已触发')
  } catch (e) {
    ElMessage.error('触发失败')
  } finally {
    // 2. 组件可能已销毁，再判断一次
    if (!unmounted) {
      triggeringIds.value.delete(id)
    }
  }
}

function downloadTemplate() {
  // try server first
  downloadDirectionalTemplate()
    .then((blobData) => {
      const blob = new Blob([blobData], { type: 'application/octet-stream' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = 'directional_template.csv'
      document.body.appendChild(a)
      a.click()
      a.remove()
      URL.revokeObjectURL(url)
    })
    .catch(() => {
      // fallback: generate CSV client-side
      const headers = ['name', 'account', 'platform', 'configType', 'dailyLimit', 'startTime', 'status']
      const sample = ['示例配置', 'example_account', 'douyin', '评论', '10', '08:00', '1']
      const csv = [headers.join(','), sample.map(s => `"${String(s).replace(/"/g, '""')}"`).join(',')].join('\n')
      const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = 'directional_template.csv'
      document.body.appendChild(a)
      a.click()
      a.remove()
      URL.revokeObjectURL(url)
    })
}

function openImport() {
  if (fileInputRef.value) fileInputRef.value.click()
}

function openStats() {
  statsVisible.value = true;
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
  saveDirectionalConfig(data).then(() => {
    ElMessage.success('保存成功')
    fetchList({ page: pagination.currentPage, pageSize: pagination.pageSize })
  }).catch(() => ElMessage.error('保存失败'))
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

*** End Patch