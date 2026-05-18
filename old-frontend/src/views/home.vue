<template>
  <div class="content">
    <!-- 背景动画粒子（向上飘浮） -->
    <div class="background-animation">
      <div
          v-for="n in 20"
          :key="n"
          class="particle"
          :style="getParticleStyle(n)"
      ></div>
    </div>

    <!-- 欢迎卡片 -->
    <div class="welcome-card">
      <div class="icon-wrapper">
        <i class="el-icon-s-platform"></i>
      </div>

      <h1 class="welcome-title">
        <span class="gradient-text">{{ $project.projectName }}</span>
      </h1>

      <p class="welcome-subtitle">欢迎使用本系统</p>

      <!-- 功能网格（动态加载） -->
      <div class="features-grid">
        <div
            v-for="(item, index) in features"
            :key="index"
            class="feature-item"
            :style="{ animationDelay: index * 0.03 + 's' }"
            @click="navigateToFeature(item)"
        >
          <div class="feature-icon">
            <i :class="item.icon"></i>
          </div>
          <span class="feature-text">{{ item.text }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import router from '@/router/router-static'

export default {
  name: 'Home',
  data() {
    return {
      features: []
    }
  },
  mounted() {
    this.init()
    this.loadFeatures()
  },
  methods: {
    init() {
      if (this.$storage.get('Token')) {
        this.$http({
          url: `${this.$storage.get('sessionTable')}/session`,
          method: 'get'
        }).then(({ data }) => {
          if (data && data.code !== 0) {
            router.push({ name: 'login' }).catch(err => {
              if (err.name !== 'NavigationDuplicated') console.error(err)
            })
          }
        }).catch(err => {
          console.error('请求失败:', err)
        })
      } else {
        router.push({ name: 'login' }).catch(err => {
          if (err.name !== 'NavigationDuplicated') console.error(err)
        })
      }
    },

    getParticleStyle() {
      const size = Math.random() * 12 + 4
      const left = Math.random() * 100
      const delay = Math.random() * 10
      const duration = Math.random() * 12 + 8
      return {
        width: `${size}px`,
        height: `${size}px`,
        left: `${left}%`,
        animationDelay: `${delay}s`,
        animationDuration: `${duration}s`
      }
    },

    loadFeatures() {
      const role = this.$storage.get('role') || ''
      if (role === '学生') {
        this.features = [
          { icon: 'el-icon-document', text: '在线报名', routeName: '竞赛信息' },
          { icon: 'el-icon-trophy', text: '竞赛管理', routeName: '竞赛报名' },
          { icon: 'el-icon-bank-card', text: '我的缴费', routeName: '我的缴费' },
          { icon: 'el-icon-data-analysis', text: '我的晋级', routeName: '我的晋级' }
        ]
      } else if (role === '教师') {
        this.features = [
          { icon: 'el-icon-trophy', text: '竞赛管理', routeName: '竞赛信息' },
          { icon: 'el-icon-document', text: '竞赛报名', routeName: '竞赛报名' },
          { icon: 'el-icon-medal', text: '作品评审', routeName: '作品打分' },
          { icon: 'el-icon-s-data', text: '缴费审核', routeName: '缴费审核' }
        ]
      } else {
        this.features = [
          { icon: 'el-icon-user', text: '学生管理', routeName: '学生' },
          { icon: 'el-icon-s-custom', text: '教师管理', routeName: '教师' },
          { icon: 'el-icon-trophy', text: '竞赛管理', routeName: '竞赛信息' },
          { icon: 'el-icon-document', text: '报名管理', routeName: '竞赛报名' }
        ]
      }
    },

    navigateToFeature(item) {
      const routeName = item.routeName
      if (routeName) {
        this.$router.push({ name: routeName }).catch(err => {
          if (err.name !== 'NavigationDuplicated') {
            console.error('路由跳转失败:', err)
          }
        })
      } else {
        this.$message.warning('功能开发中，敬请期待...')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
$primary-start: #667eea;
$primary-end: #764ba2;

.content {
  position: relative;
  min-height: calc(100vh - 60px);
  padding: 40px;
  background: linear-gradient(135deg, $primary-start 0%, $primary-end 100%);
  overflow: hidden;

  .background-animation {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    overflow: hidden;
    pointer-events: none;

    .particle {
      position: absolute;
      bottom: -20px;
      background: rgba(255, 255, 255, 0.3);
      border-radius: 50%;
      animation: float-up linear infinite;
    }
  }

  @keyframes float-up {
    0% {
      transform: translateY(0) rotate(0deg);
      opacity: 0.8;
    }
    100% {
      transform: translateY(-100vh) rotate(360deg);
      opacity: 0;
    }
  }

  .welcome-card {
    position: relative;
    z-index: 1;
    max-width: 1200px;
    margin: 0 auto;
    padding: 60px 48px;
    background: rgba(255, 255, 255, 0.96);
    backdrop-filter: blur(8px);
    border-radius: 20px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    text-align: center;
    transition: transform 0.3s;
  
    &:hover {
      transform: translateY(-4px);
    }
  
    .icon-wrapper {
      width: 90px;
      height: 90px;
      margin: 0 auto 30px;
      background: linear-gradient(135deg, $primary-start, $primary-end);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 12px 24px -8px rgba($primary-start, 0.4);
      animation: iconPulse 2s infinite;
  
      i {
        font-size: 46px;
        color: #fff;
      }
    }
  
    .welcome-title {
      font-size: 36px;
      font-weight: 700;
      margin: 0 0 12px;
  
      .gradient-text {
        background: linear-gradient(135deg, $primary-start, $primary-end);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
    }
  
    .welcome-subtitle {
      font-size: 18px;
      color: #6c757d;
      margin: 0 0 40px;
    }
  
    .features-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 20px;
      margin-top: 0;
  
      .feature-item {
        padding: 28px 16px;
        background: #ffffff;
        border-radius: 12px;
        cursor: pointer;
        transition: all 0.3s cubic-bezier(0.2, 0.9, 0.4, 1.1);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
        border: 1px solid rgba(102, 126, 234, 0.1);
        animation: fadeInUp 0.6s ease-out backwards;
  
        &:hover {
          transform: translateY(-5px);
          box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
        }
  
        .feature-icon {
          width: 60px;
          height: 60px;
          margin: 0 auto 15px;
          background: linear-gradient(135deg, $primary-start, $primary-end);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
        
          i {
            font-size: 28px;
            color: #fff;
          }
        }
  
        .feature-text {
          font-size: 16px;
          color: #333;
          font-weight: 500;
        }
      }
    }
  }

  @keyframes iconPulse {
    0%, 100% {
      transform: scale(1);
      box-shadow: 0 12px 24px -8px rgba($primary-start, 0.4);
    }
    50% {
      transform: scale(1.05);
      box-shadow: 0 20px 30px -10px rgba($primary-start, 0.6);
    }
  }

  @keyframes fadeInUp {
    from {
      opacity: 0;
      transform: translateY(20px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
}

// 响应式优化
@media (max-width: 768px) {
  .content {
    padding: 20px;

    .welcome-card {
      padding: 40px 20px;

      .welcome-title {
        font-size: 28px;
      }

      .features-grid {
        grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
        gap: 15px;

        .feature-item {
          padding: 20px 15px;

          .feature-icon {
            width: 50px;
            height: 50px;

            i {
              font-size: 24px;
            }
          }

          .feature-text {
            font-size: 14px;
          }
        }
      }
    }
  }
}
</style>