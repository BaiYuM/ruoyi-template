<script setup>
import { reactive, ref, onMounted } from 'vue'
import { getDirectionalList, saveDirectionalConfig } from '@/api/exposure'
import MyTable from '@/components/myTable/MyTable.vue'
import DirectionalConfigDrawer from './DirectionalConfigDrawer.vue'
import PageHeader from '@/components/PageHeader'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

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
            </div>
          </template>
        </MyTable>
      </div>
    </el-card>

    <DirectionalConfigDrawer v-model:visible="drawerVisible" :config="editingData" :platform-options="platformOptions" :is-editing="isEditing" @save="onConfigSave" />
  </div>
</template>

<style scoped>
@import '@/assets/styles/page-common.css';
</style>

*** End Patch