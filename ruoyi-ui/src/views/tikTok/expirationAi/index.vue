<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="抖音昵称" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="workPeriodStart">
        <el-date-picker
            v-model="range"
            type="daterange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            range-separator="→"
            unlink-panels
            :picker-options="pickerOptions"
            @change="onRangeChange"
          />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['expirationAi:expirationAi:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['expirationAi:expirationAi:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['expirationAi:expirationAi:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['expirationAi:expirationAi:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="expirationAiList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="账号信息" align="center" prop="userId" />
            <el-table-column label="授权到期时间" align="center" prop="expirationDate" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.expirationDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
        <el-table-column label="AI智能客服" align="center" prop="openAi" />
      <el-table-column label="留资提取" align="center" prop="funds" />
      <el-table-column label="工作时段" align="center" prop="workPeriodType" />

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['expirationAi:expirationAi:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['expirationAi:expirationAi:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改ai客服配置对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="expirationAiRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="主表外键" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入主表外键" />
        </el-form-item>
        <el-form-item label="授权到期时间" prop="expirationDate">
          <el-date-picker clearable
            v-model="form.expirationDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择授权到期时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="是否配置ai客服" prop="openAi">
          <el-input v-model="form.openAi" placeholder="请输入是否配置ai客服" />
        </el-form-item>
        <el-form-item label="留资提取" prop="funds">
          <el-input v-model="form.funds" placeholder="请输入留资提取" />
        </el-form-item>
        <el-form-item label="工作时间段开始" prop="workPeriodStart">
          <el-date-picker clearable
            v-model="form.workPeriodStart"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择工作时间段开始">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="工作时间段结束" prop="workPeriodEnd">
          <el-date-picker clearable
            v-model="form.workPeriodEnd"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择工作时间段结束">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ExpirationAi">
import { listExpirationAi, getExpirationAi, delExpirationAi, addExpirationAi, updateExpirationAi } from "@/api/expirationAi/expirationAi"

const { proxy } = getCurrentInstance()

const expirationAiList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userId: null,
    expirationDate: null,
    openAi: null,
    funds: null,
    workPeriodType: null,
    workPeriodStart: null,
    workPeriodEnd: null,
  },
  rules: {
  }
})

const { queryParams, form, rules } = toRefs(data)
function onRangeChange() { loadData() }
/** 查询ai客服配置列表 */
function getList() {
  loading.value = true
  listExpirationAi(queryParams.value).then(response => {
    expirationAiList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    userId: null,
    expirationDate: null,
    openAi: null,
    funds: null,
    workPeriodType: null,
    workPeriodStart: null,
    workPeriodEnd: null,
    createTime: null,
    createBy: null,
    updateTime: null,
    updateBy: null
  }
  proxy.resetForm("expirationAiRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加ai客服配置"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getExpirationAi(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改ai客服配置"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["expirationAiRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateExpirationAi(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addExpirationAi(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除ai客服配置编号为"' + _ids + '"的数据项？').then(function() {
    return delExpirationAi(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('expirationAi/expirationAi/export', {
    ...queryParams.value
  }, `expirationAi_${new Date().getTime()}.xlsx`)
}

getList()
</script>
