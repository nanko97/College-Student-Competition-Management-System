/**
 * 图片URL处理工具
 * 解决前后端分离时图片路径问题
 */

// 后端基础URL（开发环境）
const BASE_URL = 'http://localhost:9090/BYSJ_Springboot'

/**
 * 处理图片URL
 * @param {string} url - 图片路径（如：upload/人像.png）
 * @returns {string} 完整的图片URL
 */
export function imageUrl(url) {
  if (!url) return ''
  
  // 如果已经是完整URL（http开头），直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  
  // 如果以/开头，拼接基础URL
  if (url.startsWith('/')) {
    return BASE_URL + url
  }
  
  // 相对路径，拼接基础URL
  return BASE_URL + '/' + url
}

/**
 * 批量处理图片URL
 * @param {string} urls - 逗号分隔的图片路径
 * @returns {Array} 完整URL数组
 */
export function imageUrls(urls) {
  if (!urls) return []
  return urls.split(',').map(url => imageUrl(url.trim()))
}

export default {
  imageUrl,
  imageUrls
}
