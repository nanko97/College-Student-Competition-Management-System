<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">费用配置管理</h2>
      <p class="page-subtitle">Competition Fee Configuration</p>
    </div>

    <!-- 提示信息 -->
    <div class="role-tip">
      <i class="el-icon-info"></i>
      <span>提示：管理所有竞赛的费用配置信息，包括报名费、缴费截止日期等</span>
    </div>

    <!-- 统计信息 -->
    <div class="statistics-wrapper">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="stat-card stat-purple">
            <div class="stat-icon"><i class="el-icon-coin"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.totalFeiyong || 0 }}</div>
              <div class="stat-label">费用配置总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card stat-pink">
            <div class="stat-icon"><i class="el-icon-wallet"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.shoufeiCount || 0 }}</div>
              <div class="stat-label">收费竞赛数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card stat-blue">
            <div class="stat-icon"><i class="el-icon-circle-check"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.mianfeiCount || 0 }}</div>
              <div class="stat-label">免费竞赛数</div>
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
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" @click="search()">查询</el-button>
            </el-form-item>
          </el-row>
        </el-form>
      </div>

      <!-- 操作按钮区域 -->
      <div class="action-wrapper">
        <el-button
          type="success"
          icon="el-icon-plus"
          @click="addOrUpdateHandler()"
        >新增费用配置</el-button>
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
          <el-table-column prop="jingsaimingcheng" header-align="center" align="center" label="竞赛名称">
            <template slot-scope="scope">
              <span style="font-weight: 600; color: #5c7cfa;">{{ scope.row.jingsaimingcheng }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="baomingfei" header-align="center" align="center" label="报名费(元)">
            <template slot-scope="scope">
              <span style="font-weight: 700; color: #ff5252; font-size: 18px;">¥{{ scope.row.baomingfei }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="shifouShoufei" header-align="center" align="center" label="是否收费">
            <template slot-scope="scope">
              <el-tag :type="scope.row.shifouShoufei === '是' ? 'success' : 'info'" size="small">
                {{ scope.row.shifouShoufei }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="jiaofeiJiezhiRiqi" header-align="center" align="center" label="缴费截止日期">
            <template slot-scope="scope">{{ scope.row.jiaofeiJiezhiRiqi }}</template>
          </el-table-column>
          <el-table-column prop="beizhu" header-align="center" align="center" label="备注" show-overflow-tooltip></el-table-column>
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
    
    <!-- 添加/修改页面 -->
    <add-or-update v-if="addOrUpdateFlag" :parent="this" ref="addOrUpdate"></add-or-update>
  </div>
</template>

<script>
import AddOrUpdate from "./add-or-update";
export default {
  data() {
    return {
      searchForm: {},
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      showFlag: true,
      addOrUpdateFlag: false,
      // 统计信息
      statistics: {
        totalFeiyong: 0,
        shoufeiCount: 0,
        mianfeiCount: 0
      }
    };
  },
  components: { AddOrUpdate },
  created() {
    this.getDataList();
    this.getStatistics();
  },
  methods: {
    search() {
      this.pageIndex = 1;
      this.getDataList();
    },
    getDataList() {
      this.dataListLoading = true;
      let params = { page: this.pageIndex, limit: this.pageSize }
      if(this.searchForm.jingsaimingcheng) params['jingsaimingcheng'] = '%' + this.searchForm.jingsaimingcheng + '%'
      this.$http({ url: "jingsaiFeiyong/page", method: "get", params }).then(({ data }) => {
        console.log('费用配置API响应:', data)
        if (data && data.code === 0) {
          // 兼容多种数据返回格式
          if (data.page && data.page.list) {
            this.dataList = data.page.list
            this.totalPage = data.page.totalCount || data.page.total || 0
          } else if (data.data && data.data.list) {
            this.dataList = data.data.list
            this.totalPage = data.data.total || 0
          } else if (Array.isArray(data.data)) {
            this.dataList = data.data
            this.totalPage = data.data.length || 0
          } else {
            this.dataList = []
            this.totalPage = 0
          }
          console.log('费用配置数据:', this.dataList, '总数:', this.totalPage)
        } else {
          console.error('查询失败:', data.msg)
          this.dataList = []
          this.totalPage = 0
          this.$message.error(data.msg || '查询失败')
        }
        this.dataListLoading = false;
      }).catch(err => {
        console.error('费用配置查询异常:', err)
        this.dataList = []
        this.totalPage = 0
        this.dataListLoading = false
        this.$message.error('查询失败：' + (err.message || '请检查后端服务'))
      });
    },
    sizeChangeHandle(val) {
      this.pageSize = val;
      this.pageIndex = 1;
      this.getDataList();
    },
    currentChangeHandle(val) {
      this.pageIndex = val;
      this.getDataList();
    },
    selectionChangeHandler(val) {
      this.dataListSelections = val;
    },
    addOrUpdateHandler(id) {
      this.showFlag = false;
      this.addOrUpdateFlag = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id);
      });
    },
    deleteHandler(id) {
      var ids = id ? [id] : this.dataListSelections.map(item => item.id);
      this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$http({ url: "jingsaiFeiyong/delete", method: "post", data: ids }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message({ message: "操作成功", type: "success", duration: 1500, onClose: () => { 
              this.search();
              this.getStatistics();
            }});
          } else {
            this.$message.error(data.msg);
          }
        });
      });
    },
    // 获取统计信息
    getStatistics() {
      this.$http({
        url: 'jingsaiFeiyong/statistics',
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
};
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
}

.tech-pagination { margin-top: 20px; }

/* 横向滚动优化 */
@media screen and (max-width: 768px) {
  .table-wrapper {
    -webkit-overflow-scrolling: touch;
  }
}
</style>
