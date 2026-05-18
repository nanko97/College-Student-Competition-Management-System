import Vue from 'vue';
//配置路由
import VueRouter from 'vue-router'
//1.创建组件
import Index from '@/views/index'
import Home from '@/views/home'
import Login from '@/views/login'
import NotFound from '@/views/404'
import UpdatePassword from '@/views/update-password'
import pay from '@/views/pay'
import register from '@/views/register'
import center from '@/views/center'
import jingsaixinxi from '@/views/modules/jingsaixinxi/list'
import xuesheng from '@/views/modules/xuesheng/list'
import jingsaibaoming from '@/views/modules/jingsaibaoming/list'
import jiaoshi from '@/views/modules/jiaoshi/list'
import zuopindafen from '@/views/modules/zuopindafen/list'
// 新增功能模块路由
import jingsaiFeiyong from '@/views/modules/jingsai-feiyong/list'
import jingsaiJiaofei from '@/views/modules/jingsai-jiaofei/list'
import jingsaiJiaofeiShenhe from '@/views/modules/jingsai-jiaofei/shenhe-list'
import jingsaiJinjiGuanxi from '@/views/modules/jingsai-jinji/guanxi-list'
import jingsaiJinjiShenhe from '@/views/modules/jingsai-jinji/shenhe-list'
import jingsaiJinjiJiaoshiShenhe from '@/views/modules/jingsai-jinji/jiaoshi-shenhe-list'
import jingsaiJibieBanben from '@/views/modules/jingsai-jibie/banben-list'
import jingsaiSaidao from '@/views/modules/jingsai-saidao/list'
import jingsaiTuandui from '@/views/modules/jingsai-tuandui/list'
import jingsaiRenyuanBiangueng from '@/views/modules/jingsai-renyuan-biangueng/apply'
import jingsaiRenyuanBianguengShenhe from '@/views/modules/jingsai-renyuan-biangueng/shenhe-list'
import zuopindafenFuheShenhe from '@/views/modules/zuopindafen/fuhe-shenhe'
import zuopindafenMyFuhe from '@/views/modules/zuopindafen/my-fuhe'
// 学生角色专属路由
import xueshengMyJiaofei from '@/views/modules/xuesheng/my-jiaofei'
import xueshengMyJinji from '@/views/modules/xuesheng/my-jinji'
import xueshengMyZuopin from '@/views/modules/xuesheng/my-zuopin'
import xueshengMyTuandui from '@/views/modules/xuesheng/my-tuandui'
import xueshengTuanduiCreate from '@/views/modules/xuesheng/tuandui-create'
import xueshengTuanduiJoin from '@/views/modules/xuesheng/tuandui-list'
// 作品管理路由
import zuopinList from '@/views/modules/zuopin/list'

Vue.use(VueRouter);


