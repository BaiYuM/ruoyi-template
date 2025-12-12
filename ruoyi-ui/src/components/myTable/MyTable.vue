<script setup lang="ts">
import PureTable, { LoadingConfig, PaginationProps } from "@pureadmin/table";
import { onMounted, reactive, ref, toRefs, useTemplateRef, computed } from "vue";
import { Delete, Edit, CircleClose, Check } from "@element-plus/icons-vue";
import type { TableInstance } from "element-plus";

defineOptions({
  name: "MyTable"
});

interface MyTableProps {
  columns: any[];
  loading: boolean;
  showOverflowTooltip?: boolean;
  fetchData: () => void;
  handleView?: (row: any) => void; // 新增查看功能处理函数
  handleEdit?: (row: any) => void;
  pagination: any;
  handleDelete?: (row: any) => void;
  handleDisable?: (row: any) => void; // 停用功能处理函数
  handleEnable?: (row: any) => void; // 新增启用功能处理函数
  handleSortChange?: (sort: string, order: string) => void;
  handleSelectionChange?: (selection: any[]) => void;
  rowClickable?: boolean; // 新增行可点击属性
}
const props = defineProps<MyTableProps>();

defineModel("modelValue");

const { pagination } = toRefs(props);

// 确保 operation 列存在
const localColumns = computed(() => {
  const cols = Array.isArray(props.columns) ? [...props.columns] : [];
  const hasOperation = cols.some(c => c && (c.prop === 'operation' || c.label === '操作'));
  // mark operation column with slot name to help pure-table map slot templates
  for (const c of cols) {
    if (c && c.prop === 'operation') {
      c.slot = 'operation';
      // some table implementations support a slots object
      c.slots = c.slots || { default: 'operation' };
    }
  }
  if (!hasOperation) {
    cols.push({ prop: 'operation', label: '操作', width: 220, align: 'center', fixed: 'right', slot: 'operation' });
  }
  return cols;
});

const loadingConfig = reactive<LoadingConfig>({
  text: "正在加载第一页...",
  viewBox: "-10, -10, 50, 50",
  spinner: `
        <path class="path" d="
          M 30 15
          L 28 17
          M 25.61 25.61
          A 15 15, 0, 0, 1, 15 30
          A 15 15, 0, 1, 1, 27.99 7.5
          L 15 15
        " style="stroke-width: 4px; fill: rgba(0, 0, 0, 0)"/>
      `
  // svg: "",
  // background: rgba()
});

function onSizeChange(val) {
  loadingConfig.text = `正在加载第${pagination.value.currentPage}页...`;
  pagination.value.pageSize = val;
  props.fetchData();
}

function onCurrentChange(val) {
  loadingConfig.text = `正在加载第${val}页...`;
  pagination.value.currentPage = val;
  props.fetchData();
}
onMounted(() => {
  // mounted: no debug logs in production
});

function onSortChange({ prop, order }) {
  if (props.handleSortChange) {
    loadingConfig.text = `正在加载...`;
    props.handleSortChange(prop, order);
  }
}

function onSelectionChange(val) {
  if (props.handleSelectionChange) {
    props.handleSelectionChange(val);
  }
}

type TableRefExtra = typeof PureTable & {
  getTableRef: () => TableInstance;
};

const tableRef = useTemplateRef<TableRefExtra>("tableRef");

const exportTableRef = function (): TableRefExtra {
  return tableRef.value;
};

defineExpose({
  exportTableRef
});
// 行点击处理函数
function handleRowClick(row) {
  if (props.rowClickable && props.handleView) {
    props.handleView(row);
  }
}
</script>

<template>
  <pure-table
    ref="tableRef"
    :loading="loading"
    row-key="id"
    adaptive
    :header-cell-style="{
      background: 'var(--el-fill-color-light)',
      color: 'var(--el-text-color-primary)'
    }"
    :data="modelValue"
    :columns="localColumns"
    :pagination="pagination"
    :loading-config="loadingConfig"
    :showOverflowTooltip="showOverflowTooltip"
    :row-class-name="rowClickable ? 'cursor-pointer' : ''"
    @page-size-change="onSizeChange"
    @page-current-change="onCurrentChange"
    @sort-change="onSortChange"
    @selection-change="onSelectionChange"
    @row-click="handleRowClick"
  >
    <!-- forward any parent-provided slots (e.g., column cell templates) to pure-table -->
    <slot />
    <template #empty>
      <el-empty v-if="!loading" description="啥也没有喔" />
    </template>
    <template #id="{ row }">
      <slot name="customId" :row="row">
        <el-empty description="啥也没有喔" />
      </slot>
    </template>
    <template #operation="{ row }">
        <slot name="customOperation" :row="row">
          <div class="flex w-full items-center">
            <div class="flex-1 flex">
              <el-button
                v-if="handleEdit"
                type="primary"
                size="small"
                class="mr-2"
                @click.stop="handleEdit(row)"
              >编辑</el-button>
              <el-popconfirm
                v-if="handleDelete"
                title="你确定要删除吗？"
                @confirm="handleDelete(row)"
              >
                <template #reference>
                  <el-button type="danger" size="small" @click.stop>删除</el-button>
                </template>
              </el-popconfirm>
            </div>
          </div>
        </slot>
    </template>
  </pure-table>
</template>

<style scoped></style>
