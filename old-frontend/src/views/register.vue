<template>
  <div class="register-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="grid-bg"></div>
      <div class="floating-orb orb-1"></div>
      <div class="floating-orb orb-2"></div>
      <div class="floating-orb orb-3"></div>
    </div>

    <div class="main-content">
      <!-- 左侧注册卡片 -->
      <div class="left-panel">
        <div class="register-card">
          <!-- 卡片头部 -->
          <div class="card-header">
            <div class="logo-wrapper">
              <div class="logo-icon">
                <i class="el-icon-trophy"></i>
              </div>
            </div>
            <p class="system-title">COMPETITION MANAGEMENT SYSTEM</p>
          </div>

          <!-- 角色选择 -->
          <div class="role-selector">
            <div
              class="role-item"
              :class="{ active: form.role === '学生' }"
              @click="form.role = '学生'"
            >
              <div class="role-icon">
                <i class="el-icon-user"></i>
              </div>
              <span>学生</span>
            </div>
            <div
              class="role-item"
              :class="{ active: form.role === '教师' }"
              @click="form.role = '教师'"
            >
              <div class="role-icon">
                <i class="el-icon-reading"></i>
              </div>
              <span>教师</span>
            </div>
          </div>

          <!-- 注册表单 -->
          <el-form ref="registerForm" class="register-form" :model="form" :rules="rules">
            <el-form-item prop="username">
              <el-input
                v-model="form.username"
                :placeholder="accountPlaceholder"
                prefix-icon="el-icon-user"
                size="medium"
                clearable
                @blur="checkUsername"
              >
                <template slot="prepend">
                  <i :class="roleIcon"></i>
                </template>
                <template slot="append">
                  <el-button
                    v-if="form.username"
                    :icon="usernameCheckStatus === 'checking' ? 'el-icon-loading' : (usernameCheckStatus === 'success' ? 'el-icon-circle-check' : 'el-icon-circle-close')"
                    @click="checkUsername"
                    :disabled="usernameCheckStatus === 'checking'"
                  ></el-button>
                </template>
              </el-input>
              <div v-if="usernameCheckStatus === 'success'" class="check-success">✓ 账号可用</div>
              <div v-else-if="usernameCheckStatus === 'error'" class="check-error">✗ 该账号已被注册</div>
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码（6-20位）"
                prefix-icon="el-icon-lock"
                show-password
                size="medium"
                clearable
                @input="checkPasswordStrength"
              />
              <!-- 密码强度显示 -->
              <div v-if="form.password" class="password-strength">
                <div class="strength-bar">
                  <div class="strength-fill" :style="{ width: passwordStrength.score + '%', background: passwordStrength.color }"></div>
                </div>
                <span class="strength-text" :style="{ color: passwordStrength.color }">{{ passwordStrength.text }}</span>
              </div>
              <!-- 密码设置建议 -->
              <div v-if="form.password" class="password-tips">
                <div class="tip-item" :class="{ active: passwordLength >= 6 }">
                  <i :class="passwordLength >= 6 ? 'el-icon-circle-check' : 'el-icon-minus'"></i>
                  <span>长度至少6位</span>
                </div>
                <div class="tip-item" :class="{ active: hasUpperCase }">
                  <i :class="hasUpperCase ? 'el-icon-circle-check' : 'el-icon-minus'"></i>
                  <span>包含大写字母</span>
                </div>
                <div class="tip-item" :class="{ active: hasLowerCase }">
                  <i :class="hasLowerCase ? 'el-icon-circle-check' : 'el-icon-minus'"></i>
                  <span>包含小写字母</span>
                </div>
                <div class="tip-item" :class="{ active: hasNumber }">
                  <i :class="hasNumber ? 'el-icon-circle-check' : 'el-icon-minus'"></i>
                  <span>包含数字</span>
                </div>
                <div class="tip-item" :class="{ active: hasSpecialChar }">
                  <i :class="hasSpecialChar ? 'el-icon-circle-check' : 'el-icon-minus'"></i>
                  <span>包含特殊字符</span>
                </div>
              </div>
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input
                v-model="form.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                prefix-icon="el-icon-lock"
                show-password
                size="medium"
                clearable
              />
            </el-form-item>

            <el-form-item prop="name">
              <el-input
                v-model="form.name"
                placeholder="请输入真实姓名"
                prefix-icon="el-icon-user-solid"
                size="medium"
                clearable
              />
            </el-form-item>

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

            <el-form-item>
              <el-button
                class="register-btn"
                type="primary"
                size="medium"
                :loading="loading"
                @click="handleRegister"
              >
                {{ loading ? '注册中...' : '立即注册' }}
              </el-button>
            </el-form-item>

            <div class="form-footer">
              <span>已有账号？</span>
              <el-link type="primary" :underline="false" @click="goToLogin">
                立即登录
              </el-link>
            </div>
          </el-form>

          <!-- 卡片底部 -->
          <div class="card-footer">
            <p>© 2026 竞赛管理系统 · 让竞赛更精彩</p>
          </div>
        </div>
      </div>

      <!-- 右侧欢迎面板 -->
      <div class="right-panel">
        <div class="welcome-content">
          <div class="welcome-header">
            <p class="welcome-subtitle">WELCOME TO</p>
            <h1 class="welcome-title">大学生竞赛管理系统</h1>
          </div>

          <div class="stats-display">
            <div class="stat-item">
              <div class="stat-number">50+</div>
              <div class="stat-label">竞赛项目</div>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <div class="stat-number">1000+</div>
              <div class="stat-label">参赛学生</div>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <div class="stat-number">100+</div>
              <div class="stat-label">指导教师</div>
            </div>
          </div>

          <div class="features-list">
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

          <div class="decorative-circle">
            <div class="circle-ring"></div>
            <div class="circle-ring"></div>
            <div class="circle-center"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import http from '@/utils/http'

