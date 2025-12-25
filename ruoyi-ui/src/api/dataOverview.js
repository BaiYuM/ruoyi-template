import request from '@/utils/request'

export function getOverview() {
  return request({
    url: '/privateChat/private_chat/accounts',
    method: 'get'
  })
}