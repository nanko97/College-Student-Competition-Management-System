<template>
  <div id="app" class="">
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: "app",
};
</script>

<style lang="scss">
// 全局样式规范 - Naive Admin 风格
@import './styles/global.scss';
@import './assets/css/tech-theme.scss';
// 电脑端响应式适配（窗口缩放比例适配）
@import './assets/css/responsive.css';
// 移动端专属适配（手机、平板 <= 768px）
@import './assets/css/mobile.css';

*{
  padding: 0;
  margin:0;
}
html,body{
  width: 100%;
  height: 100%;
}
#app{
  height:100%;
}
body {
  padding: 0;
  margin: 0;
  
}
</style>
