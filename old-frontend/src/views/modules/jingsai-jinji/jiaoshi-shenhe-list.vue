<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">晋级审核管理</h2>
      <p class="page-subtitle">Promotion Review Management</p>
    </div>

    <!-- 提示信息 -->
    <div class="role-tip">
      <i class="el-icon-info"></i>
      <span>提示：请仔细审核学生的晋级申请，确保符合竞赛晋级规则</span>
    </div>

    <!-- 统计卡片 -->
    <div class="statistics-wrapper">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card stat-pink">
            <div class="stat-icon"><i class="el-icon-time"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.pendingCount || 0 }}</div>
              <div class="stat-label">待审核</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card stat-blue">
            <div class="stat-icon"><i class="el-icon-check"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.approvedCount || 0 }}</div>
              <div class="stat-label">已通过</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card stat-orange">
            <div class="stat-icon"><i class="el-icon-close"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.rejectedCount || 0 }}</div>
              <div class="stat-label">已驳回</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card stat-purple">
            <div class="stat-icon"><i class="el-icon-document"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.totalCount || 0 }}</div>
              <div class="stat-label">总记录数</div>
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
            <el-form-item label="学生姓名">
              <el-input 
                v-model="searchForm.xueshengXingming" 
                placeholder="请输入学生姓名" 
                clearable
                prefix-icon="el-icon-user"
              ></el-input>
            </el-form-item>
            <el-form-item label="学号">
              <el-input 
                v-model="searchForm.xuehao" 
                placeholder="请输入学号" 
                clearable
                prefix-icon="el-icon-notebook-2"
              ></el-input>
            </el-form-item>
            <el-form-item label="审核状态">
              <el-select v-model="searchForm.jinjiZhuangtai" placeholder="请选择状态" clearable style="width: 150px;">
                <el-option label="待审核" value="待审核"></el-option>
                <el-option label="已通过" value="已通过"></el-option>
                <el-option label="已驳回" value="已驳回"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" @click="search()">查询</el-button>
              <el-button type="success" icon="el-icon-refresh" @click="refreshData()">刷新</el-button>
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
          <el-table-column prop="xuehao" header-align="center" align="center" label="学号" width="120">
            <template slot-scope="scope">
              <span style="font-weight: 500;">{{ scope.row.xuehao }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="xueshengXingming" header-align="center" align="center" label="学生姓名" width="100">
            <template slot-scope="scope">
              <span style="color: #5c7cfa; font-weight: 600;">{{ scope.row.xueshengXingming }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="yuanJingsaimingcheng" header-align="center" align="center" label="原竞赛" show-overflow-tooltip min-width="150"></el-table-column>
          <el-table-column prop="yuanJibie" header-align="center" align="center" label="原级别" width="100">
            <template slot-scope="scope">
              <el-tag type="info" size="small">{{ scope.row.yuanJibie }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="xinJingsaimingcheng" header-align="center" align="center" label="目标竞赛" show-overflow-tooltip min-width="150"></el-table-column>
          <el-table-column prop="xinJibie" header-align="center" align="center" label="目标级别" width="100">
            <template slot-scope="scope">
              <el-tag type="success" size="small">{{ scope.row.xinJibie }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="jinjiYuanyin" header-align="center" align="center" label="晋级原因" show-overflow-tooltip min-width="180"></el-table-column>
          <el-table-column prop="jinjiZhuangtai" header-align="center" align="center" label="审核状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.jinjiZhuangtai)" size="small">{{ scope.row.jinjiZhuangtai }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="shenheShijian" header-align="center" align="center" label="审核时间" width="160">
            <template slot-scope="scope">
              <span>{{ scope.row.shenheShijian || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column fixed="right" header-align="center" align="center" width="180" label="操作">
            <template slot-scope="scope">
              <el-button 
                v-if="scope.row.jinjiZhuangtai === '待审核'" 
                type="success" 
                icon="el-icon-check" 
                size="mini" 
                @click="approveHandler(scope.row)">
                通过
              </el-button>
              <el-button 
                v-if="scope.row.jinjiZhuangtai === '待审核'" 
                type="danger" 
                icon="el-icon-close" 
                size="mini" 
                @click="rejectHandler(scope.row)">
                驳回
              </el-button>
              <el-button 
                v-else
                type="info" 
                icon="el-icon-view" 
                size="mini" 
                @click="viewDetail(scope.row)">
                详情
              </el-button>
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
        xueshengXingming: '',
        xuehao: '',
        jinjiZhuangtai: ''
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      showFlag: true,
      statistics: {
        pendingCount: 0,
        approvedCount: 0,
        rejectedCount: 0,
        totalCount: 0
      }
    }
  },
  created() {
    this.getDataList()
    this.getStatistics()
  },
  activated() {
    this.getDataList()
    this.getStatistics()
  },
  methods: {
    getStatusType(status) {
      if (status === '已通过') return 'success'
      if (status === '已驳回') return 'danger'
      return 'warning'
    },
    getStatistics() {
      this.$http({
        url: 'jingsai/jinji/statistics',
        method: 'get',
        params: {}
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.statistics = data.data || {}
        }
      }).catch(err => {
        console.error('获取统计信息失败:', err)
      })
    },
    getDataList() {
      this.dataListLoading = true
      const params = {
        'page': this.pageIndex,
        'limit': this.pageSize
      }
      
      if (this.searchForm.xueshengXingming) {
        params.xueshengXingming = this.searchForm.xueshengXingming
      }
      if (this.searchForm.xuehao) {
        params.xuehao = this.searchForm.xuehao
      }
      if (this.searchForm.jinjiZhuangtai) {
        params.jinjiZhuangtai = this.searchForm.jinjiZhuangtai
      }
      
      this.$http({
        url: 'jingsai/jinji/shenhe/page',
        method: 'get',
        params: params
      }).then(({data}) => {
        console.log('=== 晋级审核API响应 ===')
        console.log('完整响应:', JSON.stringify(data, null, 2))
        
        if (data && data.code === 0) {
          if (data.page) {
            console.log('data.page:', data.page)
            console.log('data.page.list:', data.page.list)
            console.log('data.page.total:', data.page.total)
            
            this.dataList = data.page.list || []
            this.totalPage = data.page.total || 0
            
            console.log('解析后的数据:')
            console.log('- dataList:', this.dataList)
            console.log('- totalPage:', this.totalPage)
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
        console.error('晋级审核查询异常:', err)
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
    refreshData() {
      this.searchForm = {
        xueshengXingming: '',
        xuehao: '',
        jinjiZhuangtai: ''
      }
      this.pageIndex = 1
      this.getDataList()
      this.getStatistics()
      this.$message.success('数据已刷新')
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
    approveHandler(row) {
      this.$confirm(`确定通过 ${row.xueshengXingming} 的晋级申请吗？\n原竞赛：${row.yuanJingsaimingcheng}\n目标竞赛：${row.xinJingsaimingcheng}`, '审核通过确认', {
        confirmButtonText: '确定通过',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: 'jingsai/jinji/shenhe/approve',
          method: 'post',
          data: {
            'jinjiId': row.id
          }
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.$message.success('审核通过成功')
            this.getDataList()
            this.getStatistics()
          } else {
            this.$message.error(data.msg)
          }
        }).catch(err => {
          console.error('审批通过失败:', err)
          this.$message.error('操作失败，请重试')
        })
      }).catch(() => {
        // 用户取消操作
      })
    },
    rejectHandler(row) {
      this.$prompt('请输入驳回原因', `驳回 ${row.xueshengXingming} 的晋级申请`, {
        confirmButtonText: '确定驳回',
        cancelButtonText: '取消',
        inputPattern: /.+/,
        inputErrorMessage: '驳回原因不能为空'
      }).then(({ value }) => {
        this.$http({
          url: 'jingsai/jinji/shenhe/reject',
          method: 'post',
          data: {
            'jinjiId': row.id,
            'shenheYijian': value
          }
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.$message.success('已驳回晋级申请')
            this.getDataList()
            this.getStatistics()
          } else {
            this.$message.error(data.msg)
          }
        }).catch(err => {
          console.error('审批驳回失败:', err)
          this.$message.error('操作失败，请重试')
        })
      }).catch(() => {
        // 用户取消操作
      })
    },
    viewDetail(row) {
      this.$alert(`
        <div style="line-height: 1.8;">
          <p><strong>学生姓名：</strong>${row.xueshengXingming}</p>
          <p><strong>学号：</strong>${row.xuehao}</p>
          <p><strong>原竞赛：</strong>${row.yuanJingsaimingcheng}（${row.yuanJibie}）</p>
          <p><strong>目标竞赛：</strong>${row.xinJingsaimingcheng}（${row.xinJibie}）</p>
          <p><strong>晋级原因：</strong>${row.jinjiYuanyin || '-'}</p>
          <p><strong>审核状态：</strong>${row.jinjiZhuangtai}</p>
          <p><strong>审核时间：</strong>${row.shenheShijian || '-'}</p>
        </div>
      `, '晋级详情', {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '关闭'
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
