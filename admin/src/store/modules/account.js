import { login, logout, detail } from '@/api/account'
import { getToken, setToken, removeToken } from '@/utils/token'

const account = {
  state: {
    token: getToken(),
    accountId: -1,
    email: null,
    name: null,
    loginTime: -1,
    registerTime: -1,
    roleName: null,
    permissionCodeList: []
  },

  mutations: {
    SET_TOKEN: (state, token) => {
      state.token = token
    },
    SET_ACCOUNT: (state, account) => {
      state.accountId = account.id
      state.email = account.email
      state.name = account.name
      state.loginTime = account.loginTime
      state.registerTime = account.registerTime
      state.roleName = account.roleName
      state.permissionCodeList = account.permissionCodeList
    },
    RESET_ACCOUNT: (state) => {
      state.token = null
      state.accountId = -1
      state.email = null
      state.name = null
      state.loginTime = -1
      state.registerTime = -1
      state.roleName = null
      state.permissionCodeList = []
    }
  },

  actions: {
    // 등록
    Login({ commit }, loginForm) {
      return new Promise((resolve, reject) => {
        login(loginForm).then(response => {
          if (response.code === 200) {
            // 쿠키에 토큰 저장
            setToken(response.data)
            // 뷰엑스에 토큰 저장
            commit('SET_TOKEN', response.data)
          }
          // 전달/login/index.vue : store.dispatch('Login').then(data)
          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 사용자 정보 얻기
    Detail({ commit }) {
      return new Promise((resolve, reject) => {
        detail().then(response => {
          // 사용자 정보 저장
          commit('SET_ACCOUNT', response.data)
          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 로그아웃
    Logout({ commit }) {
      return new Promise((resolve, reject) => {
        logout().then(() => {
          // 토큰 및 기타 관련 역할 정보 지우기
          commit('RESET_ACCOUNT')
          removeToken()
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 프런트 엔드 로그아웃
    FedLogout({ commit }) {
      return new Promise(resolve => {
        commit('RESET_ACCOUNT')
        removeToken()
        resolve()
      })
    }
  }
}

export default account