//2.配置路由   注意：名字
const routes = [{
  path: '/index',
  // 注意：父路由有子路由时，不应该设置name，否则会导致默认子路由不渲染
  component: Index,
  children: [{
    // 这里不设置值，是把main作为默认页面
    path: '/',
    name: 'Home',
    component: Home,
    meta: {icon: '', title: 'center'}
  }, {
    path: '/updatePassword',
    name: '修改密码',
    component: UpdatePassword,
    meta: {icon: '', title: 'updatePassword'}
  }, {
    path: '/pay',
    name: '支付',
    component: pay,
    meta: {icon: '', title: 'pay'}
  }, {
    path: '/center',
    name: '个人信息',
    component: center,
    meta: {icon: '', title: 'center'}
  }
    , {
      path: '/jingsaixinxi',
      name: '竞赛信息',
      component: jingsaixinxi
    }
    , {
      path: '/xuesheng',
      name: '学生',
      component: xuesheng
    }
    , {
      path: '/jingsaibaoming',
      name: '竞赛报名',
      component: jingsaibaoming
    }
    , {
      path: '/jiaoshi',
      name: '教师',
      component: jiaoshi
    }
    , {
      path: '/zuopindafen',
      name: '作品打分',
      component: zuopindafen
    }
    , {
      path: '/zuopindafen-fuhe-shenhe',
      name: '成绩复核审核',
      component: zuopindafenFuheShenhe
    }
    , {
      path: '/zuopindafen-my-fuhe',
      name: '我的复核',
      component: zuopindafenMyFuhe
    }
    , {
      path: '/jingsai-feiyong',
      name: '费用配置',
      component: jingsaiFeiyong
    }
    , {
      path: '/jingsai-jiaofei',
      name: '缴费记录',
      component: jingsaiJiaofei
    }
    , {
      path: '/jingsai-jiaofei-shenhe',
      name: '缴费审核',
      component: jingsaiJiaofeiShenhe
    }
    , {
      path: '/jingsai-jinji-guanxi',
      name: '晋级关系',
      component: jingsaiJinjiGuanxi
    }
    , {
      path: '/jingsai-jinji-shenhe',
      name: '晋级审核（学生查看）',
      component: jingsaiJinjiShenhe
    }
    , {
      path: '/jingsai-jinji-jiaoshi-shenhe',
      name: '晋级审核管理',
      component: jingsaiJinjiJiaoshiShenhe
    }
    , {
      path: '/jingsai-jibie-banben',
      name: '级别版本',
      component: jingsaiJibieBanben
    }
    , {
      path: '/jingsai-saidao',
      name: '赛道管理',
      component: jingsaiSaidao
    }
    , {
      path: '/jingsai-tuandui',
      name: '团队管理',
      component: jingsaiTuandui
    }
    , {
      path: '/jingsai-renyuan-biangueng',
      name: '人员变更申请',
      component: jingsaiRenyuanBiangueng
    }
    , {
      path: '/jingsai-renyuan-biangueng-shenhe',
      name: '人员变更审核',
      component: jingsaiRenyuanBianguengShenhe
    }
    , {
      path: '/xuesheng-my-jiaofei',
      name: '我的缴费',
      component: xueshengMyJiaofei
    }
    , {
      path: '/xuesheng-my-jinji',
      name: '我的晋级',
      component: xueshengMyJinji
    }
    , {
      path: '/xuesheng-my-zuopin',
      name: '我的作品',
      component: xueshengMyZuopin
    }
    , {
      path: '/xuesheng-my-tuandui',
      name: '我的团队',
      component: xueshengMyTuandui
    }
    , {
      path: '/xuesheng-tuandui-create',
      name: '创建团队',
      component: xueshengTuanduiCreate
    }
    , {
      path: '/xuesheng-tuandui-join',
      name: '申请加入团队',
      component: xueshengTuanduiJoin
    }
    , {
      path: '/zuopin',
      name: '作品管理',
      component: zuopinList
    }
  ]
},
  {
    path: '/login',
    name: 'login',
    component: Login,
    meta: {icon: '', title: 'login'}
  },
  {
    path: '/register',
    name: 'register',
    component: register,
    meta: {icon: '', title: 'register'}
  },
  {
    path: '/',
    name: 'Root',
    redirect: '/index'
  }, /*默认跳转路由*/
  {
    path: '*',
    component: NotFound
  }
]
//3.实例化VueRouter  注意：名字
const router = new VueRouter({
  mode: 'hash',
  /*hash模式改为history*/
  routes // （缩写）相当于 routes: routes
})

// 解决 NavigationDuplicated 错误
// 在 Vue Router 3.x 中，导航到相同路由时会抛出 NavigationDuplicated 错误
// 这里重写 push 和 replace 方法，忽略该错误
const originalPush = VueRouter.prototype.push
const originalReplace = VueRouter.prototype.replace

VueRouter.prototype.push = function push(location, onResolve, onReject) {
  if (onResolve || onReject) {
    return originalPush.call(this, location, onResolve, onReject)
  }
  return originalPush.call(this, location).catch(err => {
    if (err.name !== 'NavigationDuplicated') {
      return Promise.reject(err)
    }
  })
}

VueRouter.prototype.replace = function replace(location, onResolve, onReject) {
  if (onResolve || onReject) {
    return originalReplace.call(this, location, onResolve, onReject)
  }
  return originalReplace.call(this, location).catch(err => {
    if (err.name !== 'NavigationDuplicated') {
      return Promise.reject(err)
    }
  })
}

export default router;
