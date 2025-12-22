<template>
    <div style="font-weight: bold; font-size: 18px; margin: 20px 30px;">私信消息</div>
    <div class="private-msg">
        <!-- 主体区域 - 三栏布局 -->
        <div class="main-body">
            <!-- 左侧会话列表 -->
            <div class="session-panel">
                <!-- 搜索筛选区域 -->
                <div class="filter-section">
                    <!-- 第一行：选择抖音号文本 -->
                    <div class="filter-label-row">
                        选择抖音号
                    </div>
                    
                    <!-- 第二行：筛选框（灰色背景） -->
                    <div class="search-row">
                        <el-select
                            v-model="selectedDouyin"
                            placeholder="筛选抖音号"
                            class="filter-input"
                            filterable
                            clearable
                        >
                            <el-option 
                                v-for="item in douyinOptions" 
                                :key="item.value"
                                :label="item.label"
                                :value="item.value"
                            />
                        </el-select>
                    </div>
                    
                    <div class="filter-row">
                        <div class="filter-all-wrapper">
                            <div class="filter-all-text" @click.stop="toggleFilterOptions">
                                <span>全部</span>
                                <el-icon :class="['arrow-icon', { 'rotate': showFilterOptions }]">
                                    <ArrowDown />
                                </el-icon>
                            </div>
                            <div v-if="showFilterOptions" class="filter-options">
                                <div 
                                    class="filter-option" 
                                    :class="{ 'active': filterType === 'all' }"
                                    @click="selectFilter('all')"
                                >
                                    全部
                                </div>
                                <div 
                                    class="filter-option" 
                                    :class="{ 'active': filterType === 'lead' }"
                                    @click="selectFilter('lead')"
                                >
                                    已留资
                                </div>
                                <div 
                                    class="filter-option" 
                                    :class="{ 'active': filterType === 'no-lead' }"
                                    @click="selectFilter('no-lead')"
                                >
                                    未留资
                                </div>
                            </div>
                        </div>
                        <el-button 
                            type="text" 
                            class="refresh-btn"
                            @click="handleRefresh"
                        >
                            <el-icon><Refresh /></el-icon>
                        </el-button>
                    </div>
                </div>

                <!-- 会话列表 -->
                <div class="session-list">
                    <div 
                        class="session-item" 
                        v-for="item in filteredSessions" 
                        :key="item.id"
                        :class="{ 'active': activeSessionId === item.id }"
                        @click="selectSession(item)"
                    >
                        <div class="session-avatar">
                            <el-avatar :size="40" :src="item.avatar" />
                        </div>
                        <div class="session-content">
                            <div class="session-header">
                                <div class="header-left">
                                    <span class="nickname">{{ item.nick }}</span>
                                    <el-tag 
                                        v-if="item.isLead" 
                                        size="small" 
                                        type="success"
                                        class="lead-tag"
                                    >已留资</el-tag>
                                    <el-tag 
                                        v-else 
                                        size="small" 
                                        type="info"
                                        class="lead-tag"
                                    >未留资</el-tag>
                                </div>
                                <span class="time">{{ formatTime(item.time) }}</span>
                            </div>
                            <div class="session-preview">
                                {{ item.lastMsg }}
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 底部信息 -->
                <div class="session-footer">
                    已拉取72小时内上线{{ total }}条会话
                </div>
            </div>

            <!-- 中间聊天区域 -->
            <div class="chat-panel">
                <div v-if="activeSession" class="chat-container">
                    <!-- 聊天消息区域 -->
                    <div class="message-list" ref="messageListRef">
                        <div 
                            v-for="(msg, index) in messages" 
                            :key="index"
                            :class="['message-item', msg.type]"
                        >
                            <!-- 接收的消息 -->
                            <div v-if="msg.type === 'received'" class="message-received">
                                <div class="message-avatar">
                                    <el-avatar :size="32" :src="activeSession.avatar" />
                                </div>
                                <div class="message-content-wrapper">
                                    <div class="message-nickname">{{ activeSession.nick }}</div>
                                    <div class="message-time">{{ msg.time }}</div>
                                    <div class="message-content">
                                        <div class="message-text">{{ msg.content }}</div>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- 发送的消息 -->
                            <div v-if="msg.type === 'sent'" class="message-sent">
                                <div class="message-content-wrapper">
                                    <div class="message-nickname-sent">我</div>
                                    <div class="message-time-sent">{{ msg.time }}</div>
                                    <div class="message-content">
                                        <div class="message-text">{{ msg.content }}</div>
                                    </div>
                                </div>
                                <div class="message-avatar">
                                    <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- 聊天输入区域 -->
                    <div class="chat-input">
                        <el-input
                            v-model="inputMessage"
                            type="textarea"
                            :rows="3"
                            placeholder="回复内容，可按Enter键发送，按AIt+Enter换行"
                            resize="none"
                            class="reply-input"
                            @keydown.enter="handleKeydown"
                        />
                        <div class="input-actions">
                            <el-button 
                                type="default" 
                                class="send-btn"
                                @click="sendMessage"
                            >
                                发送 (Enter)
                            </el-button>
                        </div>
                    </div>
                </div>
                <div v-else class="empty-chat">
                    <el-empty description="暂无会话，点击左侧列表开始聊天">
                        <template #image>
                            <el-icon size="64" color="#dcdfe6"><ChatDotSquare /></el-icon>
                        </template>
                    </el-empty>
                </div>
            </div>

            <!-- 右侧线索区域 -->
            <div class="clue-panel">
                <div v-if="activeSession" class="clue-container">
                    <!-- 线索头部 -->
                    <div class="clue-header">
                        <div class="clue-user">
                            <el-avatar :size="48" :src="activeSession.avatar" />
                            <div class="clue-user-info">
                                <div class="clue-nickname">{{ activeSession.nick }}</div>
                            </div>
                        </div>
                    </div>

                    <!-- 线索内容 -->
                    <div class="clue-content">
                        <div class="clue-title">已关联线索</div>
                        <div class="clue-list">
                            <div 
                                v-for="(clue, index) in clues" 
                                :key="index"
                                class="clue-item"
                            >
                                <div class="clue-item-header">
                                    <span class="clue-label">{{ clue.label }}</span>
                                    <span class="clue-time">{{ clue.time }}</span>
                                </div>
                                <div class="clue-value">{{ clue.value }}</div>
                            </div>
                        </div>
                    </div>
                </div>
                <div v-else class="empty-clue">
                    <el-empty description="暂无线索信息">
                        <template #image>
                            <el-icon size="64" color="#dcdfe6"><Collection /></el-icon>
                        </template>
                    </el-empty>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ChatDotSquare, ArrowDown, Refresh, Collection } from '@element-plus/icons-vue'

