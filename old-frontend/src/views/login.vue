<template>
  <div class="login-container">
    <!-- 左侧Banner区域 -->
    <div class="left-banner">
      <!-- 背景轮播图 -->
      <div class="banner-slideshow">
        <transition-group name="slide-fade" tag="div" class="slides-wrapper">
          <div
            v-for="(slide, index) in slides"
            :key="index"
            class="slide-item"
            :class="{ active: currentSlide === index }"
            :style="{ backgroundImage: `url(${slide.image})` }"
          >
            <div class="slide-overlay"></div>
          </div>
        </transition-group>
      </div>

      <div class="banner-content">
        <!-- 系统Logo区域 -->
        <div class="logo-area">
          <div class="logo-icon">
            <i class="el-icon-trophy"></i>
          </div>
          <h1 class="system-name">大学生竞赛管理系统</h1>
          <p class="system-desc">Competition Management System</p>
        </div>
        
        <!-- 统计数据 -->
        <div class="stats-section">
          <div class="stat-item">
            <div class="stat-number">50+</div>
            <div class="stat-label">竞赛项目</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">1000+</div>
            <div class="stat-label">参赛学生</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">100+</div>
            <div class="stat-label">指导教师</div>
          </div>
        </div>
        
        <!-- 功能特性 -->
        <div class="features-section">
          <div class="feature-item">
            <i class="el-icon-circle-check"></i>
            <span>在线报名参赛</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-circle-check"></i>
            <span>作品提交管理</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-circle-check"></i>
            <span>成绩实时查询</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧表单区域 -->
    <div class="right-form">
      <div class="form-wrapper">
        <div class="form-header">
          <h2>欢迎登录</h2>
          <p>请输入您的账号信息</p>
        </div>

        <!-- 角色选择：三按钮并排，确保完整显示 -->
        <div class="role-group">
          <div
              class="role-btn"
              :class="{ active: form.role === '学生' }"
              @click="form.role = '学生'"
          >
            <i class="el-icon-user"></i>
            <span>学生</span>
          </div>
          <div
              class="role-btn"
              :class="{ active: form.role === '教师' }"
              @click="form.role = '教师'"
          >
            <i class="el-icon-reading"></i>
            <span>教师</span>
          </div>
          <div
              class="role-btn"
              :class="{ active: form.role === '管理员' }"
              @click="form.role = '管理员'"
          >
            <i class="el-icon-s-tools"></i>
            <span>管理员</span>
          </div>
        </div>

        <el-form
            ref="loginForm"
            :model="form"
            :rules="rules"
            @keyup.enter.native="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
                v-model="form.username"
                :placeholder="accountPlaceholder"
                prefix-icon="el-icon-user"
                size="large"
                clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="el-icon-lock"
                show-password
                size="large"
                clearable
            />
          </el-form-item>

          <div class="options">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
          </div>

          <el-button
              type="primary"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
          >
            {{ loading ? '登录中...' : '立即登录' }}
          </el-button>

          <div class="register-link">
            <span>还没有账号？</span>
            <el-link type="primary" :underline="false" @click="goToRegister">
              立即注册
            </el-link>
          </div>
        </el-form>

        <div class="copyright">
          <p>© 2026 竞赛管理系统 · 让竞赛更精彩</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { login } from '@/api/login';

export default {
  name: 'Login',
  data() {
    return {
      form: {
        role: '学生',
        username: '',
        password: ''
      },
      rules: {
        username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ]
      },
      rememberMe: false,
      loading: false,
      // 轮播图数据
      currentSlide: 0,
      slides: [
        {
          image: 'https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=1920&q=80',
          title: '竞赛舞台'
        },
        {
          image: 'https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?w=1920&q=80',
          title: '团队协作'
        },
        {
          image: 'https://images.unsplash.com/photo-1524178232363-1fb2b075b655?w=1920&q=80',
          title: '学术交流'
        },
        {
          image: 'https://images.unsplash.com/photo-1427504494785-3a9ca7044f45?w=1920&q=80',
          title: '校园风光'
        }
      ],
      slideInterval: null
    };
  },
  computed: {
    accountPlaceholder() {
      const placeholders = {
        '学生': '请输入学号',
        '教师': '请输入工号',
        '管理员': '请输入用户名'
      };
      return placeholders[this.form.role] || '请输入账号';
    }
  },
  mounted() {
    const savedUsername = this.$storage.get('saved_username');
    const savedRole = this.$storage.get('saved_role');
    if (savedUsername && savedRole) {
      this.form.username = savedUsername;
      this.form.role = savedRole;
      this.rememberMe = true;
    }
    
    // 启动轮播图
    this.startSlideshow();
  },
  beforeDestroy() {
    // 清除定时器
    if (this.slideInterval) {
      clearInterval(this.slideInterval);
    }
  },
  methods: {
    // 启动轮播图
    startSlideshow() {
      this.slideInterval = setInterval(() => {
        this.currentSlide = (this.currentSlide + 1) % this.slides.length;
      }, 3000); // 每3秒切换一次
    },
    
    getTableNameByRole(role) {
      const map = { '学生': 'xuesheng', '教师': 'jiaoshi', '管理员': 'users' };
      return map[role] || 'users';
    },
    async handleLogin() {
      try {
        await this.$refs.loginForm.validate();
      } catch {
        return;
      }
      this.loading = true;
      try {
        const tableName = this.getTableNameByRole(this.form.role);
        const { data } = await login({
          tableName,
          username: this.form.username,
          password: this.form.password
        });
        if (data && data.code === 0) {
          this.$storage.set('Token', data.token);
          this.$storage.set('role', this.form.role);
          this.$storage.set('sessionTable', tableName);
          this.$storage.set('adminName', this.form.username);
          this.$storage.set('username', this.form.username);
          const realName = data.xueshengxingming || data.jiaoshixingming;
          if (realName) this.$storage.set('realName', realName);
          if (this.rememberMe) {
            this.$storage.set('saved_username', this.form.username);
            this.$storage.set('saved_role', this.form.role);
          } else {
            this.$storage.remove('saved_username');
            this.$storage.remove('saved_role');
          }
          this.$message.success('登录成功');
          this.$router.replace({ path: '/' });
        } else {
          this.$message.error(data.msg || '登录失败');
        }
      } catch (error) {
        console.error(error);
        this.$message.error('登录失败，请检查网络或联系管理员');
      } finally {
        this.loading = false;
      }
    },
    goToRegister() {
      // 添加过渡动画效果
      const container = document.querySelector('.login-container')
      if (container) {
        container.style.transition = 'all 0.5s cubic-bezier(0.4, 0, 0.2, 1)'
        container.style.opacity = '0'
        container.style.transform = 'translateX(-50px) scale(0.95)'
      }
          
      setTimeout(() => {
        this.$router.push({ path: '/register' })
      }, 300)
    }
  }
};
</script>

