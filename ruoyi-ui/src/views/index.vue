<!--
  TEMPLATE: 项目首页（模板）
  说明：
  - 这是一个示例首页，包含版本、联系信息与更新日志模块，用作项目启动页模板。
  - 作为模板使用时，可替换或移除不需要的卡片组件、链接与文案。
  - 若用于新项目，请修改 `version` 来源、外部链接与版权信息。
-->
<template>
  <div class="app-container home">
    <el-row :gutter="20">
      <el-col :sm="24" :lg="12" style="padding-left: 20px">
        <h2>若依后台管理框架</h2>
        <p>
          <b>当前版本:</b> <span>v{{ version }}</span>
        </p>
        <template>
          <div class="app-container home">
            <el-row :gutter="20">
              <el-col :xs="24" :sm="24" :md="8">
                <el-card class="col-item" shadow="hover">
                  <div slot="header" class="clearfix">
                    <span>系统状态</span>
                  </div>
                  <div class="body status-list">
                    <div class="status-item">
                      <div class="status-title">CPU</div>
                      <el-progress
                        :percentage="systemStatus.cpu"
                        status="active"
                      />
                    </div>
                    <div class="status-item">
                      <div class="status-title">内存</div>
                      <el-progress
                        :percentage="systemStatus.memory"
                        color="#20a0ff"
                      />
                    </div>
                    <div class="status-item">
                      <div class="status-title">磁盘</div>
                      <el-progress
                        :percentage="systemStatus.disk"
                        color="#13ce66"
                      />
                    </div>
                    <el-button type="text" size="mini" @click="refreshStatus"
                      >刷新</el-button
                    >
                  </div>
                </el-card>
              </el-col>

              <el-col :xs="24" :sm="24" :md="16">
                <el-card class="col-item" shadow="hover">
                  <div slot="header" class="clearfix">
                    <span>最近活动</span>
                  </div>
                  <div class="body">
                    <el-timeline>
                      <el-timeline-item
                        v-for="(item, idx) in recentActivities"
                        :key="idx"
                        :timestamp="item.time"
                      >
                        {{ item.text }}
                      </el-timeline-item>
                    </el-timeline>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </template>
      </el-col>
      <!-- 捐赠支持版块已移除 -->
    </el-row>
  </div>
</template>

<script>
export default {
  name: "Index",
  data() {
    return {
      // 版本号
      version: "3.9.0",
      systemStatus: {
        cpu: 12,
        memory: 46,
        disk: 68,
      },
      recentActivities: [
        { time: "现在", text: "系统已启动" },
        { time: "1 小时前", text: "用户 admin 登录" },
        { time: "昨天", text: "导入示例数据" },
      ],
    };
  },
  created() {
    // 初始加载时刷新一次状态（占位实现）
    this.refreshStatus();
  },
  methods: {
    goTarget(href) {
      window.open(href, "_blank");
    },
    openGuide(step) {
      // 占位：根据不同步骤打开对应文档或路由，项目中可替换为实际操作
      this.$message({ message: `快速开始：${step}`, type: "info" });
    },
    refreshStatus() {
      // 占位：真实项目可调用后端接口获取系统监控数据
      // 这里用随机值模拟一个刷新效果
      this.systemStatus.cpu = Math.floor(Math.random() * 40) + 10;
      this.systemStatus.memory = Math.floor(Math.random() * 60) + 20;
      this.systemStatus.disk = Math.floor(Math.random() * 80) + 10;
      this.$message({ message: "系统状态已刷新", type: "success" });
    },
    openActivities() {
      // 占位：可以跳转到日志或活动列表页
      this.$router.push({ path: "/monitor/operlog" }).catch(() => {});
    },
  },
};
</script>

<style scoped lang="scss">
.home {
  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }
  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }
  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }
  .status-list {
    .status-item {
      margin-bottom: 12px;
    }
    .status-title {
      font-size: 12px;
      color: #606266;
      margin-bottom: 6px;
    }
  }
}
</style>
