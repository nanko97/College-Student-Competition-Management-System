<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">人员变更审核</h2>
      <p class="page-subtitle">Personnel Change Review</p>
    </div>

    <!-- 提示信息 -->
    <div class="role-tip">
      <i class="el-icon-info"></i>
      <span>提示：审核团队成员变更申请，包括添加成员、移除成员、更换负责人等操作</span>
    </div>

    <!-- 统计信息 -->
    <div class="statistics-wrapper">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="stat-card stat-purple">
            <div class="stat-icon"><i class="el-icon-document"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.totalBiangueng || 0 }}</div>
              <div class="stat-label">变更申请总数</div>
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
              <div class="stat-value">{{ statistics.todayProcessed || 0 }}</div>
              <div class="stat-label">今日已审核</div>
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
            <el-form-item label="团队编号">
              <el-input 
                v-model="searchForm.tuanduiBianhao" 
                placeholder="请输入团队编号" 
                clearable
                prefix-icon="el-icon-s-order"
              ></el-input>
            </el-form-item>
            <el-form-item label="变更类型">
              <el-select v-model="searchForm.bianguengLeixing" placeholder="请选择" clearable style="width: 150px;">
                <el-option label="添加成员" value="添加成员"></el-option>
                <el-option label="移除成员" value="移除成员"></el-option>
                <el-option label="更换负责人" value="更换负责人"></el-option>
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
          <el-table-column prop="tuanduiBianhao" header-align="center" align="center" label="团队编号" width="130">
            <template slot-scope="scope">
              <el-tag size="small" type="info">{{ scope.row.tuanduiBianhao }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="bianguengLeixing" header-align="center" align="center" label="变更类型" width="120">
            <template slot-scope="scope">
              <el-tag :type="getLeixingType(scope.row.bianguengLeixing)" size="small">{{ scope.row.bianguengLeixing }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="caozuoRenXingming" header-align="center" align="center" label="申请人" width="100"></el-table-column>
          <el-table-column prop="caozuoYuanyin" header-align="center" align="center" label="变更原因" show-overflow-tooltip></el-table-column>
          <el-table-column prop="addtime" header-align="center" align="center" label="申请时间" width="160"></el-table-column>
          <el-table-column fixed="right" header-align="center" align="center" width="220" label="操作">
            <template slot-scope="scope">
              <el-button type="primary" icon="el-icon-view" size="mini" @click="viewDetail(scope.row)">详情</el-button>
              <el-button type="success" icon="el-icon-check" size="mini" @click="shenheHandler(scope.row, '已通过')">通过</el-button>
              <el-button type="danger" icon="el-icon-close" size="mini" @click="shenheHandler(scope.row, '已驳回')">驳回</el-button>
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
    <shenhe-dialog v-if="shenheVisible" ref="shenheDialog" @refreshDataList="getDataList" @close="shenheVisible = false"></shenhe-dialog>
    <detail-dialog v-if="detailVisible" ref="detailDialog" @close="detailVisible = false"></detail-dialog>
  </div>
</template>

<script>
import ShenheDialog from './shenhe-dialog'
import DetailDialog from './detail-dialog'
export default {
  data() {
    return {
      searchForm: {
        tuanduiBianhao: '',
        bianguengLeixing: ''
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      showFlag: true,
      shenheVisible: false,
      detailVisible: false,
      // 统计信息
      statistics: {
        totalBiangueng: 0,
        pendingCount: 0,
        todayProcessed: 0
      }
    }
  },
  components: {
    ShenheDialog,
    DetailDialog
  },
  created() {
    this.getDataList();
    this.getStatistics();
  },
  activated() {
    this.getDataList()
  },
  methods: {
    getLeixingType(leixing) {
      if (leixing === '添加成员') return 'success'
      if (leixing === '移除成员') return 'warning'
      return 'danger'
    },
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: 'jingsai/biangueng/shenhe/page',
        method: 'get',
        params: {
          'page': this.pageIndex,
          'limit': this.pageSize,
          'tuanduiBianhao': this.searchForm.tuanduiBianhao,
          'bianguengLeixing': this.searchForm.bianguengLeixing
        }
      }).then(({data}) => {
        console.log('人员变更API响应:', data)
        if (data && data.code === 0) {
          this.dataList = data.page?.list || []
          this.totalPage = data.page?.total || 0
          console.log('人员变更数据:', this.dataList, '总数:', this.totalPage)
        } else {
          this.dataList = []
          this.totalPage = 0
          this.$message.error(data.msg || '查询失败')
        }
        this.dataListLoading = false
      }).catch(err => {
        console.error('审核查询异常:', err)
        this.dataList = []
        this.totalPage = 0
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
    shenheHandler(row, result) {
      this.shenheVisible = true
      this.$nextTick(() => {
        this.$refs.shenheDialog.init(row, result)
      })
    },
    viewDetail(row) {
      this.detailVisible = true
      this.$nextTick(() => {
        this.$refs.detailDialog.init(row)
      })
    },
    // 获取统计信息
    getStatistics() {
      this.$http({
        url: 'jingsai/biangueng/statistics',
        method: 'get'
      }).then(({data}) => {
        if (data && data.code === 0) {
          const stats = data.data || {}
          // 字段名一致，直接赋值
          this.statistics = {
            totalBiangueng: stats.totalBiangueng || 0,
            pendingCount: stats.pendingCount || 0,
            todayProcessed: stats.todayProcessed || 0
          }
          console.log('人员变更统计数据加载成功:', this.statistics)
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

/* 横向滚动优化 */
@media screen and (max-width: 768px) {
  .table-wrapper {
    -webkit-overflow-scrolling: touch;
  }
}
</style>