// 抖音号选择
const selectedDouyin = ref('')
const filterType = ref('all')
const showFilterOptions = ref(false)

// 分页
const page = ref(1)
const pageSize = 50
const total = ref(0)

// 会话相关
const sessionList = ref([])
const activeSessionId = ref(null)
const activeSession = ref(null)

// 聊天消息
const messages = ref([])
const inputMessage = ref('')
const messageListRef = ref(null)

// 线索数据
const clues = ref([
    { label: '手机号', value: '138****5678', time: '2025-11-04' },
    { label: '微信号', value: 'wx_abc123', time: '2025-11-03' },
    { label: '邮箱', value: 'user@example.com', time: '2025-11-02' },
    { label: '咨询产品', value: '抖音代运营服务', time: '2025-11-01' },
    { label: '预算范围', value: '1000-2000元', time: '2025-10-31' }
])

// 获取抖音号选项
const douyinOptions = computed(() => {
    // 获取所有抖音号
    const allDouyinIds = sessionList.value.map(item => item.douyinId)
    // 去重
    const uniqueIds = [...new Set(allDouyinIds)]
    
    // 创建选项
    return uniqueIds.map(douyinId => ({
        label: douyinId,
        value: douyinId
    }))
})

// 切换筛选选项显示
const toggleFilterOptions = (e) => {
    e.stopPropagation()
    showFilterOptions.value = !showFilterOptions.value
}

