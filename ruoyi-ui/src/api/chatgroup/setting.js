import request from '@/utils/request'

// 查询私聊设置列表
export function listSetting(query) {
  return request({
    url: '/private/setting/list',
    method: 'get',
    params: query
  })
}

// 查询私聊设置详细
export function getSetting(id) {
  return request({
    url: '/private/setting/' + id,
    method: 'get'
  })
}

// 新增私聊设置
export function addSetting(data) {
  return request({
    url: '/private/setting',
    method: 'post',
    data: data
  })
}

// 修改私聊设置
export function updateSetting(data) {
  return request({
    url: '/private/setting',
    method: 'put',
    data: data
  })
}

// 删除私聊设置
export function delSetting(id) {
  return request({
    url: '/private/setting/' + id,
    method: 'delete'
  })
}
