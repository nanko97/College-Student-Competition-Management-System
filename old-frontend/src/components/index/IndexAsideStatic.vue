<template>
  <el-aside class="index-aside" height="calc(100vh - 60px)" :width="isCollapse ? '48px' : '230px'">
    <div class="index-aside-inner menulist" style="height: 100%">
      <div 
        v-for="item in menuList" 
        :key="item.roleName"
        v-if="role === item.roleName"
        class="menulist-item"
        style="height: 100%; border: 0; background-color: #0a0e27"
      >
      <el-menu
        mode="vertical" 
        :unique-opened="true" 
        class="el-menu-demo" 
        style="height:100%;" 
        background-color="#0a0e27" 
        text-color="#a0aec0" 
        active-text-color="#5c7cfa" 
        :default-active="defaultActive"
        :collapse="isCollapse"
        :collapse-transition="false"
      >
        <!-- 首页 -->
        <el-menu-item index="0" @click="menuHandler('')" popper-class="sidebar-tooltip">
          <i class="el-icon-s-home" />
          <span slot="title">首页</span>
        </el-menu-item>
        
        <!-- 个人中心 -->
        <el-submenu index="1" popper-class="sidebar-tooltip">
          <template slot="title">
            <i class="el-icon-user-solid" />
            <span>个人中心</span>
          </template>
          <el-menu-item index="1-1" @click="menuHandler('updatePassword')" popper-class="sidebar-tooltip">
            修改密码
          </el-menu-item>
          <el-menu-item index="1-2" @click="menuHandler('center')" popper-class="sidebar-tooltip">
            个人信息
          </el-menu-item>
        </el-submenu>
        
        <!-- 动态菜单 -->
        <el-submenu 
          v-for="(menu, index) in item.backMenu" 
          :key="menu.menu" 
          :index="(index + 2).toString()"
          popper-class="sidebar-tooltip"
        >
          <template slot="title">
            <i :class="icons[index]" />
            <span>{{ menu.menu }}</span>
          </template>
          <el-menu-item
            v-for="(child, sort) in menu.child"
            :key="sort" 
            :index="`${index + 2}-${sort}`" 
            @click="menuHandler(child.tableName)"
            popper-class="sidebar-tooltip"
          >
            {{ child.menu }}
          </el-menu-item>
        </el-submenu>
      </el-menu>
      </div>
      
      <!-- 折叠/展开按钮 -->
      <div class="collapse-btn" @click="toggleCollapse">
        <i :class="isCollapse ? 'el-icon-s-unfold' : 'el-icon-s-fold'"></i>
      </div>
    </div>
  </el-aside>
</template>
<script>
import menu from '@/utils/menu'