// 选择筛选条件
const selectFilter = (type) => {
    filterType.value = type
    showFilterOptions.value = false
}

// 处理键盘事件：Enter发送，Alt+Enter换行
const handleKeydown = (e) => {
    if (e.key === 'Enter') {
        if (e.altKey) {
            // Alt+Enter：换行
            e.preventDefault()
            const cursorPos = e.target.selectionStart
            const value = inputMessage.value
            inputMessage.value = value.substring(0, cursorPos) + '\n' + value.substring(cursorPos)
            nextTick(() => {
                e.target.selectionStart = e.target.selectionEnd = cursorPos + 1
            })
        } else if (!e.shiftKey) {
            // Enter（无修饰键）：发送消息
            e.preventDefault()
            sendMessage()
        }
    }
}

// 模拟数据 - 增加时间变化
const mockSessions = () => {
    const now = new Date()
    const sessions = []
    const timeOptions = [
        { label: '刚刚', value: 0 },
        { label: '1小时前', value: 1 },
        { label: '3小时前', value: 3 },
        { label: '昨天', value: 24 },
        { label: '2天前', value: 48 },
        { label: '3天前', value: 72 },
        { label: '一周前', value: 168 }
    ]
    
    for (let i = 1; i <= 15; i++) {
        const timeOption = timeOptions[i % 7]
        const time = new Date(now.getTime() - timeOption.value * 60 * 60 * 1000)
        
        sessions.push({
            id: i,
            avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
            nick: i % 3 === 0 ? '非常长的昵称需要省略号显示测试' : ['Gina商业思维', '修星星嘻嘻', '爱喝Lemon'][i % 3],
            douyinId: `DouMaster${i}`,
            lastMsg: '您好，抖音代运营服务单平台价格为980元，支持多主流平台，包含内容策划、视频制作、数据分析等一站式服务。如果您需要更详细的信息，可以随时联系我们的客服。',
            time: time.toISOString(),
            isLead: i % 4 === 0
        })
    }
    return sessions
}

// 模拟聊天消息
const mockMessages = () => [
    { 
        type: 'received', 
        content: '您好，我想咨询一下抖音代运营的服务', 
        time: '2025-11-04 10:30:24' 
    },
    { 
        type: 'sent', 
        content: '您好，我们提供专业的抖音代运营服务，包括内容策划、视频制作、数据分析等', 
        time: '2025-11-04 10:32:15' 
    },
    { 
        type: 'received', 
        content: '价格大概是多少？有哪些套餐可以选择？', 
        time: '2025-11-04 10:33:47' 
    },
    { 
        type: 'sent', 
        content: '我们有基础套餐980元/月，专业套餐1980元/月，企业套餐3980元/月，可以根据您的需求选择', 
        time: '2025-11-04 10:35:32' 
    },
    { 
        type: 'received', 
        content: '好的，我先考虑一下，有需要再联系您', 
        time: '2025-11-04 10:40:18' 
    },
    { 
        type: 'sent', 
        content: '好的，有任何问题随时联系我，我们提供7天免费咨询', 
        time: '2025-11-04 10:42:05' 
    }
]

// 格式化时间显示
const formatTime = (timeString) => {
    const time = new Date(timeString)
    const now = new Date()
    const diff = now - time
    
    const minute = 60 * 1000
    const hour = 60 * minute
    const day = 24 * hour
    const week = 7 * day
    
    if (diff < minute) return '刚刚'
    if (diff < hour) return `${Math.floor(diff / minute)}分钟前`
    if (diff < day) return `${Math.floor(diff / hour)}小时前`
    if (diff < week) return `${Math.floor(diff / day)}天前`
    return `${Math.floor(diff / week)}周前`
}