<style lang="scss" scoped>
$primary: #667eea;
$secondary: #764ba2;
$white: #ffffff;

.login-container {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(135deg, $primary 0%, $secondary 100%);
}

/* 左侧Banner区域 */
.left-banner {
  position: relative;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  color: $white;
  overflow: hidden;
  
  /* 背景轮播图 */
  .banner-slideshow {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 0;
    
    .slides-wrapper {
      width: 100%;
      height: 100%;
      position: relative;
      
      .slide-item {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-size: cover;
        background-position: center;
        background-repeat: no-repeat;
        opacity: 0;
        transition: opacity 0.8s ease-in-out;
        
        &.active {
          opacity: 1;
        }
        
        .slide-overlay {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          background: linear-gradient(
            135deg,
            rgba(102, 126, 234, 0.85) 0%,
            rgba(118, 75, 162, 0.85) 100%
          );
        }
      }
    }
  }
  
  .banner-content {
    position: relative;
    z-index: 1;
    max-width: 500px;
    
    .logo-area {
      text-align: center;
      margin-bottom: 60px;
      
      .logo-icon {
        width: 80px;
        height: 80px;
        margin: 0 auto 20px;
        font-size: 56px;
        color: $white;
        line-height: 80px;
      }
      
      .system-name {
        font-size: 32px;
        font-weight: bold;
        margin: 0 0 10px;
      }
      
      .system-desc {
        font-size: 16px;
        opacity: 0.9;
        margin: 0;
      }
    }
    
    .stats-section {
      display: flex;
      justify-content: space-around;
      margin-bottom: 40px;
      padding: 30px 0;
      background: rgba(255, 255, 255, 0.1);
      border-radius: 12px;
      backdrop-filter: blur(10px);
      
      .stat-item {
        text-align: center;
        
        .stat-number {
          font-size: 36px;
          font-weight: bold;
          margin-bottom: 8px;
        }
        
        .stat-label {
          font-size: 14px;
          opacity: 0.9;
        }
      }
    }
    
    .features-section {
      .feature-item {
        display: flex;
        align-items: center;
        margin-bottom: 15px;
        font-size: 16px;
        
        i {
          margin-right: 10px;
          font-size: 20px;
        }
      }
    }
  }
}

/* 右侧表单区域 */
.right-form {
  width: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: $white;
  
  .form-wrapper {
    width: 100%;
    max-width: 400px;

    .form-header {
      text-align: center;
      margin-bottom: 40px;
      
      h2 {
        font-size: 28px;
        color: #333;
        margin: 0 0 10px;
      }
      
      p {
        font-size: 14px;
        color: #999;
        margin: 0;
      }
    }

    /* 角色按钮组 */
    .role-group {
      display: flex;
      justify-content: space-around;
      margin-bottom: 30px;

      .role-btn {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 15px 10px;
        margin: 0 5px;
        border: 2px solid #e0e0e0;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.3s;
        background: #fff;
        color: #334155;
        white-space: nowrap;

        &:hover {
          border-color: $primary;
          transform: translateY(-2px);
        }
        
        &.active {
          border-color: $primary;
          background: linear-gradient(135deg, $primary, $secondary);
          color: white;
          
          i { 
            color: white; 
          }
        }
        
        i {
          font-size: 24px;
          margin-bottom: 8px;
          color: $primary;
        }
        
        span {
          font-size: 14px;
        }
      }
    }

    /* 表单元素 */
    .el-form-item {
      margin-bottom: 24px;
    }
    
    ::v-deep .el-input__inner {
      height: 48px;
      line-height: 48px;
    }
    
    .options {
      margin-bottom: 20px;
    }
    
    .login-btn {
      width: 100%;
      height: 48px;
      font-size: 16px;
      background: linear-gradient(135deg, $primary, $secondary);
      border: none;
      
      &:hover {
        opacity: 0.9;
      }
    }
    
    .register-link {
      text-align: center;
      margin-top: 20px;
      font-size: 14px;
      color: #666;
      
      span {
        margin-right: 5px;
      }
    }
    
    .copyright {
      text-align: center;
      margin-top: 30px;
      font-size: 12px;
      color: #999;
      
      p {
        margin: 0;
      }
    }
  }
}

@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
  }
  
  .left-banner {
    display: none;
  }
  
  .right-form {
    width: 100%;
    padding: 20px;
  }
}
</style>