<template>
  <el-dialog
    :model-value="visible"
    title="曝光统计"
    width="80%"
    @close="onClose"
  >
    <div style="min-height: 320px">
      <div class="flex items-center mb-3">
        <el-button size="small" @click="fetchList">刷新</el-button>
        <el-button size="small" class="ml-2" @click="onClose">关闭</el-button>
      </div>

      <MyTable
        v-model:modelValue="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        :showOverflowTooltip="true"
        :fetchData="fetchList"
      >
        <template #account="{ row }">
          <div class="flex items-center">
            <div
              style="
                width: 32px;
                height: 32px;
                border-radius: 50%;
                background: #eee;
                margin-right: 8px;
              "
            ></div>
            <div>{{ row.account || "已删除" }}</div>
          </div>
        </template>
        
      </MyTable>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, getCurrentInstance } from "vue";
import { getExposureStats } from "@/api/exposure";
import MyTable from "@/components/myTable/MyTable.vue";
import { parseTime } from '@/utils/ruoyi';
const props = defineProps({ visible: { type: Boolean, default: false } });
const emit = defineEmits(["update:visible"]);

const tableData = ref([]);
const loading = ref(false);
const pagination = reactive({ pageSize: 10, currentPage: 1, total: 0 });

const columns = [
  { prop: "account", label: "账号", minWidth: 180 },
  { prop: "platform", label: "平台", width: 120 },
  { prop: "exposureCount", label: "曝光次数", width: 120 },
  {
    prop: "lastExposureTime",
    label: "最后曝光时间",
    width: 200,
    formatter: (row, column, value) => (value ? parseTime(value) : "")
  },
  {
    prop: "createTime",
    label: "创建时间",
    width: 200,
    formatter: (row, column, value) => (value ? parseTime(value) : "")
  },
];

watch(
  () => props.visible,
  (v) => {
    if (v) fetchList({ page: 1 });
  }
);

function fetchList(opts = {}) {
  loading.value = true;
  const page = opts.page ?? pagination.currentPage;
  const size = opts.pageSize ?? pagination.pageSize;
  getExposureStats({ pageNum: page, pageSize: size })
    .then((res) => {
      loading.value = false;
      // follow TableDataInfo: rows, total
      const rows = res.rows || [];
      // 尝试根据账号推断平台（当后端未提供 platform 时）
      function detectPlatform(account) {
        if (!account) return '';
        const a = account.toString().trim();
        if (/^\d{8,}$/.test(a)) return 'douyin';
        if (/@/.test(a)) return 'weibo';
        if (/^wx|wechat/i.test(a)) return 'weixin';
        if (/^[a-zA-Z0-9_\-]+$/.test(a) && a.length <= 6) return 'shortid';
        return '';
      }

      tableData.value = rows.map(r => ({
        ...r,
        platform: r.platform || detectPlatform(r.account)
      }));
      pagination.total = res.total || (res.rows ? res.rows.length : 0);
      pagination.currentPage = page;
    })
    .catch(() => {
      loading.value = false;
    });
}

// MyTable will call fetchList() when page/size changes
function onPageChange(page) {
  fetchList({ page });
}

function onClose() {
  emit("update:visible", false);
}
</script>

<style scoped>
.ml-2 {
  margin-left: 8px;
}
.mt-3 {
  margin-top: 12px;
}
.mb-3 {
  margin-bottom: 12px;
}
</style>
