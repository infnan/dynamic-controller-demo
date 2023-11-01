import request from '@/utils/request'

// 创建接口分组
export function createInterfaceGroup(data) {
  return request({
    url: '/system/interfacegroup',
    method: 'post',
    data: data
  })
}

// 更新接口分组
export function updateInterfaceGroup(data) {
  return request({
    url: '/system/interfacegroup',
    method: 'put',
    data: data
  })
}

// 删除接口分组
export function deleteInterfaceGroup(id) {
  return request({
    url: '/system/interfacegroup/' + id,
    method: 'delete'
  })
}

// 获得接口分组
export function getInterfaceGroup(id) {
  return request({
    url: '/system/interfacegroup/' + id,
    method: 'get'
  })
}

// 获得接口分组列表
export function getInterfaceGroupList(query) {
  return request({
    url: '/system/interfacegroup/list',
    method: 'get',
    params: query
  })
}