// 筛选后的会话列表
const filteredSessions = computed(() => {
    let filtered = sessionList.value
    
    // 按抖音号筛选
    if (selectedDouyin.value) {
        filtered = filtered.filter(session => session.douyinId === selectedDouyin.value)
    }
    
    // 按留资状态筛选
    if (filterType.value === 'lead') {
        filtered = filtered.filter(session => session.isLead)
    } else if (filterType.value === 'no-lead') {
        filtered = filtered.filter(session => !session.isLead)
    }
    
    return filtered
})

// 选择会话
const selectSession = (session) => {
    activeSessionId.value = session.id
    activeSession.value = session
    messages.value = mockMessages()
    
    // 滚动到底部
    nextTick(() => {
        if (messageListRef.value) {
            messageListRef.value.scrollTop = messageListRef.value.scrollHeight
        }
    })
}

// 发送消息
const sendMessage = () => {
    if (!inputMessage.value.trim()) return
    
    const now = new Date()
    const year = now.getFullYear()
    const month = String(now.getMonth() + 1).padStart(2, '0')
    const day = String(now.getDate()).padStart(2, '0')
    const hours = String(now.getHours()).padStart(2, '0')
    const minutes = String(now.getMinutes()).padStart(2, '0')
    const seconds = String(now.getSeconds()).padStart(2, '0')
    const timeStr = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    
    messages.value.push({
        type: 'sent',
        content: inputMessage.value,
        time: timeStr
    })
    
    inputMessage.value = ''
    
    // 滚动到底部
    nextTick(() => {
        if (messageListRef.value) {
            messageListRef.value.scrollTop = messageListRef.value.scrollHeight
        }
    })
}

// 刷新
const handleRefresh = () => {
    loadSession()
    selectedDouyin.value = ''
    filterType.value = 'all'
    showFilterOptions.value = false
    activeSessionId.value = null
    activeSession.value = null
    messages.value = []
}

// 加载会话
const loadSession = () => {
    sessionList.value = mockSessions()
    total.value = 2000
}

onMounted(() => {
    loadSession()
    
    // 点击页面其他区域关闭筛选选项
    document.addEventListener('click', () => {
        showFilterOptions.value = false
    })
})
</script>

<style scoped lang="scss">
.private-msg {
    background: #fff;
    height: calc(90vh - 60px);
    display: flex;
    flex-direction: column;
    margin: 0 30px;
}

.main-body {
    flex: 1;
    display: flex;
    height: 100%;
    gap: 16px;
    overflow: hidden;
}

// 左侧会话面板
.session-panel {
    width: 380px;
    display: flex;
    flex-direction: column;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    background: #fff;
    overflow: hidden;
}

.filter-section {
    padding: 16px;
    border-bottom: 1px solid #f0f0f0;
    background: #fafafa;
}

.filter-label-row {
    font-size: 14px;
    color: #606266;
    margin-bottom: 12px;
    font-weight: 500;
}

.search-row {
    margin-bottom: 12px;
    
    .filter-input {
        width: 100%;
        
        :deep(.el-input__wrapper) {
            background: #f0f2f5;
            border-radius: 6px;
            box-shadow: none;
            border: 1px solid #e4e7ed;
            
            &:hover {
                border-color: #c0c4cc;
                background: #e8e9ed;
            }
            
            &.is-focus {
                border-color: #409eff;
                background: #ffffff;
                box-shadow: 0 0 0 1px #409eff inset;
            }
        }
        
        :deep(.el-input__inner) {
            color: #303133;
        }
        
        :deep(.el-icon) {
            color: #909399;
        }
    }
}

.filter-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.filter-all-wrapper {
    position: relative;
    flex: 1;
}

.filter-all-text {
    display: inline-flex;
    align-items: center;
    padding: 0;
    height: auto;
    background: transparent;
    cursor: pointer;
    color: #606266;
    font-size: 14px;
    transition: all 0.3s;
    
    span {
        margin-right: 4px;
    }
    
    &:hover {
        color: #409eff;
    }
}

