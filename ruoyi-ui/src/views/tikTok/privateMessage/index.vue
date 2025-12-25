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
                                    :key="item.account"
                                    :label="item.account"
                                    :value="item.account"
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
                                v-if="filteredSessions.length === 0"
                                class="empty-sessions"
                            >
                                <el-empty description="暂无会话" :image-size="60" />
                            </div>
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
                                            <span class="nickname">{{ item.nick || '未知用户' }}</span>
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
                                        <span class="time">{{ formatTime(item.lastMsgTime) }}</span>
                                    </div>
                                    <div class="session-preview">
                                        {{ item.lastMsgContent || '暂无消息' }}
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
                            已加载 {{ filteredSessions.length }} 条会话
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
                                <div v-if="messages.length === 0" class="empty-messages">
                                    <el-empty description="暂无消息" :image-size="50" />
                                </div>
                                <div 
                                    v-for="(msg, index) in messages" 
                                    :key="index"
                                    :class="['message-item', msg.senderType === 'peer' ? 'received' : 'sent']"
                                >
                                    <!-- 接收的消息 -->
                                    <div v-if="msg.senderType === 'peer'" class="message-received">
                                        <div class="message-avatar">
                                            <el-avatar :size="32" :src="activeSession.avatar" />
                                        </div>
                                        <div class="message-content-wrapper">
                                            <div class="message-nickname">{{ activeSession.nick || '对方' }}</div>
                                            <div class="message-time">{{ formatDateTime(msg.sendTime) }}</div>
                                            <div class="message-content">
                                                <div class="message-text">{{ msg.msgContent }}</div>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <!-- 发送的消息 -->
                                    <div v-if="msg.senderType === 'self'" class="message-sent">
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
                                placeholder="回复内容，可按Enter键发送，按Alt+Enter换行"
                                resize="none"
                                class="reply-input"
                                @keydown.enter="handleKeydown"
                                :disabled="!activeSession"
                            />
                            <div class="input-actions">
                                <el-button 
                                    type="primary" 
                                    class="send-btn"
                                    @click="sendMessage"
                                    :disabled="!activeSession || loading.send || !inputMessage.trim()"
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
                                    <div class="clue-nickname">{{ activeSession.nick || '未知用户' }}</div>
                                    <div class="clue-account" v-if="activeSession.account">
                                        {{ activeSession.account }}
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- 线索内容 -->
                        <div class="clue-content">
                            <div class="clue-title">用户信息</div>
                            <div class="clue-list">
                                <div class="clue-item">
                                    <div class="clue-item-header">
                                        <span class="clue-label">用户ID</span>
                                    </div>
                                    <div class="clue-value">{{ activeSession.userId || '未知' }}</div>
                                </div>
                                <div class="clue-item">
                                    <div class="clue-item-header">
                                        <span class="clue-label">留资状态</span>
                                    </div>
                                    <div class="clue-value">
                                        <el-tag 
                                            v-if="activeSession.isLead" 
                                            size="small" 
                                            type="success"
                                        >已留资</el-tag>
                                        <el-tag 
                                            v-else 
                                            size="small" 
                                            type="info"
                                        >未留资</el-tag>
                                    </div>
                                </div>
                                <div class="clue-item">
                                    <div class="clue-item-header">
                                        <span class="clue-label">最后活跃</span>
                                    </div>
                                    <div class="clue-value">{{ formatDateTime(activeSession.lastMsgTime) }}</div>
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
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

// 加载状态
const loading = reactive({
    accounts: false,
    sessions: false,
    messages: false,
    send: false,
    refresh: false
})

// 筛选条件
const selectedAccount = ref('')
const filterType = ref('all')
const showFilterOptions = ref(false)

// 会话相关
const sessionList = ref([])
const activeSessionId = ref(null)
const activeSession = ref(null)

// 抖音号选项（包含AI配置ID）
const accountOptions = ref([])

// 聊天消息
const messages = ref([])
const inputMessage = ref('')
const messageListRef = ref(null)

// 切换筛选选项显示
const toggleFilterOptions = (e) => {
    e.stopPropagation()
    showFilterOptions.value = !showFilterOptions.value
}

// 选择筛选条件
const selectFilter = (type) => {
    filterType.value = type
    showFilterOptions.value = false
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
    
    try {
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
        return `${time.getMonth() + 1}月${time.getDate()}日`
    } catch (e) {
        return timeString
    }
}

// 格式化日期时间显示
const formatDateTime = (timeString) => {
    if (!timeString) return ''
    
    try {
        const time = new Date(timeString)
        const now = new Date()
        const isToday = time.toDateString() === now.toDateString()
        
        if (isToday) {
            const hours = String(time.getHours()).padStart(2, '0')
            const minutes = String(time.getMinutes()).padStart(2, '0')
            const seconds = String(time.getSeconds()).padStart(2, '0')
            return `今天 ${hours}:${minutes}:${seconds}`
        } else {
            const month = String(time.getMonth() + 1).padStart(2, '0')
            const day = String(time.getDate()).padStart(2, '0')
            const hours = String(time.getHours()).padStart(2, '0')
            const minutes = String(time.getMinutes()).padStart(2, '0')
            return `${month}-${day} ${hours}:${minutes}`
        }
    } catch (e) {
        return timeString
    }
}

