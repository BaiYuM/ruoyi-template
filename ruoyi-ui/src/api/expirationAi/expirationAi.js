import request from '@/utils/request'

// 查询ai客服配置列表
export function listExpirationAi(query) {
  return request({
    url: '/aiCuServer/expirationAi/list',
    method: 'get',
    params: query
  })
}

// 查询ai客服配置详细
export function getExpirationAi(id) {
  return request({
    url: '/aiCuServer/expirationAi/' + id,
    method: 'get'
  })
}

// 新增ai客服配置
export function addExpirationAi(data) {
  return request({
    url: '/aiCuServer/expirationAi',
    method: 'post',
    data: data
  })
}

// 修改ai客服配置
export function updateExpirationAi(data) {
  return request({
    url: '/aiCuServer/expirationAi',
    method: 'put',
    data: data
  })
}

// 删除ai客服配置
export function delExpirationAi(id) {
  return request({
    url: '/aiCuServer/expirationAi/' + id,
    method: 'delete'
  })
}
