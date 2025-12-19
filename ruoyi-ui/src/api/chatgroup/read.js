import request from '@/utils/request'

// 查询私聊消息已读列表
export function listRead(query) {
  return request({
    url: '/private/read/list',
    method: 'get',
    params: query
  })
}

// 查询私聊消息已读详细
export function getRead(id) {
  return request({
    url: '/private/read/' + id,
    method: 'get'
  })
}

// 新增私聊消息已读
export function addRead(data) {
  return request({
    url: '/private/read',
    method: 'post',
    data: data
  })
}

// 修改私聊消息已读
export function updateRead(data) {
  return request({
    url: '/private/read',
    method: 'put',
    data: data
  })
}

// 删除私聊消息已读
export function delRead(id) {
  return request({
    url: '/private/read/' + id,
    method: 'delete'
  })
}
