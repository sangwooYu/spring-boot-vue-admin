import Vue from 'vue'
import ElementUI from 'element-ui'
import './styles/element-variables.scss'
import App from './App'
import router from './router'
import store from './store'
import '@/icons' // icon
import '@/permission' // 권한
import { default as request } from './utils/request'
import { hasPermission } from './utils/hasPermission'
import lang from 'element-ui/lib/locale/lang/zh-CN'
import locale from 'element-ui/lib/locale'

// 언어 설정
locale.use(lang)

Vue.use(ElementUI, {
  size: 'small'
})

// 전역 상수
Vue.prototype.request = request
Vue.prototype.hasPermission = hasPermission

// 프로덕션 환경에서 자동으로 false로 설정하여 시작 시 웹에서 프로덕션 프롬프트가 생성되지 않도록 합니다.
Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  template: '<App/>',
  components: { App }
})
