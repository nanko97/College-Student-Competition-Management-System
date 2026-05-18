<template>
  <div class="main-content">
    <el-form :inline="true" :model="searchForm" class="form-content">
      <el-row :gutter="20" class="slt">
        <el-form-item label="竞赛名称">
          <el-input v-model="searchForm.jingsaimingcheng" placeholder="竞赛名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="学生姓名">
          <el-input v-model="searchForm.xueshengxingming" placeholder="学生姓名" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="search()">查询</el-button>
        </el-form-item>
      </el-row>
    </el-form>

    <!-- 统计信息 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="8">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 16px; color: #909399">已审核报名数</div>
            <div style="font-size: 32px; font-weight: bold; color: #409EFF; margin-top: 10px">{{ statistics.totalBaoming || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 16px; color: #909399">已提交作品</div>
            <div style="font-size: 32px; font-weight: bold; color: #67C23A; margin-top: 10px">{{ statistics.submittedCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 16px; color: #909399">未提交作品</div>
            <div style="font-size: 32px; font-weight: bold; color: #F56C6C; margin-top: 10px">{{ statistics.unsubmittedCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <div class="table-content">
      <el-table :data="dataList" v-loading="dataListLoading" border stripe>
        <el-table-column label="索引" type="index" width="50" />
        <el-table-column prop="xueshengxingming" header-align="center" align="center" label="学生姓名" width="100"></el-table-column>
        <el-table-column prop="xuehao" header-align="center" align="center" label="学号" width="120"></el-table-column>
        <el-table-column prop="jingsaimingcheng" header-align="center" align="center" label="竞赛名称" show-overflow-tooltip></el-table-column>
        <el-table-column prop="jingsaileixing" header-align="center" align="center" label="竞赛类型" width="120"></el-table-column>
        <el-table-column prop="cansaileixing" header-align="center" align="center" label="参赛类型" width="100"></el-table-column>
        <el-table-column prop="cansaizuopin" header-align="center" align="center" label="作品状态" width="120">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.cansaizuopin" type="success">已提交</el-tag>
            <el-tag v-else type="warning">未提交</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="shenqingriqi" header-align="center" align="center" label="报名日期" width="110"></el-table-column>
        <el-table-column fixed="right" header-align="center" align="center" width="250" label="操作">
          <template slot-scope="scope">
            <el-button v-if="scope.row.cansaizuopin" type="text" size="small" @click="downloadHandler(scope.row)">下载作品</el-button>
            <el-button v-if="scope.row.cansaizuopin" type="text" size="small" @click="scoreHandler(scope.row)">评分</el-button>
            <el-button v-if="scope.row.cansaizuopin" type="text" size="small" @click="viewScoreHandler(scope.row)">查看评分</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        @size-change="sizeChangeHandle"
        @current-change="currentChangeHandle"
        :current-page="pageIndex"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        :total="totalPage"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination-content">
      </el-pagination>
    </div>

    <!-- 评分对话框 -->
    <el-dialog title="作品评分" :visible.sync="scoreVisible" width="600px" :close-on-click-modal="false">
      <el-form :model="scoreForm" label-width="120px">
        <el-form-item label="学生姓名">
          <el-input :value="scoreForm.xueshengxingming" disabled></el-input>
        </el-form-item>
        <el-form-item label="学号">
          <el-input :value="scoreForm.xuehao" disabled></el-input>
        </el-form-item>
        <el-form-item label="竞赛名称">
          <el-input :value="scoreForm.jingsaimingcheng" disabled></el-input>
        </el-form-item>
        <el-form-item label="作品评分" prop="zuopinpingfen">
          <el-input-number v-model="scoreForm.zuopinpingfen" :min="0" :max="100" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="评价内容" prop="pingjianeirong">
          <el-input v-model="scoreForm.pingjianeirong" type="textarea" :rows="4" placeholder="请输入评价内容"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="scoreVisible = false">取消</el-button>
        <el-button type="primary" @click="submitScore()" :loading="submitting">确定评分</el-button>
      </span>
    </el-dialog>

    <!-- 查看评分对话框 -->
    <el-dialog title="评分详情" :visible.sync="viewScoreVisible" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="学生姓名">{{ viewScoreInfo.xueshengxingming }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ viewScoreInfo.xuehao }}</el-descriptions-item>
        <el-descriptions-item label="竞赛名称">{{ viewScoreInfo.jingsaimingcheng }}</el-descriptions-item>
        <el-descriptions-item label="作品评分">
          <el-tag type="success" size="large">{{ viewScoreInfo.zuopinpingfen }} 分</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="评价内容">{{ viewScoreInfo.pingjianeirong || '无' }}</el-descriptions-item>
        <el-descriptions-item label="评分教师">{{ viewScoreInfo.jiaoshixingming || '-' }}</el-descriptions-item>
        <el-descriptions-item label="评分时间">{{ viewScoreInfo.pingjiashijian || '-' }}</el-descriptions-item>
      </el-descriptions>
      <span slot="footer" class="dialog-footer">
        <el-button @click="viewScoreVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      searchForm: {
        jingsaimingcheng: '',
        xueshengxingming: ''
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      statistics: {
        totalBaoming: 0,
        submittedCount: 0,
        unsubmittedCount: 0
      },
      scoreVisible: false,
      submitting: false,
      scoreForm: {
        id: '',
        xueshengxingming: '',
        xuehao: '',
        jingsaimingcheng: '',
        jingsaileixing: '',
        zuopinpingfen: 0,
        pingjianeirong: ''
      },
      viewScoreVisible: false,
      viewScoreInfo: {}
    }
  },
  mounted() {
    this.getDataList()
    this.getStatistics()
  },
  activated() {
    this.getDataList()
    this.getStatistics()
  },
  methods: {
    getStatistics() {
      this.$http({
        url: 'zuopin/statistics',
        method: 'get'
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.statistics = data.data || {}
        }
      })
    },
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: 'zuopin/list',
        method: 'get',
        params: {
          'page': this.pageIndex,
          'limit': this.pageSize,
          'jingsaimingcheng': this.searchForm.jingsaimingcheng,
          'xueshengxingming': this.searchForm.xueshengxingming
        }
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.dataList = data.page ? data.page.list : []
          this.totalPage = data.page ? data.page.total : 0
        } else {
          this.dataList = []
          this.totalPage = 0
        }
        this.dataListLoading = false
      }).catch(() => {
        this.dataListLoading = false
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
    downloadHandler(row) {
      if (row.cansaizuopin) {
        window.open(this.$imageUrl(row.cansaizuopin))
      }
    },
    scoreHandler(row) {
      // 先查询是否已有评分
      this.$http({
        url: 'zuopindafen/page',
        method: 'get',
        params: {
          'xuehao': row.xuehao,
          'jingsaimingcheng': row.jingsaimingcheng,
          'page': 1,
          'limit': 1
        }
      }).then(({data}) => {
        if (data && data.code === 0 && data.page && data.page.list && data.page.list.length > 0) {
          // 已有评分，询问是否修改
          const existScore = data.page.list[0]
          this.$confirm(`该学生已有评分 ${existScore.zuopinpingfen} 分，是否重新评分？`, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.openScoreDialog(row, existScore.id)
          })
        } else {
          // 没有评分，直接打开评分对话框
          this.openScoreDialog(row, '')
        }
      })
    },
    openScoreDialog(row, existId) {
      this.scoreForm = {
        id: existId,
        xueshengxingming: row.xueshengxingming,
        xuehao: row.xuehao,
        jingsaimingcheng: row.jingsaimingcheng,
        jingsaileixing: row.jingsaileixing,
        zuopinpingfen: 0,
        pingjianeirong: ''
      }
      this.scoreVisible = true
    },
    submitScore() {
      if (!this.scoreForm.zuopinpingfen) {
        this.$message.error('请输入作品评分')
        return
      }

      this.submitting = true
      const url = this.scoreForm.id ? 'zuopindafen/update' : 'zuopindafen/save'
      
      this.$http({
        url: url,
        method: 'post',
        data: {
          id: this.scoreForm.id || undefined,
          xueshengxingming: this.scoreForm.xueshengxingming,
          xuehao: this.scoreForm.xuehao,
          jingsaimingcheng: this.scoreForm.jingsaimingcheng,
          jingsaileixing: this.scoreForm.jingsaileixing,
          zuopinpingfen: this.scoreForm.zuopinpingfen,
          pingjianeirong: this.scoreForm.pingjianeirong
        }
      }).then(({data}) => {
        this.submitting = false
        if (data && data.code === 0) {
          this.$message({
            message: '评分成功',
            type: 'success',
            duration: 1500,
            onClose: () => {
              this.scoreVisible = false
              this.getDataList()
            }
          })
        } else {
          this.$message.error(data.msg || '评分失败')
        }
      }).catch(() => {
        this.submitting = false
        this.$message.error('评分失败')
      })
    },
    viewScoreHandler(row) {
      this.$http({
        url: 'zuopindafen/page',
        method: 'get',
        params: {
          'xuehao': row.xuehao,
          'jingsaimingcheng': row.jingsaimingcheng,
          'page': 1,
          'limit': 1
        }
      }).then(({data}) => {
        if (data && data.code === 0 && data.page && data.page.list && data.page.list.length > 0) {
          this.viewScoreInfo = data.page.list[0]
          this.viewScoreVisible = true
        } else {
          this.$message.info('该学生暂无评分')
        }
      })
    }
  }
}
</script>

