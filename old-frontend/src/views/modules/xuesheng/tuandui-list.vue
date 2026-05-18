<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">申请加入团队</h2>
      <p class="page-subtitle">Apply to Join Team</p>
    </div>

    <!-- 提示信息 -->
    <div class="role-tip">
      <i class="el-icon-info"></i>
      <span>提示：浏览所有开放团队，选择申请加入</span>
    </div>

    <!-- 搜索区域 -->
    <div class="search-wrapper">
      <el-form :inline="true" :model="searchForm" class="tech-search-form">
        <el-form-item label="团队名称">
          <el-input 
            v-model="searchForm.tuanduiMingcheng" 
            placeholder="请输入团队名称" 
            clearable
            prefix-icon="el-icon-user-solid"
          ></el-input>
        </el-form-item>
        <el-form-item label="竞赛名称">
          <el-input 
            v-model="searchForm.jingsaimingcheng" 
            placeholder="请输入竞赛名称" 
            clearable
            prefix-icon="el-icon-trophy"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="getDataList()">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetSearch()">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <div class="table-wrapper">
      <el-table 
        class="tech-table"
        :data="dataList"
        v-loading="dataListLoading"
        border>
      
      <el-table-column label="序号" type="index" width="60" align="center"></el-table-column>
      
      <el-table-column prop="tuanduiBianhao" label="团队编号" align="center" width="180"></el-table-column>
      
      <el-table-column prop="tuanduiMingcheng" label="团队名称" align="center" min-width="200">
        <template slot-scope="scope">
          <span style="font-weight: 600; color: #5c7cfa;">{{scope.row.tuanduiMingcheng}}</span>
        </template>
      </el-table-column>
      
      <el-table-column prop="jingsaimingcheng" label="竞赛名称" align="center" min-width="200"></el-table-column>
      
      <el-table-column prop="fuzerenXingming" label="负责人" align="center" width="120"></el-table-column>
      
      <el-table-column prop="chengyuanRenshu" label="成员数" align="center" width="100">
        <template slot-scope="scope">
          <span style="font-weight: bold; color: #51cf66;">{{scope.row.chengyuanRenshu}}</span> 人
        </template>
      </el-table-column>
      
      <el-table-column prop="shenheZhuangtai" label="审核状态" align="center" width="120">
        <template slot-scope="scope">
          <el-tag :type="getShenheType(scope.row.shenheZhuangtai)" effect="dark">
            {{scope.row.shenheZhuangtai}}
          </el-tag>
        </template>
      </el-table-column>
      
      <el-table-column label="操作" width="200" align="center">
        <template slot-scope="scope">
          <el-button 
            type="primary" 
            icon="el-icon-view" 
            size="mini" 
            @click="viewMembers(scope.row)">
            查看成员
          </el-button>
          <el-button 
            type="success" 
            icon="el-icon-circle-plus" 
            size="mini" 
            @click="applyJoin(scope.row)"
            :disabled="scope.row.shenheZhuangtai !== '已通过'">
            申请加入
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    </div>
    
    <!-- 分页 -->
    <el-pagination
      class="tech-pagination"
      @size-change="sizeChangeHandle"
      @current-change="currentChangeHandle"
      :current-page="pageIndex"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pageSize"
      :total="totalPage"
      layout="total, sizes, prev, pager, next, jumper"
    ></el-pagination>

    <!-- 团队成员对话框 -->
    <el-dialog
      :title="'团队成员 - ' + currentTeam.tuanduiMingcheng"
      :visible.sync="memberDialogVisible"
      width="700px">
      <el-table :data="memberList" border>
        <el-table-column label="序号" type="index" width="60" align="center"></el-table-column>
        <el-table-column prop="xuehao" label="学号" align="center" width="150"></el-table-column>
        <el-table-column prop="xueshengxingming" label="姓名" align="center" width="120"></el-table-column>
        <el-table-column prop="juese" label="角色" align="center" width="120">
          <template slot-scope="scope">
            <el-tag :type="scope.row.juese === '负责人' ? 'danger' : 'info'" effect="dark">
              {{scope.row.juese}}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ruzuiShijian" label="加入时间" align="center"></el-table-column>
      </el-table>
    </el-dialog>

    <!-- 申请加入对话框 -->
    <el-dialog
      title="申请加入团队"
      :visible.sync="applyDialogVisible"
      width="600px">
      <el-form :model="applyForm" :rules="applyRules" ref="applyForm" label-width="100px">
        <el-form-item label="团队名称">
          <el-input v-model="applyForm.tuanduiMingcheng" disabled></el-input>
        </el-form-item>
        <el-form-item label="竞赛名称">
          <el-input v-model="applyForm.jingsaimingcheng" disabled></el-input>
        </el-form-item>
        <el-form-item label="申请理由" prop="applyReason">
          <el-input 
            type="textarea" 
            v-model="applyForm.applyReason" 
            :rows="4"
            placeholder="请说明申请加入该团队的理由..."
            maxlength="200"
            show-word-limit>
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApply()">提交申请</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      searchForm: {
        tuanduiMingcheng: '',
        jingsaimingcheng: ''
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      memberDialogVisible: false,
      applyDialogVisible: false,
      memberList: [],
      currentTeam: {},
      applyForm: {
        tuanduiId: null,
        tuanduiMingcheng: '',
        jingsaimingcheng: '',
        applyReason: ''
      },
      applyRules: {
        applyReason: [
          { required: true, message: '请填写申请理由', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getDataList()
  },
  methods: {
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true
      let params = {
        page: this.pageIndex,
        limit: this.pageSize,
        sort: 'id'
      }
      
      if (this.searchForm.tuanduiMingcheng) {
        params.tuanduiMingcheng = this.searchForm.tuanduiMingcheng
      }
      if (this.searchForm.jingsaimingcheng) {
        params.jingsaimingcheng = this.searchForm.jingsaimingcheng
      }
      
      this.$http({
        url: 'jingsai/tuandui/list',
        method: 'get',
        params: params
      }).then(({ data }) => {
        console.log('【完整响应】:', data)
        console.log('【响应类型】:', typeof data)
        console.log('【响应keys】:', Object.keys(data))
        
        if (data && data.code === 0) {
          console.log('【page对象】:', data.page)
          console.log('【page类型】:', typeof data.page)
          
          if (data.page) {
            console.log('【page.list】:', data.page.list)
            console.log('【page.total】:', data.page.total)
            
            if (data.page.list && Array.isArray(data.page.list)) {
              this.dataList = data.page.list
              this.totalPage = data.page.total || 0
              console.log('✅ 团队数据加载成功:', this.dataList.length, '条')
            } else {
              console.warn('⚠️ page.list 不是数组:', data.page.list)
              this.dataList = []
            }
          } else {
            console.warn('⚠️ 没有page对象')
            this.dataList = []
          }
        } else {
          console.error('❌ API返回错误:', data)
          this.dataList = []
        }
        this.dataListLoading = false
      }).catch((error) => {
        console.error('❌ 请求失败:', error)
        this.dataList = []
        this.dataListLoading = false
      })
    },
    
    // 分页大小改变
    sizeChangeHandle(val) {
      this.pageSize = val
      this.pageIndex = 1
      this.getDataList()
    },
    
    // 当前页改变
    currentChangeHandle(val) {
      this.pageIndex = val
      this.getDataList()
    },
    
    // 重置搜索
    resetSearch() {
      this.searchForm = {
        tuanduiMingcheng: '',
        jingsaimingcheng: ''
      }
      this.pageIndex = 1
      this.getDataList()
    },
    
    // 获取审核状态类型
    getShenheType(status) {
      if (status === '已通过') return 'success'
      if (status === '已驳回') return 'danger'
      if (status === '待审核') return 'warning'
      return 'info'
    },
    
    // 查看团队成员
    viewMembers(row) {
      this.currentTeam = row
      this.memberDialogVisible = true
      
      this.$http({
        url: `jingsai/tuandui/chengyuan/list/${row.id}`,
        method: 'get'
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.memberList = data.data || []
        }
      })
    },
    
    // 申请加入
    applyJoin(row) {
      const xuehao = this.$storage.get('username')
      if (!xuehao) {
        this.$message.error('请先登录')
        return
      }
      
      this.currentTeam = row
      this.applyForm = {
        tuanduiId: row.id,
        tuanduiMingcheng: row.tuanduiMingcheng,
        jingsaimingcheng: row.jingsaimingcheng || '',
        applyReason: ''
      }
      this.applyDialogVisible = true
    },
    
    // 提交申请
    submitApply() {
      this.$refs.applyForm.validate((valid) => {
        if (valid) {
          const xuehao = this.$storage.get('username')
          const xueshengxingming = this.$storage.get('session')?.xingming || ''
          
          const params = {
            tuanduiId: this.applyForm.tuanduiId,
            xuehao: xuehao,
            xueshengxingming: xueshengxingming,
            applyReason: this.applyForm.applyReason
          }
          
          this.$http({
            url: 'tuandui/apply/join',
            method: 'post',
            data: params
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message.success('申请已提交，等待负责人审核')
              this.applyDialogVisible = false
            } else {
              this.$message.error(data.msg || '申请失败')
            }
          })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/css/tech-theme.scss';

.search-wrapper {
  margin-bottom: 20px;
}

.tech-search-form {
  ::v-deep .el-form-item {
    margin-bottom: 0;
    margin-right: 20px;
  }
}

.tech-table {
  ::v-deep .el-table__body tr:hover > td {
    background: rgba(#5c7cfa, 0.08) !important;
  }
}

.tech-pagination { 
  margin-top: 20px;
  text-align: right;
}
</style>
