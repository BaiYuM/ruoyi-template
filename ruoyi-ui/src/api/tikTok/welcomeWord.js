import request from '@/utils/request'

// 查询欢迎词配置列表
export function listWelcomeWord(query) {
  return request({
    url: '/welWord/wel_word_config/list',
    method: 'get',
    params: query
  })
}

// 查询欢迎词配置详细
export function getWelcomeWord(id) {
  return request({
    url: '/welWord/wel_word_config/' + id,
    method: 'get'
  })
}

// 新增欢迎词配置
export function addWelcomeWord(data) {
  return request({
    url: '/welWord/wel_word_config',
    method: 'post',
    data: data
  })
}

// 修改欢迎词配置
export function updateWelcomeWord(data) {
  return request({
    url: '/welWord/wel_word_config',
    method: 'put',
    data: data
  })
}

// 删除欢迎词配置
export function delWelcomeWord(id) {
  return request({
    url: '/welWord/wel_word_config/' + id,
    method: 'delete'
  })
}

// 导出欢迎词配置
export function exportWelcomeWord(query) {
  return request({
    url: '/welWord/wel_word_config/export',
    method: 'post',
    params: query
  })
}
