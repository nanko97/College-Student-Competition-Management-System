<template>
  <div class="register-container">
    <!-- 背景装饰 -->
    <div class="background">
      <div class="shape"></div>
      <div class="shape"></div>
      <div class="shape"></div>
    </div>

    <!-- 注册表单卡片 -->
    <div class="register-card">
      <div class="card-header">
        <h1 class="title">
          <i class="el-icon-user-solid"></i>
          用户注册
        </h1>
        <p class="subtitle">欢迎加入大学生竞赛管理系统</p>
      </div>

      <el-form 
        ref="registerForm" 
        :model="form" 
        :rules="rules" 
        label-width="100px"
        class="register-form"
        size="medium"
      >
        <!-- 角色选择 -->
        <el-form-item label="角色类型" prop="role">
          <el-radio-group v-model="form.role" class="role-selector" @change="onRoleChange">
            <el-radio-button label="学生">
              <i class="el-icon-s-custom"></i> 学生
            </el-radio-button>
            <el-radio-button label="教师">
              <i class="el-icon-reading"></i> 教师
            </el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-divider></el-divider>

        <!-- 基本信息 -->
        <el-form-item label="账号" prop="account">
          <el-input 
            v-model="form.account" 
            :placeholder="accountPlaceholder"
            prefix-icon="el-icon-edit"
            clearable
            @blur="checkAccountAvailability"
          >
            <el-button 
              slot="append" 
              @click="checkAccountAvailability" 
              :loading="checkingAccount"
              :disabled="!form.account"
            >
              {{ checkingAccount ? '检查中' : (accountAvailable === null ? '检查' : (accountAvailable ? '✓可用' : '×已存在')) }}
            </el-button>
          </el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="form.password" 
            type="password"
            placeholder="6-20 位字母、数字或特殊字符"
            prefix-icon="el-icon-lock"
            show-password
            clearable
          ></el-input>
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="form.confirmPassword" 
            type="password"
            placeholder="请再次输入密码"
            prefix-icon="el-icon-lock"
            show-password
            clearable
          ></el-input>
        </el-form-item>

        <el-form-item label="姓名" prop="name">
          <el-input 
            v-model="form.name" 
            :placeholder="form.role === '学生' ? '请输入真实姓名' : '请输入真实姓名'"
            prefix-icon="el-icon-user"
            clearable
          ></el-input>
        </el-form-item>

        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio label="男"><i class="el-icon-male"></i> 男</el-radio>
            <el-radio label="女"><i class="el-icon-female"></i> 女</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 扩展信息 -->
        <el-collapse-transition>
          <div v-show="showAdvancedFields">
            <el-form-item label="学院" prop="college">
              <el-input 
                v-model="form.college" 
                placeholder="请输入所在学院"
                prefix-icon="el-icon-school"
                clearable
              ></el-input>
            </el-form-item>

            <el-form-item :label="form.role === '学生' ? '班级' : '职称'" prop="grade">
              <el-input 
                v-model="form.grade" 
                :placeholder="form.role === '学生' ? '请输入班级（如：2101 班）' : '请输入职称（如：讲师）'"
                prefix-icon="el-icon-office-building"
                clearable
              ></el-input>
            </el-form-item>

            <el-form-item label="手机号码" prop="phone">
              <el-input 
                v-model="form.phone" 
                placeholder="请输入手机号码"
                prefix-icon="el-icon-mobile-phone"
                maxlength="11"
                clearable
              ></el-input>
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input 
                v-model="form.email" 
                placeholder="请输入邮箱地址"
                prefix-icon="el-icon-message"
                clearable
              ></el-input>
            </el-form-item>
          </div>
        </el-collapse-transition>

        <el-form-item>
          <el-link 
            type="primary" 
            @click="showAdvancedFields = !showAdvancedFields"
            :underline="false"
            class="toggle-advanced"
          >
            <i :class="showAdvancedFields ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
            {{ showAdvancedFields ? '收起' : '展开更多选项' }}
          </el-link>
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button 
            type="primary" 
            @click="submitForm" 
            :loading="submitting"
            class="submit-btn"
            size="large"
            round
          >
            <i class="el-icon-check"></i> 立即注册
          </el-button>
        </el-form-item>

        <!-- 底部链接 -->
        <div class="form-footer">
          <span>已有账号？</span>
          <el-link type="primary" @click="goToLogin" :underline="false">
            <i class="el-icon-back"></i> 返回登录
          </el-link>
        </div>
      </el-form>
    </div>

    <!-- 右侧说明面板 -->
    <div class="info-panel">
      <div class="info-content">
        <h2>欢迎注册</h2>
        <div class="info-list">
          <div class="info-item">
            <i class="el-icon-circle-check"></i>
            <div>
              <h4>学生用户</h4>
              <p>可以查看竞赛信息、在线报名、查看成绩等</p>
            </div>
          </div>
          <div class="info-item">
            <i class="el-icon-circle-check"></i>
            <div>
              <h4>教师用户</h4>
              <p>可以创建竞赛、管理报名、评审作品等</p>
            </div>
          </div>
        </div>
        <div class="tips">
          <i class="el-icon-info"></i>
          <p>温馨提示：账号注册后需妥善保管，密码建议使用大小写字母 + 数字组合</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Register',
  data() {
    // 自定义验证规则
    const validateAccount = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入账号'));
      } else if (!/^[a-zA-Z0-9_]{4,20}$/.test(value)) {
        callback(new Error('账号格式不正确 (4-20 位字母数字下划线)'));
      } else if (this.accountAvailable === false) {
        callback(new Error('该账号已被使用'));
      } else {
        callback();
      }
    };

    const validatePassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入密码'));
      } else if (!/^[a-zA-Z0-9_@#$%^&+=!]{6,20}$/.test(value)) {
        callback(new Error('密码格式不正确 (6-20 位)'));
      } else {
        callback();
      }
    };

    const validateConfirmPassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请确认密码'));
      } else if (value !== this.form.password) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };

    return {
      form: {
        role: '学生',
        account: '',
        password: '',
        confirmPassword: '',
        name: '',
        gender: '男',
        college: '',
        grade: '',
        phone: '',
        email: ''
      },
      rules: {
        role: [
          { required: true, message: '请选择角色', trigger: 'change' }
        ],
        account: [
          { required: true, validator: validateAccount, trigger: 'blur' }
        ],
        password: [
          { required: true, validator: validatePassword, trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, validator: validateConfirmPassword, trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        phone: [
          { 
            pattern: /^1[3-9]\d{9}$/, 
            message: '请输入正确的手机号码', 
            trigger: 'blur' 
          }
        ],
        email: [
          { 
            type: 'email', 
            message: '请输入正确的邮箱地址', 
            trigger: 'blur' 
          }
        ]
      },
      checkingAccount: false,
      accountAvailable: null, // null: 未检查，true: 可用，false: 已存在
      submitting: false,
      showAdvancedFields: false
    };
  },
  computed: {
    accountPlaceholder() {
      return this.form.role === '学生' ? '请输入学号（4-20 位）' : '请输入工号（4-20 位）';
    }
  },
  methods: {
    // 角色切换时重置账号可用性状态
    onRoleChange() {
      this.accountAvailable = null;
      this.$refs.registerForm.clearValidate('account');
    },

    // 检查账号可用性
    async checkAccountAvailability() {
      if (!this.form.account || this.checkingAccount) return;
      
      this.checkingAccount = true;
      try {
        const res = await this.$http.get('/registration/check-account', {
          params: {
            role: this.form.role,
            account: this.form.account
          }
        });
        
        if (res.data.code === 0) {
          this.accountAvailable = res.data.available;
          if (this.accountAvailable) {
            this.$message.success('账号可用');
          } else {
            this.$message.error('账号已被使用');
          }
        } else {
          this.$message.error('检查失败，请稍后重试');
        }
      } catch (error) {
        console.error('检查账号失败:', error);
        this.$message.error('网络错误，请稍后重试');
      } finally {
        this.checkingAccount = false;
      }
    },

    // 提交表单
    submitForm() {
      this.$refs.registerForm.validate(async (valid) => {
        if (!valid) {
          this.$message.warning('请填写完整并正确填写注册信息');
          return;
        }
        
        if (this.accountAvailable === false) {
          this.$message.warning('请先使用可用的账号');
          return;
        }
        
        if (this.accountAvailable === null) {
          // 自动检查账号
          await this.checkAccountAvailability();
          if (!this.accountAvailable) {
            return;
          }
        }
        
        this.submitting = true;
        try {
          const registerData = {
            ...this.form,
            confirmPassword: undefined // 不发送确认密码字段
          };
          
          await this.$http.post('/registration/register', registerData);
          
          this.$alert('注册成功！即将跳转到登录页面', '提示', {
            confirmButtonText: '确定',
            type: 'success',
            onClose: () => {
              this.$router.push('/login');
            }
          });
        } catch (error) {
          console.error('注册失败:', error);
          const msg = error.response?.data?.msg || error.message || '注册失败，请稍后重试';
          this.$message.error(msg);
        } finally {
          this.submitting = false;
        }
      });
    },

    // 返回登录页
    goToLogin() {
      this.$router.push('/login');
    }
  },
  
  // 页面滚动到顶部
  mounted() {
    window.scrollTo(0, 0);
  }
};
</script>

