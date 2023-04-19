import router from './router'
import store from './store'
import NProgress from 'nprogress' // 진행률 표시줄
import 'nprogress/nprogress.css'// 진행률 표시줄 스타일
import { getToken } from '@/utils/token'

const whiteList = ['/login'] // 화이트리스트, 로그인 필요 없는 경로

router.beforeEach((to, from, next) => {
  NProgress.start() // 진행 시작
  // 쿠키에서 토큰을 가져옵니다.
  if (getToken()) {
    // token 있으면
    if (to.path === '/login') {
      // 하지만 다음 단계는 랜딩 페이지입니다.
      // 홈 페이지로 이동
      next({ path: '/' })
    } else {
      // 다음 점프는 랜딩 페이지가 아닙니다.
      // VUEX 지워짐, 캐릭터 이름 없음
      if (store.getters.roleName === null) {
        // 사용자 정보 검색
        store.dispatch('Detail').then(response => {
          // 경로 생성
          store.dispatch('GenerateRoutes', response.data).then(() => {
            router.addRoutes(store.getters.addRouters)
            next({ ...to })
          })
        })
      } else {
        next()
      }
    }
  } else {
    // 이동하려는 경로가 화이트리스트에 있는 경우 직접
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      // 경로가 화이트리스트에 등록되지 않았고 로그인하지 않은 경우 로그인 페이지로 리디렉션됩니다.
      next('/login')
      NProgress.done() // 진행 상황 종료
    }
  }
})

router.afterEach(() => {
  NProgress.done() // 진행 상황 종료
})
