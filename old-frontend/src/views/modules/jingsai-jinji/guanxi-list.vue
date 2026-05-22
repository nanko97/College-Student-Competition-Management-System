<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">晋级关系管理</h2>
      <p class="page-subtitle">Competition Promotion Relationship</p>
    </div>

    <!-- 统计卡片 -->
    <div class="statistics-wrapper">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card stat-purple">
            <div class="stat-icon"><i class="el-icon-s-order"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.totalGuanxi || 0 }}</div>
              <div class="stat-label">总晋级关系</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card stat-green">
            <div class="stat-icon"><i class="el-icon-check"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.activeGuanxi || 0 }}</div>
              <div class="stat-label">活跃关系</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card stat-blue">
            <div class="stat-icon"><i class="el-icon-document"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.totalJilu || 0 }}</div>
              <div class="stat-label">晋级记录</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card stat-pink">
            <div class="stat-icon"><i class="el-icon-time"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.pendingCount || 0 }}</div>
              <div class="stat-label">待审核</div>
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
            <el-form-item label="父竞赛">
              <el-select v-model="searchForm.fuJingsaiId" placeholder="请选择父竞赛" clearable filterable style="width: 250px;">
                <el-option v-for="item in jingsaiList" :key="item.id" :label="item.jingsaimingcheng" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" @click="search()">查询</el-button>
            </el-form-item>
          </el-row>
        </el-form>
      </div>

      <!-- 操作按钮区域 -->
      <div class="action-wrapper">
        <el-button type="success" icon="el-icon-plus" @click="addOrUpdateHandler()">新增晋级关系</el-button>
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
          <el-table-column prop="fuJingsaimingcheng" header-align="center" align="center" label="父竞赛">
            <template slot-scope="scope">
              <span style="font-weight: 600; color: #1971c2;">{{ scope.row.fuJingsaimingcheng }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="fuJibie" header-align="center" align="center" label="父级别">
            <template slot-scope="scope">
              <el-tag type="info" size="small">{{ scope.row.fuJibie }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="ziJingsaimingcheng" header-align="center" align="center" label="子竞赛">
            <template slot-scope="scope">
              <span style="font-weight: 600; color: #1971c2;">{{ scope.row.ziJingsaimingcheng }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="ziJibie" header-align="center" align="center" label="子级别">
            <template slot-scope="scope">
              <el-tag type="success" size="small">{{ scope.row.ziJibie }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="zuidiFenshu" header-align="center" align="center" label="最低分数要求">
            <template slot-scope="scope">
              <span v-if="scope.row.zuidiFenshu" style="color: #e67700; font-weight: 600;">{{ scope.row.zuidiFenshu }}分</span>
              <span v-else style="color: #909399;">无</span>
            </template>
          </el-table-column>
          <el-table-column prop="zuidiMingci" header-align="center" align="center" label="最高名次要求">
            <template slot-scope="scope">
              <span v-if="scope.row.zuidiMingci" style="color: #e67700; font-weight: 600;">第{{ scope.row.zuidiMingci }}名</span>
              <span v-else style="color: #909399;">无</span>
            </template>
          </el-table-column>
          <el-table-column prop="isActive" header-align="center" align="center" label="状态">
            <template slot-scope="scope">
              <el-tag :type="scope.row.isActive === '是' ? 'success' : 'info'" size="small">{{ scope.row.isActive }}</el-tag>
            </template>
          </el-table-column>
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
import AddOrUpdate from './guanxi-add-or-update'
export default {
  data() {
    return {
      searchForm: {
        fuJingsaiId: null
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      jingsaiList: [],
      showFlag: true,
      statistics: {
        totalGuanxi: 0,
        activeGuanxi: 0,
        totalJilu: 0,
        pendingCount: 0,
        approvedCount: 0
      }
    }
  },
  components: {
    AddOrUpdate
  },
  created() {
    this.getDataList()
    this.getJingsaiList()
    this.getStatistics()
  },
  activated() {
    this.getDataList()
    this.getJingsaiList()
  },
  methods: {
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
    getJingsaiList() {
      this.$http({
        url: 'jingsaixinxi/list',
        method: 'get',
        params: {
          page: 1,
          limit: 1000
        }
      }).then(({data}) => {
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
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: 'jingsai/jinji/guanxi/list',
        method: 'get',
        params: {
          'page': this.pageIndex,
          'limit': this.pageSize,
          'fuJingsaiId': this.searchForm.fuJingsaiId
        }
      }).then(({data}) => {
        console.log('晋级关系API响应:', data)
        if (data && data.code === 0) {
          if (data.page && data.page.list) {
            this.dataList = data.page.list
            this.totalPage = data.page.totalCount || data.page.total || 0
          } else {
            this.dataList = []
            this.totalPage = 0
          }
          console.log('晋级关系数据:', this.dataList, '总数:', this.totalPage)
        } else {
          console.error('查询失败:', data.msg)
          this.dataList = []
          this.totalPage = 0
          this.$message.error(data.msg || '查询失败')
        }
        this.dataListLoading = false
      }).catch(err => {
        console.error('晋级关系查询异常:', err)
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
          url: 'jingsai/jinji/guanxi/delete',
          method: 'post',
          data: ids
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.$message({
              message: data.msg || '操作成功',
              type: 'success',
              duration: 2000,
              onClose: () => {
                this.getDataList()
                this.getStatistics()
              }
            })
          } else {
            this.$message.error(data.msg)
          }
        }).catch(err => {
          console.error('删除操作失败:', err)
          this.$message.error('操作失败：' + (err.message || '请重试'))
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/css/tech-theme.scss';
@import '@/assets/css/global-responsive-mixin.scss';
@import '@/assets/css/statistics-cards.scss';

.action-wrapper {
  margin-bottom: 20px;
  .el-button { margin-right: 10px; }
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

</style>