// 选择会话
const selectSession = async (session) => {
    activeSessionId.value = session.id
    activeSession.value = session
    messages.value = []
    
    // 加载消息
    await loadMessages(session.id)
    
    // 滚动到底部
    nextTick(() => {
        scrollToBottom()
    })
}

// 发送消息
const sendMessage = async () => {
    if (!inputMessage.value.trim() || !activeSession.value) return
    
    loading.send = true
    
    try {
        // 注意：API文档中没有发送消息的接口
        // 这里模拟发送，实际对接时需要根据实际接口调整
        const now = new Date()
        const newMessage = {
            senderType: 'self',
            msgContent: inputMessage.value.trim(),
            sendTime: now.toISOString()
        }
        
        // 添加到消息列表
        messages.value.push(newMessage)
        
        // 更新会话的最后一条消息
        const sessionIndex = sessionList.value.findIndex(s => s.id === activeSession.value.id)
        if (sessionIndex !== -1) {
            sessionList.value[sessionIndex].lastMsgContent = inputMessage.value.trim()
            sessionList.value[sessionIndex].lastMsgTime = now.toISOString()
        }
        
        inputMessage.value = ''
        
        // 滚动到底部
        nextTick(() => {
            scrollToBottom()
        })
        
        ElMessage.success('发送成功')
        
        // 如果需要实际发送到服务器，可以在这里调用API
        // await request({
        //     url: '/privateChat/private_chat/send',
        //     method: 'POST',
        //     data: {
        //         sessionId: activeSession.value.id,
        //         message: inputMessage.value.trim()
        //     }
        // })
        
    } catch (error) {
        console.error('发送消息失败:', error)
        ElMessage.error('发送失败，请重试')
    } finally {
        loading.send = false
    }
}

// 滚动到底部
const scrollToBottom = () => {
    if (messageListRef.value) {
        messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
}

// 刷新
const handleRefresh = async () => {
    loading.refresh = true
    
    try {
        await loadAccounts()
        await loadSessions()
        
        selectedAccount.value = ''
        filterType.value = 'all'
        showFilterOptions.value = false
        activeSessionId.value = null
        activeSession.value = null
        messages.value = []
        
        ElMessage.success('刷新成功')
    } catch (error) {
        console.error('刷新失败:', error)
        ElMessage.error('刷新失败')
    } finally {
        loading.refresh = false
    }
}

// 加载抖音号列表（附带AI配置ID）
const loadAccounts = async () => {
    loading.accounts = true
    
    try {
        const response = await request({
            url: '/privateChat/private_chat/accounts',
            method: 'GET'
        })
        
        if (response.code === 200) {
            // 根据API文档，返回的数据结构可能是 { "key": {} }
            // 这里假设实际数据在 data 字段中
            let accounts = response.data || []
            
            // 如果数据结构是 { "key": {} }，尝试提取
            if (typeof response.data === 'object' && !Array.isArray(response.data)) {
                accounts = Object.values(response.data)
            }
            
            accountOptions.value = accounts.map(account => ({
                account: account || '未知账号',
                ...account
            }))
        } else {
            ElMessage.error(response.msg || '加载抖音号列表失败')
        }
    } catch (error) {
        console.error('加载抖音号列表失败:', error)
        ElMessage.error('加载抖音号列表失败')
    } finally {
        loading.accounts = false
    }
}

// 加载会话列表
const loadSessions = async () => {
    loading.sessions = true
    
    try {
        const params = {}
        
        // 根据API文档添加参数
        if (selectedAccount.value) {
            // 查找对应的AI配置ID
            const accountInfo = accountOptions.value.find(acc => acc.account === selectedAccount.value)
            if (accountInfo && accountInfo.aiConfigId) {
                params.expirationAiId = accountInfo.aiConfigId
            }
        }
        
        // 是否留资参数
        if (filterType.value === 'lead') {
            params.isLead = 1
        } else if (filterType.value === 'no-lead') {
            params.isLead = 0
        }
        // all 时不传 isLead 参数
        
        const response = await request({
            url: '/privateChat/private_chat/sessions',
            method: 'GET',
            params
        })
        
        if (response.code === 200) {
            let sessions = response.data || []
            
            // 处理数据结构
            if (typeof response.data === 'object' && !Array.isArray(response.data)) {
                sessions = Object.values(response.data)
            }
            
            sessionList.value = sessions.map(session => ({
                id: session.id,
                userId: session.peerUserId || session.peerId,
                avatar: session.peerAvatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
                nick: session.peerNickname  || '未知用户',
                account: session.peerAccount,
                lastMsgContent: session.lastMsgContent,
                lastMsgTime: session.lastSendTime,
                isLead: session.isBlocked,
                ...session
            }))
        } else {
            ElMessage.error(response.msg || '加载会话列表失败')
            sessionList.value = []
        }
    } catch (error) {
        console.error('加载会话列表失败:', error)
        ElMessage.error('加载会话列表失败')
        sessionList.value = []
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
            let messagesData = response.data || []
            
            // 处理数据结构
            if (typeof response.data === 'object' && !Array.isArray(response.data)) {
                messagesData = Object.values(response.data)
            }
            
            messages.value = messagesData.map(msg => ({
                id: msg.id || msg.msgId,
                senderType: msg.senderType || (msg.senderId === activeSession.value?.userId ? 'peer' : 'self'),
                msgContent: msg.content || msg.msgContent || msg.message || '',
                sendTime: msg.sendTime || msg.createTime || msg.timestamp,
                msgType: msg.msgType || msg.type || 0,
                ...msg
            }))
        } else {
            ElMessage.error(response.msg || '加载消息失败')
            messages.value = []
        }
    } catch (error) {
        console.error('加载消息失败:', error)
        ElMessage.error('加载消息失败')
        messages.value = []
    } finally {
        loading.messages = false
    }
}

