import request from '@/utils/request'

export function getOverview(params) {
  return request({
    url: '/system/dataOverview',
    method: 'get',
    params
  })
}
