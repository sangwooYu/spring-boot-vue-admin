import axios from 'axios'
import { Message, MessageBox } from 'element-ui'
import store from '../store'
import { getToken } from '@/utils/token'

// axios 인스턴스 생성
// https://www.kancloud.cn/yunye/axios/234845
const service = axios.create({
  baseURL: process.env.BASE_API, // API의 base_url
  timeout: 5000, // 요청 시간 초과
  // 모든 요청은 Json으로 전달됩니다.
  // 사전 테스트 요청이 있을 것이며 서버는 옵션을 통해 정상적으로 요청해야 합니다.
  // http://www.ruanyifeng.com/blog/2016/04/cors
  headers: {
    'Content-type': 'application/json;charset=UTF-8'
  }
})

// 요청 인터셉터
service.interceptors.request.use(config => {
  if (store.getters.token) {
    // 각 요청에 사용자 지정 토큰을 포함하도록 설정하세요. 적절히 수정하세요.
    config.headers['Authorization'] = getToken()
  }
  return config
}, error => {
  // Do something with request error
  console.debug(error) // for debug
  Promise.reject(error)
})

// 응답 인터셉터
service.interceptors.response.use(
  response => {
    if (response.data.code === 200) {
      return response.data
    } else {
      Message({
        message: response.data.message,
        type: 'error',
        duration: 5 * 1000
      })
      return Promise.reject('error')
    }
  },
  error => {
    // 4002: 인증 필요
    if (error.response.data.code === 4002) {
      MessageBox.confirm('로그인이 필요합니다!', '경고', {
        confirmButtonText: '로그인',
        cancelButtonText: '취소',
        type: 'warning'
      }).then(() => {
        store.dispatch('FedLogout').then(() => {
          location.reload()// 为了重新实例化vue-router对象 避免bug
        })
      })
    } else {
      Message({
        message: error.response.data.message,
        type: 'error',
        duration: 5 * 1000
      })
    }
    return Promise.reject(error)
  }
)

export default service
