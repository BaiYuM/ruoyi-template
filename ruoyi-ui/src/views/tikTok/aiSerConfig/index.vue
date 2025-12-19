<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="授权用户表" prop="expirationId">
        <el-input
          v-model="queryParams.expirationId"
          placeholder="请输入授权用户表"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="ai名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入ai名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="携带上下文轮数" prop="count">
        <el-input
          v-model="queryParams.count"
          placeholder="请输入携带上下文轮数"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="严谨程度" prop="level">
        <el-input
          v-model="queryParams.level"
          placeholder="请输入严谨程度"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="ai模型" prop="aiModel">
        <el-input
          v-model="queryParams.aiModel"
          placeholder="请输入ai模型"
          clearable
          @keyup.enter="handleQuery"
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
          v-hasPermi="['tikTok:aiSerConfig:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['tikTok:aiSerConfig:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['tikTok:aiSerConfig:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['tikTok:aiSerConfig:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="aiconfigList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" />
      <el-table-column label="授权用户表" align="center" prop="expirationId" />
      <el-table-column label="ai名称" align="center" prop="name" />
      <el-table-column label="提示词" align="center" prop="prompt" />
      <el-table-column label="知识库" align="center" prop="knowledgeBase" />
      <el-table-column label="携带上下文轮数" align="center" prop="count" />
      <el-table-column label="严谨程度" align="center" prop="level" />
      <el-table-column label="状态" align="center" prop="status" />
      <el-table-column label="ai模型" align="center" prop="aiModel" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['tikTok:aiSerConfig:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['tikTok:aiSerConfig:remove']">删除</el-button>
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

    <!-- 添加或修改AI客服配置对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="aiconfigRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="授权用户表" prop="expirationId">
          <el-input v-model="form.expirationId" placeholder="请输入授权用户表" />
        </el-form-item>
        <el-form-item label="ai名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入ai名称" />
        </el-form-item>
        <el-form-item label="提示词" prop="prompt">
          <el-input v-model="form.prompt" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="知识库" prop="knowledgeBase">
          <el-input v-model="form.knowledgeBase" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="携带上下文轮数" prop="count">
          <el-input v-model="form.count" placeholder="请输入携带上下文轮数" />
        </el-form-item>
        <el-form-item label="严谨程度" prop="level">
          <el-input v-model="form.level" placeholder="请输入严谨程度" />
        </el-form-item>
        <el-form-item label="ai模型" prop="aiModel">
          <el-input v-model="form.aiModel" placeholder="请输入ai模型" />
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

<script setup name="Aiconfig">
import { listAiconfig, getAiconfig, delAiconfig, addAiconfig, updateAiconfig } from "@/api/aiSerConfig/aiconfig"

const { proxy } = getCurrentInstance()

const aiconfigList = ref([])
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
    expirationId: null,
    name: null,
    prompt: null,
    knowledgeBase: null,
    count: null,
    level: null,
    status: null,
    aiModel: null,
  },
  rules: {
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询AI客服配置列表 */
function getList() {
  loading.value = true
  listAiconfig(queryParams.value).then(response => {
    aiconfigList.value = response.rows
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
    expirationId: null,
    name: null,
    prompt: null,
    knowledgeBase: null,
    count: null,
    level: null,
    status: null,
    aiModel: null,
    createTime: null
  }
  proxy.resetForm("aiconfigRef")
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
  title.value = "添加AI客服配置"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getAiconfig(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改AI客服配置"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["aiconfigRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateAiconfig(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addAiconfig(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除AI客服配置编号为"' + _ids + '"的数据项？').then(function() {
    return delAiconfig(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('aiSerConfig/aiconfig/export', {
    ...queryParams.value
  }, `aiconfig_${new Date().getTime()}.xlsx`)
}

getList()
</script>
