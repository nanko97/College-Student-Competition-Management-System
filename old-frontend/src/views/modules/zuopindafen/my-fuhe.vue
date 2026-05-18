<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">我的复核记录</h2>
      <p class="page-subtitle">查看我的所有成绩复核申请及审核结果</p>
    </div>

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
          <el-form-item label="复核状态">
            <el-select v-model="searchForm.fuheStatus" placeholder="请选择复核状态" clearable>
              <el-option label="待审核" value="待审核"></el-option>
              <el-option label="已通过" value="已通过"></el-option>
              <el-option label="已驳回" value="已驳回"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="search()">查询</el-button>
            <el-button icon="el-icon-refresh" @click="resetSearch()">重置</el-button>
          </el-form-item>
        </el-row>
      </el-form>
    </div>

    <!-- 数据统计 -->
    <div class="statistics-wrapper">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card stat-total">
            <div class="stat-icon"><i class="el-icon-document"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ total }}</div>
              <div class="stat-label">总申请数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card stat-pending">
            <div class="stat-icon"><i class="el-icon-time"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ pendingCount }}</div>
              <div class="stat-label">待审核</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card stat-approved">
            <div class="stat-icon"><i class="el-icon-circle-check"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ approvedCount }}</div>
              <div class="stat-label">已通过</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card stat-rejected">
            <div class="stat-icon"><i class="el-icon-circle-close"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ rejectedCount }}</div>
              <div class="stat-label">已驳回</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 数据表格 -->
    <div class="table-wrapper">
      <el-table 
        class="tech-table"
        :data="dataList"
        v-loading="dataListLoading"
        border>
        
        <el-table-column label="序号" type="index" width="60" align="center"></el-table-column>
        
        <el-table-column prop="jingsaimingcheng" label="竞赛名称" align="center" min-width="200">
          <template slot-scope="scope">
            <span style="font-weight: 600; color: #5c7cfa;">{{scope.row.jingsaimingcheng}}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="yuanChengji" label="原成绩" align="center" width="100">
          <template slot-scope="scope">
            <span style="font-weight: 700; color: #ff6b6b; font-size: 16px;">{{scope.row.yuanChengji}} 分</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="xinChengji" label="新成绩" align="center" width="100">
          <template slot-scope="scope">
            <span v-if="scope.row.xinChengji && scope.row.fuheStatus === '已通过'" 
                  style="font-weight: 700; color: #51cf66; font-size: 16px;">
              {{scope.row.xinChengji}} 分
            </span>
            <span v-else style="color: #868e96;">-</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="fuheReason" label="复核理由" align="center" min-width="200" show-overflow-tooltip></el-table-column>
        
        <el-table-column prop="fuheStatus" label="复核状态" align="center" width="120">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.fuheStatus)" effect="dark">
              {{scope.row.fuheStatus}}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="shenqingShijian" label="申请时间" align="center" width="170"></el-table-column>
        
        <el-table-column prop="shenheShijian" label="审核时间" align="center" width="170">
          <template slot-scope="scope">
            <span v-if="scope.row.shenheShijian">{{scope.row.shenheShijian}}</span>
            <span v-else style="color: #868e96;">-</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="shenheGonghao" label="审核工号" align="center" width="120">
          <template slot-scope="scope">
            <span v-if="scope.row.shenheGonghao">{{scope.row.shenheGonghao}}</span>
            <span v-else style="color: #868e96;">-</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="shenheJiaoshi" label="审核教师" align="center" width="120">
          <template slot-scope="scope">
            <span v-if="scope.row.shenheJiaoshi">{{scope.row.shenheJiaoshi}}</span>
            <span v-else style="color: #868e96;">-</span>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="150" align="center">
          <template slot-scope="scope">
            <el-button 
              type="primary" 
              icon="el-icon-view" 
              size="mini" 
              @click="viewDetail(scope.row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
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
    </div>

    <!-- 详情对话框 -->
    <el-dialog
      title="复核申请详情"
      :visible.sync="detailDialogVisible"
      width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="学号">{{detailData.xuehao}}</el-descriptions-item>
        <el-descriptions-item label="学生姓名">{{detailData.xueshengxingming}}</el-descriptions-item>
        <el-descriptions-item label="竞赛名称" :span="2">{{detailData.jingsaimingcheng}}</el-descriptions-item>
        <el-descriptions-item label="原成绩">{{detailData.yuanChengji}} 分</el-descriptions-item>
        <el-descriptions-item label="新成绩">
          <span v-if="detailData.xinChengji && detailData.fuheStatus === '已通过'" style="color: #51cf66; font-weight: bold;">
            {{detailData.xinChengji}} 分
          </span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="getStatusType(detailData.fuheStatus)" effect="dark">{{detailData.fuheStatus}}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">{{detailData.shenqingShijian}}</el-descriptions-item>
        <el-descriptions-item label="复核理由" :span="2">{{detailData.fuheReason}}</el-descriptions-item>
        <el-descriptions-item label="审核工号" v-if="detailData.shenheGonghao">{{detailData.shenheGonghao}}</el-descriptions-item>
        <el-descriptions-item label="审核教师" v-if="detailData.shenheJiaoshi">{{detailData.shenheJiaoshi}}</el-descriptions-item>
        <el-descriptions-item label="审核时间" v-if="detailData.shenheShijian">{{detailData.shenheShijian}}</el-descriptions-item>
        <el-descriptions-item label="审核意见" :span="2" v-if="detailData.shenheYijian">{{detailData.shenheYijian}}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      searchForm: {
        jingsaimingcheng: '',
        fuheStatus: ''
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      total: 0,
      dataListLoading: false,
      detailDialogVisible: false,
      detailData: {},
      pendingCount: 0,
      approvedCount: 0,
      rejectedCount: 0
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
      
      // 添加当前学生学号筛选
      const xuehao = this.$storage.get('username')
      if (xuehao) {
        params.xuehao = xuehao
      }
      
      if (this.searchForm.jingsaimingcheng) {
        params.jingsaimingcheng = this.searchForm.jingsaimingcheng
      }
      if (this.searchForm.fuheStatus) {
        params.fuheStatus = this.searchForm.fuheStatus
      }
      
      this.$http({
        url: 'zuopindafenfuhe/page',
        method: 'get',
        params: params
      }).then(({ data }) => {
        if (data && data.code === 0) {
          if (data.data && data.data.list) {
            this.dataList = data.data.list
            this.totalPage = data.data.totalPage || data.data.total || 0
            this.total = data.data.total || 0
            
            // 【修复】使用后端返回的统计数据
            if (data.statistics) {
              this.total = data.statistics.total || 0
              this.pendingCount = data.statistics.pendingCount || 0
              this.approvedCount = data.statistics.approvedCount || 0
              this.rejectedCount = data.statistics.rejectedCount || 0
            }
          }
        }
        this.dataListLoading = false
      }).catch(() => {
        this.dataListLoading = false
      })
    },
    

    
    // 搜索
    search() {
      this.pageIndex = 1
      this.getDataList()
    },
    
    // 重置搜索
    resetSearch() {
      this.searchForm = {
        jingsaimingcheng: '',
        fuheStatus: ''
      }
      this.pageIndex = 1
      this.getDataList()
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
    
    // 获取状态类型
    getStatusType(status) {
      if (status === '待审核') return 'warning'
      if (status === '已通过') return 'success'
      if (status === '已驳回') return 'danger'
      return 'info'
    },
    
    // 查看详情
    viewDetail(row) {
      this.detailData = row
      this.detailDialogVisible = true
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/css/tech-theme.scss';

.search-wrapper {
  margin-bottom: 20px;
}

.statistics-wrapper {
  margin-bottom: 20px;
  
  .stat-card {
    padding: 20px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    transition: all 0.3s;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
    }
    
    .stat-icon {
      font-size: 40px;
      margin-right: 15px;
      opacity: 0.8;
    }
    
    .stat-content {
      flex: 1;
      
      .stat-value {
        font-size: 28px;
        font-weight: bold;
        line-height: 1;
        margin-bottom: 5px;
      }
      
      .stat-label {
        font-size: 14px;
        opacity: 0.9;
      }
    }
  }
  
  .stat-pending {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  }
  
  .stat-approved {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  }
  
  .stat-rejected {
    background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  }
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
