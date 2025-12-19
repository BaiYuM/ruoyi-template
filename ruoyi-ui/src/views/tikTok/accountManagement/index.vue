<template>
  <div class="page-wrapper">
    <!-- 1. 筛选卡片 -->
    <el-card shadow="never" class="search-card mb-4">
      <el-form :model="filters" label-width="80px" class="search-form">
        <el-row :gutter="16">
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
          <span></span>
          <el-button type="primary" :icon="Plus" round @click="openAuthTipDialog">新增授权账号</el-button>
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
        <el-table-column prop="expireTime" label="授权到期时间" />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column prop="workPeriod" label="工作时段" />
        <el-table-column label="AI智能客服" fixed="right" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.aiEnable" @change="() => toggleAi(row)" />
          </template>
        </el-table-column>
        <el-table-column label="留资提取" fixed="right" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.leadEnable" @change="() => toggleLead(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right" align="center">
          <template #default="{ row }">
            <el-button 
              link 
              type="primary" 
              :icon="Delete" 
              @click="() => handleDelete(row)"
              class="delete-btn"
            />
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pager.page"
          v-model:page-size="pager.size"
          :total="pager.total"
          layout="total, prev, pager, next, jumper, sizes"
          :page-sizes="[10, 20, 50]"
          @size-change="fetchList"
          @current-change="fetchList"
        />
      </div>
    </el-card>

    <AuthTipDialog
      v-model="authTipDialogVisible"
      @confirm="handleAuthTipConfirm"
      @close="handleAuthTipClose"
    />
  </div>
</template>

<script setup lang="js">
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
// 仅导入用到的图标和组件
import { Plus, Delete } from '@element-plus/icons-vue'
import AuthTipDialog from './AuthTipDialog.vue'

const filters = reactive({
  nickname: '',
  createTime: []
})

const loading = ref(false)
const tableData = ref([])
const pager = reactive({ page: 1, size: 10, total: 0 })

const authTipDialogVisible = ref(false)

const openAuthTipDialog = () => {
  authTipDialogVisible.value = true
}

const handleAuthTipConfirm = () => {
  ElMessage.info('已确认，即将打开新增授权表单')
}

const handleAuthTipClose = () => {
  console.log('提示弹窗已关闭')
}

const fetchList = () => {
  loading.value = true
  setTimeout(() => {
    tableData.value = [
      {
        awemeId: 'DouMaster',
        nickname: 'Dou大师',
        expireTime: '2026-02-12 14:00:00',
        createTime: '2025-11-14 14:01:00',
        workPeriod: '全天',
        aiEnable: true,
        leadEnable: true
      },
      {
        awemeId: 'TechTian',
        nickname: '添澎科技',
        expireTime: '2026-02-10 10:09:38',
        createTime: '2025-11-12 10:27:39',
        workPeriod: '全天',
        aiEnable: true,
        leadEnable: false
      }
    ]
    pager.total = tableData.value.length
    loading.value = false
  }, 300)
}

/* 5. 功能方法 */
// 重置筛选条件
const resetSearch = () => {
  Object.assign(filters, { nickname: '', createTime: [] })
  pager.page = 1
  fetchList()
}

// 切换AI智能客服状态
const toggleAi = (row) => {
  ElMessage.success(`AI智能客服已${row.aiEnable ? '开启' : '关闭'}`)
}

// 切换留资提取状态
const toggleLead = (row) => {
  ElMessage.success(`留资提取已${row.leadEnable ? '开启' : '关闭'}`)
}

// 删除账号
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '此操作将永久删除该授权账号，是否继续？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    tableData.value = tableData.value.filter(item => item.awemeId !== row.awemeId)
    pager.total = tableData.value.length
    ElMessage.success('删除成功!')
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 初始化加载数据
onMounted(() => fetchList())
</script>

<style scoped>
.page-wrapper {
  margin: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 40px);
}

.search-form .el-form-item {
  margin-bottom: 0;
}

.search-actions {
  text-align: right;
  line-height: 32px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.account-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-id {
  font-weight: 600;
}

.info-name {
  color: #666;
  font-size: 12px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-right: 10px;
}

/* 删除按钮样式 */
.delete-btn {
  color: #409eff !important; /* 蓝色 */
}

.delete-btn:hover {
  color: #66b1ff !important; /* hover浅蓝 */
}
</style>