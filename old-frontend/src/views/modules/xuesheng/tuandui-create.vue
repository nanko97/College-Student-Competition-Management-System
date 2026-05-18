<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">创建团队</h2>
      <p class="page-subtitle">Create Team</p>
    </div>

    <!-- 提示信息 -->
    <div class="role-tip">
      <i class="el-icon-info"></i>
      <span>提示：创建新团队，招募成员参加比赛</span>
    </div>

    <!-- 创建团队表单 -->
    <div class="search-wrapper">
      <el-form :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="选择竞赛" prop="jingsaiId">
              <el-select 
                v-model="dataForm.jingsaiId" 
                placeholder="请选择竞赛"
                @change="handleJingsaiChange"
                style="width: 100%;">
                <el-option
                  v-for="item in jingsaiList"
                  :key="item.id"
                  :label="item.jingsaimingcheng"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="选择赛道" prop="saidaoId">
              <el-select 
                v-model="dataForm.saidaoId" 
                placeholder="请选择赛道"
                style="width: 100%;">
                <el-option
                  v-for="item in saidaoList"
                  :key="item.id"
                  :label="item.saidaoMingcheng"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="团队名称" prop="tuanduiMingcheng">
              <el-input 
                v-model="dataForm.tuanduiMingcheng" 
                placeholder="请输入团队名称"
                maxlength="50"
                show-word-limit>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人手机" prop="fuzerenShouji">
              <el-input 
                v-model="dataForm.fuzerenShouji" 
                placeholder="请输入手机号码"
                maxlength="11">
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="作品介绍" prop="zuopinJieshao">
          <el-input 
            type="textarea" 
            v-model="dataForm.zuopinJieshao" 
            :rows="4"
            placeholder="请简要介绍您的团队和参赛作品方向..."
            maxlength="500"
            show-word-limit>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitCreate()" :loading="loading">
            <i class="el-icon-plus"></i> 创建团队
          </el-button>
          <el-button @click="resetForm()">
            <i class="el-icon-refresh"></i> 重置
          </el-button>
          <el-button @click="$router.back()">
            <i class="el-icon-back"></i> 返回
          </el-button>
        </el-form-item>
      </el-form>
    </div>

  </div>
</template>

<script>
export default {
  data() {
    return {
      dataForm: {
        jingsaiId: null,
        jingsaimingcheng: '',
        saidaoId: null,
        saidaoMingcheng: '',
        tuanduiMingcheng: '',
        fuzerenXuehao: '',
        fuzerenXingming: '',
        fuzerenShouji: '',
        zuopinJieshao: ''
      },
      jingsaiList: [],
      saidaoList: [],
      loading: false,
      dataRule: {
        jingsaiId: [
          { required: true, message: '请选择竞赛', trigger: 'change' }
        ],
        tuanduiMingcheng: [
          { required: true, message: '请输入团队名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.initData()
  },
  methods: {
    // 初始化数据
    initData() {
      // 获取当前学生信息
      const xuehao = this.$storage.get('username')
      const session = this.$storage.get('session') || {}
      
      this.dataForm.fuzerenXuehao = xuehao
      this.dataForm.fuzerenXingming = session.xingming || ''
      
      // 获取竞赛列表
      this.$http({
        url: 'jingsaixinxi/page',
        method: 'get',
        params: {
          page: 1,
          limit: 1000,
          sfsh: '是'  // 只显示审核通过的竞赛
        }
      }).then(({ data }) => {
        console.log('竞赛列表API响应:', data)
        if (data && data.code === 0) {
          // 兼容多种数据返回格式
          if (data.page && data.page.list) {
            this.jingsaiList = data.page.list
          } else if (data.data && data.data.list) {
            this.jingsaiList = data.data.list
          } else if (Array.isArray(data.data)) {
            this.jingsaiList = data.data
          } else {
            this.jingsaiList = []
          }
          console.log('竞赛列表加载成功:', this.jingsaiList.length, '条', this.jingsaiList)
        } else {
          console.error('竞赛列表加载失败:', data.msg)
          this.$message.error(data.msg || '竞赛列表加载失败')
        }
      }).catch(err => {
        console.error('获取竞赛列表失败:', err)
        this.$message.error('获取竞赛列表失败：' + (err.message || '请检查后端服务'))
      })
    },
    
    // 竞赛改变时加载赛道列表
    handleJingsaiChange(jingsaiId) {
      const jingsai = this.jingsaiList.find(item => item.id === jingsaiId)
      if (jingsai) {
        this.dataForm.jingsaimingcheng = jingsai.jingsaimingcheng
      }
      
      this.$http({
        url: 'jingsai/saidao/list',
        method: 'get',
        params: {
          jingsaiId: jingsaiId,
          sfsh: '是'
        }
      }).then(({ data }) => {
        console.log('赛道列表API响应:', data)
        if (data && data.code === 0) {
          // 兼容多种数据返回格式
          if (data.page && data.page.list) {
            this.saidaoList = data.page.list
          } else if (data.data && data.data.list) {
            this.saidaoList = data.data.list
          } else if (Array.isArray(data.data)) {
            this.saidaoList = data.data
          } else {
            this.saidaoList = []
          }
          console.log('赛道列表加载成功:', this.saidaoList.length, '条', this.saidaoList)
        } else {
          console.error('赛道列表加载失败:', data.msg)
          this.$message.error(data.msg || '赛道列表加载失败')
        }
      }).catch(err => {
        console.error('获取赛道列表失败:', err)
        this.$message.error('获取赛道列表失败：' + (err.message || '请检查后端服务'))
      })
    },
    
    // 提交创建
    submitCreate() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          this.loading = true
          
          this.$http({
            url: 'jingsai/tuandui/create',
            method: 'post',
            data: this.dataForm
          }).then(({ data }) => {
            this.loading = false
            if (data && data.code === 0) {
              this.$message.success(data.msg || '团队创建成功，等待审核')
              this.$router.replace('/xuesheng-my-tuandui')
            } else {
              this.$message.error(data.msg || '创建失败')
            }
          }).catch(() => {
            this.loading = false
          })
        }
      })
    },
    
    // 重置表单
    resetForm() {
      this.$refs.dataForm.resetFields()
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/css/tech-theme.scss';
</style>
