<template>
  <div class="login-container">
    <!-- 3D 背景网格 -->
    <div class="cyber-grid">
      <div class="grid-line" v-for="n in 20" :key="'h'+n" :style="{top: (n*5)+'%'}"></div>
      <div class="grid-line vertical" v-for="n in 20" :key="'v'+n" :style="{left: (n*5)+'%'}"></div>
    </div>

    <!-- 浮动数据粒子 -->
    <div class="data-particles">
      <div class="particle" v-for="(item, index) in particles" :key="index" 
           :style="getParticleStyle(index)">
        <i :class="item.icon"></i>
      </div>
    </div>

    <!-- 左侧登录面板 -->
    <div class="login-panel">
      <div class="panel-content">
        <!-- Logo 区域 -->
        <div class="logo-section">
          <div class="logo-ring">
            <div class="ring ring-1"></div>
            <div class="ring ring-2"></div>
            <div class="ring ring-3"></div>
            <div class="logo-icon">
              <i class="el-icon-s-platform"></i>
            </div>
          </div>
          <h1 class="system-title">
            <span class="title-line">竞赛管理系统</span>
          </h1>
          <p class="system-subtitle">Competition Management System</p>
        </div>

        <!-- 登录表单 -->
        <el-form 
          ref="loginForm" 
          :model="form" 
          :rules="rules" 
          class="login-form"
          size="medium"
          @keyup.enter.native="handleLogin"
        >
          <!-- 角色选择 -->
          <el-form-item prop="role">
            <div class="role-cards">
              <div 
                class="role-card" 
                :class="{ active: form.role === '学生' }"
                @click="form.role = '学生'"
              >
                <div class="role-icon-wrapper">
                  <i class="el-icon-s-custom"></i>
                </div>
                <span class="role-name">学生</span>
                <div class="role-wave"></div>
              </div>
              
              <div 
                class="role-card" 
                :class="{ active: form.role === '教师' }"
                @click="form.role = '教师'"
              >
                <div class="role-icon-wrapper">
                  <i class="el-icon-reading"></i>
                </div>
                <span class="role-name">教师</span>
                <div class="role-wave"></div>
              </div>
              
              <div 
                class="role-card" 
                :class="{ active: form.role === '管理员' }"
                @click="form.role = '管理员'"
              >
                <div class="role-icon-wrapper">
                  <i class="el-icon-s-tools"></i>
                </div>
                <span class="role-name">管理员</span>
                <div class="role-wave"></div>
              </div>
            </div>
          </el-form-item>

          <!-- 账号输入 -->
          <el-form-item prop="username">
            <div class="input-wrapper">
              <i class="input-icon el-icon-user"></i>
              <el-input 
                v-model="form.username" 
                placeholder="请输入账号"
                clearable
                autocomplete="username"
              ></el-input>
            </div>
          </el-form-item>

          <!-- 密码输入 -->
          <el-form-item prop="password">
            <div class="input-wrapper">
              <i class="input-icon el-icon-lock"></i>
              <el-input 
                v-model="form.password" 
                type="password"
                placeholder="请输入密码"
                show-password
                clearable
                autocomplete="current-password"
              ></el-input>
            </div>
          </el-form-item>

          <!-- 记住我 -->
          <div class="form-options">
            <el-checkbox v-model="rememberMe" class="remember-me">
              <span>记住我</span>
            </el-checkbox>
          </div>

          <!-- 登录按钮 -->
          <el-form-item>
            <div class="button-wrapper">
              <button class="cyber-button" @click="handleLogin" :disabled="loading">
                <span class="button-text">{{ loading ? '登录中...' : '立即登录' }}</span>
                <div class="button-bg"></div>
                <div class="button-shine"></div>
              </button>
            </div>
          </el-form-item>

          <!-- 注册链接 -->
          <div class="form-footer">
            <span>还没有账号？</span>
            <el-link type="primary" @click="goToRegister" :underline="false">
              <i class="el-icon-circle-plus"></i> 立即注册
            </el-link>
          </div>
        </el-form>
      </div>
    </div>

    <!-- 右侧装饰面板 -->
    <div class="decoration-panel">
      <div class="deco-content">
        <div class="welcome-animation">
          <h2 class="welcome-title">WELCOME TO</h2>
          <h3 class="welcome-subtitle">大学生竞赛管理系统</h3>
        </div>
        
        <div class="stats-container">
          <div class="stat-item" v-for="(stat, index) in stats" :key="index" :style="{animationDelay: index * 0.2 + 's'}">
            <div class="stat-number">{{stat.number}}</div>
            <div class="stat-label">{{stat.label}}</div>
          </div>
        </div>

        <div class="tech-decoration">
          <div class="tech-circle"></div>
          <div class="tech-circle"></div>
          <div class="tech-circle"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import menu from "@/utils/menu";