export default {
  name: 'Register',
  data() {
    // 确认密码验证
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.form.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    return {
      form: {
        role: '学生',
        username: '',
        password: '',
        confirmPassword: '',
        name: '',
        gender: '男'
      },
      usernameCheckStatus: '',  // checking, success, error
      passwordStrength: {
        score: 0,
        text: '',
        color: ''
      },
      passwordLength: 0,
      hasUpperCase: false,
      hasLowerCase: false,
      hasNumber: false,
      hasSpecialChar: false,
      rules: {
        username: [
          { required: true, message: '请输入账号', trigger: 'blur' },
          { min: 7, max: 20, message: '账号长度在 7 到 20 个字符', trigger: 'blur' },
          { pattern: /^[a-zA-Z0-9_]+$/, message: '账号只能包含字母、数字和下划线', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, validator: validateConfirmPassword, trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  computed: {
    accountPlaceholder() {
      const placeholders = {
        '学生': '请输入学号',
        '教师': '请输入工号'
      }
      return placeholders[this.form.role] || '请输入账号'
    },
    roleIcon() {
      const icons = {
        '学生': 'el-icon-user',
        '教师': 'el-icon-reading'
      }
      return icons[this.form.role] || 'el-icon-user'
    }
  },
  watch: {
    'form.password'(newVal) {
      this.passwordLength = newVal.length
      this.hasUpperCase = /[A-Z]/.test(newVal)
      this.hasLowerCase = /[a-z]/.test(newVal)
      this.hasNumber = /\d/.test(newVal)
      this.hasSpecialChar = /[^A-Za-z0-9]/.test(newVal)
    }
  },
  methods: {
    // 检查账号唯一性
    checkUsername() {
      if (!this.form.username) {
        this.usernameCheckStatus = ''
        return
      }

      this.usernameCheckStatus = 'checking'

      // 角色映射
      const roleMap = {
        '学生': 'xuesheng',
        '教师': 'jiaoshi'
      }

      const roleKey = roleMap[this.form.role] || 'xuesheng'

      console.log('检查账号:', this.form.username, '角色:', this.form.role, roleKey)

      this.$http({
        url: `/registration/check-account`,
        method: 'get',
        params: {
          role: roleKey,
          account: this.form.username
        }
      }).then(res => {
        console.log('检查返回:', res.data)
        if (res.data.code === 0) {
          const available = res.data.data.available
          console.log('账号可用:', available)
          if (available === true) {
            this.usernameCheckStatus = 'success'
          } else {
            this.usernameCheckStatus = 'error'
          }
        } else {
          this.$message.error('检查失败：' + (res.data.msg || '未知错误'))
          this.usernameCheckStatus = ''
        }
      }).catch(err => {
        console.error('检查账号错误:', err)
        this.$message.error('网络错误，请重试')
        this.usernameCheckStatus = ''
      })
    },

    // 检查密码强度
    checkPasswordStrength() {
      const pwd = this.form.password
      if (!pwd) {
        this.passwordStrength = { score: 0, text: '', color: '' }
        return
      }

      let score = 0
      // 基础分（长度）
      score += Math.min(pwd.length * 4, 40)
      // 复杂度分
      if (/[A-Z]/.test(pwd)) score += 10
      if (/[a-z]/.test(pwd)) score += 10
      if (/\d/.test(pwd)) score += 10
      if (/[^A-Za-z0-9]/.test(pwd)) score += 20
      // 长度加分
      if (pwd.length >= 12) score += 10
      score = Math.min(score, 100)

      let text = ''
      let color = ''
      if (score >= 80) {
        text = '强'
        color = '#52c41a'
      } else if (score >= 60) {
        text = '中'
        color = '#faad14'
      } else if (score >= 40) {
        text = '弱'
        color = '#ff7a45'
      } else {
        text = '极弱'
        color = '#ff4d4f'
      }

      this.passwordStrength = { score, text, color }
    },

    handleRegister() {
      this.$refs.registerForm.validate((valid) => {
        if (!valid) {
          return false
        }

        this.loading = true

        // 根据角色确定注册接口和参数
        let registerUrl = ''
        let params = {}

        if (this.form.role === '学生') {
          registerUrl = '/xuesheng/register'
          params = {
            xuehao: this.form.username,
            mima: this.form.password,
            xueshengxingming: this.form.name,
            xingbie: this.form.gender
          }
        } else if (this.form.role === '教师') {
          registerUrl = '/jiaoshi/register'
          params = {
            gonghao: this.form.username,
            mima: this.form.password,
            jiaoshixingming: this.form.name,
            xingbie: this.form.gender
          }
        }

        // 发送注册请求
        http.post(registerUrl, params)
          .then(response => {
            const res = response.data
            if (res.code === 0) {
              this.$message.success('注册成功')

              // 延迟跳转到登录页
              setTimeout(() => {
                this.$router.push({ name: 'login' })
              }, 1000)
            } else {
              this.$message.error(res.msg || '注册失败')
            }
          })
          .catch(error => {
            console.error('注册错误:', error)
            this.$message.error('注册失败，请检查网络连接')
          })
          .finally(() => {
            this.loading = false
          })
      })
    },

    goToLogin() {
      // 添加过渡动画效果
      const container = document.querySelector('.register-container')
      if (container) {
        container.style.transition = 'all 0.5s cubic-bezier(0.4, 0, 0.2, 1)'
        container.style.opacity = '0'
        container.style.transform = 'translateX(50px) scale(0.95)'
      }
      
      setTimeout(() => {
        this.$router.push({ name: 'login' })
      }, 300)
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/variables";

.register-container {
  position: relative;
  min-height: 100vh;
  background: $bg-primary;
  overflow: hidden;

  .bg-decoration {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    overflow: hidden;

    .grid-bg {
      position: absolute;
      width: 100%;
      height: 100%;
      background-image:
        linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
        linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
      background-size: 50px 50px;
    }

    .floating-orb {
      position: absolute;
      border-radius: 50%;
      background: radial-gradient(circle, rgba(102, 126, 234, 0.15), transparent);
      animation: float 20s infinite ease-in-out;

      &.orb-1 {
        width: 300px;
        height: 300px;
        top: -100px;
        right: -100px;
        animation-delay: 0s;
      }

      &.orb-2 {
        width: 200px;
        height: 200px;
        bottom: -50px;
        left: -50px;
        animation-delay: 5s;
      }

      &.orb-3 {
        width: 150px;
        height: 150px;
        top: 50%;
        left: 50%;
        animation-delay: 10s;
      }
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -30px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.9);
  }
}

.main-content {
  position: relative;
  z-index: 1;
  display: flex;
  min-height: 100vh;
  padding: 40px;
  gap: 40px;
}

.left-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;

  .register-card {
    width: 100%;
    max-width: 450px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    padding: 40px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);

    .card-header {
      text-align: center;
      margin-bottom: 30px;

      .logo-wrapper {
        .logo-icon {
          width: 60px;
          height: 60px;
          margin: 0 auto 15px;
          background: $brand-gradient;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;

          i {
            font-size: 32px;
            color: #fff;
          }
        }
      }

      .system-title {
        font-size: 14px;
        color: #595959;
        letter-spacing: 2px;
        margin: 0;
      }
    }

      .role-selector {
      display: flex;
      justify-content: space-around;
      margin-bottom: 30px;

      .role-item {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 12px 8px;
        margin: 0 5px;
        border: 2px solid $border-light;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.3s;
        color: #2c3e50;

        &:hover {
          border-color: $brand-primary;
          transform: translateY(-2px);
        }

        &.active {
          border-color: $brand-primary;
          background: $brand-gradient;
          color: #fff;

          .role-icon {
            color: #fff;
          }
        }

        .role-icon {
          font-size: 20px;
          margin-bottom: 5px;
          color: $brand-primary;
        }

        span {
          font-size: 13px;
          font-weight: 500;
          color: #2c3e50;
        }
      }
    }

    .register-form {
      ::v-deep .el-input__inner {
        height: 42px;
        line-height: 42px;
      }

      ::v-deep .el-input-group__prepend {
        background: $bg-light;
        border-right: none;
      }

      // 账号唯一性检查样式
      .check-success {
        margin-top: 5px;
        font-size: 12px;
        color: #52c41a;
        padding-left: 5px;
      }

      .check-error {
        margin-top: 5px;
        font-size: 12px;
        color: #ff4d4f;
        padding-left: 5px;
      }

      // 密码强度显示
      .password-strength {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-top: 8px;

        .strength-bar {
          flex: 1;
          height: 6px;
          background: #e8e8e8;
          border-radius: 3px;
          overflow: hidden;

          .strength-fill {
            height: 100%;
            border-radius: 3px;
            transition: width 0.3s, background 0.3s;
          }
        }

        .strength-text {
          font-size: 12px;
          font-weight: 500;
          min-width: 30px;
          text-align: right;
        }
      }

      // 密码设置建议
      .password-tips {
        margin-top: 12px;
        padding: 10px;
        background: #f6f8fa;
        border-radius: 6px;

        .tip-item {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 12px;
          color: #8c8c8c;
          margin-bottom: 4px;
          transition: all 0.3s;

          &:last-child {
            margin-bottom: 0;
          }

          i {
            font-size: 14px;
            transition: all 0.3s;
          }

          &.active {
            color: #52c41a;

            i {
              color: #52c41a;
            }
          }
        }
      }

      .gender-selector {
        display: flex;
        gap: 15px;

        .gender-option {
          flex: 1;
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 12px;
          border: 2px solid $border-light;
          border-radius: 8px;
          cursor: pointer;
          transition: all 0.3s;
          color: #2c3e50;

          &:hover {
            border-color: $brand-primary;
          }

          &.active {
            border-color: $brand-primary;
            background: $brand-gradient;
            color: #fff;
          }

          i {
            margin-right: 8px;
            font-size: 18px;
          }

          span {
            font-size: 14px;
            font-weight: 500;
            color: #2c3e50;
          }
        }
      }

      .register-btn {
        width: 100%;
        height: 42px;
        font-size: 16px;
        background: $brand-gradient;
        border: none;

        &:hover {
          opacity: 0.9;
        }
      }

      .form-footer {
        text-align: center;
        margin-top: 20px;
        font-size: 14px;
        color: #595959;

        span {
          margin-right: 5px;
        }
      }
    }

    .card-footer {
      text-align: center;
      margin-top: 30px;
      padding-top: 20px;
      border-top: 1px solid $border-light;
      font-size: 12px;
      color: #595959;

      p {
        margin: 0;
      }
    }
  }
}

.right-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;

  .welcome-content {
    max-width: 500px;

    .welcome-header {
      margin-bottom: 40px;

      .welcome-subtitle {
        font-size: 16px;
        letter-spacing: 3px;
        opacity: 0.9;
        margin: 0 0 10px;
      }

      .welcome-title {
        font-size: 36px;
        font-weight: bold;
        margin: 0;
      }
    }

    .stats-display {
      display: flex;
      align-items: center;
      justify-content: space-around;
      margin-bottom: 40px;
      padding: 30px;
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

      .stat-divider {
        width: 1px;
        height: 50px;
        background: rgba(255, 255, 255, 0.3);
      }
    }

    .features-list {
      margin-bottom: 40px;

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

    .decorative-circle {
      position: relative;
      width: 200px;
      height: 200px;
      margin: 0 auto;

      .circle-ring {
        position: absolute;
        border: 2px solid rgba(255, 255, 255, 0.3);
        border-radius: 50%;
        animation: rotate 20s linear infinite;

        &:nth-child(1) {
          width: 100%;
          height: 100%;
          top: 0;
          left: 0;
        }

        &:nth-child(2) {
          width: 70%;
          height: 70%;
          top: 15%;
          left: 15%;
          animation-direction: reverse;
          animation-duration: 15s;
        }
      }

      .circle-center {
        position: absolute;
        width: 40%;
        height: 40%;
        top: 30%;
        left: 30%;
        background: rgba(255, 255, 255, 0.2);
        border-radius: 50%;
      }
    }
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 1200px) {
  .right-panel {
    display: none;
  }
}

@media (max-width: 768px) {
  .main-content {
    padding: 20px;
  }

  .left-panel .register-card {
    padding: 30px 20px;
  }
}
</style>
