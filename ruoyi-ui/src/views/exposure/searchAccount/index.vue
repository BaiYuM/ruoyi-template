<template>
  <div class="page-wrapper">
    <!-- Header (统一 PageHeader) -->
    <PageHeader>
      <template #actions>
      </template>
    </PageHeader>

    <!-- Search card (multi-column grid like screenshot) -->
    <el-card shadow="never" class="search-card mb-4">
      <div class="search-row">
        <div class="search-grid">
          <div class="search-cell">
            <label class="cell-label">平台账号</label>
            <el-select v-model="filters.platform" placeholder="请选择平台" clearable class="select-control">
              <el-option v-for="item in platformOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </div>
          <div class="search-cell">
            <label class="cell-label">账号/昵称</label>
            <el-input v-model="filters.keyword" placeholder="请输入账号或昵称" clearable />
          </div>
          <div class="search-cell">
            <label class="cell-label">状态</label>
            <el-select v-model="filters.status" placeholder="全部" clearable>
              <el-option label="全部" value="" />
              <el-option label="启用" value="enabled" />
              <el-option label="停用" value="disabled" />
            </el-select>
          </div>  
        </div>

        <div class="search-actions">
          <el-button type="primary" :icon="Search" @click="fetchUserList">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </div>
      </div>
    </el-card>

    <!-- Table card using MyTable (保持项目样式与交互) -->
    <el-card shadow="never" class="table-card">
      <div class="table-toolbar flex items-center mb-4">
        <div class="toolbar-left">
          <div style="margin-right: 8px;">曝光统计</div>
          <el-button type="primary" :icon="Plus" @click="openCreate">添加配置</el-button>
          <!-- <el-button class="ml-2" type="danger" @click="ElMessage.info('批量停用（mock）')">批量停用</el-button>
          <el-button class="ml-2" type="success" @click="ElMessage.info('批量启用（mock）')">批量启用</el-button> -->
        </div>
      </div>

      <div class="table-body">
        <MyTable
          v-model="pageConfig.tableData"
          :loading="loading"
          :columns="columns"
          :pagination="pagination"
          :fetch-data="fetchUserList"
          :handle-edit="handleEdit"
          :handle-view="handleView"
          :handle-disable="onDisableUser"
          :handle-enable="onEnableUser"
          :handle-selection-change="rows => (selectedRows.value = rows)"
          :rowClickable="true"
        >
          <template #customOperation="{ row }">
            <div class="flex flex-nowrap">
              <el-button type="primary" size="small" @click.stop="handleEdit(row)">编辑</el-button>
              <el-button type="primary" size="small" class="ml-2" @click.stop="handleClassify(row)">分配</el-button>
              <el-button v-if="!row.isClosed" type="danger" size="small" class="ml-2" @click.stop="onDisableUser(row)">停用</el-button>
              <el-button v-else type="success" size="small" class="ml-2" @click.stop="onEnableUser(row)">启用</el-button>
            </div>
          </template>
        </MyTable>
      </div>
    </el-card>

    <!-- Drawer component -->
    <ConfigDrawer v-model:visible="drawerVisible" :config="editingData" :platform-options="platformOptions" :is-editing="isEditing" @save="onConfigSave" />
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, nextTick, computed } from 'vue'
import { getDirectionalList } from '@/api/exposure'
import ConfigDrawer from './ConfigDrawer.vue'
import MyTable from '@/components/myTable/MyTable.vue'
import PageHeader from '@/components/PageHeader'
import { Plus, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { saveDirectionalConfig } from '@/api/exposure'

// Filters and options
const filters = reactive({ platform: '', keyword: '' })
const platformOptions = [
  { label: '全部', value: '' },
  { label: '抖音', value: 'douyin' },
  { label: '快手', value: 'kuaishou' },
  { label: '微博', value: 'weibo' }
]

// Table data + page config
const pageConfig = reactive({ tableData: [] })
const pagination = reactive({
  pageSize: 10,
  currentPage: 1,
  pageSizes: [10, 20, 50],
  total: 0,
  align: 'right',
  background: true,
  size: 'default'
})

const loading = ref(false)
const selectedRows = ref([])


// Drawer visibility + editing data
const drawerVisible = ref(false)
const editingData = reactive({})
const isEditing = ref(false)

const columns = [
  { prop: 'name', label: '配置名称' },
  { prop: 'platform', label: '平台' },
  { prop: 'exposure', label: '曝光值' },
  { prop: 'extra', label: '搜索关键词' },
  { prop: 'status', label: '状态' },
  { prop: 'operation', label: '操作' }
]

function fetchUserList(params) {
  loading.value = true
  const page = params && params.page ? params.page : pagination.currentPage
  const size = params && params.pageSize ? params.pageSize : pagination.pageSize
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
  fetchUserList({ page: 1, pageSize: pagination.pageSize })
}

function openCreate() {
  isEditing.value = false
  Object.keys(editingData).forEach(k => delete editingData[k])
  drawerVisible.value = true
}

function openStats() { ElMessage.info('打开曝光统计（mock）') }

function handleEdit(row) {
  isEditing.value = true
  // copy row into editingData
  Object.keys(editingData).forEach(k => delete editingData[k])
  Object.assign(editingData, row)
  drawerVisible.value = true
}

function saveConfig() {
  const validate = formRef.value && formRef.value.validate
  if (validate) {
    formRef.value.validate(valid => {
      if (!valid) return
      doSave()
    })
  } else {
    doSave()
  }
}

function doSave() {
  saveDirectionalConfig(form).then(() => {
    ElMessage.success('保存成功')
    drawerVisible.value = false
    fetchUserList({ page: pagination.currentPage, pageSize: pagination.pageSize })
  }).catch(() => ElMessage.error('保存失败'))
}
function handleView(row) { ElMessage.info(`查看 ${row.account}`) }
function handleClassify(row) { ElMessage.info(`分配 ${row.account}`) }
function onDisableUser(row) { ElMessage.success(`停用 ${row.account}`) }
function onEnableUser(row) { ElMessage.success(`启用 ${row.account}`) }

onMounted(() => {
  fetchUserList({ page: pagination.currentPage, pageSize: pagination.pageSize })
})

// 保存回调：新增或更新表格数据
function onConfigSave(data) {
  saveDirectionalConfig(data).then(() => {
    ElMessage.success('保存成功')
    fetchUserList({ page: pagination.currentPage, pageSize: pagination.pageSize })
  }).catch(() => ElMessage.error('保存失败'))
}

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