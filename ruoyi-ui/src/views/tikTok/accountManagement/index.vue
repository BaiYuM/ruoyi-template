<template>
  <div class="page-wrapper">
    <!-- 1. 筛选卡片 -->
    <el-card shadow="never" class="search-card mb-4">
      <el-form :model="filters" label-width="80px" class="search-form">
        <el-row :gutter="16">
          <el-col :span="6">
            <el-form-item label="用户昵称">
              <el-input
                v-model="filters.nickName"
                placeholder="请输入昵称"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="创建时间">
              <el-date-picker
                v-model="filters.createTimeRange"
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
          <el-button type="primary" :icon="Plus" round @click="openAuthTipDialog">新增授权账号</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" size="small">
        <el-table-column label="账号信息" min-width="160">
          <template #default="{ row }">
            <div class="account-info">
              <el-avatar
                :src="row.avatar"
                :size="32"
                class="avatar"
              >
                <span v-if="!row.avatar">{{ row.nickName?.charAt(0) || '用' }}</span>
              </el-avatar>
              <div class="account-text">
                <div class="nickname">{{ row.nickName || '未设置昵称' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="expirationDate" label="授权到期时间" />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="工作时段">
          <template #default="{ row }">
            <span v-if="row.workPeriodType === 0">全天</span>
            <span v-else>{{ row.workPeriodStart }} - {{ row.workPeriodEnd }}</span>
          </template>
        </el-table-column>
        <el-table-column label="AI智能客服" fixed="right" align="center">
          <template #default="{ row }">
            <el-switch 
              v-model="row.openAi" 
              :active-value="1"
              :inactive-value="0"
              @change="() => toggleAi(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="留资提取" fixed="right" align="center">
          <template #default="{ row }">
            <el-switch 
              v-model="row.funds" 
              :active-value="1"
              :inactive-value="0"
              @change="() => toggleFunds(row)" 
            />
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
// 导入封装的request请求
import request from '@/utils/request'
import { useRouter } from 'vue-router'
// 仅导入用到的图标和组件
import { Plus, Delete } from '@element-plus/icons-vue'
import AuthTipDialog from './AuthTipDialog.vue'

const router = useRouter()

const filters = reactive({
  nickName: '',
  createTimeRange: []
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

const fetchList = async () => {
  loading.value = true
  try {
    // 构建请求参数
    const params = {
      page: pager.page,
      size: pager.size,
      nickName: filters.nickName,
    }
    
    // 添加时间范围参数
    if (filters.createTimeRange && filters.createTimeRange.length === 2) {
      params.beginCreateTime = filters.createTimeRange[0]
      params.endCreateTime = filters.createTimeRange[1]
    }
    
    const response = await request({
      url: '/aiCuServer/expirationAi/getExpirationAiInfo',
      method: 'get',
      params
    })
    if (response && response.code == 200) {
      tableData.value = response.rows || []
      pager.total = response.total || 0

      // 转换字段类型（如果后端返回的是字符串）
      tableData.value.forEach(item => {
        item.openAi = Number(item.openAi)
        item.funds = Number(item.funds) || 0
        item.workPeriodType = Number(item.workPeriodType) || 0
      })
    } else {
      ElMessage.error(response?.message || '获取数据失败')
    }
  } catch (error) {
    console.error('获取数据失败:', error)
    ElMessage.error('获取数据失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

/* 5. 功能方法 */
// 重置筛选条件
const resetSearch = () => {
  Object.assign(filters, { nickName: '', createTimeRange: [] })
  pager.page = 1
  fetchList()
}

// 切换AI智能客服状态
const toggleAi = async (row) => {
  try {

    const isEnabling = row.openAi

    // 如果是开启AI客服，先检查是否有配置
    if (isEnabling) {
      const configRes = await request({
        url: `/aiSerConfig/aiconfig/byExpiration/${row.id}`,
        method: 'get'
      })

      // 如果没有配置，提示用户去创建
      if (!configRes.data) {
        const action = await ElMessageBox.confirm(
          '该账号尚未配置AI客服，是否立即前往配置？',
          '提示',
          {
            confirmButtonText: '去配置',
            cancelButtonText: '暂不配置',
            type: 'warning'
          }
        )
        
        if (action === 'confirm') {
          // 更新开关状态
          await request({
            url: 'aiCuServer/expirationAi',
            method: 'put',
            data: { id: row.id, openAi: 1 }
          })
          // 跳转到AI配置页面，并携带账号ID
          router.push({
            path: '/tiktok/aiSerConfig/detail/index',
            query: { type: 'add', expirationId: row.id }
          })
        } else {
          // 用户取消，恢复开关状态
          row.openAi = 0
        }
        return
      }
    }

    const newValue = isEnabling ? 1 : 0
    await request({
      url: 'aiCuServer/expirationAi',
      method: 'put',
      data: {
        id: row.id,
        openAi: newValue
      }
    })
    
    if (isEnabling) {
      ElMessage.success('AI智能客服已开启')
    } else {
      ElMessage.info('AI智能客服已关闭')
    }

    // 重新获取列表以更新数据
    await fetchList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新状态失败:', error)
      ElMessage.error('更新失败，请稍后重试')
      // 恢复到原始状态
      row.openAi = originalState
    }
  }
}

// 切换留资提取状态
const toggleFunds = async (row) => {
  try {
    
    const newValue = row.funds
    await request({
      url: 'aiCuServer/expirationAi',
      method: 'put',
      data: {
        id: row.id,
        funds: newValue
      }
    })
    
    if (newValue === 1) {
      ElMessage.success('留资提取已开启')
    } else {
      ElMessage.info('留资提取已关闭')
    }
    
    // 重新获取列表以更新数据
    await fetchList()
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error('更新失败，请稍后重试')
    // 恢复到原始状态
    row.funds = originalState
  }
}

// 删除账号
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      '此操作将永久删除该授权账号，是否继续？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await request({
      url: `aiCuServer/expirationAi/${row.id}`,
      method: 'delete'
    })
    
    // 从列表中移除
    tableData.value = tableData.value.filter(item => item.id !== row.id)
    pager.total = tableData.value.length
    ElMessage.success('删除成功!')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    } else {
      ElMessage.info('已取消删除')
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

.account-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  flex-shrink: 0;
  background-color: #409eff;
  color: white;
}

.account-text {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.nickname {
  color: #333;
  font-size: 14px;
  font-weight: 500;
  line-height: 1.2;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-right: 10px;
}

/* 删除按钮样式 */
.delete-btn {
  color: #409eff !important;
}

.delete-btn:hover {
  color: #66b1ff !important;
}
</style>