<template>
  <div class="register-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="bg-circle bg-circle-1"></div>
      <div class="bg-circle bg-circle-2"></div>
      <div class="bg-circle bg-circle-3"></div>
    </div>

    <!-- 注册卡片 -->
    <div class="register-card">
      <!-- Logo 和品牌 -->
      <div class="card-header">
        <div class="logo-wrapper">
          <i class="el-icon-user-solid"></i>
        </div>
        <h1 class="system-name">用户注册</h1>
        <p class="system-desc">Create Your Account</p>
      </div>

      <!-- 注册表单 -->
      <el-form
        ref="registerForm"
        :model="form"
        :rules="rules"
        class="register-form"
      >
        <!-- 角色选择 -->
        <el-form-item prop="role">
          <div class="role-tabs">
            <div
              v-for="role in roles"
              :key="role.value"
              class="role-tab"
              :class="{ active: form.role === role.value }"
              @click="form.role = role.value"
            >
              <i :class="role.icon"></i>
              <span>{{ role.label }}</span>
            </div>
          </div>
        </el-form-item>

        <!-- 账号输入 -->
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            :placeholder="accountPlaceholder"
            prefix-icon="el-icon-user"
            size="large"
            clearable
          />
        </el-form-item>

        <!-- 密码输入 -->
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码（6-20位）"
            prefix-icon="el-icon-lock"
            show-password
            size="large"
            clearable
          />
        </el-form-item>

        <!-- 确认密码 -->
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            prefix-icon="el-icon-lock"
            show-password
            size="large"
            clearable
          />
        </el-form-item>

        <!-- 姓名 -->
        <el-form-item prop="name">
          <el-input
            v-model="form.name"
            placeholder="请输入真实姓名"
            prefix-icon="el-icon-user"
            size="large"
            clearable
          />
        </el-form-item>

        <!-- 性别 -->
        <el-form-item prop="gender">
          <div class="gender-selector">
            <div
              class="gender-option"
              :class="{ active: form.gender === '男' }"
              @click="form.gender = '男'"
            >
              <i class="el-icon-male"></i>
              <span>男</span>
            </div>
            <div
              class="gender-option"
              :class="{ active: form.gender === '女' }"
              @click="form.gender = '女'"
            >
              <i class="el-icon-female"></i>
              <span>女</span>
            </div>
          </div>
        </el-form-item>

        <!-- 注册按钮 -->
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="register-btn"
            :loading="loading"
            @click="handleRegister"
          >
            {{ loading ? '注册中...' : '注 册' }}
          </el-button>
        </el-form-item>

        <!-- 登录链接 -->
        <div class="form-footer">
          <span>已有账号？</span>
          <el-link type="primary" @click="goToLogin" :underline="false">
            立即登录
          </el-link>
        </div>
      </el-form>

      <!-- 版权信息 -->
      <div class="card-footer">
        <p>© 2026 竞赛管理系统</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Register',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.form.password) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };

    return {
      form: {
        username: '',
        password: '',
        confirmPassword: '',
        name: '',
        gender: '男',
        role: '学生'
      },
      rules: {
        username: [
          { required: true, message: '请输入账号', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度 6-20 位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        gender: [
          { required: true, message: '请选择性别', trigger: 'change' }
        ],
        role: [
          { required: true, message: '请选择角色', trigger: 'change' }
        ]
      },
      loading: false,
      roles: [
        { value: '学生', label: '学生', icon: 'el-icon-user' },
        { value: '教师', label: '教师', icon: 'el-icon-reading' }
      ]
    };
  },
  computed: {
    accountPlaceholder() {
      const placeholders = {
        '学生': '请输入学号',
        '教师': '请输入工号'
      };
      return placeholders[this.form.role] || '请输入账号';
    }
  },
  methods: {
    getTableNameByRole(role) {
      const tableMap = {
        '学生': 'xuesheng',
        '教师': 'jiaoshi'
      };
      return tableMap[role] || 'users';
    },

    async handleRegister() {
      try {
        await this.$refs.registerForm.validate();
      } catch (error) {
        return;
      }

      this.loading = true;

      try {
        const tableName = this.getTableNameByRole(this.form.role);

        const registerData = {
          username: this.form.username,
          password: this.form.password,
          xingming: this.form.name,
          xingbie: this.form.gender
        };

        if (tableName === 'xuesheng') {
          registerData.xuehao = this.form.username;
        } else if (tableName === 'jiaoshi') {
          registerData.gonghao = this.form.username;
        }

        const { data } = await this.$http({
          url: `/${tableName}/register`,
          method: 'post',
          data: registerData
        });

        if (data && data.code === 0) {
          this.$message.success('注册成功');
          this.$router.push({ path: '/login' });
        } else {
          this.$message.error(data.msg || '注册失败');
        }
      } catch (error) {
        console.error('注册异常:', error);
        this.$message.error('注册失败，请检查网络或联系管理员');
      } finally {
        this.loading = false;
      }
    },

    goToLogin() {
      this.$router.push({ path: '/login' });
    }
  }
};
</script>

