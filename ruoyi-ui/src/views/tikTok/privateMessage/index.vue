<template>
    <div class="private-message-container">
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
                                v-model="selectedAccount"
                                placeholder="筛选抖音号"
                                class="filter-input"
                                filterable
                                clearable
                                @change="handleAccountFilter"
                                :loading="loading.accounts"
                            >
                                <el-option 
                                    v-for="item in accountOptions" 
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
                                :loading="loading.refresh"
                            >
                                <el-icon v-if="!loading.refresh"><Refresh /></el-icon>
                            </el-button>
                        </div>
                    </div>

                    <!-- 会话列表 -->
                    <div class="session-list">
                        <template v-if="loading.sessions">
                            <div class="loading-sessions">
                                <el-skeleton v-for="i in 5" :key="i" animated class="session-skeleton">
                                    <template #template>
                                        <el-skeleton-item variant="circle" style="width: 40px; height: 40px;" />
                                        <div style="margin-left: 12px; flex: 1;">
                                            <el-skeleton-item variant="text" style="width: 50%;" />
                                            <el-skeleton-item variant="text" style="width: 80%; margin-top: 8px;" />
                                        </div>
                                    </template>
                                </el-skeleton>
                            </div>
                        </template>
                        <template v-else>
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
                                                v-if="item.hasFunds" 
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
                                        <span class="time">{{ formatTime(item.lastMsgTime) }}</span>
                                    </div>
                                    <div class="session-preview">
                                        {{ item.lastMsgContent }}
                                    </div>
                                </div>
                            </div>
                        </template>
                    </div>

                    <!-- 底部信息 -->
                    <div class="session-footer">
                        <template v-if="loading.sessions">
                            加载中...
                        </template>
                        <template v-else>
                            已拉取72小时内上线{{ total }}条会话
                        </template>
                    </div>
                </div>

                <!-- 中间聊天区域 -->
                <div class="chat-panel">
                    <div v-if="activeSession" class="chat-container">
                        <!-- 聊天消息区域 -->
                        <div class="message-list" ref="messageListRef">
                            <template v-if="loading.messages">
                                <div class="loading-messages">
                                    <el-skeleton v-for="i in 3" :key="i" animated class="message-skeleton">
                                        <template #template>
                                            <el-skeleton-item variant="circle" style="width: 32px; height: 32px;" />
                                            <div style="margin-left: 12px; flex: 1;">
                                                <el-skeleton-item variant="text" style="width: 30%;" />
                                                <el-skeleton-item variant="text" style="width: 80%; margin-top: 8px;" />
                                            </div>
                                        </template>
                                    </el-skeleton>
                                </div>
                            </template>
                            <template v-else>
                                <div 
                                    v-for="(msg, index) in messages" 
                                    :key="index"
                                    :class="['message-item', msg.senderId === activeSession.userId ? 'received' : 'sent']"
                                >
                                    <!-- 接收的消息 -->
                                    <div v-if="msg.senderId === activeSession.userId" class="message-received">
                                        <div class="message-avatar">
                                            <el-avatar :size="32" :src="activeSession.avatar" />
                                        </div>
                                        <div class="message-content-wrapper">
                                            <div class="message-nickname">{{ activeSession.nick }}</div>
                                            <div class="message-time">{{ formatDateTime(msg.sendTime) }}</div>
                                            <div class="message-content">
                                                <div class="message-text">{{ msg.msgContent }}</div>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <!-- 发送的消息 -->
                                    <div v-if="msg.senderId !== activeSession.userId" class="message-sent">
                                        <div class="message-content-wrapper">
                                            <div class="message-nickname-sent">我</div>
                                            <div class="message-time-sent">{{ formatDateTime(msg.sendTime) }}</div>
                                            <div class="message-content">
                                                <div class="message-text">{{ msg.msgContent }}</div>
                                            </div>
                                        </div>
                                        <div class="message-avatar">
                                            <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
                                        </div>
                                    </div>
                                </div>
                            </template>
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
                                :disabled="!activeSession"
                            />
                            <div class="input-actions">
                                <el-button 
                                    type="default" 
                                    class="send-btn"
                                    @click="sendMessage"
                                    :disabled="!activeSession || loading.send"
                                    :loading="loading.send"
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
                                <template v-if="loading.clues">
                                    <div class="loading-clues">
                                        <el-skeleton v-for="i in 3" :key="i" animated class="clue-skeleton">
                                            <template #template>
                                                <el-skeleton-item variant="text" style="width: 100%; height: 60px;" />
                                            </template>
                                        </el-skeleton>
                                    </div>
                                </template>
                                <template v-else>
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
                                </template>
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
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'

// 导入项目中已有的 request 方法
import request from '@/utils/request'

// 加载状态
const loading = reactive({
    accounts: false,
    sessions: false,
    messages: false,
    clues: false,
    send: false,
    refresh: false
})

// 抖音号选择
const selectedAccount = ref('')
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

// 抖音号选项
const accountOptions = ref([])

// 聊天消息
const messages = ref([])
const inputMessage = ref('')
const messageListRef = ref(null)

