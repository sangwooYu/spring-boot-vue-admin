<template>
  <div class="errPage-container">
    <el-button @click="back" icon="arrow-left" class="pan-back-btn">뒤로</el-button>
    <el-row>
      <el-col :span="12">
        <h1 class="text-jumbo text-ginormous">Oops!</h1>gif출처
        <a href="https://zh.airbnb.com/" target="_blank">airbnb</a> 페이지
        <h2>이 페이지로 이동할 수 있는 권한이 없습니다.</h2>
        <h6>권한이 필요한 경우 관리자에게 문의하세요.</h6>
        <ul class="list-unstyled">
          <li>또는 다음으로 이동할 수 있습니다.</li>
          <li class="link-type">
            <router-link to="/dashboard">홈 페이지로 돌아가기</router-link>
          </li>
          <li class="link-type">
            <a href="https://www.taobao.com/">둘러보기</a>
          </li>
          <li>
            <a @click.prevent="dialogVisible=true" href="#">사진을 보려면 여기를 클릭하세요.</a>
          </li>
        </ul>
      </el-col>
      <el-col :span="12">
        <img :src="errGif" width="313" height="428" alt="Girl has dropped her ice cream." />
      </el-col>
    </el-row>
    <el-dialog title="무작위로 시청" :visible.sync="dialogVisible">
      <img class="pan-img" :src="ewizardClap" />
    </el-dialog>
  </div>
</template>

<script>
import errGif from '@/assets/401Images/401.gif'

export default {
  name: 'page401',
  data() {
    return {
      errGif: errGif + '?' + +new Date(),
      ewizardClap:
        'https://wpimg.wallstcn.com/007ef517-bafd-4066-aae4-6883632d9646',
      dialogVisible: false
    }
  },
  methods: {
    back() {
      if (this.$route.query.noGoBack) {
        this.$router.push({ path: '/dashboard' })
      } else {
        this.$router.go(-1)
      }
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.errPage-container {
  width: 800px;
  margin: 100px auto;
  .pan-back-btn {
    background: #008489;
    color: #fff;
  }
  .pan-gif {
    margin: 0 auto;
    display: block;
  }
  .pan-img {
    display: block;
    margin: 0 auto;
  }
  .text-jumbo {
    font-size: 60px;
    font-weight: 700;
    color: #484848;
  }
  .list-unstyled {
    font-size: 14px;
    li {
      padding-bottom: 5px;
    }
    a {
      color: #008489;
      text-decoration: none;
      &:hover {
        text-decoration: underline;
      }
    }
  }
}
</style>
