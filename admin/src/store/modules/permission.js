import { asyncRouterMap, constantRouterMap } from '@/router/index'

/**
 * meta.auth를 통해 현재 사용자의 권한과 일치하는지 확인합니다.
 * @param permissionCodeList
 * @param route
 */
function hasPermission(permissionCodeList, route) {
  if (route.meta && route.meta.permission) {
    return permissionCodeList.some(permission => route.meta.permission.indexOf(permission) >= 0)
  } else {
    return true
  }
}

/**
 * 비동기 라우팅 테이블을 재귀적으로 필터링하여 사용자의 역할 권한과 일치하는 라우팅 테이블을 반환합니다.
 * @param asyncRouterMap
 * @param permissionCodeList
 */
function filterAsyncRouter(asyncRouterMap, permissionCodeList) {
  return asyncRouterMap.filter(route => {
    // 배열을 필터링하는 필터, JS 구문
    if (hasPermission(permissionCodeList, route)) {
      if (route.children && route.children.length) {
        // 이 경로 아래에 다음 단계가 있는 경우 재귀적으로 호출합니다.
        route.children = filterAsyncRouter(route.children, permissionCodeList)
        // 필터링 후 더 이상 하위 요소가 없는 경우 상위 메뉴도 표시되지 않습니다.
        // return (route.children && route.children.length)
      }
      return true
    }
    return false
  })
}

const permission = {
  state: {
    routers: constantRouterMap, // 고정 경로 및 다음 추가 라우터를 포함하여 이 사용자에 대한 모든 경로
    addRouters: [] // 이 사용자의 역할에 의해 제공되는 새로운 동적 경로
  },
  mutations: {
    SET_ROUTERS: (state, routers) => {
      state.addRouters = routers
      state.routers = constantRouterMap.concat(routers) // 고정 경로와 새 경로를 결합하여 이 사용자에 대한 최종 경로 정보가 됩니다.
    }
  },
  actions: {
    GenerateRoutes({ commit }, account) {
      return new Promise(resolve => {
        const role = account.roleName
        const permissionCodeList = account.permissionCodeList
        // 역할에 사용할 수 있는 경로를 선언합니다.
        let accessedRouters
        if (role === '슈퍼 관리자') {
          // 역할에 '슈퍼 관리자'가 포함되어 있으면 모든 경로를 사용할 수 있습니다.
          // 실제로 관리자에게도 모든 메뉴가 있으며, 여기서 주된 목적은 역할 판단을 사용하여 로딩 시간을 절약하는 것입니다.
          accessedRouters = asyncRouterMap
        } else {
          // 그렇지 않으면 이 역할에 사용할 수 있는 경로를 다음을 기준으로 필터링해야 합니다.
          accessedRouters = filterAsyncRouter(asyncRouterMap, permissionCodeList)
        }
        // 경로 설정 방법을 실행합니다.
        commit('SET_ROUTERS', accessedRouters)
        resolve()
      })
    }
  }
}

export default permission
