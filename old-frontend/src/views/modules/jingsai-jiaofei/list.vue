<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">缴费记录管理</h2>
      <p class="page-subtitle">Payment Records Management</p>
    </div>

    <!-- 提示信息 -->
    <div class="role-tip">
      <i class="el-icon-info"></i>
      <span>提示：查看和管理所有竞赛缴费记录，教师可进行缴费审核</span>
    </div>

    <!-- 统计信息 -->
    <div class="statistics-wrapper">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="stat-card stat-purple">
            <div class="stat-icon"><i class="el-icon-money"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.totalJiaofei || 0 }}</div>
              <div class="stat-label">缴费总数</div>
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
              <div class="stat-label">已审核</div>
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
                v-model="searchForm.xueshengxingming" 
                placeholder="请输入学生姓名" 
                clearable
                prefix-icon="el-icon-user"
              ></el-input>
            </el-form-item>
            <el-form-item label="缴费状态">
              <el-select v-model="searchForm.jiaofeiZhuangtai" placeholder="请选择状态" clearable>
                <el-option label="待审核 (已缴费)" value="已缴费"></el-option>
                <el-option label="已通过" value="已通过"></el-option>
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
          <el-table-column prop="jingsaimingcheng" header-align="center" align="center" label="竞赛名称" show-overflow-tooltip></el-table-column>
          <el-table-column prop="jiaofeiJine" header-align="center" align="center" label="缴费金额(元)">
            <template slot-scope="scope">
              <span style="font-weight: 700; color: #ff5252; font-size: 18px;">¥{{ scope.row.jiaofeiJine }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="jiaofeiShijian" header-align="center" align="center" label="缴费时间">
            <template slot-scope="scope">{{ scope.row.jiaofeiShijian }}</template>
          </el-table-column>
          <el-table-column prop="jiaofeiZhuangtai" header-align="center" align="center" label="缴费状态">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.jiaofeiZhuangtai)" size="small">{{ scope.row.jiaofeiZhuangtai }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="pingzhengTupian" header-align="center" align="center" label="缴费凭证">
            <template slot-scope="scope">
              <div v-if="scope.row.pingzhengTupian" class="image-preview">
                <el-image 
                  :src="$imageUrl(scope.row.pingzhengTupian)" 
                  :preview-src-list="[$imageUrl(scope.row.pingzhengTupian)]" 
                  style="width: 60px; height: 60px; border-radius: 8px;"
                  fit="cover">
                </el-image>
              </div>
              <span v-else style="color: #909399;">无</span>
            </template>
          </el-table-column>
          <el-table-column prop="shenheYijian" header-align="center" align="center" label="审核意见" show-overflow-tooltip></el-table-column>
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
      showFlag: true,
      searchForm: {
        xueshengxingming: '',
        jiaofeiZhuangtai: ''
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      // 统计信息
      statistics: {
        totalJiaofei: 0,
        passedCount: 0,
        pendingCount: 0
      }
    }
  },
  created() {
    this.getDataList();
    this.getStatistics();
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
      // 根据用户角色调用不同接口
      const tableName = sessionStorage.getItem('tableName')
      let url = ''
      let params = {
        'page': this.pageIndex,
        'limit': this.pageSize
      }
      
      if (tableName === 'xuesheng') {
        // 学生端：查询我的缴费记录
        url = 'jingsai/jiaofei/my/list'
      } else {
        // 教师/管理员端：查询所有缴费记录（审核页面）
        url = 'jingsai/jiaofei/shenhe/page'
        if (this.searchForm.xueshengxingming) {
          params['xueshengxingming'] = this.searchForm.xueshengxingming
        }
        if (this.searchForm.jiaofeiZhuangtai) {
          params['jiaofeiZhuangtai'] = this.searchForm.jiaofeiZhuangtai
        }
      }
      
      this.$http({
        url: url,
        method: 'get',
        params: params
      }).then(({data}) => {
        console.log('缴费记录API响应:', data)
        if (data && data.code === 0) {
          // 兼容两种返回格式
          if (data.page && data.page.list) {
            this.dataList = data.page.list
            this.totalPage = data.page.totalCount || data.page.total || 0
          } else if (data.data && data.data.list) {
            this.dataList = data.data.list
            this.totalPage = data.data.totalCount || data.data.total || 0
          } else {
            console.warn('未知的数据格式:', data)
            this.dataList = []
            this.totalPage = 0
          }
          console.log('缴费记录数据:', this.dataList, '总数:', this.totalPage)
        } else {
          console.error('查询失败:', data.msg)
          this.dataList = []
          this.totalPage = 0
          this.$message.error(data.msg || '查询失败')
        }
        this.dataListLoading = false
      }).catch(err => {
        console.error('缴费记录查询异常:', err)
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
      this.pageIndex = val;
      this.getDataList();
    },
    // 获取统计信息
    getStatistics() {
      this.$http({
        url: 'jingsai/jiaofei/statistics',
        method: 'get'
      }).then(({data}) => {
        if (data && data.code === 0) {
          const stats = data.data || {}
          // 将后端返回的字段名映射为前端期望的字段名
          this.statistics = {
            totalJiaofei: stats.total || 0,
            approvedCount: stats.yitongguo || 0,
            pendingCount: stats.daishenhe || 0
          }
          console.log('缴费统计数据加载成功:', this.statistics)
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
@import '@/assets/css/global-responsive-mixin.scss';
@import '@/assets/css/statistics-cards.scss';

.page-header {
  margin-bottom: 24px;
}

.role-tip {
  margin-bottom: 20px;
}

.search-wrapper {
  margin-bottom: 20px;
}

.table-wrapper {
  margin-top: 0;
}

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

.image-preview {
  display: flex;
  justify-content: center;
  align-items: center;
  
  ::v-deep .el-image {
    transition: transform 0.3s;
    
    &:hover {
      transform: scale(1.5);
    }
  }
}

.tech-table {
  ::v-deep .el-table__body tr:hover > td {
    background: rgba(#5c7cfa, 0.08) !important;
  }
}

.tech-pagination { margin-top: 20px; }

/* 响应式设计 - 平板设备 */
@media screen and (max-width: 1200px) {
  .stat-value {
    font-size: 24px;
  }
}

/* 响应式设计 - 手机设备 */
@media screen and (max-width: 768px) {
  .el-row {
    margin-left: 0 !important;
    margin-right: 0 !important;
  }
  
  .el-col {
    padding-left: 0 !important;
    padding-right: 0 !important;
  }
  
  .el-table {
    font-size: 12px;
  }
  
  .el-table .cell {
    padding-left: 5px;
    padding-right: 5px;
  }
  
  .el-button--mini {
    padding: 5px 8px;
    font-size: 11px;
  }
  
  .tech-pagination {
    text-align: center;
  }
  
  .el-pagination {
    justify-content: center;
  }
  
  ::v-deep .el-dialog {
    width: 95% !important;
    margin-top: 5vh !important;
  }
  
  ::v-deep .el-dialog__body {
    padding: 15px;
  }
}

/* 响应式设计 - 超小屏幕设备 */
@media screen and (max-width: 480px) {
  .stat-value {
    font-size: 18px;
  }
  
  .stat-label {
    font-size: 11px;
  }
  
  .el-table {
    font-size: 11px;
  }
}

/* 横向滚动优化 */
@media screen and (max-width: 768px) {
  .table-wrapper {
    -webkit-overflow-scrolling: touch;
  }
}
</style>
