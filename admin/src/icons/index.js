import Vue from 'vue'
import IconSvg from '@/components/Icon-svg'// SVG 구성 요소

// 글로벌 컴포넌트 등록
Vue.component('icon-svg', IconSvg)

const requireAll = requireContext => requireContext.keys().map(requireContext)
const req = require.context('./svg', false, /\.svg$/)
requireAll(req)
