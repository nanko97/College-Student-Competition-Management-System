import axios from 'axios'
import router from '@/router/router-static'
import storage from '@/utils/storage'
import JSONBig from 'json-bigint'

const http = axios.create({
  timeout: 1000 * 86400,
  withCredentials: true,
  baseURL: '/BYSJ_Springboot',
  transformResponse: [function (data) {
    // 使用 json-bigint 处理响应数据，保持大数字精度
    try {
      return JSONBig.parse(data)
    } catch (e) {
      // 如果不是 JSON 数据，返回原始数据
      return data
    }
  }]
})

// 请求拦截
http.interceptors.request.use(config => {
   config.headers['Token'] = storage.get('Token') // 请求头带上 token
    return config
}, error => {
    return Promise.reject(error)
})

// 响应拦截
http.interceptors.response.use(response => {
  if (response.data && response.data.code === 401) {
    // 401, token 失效
    storage.remove('Token')
    storage.remove('role')
    storage.remove('username')
    
    // 如果当前不在登录页面，才跳转到登录页
    const currentRoute = router.currentRoute
    if (currentRoute && currentRoute.path !== '/login') {
      router.push({ path: '/login' }).catch(() => {})
    }
  }
  return response
}, error => {
  return Promise.reject(error)
})

export default http
