import request from '@/utils/request'

export function getDirectionalList(params) {
  return request({
    url: '/exposure/directional/list',
    method: 'get',
    params
  })
}

export function saveDirectionalConfig(data) {
  return request({
    url: '/exposure/directional/save',
    method: 'post',
    data
  })
}

export function uploadDirectionalFile(formData) {
  return request({
    url: '/exposure/directional/upload',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function uploadDirectionalFileAsync(formData) {
  return request({
    url: '/exposure/directional/upload/async',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function downloadDirectionalTemplate() {
  return request({
    url: '/exposure/directional/template',
    method: 'get',
    responseType: 'blob'
  })
}

export function getParseResult(taskId) {
  return request({
    url: '/exposure/parse/result',
    method: 'get',
    params: { taskId }
  })
}

export function getAutoList(params) {
  return request({
    url: '/exposure/auto/list',
    method: 'get',
    params
  })
}

export function saveAutoConfig(data) {
  return request({
    url: '/exposure/auto/save',
    method: 'post',
    data
  })
}

export function getExposureStats(params) {
  return request({
    url: '/exposure/auto/stats',
    method: 'get',
    params
  })
}

export function getTodayCount(configId) {
  return request({
    url: '/system/exposure/todayCount',
    method: 'get',
    params: { configId }
  })
}

export function triggerAutoExposure(configId) {
  return request({
    url: '/system/exposure/trigger',
    method: 'post',
    params: { configId }
  })
}

export function getAutoSettings() {
  return request({
    url: '/exposure/auto/settings',
    method: 'get'
  })
}

export function saveAutoSettings(data) {
  return request({
    url: '/exposure/auto/settings',
    method: 'post',
    data
  })
}