export default {
  name: 'Login',
  data() {
    return {
      form: {
        username: '',
        password: '',
        role: '学生'
      },
      rules: {
        username: [
          { required: true, message: '请输入账号', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        role: [
          { required: true, message: '请选择角色', trigger: 'change' }
        ]
      },
      loading: false,
      rememberMe: false,
      menus: [],
      particles: [
        { icon: 'el-icon-trophy' },
        { icon: 'el-icon-document' },
        { icon: 'el-icon-medal' },
        { icon: 'el-icon-data-analysis' },
        { icon: 'el-icon-s-check' },
        { icon: 'el-icon-star-on' }
      ],
      stats: [
        { number: '50+', label: '竞赛项目' },
        { number: '1000+', label: '参赛学生' },
        { number: '100+', label: '指导教师' }
      ]
    };
  },
  computed: {
    getRoleIcon() {
      const icons = {
        '学生': 'el-icon-s-custom',
        '教师': 'el-icon-reading',
        '管理员': 'el-icon-s-tools'
      };
      return icons[this.form.role] || 'el-icon-user';
    }
  },
  mounted() {
    this.menus = menu.list();
    
    const savedUsername = localStorage.getItem('rememberedUsername');
    const savedRole = localStorage.getItem('rememberedRole');
    if (savedUsername && savedRole) {
      this.form.username = savedUsername;
      this.form.role = savedRole;
      this.rememberMe = true;
    }
  },
  methods: {
    getParticleStyle(index) {
      const size = Math.random() * 40 + 30;
      const left = Math.random() * 100;
      const top = Math.random() * 100;
      const delay = Math.random() * 5;
      const duration = Math.random() * 10 + 10;
      return {
        width: size + 'px',
        height: size + 'px',
        left: left + '%',
        top: top + '%',
        animationDelay: delay + 's',
        animationDuration: duration + 's'
      };
    },

    async handleLogin() {
      try {
        await this.$refs.loginForm.validate();
      } catch (error) {
        this.$message.warning('请填写完整的登录信息');
        return;
      }

      this.loading = true;

      try {
        const tableName = this.getTableNameByRole(this.form.role);
        
        const response = await this.$http({
          url: `/${tableName}/login`,
          method: 'post',
          params: {
            username: this.form.username,
            password: this.form.password
          }
        });

        const { data } = response;

        if (data && data.code === 0) {
          this.$storage.set('Token', data.token);
          this.$storage.set('role', this.form.role);
          this.$storage.set('sessionTable', tableName);
          this.$storage.set('adminName', this.form.username);

          if (this.rememberMe) {
            localStorage.setItem('rememberedUsername', this.form.username);
            localStorage.setItem('rememberedRole', this.form.role);
          } else {
            localStorage.removeItem('rememberedUsername');
            localStorage.removeItem('rememberedRole');
          }

          this.$message.success({
            message: `欢迎回来，${this.form.role} ${this.form.username}！`,
            duration: 1500
          });

          setTimeout(() => {
            this.$router.replace({ path: '/index/' });
          }, 500);
        } else {
          this.$message.error(data.msg || '登录失败，请检查账号和密码');
        }
      } catch (error) {
        console.error('登录错误:', error);
        const errorMsg = error.response?.data?.msg || error.message || '登录失败，请稍后重试';
        this.$message.error(errorMsg);
      } finally {
        this.loading = false;
      }
    },

    getTableNameByRole(role) {
      const roleMap = {
        '学生': 'xuesheng',
        '教师': 'jiaoshi',
        '管理员': 'users'
      };
      
      for (let item of this.menus) {
        if (item.roleName === role && item.hasBackLogin === '是') {
          return item.tableName;
        }
      }
      
      return roleMap[role] || 'users';
    },

    goToRegister() {
      this.$router.push('/register');
    }
  }
};
</script>

<style lang="scss" scoped>
$primary-color: #00f2fe;
$secondary-color: #4facfe;
$accent-color: #667eea;
$dark-bg: #0a0e27;
$panel-bg: rgba(255, 255, 255, 0.98);
$text-primary: #ffffff;
$text-secondary: #a0aec0;

.login-container {
  min-height: 100vh;
  display: flex;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, $dark-bg 0%, #1a1f3f 50%, #2d1b4e 100%);
  
  // 赛博网格背景
  .cyber-grid {
    position: absolute;
    width: 100%;
    height: 100%;
    perspective: 1000px;
    
    .grid-line {
      position: absolute;
      width: 100%;
      height: 1px;
      background: linear-gradient(90deg, transparent, rgba($primary-color, 0.3), transparent);
      animation: gridMove 3s ease-in-out infinite;
      
      &.vertical {
        width: 1px;
        height: 100%;
        background: linear-gradient(180deg, transparent, rgba($secondary-color, 0.3), transparent);
        animation: gridMoveVertical 4s ease-in-out infinite;
      }
    }
  }
  
  // 数据粒子
  .data-particles {
    position: absolute;
    width: 100%;
    height: 100%;
    overflow: hidden;
    
    .particle {
      position: absolute;
      color: rgba($primary-color, 0.4);
      font-size: 24px;
      animation: particleFloat 15s linear infinite;
      filter: drop-shadow(0 0 10px rgba($primary-color, 0.6));
    }
  }
  
  // 登录面板
  .login-panel {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 40px;
    z-index: 1;
    
    .panel-content {
      width: 100%;
      max-width: 520px;
      background: $panel-bg;
      backdrop-filter: blur(20px);
      border-radius: 24px;
      box-shadow: 
        0 25px 80px rgba(0, 0, 0, 0.3),
        0 0 0 1px rgba(255, 255, 255, 0.1) inset,
        0 0 60px rgba($primary-color, 0.2);
      padding: 50px 45px;
      animation: panelSlideIn 0.8s cubic-bezier(0.34, 1.56, 0.64, 1);
      position: relative;
      
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 3px;
        background: linear-gradient(90deg, $primary-color, $secondary-color, $accent-color);
        animation: gradientFlow 3s linear infinite;
      }
    }
  }
  
  // Logo 区域
  .logo-section {
    text-align: center;
    margin-bottom: 40px;
    
    .logo-ring {
      position: relative;
      width: 120px;
      height: 120px;
      margin: 0 auto 25px;
      
      .ring {
        position: absolute;
        border-radius: 50%;
        border: 2px solid transparent;
        animation: ringRotate 8s linear infinite;
        
        &.ring-1 {
          width: 100%;
          height: 100%;
          border-top-color: $primary-color;
          border-right-color: $secondary-color;
          filter: drop-shadow(0 0 15px rgba($primary-color, 0.6));
        }
        
        &.ring-2 {
          width: 80%;
          height: 80%;
          top: 10%;
          left: 10%;
          border-bottom-color: $secondary-color;
          border-left-color: $accent-color;
          animation-direction: reverse;
          animation-duration: 6s;
        }
        
        &.ring-3 {
          width: 60%;
          height: 60%;
          top: 20%;
          left: 20%;
          border-top-color: $accent-color;
          animation-duration: 4s;
        }
      }
      
      .logo-icon {
        position: absolute;
        width: 60px;
        height: 60px;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        background: linear-gradient(135deg, $primary-color, $secondary-color);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 10px 30px rgba($primary-color, 0.5);
        animation: iconPulse 2s ease-in-out infinite;
        
        i {
          font-size: 32px;
          color: #fff;
        }
      }
    }
    
    .system-title {
      font-size: 32px;
      font-weight: 700;
      margin: 0 0 10px 0;
      background: linear-gradient(135deg, $primary-color 0%, $secondary-color 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      letter-spacing: 2px;
      
      .title-line {
        display: inline-block;
        animation: titleGlow 3s ease-in-out infinite;
      }
    }
    
    .system-subtitle {
      font-size: 14px;
      color: $text-secondary;
      margin: 0;
      letter-spacing: 3px;
      text-transform: uppercase;
    }
  }
  
  // 登录表单
  .login-form {
    .role-cards {
      display: flex;
      gap: 15px;
      margin-bottom: 30px;
      
      .role-card {
        flex: 1;
        background: linear-gradient(135deg, rgba($primary-color, 0.05) 0%, rgba($secondary-color, 0.05) 100%);
        border: 2px solid rgba($primary-color, 0.2);
        border-radius: 16px;
        padding: 20px 15px;
        text-align: center;
        cursor: pointer;
        transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
        position: relative;
        overflow: hidden;
        
        &:hover {
          transform: translateY(-5px);
          border-color: $primary-color;
          box-shadow: 0 10px 30px rgba($primary-color, 0.3);
          
          .role-icon-wrapper {
            transform: scale(1.1) rotate(10deg);
          }
        }
        
        &.active {
          background: linear-gradient(135deg, $primary-color 0%, $secondary-color 100%);
          border-color: $primary-color;
          box-shadow: 0 10px 40px rgba($primary-color, 0.5);
          transform: translateY(-5px);
          
          .role-icon-wrapper {
            background: rgba(255, 255, 255, 0.2);
            
            i {
              color: #fff;
            }
          }
          
          .role-name {
            color: #fff;
          }
          
          .role-wave {
            opacity: 1;
          }
        }
        
        .role-icon-wrapper {
          width: 50px;
          height: 50px;
          margin: 0 auto 10px;
          background: rgba($primary-color, 0.1);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          transition: all 0.4s;
          
          i {
            font-size: 24px;
            color: $primary-color;
            transition: all 0.4s;
          }
        }
        
        .role-name {
          display: block;
          font-size: 14px;
          font-weight: 600;
          color: #2c3e50;
          transition: all 0.4s;
        }
        
        .role-wave {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          background: radial-gradient(circle, rgba(255,255,255,0.3) 0%, transparent 70%);
          opacity: 0;
          transition: opacity 0.4s;
        }
      }
    }
    
    .input-wrapper {
      position: relative;
      width: 100%;
      
      .input-icon {
        position: absolute;
        left: 15px;
        top: 50%;
        transform: translateY(-50%);
        font-size: 18px;
        color: $primary-color;
        z-index: 1;
        transition: all 0.3s;
      }
      
      /deep/ .el-input__inner {
        height: 50px;
        padding-left: 50px;
        border-radius: 12px;
        border: 2px solid rgba($primary-color, 0.2);
        background: rgba($primary-color, 0.02);
        transition: all 0.3s;
        font-size: 15px;
        
        &:focus {
          border-color: $primary-color;
          box-shadow: 0 0 0 4px rgba($primary-color, 0.1);
          background: #fff;
          
          ~ .input-icon {
            animation: iconBounce 0.6s ease;
          }
        }
      }
    }
    
    .form-options {
      margin-bottom: 25px;
      
      .remember-me {
        /deep/ .el-checkbox__label {
          font-size: 14px;
          color: #606266;
          font-weight: 500;
        }
      }
    }
    
    .button-wrapper {
      position: relative;
      
      .cyber-button {
        width: 100%;
        height: 52px;
        border: none;
        border-radius: 12px;
        background: linear-gradient(135deg, $primary-color 0%, $secondary-color 100%);
        color: #fff;
        font-size: 17px;
        font-weight: 600;
        letter-spacing: 3px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
        transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
        box-shadow: 0 10px 30px rgba($primary-color, 0.4);
        
        &:hover:not(:disabled) {
          transform: translateY(-3px) scale(1.02);
          box-shadow: 0 15px 40px rgba($primary-color, 0.6);
          
          .button-shine {
            left: 100%;
          }
        }
        
        &:active:not(:disabled) {
          transform: translateY(-1px) scale(0.98);
        }
        
        &:disabled {
          opacity: 0.7;
          cursor: not-allowed;
        }
        
        .button-text {
          position: relative;
          z-index: 2;
        }
        
        .button-bg {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          background: linear-gradient(135deg, lighten($primary-color, 10%) 0%, lighten($secondary-color, 10%) 100%);
          opacity: 0;
          transition: opacity 0.4s;
          z-index: 1;
        }
        
        .button-shine {
          position: absolute;
          top: 0;
          left: -100%;
          width: 100%;
          height: 100%;
          background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent);
          transition: 0.6s;
          z-index: 1;
        }
      }
    }
    
    .form-footer {
      text-align: center;
      margin-top: 25px;
      padding-top: 25px;
      border-top: 2px solid rgba($primary-color, 0.1);
      
      span {
        color: #909399;
        font-size: 14px;
        margin-right: 10px;
      }
      
      /deep/ .el-link {
        font-weight: 600;
        transition: all 0.3s;
        
        &:hover {
          transform: translateX(3px);
        }
        
        i {
          animation: plusRotate 2s ease-in-out infinite;
          display: inline-block;
        }
      }
    }
  }
  
  // 装饰面板
  .decoration-panel {
    width: 450px;
    background: rgba(0, 0, 0, 0.3);
    backdrop-filter: blur(10px);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 50px;
    border-left: 1px solid rgba($primary-color, 0.2);
    animation: fadeIn 1s ease-out;
    
    .deco-content {
      text-align: center;
      color: $text-primary;
      position: relative;
      
      .welcome-animation {
        margin-bottom: 50px;
        
        .welcome-title {
          font-size: 20px;
          font-weight: 400;
          margin: 0 0 15px 0;
          letter-spacing: 5px;
          color: $text-secondary;
          animation: textGlow 2s ease-in-out infinite;
        }
        
        .welcome-subtitle {
          font-size: 36px;
          font-weight: 700;
          margin: 0;
          background: linear-gradient(135deg, $primary-color 0%, $secondary-color 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
          animation: subtitleShine 3s ease-in-out infinite;
        }
      }
      
      .stats-container {
        display: flex;
        justify-content: space-around;
        margin-bottom: 50px;
        
        .stat-item {
          text-align: center;
          animation: statSlideIn 0.8s ease-out backwards;
          
          .stat-number {
            font-size: 42px;
            font-weight: 700;
            background: linear-gradient(135deg, $primary-color, $secondary-color);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            margin-bottom: 8px;
          }
          
          .stat-label {
            font-size: 14px;
            color: $text-secondary;
            letter-spacing: 1px;
          }
        }
      }
      
      .tech-decoration {
        position: relative;
        width: 200px;
        height: 200px;
        margin: 0 auto;
        
        .tech-circle {
          position: absolute;
          border-radius: 50%;
          border: 2px solid rgba($primary-color, 0.3);
          animation: techRotate 10s linear infinite;
          
          &:nth-child(1) {
            width: 100%;
            height: 100%;
            border-top-color: $primary-color;
          }
          
          &:nth-child(2) {
            width: 70%;
            height: 70%;
            top: 15%;
            left: 15%;
            border-bottom-color: $secondary-color;
            animation-direction: reverse;
            animation-duration: 8s;
          }
          
          &:nth-child(3) {
            width: 40%;
            height: 40%;
            top: 30%;
            left: 30%;
            border-top-color: $accent-color;
            animation-duration: 6s;
          }
        }
      }
    }
  }
}

// 动画集合
@keyframes gridMove {
  0%, 100% {
    transform: translateY(0);
    opacity: 0.3;
  }
  50% {
    transform: translateY(-20px);
    opacity: 0.8;
  }
}

@keyframes gridMoveVertical {
  0%, 100% {
    transform: translateX(0);
    opacity: 0.3;
  }
  50% {
    transform: translateX(-20px);
    opacity: 0.8;
  }
}

@keyframes particleFloat {
  0% {
    transform: translateY(100vh) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100vh) rotate(360deg);
    opacity: 0;
  }
}

@keyframes ringRotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes iconPulse {
  0%, 100% {
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    transform: translate(-50%, -50%) scale(1.05);
  }
}

@keyframes titleGlow {
  0%, 100% {
    filter: brightness(1);
  }
  50% {
    filter: brightness(1.3);
  }
}

@keyframes gradientFlow {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

@keyframes iconBounce {
  0%, 100% {
    transform: translateY(-50%) scale(1);
  }
  50% {
    transform: translateY(-50%) scale(1.3);
  }
}

@keyframes plusRotate {
  0%, 100% {
    transform: rotate(0deg);
  }
  25% {
    transform: rotate(45deg);
  }
  75% {
    transform: rotate(-45deg);
  }
}

@keyframes textGlow {
  0%, 100% {
    opacity: 0.7;
  }
  50% {
    opacity: 1;
  }
}

@keyframes subtitleShine {
  0%, 100% {
    filter: brightness(1);
  }
  50% {
    filter: brightness(1.4);
  }
}

@keyframes statSlideIn {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes techRotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes panelSlideIn {
  from {
    opacity: 0;
    transform: translateX(-60px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

// 响应式设计
@media (max-width: 1024px) {
  .decoration-panel {
    display: none;
  }
}

@media (max-width: 768px) {
  .login-panel {
    padding: 20px;
    
    .panel-content {
      padding: 40px 25px;
    }
  }
}

// Element UI 深度样式覆盖
/deep/ .el-form-item {
  margin-bottom: 26px;
}

/deep/ .el-checkbox__inner {
  border-radius: 4px;
  border: 2px solid rgba($primary-color, 0.3);
  transition: all 0.3s;
  
  &::after {
    transition: all 0.3s;
  }
}
</style>
