<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="会话ID" prop="chatId">
        <el-input
          v-model="queryParams.chatId"
          placeholder="请输入会话ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设置所属用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入设置所属用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否置顶" prop="isTop">
        <el-input
          v-model="queryParams.isTop"
          placeholder="请输入是否置顶"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否免打扰" prop="isMute">
        <el-input
          v-model="queryParams.isMute"
          placeholder="请输入是否免打扰"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否隐藏会话" prop="isHide">
        <el-input
          v-model="queryParams.isHide"
          placeholder="请输入是否隐藏会话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:setting:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:setting:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:setting:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:setting:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="settingList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="设置ID" align="center" prop="id" />
      <el-table-column label="会话ID" align="center" prop="chatId" />
      <el-table-column label="设置所属用户ID" align="center" prop="userId" />
      <el-table-column label="是否置顶" align="center" prop="isTop" />
      <el-table-column label="是否免打扰" align="center" prop="isMute" />
      <el-table-column label="是否隐藏会话" align="center" prop="isHide" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:setting:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:setting:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改私聊设置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="会话ID" prop="chatId">
          <el-input v-model="form.chatId" placeholder="请输入会话ID" />
        </el-form-item>
        <el-form-item label="设置所属用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入设置所属用户ID" />
        </el-form-item>
        <el-form-item label="是否置顶" prop="isTop">
          <el-input v-model="form.isTop" placeholder="请输入是否置顶" />
        </el-form-item>
        <el-form-item label="是否免打扰" prop="isMute">
          <el-input v-model="form.isMute" placeholder="请输入是否免打扰" />
        </el-form-item>
        <el-form-item label="是否隐藏会话" prop="isHide">
          <el-input v-model="form.isHide" placeholder="请输入是否隐藏会话" />
        </el-form-item>
        <el-form-item label="删除标志" prop="delFlag">
          <el-input v-model="form.delFlag" placeholder="请输入删除标志" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listSetting, getSetting, delSetting, addSetting, updateSetting } from "@/api/chatgroup/setting"

export default {
  name: "Setting",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 私聊设置表格数据
      settingList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        chatId: null,
        userId: null,
        isTop: null,
        isMute: null,
        isHide: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        chatId: [
          { required: true, message: "会话ID不能为空", trigger: "blur" }
        ],
        userId: [
          { required: true, message: "设置所属用户ID不能为空", trigger: "blur" }
        ],
        isTop: [
          { required: true, message: "是否置顶不能为空", trigger: "blur" }
        ],
        isMute: [
          { required: true, message: "是否免打扰不能为空", trigger: "blur" }
        ],
        isHide: [
          { required: true, message: "是否隐藏会话不能为空", trigger: "blur" }
        ],
        updateTime: [
          { required: true, message: "更新时间不能为空", trigger: "blur" }
        ],
        delFlag: [
          { required: true, message: "删除标志不能为空", trigger: "blur" }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询私聊设置列表 */
    getList() {
      this.loading = true
      listSetting(this.queryParams).then(response => {
        this.settingList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        chatId: null,
        userId: null,
        isTop: null,
        isMute: null,
        isHide: null,
        updateTime: null,
        delFlag: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加私聊设置"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getSetting(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改私聊设置"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateSetting(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addSetting(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除私聊设置编号为"' + ids + '"的数据项？').then(function() {
        return delSetting(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/setting/export', {
        ...this.queryParams
      }, `setting_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
