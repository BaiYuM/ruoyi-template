<script setup>
import { reactive, ref, onMounted } from "vue";
import {
  getAutoList,
  saveAutoConfig,
  getAutoSettings,
  saveAutoSettings,
  triggerAutoExposure,
  getTodayCount,
} from "@/api/exposure";
import MyTable from "@/components/myTable/MyTable.vue";
import AutoConfigDrawer from "./AutoConfigDrawer.vue";
import ExposureStatsDialog from "./ExposureStatsDialog.vue";
import AutoExposureSettingsDialog from "./AutoExposureSettingsDialog.vue";
import PageHeader from "@/components/PageHeader";
import { Plus } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox, formatter } from "element-plus";

const filters = reactive({ platform: "", configType: "" });
const platformOptions = [
  { label: "全部", value: "" },
  { label: "抖音", value: "douyin" },
  { label: "快手", value: "kuaishou" },
  { label: "微博", value: "weibo" },
];

const pageConfig = reactive({ tableData: [] });
const pagination = reactive({
  pageSize: 10,
  currentPage: 1,
  pageSizes: [10, 20, 50],
  total: 0,
});
const loading = ref(false);

// drawer
const drawerVisible = ref(false);
const editingData = reactive({});
const isEditing = ref(false);

const statsVisible = ref(false);
const settingsVisible = ref(false);
const settings = reactive({ min: 1, max: 10 });

function openStats() {
  statsVisible.value = true;
}
function openSettings() {
  // fetch current settings then open dialog
  getAutoSettings()
    .then((res) => {
      try {
        const data = res || {};
        settings.min = data.min ?? 1;
        settings.max = data.max ?? 10;
      } catch (e) {
        settings.min = 1;
        settings.max = 10;
      }
      settingsVisible.value = true;
    })
    .catch(() => {
      settings.min = 1;
      settings.max = 10;
      settingsVisible.value = true;
    });
}

function onSettingsSave(data) {
  saveAutoSettings(data)
    .then(() => {
      ElMessage.success("保存成功");
    })
    .catch(() => {
      ElMessage.error("保存失败");
    });
}

const columns = [
  { prop: "name", label: "配置名称" },
  { prop: "account", label: "平台账号" },
  { prop: "platform", label: "平台" },
  // { prop: "dailyLimit", label: "每天次数限制", width: 120 },
  // { prop: "exposureCount", label: "今日已曝光", width: 120 },
  // {
  //   prop: "remaining",
  //   label: "剩余次数",
  //   width: 120,
  //   formatter: (row) => {
  //     const limit = Number(row.dailyLimit ?? 0);
  //     const used = Number(row.exposureCount ?? 0);
  //     return Math.max(0, limit - used);
  //   },
  // },
  { prop: "lastStopReason", label: "上次停止原因" },
  { prop: "configType", label: "配置类型" },
  { prop: "searchKeywords", label: "搜索关键词" },
  { prop: "commentKeywords", label: "评论匹配关键词" },
  {
    prop: "status",
    label: "状态",
    formatter: (row, column, value) => {
      const v = typeof value === "string" ? Number(value) : value;
      return v === 0 ? "启用" : "禁用";
    },
  },
  { prop: "operation", label: "操作" },
];

function fetchList(params) {
  loading.value = true;
  const page = params?.page ?? pagination.currentPage;
  const size = params?.pageSize ?? pagination.pageSize;
  const payload = {
    pageNum: page,
    pageSize: size,
    platform: filters.platform,
    configType: filters.configType,
  };
  getAutoList(payload)
    .then((res) => {
      loading.value = false;
      // res.rows and res.total follow TableDataInfo contract
      pageConfig.tableData = res.rows || [];
      pagination.total = res.total || (res.rows ? res.rows.length : 0);
      pagination.currentPage = page;
    })
    .catch(() => {
      loading.value = false;
    });
}

function resetSearch() {
  filters.platform = "";
  filters.configType = "";
  pagination.currentPage = 1;
  fetchList({ page: 1, pageSize: pagination.pageSize });
}

function openCreate() {
  isEditing.value = false;
  Object.keys(editingData).forEach((k) => delete editingData[k]);
  drawerVisible.value = true;
}

function handleEdit(row) {
  isEditing.value = true;
  Object.keys(editingData).forEach((k) => delete editingData[k]);
  Object.assign(editingData, row);
  drawerVisible.value = true;
}

