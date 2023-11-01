import request from '@/utils/request'

// 创建接口设置
export function createInterfaceItem(data) {
  return request({
    url: '/system/interfaceitem',
    method: 'post',
    data: data
  })
}

// 更新接口设置
export function updateInterfaceItem(data) {
  return request({
    url: '/system/interfaceitem',
    method: 'put',
    data: data
  })
}

// 删除接口设置
export function deleteInterfaceItem(id) {
  return request({
    url: '/system/interfaceitem/' + id,
    method: 'delete'
  })
}

// 获得接口设置
export function getInterfaceItem(id) {
  return request({
    url: '/system/interfaceitem/' + id,
    method: 'get'
  })
}

// 获得接口设置分页
export function getInterfaceItemPage(query) {
  return request({
    url: '/system/interfaceitem/list',
    method: 'get',
    params: query
  })
}

// 调试接口设置
export function debugInterface(data) {
  return request({
    url: '/system/interfaceitem/debug',
    method: 'post',
    data: data,
    responseType: 'text'
  })
}
