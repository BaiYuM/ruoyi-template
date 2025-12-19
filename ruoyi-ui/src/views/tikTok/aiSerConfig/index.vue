<template>
  <div class="page-wrapper">
    <!-- 1. 筛选卡片 -->
    <el-card shadow="never" class="search-card mb-4">
      <el-form :model="filters" label-width="80px" class="search-form">
        <el-row :gutter="16">
          <el-col :span="6">
            <el-form-item label="配置名称">
              <el-input
                v-model="filters.name"
                placeholder="请输入配置名称"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="授权账号">
              <el-input
                v-model="filters.expiration_id"
                placeholder="请输入授权账号"
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
          <el-button type="primary" :icon="Plus" round @click="openAddConfigDialog">添加配置</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" size="small">
        <el-table-column prop="name" label="配置名称" min-width="120" />
        <el-table-column prop="expiration_id" label="授权账号" min-width="120" />
        <el-table-column prop="prompt" label="提示词" min-width="200" show-overflow-tooltip />
        <el-table-column prop="knowledge_base" label="知识库" min-width="180" show-overflow-tooltip />
        <el-table-column prop="count" label="携带上下文数" />
        <el-table-column prop="level" label="严谨程度" />
        <el-table-column prop="ai_model" label="AI模型" />
        <el-table-column prop="create_time" label="创建时间" />
        <el-table-column prop="status" label="状态" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.status" @change="() => toggleStatus(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template #default="{ row }">
            <!-- 编辑按钮：铅笔图标 + 跳转传ID -->
            <el-button link type="primary" @click="() => editConfig(row)" :icon="Edit" circle />
            <!-- 删除按钮：垃圾桶图标 -->
            <el-button link type="danger" @click="() => deleteConfig(row)" :icon="Delete" circle />
          </template>
        </el-table-column>
      </el-table>

      <!-- 3. 分页（右侧） -->
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
  </div>
</template>

<script setup lang="js">
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus,Delete,Edit } from '@element-plus/icons-vue'
// 引入Vue Router（确保已安装并配置）
import { useRouter } from 'vue-router'

// 初始化路由实例
const router = useRouter()

/* 1. 检索条件（匹配数据库字段） */
const filters = reactive({
  name: '', // 配置名称
  expiration_id: '', // 授权账号
  createTime: [] // 创建时间范围
})

/* 2. 分页 & 表格（数据映射数据库字段） */
const loading = ref(false)
const tableData = ref([])
const pager = reactive({ page: 1, size: 10, total: 0 })

/* 3. 新增配置 - 跳转到详情页（无ID） */
function openAddConfigDialog() {
  // 跳转到aiConfigDetail页面（新增模式，无ID参数）
  router.push({
     path: '/tiktok/aiSerConfig/detail/index', // 需与你的路由表中aiConfigDetail的path一致
    query: { type: 'add' } // 标记为新增模式
  })
}

/* 4. 编辑配置 - 跳转到详情页（传ID） */
function editConfig(row) {
  // 跳转到aiConfigDetail页面（编辑模式，携带ID参数）
  router.push({
   path: '/tiktok/aiSerConfig/detail/index', // 需与你的路由表中aiConfigDetail的path一致
    query: {
      type: 'edit', // 标记为编辑模式
      id: row.id // 传递当前行的ID
    }
  })
}

/* 5. 列表数据请求（模拟接口，实际需对接后端） */
function fetchList() {
  loading.value = true
  setTimeout(() => {
    // 模拟数据库返回的结构
    tableData.value = [
      {
        id: 1,
        name: '客服自动回复',
        expiration_id: 'DouMaster',
        prompt: '#角色任务 你的主要任务是作为抖音平台的...',
        knowledge_base: '主流平台可选择，单平台价格1280元；',
        count: '-',
        level: '-',
        ai_model: '-',
        create_time: '2025-11-14 14:01:00',
        status: true
      },
      {
        id: 2,
        name: '智能自动回复',
        expiration_id: 'Dou大师',
        prompt: '#角色任务 你的主要任务是作为抖音平台的...',
        knowledge_base: '涵盖多种主流平台，单平台价格为1280元',
        count: '-',
        level: '-',
        ai_model: '-',
        create_time: '2025-11-12 10:27:39',
        status: true
      },
      {
        id: 3,
        name: '平台代运营',
        expiration_id: '添澎科技',
        prompt: '#角色任务 作为AI自动客服专家，您的核心...',
        knowledge_base: '平台代运营价格为单平台包月1280元；',
        count: '-',
        level: '-',
        ai_model: '-',
        create_time: '2025-11-10 09:15:22',
        status: true
      }
    ]
    pager.total = tableData.value.length
    loading.value = false
  }, 300)
}

/* 6. 功能按钮 */
function resetSearch() {
  Object.assign(filters, { name: '', expiration_id: '', createTime: [] })
  pager.page = 1
  fetchList()
}

// 切换状态（启用/禁用）
function toggleStatus(row) {
  ElMessage.success(`状态已${row.status ? '启用' : '关闭'}`)
  // 实际项目中需调用接口更新状态
}

// 删除配置（添加确认提示）
function deleteConfig(row) {
  ElMessageBox.confirm(
    `确定要删除配置【${row.name}】吗？此操作不可撤销！`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 确认删除逻辑
    tableData.value = tableData.value.filter(item => item.id !== row.id)
    pager.total = tableData.value.length
    ElMessage.success('配置删除成功')
    // 实际项目中需调用接口删除数据
  }).catch(() => {
    // 取消删除
    ElMessage.info('已取消删除操作')
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

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-right: 10px;
}

/* 调整图标按钮间距 */
:deep(.el-table-column--fixed-right .el-button) {
  margin: 0 2px;
}
</style>