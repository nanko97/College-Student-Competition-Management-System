<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="background">
      <div class="shape"></div>
      <div class="shape"></div>
      <div class="shape"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <div class="card-header">
        <h1 class="title">
          <i class="el-icon-s-platform"></i>
          竞赛管理系统
        </h1>
        <p class="subtitle">欢迎回来，请登录您的账号</p>
      </div>

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
          <el-radio-group v-model="form.role" class="role-selector">
            <el-radio-button label="学生">
              <i class="el-icon-s-custom"></i> 学生
            </el-radio-button>
            <el-radio-button label="教师">
              <i class="el-icon-reading"></i> 教师
            </el-radio-button>
            <el-radio-button label="管理员">
              <i class="el-icon-s-tools"></i> 管理员
            </el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item prop="username">
          <el-input 
            v-model="form.username" 
            placeholder="请输入账号"
            prefix-icon="el-icon-user"
            clearable
            autocomplete="username"
          >
            <span slot="prepend">
              <i :class="getRoleIcon"></i>
            </span>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input 
            v-model="form.password" 
            type="password"
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
            show-password
            clearable
            autocomplete="current-password"
          ></el-input>
        </el-form-item>

        <!-- 记住我 & 忘记密码 -->
        <div class="form-options">
          <el-checkbox v-model="rememberMe" class="remember-me">
            <span>记住我</span>
          </el-checkbox>
          <!-- <el-link type="primary" :underline="false">忘记密码？</el-link> -->
        </div>

        <!-- 登录按钮 -->
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleLogin" 
            :loading="loading"
            class="login-btn"
            size="large"
            round
          >
            <i class="el-icon-check"></i> {{ loading ? '登录中...' : '立即登录' }}
          </el-button>
        </el-form-item>

        <!-- 底部链接 -->
        <div class="form-footer">
          <span>还没有账号？</span>
          <el-link type="primary" @click="goToRegister" :underline="false">
            <i class="el-icon-circle-plus"></i> 立即注册
          </el-link>
        </div>
      </el-form>
    </div>

    <!-- 右侧说明面板 -->
    <div class="info-panel">
      <div class="info-content">
        <div class="welcome-text">
          <h2>欢迎来到</h2>
          <h3>大学生竞赛管理系统</h3>
        </div>
        
        <div class="features">
          <div class="feature-item">
            <i class="el-icon-trophy"></i>
            <span>竞赛管理</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-document"></i>
            <span>在线报名</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-medal"></i>
            <span>作品评审</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-data-analysis"></i>
            <span>成绩查看</span>
          </div>
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
      menus: []
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
    // 加载菜单配置
    this.menus = menu.list();
    
    // 检查是否有记住的账号
    const savedUsername = localStorage.getItem('rememberedUsername');
    const savedRole = localStorage.getItem('rememberedRole');
    if (savedUsername && savedRole) {
      this.form.username = savedUsername;
      this.form.role = savedRole;
      this.rememberMe = true;
    }
  },
  methods: {
    // 处理登录
    async handleLogin() {
      try {
        await this.$refs.loginForm.validate();
      } catch (error) {
        this.$message.warning('请填写完整的登录信息');
        return;
      }

      this.loading = true;

      try {
        // 根据角色确定访问的接口
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
          // 保存 Token 和用户信息
          this.$storage.set('Token', data.token);
          this.$storage.set('role', this.form.role);
          this.$storage.set('sessionTable', tableName);
          this.$storage.set('adminName', this.form.username);

          // 记住账号
          if (this.rememberMe) {
            localStorage.setItem('rememberedUsername', this.form.username);
            localStorage.setItem('rememberedRole', this.form.role);
          } else {
            localStorage.removeItem('rememberedUsername');
            localStorage.removeItem('rememberedRole');
          }

          // 成功提示
          this.$message.success({
            message: `欢迎回来，${this.form.role} ${this.form.username}！`,
            duration: 1500
          });

          // 延迟跳转到首页
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

    // 根据角色获取表名
    getTableNameByRole(role) {
      const roleMap = {
        '学生': 'xuesheng',
        '教师': 'jiaoshi',
        '管理员': 'users'
      };
      
      // 也可以从菜单中查找
      for (let item of this.menus) {
        if (item.roleName === role && item.hasBackLogin === '是') {
          return item.tableName;
        }
      }
      
      return roleMap[role] || 'users';
    },

    // 跳转到注册页
    goToRegister() {
      this.$router.push('/register');
    }
  }
};
</script>

<style lang="scss" scoped>
// 变量定义
$primary-color: #409EFF;
$success-color: #67C23A;
$warning-color: #E6A23C;
$info-color: #909399;
$bg-gradient-start: #667eea;
$bg-gradient-end: #764ba2;

.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: linear-gradient(135deg, $bg-gradient-start 0%, $bg-gradient-end 100%);
  position: relative;
  overflow: hidden;

  // 背景装饰
  .background {
    position: absolute;
    width: 100%;
    height: 100%;
    overflow: hidden;
    z-index: 0;

    .shape {
      position: absolute;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.1);
      animation: float 20s infinite;

      &:nth-child(1) {
        width: 400px;
        height: 400px;
        left: -150px;
        top: -150px;
        animation-delay: 0s;
      }

      &:nth-child(2) {
        width: 300px;
        height: 300px;
        right: -100px;
        bottom: -100px;
        animation-delay: 7s;
      }

      &:nth-child(3) {
        width: 200px;
        height: 200px;
        right: 20%;
        top: 30%;
        animation-delay: 14s;
      }
    }
  }

  // 登录卡片
  .login-card {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    padding: 40px;
    width: 100%;
    max-width: 480px;
    z-index: 1;
    animation: slideIn 0.6s ease-out;

    .card-header {
      text-align: center;
      margin-bottom: 30px;

      .title {
        color: #333;
        font-size: 32px;
        font-weight: 600;
        margin: 0 0 10px 0;
        
        i {
          color: $primary-color;
          margin-right: 10px;
          font-size: 36px;
        }
      }

      .subtitle {
        color: $info-color;
        font-size: 14px;
        margin: 0;
      }
    }

    .login-form {
      .role-selector {
        width: 100%;
        margin-bottom: 20px;
        
        /deep/ .el-radio-button__inner {
          width: 100px;
          text-align: center;
          font-size: 13px;
          padding: 10px 16px;
          transition: all 0.3s;
          
          &:hover {
            transform: translateY(-2px);
          }
        }
        
        /deep/ .el-radio-button__orig-radio:checked + .el-radio-button__inner {
          background: linear-gradient(135deg, $primary-color 0%, darken($primary-color, 10%) 100%);
          border-color: $primary-color;
          box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
        }
      }

      /deep/ .el-input-group__prepend {
        background-color: transparent;
        border: none;
        padding: 0 10px;
        
        i {
          color: $primary-color;
          font-size: 16px;
        }
      }

      .form-options {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        
        .remember-me {
          /deep/ .el-checkbox__label {
            font-size: 13px;
            color: #606266;
          }
        }
      }

      .login-btn {
        width: 100%;
        height: 46px;
        font-size: 16px;
        font-weight: 500;
        letter-spacing: 2px;
        background: linear-gradient(135deg, $primary-color 0%, darken($primary-color, 10%) 100%);
        border: none;
        transition: all 0.3s;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4);
        }
        
        &:active {
          transform: translateY(0);
        }
      }

      .form-footer {
        text-align: center;
        margin-top: 20px;
        padding-top: 20px;
        border-top: 1px solid #EBEEF5;
        
        span {
          color: $info-color;
          font-size: 14px;
          margin-right: 10px;
        }
      }
    }
  }

  // 信息面板
  .info-panel {
    display: none;
    margin-left: 40px;
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    padding: 40px;
    max-width: 360px;
    color: #fff;
    animation: fadeIn 0.8s ease-out;

    .info-content {
      .welcome-text {
        text-align: center;
        margin-bottom: 40px;
        
        h2 {
          font-size: 20px;
          font-weight: 400;
          margin: 0 0 10px 0;
          opacity: 0.9;
        }

        h3 {
          font-size: 28px;
          font-weight: 600;
          margin: 0;
        }
      }

      .features {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 20px;

        .feature-item {
          background: rgba(255, 255, 255, 0.1);
          border-radius: 12px;
          padding: 20px;
          text-align: center;
          transition: all 0.3s;
          
          &:hover {
            background: rgba(255, 255, 255, 0.2);
            transform: translateY(-5px);
          }

          i {
            font-size: 32px;
            margin-bottom: 10px;
            display: block;
          }

          span {
            font-size: 14px;
            font-weight: 500;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (min-width: 968px) {
  .login-container {
    justify-content: flex-start;
    padding: 40px;

    .login-card {
      animation: slideInLeft 0.6s ease-out;
    }

    .info-panel {
      display: block;
      animation: slideInRight 0.6s ease-out;
    }
  }
}

// 动画
@keyframes float {
  0%, 100% {
    transform: translate(0, 0);
  }
  25% {
    transform: translate(30px, -30px);
  }
  50% {
    transform: translate(-30px, 30px);
  }
  75% {
    transform: translate(30px, 30px);
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-50px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(50px);
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

// Element UI 深度样式覆盖
/deep/ .el-form-item {
  margin-bottom: 22px;
}

/deep/ .el-input__inner {
  border-radius: 8px;
  height: 46px;
  transition: all 0.3s;
  
  &:focus {
    border-color: $primary-color;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
  }
}

/deep/ .el-form-item__content {
  display: flex;
}
</style>