function toggleAuto(row) {
  const enabling = !(row.status === "0");
  const newStatus = enabling ? "0" : "1"; // '0' 表示启用

  // 计算剩余可用曝光次数（以今日已曝光计）
  const limit = Number(row.dailyLimit ?? 0);
  const used = Number(row.exposureCount ?? 0);
  const remaining = Math.max(0, limit - used);

  // If trying to enable but no remaining capacity, stop and record reason
  if (enabling && remaining <= 0) {
    const reason = "达到每日次数上限";
    const payload = { id: row.id, status: "1", lastStopReason: reason };
    saveAutoConfig(payload)
      .then(() => {
        row.status = "1";
        row.lastStopReason = reason;
        ElMessage.warning("无法启用：已达到今日上限");
      })
      .catch(() => {
        ElMessage.error("操作失败");
      });
    return;
  }

  // When disabling, automatically set a stop reason
  let payload = { id: row.id, status: newStatus };
  if (!enabling) {
    payload.lastStopReason = "手动停止";
  } else {
    // when enabling, optionally inform backend how many runs are allowed today
    payload.maxRunsToday = remaining;
  }

  saveAutoConfig(payload)
    .then(() => {
      row.status = newStatus;
      if (!enabling) row.lastStopReason = payload.lastStopReason;
      if (enabling) row.scheduledCount = remaining;
      ElMessage.success("操作成功");
    })
    .catch(() => {
      ElMessage.error("操作失败");
    });
}

function onConfigSave(data) {
  saveAutoConfig(data)
    .then(() => {
      ElMessage.success("保存成功");
      // refresh list
      fetchList({
        page: pagination.currentPage,
        pageSize: pagination.pageSize,
      });
    })
    .catch(() => {
      ElMessage.error("保存失败");
    });
}

function manualTrigger(row) {
  if (!row || !row.id) return;
  ElMessageBox.confirm('确定立即触发一次曝光？', '提示', { type: 'warning' })
    .then(() => {
      triggerAutoExposure(row.id)
        .then((res) => {
          if (res && res.success) {
            ElMessage.success('触发成功');
            fetchList({ page: pagination.currentPage, pageSize: pagination.pageSize });
          } else {
            ElMessage.error('触发失败: ' + (res && res.message ? res.message : '未知错误'));
          }
        })
        .catch(() => {
          ElMessage.error('触发失败');
        });
    })
    .catch(() => {});
}

function showTodayCount(row) {
  if (!row || !row.id) return;
  getTodayCount(row.id)
    .then((res) => {
      const cnt = (res && res.todayCount) || 0;
      ElMessage.info(`今日曝光：${cnt}`);
    })
    .catch(() => {
      ElMessage.error('获取失败');
    });
}

onMounted(() => fetchList({ page: 1, pageSize: pagination.pageSize }));
</script>

<template>
  <div class="page-wrapper">
    <PageHeader>
      <template #actions> </template>
    </PageHeader>

    <el-card shadow="never" class="search-card mb-4">
      <div class="search-row">
        <div class="search-grid compact">
          <div class="search-cell platform-cell">
            <label class="cell-label">平台账号</label>
            <el-select
              v-model="filters.platform"
              placeholder="请选择平台"
              clearable
              class="select-control"
            >
              <el-option
                v-for="item in platformOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </div>
          <div class="search-cell keyword-cell">
            <label class="cell-label">配置类型</label>
            <el-select
              v-model="filters.configType"
              placeholder="请选择"
              clearable
              class="select-control"
            >
              <el-option label="全部" value="" />
              <el-option label="评论" value="评论" />
              <el-option label="私信（仅抖音）" value="私信" />
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
          <div class="ml-2" style="margin-right: 20px;" @click="openSettings">自动曝光设置</div>
          <el-button   type="primary" :icon="Plus" @click="openCreate"
            >添加配置</el-button>
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
              <el-button
                type="primary"
                size="small"
                @click.stop="handleEdit(row)"
                >编辑</el-button
              >
              <el-button
                size="small"
                class="ml-2"
                @click.stop="toggleAuto(row)"
                >{{ row.status === "0" ? "关闭" : "开启" }}</el-button
              >
                  <el-button
                    size="small"
                    class="ml-2"
                    @click.stop="manualTrigger(row)"
                    >立即触发</el-button
                  >
                  <el-button
                    size="small"
                    class="ml-2"
                    @click.stop="showTodayCount(row)"
                    >今日计数</el-button
                  >
            </div>
          </template>
        </MyTable>
      </div>
    </el-card>

    <AutoConfigDrawer
      v-model:visible="drawerVisible"
      :config="editingData"
      :platform-options="platformOptions"
      :is-editing="isEditing"
      @save="onConfigSave"
    />
    <ExposureStatsDialog v-model:visible="statsVisible" />
    <AutoExposureSettingsDialog
      v-model:visible="settingsVisible"
      :initial="settings"
      @save="onSettingsSave"
    />
  </div>
</template>

<style scoped>
@import "@/assets/styles/page-common.css";
.toolbar-left {
  display: flex;
  justify-content: flex-end;
  text-align: right;
  margin-bottom: 5px;
}
</style>