<style lang="scss" scoped>
$primary-color: #1890ff;
$primary-light: #e6f7ff;
$text-primary: #262626;
$text-secondary: #595959;
$text-muted: #8c8c8c;
$border-color: #d9d9d9;
$bg-color: #f0f2f5;
$card-shadow: 0 2px 8px rgba(0, 0, 0, 0.09);
$card-shadow-hover: 0 4px 16px rgba(0, 0, 0, 0.12);

.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-color;
  position: relative;
  overflow: hidden;
  padding: 20px;
}

.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;

  .bg-circle {
    position: absolute;
    border-radius: 50%;
    opacity: 0.1;
    animation: float 20s ease-in-out infinite;
  }

  .bg-circle-1 {
    width: 400px;
    height: 400px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    top: -100px;
    right: -100px;
    animation-delay: 0s;
  }

  .bg-circle-2 {
    width: 300px;
    height: 300px;
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    bottom: -50px;
    left: -50px;
    animation-delay: -7s;
  }

  .bg-circle-3 {
    width: 200px;
    height: 200px;
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    top: 50%;
    left: 20%;
    animation-delay: -14s;
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-20px) scale(1.05); }
}

.register-card {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 12px;
  box-shadow: $card-shadow;
  padding: 40px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  animation: cardFadeIn 0.6s ease-out;

  &:hover {
    box-shadow: $card-shadow-hover;
    transform: translateY(-2px);
  }

  .card-header {
    text-align: center;
    margin-bottom: 32px;
    animation: headerSlideDown 0.8s ease-out;

    .logo-wrapper {
      width: 64px;
      height: 64px;
      margin: 0 auto 16px;
      background: $primary-light;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.3s;

      i {
        font-size: 32px;
        color: $primary-color;
        transition: transform 0.3s;
      }

      &:hover {
        transform: scale(1.05);
        i { transform: rotate(10deg); }
      }
    }

    .system-name {
      font-size: 24px;
      font-weight: 600;
      color: $text-primary;
      margin: 0 0 8px 0;
      letter-spacing: 1px;
    }

    .system-desc {
      font-size: 13px;
      color: $text-muted;
      margin: 0;
      letter-spacing: 0.5px;
    }
  }

  .register-form {
    .role-tabs {
      display: flex;
      gap: 8px;
      margin-bottom: 24px;
      background: $bg-color;
      padding: 4px;
      border-radius: 8px;

      .role-tab {
        flex: 1;
        padding: 10px 12px;
        text-align: center;
        border-radius: 6px;
        cursor: pointer;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        font-size: 14px;
        color: $text-secondary;
        position: relative;
        overflow: hidden;

        i {
          margin-right: 6px;
          font-size: 16px;
          transition: transform 0.3s;
        }

        &::before {
          content: '';
          position: absolute;
          top: 50%;
          left: 50%;
          width: 0;
          height: 0;
          border-radius: 50%;
          background: rgba($primary-color, 0.1);
          transform: translate(-50%, -50%);
          transition: width 0.4s, height 0.4s;
        }

        &:hover {
          color: $primary-color;
          transform: translateY(-1px);
          i { transform: scale(1.1); }

          &::before {
            width: 100px;
            height: 100px;
          }
        }

        &.active {
          background: #fff;
          color: $primary-color;
          font-weight: 500;
          box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
          transform: scale(1.02);
        }
      }
    }

    .gender-selector {
      display: flex;
      gap: 12px;

      .gender-option {
        flex: 1;
        padding: 12px;
        text-align: center;
        border: 2px solid $border-color;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.3s;
        font-size: 14px;
        color: $text-secondary;

        i {
          margin-right: 6px;
          font-size: 16px;
          transition: transform 0.3s;
        }

        &:hover {
          border-color: $primary-color;
          color: $primary-color;
          transform: translateY(-1px);
          i { transform: scale(1.1); }
        }

        &.active {
          border-color: $primary-color;
          background: $primary-light;
          color: $primary-color;
          font-weight: 500;
        }
      }
    }

    .register-btn {
      width: 100%;
      height: 44px;
      font-size: 16px;
      font-weight: 500;
      letter-spacing: 2px;
      border-radius: 8px;
      transition: all 0.3s;

      &:hover:not(:disabled) {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba($primary-color, 0.3);
      }

      &:active:not(:disabled) {
        transform: translateY(0);
      }
    }

    .form-footer {
      text-align: center;
      margin-top: 20px;
      font-size: 14px;
      color: $text-muted;

      span { margin-right: 8px; }
    }
  }

  .card-footer {
    margin-top: 32px;
    padding-top: 24px;
    border-top: 1px solid #f0f0f0;
    text-align: center;

    p {
      font-size: 12px;
      color: $text-muted;
      margin: 0;
    }
  }
}

@media (max-width: 768px) {
  .register-card {
    max-width: 100%;
    padding: 32px 24px;
    animation-duration: 0.4s;
  }

  .bg-circle-1 { width: 250px !important; height: 250px !important; }
  .bg-circle-2 { width: 200px !important; height: 200px !important; }
  .bg-circle-3 { display: none; }
}

@keyframes cardFadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes headerSlideDown {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
