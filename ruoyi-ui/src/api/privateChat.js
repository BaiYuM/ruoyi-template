import request from '@/utils/request'

// 获取授权抖音号列表
export function getAccounts() {
  return request({
    url: '/privateChat/private_chat/accounts',
    method: 'get'
  })
}

// 获取会话列表
export function getSessions(params) {
  return request({
    url: '/privateChat/private_chat/sessions',
    method: 'get',
    params
  })
}

// 获取聊天消息
export function getMessages(sessionId) {
  return request({
    url: '/privateChat/private_chat/messages',
    method: 'get',
    params: { sessionId }
  })
}

// 获取关联线索
export function getClues(userId) {
  return request({
    url: '/privateChat/private_chat/clues',
    method: 'get',
    params: { userId }
  })
}

// 发送消息
export function sendMessage(data) {
  return request({
    url: '/privateChat/private_chat/send',
    method: 'post',
    data
  })
}
