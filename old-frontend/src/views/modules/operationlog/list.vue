<template>
  <div class="main-content">
    <!-- 统计卡片 -->
    <div class="statistics-wrapper">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card stat-purple">
            <div class="stat-icon"><i class="el-icon-document"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.total || 0 }}</div>
              <div class="stat-label">日志总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card stat-blue">
            <div class="stat-icon"><i class="el-icon-success"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.successCount || 0 }}</div>
              <div class="stat-label">操作成功</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card stat-orange">
            <div class="stat-icon"><i class="el-icon-error"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.failCount || 0 }}</div>
              <div class="stat-label">操作失败</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card stat-green">
            <div class="stat-icon"><i class="el-icon-time"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.todayCount || 0 }}</div>
              <div class="stat-label">今日操作</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 搜索筛选 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="操作人">
        <el-input v-model="searchForm.operator" placeholder="操作人" clearable style="width: 150px;"></el-input>
      </el-form-item>
      <el-form-item label="操作模块">
        <el-select v-model="searchForm.operationModule" placeholder="请选择" clearable style="width: 150px;">
          <el-option label="竞赛管理" value="竞赛管理"></el-option>
          <el-option label="报名管理" value="报名管理"></el-option>
          <el-option label="作品管理" value="作品管理"></el-option>
          <el-option label="费用管理" value="费用管理"></el-option>
          <el-option label="晋级管理" value="晋级管理"></el-option>
          <el-option label="团队管理" value="团队管理"></el-option>
          <el-option label="人员变更" value="人员变更管理"></el-option>
          <el-option label="复核管理" value="复核管理"></el-option>
          <el-option label="用户管理" value="用户管理"></el-option>
          <el-option label="系统管理" value="系统管理"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="操作类型">
        <el-select v-model="searchForm.operationType" placeholder="请选择" clearable style="width: 120px;">
          <el-option label="新增" value="新增"></el-option>
          <el-option label="修改" value="修改"></el-option>
          <el-option label="删除" value="删除"></el-option>
          <el-option label="审核" value="审核"></el-option>
          <el-option label="查询" value="查询"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="操作状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px;">
          <el-option label="成功" value="成功"></el-option>
          <el-option label="失败" value="失败"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getDataList">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 日志列表 -->
    <el-table :data="dataList" border stripe v-loading="dataListLoading" style="width: 100%;">
      <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
      <el-table-column prop="operator" label="操作人" width="120" align="center"></el-table-column>
      <el-table-column prop="operatorRole" label="角色" width="80" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.operatorRole === 'admin'" type="danger" size="mini">管理员</el-tag>
          <el-tag v-else-if="scope.row.operatorRole === 'jiaoshi'" type="warning" size="mini">教师</el-tag>
          <el-tag v-else-if="scope.row.operatorRole === 'xuesheng'" size="mini">学生</el-tag>
          <el-tag v-else size="mini">{{ scope.row.operatorRole }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="operationModule" label="操作模块" width="120" align="center"></el-table-column>
      <el-table-column prop="operationType" label="操作类型" width="80" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.operationType === '删除'" type="danger" size="mini">{{ scope.row.operationType }}</el-tag>
          <el-tag v-else-if="scope.row.operationType === '审核'" type="warning" size="mini">{{ scope.row.operationType }}</el-tag>
          <el-tag v-else-if="scope.row.operationType === '新增'" type="success" size="mini">{{ scope.row.operationType }}</el-tag>
          <el-tag v-else size="mini">{{ scope.row.operationType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="operationDesc" label="操作描述" min-width="200" show-overflow-tooltip></el-table-column>
      <el-table-column prop="requestUrl" label="请求URL" width="180" show-overflow-tooltip></el-table-column>
      <el-table-column prop="clientIp" label="IP地址" width="130" align="center"></el-table-column>
      <el-table-column prop="status" label="状态" width="70" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '成功'" type="success" size="mini">成功</el-tag>
          <el-tag v-else type="danger" size="mini">失败</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="操作时间" width="170" align="center"></el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      @size-change="sizeChangeHandle"
      @current-change="currentChangeHandle"
      :current-page="pageIndex"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pageSize"
      :total="totalPage"
      layout="total, sizes, prev, pager, next, jumper"
      class="pagination">
    </el-pagination>
  </div>
</template>

<script>
export default {
  data() {
    return {
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      searchForm: {
        operator: '',
        operationModule: '',
        operationType: '',
        status: ''
      },
      statistics: {
        total: 0,
        successCount: 0,
        failCount: 0,
        todayCount: 0
      }
    }
  },
  created() {
    this.getDataList()
    this.getStatistics()
  },
  methods: {
    // 获取日志列表
    getDataList() {
      this.dataListLoading = true
      let params = {
        page: this.pageIndex,
        limit: this.pageSize
      }
      if (this.searchForm.operator) params['operator'] = this.searchForm.operator
      if (this.searchForm.operationModule) params['operationModule'] = this.searchForm.operationModule
      if (this.searchForm.operationType) params['operationType'] = this.searchForm.operationType
      if (this.searchForm.status) params['status'] = this.searchForm.status
      
      this.$http({
        url: '/operationlog/page',
        method: 'get',
        params: params
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.dataList = data.page.list
          this.totalPage = data.page.totalCount
        } else {
          this.dataList = []
          this.totalPage = 0
        }
        this.dataListLoading = false
      }).catch(() => {
        this.dataListLoading = false
      })
    },
    // 获取统计数据
    getStatistics() {
      this.$http({
        url: '/operationlog/statistics',
        method: 'get'
      }).then(({ data }) => {
        if (data && data.code === 0 && data.data) {
          this.statistics = {
            total: data.data.total || 0,
            successCount: data.data.successCount || 0,
            failCount: data.data.failCount || 0,
            todayCount: data.data.todayCount || 0
          }
        }
      }).catch(() => {})
    },
    // 重置搜索
    resetSearch() {
      this.searchForm = {
        operator: '',
        operationModule: '',
        operationType: '',
        status: ''
      }
      this.pageIndex = 1
      this.getDataList()
    },
    // 每页数变化
    sizeChangeHandle(val) {
      this.pageSize = val
      this.pageIndex = 1
      this.getDataList()
    },
    // 当前页变化
    currentChangeHandle(val) {
      this.pageIndex = val
      this.getDataList()
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/css/statistics-cards.scss';
.main-content {
  padding: 20px;
}
.search-form {
  margin-bottom: 15px;
}
.pagination {
  margin-top: 15px;
  text-align: right;
}
</style>