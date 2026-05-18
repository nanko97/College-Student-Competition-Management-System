<template>
  <el-header class="navbar">
    <!-- 左侧标题和Logo -->
    <div class="title-menu">
      <div class="logo-icon">
        <i class="el-icon-trophy"></i>
      </div>
      <div class="title-name">
        {{ $project.projectName }}
      </div>
    </div>
    
    <!-- 右侧用户信息和退出 -->
    <div class="right-menu">
      <!-- 消息中心 -->
      <div class="message-center" @click="goToMessageCenter">
        <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99">
          <i class="el-icon-message-solid message-icon" :class="{'blink': unreadCount > 0}"></i>
        </el-badge>
      </div>
      
      <!-- 用户下拉菜单 -->
      <el-dropdown @command="handleCommand" trigger="click" class="user-dropdown">
        <div class="user-info">
          <i class="el-icon-user-solid"></i>
          <span class="role-tag">{{ $storage.get('role') }}</span>
          <span class="username">{{ $storage.get('adminName') }}</span>
          <i class="el-icon-arrow-down dropdown-icon"></i>
        </div>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="center">
            <i class="el-icon-user"></i>
            <span>个人中心</span>
          </el-dropdown-item>
          <el-dropdown-item command="password">
            <i class="el-icon-lock"></i>
            <span>修改密码</span>
          </el-dropdown-item>
          <el-dropdown-item divided command="logout">
            <i class="el-icon-switch-button"></i>
            <span>退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </el-header>
</template>

<script>
export default {
  name: 'IndexHeader',
  data() {
    return {
      unreadCount: 0,
      timer: null
    }
  },
  mounted() {
    this.loadUnreadCount()
    // 每30秒刷新一次未读数量
    this.timer = setInterval(() => {
      this.loadUnreadCount()
    }, 30000)
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    // 加载未读消息数量
    async loadUnreadCount() {
      try {
        const { data } = await this.$http.get('/xiaoxitongzhi/unread/count')
        if (data && data.code === 0) {
          this.unreadCount = data.count || 0
        }
      } catch (error) {
        console.error('获取未读消息数量失败:', error)
      }
    },
    
    // 跳转到消息中心
    goToMessageCenter() {
      this.$router.push({ path: '/xiaoxi-tongzhi' })
    },
    
    // 处理下拉菜单命令
    handleCommand(command) {
      switch (command) {
        case 'center':
          this.goToCenter()
          break
        case 'password':
          this.goToUpdatePassword()
          break
        case 'logout':
          this.onLogout()
          break
      }
    },
    
    // 跳转到个人中心
    goToCenter() {
      this.$router.push({ path: '/center' })
    },
    
    // 跳转到修改密码
    goToUpdatePassword() {
      this.$router.push({ path: '/updatePassword' })
    },
    
    onLogout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 清除存储的信息
        this.$storage.clear()
        
        // 清除定时器
        if (this.timer) {
          clearInterval(this.timer)
        }
        
        // 跳转到登录页
        this.$router.replace({ name: 'login' })
        
        this.$message.success('已退出登录')
      }).catch(() => {
        // 取消操作
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px !important;
  padding: 0 30px;
  background: #0a0e27;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  position: relative;
  z-index: 1000;
  width: 100% !important;
  
  .title-menu {
    display: flex;
    align-items: center;
    gap: 12px;
    
    .logo-icon {
      width: 40px;
      height: 40px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      
      i {
        font-size: 24px;
        color: #fff;
      }
    }
    
    .title-name {
      font-size: 18px;
      font-weight: 600;
      color: #fff;
      white-space: nowrap;
    }
  }
  
  .right-menu {
    display: flex;
    align-items: center;
    gap: 24px;
    
    .message-center {
      position: relative;
      cursor: pointer;
      padding: 8px;
      border-radius: 6px;
      transition: all 0.3s;
      
      &:hover {
        background: rgba(102, 126, 234, 0.15);
        transform: translateY(-1px);
      }
      
      .message-icon {
        font-size: 22px;
        color: #cbd5e1;
        transition: color 0.3s;
        
        &.blink {
          animation: blink-animation 1.5s ease-in-out infinite;
          color: #fbbf24;
        }
      }
    }
    
    .user-dropdown {
      cursor: pointer;
      
      .user-info {
        display: flex;
        align-items: center;
        gap: 8px;
        color: #cbd5e1;
        font-size: 14px;
        padding: 8px 12px;
        border-radius: 6px;
        transition: all 0.3s;
        
        &:hover {
          background: rgba(102, 126, 234, 0.15);
        }
        
        i {
          font-size: 18px;
          color: #667eea;
        }
        
        .role-tag {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: #fff;
          padding: 2px 8px;
          border-radius: 4px;
          font-size: 12px;
          font-weight: 500;
        }
        
        .username {
          color: #fff;
          font-weight: 600;
          font-size: 15px;
        }
        
        .dropdown-icon {
          font-size: 14px;
          color: #cbd5e1;
          margin-left: 4px;
          transition: transform 0.3s;
        }
      }
    }
  }
}

// 消息闪烁动画
@keyframes blink-animation {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.5;
    transform: scale(1.1);
  }
}
</style>
