import request from '@/utils/request'

// 查询私聊消息列表
export function listMsg(query) {
  return request({
    url: '/private/msg/list',
    method: 'get',
    params: query
  })
}

// 查询私聊消息详细
export function getMsg(id) {
  return request({
    url: '/private/msg/' + id,
    method: 'get'
  })
}

// 新增私聊消息
export function addMsg(data) {
  return request({
    url: '/private/msg',
    method: 'post',
    data: data
  })
}

// 修改私聊消息
export function updateMsg(data) {
  return request({
    url: '/private/msg',
    method: 'put',
    data: data
  })
}

// 删除私聊消息
export function delMsg(id) {
  return request({
    url: '/private/msg/' + id,
    method: 'delete'
  })
}
