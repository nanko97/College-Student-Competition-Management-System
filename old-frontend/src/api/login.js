/**
 * 登录相关 API 接口
 * 统一管理登录相关的所有请求
 */

import http from '@/utils/http'

/**
 * 用户登录
 * @param {Object} params - 登录参数
 * @param {string} params.tableName - 角色对应表名 (xuesheng/jiaoshi/users)
 * @param {string} params.username - 用户名/学号/工号
 * @param {string} params.password - 密码
 * @returns {Promise} 返回登录结果
 */
export function login({ tableName, username, password }) {
  return http({
    url: `/${tableName}/login`,
    method: 'post',
    params: {
      username,
      password
    }
  })
}

/**
 * 用户注册
 * @param {Object} params - 注册参数
 * @param {string} params.tableName - 角色对应表名
 * @param {Object} params.data - 注册数据
 * @returns {Promise}
 */
export function register({ tableName, data }) {
  return http({
    url: `/${tableName}/register`,
    method: 'post',
    data
  })
}

/**
 * 获取当前登录用户信息
 * @param {string} tableName - 用户表名
 * @returns {Promise}
 */
export function getUserInfo(tableName) {
  return http({
    url: `/${tableName}/info`,
    method: 'get'
  })
}

/**
 * 更新用户信息
 * @param {string} tableName - 用户表名
 * @param {Object} data - 用户数据
 * @returns {Promise}
 */
export function updateUserInfo(tableName, data) {
  return http({
    url: `/${tableName}/update`,
    method: 'post',
    data
  })
}

/**
 * 退出登录
 * @returns {Promise}
 */
export function logout() {
  return http({
    url: '/users/logout',
    method: 'post'
  })
}