<style lang="scss" scoped>
// 变量定义
$primary-color: #667eea;
$primary-dark: #764ba2;
$secondary-color: #f093fb;
$accent-color: #4facfe;
$success-color: #67C23A;
$warning-color: #E6A23C;
$danger-color: #F56C6C;
$info-color: #909399;
$text-color: #2c3e50;

.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30px;
  background: linear-gradient(135deg, $primary-color 0%, $primary-dark 50%, $secondary-color 100%);
  background-size: 200% 200%;
  animation: gradientShift 15s ease infinite;
  position: relative;
  overflow: hidden;

  // 动态背景粒子
  .background {
    position: absolute;
    width: 100%;
    height: 100%;
    overflow: hidden;
    z-index: 0;

    .shape {
      position: absolute;
      border-radius: 50%;
      background: radial-gradient(circle, rgba(79, 172, 254, 0.3) 0%, rgba(79, 172, 254, 0.05) 70%);
      animation: float 20s infinite;
      filter: blur(10px);

      &:nth-child(1) {
        width: 500px;
        height: 500px;
        left: -200px;
        top: -200px;
        animation-delay: 0s;
      }

      &:nth-child(2) {
        width: 400px;
        height: 400px;
        right: -150px;
        bottom: -150px;
        animation-delay: 7s;
        background: radial-gradient(circle, rgba(240, 147, 251, 0.3) 0%, rgba(240, 147, 251, 0.05) 70%);
      }

      &:nth-child(3) {
        width: 300px;
        height: 300px;
        right: 20%;
        top: 30%;
        animation-delay: 14s;
        background: radial-gradient(circle, rgba(102, 126, 234, 0.3) 0%, rgba(102, 126, 234, 0.05) 70%);
      }

      &:nth-child(4) {
        width: 150px;
        height: 150px;
        left: 30%;
        bottom: 20%;
        animation-delay: 5s;
      }

      &:nth-child(5) {
        width: 200px;
        height: 200px;
        left: 60%;
        top: 10%;
        animation-delay: 10s;
      }
    }

    // 添加网格背景
    .grid-bg {
      position: absolute;
      width: 100%;
      height: 100%;
      background-image: 
        linear-gradient(rgba(255,255,255,0.05) 1px, transparent 1px),
        linear-gradient(90deg, rgba(255,255,255,0.05) 1px, transparent 1px);
      background-size: 50px 50px;
      animation: gridMove 30s linear infinite;
    }
  }

  // 注册卡片
  .register-card {
    background: rgba(255, 255, 255, 0.98);
    backdrop-filter: blur(20px);
    border-radius: 24px;
    box-shadow: 
      0 25px 80px rgba(0, 0, 0, 0.25),
      0 0 0 1px rgba(255, 255, 255, 0.1) inset;
    padding: 50px 45px;
    width: 100%;
    max-width: 650px;
    z-index: 1;
    animation: slideIn 0.8s cubic-bezier(0.34, 1.56, 0.64, 1);
    position: relative;
    overflow: hidden;
    max-height: 90vh;
    overflow-y: auto;

    // 自定义滚动条
    &::-webkit-scrollbar {
      width: 6px;
    }
    
    &::-webkit-scrollbar-track {
      background: #f1f1f1;
      border-radius: 3px;
    }
    
    &::-webkit-scrollbar-thumb {
      background: $primary-color;
      border-radius: 3px;
      
      &:hover {
        background: $primary-dark;
      }
    }

    // 卡片顶部装饰条
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, $primary-color, $accent-color, $secondary-color);
      background-size: 200% 100%;
      animation: shimmer 3s infinite;
    }

    .card-header {
      text-align: center;
      margin-bottom: 35px;
      position: relative;

      .title {
        color: $text-color;
        font-size: 36px;
        font-weight: 700;
        margin: 0 0 12px 0;
        background: linear-gradient(135deg, $primary-color 0%, $primary-dark 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        animation: titlePulse 2s ease-in-out infinite;
        
        i {
          color: $primary-color;
          margin-right: 12px;
          font-size: 40px;
          animation: iconBounce 2s ease-in-out infinite;
          display: inline-block;
        }
      }

      .subtitle {
        color: $info-color;
        font-size: 15px;
        margin: 0;
        letter-spacing: 0.5px;
      }
    }

    .register-form {
      /deep/ .el-form-item {
        margin-bottom: 24px;
      }

      .role-selector {
        width: 100%;
        
        /deep/ .el-radio-button__inner {
          width: 130px;
          text-align: center;
          font-size: 14px;
          font-weight: 500;
          padding: 12px 16px;
          border-radius: 12px !important;
          transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
          background: #f5f7fa;
          border: 2px solid transparent;
          
          &:hover {
            transform: translateY(-3px) scale(1.05);
            box-shadow: 0 8px 20px rgba(102, 126, 234, 0.2);
            background: #fff;
          }
          
          i {
            margin-right: 6px;
            font-size: 16px;
          }
        }
        
        /deep/ .el-radio-button__orig-radio:checked + .el-radio-button__inner {
          background: linear-gradient(135deg, $primary-color 0%, $primary-dark 100%);
          border-color: $primary-color;
          box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
          transform: translateY(-2px);
          color: #fff;
          
          i {
            animation: iconSpin 0.6s ease;
          }
        }
      }

      /deep/ .el-input-group__prepend {
        background-color: transparent;
        border: none;
        padding: 0 12px;
        
        i {
          color: $primary-color;
          font-size: 18px;
          transition: all 0.3s;
        }
      }

      /deep/ .el-input {
        .el-input__inner {
          height: 46px;
          border-radius: 12px;
          border: 2px solid #e8ecef;
          font-size: 14px;
          transition: all 0.3s;
          background: #fafbfc;
          
          &:focus {
            border-color: $primary-color;
            box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
            background: #fff;
            transform: translateY(-1px);
          }
        }
      }

      /deep/ .el-button--primary {
        background: linear-gradient(135deg, $primary-color 0%, $primary-dark 100%);
        border: none;
        border-radius: 8px;
        transition: all 0.3s;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
        }
      }

      /deep/ .el-divider {
        margin: 20px 0;
        background: linear-gradient(to right, transparent, #e8ecef, transparent);
        
        /deep/ .el-divider__text {
          color: $info-color;
          font-weight: 500;
          font-size: 13px;
        }
      }

      /deep/ .el-collapse {
        border: none;
        margin-top: 15px;
        
        /deep/ .el-collapse-item__header {
          background: linear-gradient(135deg, rgba($primary-color, 0.05) 0%, rgba($primary-dark, 0.05) 100%);
          border: none;
          padding: 14px 20px;
          font-size: 14px;
          font-weight: 600;
          color: $primary-color;
          border-radius: 8px !important;
          transition: all 0.3s;
          
          &:hover {
            background: linear-gradient(135deg, rgba($primary-color, 0.1) 0%, rgba($primary-dark, 0.1) 100%);
          }
          
          /deep/ .el-collapse-item__arrow {
            transition: all 0.3s;
          }
        }
        
        /deep/ .el-collapse-item__wrap {
          border: none;
          background: rgba($primary-color, 0.02);
          border-radius: 0 0 8px 8px;
          padding: 20px;
        }
      }

      /deep/ .el-radio-group {
        /deep/ .el-radio {
          margin-right: 20px;
          font-weight: 500;
          
          /deep/ .el-radio__input.is-checked + .el-radio__label {
            color: $primary-color;
          }
        }
      }

      .form-footer {
        text-align: center;
        margin-top: 30px;
        padding-top: 25px;
        border-top: 2px solid #f0f2f5;
        
        span {
          color: $info-color;
          font-size: 14px;
          margin-right: 10px;
        }
        
        /deep/ .el-link {
          font-weight: 600;
          transition: all 0.3s;
          
          &:hover {
            transform: translateX(-3px);
          }
          
          i {
            animation: arrowLeft 2s ease-in-out infinite;
            display: inline-block;
          }
        }
      }

      .submit-btn {
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
        margin-top: 10px;
        
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
}

// 响应式设计
@media (max-width: 768px) {
  .register-container {
    padding: 15px;
    
    .register-card {
      padding: 35px 25px;
      
      .card-header {
        .title {
          font-size: 28px;
        }
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

@keyframes gridMove {
  0% {
    background-position: 0 0;
  }
  100% {
    background-position: 50px 50px;
  }
}

@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

@keyframes titlePulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.02);
  }
}

@keyframes iconBounce {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  25% {
    transform: translateY(-5px) rotate(-5deg);
  }
  75% {
    transform: translateY(-5px) rotate(5deg);
  }
}

@keyframes iconSpin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes arrowLeft {
  0%, 100% {
    transform: translateX(0);
  }
  50% {
    transform: translateX(-5px);
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

// Element UI 深度样式覆盖
/deep/ .el-form-item__label {
  font-weight: 500;
  color: $text-color;
  font-size: 14px;
}

/deep/ .el-input__inner {
  border-radius: 12px;
  height: 46px;
  transition: all 0.3s;
  
  &:focus {
    border-color: $primary-color;
    box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
  }
}

/deep/ .el-checkbox__inner {
  border-radius: 4px;
  border: 2px solid #dcdfe6;
  transition: all 0.3s;
}

/deep/ .el-button--primary {
  &:hover {
    background: linear-gradient(135deg, lighten($primary-color, 8%) 0%, lighten($primary-dark, 8%) 100%);
  }
}
</style>
        height: 150px;
        right: 30%;
        top: 20%;
        animation-delay: 10s;
      }
    }
  }

  // 注册卡片
  .register-card {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    padding: 40px;
    width: 100%;
    max-width: 560px;
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
        }
      }

      .subtitle {
        color: $info-color;
        font-size: 14px;
        margin: 0;
      }
    }

    .register-form {
      .role-selector {
        width: 100%;
        
        /deep/ .el-radio-button__inner {
          width: 120px;
          text-align: center;
          font-size: 14px;
          padding: 12px 20px;
        }
      }

      .toggle-advanced {
        width: 100%;
        text-align: center;
        display: block;
        margin-top: -10px;
      }

      .submit-btn {
        width: 100%;
        height: 46px;
        font-size: 16px;
        font-weight: 500;
        letter-spacing: 2px;
        background: linear-gradient(135deg, $primary-color 0%, darken($primary-color, 10%) 100%);
        border: none;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4);
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
    max-width: 320px;
    color: #fff;
    animation: fadeIn 0.8s ease-out;

    .info-content {
      h2 {
        font-size: 24px;
        margin: 0 0 30px 0;
        text-align: center;
      }

      .info-list {
        .info-item {
          display: flex;
          align-items: flex-start;
          margin-bottom: 25px;
          
          i {
            font-size: 24px;
            color: $success-color;
            margin-right: 15px;
            flex-shrink: 0;
          }

          h4 {
            font-size: 16px;
            margin: 0 0 8px 0;
          }

          p {
            font-size: 13px;
            margin: 0;
            opacity: 0.9;
            line-height: 1.6;
          }
        }
      }

      .tips {
        background: rgba(255, 255, 255, 0.1);
        border-radius: 10px;
        padding: 15px;
        margin-top: 30px;
        display: flex;
        align-items: flex-start;

        i {
          font-size: 18px;
          margin-right: 10px;
          flex-shrink: 0;
        }

        p {
          font-size: 12px;
          margin: 0;
          line-height: 1.6;
          opacity: 0.9;
        }
      }
    }
  }
}

// 响应式设计
@media (min-width: 968px) {
  .register-container {
    justify-content: flex-start;
    padding: 40px;

    .register-card {
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
    transform: translate(20px, -20px);
  }
  50% {
    transform: translate(-20px, 20px);
  }
  75% {
    transform: translate(20px, 20px);
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
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

// Element UI 组件深度样式覆盖
/deep/ .el-form-item__label {
  font-weight: 500;
  color: #606266;
}

/deep/ .el-input__inner {
  border-radius: 6px;
  transition: all 0.3s;
  
  &:focus {
    border-color: $primary-color;
  }
}

/deep/ .el-radio__label {
  font-size: 14px;
}

/deep/ .el-divider {
  margin: 10px 0;
}
</style>
