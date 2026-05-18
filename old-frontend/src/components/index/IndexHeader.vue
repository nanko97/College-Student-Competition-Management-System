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
      <div class="user-info">
        <i class="el-icon-user-solid"></i>
        <span class="role-tag">{{ $storage.get('role') }}</span>
        <span class="username">{{ $storage.get('adminName') }}</span>
      </div>
      <div class="logout" @click="onLogout">
        <i class="el-icon-switch-button"></i>
        <span>退出登录</span>
      </div>
    </div>
  </el-header>
</template>

<script>
export default {
  name: 'IndexHeader',
  methods: {
    onLogout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 清除存储的信息
        this.$storage.clear()
        
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
    
    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #cbd5e1;
      font-size: 14px;
      
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
    }
    
    .logout {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 8px 16px;
      color: #f87171;
      font-size: 14px;
      font-weight: 500;
      cursor: pointer;
      border-radius: 6px;
      transition: all 0.3s;
      
      i {
        font-size: 16px;
      }
      
      &:hover {
        background: rgba(248, 113, 113, 0.15);
        transform: translateY(-1px);
      }
    }
  }
}
</style>