.arrow-icon {
    font-size: 12px;
    transition: transform 0.3s;
    margin-left: 2px;
    
    &.rotate {
        transform: rotate(180deg);
    }
}

.filter-options {
    position: absolute;
    top: 100%;
    left: 0;
    background: #fff;
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    z-index: 10;
    margin-top: 4px;
    min-width: 100px;
}

.filter-option {
    padding: 8px 12px;
    font-size: 14px;
    color: #606266;
    cursor: pointer;
    transition: all 0.3s;
    white-space: nowrap;
    
    &:hover {
        background: #f5f7fa;
        color: #409eff;
    }
    
    &.active {
        color: #409eff;
        background: #ecf5ff;
    }
}

.refresh-btn {
    padding: 8px;
    
    .el-icon {
        font-size: 18px;
        color: #303133;
    }
    
    &:hover {
        .el-icon {
            color: #409eff;
        }
    }
}

.session-list {
    flex: 1;
    overflow-y: auto;
    scrollbar-width: thin;
    scrollbar-color: #409eff #f5f5f5;
    
    &::-webkit-scrollbar {
        width: 6px;
    }
    
    &::-webkit-scrollbar-track {
        background: #f5f5f5;
        border-radius: 3px;
    }
    
    &::-webkit-scrollbar-thumb {
        background: #409eff;
        border-radius: 3px;
    }
}

.session-item {
    display: flex;
    padding: 12px 16px;
    cursor: pointer;
    border-bottom: 1px solid #f0f0f0;
    transition: all 0.3s;
    
    &:hover {
        background: #f5f7fa;
    }
    
    &.active {
        background: #ecf5ff;
        
        .nickname {
            color: #409eff;
        }
    }
}

.session-avatar {
    margin-right: 12px;
}

.session-content {
    flex: 1;
    min-width: 0;
}

.session-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 4px;
}

.header-left {
    display: flex;
    align-items: center;
    gap: 6px;
    min-width: 0;
}

.nickname {
    font-weight: 500;
    color: #303133;
    font-size: 14px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 120px;
}

.lead-tag {
    flex-shrink: 0;
}

.time {
    font-size: 12px;
    color: #909399;
    flex-shrink: 0;
    margin-left: 8px;
}

.session-preview {
    font-size: 12px;
    color: #606266;
    line-height: 1.4;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
    min-height: 28px;
}

.session-footer {
    padding: 12px 16px;
    text-align: center;
    font-size: 12px;
    color: #909399;
    border-top: 1px solid #f0f0f0;
    background: #fafafa;
}

// 中间聊天面板 - 添加边框
.chat-panel {
    flex: 2;
    display: flex;
    flex-direction: column;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    background: #fff;
    overflow: hidden;
}

.chat-container {
    display: flex;
    flex-direction: column;
    height: 100%;
}

.message-list {
    flex: 1;
    padding: 20px;
    overflow-y: auto;
    scrollbar-width: thin;
    scrollbar-color: #409eff #f5f5f5;
    
    &::-webkit-scrollbar {
        width: 6px;
    }
    
    &::-webkit-scrollbar-track {
        background: #f5f5f5;
        border-radius: 3px;
    }
    
    &::-webkit-scrollbar-thumb {
        background: #409eff;
        border-radius: 3px;
    }
}

.message-item {
    margin-bottom: 24px;
}

.message-received {
    display: flex;
    align-items: flex-start;
}

.message-sent {
    display: flex;
    align-items: flex-start;
    justify-content: flex-end;
}

.message-avatar {
    flex-shrink: 0;
}

.message-received .message-avatar {
    margin-right: 12px;
}

.message-sent .message-avatar {
    margin-left: 12px;
    order: 2;
}

.message-content-wrapper {
    flex: 1;
    max-width: 80%;
}

