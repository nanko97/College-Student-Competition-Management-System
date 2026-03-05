<template>
<div class="content">
  <!-- 动态背景 -->
  <div class="background-animation">
    <div class="particle" v-for="n in 20" :key="n" :style="getParticleStyle(n)"></div>
  </div>

  <!-- 欢迎卡片 -->
  <div class="welcome-card">
    <div class="icon-wrapper">
      <i class="el-icon-s-platform"></i>
    </div>
    <h1 class="welcome-title">
      <span class="gradient-text">{{this.$project.projectName}}</span>
    </h1>
    <p class="welcome-subtitle">欢迎使用本系统</p>
    
    <div class="features-grid">
      <div class="feature-item" v-for="(item, index) in features" :key="index" :style="{animationDelay: index * 0.1 + 's'}">
        <div class="feature-icon">
          <i :class="item.icon"></i>
        </div>
        <span class="feature-text">{{item.text}}</span>
      </div>
    </div>

    <el-button type="primary" round size="medium" @click="goToDashboard" class="action-btn">
      <i class="el-icon-arrow-right"></i> 进入控制台
    </el-button>
  </div>
</div>
</template>
<script>
import router from '@/router/router-static'
export default {
  data() {
    return {
      features: [
        { icon: 'el-icon-trophy', text: '竞赛管理' },
        { icon: 'el-icon-document', text: '在线报名' },
        { icon: 'el-icon-medal', text: '作品评审' },
        { icon: 'el-icon-data-analysis', text: '成绩查看' }
      ]
    }
  },
  mounted(){
    this.init();
  },
  methods:{
    init(){
        if(this.$storage.get('Token')){
        this.$http({
            url: `${this.$storage.get('sessionTable')}/session`,
            method: "get"
        }).then(({ data }) => {
            if (data && data.code != 0) {
            router.push({ name: 'login' })
            }
        });
        }else{
            router.push({ name: 'login' })
        }
    },
    getParticleStyle(index) {
      const size = Math.random() * 60 + 20;
      const left = Math.random() * 100;
      const top = Math.random() * 100;
      const delay = Math.random() * 20;
      const duration = Math.random() * 10 + 15;
      return {
        width: size + 'px',
        height: size + 'px',
        left: left + '%',
        top: top + '%',
        animationDelay: delay + 's',
        animationDuration: duration + 's'
      };
    },
    goToDashboard() {
      this.$router.push('/index/');
    }
  }
};
</script>

<style lang="scss" scoped>
$primary-color: #667eea;
$primary-dark: #764ba2;
$secondary-color: #f093fb;
$accent-color: #4facfe;

.content {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, $primary-color 0%, $primary-dark 50%, $secondary-color 100%);
  background-size: 200% 200%;
  animation: gradientShift 15s ease infinite;

  // 动态背景粒子
  .background-animation {
    position: absolute;
    width: 100%;
    height: 100%;
    overflow: hidden;
    z-index: 0;

    .particle {
      position: absolute;
      border-radius: 50%;
      background: radial-gradient(circle, rgba(255,255,255,0.2) 0%, rgba(255,255,255,0.05) 70%);
      animation: float infinite ease-in-out;
      filter: blur(8px);
    }
  }

  // 欢迎卡片
  .welcome-card {
    position: relative;
    z-index: 1;
    background: rgba(255, 255, 255, 0.98);
    backdrop-filter: blur(20px);
    border-radius: 24px;
    box-shadow: 
      0 25px 80px rgba(0, 0, 0, 0.25),
      0 0 0 1px rgba(255, 255, 255, 0.1) inset;
    padding: 60px 50px;
    text-align: center;
    max-width: 600px;
    animation: slideIn 0.8s cubic-bezier(0.34, 1.56, 0.64, 1);

    .icon-wrapper {
      width: 100px;
      height: 100px;
      margin: 0 auto 25px;
      background: linear-gradient(135deg, $primary-color 0%, $primary-dark 100%);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4);
      animation: iconPulse 2s ease-in-out infinite;

      i {
        font-size: 50px;
        color: #fff;
      }
    }

    .welcome-title {
      font-size: 32px;
      font-weight: 700;
      margin: 0 0 15px 0;
      
      .gradient-text {
        background: linear-gradient(135deg, $primary-color 0%, $primary-dark 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
    }

    .welcome-subtitle {
      font-size: 16px;
      color: #909399;
      margin: 0 0 40px 0;
      letter-spacing: 1px;
    }

    // 功能网格
    .features-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 20px;
      margin-bottom: 40px;

      .feature-item {
        background: linear-gradient(135deg, rgba($primary-color, 0.05) 0%, rgba($primary-dark, 0.05) 100%);
        border-radius: 16px;
        padding: 25px 20px;
        transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
        border: 1px solid rgba($primary-color, 0.1);
        cursor: pointer;
        animation: fadeInUp 0.6s ease-out backwards;

        &:hover {
          transform: translateY(-8px) scale(1.05);
          box-shadow: 0 15px 35px rgba(102, 126, 234, 0.3);
          background: linear-gradient(135deg, rgba($primary-color, 0.1) 0%, rgba($primary-dark, 0.1) 100%);
          border-color: rgba($primary-color, 0.3);

          .feature-icon {
            transform: scale(1.2) rotate(10deg);
            
            i {
              color: $primary-color;
            }
          }
        }

        .feature-icon {
          width: 60px;
          height: 60px;
          margin: 0 auto 12px;
          background: rgba(255, 255, 255, 0.8);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          transition: all 0.4s;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);

          i {
            font-size: 28px;
            color: $primary-color;
            transition: all 0.4s;
          }
        }

        .feature-text {
          font-size: 15px;
          font-weight: 600;
          color: #2c3e50;
          letter-spacing: 0.5px;
        }
      }
    }

    // 操作按钮
    .action-btn {
      width: 100%;
      height: 52px;
      font-size: 17px;
      font-weight: 600;
      letter-spacing: 3px;
      background: linear-gradient(135deg, $primary-color 0%, $primary-dark 100%);
      border: none;
      border-radius: 12px;
      transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
      position: relative;
      overflow: hidden;

      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
        transition: 0.5s;
      }

      &:hover {
        transform: translateY(-3px) scale(1.02);
        box-shadow: 0 12px 30px rgba(102, 126, 234, 0.5);
        background: linear-gradient(135deg, lighten($primary-color, 5%) 0%, lighten($primary-dark, 5%) 100%);

        &::before {
          left: 100%;
        }
      }

      &:active {
        transform: translateY(-1px) scale(0.98);
      }
    }
  }
}

// 动画集合
@keyframes gradientShift {
  0%, 100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  25% {
    transform: translate(40px, -40px) rotate(90deg);
  }
  50% {
    transform: translate(-40px, 40px) rotate(180deg);
  }
  75% {
    transform: translate(40px, 40px) rotate(270deg);
  }
}

@keyframes iconPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(40px) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
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

// 响应式设计
@media (max-width: 768px) {
  .content {
    padding: 20px;
    
    .welcome-card {
      padding: 40px 25px;
      
      .features-grid {
        grid-template-columns: 1fr;
      }
    }
  }
}
</style>
