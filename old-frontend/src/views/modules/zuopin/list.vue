<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">{{ pageTitle }}</h2>
      <p class="page-subtitle">{{ pageSubtitle }}</p>
    </div>

    <!-- 教师提示信息 -->
    <div class="role-tip" v-if="isTeacher">
      <i class="el-icon-info"></i>
      <span>提示：此处仅显示您管理的竞赛下的学生提交的作品信息</span>
    </div>

    <!-- 管理员提示信息 -->
    <div class="role-tip" v-if="!isTeacher && !isStudent">
      <i class="el-icon-info"></i>
      <span>提示：管理所有学生提交的竞赛作品，可进行评分和审核操作</span>
    </div>

    <!-- 学生提示信息 -->
    <div class="role-tip" v-if="isStudent">
      <i class="el-icon-info"></i>
      <span>提示：此处显示您的作品提交情况</span>
    </div>

    <!-- 搜索区域 -->
    <div class="search-wrapper">
      <el-form :inline="true" :model="searchForm" class="tech-search-form">
        <el-form-item label="竞赛名称">
          <el-input v-model="searchForm.jingsaimingcheng" placeholder="请输入竞赛名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="学生姓名">
          <el-input v-model="searchForm.xueshengxingming" placeholder="请输入学生姓名" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="search()">查询</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 统计信息 -->
    <div class="statistics-wrapper">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="stat-card stat-purple">
            <div class="stat-icon"><i class="el-icon-document"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.totalBaoming || 0 }}</div>
              <div class="stat-label">已审核报名数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card stat-pink">
            <div class="stat-icon"><i class="el-icon-upload"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.submittedCount || 0 }}</div>
              <div class="stat-label">已提交作品</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card stat-blue">
            <div class="stat-icon"><i class="el-icon-warning-outline"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.unsubmittedCount || 0 }}</div>
              <div class="stat-label">未提交作品</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 数据表格 -->
    <div class="table-wrapper">
      <el-table :data="dataList" v-loading="dataListLoading" border stripe class="tech-table" style="width: 100%">
        <el-table-column label="索引" type="index" width="60" align="center" />
        <el-table-column prop="xueshengxingming" header-align="center" align="center" label="学生姓名" width="100"></el-table-column>
        <el-table-column prop="xuehao" header-align="center" align="center" label="学号" width="120"></el-table-column>
        <el-table-column prop="jingsaimingcheng" header-align="center" align="center" label="竞赛名称" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="jingsaileixing" header-align="center" align="center" label="竞赛类型" width="130"></el-table-column>
        <el-table-column prop="cansaileixing" header-align="center" align="center" label="参赛类型" width="100"></el-table-column>
        <el-table-column prop="cansaizuopin" header-align="center" align="center" label="作品状态" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.cansaizuopin" type="success" effect="dark">已提交</el-tag>
            <el-tag v-else type="warning">未提交</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="shenqingriqi" header-align="center" align="center" label="报名日期" width="110"></el-table-column>
        <el-table-column fixed="right" header-align="center" align="center" width="250" label="操作">
          <template slot-scope="scope">
            <el-button v-if="scope.row.cansaizuopin" type="info" size="mini" icon="el-icon-download" @click="downloadHandler(scope.row)">下载</el-button>
            <el-button v-if="scope.row.cansaizuopin" type="primary" size="mini" icon="el-icon-edit" @click="scoreHandler(scope.row)">评分</el-button>
            <el-button v-if="scope.row.cansaizuopin" type="success" size="mini" icon="el-icon-view" @click="viewScoreHandler(scope.row)">查看评分</el-button>
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
        class="tech-pagination">
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
  computed: {
    pageTitle() {
      return "作品打分";
    },
    pageSubtitle() {
      return "Work Scoring";
    },
    isTeacher() {
      const tableName = this.$storage.get("sessionTable");
      return tableName === "jiaoshi";
    },
    isStudent() {
      const tableName = this.$storage.get("sessionTable");
      return tableName === "xuesheng";
    }
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
        // 使用zuopin/download接口下载，确保二进制文件(docx/zip等)能正确下载而非在浏览器中打开
        const token = this.$storage.get('Token')
        const url = '/BYSJ_Springboot/zuopin/download?baomingId=' + row.id + '&Token=' + token
        window.open(url)
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

<style lang="scss" scoped>
@import '@/assets/css/tech-theme.scss';
@import '@/assets/css/global-responsive-mixin.scss';
@import '@/assets/css/statistics-cards.scss';

/* 页面头部样式 */
.page-header {
  margin-bottom: 24px;
}

/* 提示信息样式 */
.role-tip {
  margin-bottom: 20px;
}

/* 搜索区域样式 */
.search-wrapper {
  margin-bottom: 20px;
}

/* 表格区域样式 */
.table-wrapper {
  margin-top: 0;
}

/* 搜索表单样式 */
.tech-search-form {
  ::v-deep .el-form-item {
    margin-bottom: 0;
    margin-right: 20px;
  }
  ::v-deep .el-input__inner {
    width: 200px;
  }
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

/* 表格样式 */
.tech-table {
  ::v-deep .el-table__body tr:hover > td {
    background: rgba(#5c7cfa, 0.08) !important;
  }
  
  ::v-deep .el-button--mini {
    padding: 7px 12px;
    margin: 2px;
  }
}

/* 分页样式 */
.tech-pagination {
  margin-top: 20px;
}

/* 响应式设计 - 平板设备 */
@media screen and (max-width: 1200px) {
  .search-wrapper {
    margin-bottom: 15px;
  }
  
  .tech-search-form {
    ::v-deep .el-input__inner {
      width: 150px;
    }
  }
}

/* 响应式设计 - 手机设备 */
@media screen and (max-width: 768px) {
  .page-header {
    margin-bottom: 16px;
  }
  
  .role-tip {
    margin-bottom: 15px;
  }
  
  .search-wrapper {
    margin-bottom: 10px;
  }
  
  .tech-search-form {
    ::v-deep .el-form-item {
      margin-right: 10px;
    }
    
    ::v-deep .el-input__inner {
      width: 120px;
    }
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
  
  ::v-deep .el-descriptions-item__label {
    width: 80px !important;
  }
}

/* 响应式设计 - 超小屏幕设备 */
@media screen and (max-width: 480px) {
  .tech-search-form {
    ::v-deep .el-input__inner {
      width: 100px;
    }
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
  .table-wrapper {
    -webkit-overflow-scrolling: touch;
  }
}
</style>
