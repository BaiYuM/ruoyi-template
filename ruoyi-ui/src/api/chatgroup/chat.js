import request from '@/utils/request'

// 查询私聊会话列表
export function listChat(query) {
  return request({
    url: '/private/chat/list',
    method: 'get',
    params: query
  })
}

// 查询私聊会话详细
export function getChat(id) {
  return request({
    url: '/private/chat/' + id,
    method: 'get'
  })
}

// 新增私聊会话
export function addChat(data) {
  return request({
    url: '/private/chat',
    method: 'post',
    data: data
  })
}

// 修改私聊会话
export function updateChat(data) {
  return request({
    url: '/private/chat',
    method: 'put',
    data: data
  })
}

// 删除私聊会话
export function delChat(id) {
  return request({
    url: '/private/chat/' + id,
    method: 'delete'
  })
}