// 接收消息的昵称和时间样式
.message-nickname {
    font-size: 13px;
    font-weight: 500;
    color: #606266;
    margin-bottom: 2px;
    text-align: left;
}

.message-time {
    font-size: 11px;
    color: #909399;
    margin-bottom: 6px;
    text-align: left;
}

// 发送消息的昵称和时间样式 - 独占一行且在右侧
.message-nickname-sent {
    font-size: 13px;
    font-weight: 500;
    color: #606266;
    margin-bottom: 2px;
    text-align: right;
    width: 100%;
    display: block;
}

.message-time-sent {
    font-size: 11px;
    color: #909399;
    margin-bottom: 6px;
    text-align: right;
    width: 100%;
    display: block;
}

.message-content {
    padding: 12px 16px;
    border-radius: 8px;
    word-break: break-word;
}

.message-received .message-content {
    background: #f0f7ff;
    border-top-left-radius: 2px;
}

.message-sent .message-content {
    background: #ecf5ff;
    border-top-right-radius: 2px;
}

.message-text {
    font-size: 14px;
    color: #303133;
    line-height: 1.5;
}

.chat-input {
    padding: 20px;
    border-top: 1px solid #f0f0f0;
    background: #fff;
}

.reply-input {
    margin-bottom: 12px;
    
    :deep(.el-textarea__inner) {
        font-size: 14px;
        line-height: 1.5;
        box-shadow: none !important;
        border: none !important;
        
        &:focus {
            box-shadow: none !important;
            border: none !important;
        }
    }
    
    :deep(.el-input__wrapper) {
        box-shadow: none !important;
        border: none !important;
        padding: 0 !important;
        
        &.is-focus {
            box-shadow: none !important;
            border: none !important;
        }
    }
}

.input-actions {
    display: flex;
    justify-content: flex-end;
}

.send-btn {
    background-color: #f0f2f5;
    border-color: #dcdfe6;
    color: #606266;
    font-size: 14px;
    border-radius: 4px;
    padding: 8px 16px;
    
    &:hover {
        background-color: #e4e6eb;
        border-color: #c0c4cc;
        color: #303133;
    }
    
    &:active {
        background-color: #d4d6db;
    }
}

.empty-chat {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
}

// 右侧线索面板
.clue-panel {
    width: 320px;
    display: flex;
    flex-direction: column;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    background: #fff;
    overflow: hidden;
}

.clue-container {
    display: flex;
    flex-direction: column;
    height: 100%;
}

.clue-header {
    padding: 20px;
    border-bottom: 1px solid #f0f0f0;
    background: #fafafa;
}

.clue-user {
    display: flex;
    align-items: center;
    gap: 12px;
}

.clue-user-info {
    .clue-nickname {
        font-weight: 500;
        color: #303133;
        font-size: 16px;
    }
}

.clue-content {
    flex: 1;
    overflow-y: auto;
    scrollbar-width: thin;
    scrollbar-color: #409eff #f5f5f5;
    
    &::-webkit-scrollbar {
        width: 6px;
    }
    
    &::-webkit-scrollbar-track {
        background: #f5f5f5;
        border-radius: 3px;
    }
    
    &::-webkit-scrollbar-thumb {
        background: #409eff;
        border-radius: 3px;
    }
}

.clue-title {
    padding: 16px 20px 12px;
    font-size: 14px;
    font-weight: 500;
    color: #303133;
    border-bottom: 1px solid #f0f0f0;
}

.clue-list {
    padding: 16px 20px;
}

.clue-item {
    margin-bottom: 16px;
    
    &:last-child {
        margin-bottom: 0;
    }
}

.clue-item-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 6px;
}

.clue-label {
    font-size: 14px;
    font-weight: 500;
    color: #303133;
}

.clue-time {
    font-size: 12px;
    color: #909399;
}

.clue-value {
    font-size: 14px;
    color: #606266;
    padding: 8px 12px;
    background: #f5f7fa;
    border-radius: 4px;
    word-break: break-all;
}

.empty-clue {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
}
</style>