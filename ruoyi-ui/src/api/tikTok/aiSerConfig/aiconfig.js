import request from '@/utils/request'

// 查询AI客服配置列表
export function listAiconfig(query) {
  return request({
    url: '/aiSerConfig/aiconfig/list',
    method: 'get',
    params: query
  })
}

// 查询AI客服配置详细
export function getAiconfig(id) {
  return request({
    url: '/aiSerConfig/aiconfig/' + id,
    method: 'get'
  })
}

// 新增AI客服配置
export function addAiconfig(data) {
  return request({
    url: '/aiSerConfig/aiconfig',
    method: 'post',
    data: data
  })
}

// 修改AI客服配置
export function updateAiconfig(data) {
  return request({
    url: '/aiSerConfig/aiconfig',
    method: 'put',
    data: data
  })
}

// 删除AI客服配置
export function delAiconfig(id) {
  return request({
    url: '/aiSerConfig/aiconfig/' + id,
    method: 'delete'
  })
}