<style scoped>
.main-content {
  width: 100%;
  min-height: calc(100vh - 60px);
  padding: 20px;
  box-sizing: border-box;
}

.form-content {
  background: transparent;
  padding: 10px;
}

.table-content {
  background: transparent;
  padding: 10px;
  overflow-x: auto;
}

.pagination-content {
  margin-top: 20px;
  text-align: right;
}

/* 统计卡片样式 */
.el-card {
  transition: all 0.3s;
  height: 100%;
}

.el-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

/* 响应式设计 - 平板设备 */
@media screen and (max-width: 1200px) {
  .main-content {
    padding: 15px;
  }
  
  .el-card {
    margin-bottom: 15px;
  }
}

/* 响应式设计 - 手机设备 */
@media screen and (max-width: 768px) {
  .main-content {
    padding: 10px;
  }
  
  .form-content {
    padding: 5px;
  }
  
  .table-content {
    padding: 10px;
  }
  
  /* 统计卡片在手机上的布局 */
  .el-row {
    margin-left: 0 !important;
    margin-right: 0 !important;
  }
  
  .el-col {
    padding-left: 0 !important;
    padding-right: 0 !important;
  }
  
  .el-card {
    margin-bottom: 10px;
  }
  
  /* 表格在小屏幕上的优化 */
  .el-table {
    font-size: 12px;
  }
  
  .el-table .cell {
    padding-left: 5px;
    padding-right: 5px;
  }
  
  /* 操作按钮在小屏幕上的优化 */
  .el-button--text {
    font-size: 11px;
    padding: 2px 4px;
  }
  
  /* 分页器在小屏幕上的优化 */
  .pagination-content {
    text-align: center;
  }
  
  .el-pagination {
    justify-content: center;
  }
  
  /* 对话框在小屏幕上的优化 */
  ::v-deep .el-dialog {
    width: 95% !important;
    margin-top: 5vh !important;
  }
  
  ::v-deep .el-dialog__body {
    padding: 15px;
  }
  
  ::v-deep .el-descriptions-item__label {
    width: 80px !important;
  }
}

/* 响应式设计 - 超小屏幕设备 */
@media screen and (max-width: 480px) {
  .main-content {
    padding: 5px;
  }
  
  .el-table {
    font-size: 11px;
  }
  
  /* 隐藏部分列以节省空间 */
  .el-table th.el-table__cell:nth-child(2),
  .el-table td.el-table__cell:nth-child(2),
  .el-table th.el-table__cell:nth-child(3),
  .el-table td.el-table__cell:nth-child(3) {
    display: none;
  }
}

/* 横向滚动优化 */
@media screen and (max-width: 768px) {
  .table-content {
    -webkit-overflow-scrolling: touch;
  }
}
</style>
