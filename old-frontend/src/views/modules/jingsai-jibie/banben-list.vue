<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">级别版本管理</h2>
      <p class="page-subtitle">Competition Level Version Management</p>
    </div>

    <!-- 提示信息 -->
    <div class="role-tip">
      <i class="el-icon-info"></i>
      <span>提示：管理竞赛级别版本信息，包括国家级、省级、校级等不同级别的认定标准</span>
    </div>

    <!-- 统计信息 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <i class="el-icon-document"></i>
            </div>
            <div class="stat-info">
              <div class="stat-label">级别版本总数</div>
              <div class="stat-value">{{ statistics.totalBanben || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <i class="el-icon-star-on"></i>
            </div>
            <div class="stat-info">
              <div class="stat-label">国家级</div>
              <div class="stat-value">{{ statistics.guojiajiCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
              <i class="el-icon-success"></i>
            </div>
            <div class="stat-info">
              <div class="stat-label">当前版本</div>
              <div class="stat-value">{{ statistics.currentBanben || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

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
            <el-form-item label="级别">
              <el-select v-model="searchForm.jibie" placeholder="请选择级别" clearable style="width: 150px;">
                <el-option label="校级" value="校级"></el-option>
                <el-option label="省级" value="省级"></el-option>
                <el-option label="国家级" value="国家级"></el-option>
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
        <el-button type="success" icon="el-icon-plus" @click="addOrUpdateHandler()">新增级别版本</el-button>
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
          <el-table-column label="索引" type="index" width="50" />
          <el-table-column prop="jingsaimingcheng" header-align="center" align="left" min-width="200" label="竞赛名称" show-overflow-tooltip>
            <template slot-scope="scope">
              <span style="font-weight: 600; color: #5c7cfa;">{{ scope.row.jingsaimingcheng }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="jingsaiNianfen" header-align="center" align="center" label="年份" width="80">
            <template slot-scope="scope">
              <span style="color: #ff9800; font-weight: 600;">{{ scope.row.jingsaiNianfen || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="jibie" header-align="center" align="center" label="级别" width="100">
            <template slot-scope="scope">
              <el-tag :type="getJibieType(scope.row.jibie)" size="small">{{ scope.row.jibie || '-' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="renzhengJigou" header-align="center" align="left" min-width="150" label="认定机构" show-overflow-tooltip>
            <template slot-scope="scope">
              {{ scope.row.renzhengJigou || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="wenjianHao" header-align="center" align="left" min-width="150" label="文件号" show-overflow-tooltip>
            <template slot-scope="scope">
              {{ scope.row.wenjianHao || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="shengxiaoRiqi" header-align="center" align="center" label="生效日期" width="110">
            <template slot-scope="scope">
              {{ scope.row.shengxiaoRiqi || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="shixiaoRiqi" header-align="center" align="center" label="失效日期" width="110">
            <template slot-scope="scope">
              {{ scope.row.shixiaoRiqi || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="isCurrent" header-align="center" align="center" label="当前版本" width="100">
            <template slot-scope="scope">
              <el-tag :type="scope.row.isCurrent === '是' ? 'success' : 'info'" size="small">{{ scope.row.isCurrent || '-' }}</el-tag>
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
import AddOrUpdate from './banben-add-or-update'
export default {
  data() {
    return {
      searchForm: {
        jingsaimingcheng: '',
        jibie: ''
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
        totalBanben: 0,
        guojiajiCount: 0,
        currentBanben: 0
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
    getJibieType(jibie) {
      if (jibie === '国家级') return 'danger'
      if (jibie === '省级') return 'warning'
      return 'success'
    },
    getDataList() {
      this.dataListLoading = true
      console.log('级别版本筛选参数:', this.searchForm)
      this.$http({
        url: 'jingsai/jibiebanben/page',
        method: 'get',
        params: {
          'page': this.pageIndex,
          'limit': this.pageSize,
          'jingsaimingcheng': this.searchForm.jingsaimingcheng,
          'jibie': this.searchForm.jibie
        }
      }).then(({data}) => {
        console.log('级别版本API响应:', data)
        console.log('响应data.page:', data.page)
        if (data && data.code === 0) {
          if (data.page && data.page.list) {
            this.dataList = data.page.list
            this.totalPage = data.page.totalCount || data.page.total || 0
            console.log('✅ 查询成功，显示', this.dataList.length, '条数据')
          } else {
            this.dataList = []
            this.totalPage = 0
            console.warn('️ 返回数据为空')
          }
          console.log('级别版本数据:', this.dataList, '总数:', this.totalPage)
        } else {
          console.error('查询失败:', data.msg)
          this.dataList = []
          this.totalPage = 0
          this.$message.error(data.msg || '查询失败')
        }
        this.dataListLoading = false
      }).catch(err => {
        console.error('级别版本查询异常:', err)
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
          url: 'jingsai/jibiebanben/delete',
          method: 'post',
          data: ids
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.$message({
              message: '操作成功',
              type: 'success',
              duration: 1500,
              onClose: () => {
                this.getDataList();
                this.getStatistics();
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
    },
    // 获取统计信息
    getStatistics() {
      this.$http({
        url: 'jingsai/jibiebanben/statistics',
        method: 'get'
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.statistics = data.data || {}
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

/* 统计卡片样式优化 */
.stats-row {
  margin-bottom: 20px;
  
  .stat-card {
    margin-bottom: 0;
  }
}

.page-header {
  margin-bottom: 24px;
}

.role-tip {
  margin-bottom: 20px;
}

.search-wrapper {
  margin-bottom: 20px;
}

.action-wrapper {
  margin-bottom: 20px;
  .el-button { margin-right: 10px; }
}

.table-wrapper {
  margin-top: 0;
}

.stat-card {
  transition: all 0.3s;
  height: 100%;
  
  &:hover {
    transform: translateY(-5px);
  }
  
  .stat-content {
    display: flex;
    align-items: center;
    padding: 10px;
  }
  
  .stat-icon {
    width: 60px;
    height: 60px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 15px;
    flex-shrink: 0;
    
    i {
      font-size: 28px;
      color: #fff;
    }
  }
  
  .stat-info {
    flex: 1;
    
    .stat-label {
      font-size: 14px;
      color: #909399;
      margin-bottom: 8px;
    }
    
    .stat-value {
      font-size: 28px;
      font-weight: bold;
      color: #303133;
    }
  }
}

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

/* 响应式设计 - 平板设备 */
@media screen and (max-width: 1200px) {
  .stats-row {
    margin-bottom: 15px;
  }
  
  .stat-card {
    margin-bottom: 15px;
  }
  
  .stat-icon {
    width: 50px;
    height: 50px;
    
    i {
      font-size: 24px;
    }
  }
  
  .stat-value {
    font-size: 24px;
  }
}

/* 响应式设计 - 手机设备 */
@media screen and (max-width: 768px) {
  .stats-row {
    margin-bottom: 10px;
  }
  
  .el-row {
    margin-left: 0 !important;
    margin-right: 0 !important;
  }
  
  .el-col {
    padding-left: 0 !important;
    padding-right: 0 !important;
  }
  
  .stat-card {
    margin-bottom: 10px;
  }
  
  .stat-content {
    flex-direction: column;
    text-align: center;
    padding: 15px 10px;
  }
  
  .stat-icon {
    margin-right: 0;
    margin-bottom: 10px;
    width: 50px;
    height: 50px;
  }
  
  .stat-label {
    font-size: 12px;
  }
  
  .stat-value {
    font-size: 20px;
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
