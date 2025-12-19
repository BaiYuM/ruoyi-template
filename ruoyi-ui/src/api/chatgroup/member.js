import request from '@/utils/request'

// 查询群聊成员关联列表
export function listMember(query) {
  return request({
    url: '/group/member/list',
    method: 'get',
    params: query
  })
}

// 查询群聊成员关联详细
export function getMember(id) {
  return request({
    url: '/group/member/' + id,
    method: 'get'
  })
}

// 新增群聊成员关联
export function addMember(data) {
  return request({
    url: '/group/member',
    method: 'post',
    data: data
  })
}

// 修改群聊成员关联
export function updateMember(data) {
  return request({
    url: '/group/member',
    method: 'put',
    data: data
  })
}

// 删除群聊成员关联
export function delMember(id) {
  return request({
    url: '/group/member/' + id,
    method: 'delete'
  })
}