// 线索数据
const clues = ref([])

// 切换筛选选项显示
const toggleFilterOptions = (e) => {
    e.stopPropagation()
    showFilterOptions.value = !showFilterOptions.value
}

// 选择筛选条件
const selectFilter = (type) => {
    filterType.value = type
    showFilterOptions.value = false
    // 重新加载会话列表
    loadSessions()
}

// 处理抖音号筛选
const handleAccountFilter = () => {
    loadSessions()
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

// 格式化时间显示（相对时间）
const formatTime = (timeString) => {
    if (!timeString) return ''
    
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

// 格式化日期时间显示
const formatDateTime = (timeString) => {
    if (!timeString) return ''
    
    const time = new Date(timeString)
    const year = time.getFullYear()
    const month = String(time.getMonth() + 1).padStart(2, '0')
    const day = String(time.getDate()).padStart(2, '0')
    const hours = String(time.getHours()).padStart(2, '0')
    const minutes = String(time.getMinutes()).padStart(2, '0')
    const seconds = String(time.getSeconds()).padStart(2, '0')
    
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 选择会话
const selectSession = async (session) => {
    activeSessionId.value = session.id
    activeSession.value = session
    
    // 加载消息
    await loadMessages(session.id)
    
    // 加载线索 - 使用对方用户ID
    if (session.userId) {
        await loadClues(session.userId)
    }
    
    // 滚动到底部
    nextTick(() => {
        if (messageListRef.value) {
            messageListRef.value.scrollTop = messageListRef.value.scrollHeight
        }
    })
}

// 发送消息
const sendMessage = async () => {
    if (!inputMessage.value.trim() || !activeSession.value) return
    
    loading.send = true
    
    try {
        // 发送私信消息
        const response = await request({
            url: '/privateChat/private_chat/send',
            method: 'POST',
            data: {
                chatId: activeSession.value.id,
                receiverId: activeSession.value.userId,
                msgType: 0, // 文本消息
                msgContent: inputMessage.value.trim()
            }
        })
        
        if (response.code === 200) {
            // 添加发送的消息到列表
            const now = new Date()
            messages.value.push({
                senderId: null, // 我方ID，需要根据实际情况获取
                msgContent: inputMessage.value.trim(),
                sendTime: now.toISOString(),
                msgType: 0
            })
            
            inputMessage.value = ''
            
            // 更新会话的最后一条消息
            if (sessionList.value) {
                const sessionIndex = sessionList.value.findIndex(s => s.id === activeSession.value.id)
                if (sessionIndex !== -1) {
                    sessionList.value[sessionIndex].lastMsgContent = inputMessage.value.trim()
                    sessionList.value[sessionIndex].lastMsgTime = now.toISOString()
                }
            }
            
            // 滚动到底部
            nextTick(() => {
                if (messageListRef.value) {
                    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
                }
            })
            
            ElMessage.success('发送成功')
        } else {
            ElMessage.error(response.msg || '发送失败')
        }
    } catch (error) {
        console.error('发送消息失败:', error)
        ElMessage.error('发送失败，请重试')
    } finally {
        loading.send = false
    }
}

// 刷新
const handleRefresh = async () => {
    loading.refresh = true
    
    // 重新加载所有数据
    await loadAccounts()
    await loadSessions()
    
    selectedAccount.value = ''
    filterType.value = 'all'
    showFilterOptions.value = false
    activeSessionId.value = null
    activeSession.value = null
    messages.value = []
    clues.value = []
    
    loading.refresh = false
}

// 加载抖音号列表
const loadAccounts = async () => {
    loading.accounts = true
    
    try {
        const response = await request({
            url: '/privateChat/private_chat/accounts',
            method: 'GET'
        })
        
        if (response.code === 200) {
            // 根据API响应格式调整
            const accounts = response.data || []
            accountOptions.value = accounts.map(account => ({
                label: account || '未知账号',
                value: account
            }))
        }
    } catch (error) {
        console.error('加载抖音号列表失败:', error)
        ElMessage.error('加载抖音号列表失败')
        
        // 开发环境使用模拟数据
        if (process.env.NODE_ENV === 'development') {
            accountOptions.value = [
                { label: '抖音账号1', value: 'account1' },
                { label: '抖音账号2', value: 'account2' },
                { label: '抖音账号3', value: 'account3' }
            ]
        }
    } finally {
        loading.accounts = false
    }
}

// 加载会话列表
const loadSessions = async () => {
    loading.sessions = true
    
    try {
        const params = {}
        
        // 添加筛选参数
        if (selectedAccount.value) {
            params.account = selectedAccount.value
        }
        
        if (filterType.value === 'lead') {
            params.funds = 1
        } else if (filterType.value === 'no-lead') {
            params.funds = 0
        }
        
        const response = await request({
            url: '/privateChat/private_chat/sessions',
            method: 'GET',
            params
        })
        
        if (response.code === 200) {
            // 根据API响应格式调整
            const sessions = response.data || []
            sessionList.value = sessions.map(session => ({
                id: session.id,
                userId: session.userId || session.commentUserId,
                avatar: session.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
                nick: session.peerAccount || session.nick || '未知用户',
                lastMsgContent: session.lastMsg || session.lastMsgContent || '暂无消息',
                lastMsgTime: session.lastMsgTime || session.time,
                hasFunds: session.hasFunds || session.funds === 1,
                account: session.account
            }))
            
            total.value = sessionList.value.length
        }
    } catch (error) {
        console.error('加载会话列表失败:', error)
        ElMessage.error('加载会话列表失败')
        
        // 开发环境使用模拟数据
        if (process.env.NODE_ENV === 'development') {
            sessionList.value = mockSessions()
            total.value = sessionList.value.length
        }
    } finally {
        loading.sessions = false
    }
}

// 加载消息
const loadMessages = async (sessionId) => {
    loading.messages = true
    
    try {
        const response = await request({
            url: '/privateChat/private_chat/messages',
            method: 'GET',
            params: { sessionId }
        })
        
        if (response.code === 200) {
            // 根据API响应格式调整
            messages.value = response.data || []
        }
    } catch (error) {
        console.error('加载消息失败:', error)
        ElMessage.error('加载消息失败')
        
        // 开发环境使用模拟数据
        if (process.env.NODE_ENV === 'development') {
            messages.value = mockMessages()
        }
    } finally {
        loading.messages = false
    }
}

// 加载线索
const loadClues = async (userId) => {
    loading.clues = true
    
    try {
        const response = await request({
            url: '/privateChat/private_chat/clues',
            method: 'GET',
            params: { userId }
        })
        
        if (response.code === 200) {
            // 根据API响应格式调整
            const cluesData = response.data || []
            clues.value = cluesData.map(clue => ({
                label: clue.fieldName || clue.label,
                value: clue.fieldValue || clue.value,
                time: clue.createTime || clue.time
            }))
        }
    } catch (error) {
        console.error('加载线索失败:', error)
        ElMessage.error('加载线索失败')
        
        // 开发环境使用模拟数据
        if (process.env.NODE_ENV === 'development') {
            clues.value = [
                { label: '手机号', value: '138****5678', time: '2025-11-04' },
                { label: '微信号', value: 'wx_abc123', time: '2025-11-03' },
                { label: '邮箱', value: 'user@example.com', time: '2025-11-02' },
                { label: '咨询产品', value: '抖音代运营服务', time: '2025-11-01' },
                { label: '预算范围', value: '1000-2000元', time: '2025-10-31' }
            ]
        }
    } finally {
        loading.clues = false
    }
}

// 筛选后的会话列表
const filteredSessions = computed(() => {
    return sessionList.value
})

// 模拟数据 - 开发环境使用
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
            userId: i,
            avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
            nick: i % 3 === 0 ? '非常长的昵称需要省略号显示测试' : ['Gina商业思维', '修星星嘻嘻', '爱喝Lemon'][i % 3],
            lastMsgContent: '您好，抖音代运营服务单平台价格为980元，支持多主流平台，包含内容策划、视频制作、数据分析等一站式服务。如果您需要更详细的信息，可以随时联系我们的客服。',
            lastMsgTime: time.toISOString(),
            hasFunds: i % 4 === 0,
            account: `account${i}`
        })
    }
    return sessions
}