// 筛选后的会话列表
const filteredSessions = computed(() => {
    let sessions = sessionList.value
    
    // 如果没有选择抖音号，显示所有会话
    // 如果选择了抖音号，根据账号筛选（后端已根据AI配置ID筛选，这里做前端双重保障）
    if (selectedAccount.value) {
        sessions = sessions.filter(session => 
            !session.account || session.account === selectedAccount.value
        )
    }
    
    return sessions
})

// 初始化数据
onMounted(async () => {
    try {
        await loadAccounts()
        await loadSessions()
    } catch (error) {
        console.error('初始化失败:', error)
    }
    
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
    
    &::-webkit-scrollbar {
        width: 6px;
    }
    
    &::-webkit-scrollbar-track {
        background: #f5f5f5;
    }
    
    &::-webkit-scrollbar-thumb {
        background: #c0c4cc;
        border-radius: 3px;
    }
    
    &::-webkit-scrollbar-thumb:hover {
        background: #909399;
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

.empty-sessions {
    padding: 40px 20px;
    text-align: center;
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
    flex-shrink: 0;
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
    flex: 1;
}

.nickname {
    font-weight: 500;
    color: #303133;
    font-size: 14px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    flex: 1;
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
    
    &::-webkit-scrollbar {
        width: 6px;
    }
    
    &::-webkit-scrollbar-track {
        background: #f5f5f5;
    }
    
    &::-webkit-scrollbar-thumb {
        background: #c0c4cc;
        border-radius: 3px;
    }
    
    &::-webkit-scrollbar-thumb:hover {
        background: #909399;
    }
}

.loading-messages {
    display: flex;
    flex-direction: column;
    gap: 24px;
}

.empty-messages {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    min-height: 200px;
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
}

.message-content-wrapper {
    flex: 1;
    max-width: 70%;
}

.message-nickname {
    font-size: 13px;
    color: #606266;
    margin-bottom: 4px;
}

.message-time {
    font-size: 11px;
    color: #909399;
    margin-bottom: 6px;
}

.message-nickname-sent {
    font-size: 13px;
    color: #606266;
    margin-bottom: 4px;
    text-align: right;
}

.message-time-sent {
    font-size: 11px;
    color: #909399;
    margin-bottom: 6px;
    text-align: right;
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
    white-space: pre-wrap;
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
        resize: none;
        
        &:focus {
            box-shadow: none !important;
            border: none !important;
        }
        
        &:disabled {
            background-color: #f5f7fa;
        }
    }
}

.input-actions {
    display: flex;
    justify-content: flex-end;
}

.send-btn {
    background-color: #409eff;
    border-color: #409eff;
    color: #fff;
    font-size: 14px;
    border-radius: 4px;
    padding: 8px 20px;
    
    &:hover {
        background-color: #79bbff;
        border-color: #79bbff;
    }
    
    &:active {
        background-color: #337ecc;
    }
    
    &:disabled {
        background-color: #a0cfff;
        border-color: #a0cfff;
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
        margin-bottom: 4px;
    }
    
    .clue-account {
        font-size: 12px;
        color: #909399;
    }
}

.clue-content {
    flex: 1;
    overflow-y: auto;
    
    &::-webkit-scrollbar {
        width: 6px;
    }
    
    &::-webkit-scrollbar-track {
        background: #f5f5f5;
    }
    
    &::-webkit-scrollbar-thumb {
        background: #c0c4cc;
        border-radius: 3px;
    }
}

.clue-title {
    padding: 16px 20px;
    font-size: 14px;
    font-weight: 500;
    color: #303133;
    border-bottom: 1px solid #f0f0f0;
    background: #fafafa;
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

// 响应式调整
@media (max-width: 1200px) {
    .main-body {
        gap: 12px;
    }
    
    .session-panel {
        width: 320px;
    }
    
    .clue-panel {
        width: 280px;
    }
}

@media (max-width: 992px) {
    .private-msg {
        margin: 0 15px;
    }
    
    .clue-panel {
        display: none;
    }
}
</style>