export default {
  name: 'IndexAside',
  data() {
    return {
      menuList: [],
      role: '',
      icons: [
        'el-icon-s-cooperation',
        'el-icon-s-order',
        'el-icon-s-platform',
        'el-icon-s-fold',
        'el-icon-s-unfold',
        'el-icon-s-operation',
        'el-icon-s-marketing',
        'el-icon-s-release',
        'el-icon-s-ticket',
        'el-icon-s-management',
        'el-icon-s-open',
        'el-icon-s-shop',
        'el-icon-s-help',
        'el-icon-s-goods',
        'el-icon-s-promotion',
        'el-icon-s-team',
        'el-icon-s-food'
      ],
      defaultActive: '0',
      isCollapse: false
    }
  },
  created() {
    // 获取当前用户角色
    this.role = this.$storage.get('role') || ''
    
    // 根据角色加载菜单 - 调用 list() 函数
    const menus = menu.list()
    this.menuList = menus || []
    
    console.log('菜单数据:', this.menuList)
    console.log('当前角色:', this.role)
  },
  methods: {
    menuHandler(name) {
      let router = this.$router
      // 如果是空字符串或首页，跳转到首页
      if (!name || name === '') {
        router.push({ name: 'Home' }).catch(err => {
          if (err.name !== 'NavigationDuplicated') {
            console.error('路由跳转失败:', err)
          }
        })
        this.defaultActive = '0'
        return
      }
      // 将 tableName 的下划线转换为短横线以匹配路由定义
      const path = '/' + name.replace(/_/g, '-')
      router.push(path).catch(err => {
        if (err.name !== 'NavigationDuplicated') {
          console.error('路由跳转失败:', err)
        }
      })
    },
    toggleCollapse() {
      this.isCollapse = !this.isCollapse
      // 发送事件通知主内容区域更新边距
      this.$root.$emit('sidebar-collapse', this.isCollapse)
    }
  }
}
</script>
<style lang="scss" scoped>
.index-aside {
  background-color: #0a0e27;
  position: fixed;
  top: 60px;
  left: 0;
  bottom: 0;
  height: auto !important;
  z-index: 999;
  transition: width 0.3s ease;
  
  // 折叠状态下的宽度
  &.el-aside--collapse {
    width: 48px !important;
  }
  
  .index-aside-inner {
    height: 100% !important;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    position: relative;
    
    &::-webkit-scrollbar {
      width: 0;
      height: 0;
    }
    
    &::-webkit-scrollbar-thumb {
      background: #2d3748;
      border-radius: 3px;
      
      &:hover {
        background: #4a5568;
      }
    }
    
    .menulist-item {
      flex: 1;
      ::v-deep .el-menu {
        border-right: none;
        
        // 折叠状态下隐藏滚动条
        &.el-menu--collapse {
          &::-webkit-scrollbar {
            display: none;
          }
          overflow: hidden;
        }
        
        .el-menu-item,
        .el-submenu__title {
          height: 44px;
          line-height: 44px;
          overflow: hidden;
          
          &:hover {
            background-color: #1a202c !important;
          }
        }
        
        // 子菜单标题特殊样式
        .el-submenu__title {
          position: relative;
          
          // 隐藏 Element UI 自带的箭头
          .el-submenu__icon-arrow {
            display: none !important;
          }
          
          // 添加自定义下拉箭头
          &::after {
            content: '';
            position: absolute;
            right: 15px;
            top: 50%;
            transform: translateY(-70%) rotate(45deg);
            width: 6px;
            height: 6px;
            border-right: 2px solid #a0aec0;
            border-bottom: 2px solid #a0aec0;
            transition: all 0.3s ease;
          }
          
          &:hover::after {
            border-color: #5c7cfa;
          }
          
          // 展开状态箭头旋转
          &.is-active::after,
          &.is-opened::after {
            transform: translateY(-30%) rotate(-135deg);
            border-color: #5c7cfa;
          }
        }
        
        // 折叠状态下的菜单项样式
        &.el-menu--collapse {
          .el-menu-item,
          .el-submenu__title {
            height: 48px;
            line-height: 48px;
            padding: 0 !important;
            margin: 8px 6px !important;
            justify-content: center;
            overflow: visible;
            white-space: nowrap;
            border-radius: 8px !important;
            transition: all 0.3s ease;
            
            .el-tooltip {
              padding: 0 !important;
              margin: 0 !important;
            }
            
            // 立即隐藏文字,不做过渡动画
            span {
              display: none !important;
              opacity: 0 !important;
              visibility: hidden !important;
              transition: none !important;
            }
            
            // 图标居中并增大
            i {
              margin: 0 !important;
              font-size: 20px;
              transition: all 0.3s ease;
            }
            
            // 悬停时图标放大并变色
            &:hover {
              background: linear-gradient(135deg, #5c7cfa 0%, #748ffc 100%) !important;
              transform: scale(1.05);
              box-shadow: 0 4px 12px rgba(92, 124, 250, 0.4) !important;
              
              i {
                color: #fff !important;
                transform: scale(1.1);
              }
            }
            
            // 移除箭头
            &::after {
              display: none !important;
            }
          }
        }
        
        .el-menu-item.is-active {
          background-color: #5c7cfa !important;
          color: #fff !important;
        }
        
        // 折叠状态下的下拉菜单样式
        &.el-menu--collapse {
          .el-menu--popup {
            background-color: #0a0e27 !important;
            border: 1px solid rgba(92, 124, 250, 0.3) !important;
            border-radius: 8px !important;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5) !important;
            padding: 6px !important;
            margin-left: 8px !important;
            
            ::v-deep .el-menu-item {
              height: 44px !important;
              line-height: 44px !important;
              font-size: 14px;
              color: #a0aec0 !important;
              border-radius: 6px !important;
              margin: 2px 0 !important;
              transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1) !important;
              position: relative !important;
              cursor: pointer !important;
              overflow: hidden !important;
              
              &:hover {
                background: linear-gradient(135deg, #5c7cfa 0%, #748ffc 100%) !important;
                color: #ffffff !important;
                transform: translateX(8px) scale(1.02) !important;
                box-shadow: 0 6px 20px rgba(92, 124, 250, 0.5), 0 0 12px rgba(92, 124, 250, 0.3) !important;
                font-weight: 600 !important;
                text-shadow: 0 0 6px rgba(255, 255, 255, 0.6) !important;
                
                &::before {
                  content: '';
                  position: absolute;
                  left: 0;
                  top: 50%;
                  transform: translateY(-50%);
                  width: 3px;
                  height: 50%;
                  background: rgba(255, 255, 255, 0.8);
                  border-radius: 2px;
                  box-shadow: 0 0 6px rgba(255, 255, 255, 0.6);
                }
              }
              
              &.is-active {
                background: linear-gradient(135deg, #5c7cfa 0%, #748ffc 100%) !important;
                color: #ffffff !important;
                font-weight: 700 !important;
                text-shadow: 0 0 8px rgba(255, 255, 255, 1), 0 0 16px rgba(92, 124, 250, 0.8) !important;
                box-shadow: 0 4px 16px rgba(92, 124, 250, 0.6), inset 0 0 12px rgba(255, 255, 255, 0.3) !important;
                transform: translateX(4px) !important;
                
                &::before {
                  content: '';
                  position: absolute;
                  left: 0;
                  top: 50%;
                  transform: translateY(-50%);
                  width: 4px;
                  height: 70%;
                  background: #ffffff;
                  border-radius: 2px;
                  box-shadow: 0 0 10px rgba(255, 255, 255, 1);
                }
                
                &:hover {
                  transform: translateX(8px) scale(1.03) !important;
                  box-shadow: 0 8px 24px rgba(92, 124, 250, 0.7), inset 0 0 16px rgba(255, 255, 255, 0.4) !important;
                }
              }
            }
          }
        }
        
        .el-submenu__title:hover {
          background-color: #1a202c !important;
        }
      }
    }
    
    // 折叠/展开按钮
    .collapse-btn {
      position: absolute;
      bottom: 20px;
      left: 50%;
      transform: translateX(-50%);
      width: 40px;
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: #1a202c;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s ease;
      
      i {
        font-size: 18px;
        color: #a0aec0;
        transition: color 0.3s ease;
      }
      
      &:hover {
        background-color: #5c7cfa;
        
        i {
          color: #fff;
        }
      }
    }
  }
}
</style>

<style lang="scss">
// 全局样式 - 折叠状态下的弹出菜单悬停效果
.el-menu--popup.el-menu--vertical {
  background-color: #0a0e27 !important;
  border: 1px solid rgba(92, 124, 250, 0.3) !important;
  border-radius: 8px !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5) !important;
  padding: 6px !important;
  margin-left: 8px !important;
  
  .el-menu-item {
    height: 44px !important;
    line-height: 44px !important;
    font-size: 14px;
    color: #a0aec0 !important;
    border-radius: 6px !important;
    margin: 2px 0 !important;
    transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1) !important;
    position: relative !important;
    cursor: pointer !important;
    overflow: hidden !important;
    
    &:hover {
      background: linear-gradient(135deg, #5c7cfa 0%, #748ffc 100%) !important;
      color: #ffffff !important;
      transform: translateX(8px) scale(1.02) !important;
      box-shadow: 0 6px 20px rgba(92, 124, 250, 0.5), 0 0 12px rgba(92, 124, 250, 0.3) !important;
      font-weight: 600 !important;
      text-shadow: 0 0 6px rgba(255, 255, 255, 0.6) !important;
      
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 3px;
        height: 50%;
        background: rgba(255, 255, 255, 0.8);
        border-radius: 2px;
        box-shadow: 0 0 6px rgba(255, 255, 255, 0.6);
      }
    }
    
    &.is-active {
      background: linear-gradient(135deg, #5c7cfa 0%, #748ffc 100%) !important;
      color: #ffffff !important;
      font-weight: 700 !important;
      text-shadow: 0 0 8px rgba(255, 255, 255, 1), 0 0 16px rgba(92, 124, 250, 0.8) !important;
      box-shadow: 0 4px 16px rgba(92, 124, 250, 0.6), inset 0 0 12px rgba(255, 255, 255, 0.3) !important;
      transform: translateX(4px) !important;
      
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 70%;
        background: #ffffff;
        border-radius: 2px;
        box-shadow: 0 0 10px rgba(255, 255, 255, 1);
      }
      
      &:hover {
        transform: translateX(8px) scale(1.03) !important;
        box-shadow: 0 8px 24px rgba(92, 124, 250, 0.7), inset 0 0 16px rgba(255, 255, 255, 0.4) !important;
      }
    }
  }
}