// 模拟聊天消息 - 开发环境使用
const mockMessages = () => {
    const now = new Date()
    return [
        { 
            senderId: 1, 
            msgContent: '您好，我想咨询一下抖音代运营的服务', 
            sendTime: new Date(now.getTime() - 30 * 60 * 1000).toISOString(),
            msgType: 0
        },
        { 
            senderId: null, // 我方消息
            msgContent: '您好，我们提供专业的抖音代运营服务，包括内容策划、视频制作、数据分析等', 
            sendTime: new Date(now.getTime() - 28 * 60 * 1000).toISOString(),
            msgType: 0
        },
        { 
            senderId: 1, 
            msgContent: '价格大概是多少？有哪些套餐可以选择？', 
            sendTime: new Date(now.getTime() - 26 * 60 * 1000).toISOString(),
            msgType: 0
        }
    ]
}

onMounted(async () => {
    // 加载抖音号列表
    await loadAccounts()
    
    // 加载会话列表
    await loadSessions()
    
    // 点击页面其他区域关闭筛选选项
    document.addEventListener('click', () => {
        showFilterOptions.value = false
    })
})
</script>

<style scoped lang="scss">
.private-message-container {
    height: 100%;
}

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

.loading-sessions {
    padding: 12px 16px;
}

.session-skeleton {
    display: flex;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
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

// 中间聊天面板
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

.loading-messages {
    display: flex;
    flex-direction: column;
    gap: 24px;
}

.message-skeleton {
    display: flex;
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
    
    &:disabled {
        background-color: #f5f7fa;
        border-color: #e4e7ed;
        color: #c0c4cc;
        cursor: not-allowed;
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

.loading-clues {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.clue-skeleton {
    width: 100%;
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