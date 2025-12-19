import request from '@/utils/request'

export function getDirectionalList(params) {
  return request({
    url: '/exposure/directional/list',
    method: 'get',
    params
  })
}

