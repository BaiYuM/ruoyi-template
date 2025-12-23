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
                v-model="filters.account"
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
                value-format="YYYY-MM-DD"
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
        <el-table-column prop="account" label="授权账号" min-width="120" />
        <el-table-column prop="prompt" label="提示词" min-width="200" show-overflow-tooltip />
        <el-table-column prop="knowledgeBase" label="知识库" min-width="180" show-overflow-tooltip />
        <el-table-column prop="count" label="携带上下文数" />
        <el-table-column prop="level" label="严谨程度" />
        <el-table-column prop="aiModel" label="AI模型">
          <template #default="{ row }">
            {{ aiModelMap[row.aiModel] || row.aiModel }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column prop="status" label="状态" align="center">
          <template #default="{ row }">
            <el-switch 
              v-model="row.status" 
              :active-value="1" 
              :inactive-value="0"
              @change="() => toggleStatus(row)" 
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="() => editConfig(row)" :icon="Edit" circle />
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
import { reactive, ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, Edit } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
// 导入封装的request请求
import request from '@/utils/request'

const router = useRouter()

const filters = reactive({
  name: '',
  account: '', 
  createTime: [] 
})

// AI模型映射
const aiModelMap = ref({
  '1': 'GPT-3.5',
  '2': 'GPT-4',
  '3': '文心一言',
  '4': '通义千问'
})

/* 2. 分页 & 表格 */
const loading = ref(false)
const tableData = ref([])
const pager = reactive({ page: 1, size: 10, total: 0 })

// 计算属性：处理查询参数
const queryParams = computed(() => {
  const params = {
    pageNum: pager.page,
    pageSize: pager.size,
    name: filters.name || undefined,
    account: filters.account || undefined,
  }
  
  // 处理创建时间范围
  if (filters.createTime && filters.createTime.length === 2) {
    params.beginCreateTime = filters.createTime[0]
    params.endCreateTime = filters.createTime[1]
  } else {
    // 如果没有选择时间范围，移除时间参数
    delete params.beginCreateTime
    delete params.endCreateTime
  }
  
  // 移除值为undefined的参数
  Object.keys(params).forEach(key => {
    if (params[key] === undefined) {
      delete params[key]
    }
  })
  
  return params
})

function openAddConfigDialog() {
  router.push({
    path: '/tiktok/aiSerConfig/detail/index',
    query: { type: 'add' }
  })
}

function editConfig(row) {
  router.push({
    path: '/tiktok/aiSerConfig/detail/index',
    query: {
      type: 'edit',
      id: row.id
    }
  })
}

async function fetchList() {
  loading.value = true
  try {
    // 使用封装的request请求
    const response = await request({
      url: '/aiSerConfig/aiconfig/listWithCommentUser',
      method: 'GET',
      params: queryParams.value
    })
    
    console.log('查询参数:', queryParams.value)
    console.log('接口响应:', response)
    
    // 根据实际API返回结构调整
    if (response.code === 200) {
      tableData.value = response.rows || response.data || []
      pager.total = response.total || 0
    } else {
      ElMessage.error(response.msg || '获取数据失败')
      tableData.value = []
      pager.total = 0
    }
  } catch (error) {
    console.error('获取列表失败:', error)
    ElMessage.error('网络错误或服务器异常')
    tableData.value = []
    pager.total = 0
  } finally {
    loading.value = false
  }
}

/* 6. 重置搜索 */
function resetSearch() {
  Object.assign(filters, { name: '', account: '', createTime: [] })
  pager.page = 1
  fetchList()
}

/* 7. 切换状态 */
async function toggleStatus(row) {
  const originalStatus = row.status == 1 ? 0 : 1

  try {
    const response = await request({
      url: `/aiSerConfig/aiconfig`,
      method: 'PUT',
      data: {
        status: row.status,
        id: row.id
      }
    })
    
    if (response.code !== 200) {
      // 如果请求失败，回滚状态
      row.status = originalStatus
      ElMessage.error(response.msg || '状态更新失败')
    }
  } catch (error) {
    console.error('状态更新失败:', error)
    // 如果请求失败，回滚状态
    row.status = originalStatus
    ElMessage.error('状态更新失败')
  }
}

/* 8. 删除配置 */
async function deleteConfig(row) {
  try {
    await ElMessageBox.confirm(
      `确定要删除配置【${row.name}】吗？此操作不可撤销！`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 使用封装的request请求
    const response = await request({
      url: `/aiSerConfig/aiconfig/${row.id}`,
      method: 'DELETE'
    })
    
    if (response.code === 200) {
      ElMessage.success('配置删除成功')
      // 重新获取列表
      fetchList()
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
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

:deep(.el-table-column--fixed-right .el-button) {
  margin: 0 2px;
}
</style>