// 全局样式 - 侧边栏折叠状态下的 Tooltip 提示框动画
.el-tooltip__popper.is-dark {
  &.sidebar-tooltip {
    background: linear-gradient(135deg, #5c7cfa 0%, #748ffc 100%) !important;
    border: 1px solid rgba(255, 255, 255, 0.3) !important;
    border-radius: 8px !important;
    box-shadow: 0 4px 16px rgba(92, 124, 250, 0.5), 0 0 12px rgba(92, 124, 250, 0.3) !important;
    padding: 8px 16px !important;
    font-size: 14px !important;
    font-weight: 600 !important;
    color: #ffffff !important;
    text-shadow: 0 0 6px rgba(255, 255, 255, 0.6) !important;
    transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1) !important;
    animation: tooltipPulse 0.6s ease-in-out;
    
    // 箭头样式
    &[x-placement^="right"] .popper__arrow {
      border-right-color: #5c7cfa !important;
      
      &::after {
        border-right-color: #5c7cfa !important;
      }
    }
    
    &[x-placement^="left"] .popper__arrow {
      border-left-color: #5c7cfa !important;
      
      &::after {
        border-left-color: #5c7cfa !important;
      }
    }
  }
}

// Tooltip 出现时的脉冲动画
@keyframes tooltipPulse {
  0% {
    transform: scale(0.8);
    opacity: 0;
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

// 全局样式 - 侧边栏折叠状态下的图标悬停效果
.el-menu--collapse {
  .el-menu-item,
  .el-submenu__title {
    transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1) !important;
    
    &:hover {
      background: linear-gradient(135deg, #5c7cfa 0%, #748ffc 100%) !important;
      box-shadow: 0 4px 16px rgba(92, 124, 250, 0.6), 0 0 12px rgba(92, 124, 250, 0.3) !important;
      transform: scale(1.05) !important;
      
      i {
        color: #ffffff !important;
        text-shadow: 0 0 8px rgba(255, 255, 255, 1), 0 0 16px rgba(92, 124, 250, 0.8) !important;
        filter: drop-shadow(0 0 6px rgba(92, 124, 250, 0.8));
      }
    }
  }
  
  // 选中状态 - 保持持续发光
  .el-menu-item.is-active {
    background: linear-gradient(135deg, #5c7cfa 0%, #748ffc 100%) !important;
    box-shadow: 0 4px 20px rgba(92, 124, 250, 0.8), 0 0 16px rgba(92, 124, 250, 0.5), inset 0 0 12px rgba(255, 255, 255, 0.3) !important;
    animation: activePulse 2s ease-in-out infinite;
    
    i {
      color: #ffffff !important;
      text-shadow: 0 0 10px rgba(255, 255, 255, 1), 0 0 20px rgba(92, 124, 250, 1) !important;
      filter: drop-shadow(0 0 8px rgba(92, 124, 250, 1));
    }
    
    &:hover {
      box-shadow: 0 6px 24px rgba(92, 124, 250, 0.9), 0 0 20px rgba(92, 124, 250, 0.6), inset 0 0 16px rgba(255, 255, 255, 0.4) !important;
    }
  }
  
  // 选中状态的子菜单标题
  .el-submenu.is-active > .el-submenu__title {
    background: linear-gradient(135deg, #5c7cfa 0%, #748ffc 100%) !important;
    box-shadow: 0 4px 20px rgba(92, 124, 250, 0.8), 0 0 16px rgba(92, 124, 250, 0.5), inset 0 0 12px rgba(255, 255, 255, 0.3) !important;
    animation: activePulse 2s ease-in-out infinite;
    
    i {
      color: #ffffff !important;
      text-shadow: 0 0 10px rgba(255, 255, 255, 1), 0 0 20px rgba(92, 124, 250, 1) !important;
      filter: drop-shadow(0 0 8px rgba(92, 124, 250, 1));
    }
  }
}

// 选中状态的脉冲动画
@keyframes activePulse {
  0%, 100% {
    box-shadow: 0 4px 20px rgba(92, 124, 250, 0.8), 0 0 16px rgba(92, 124, 250, 0.5), inset 0 0 12px rgba(255, 255, 255, 0.3);
  }
  50% {
    box-shadow: 0 4px 24px rgba(92, 124, 250, 1), 0 0 24px rgba(92, 124, 250, 0.7), inset 0 0 16px rgba(255, 255, 255, 0.4);
  }
}
</style>
