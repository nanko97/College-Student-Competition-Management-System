<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">赛道管理</h2>
      <p class="page-subtitle">Competition Track Management</p>
    </div>

    <!-- 提示信息 -->
    <div class="role-tip">
      <i class="el-icon-info"></i>
      <span>提示：管理竞赛赛道信息，包括赛道名称、参赛类型、人数限制等</span>
    </div>

    <!-- 统计信息 -->
    <div class="statistics-wrapper">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="stat-card stat-purple">
            <div class="stat-icon"><i class="el-icon-s-flag"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.totalSaidao || 0 }}</div>
              <div class="stat-label">赛道总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card stat-pink">
            <div class="stat-icon"><i class="el-icon-success"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.activeSaidao || 0 }}</div>
              <div class="stat-label">活跃赛道</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card stat-blue">
            <div class="stat-icon"><i class="el-icon-trophy"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.jingsaiCount || 0 }}</div>
              <div class="stat-label">关联竞赛</div>
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
            <el-form-item label="赛道名称">
              <el-input 
                v-model="searchForm.saidaoMingcheng" 
                placeholder="请输入赛道名称" 
                clearable
                prefix-icon="el-icon-s-flag"
              ></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" @click="search()">查询</el-button>
            </el-form-item>
          </el-row>
        </el-form>
      </div>

      <!-- 操作按钮区域 -->
      <div class="action-wrapper">
        <el-button type="success" icon="el-icon-plus" @click="addOrUpdateHandler()">新增赛道</el-button>
        <el-button
          :disabled="dataListSelections.length <= 0"
          type="danger"
          icon="el-icon-delete"
          @click="deleteHandler()"
        >批量删除</el-button>
      </div>

      <!-- 数据表格 -->
      <div class="table-wrapper">
        <el-table 
          class="tech-table"
          :data="dataList" 
          v-loading="dataListLoading" 
          @selection-change="selectionChangeHandler" 
          border 
          stripe>
          <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
          <el-table-column label="索引" header-align="center" align="center" width="60">
            <template slot-scope="scope">
              {{ (pageIndex - 1) * pageSize + scope.$index + 1 }}
            </template>
          </el-table-column>
          <el-table-column prop="jingsaimingcheng" header-align="center" align="center" label="竞赛名称" show-overflow-tooltip>
            <template slot-scope="scope">
              <span style="font-weight: 600; color: #5c7cfa;">{{ scope.row.jingsaimingcheng }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="saidaoMingcheng" header-align="center" align="center" label="赛道名称">
            <template slot-scope="scope">
              <span style="color: #ff9800; font-weight: 500;">{{ scope.row.saidaoMingcheng }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="saidaoBianma" header-align="center" align="center" label="赛道编码" width="120">
            <template slot-scope="scope">
              <el-tag size="small" type="info">{{ scope.row.saidaoBianma }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="cansaiLeixing" header-align="center" align="center" label="参赛类型" width="100"></el-table-column>
          <el-table-column prop="tuanduiRenshuMin" header-align="center" align="center" label="最少人数" width="90"></el-table-column>
          <el-table-column prop="tuanduiRenshuMax" header-align="center" align="center" label="最多人数" width="90"></el-table-column>
          <el-table-column prop="isActive" header-align="center" align="center" label="状态" width="80">
            <template slot-scope="scope">
              <el-tag :type="scope.row.isActive === '是' ? 'success' : 'info'" size="small">{{ scope.row.isActive }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="sortOrder" header-align="center" align="center" label="排序" width="80"></el-table-column>
          <el-table-column fixed="right" header-align="center" align="center" width="180" label="操作">
            <template slot-scope="scope">
              <el-button type="primary" icon="el-icon-edit" size="mini" @click="addOrUpdateHandler(scope.row.id)">编辑</el-button>
              <el-button type="danger" icon="el-icon-delete" size="mini" @click="deleteHandler(scope.row.id)">删除</el-button>
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
    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"></add-or-update>
  </div>
</template>

<script>
import AddOrUpdate from './add-or-update'
export default {
  data() {
    return {
      searchForm: {
        jingsaimingcheng: '',
        saidaoMingcheng: ''
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      showFlag: true,
      // 统计信息
      statistics: {
        totalSaidao: 0,
        activeSaidao: 0,
        jingsaiCount: 0
      }
    }
  },
  components: {
    AddOrUpdate
  },
  created() {
    this.getDataList();
    this.getStatistics();
  },
  activated() {
    this.getDataList()
  },
  methods: {
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: 'jingsai/saidao/page',
        method: 'get',
        params: {
          'page': this.pageIndex,
          'limit': this.pageSize,
          'jingsaimingcheng': this.searchForm.jingsaimingcheng,
          'saidaoMingcheng': this.searchForm.saidaoMingcheng
        }
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.dataList = data.page?.list || []
          this.totalPage = data.page?.totalCount || 0
        } else {
          this.dataList = []
          this.totalPage = 0
          this.$message.error(data.msg || '查询失败')
        }
        this.dataListLoading = false
      }).catch(err => {
        console.error('赛道查询异常:', err)
        this.dataList = []
        this.totalPage = 0
        this.dataListLoading = false
      })
    },
    search() {
      this.pageIndex = 1
      this.getDataList()
    },
    selectionChangeHandler(val) {
      this.dataListSelections = val
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
    addOrUpdateHandler(id) {
      this.addOrUpdateVisible = true
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id)
      })
    },
    deleteHandler(id) {
      var ids = id ? [id] : this.dataListSelections.map(item => item.id)
      this.$confirm(`确定对[id=${ids.join(',')}]进行[${id ? '删除' : '批量删除'}]操作?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: 'jingsai/saidao/delete',
          method: 'post',
          data: ids
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.$message.success(data.msg || '操作成功')
            this.getDataList();
            this.getStatistics();
          } else {
            this.$message.error(data.msg)
          }
        }).catch(err => {
          console.error('删除失败:', err)
          this.$message.error('操作失败：' + (err.message || '请重试'))
        })
      })
    },
    // 获取统计信息
    getStatistics() {
      this.$http({
        url: 'jingsai/saidao/statistics',
        method: 'get'
      }).then(({data}) => {
        if (data && data.code === 0) {
          const stats = data.data || {}
          // 字段名一致，直接赋值
          this.statistics = {
            totalSaidao: stats.totalSaidao || 0,
            activeSaidao: stats.activeSaidao || 0,
            jingsaiCount: stats.jingsaiCount || 0
          }
          console.log('赛道统计数据加载成功:', this.statistics)
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
