import Vue from 'vue'
import Router from 'vue-router'
// in development env not use Lazy Loading,because Lazy Loading too many pages will cause webpack hot update too slow.so only in production use Lazy Loading

/* layout */
import Layout from '../views/layout/Layout'

const _import = require('./_import_' + process.env.NODE_ENV)

Vue.use(Router)

/**
 * icon : the icon show in the sidebar
 * hidden : if `hidden:true` will not show in the sidebar
 * redirect : if `redirect:noRedirect` will not redirect in the levelBar
 * noDropDown : if `noDropDown:true` will not has submenu in the sidebar
 * meta : `{ permission: ['a:xx'] }`  will control the page permission
 **/
export const constantRouterMap = [
  { path: '/login', component: _import('login/index'), hidden: true },
  { path: '/404', component: _import('errorPage/404'), hidden: true },
  { path: '/401', component: _import('errorPage/401'), hidden: true },
  {
    path: '',
    component: Layout,
    redirect: 'dashboard',
    icon: 'dashboard',
    noDropDown: true,
    children: [{
      path: 'dashboard',
      name: '콘솔',
      component: _import('dashboard/index'),
      meta: { title: 'dashboard', noCache: true }
    }]
  }
]

export default new Router({
  // mode: 'history', //백엔드 지원을 열 수 있습니다.
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRouterMap
})

export const asyncRouterMap = [
  {
    path: '/account',
    component: Layout,
    redirect: '/account/list',
    icon: 'name',
    noDropDown: true,
    children: [{
      path: 'list',
      name: '계정 관리',
      component: _import('account/list'),
      meta: { permission: ['account:list'] }
    }]
  },

  {
    path: '/account',
    component: Layout,
    redirect: '/account/detail',
    hidden: true,
    children: [{
      path: 'detail',
      name: '계정 센터',
      component: _import('account/detail')
    }]
  },

  {
    path: '/role',
    component: Layout,
    redirect: '/role/list',
    icon: 'role',
    noDropDown: true,
    children: [{
      path: 'list',
      name: '역할 관리',
      component: _import('role/list'),
      meta: { permission: ['role:list'] }
    }]
  }
]
