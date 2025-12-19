<template>
  <div class="page-wrapper">
    <!-- 1. 筛选卡片 -->
    <el-card shadow="never" class="search-card mb-4">
      <el-form :model="filters" label-width="80px" class="search-form">
        <el-row :gutter="16">
          <el-col :span="6">
            <el-form-item label="抖音号">
              <el-input
                v-model="filters.awemeId"
                placeholder="请输入抖音号"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="抖音昵称">
              <el-input
                v-model="filters.nickname"
                placeholder="请输入昵称"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="创建时间">
              <el-date-picker
                v-model="filters.createTime"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="4" class="search-actions">
            <el-button round @click="resetSearch">重置</el-button>
            <el-button type="primary" round @click="fetchList">查询</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 2. 表格卡片 -->
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>账号列表</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增授权账号</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" size="small">
        <el-table-column prop="awemeId" label="账号信息" min-width="140">
          <template #default="{ row }">
            <div class="account-info">
              <span class="info-id">{{ row.awemeId }}</span>
              <span class="info-name">{{ row.nickname }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="expireTime" label="授权到期时间" width="160" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column prop="workPeriod" label="工作时段" width="100" />
        <el-table-column label="AI智能客服" width="100" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.aiEnable" @change="toggleAi(row)" />
          </template>
        </el-table-column>
        <el-table-column label="留资提取" width="100" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.leadEnable" @change="toggleLead(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button v-if="row.status === 'enabled'" link type="danger" @click="toggleStatus(row, 'disabled')">停用</el-button>
            <el-button v-else link type="success" @click="toggleStatus(row, 'enabled')">启用</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 3. 分页 -->
      <el-pagination
        v-model:current-page="pager.page"
        v-model:page-size="pager.size"
        :total="pager.total"
        layout="total, prev, pager, next, jumper, sizes"
        :page-sizes="[10, 20, 50]"
        class="mt-4"
        @size-change="fetchList"
        @current-change="fetchList"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

/* 1. 检索条件 */
const filters = reactive({
  awemeId: '',
  nickname: '',
  createTime: []
})

/* 2. 分页 & 表格 */
const loading = ref(false)
const tableData = ref<any[]>([])
const pager = reactive({ page: 1, size: 10, total: 0 })

/* 3. 列表数据（mock）*/
function fetchList() {
  loading.value = true
  setTimeout(() => {
    // 假数据，与截图一致
    tableData.value = [
      {
        awemeId: 'DouMaster',
        nickname: 'Dou大师',
        expireTime: '2026-02-12 14:00:00',
        createTime: '2025-11-14 14:01:00',
        workPeriod: '全天',
        aiEnable: true,
        leadEnable: true,
        status: 'enabled'
      },
      {
        awemeId: 'TechTian',
        nickname: '添澎科技',
        expireTime: '2026-02-10 10:09:38',
        createTime: '2025-11-12 10:27:39',
        workPeriod: '全天',
        aiEnable: true,
        leadEnable: false,
        status: 'enabled'
      }
    ]
    pager.total = tableData.value.length
    loading.value = false
  }, 300)
}

/* 4. 功能按钮 */
function resetSearch() {
  Object.assign(filters, { awemeId: '', nickname: '', createTime: [] })
  fetchList()
}
function handleAdd() {
  ElMessage.info('打开新增授权弹窗')
}
function toggleAi(row: any) {
  ElMessage.success(`AI智能客服已${row.aiEnable ? '开启' : '关闭'}`)
}
function toggleLead(row: any) {
  ElMessage.success(`留资提取已${row.leadEnable ? '开启' : '关闭'}`)
}
function toggleStatus(row: any, status: 'enabled' | 'disabled') {
  row.status = status
  ElMessage.success(`已${status === 'enabled' ? '启用' : '停用'}`)
}

onMounted(() => fetchList())
</script>

<style scoped>
.search-form .el-form-item { margin-bottom: 0; }
.search-actions { text-align: right; line-height: 32px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.account-info { display: flex; flex-direction: column; gap: 4px; }
.info-id { font-weight: 600; }
.info-name { color: #666; font-size: 12px; }
</style>