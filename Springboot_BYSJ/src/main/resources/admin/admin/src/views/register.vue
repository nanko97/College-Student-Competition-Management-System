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
$primary-color: #409EFF;
$success-color: #67C23A;
$warning-color: #E6A23C;
$danger-color: #F56C6C;
$info-color: #909399;
$bg-gradient-start: #667eea;
$bg-gradient-end: #764ba2;

.register-container {
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
        width: 300px;
        height: 300px;
        left: -100px;
        top: -100px;
        animation-delay: 0s;
      }

      &:nth-child(2) {
        width: 200px;
        height: 200px;
        right: -50px;
        bottom: -50px;
        animation-delay: 5s;
      }

      &:nth-child(3) {
        width: 150px;
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
