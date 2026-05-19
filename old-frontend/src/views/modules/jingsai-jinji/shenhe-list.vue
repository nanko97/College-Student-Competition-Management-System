<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">我的晋级记录</h2>
      <p class="page-subtitle">My Promotion Records</p>
    </div>

    <!-- 提示信息 -->
    <div class="role-tip">
      <i class="el-icon-info"></i>
      <span>提示：晋级由教师根据竞赛成绩和排名自动审核，学生无需主动申请</span>
    </div>

    <!-- 统计信息 -->
    <div class="statistics-wrapper">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="stat-card stat-purple">
            <div class="stat-icon"><i class="el-icon-document"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.totalJinji || 0 }}</div>
              <div class="stat-label">晋级记录总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card stat-pink">
            <div class="stat-icon"><i class="el-icon-time"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.pendingCount || 0 }}</div>
              <div class="stat-label">待审核</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card stat-blue">
            <div class="stat-icon"><i class="el-icon-success"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.approvedCount || 0 }}</div>
              <div class="stat-label">已通过</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 列表页 -->
    <div v-if="showFlag">
      <!-- 搜索区域 -->
      <div class="search-wrapper">
        <el-form :inline="true" :model="searchForm" class="tech-search-form">
          <el-row :gutter="20" class="search-row">
            <el-form-item label="竞赛名称">
              <el-input 
                v-model="searchForm.jingsaimingcheng" 
                placeholder="请输入竞赛名称" 
                clearable
                prefix-icon="el-icon-trophy"
              ></el-input>
            </el-form-item>
            <el-form-item label="晋级状态">
              <el-select v-model="searchForm.jinjiZhuangtai" placeholder="请选择状态" clearable style="width: 150px;">
                <el-option label="已通过" value="已通过"></el-option>
                <el-option label="待审核" value="待审核"></el-option>
                <el-option label="已驳回" value="已驳回"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" @click="search()">查询</el-button>
            </el-form-item>
          </el-row>
        </el-form>
      </div>

      <!-- 数据表格 -->
      <div class="table-wrapper">
        <el-table 
          class="tech-table"
          :data="dataList" 
          v-loading="dataListLoading" 
          border 
          stripe>
          <el-table-column label="索引" header-align="center" align="center" width="60">
            <template slot-scope="scope">
              {{ (pageIndex - 1) * pageSize + scope.$index + 1 }}
            </template>
          </el-table-column>
          <el-table-column prop="xuehao" header-align="center" align="center" label="学号">
            <template slot-scope="scope">
              <span style="font-weight: 500;">{{ scope.row.xuehao }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="xueshengxingming" header-align="center" align="center" label="学生姓名">
            <template slot-scope="scope">
              <span style="color: #5c7cfa;">{{ scope.row.xueshengxingming }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="yuanJingsaimingcheng" header-align="center" align="center" label="原竞赛" show-overflow-tooltip></el-table-column>
          <el-table-column prop="yuanJibie" header-align="center" align="center" label="原级别">
            <template slot-scope="scope">
              <el-tag type="info" size="small">{{ scope.row.yuanJibie }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="xinJingsaimingcheng" header-align="center" align="center" label="目标竞赛" show-overflow-tooltip></el-table-column>
          <el-table-column prop="xinJibie" header-align="center" align="center" label="目标级别">
            <template slot-scope="scope">
              <el-tag type="success" size="small">{{ scope.row.xinJibie }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="jinjiZhuangtai" header-align="center" align="center" label="晋级状态">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.jinjiZhuangtai)" size="small">{{ scope.row.jinjiZhuangtai }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column fixed="right" header-align="center" align="center" width="150" label="审核时间">
            <template slot-scope="scope">
              <span>{{ scope.row.shenheShijian || '-' }}</span>
            </template>
          </el-table-column>
        </el-table>
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
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      searchForm: {
        jingsaimingcheng: '',
        jinjiZhuangtai: ''
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      showFlag: true,
      // 统计信息
      statistics: {
        totalJinji: 0,
        pendingCount: 0,
        approvedCount: 0
      }
    }
  },
  created() {
    this.getDataList()
    this.getStatistics()
  },
  activated() {
    this.getDataList()
  },
  methods: {
    getStatusType(status) {
      if (status === '已通过') return 'success'
      if (status === '已驳回') return 'danger'
      return 'warning'
    },
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: 'jingsai/jinji/my/list',
        method: 'get',
        params: {
          'page': this.pageIndex,
          'limit': this.pageSize
        }
      }).then(({data}) => {
        console.log('=== 晋级记录API响应 ===')
        console.log('完整响应:', JSON.stringify(data, null, 2))
        
        if (data && data.code === 0) {
          // 后端返回格式: { code: 0, page: { total: 10, list: [...] } }
          if (data.page) {
            console.log('data.page:', data.page)
            console.log('data.page.list:', data.page.list)
            console.log('data.page.total:', data.page.total)
            
            this.dataList = data.page.list || []
            this.totalPage = data.page.total || 0
            
            console.log('解析后的数据:')
            console.log('- dataList:', this.dataList)
            console.log('- totalPage:', this.totalPage)
            console.log('- 第一条数据:', this.dataList[0])
          } else {
            console.warn('响应中没有page对象')
            this.dataList = []
            this.totalPage = 0
          }
        } else {
          console.error('查询失败:', data.msg)
          this.dataList = []
          this.totalPage = 0
          this.$message.error(data.msg || '查询失败')
        }
        this.dataListLoading = false
      }).catch(err => {
        console.error('晋级记录查询异常:', err)
        this.dataList = []
        this.totalPage = 0
        this.dataListLoading = false
        this.$message.error('查询失败：' + (err.message || '请检查后端服务'))
      })
    },
    search() {
      this.pageIndex = 1
      this.getDataList()
    },
    sizeChangeHandle(val) {
      this.pageSize = val
      this.pageIndex = 1
      this.getDataList()
    },
    currentChangeHandle(val) {
      this.pageIndex = val
      this.getDataList()
    },
    // 获取统计信息
    getStatistics() {
      this.$http({
        url: 'jingsai/jinji/statistics',
        method: 'get'
      }).then(({data}) => {
        if (data && data.code === 0) {
          const stats = data.data || {}
          // 适配后端返回的字段名
          this.statistics = {
            totalJinji: stats.totalJilu || 0,
            pendingCount: stats.pendingCount || 0,
            approvedCount: stats.approvedCount || 0
          }
        }
      }).catch((error) => {
        console.error('获取统计数据失败:', error)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/css/tech-theme.scss';
@import '@/assets/css/statistics-cards.scss';

.tech-search-form {
  ::v-deep .el-form-item {
    margin-bottom: 0;
    margin-right: 20px;
  }
  ::v-deep .el-input__inner { width: 200px; }
  ::v-deep .el-select .el-input__inner { width: 150px; }
  // 让最后一个form-item（查询按钮）垂直居中
  ::v-deep .el-form-item:last-child {
    margin-right: 0;
    .el-form-item__content {
      display: flex;
      align-items: center;
      line-height: 32px; // 与输入框高度一致
    }
  }
}

.tech-table {
  ::v-deep .el-table__body tr:hover > td {
    background: rgba(#5c7cfa, 0.08) !important;
  }
  
  ::v-deep .el-button--mini {
    padding: 7px 12px;
    margin: 2px;
  }
}

.tech-pagination { margin-top: 20px; }
</style>
