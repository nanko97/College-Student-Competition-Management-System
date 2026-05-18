<template>
  <el-aside class="index-aside" height="calc(100vh - 60px)" width="230px">
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
      >
        <!-- 首页 -->
        <el-menu-item index="0" @click="menuHandler('')">
          <i class="el-icon-s-home" />
          <span slot="title">首页</span>
        </el-menu-item>
        
        <!-- 个人中心 -->
        <el-submenu index="1">
          <template slot="title">
            <i class="el-icon-user-solid" />
            <span>个人中心</span>
          </template>
          <el-menu-item index="1-1" @click="menuHandler('updatePassword')">
            修改密码
          </el-menu-item>
          <el-menu-item index="1-2" @click="menuHandler('center')">
            个人信息
          </el-menu-item>
        </el-submenu>
        
        <!-- 动态菜单 -->
        <el-submenu 
          v-for="(menu, index) in item.backMenu" 
          :key="menu.menu" 
          :index="(index + 2).toString()"
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
          >
            {{ child.menu }}
          </el-menu-item>
        </el-submenu>
      </el-menu>
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
      defaultActive: '0'
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
  width: 230px !important;
  height: auto !important;
  z-index: 999;
  
  .index-aside-inner {
    height: 100% !important;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    
    &::-webkit-scrollbar {
      width: 6px;
    }
    
    &::-webkit-scrollbar-thumb {
      background: #2d3748;
      border-radius: 3px;
      
      &:hover {
        background: #4a5568;
      }
    }
    
    .menulist-item {
      ::v-deep .el-menu {
        border-right: none;
        
        .el-menu-item,
        .el-submenu__title {
          height: 50px;
          line-height: 50px;
          
          &:hover {
            background-color: #1a202c !important;
          }
        }
        
        .el-menu-item.is-active {
          background-color: #5c7cfa !important;
          color: #fff !important;
        }
        
        .el-submenu__title:hover {
          background-color: #1a202c !important;
        }
      }
    }
  }
}
</style>
