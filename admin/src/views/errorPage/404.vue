<template>
  <div class="main-404">
    <div class="http404">
      <div class="pic-404">
        <img class="pic-404__parent" :src="img_404" alt="404" />
        <img class="pic-404__child left" :src="img_404_cloud" alt="404" />
        <img class="pic-404__child mid" :src="img_404_cloud" alt="404" />
        <img class="pic-404__child right" :src="img_404_cloud" alt="404" />
      </div>
      <div class="tip-404">
        <div class="tip-404__msg-404">입력한 URL이 올바른지 확인하세요.</div>
        <a href="/" class="tip-404__return-home">맨 위로 가기</a>
      </div>
    </div>
  </div>
</template>

<script>
import img404 from '@/assets/404Images/404.png'
import img404cloud from '@/assets/404Images/404cloud.png'

export default {
  name: 'page404',
  data() {
    return {
      img404,
      img404cloud
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.main-404 {
  background: #f0f2f5;
  margin-top: -20px;
  height: 100%;
}

.http404 {
  position: relative;
  width: 1200px;
  margin: 20px auto 60px;
  padding: 0 100px;
  overflow: hidden;
  .pic-404 {
    position: relative;
    float: left;
    width: 600px;
    padding: 180px 0;
    overflow: hidden;
    &__parent {
      width: 100%;
    }
    &__child {
      position: absolute;
      &.left {
        width: 80px;
        top: 17px;
        left: 220px;
        opacity: 0;
        animation-name: cloudLeft;
        animation-duration: 2s;
        animation-timing-function: linear;
        animation-fill-mode: forwards;
        animation-delay: 1s;
      }
      &.mid {
        width: 46px;
        top: 10px;
        left: 420px;
        opacity: 0;
        animation-name: cloudMid;
        animation-duration: 2s;
        animation-timing-function: linear;
        animation-fill-mode: forwards;
        animation-delay: 1.2s;
      }
      &.right {
        width: 62px;
        top: 100px;
        left: 500px;
        opacity: 0;
        animation-name: cloudRight;
        animation-duration: 2s;
        animation-timing-function: linear;
        animation-fill-mode: forwards;
        animation-delay: 1s;
      }
      @keyframes cloudLeft {
        0% {
          top: 17px;
          left: 220px;
          opacity: 0;
        }
        20% {
          top: 33px;
          left: 188px;
          opacity: 1;
        }
        80% {
          top: 81px;
          left: 92px;
          opacity: 1;
        }
        100% {
          top: 97px;
          left: 60px;
          opacity: 0;
        }
      }
      @keyframes cloudMid {
        0% {
          top: 10px;
          left: 420px;
          opacity: 0;
        }
        20% {
          top: 40px;
          left: 360px;
          opacity: 1;
        }
        70% {
          top: 130px;
          left: 180px;
          opacity: 1;
        }
        100% {
          top: 160px;
          left: 120px;
          opacity: 0;
        }
      }
      @keyframes cloudRight {
        0% {
          top: 100px;
          left: 500px;
          opacity: 0;
        }
        20% {
          top: 120px;
          left: 460px;
          opacity: 1;
        }
        80% {
          top: 180px;
          left: 340px;
          opacity: 1;
        }
        100% {
          top: 200px;
          left: 300px;
          opacity: 0;
        }
      }
    }
  }
  .tip-404 {
    position: relative;
    float: left;
    width: 300px;
    padding: 250px 0;
    overflow: hidden;
    &__msg-404 {
      font-size: 13px;
      line-height: 21px;
      color: grey;
      opacity: 0;
      margin-bottom: 30px;
      animation-name: slideUp;
      animation-duration: 0.5s;
      animation-delay: 0.2s;
      animation-fill-mode: forwards;
    }
    &__return-home {
      display: block;
      float: left;
      width: 110px;
      height: 36px;
      background: #1482f0;
      border-radius: 100px;
      text-align: center;
      color: #ffffff;
      opacity: 0;
      font-size: 14px;
      line-height: 36px;
      cursor: pointer;
      animation-name: slideUp;
      animation-duration: 0.5s;
      animation-delay: 0.3s;
      animation-fill-mode: forwards;
    }
    @keyframes slideUp {
      0% {
        transform: translateY(60px);
        opacity: 0;
      }
      100% {
        transform: translateY(0);
        opacity: 1;
      }
    }
  }
}
</style>
