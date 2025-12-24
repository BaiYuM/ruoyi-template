import request from '@/utils/request'

// 查询私信追杀配置列表
export function listConfig(query) {
  return request({
    url: '/privateChat/private_har_config/list',
    method: 'get',
    params: query
  })
}

// 获取私信追杀配置详细信息
export function getConfig(id) {
  return request({
    url: '/privateChat/private_har_config/' + id,
    method: 'get'
  })
}

// 新增私信追杀配置
export function addConfig(data) {
  return request({
    url: '/privateChat/private_har_config',
    method: 'post',
    data: data
  })
}

// 修改私信追杀配置
export function updateConfig(data) {
  return request({
    url: '/privateChat/private_har_config',
    method: 'put',
    data: data
  })
}

// 删除私信追杀配置
export function delConfig(id) {
  return request({
    url: '/privateChat/private_har_config/' + id,
    method: 'delete'
  })
}
