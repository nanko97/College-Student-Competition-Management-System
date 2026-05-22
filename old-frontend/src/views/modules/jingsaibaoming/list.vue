<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">{{ pageTitle }}</h2>
      <p class="page-subtitle">{{ pageSubtitle }}</p>
    </div>
    
    <!-- 角色提示（仅学生可见） -->
    <div class="role-tip" v-if="isStudent">
      <i class="el-icon-info"></i>
      <span>提示：此处仅显示您的个人报名记录</span>
    </div>

    <!-- 教师提示信息 -->
    <div class="role-tip" v-if="isTeacher">
      <i class="el-icon-info"></i>
      <span>提示：此处仅显示您组织的竞赛的报名记录</span>
    </div>

    <!-- 管理员提示信息 -->
    <div class="role-tip" v-if="!isStudent && !isTeacher">
      <i class="el-icon-info"></i>
      <span>提示：管理所有学生的竞赛报名信息，可进行审核和操作</span>
    </div>

    <!-- 统计信息 -->
    <div class="statistics-wrapper">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="stat-card stat-purple">
            <div class="stat-icon"><i class="el-icon-document"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.totalBaoming || 0 }}</div>
              <div class="stat-label">报名总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card stat-blue">
            <div class="stat-icon"><i class="el-icon-success"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.passedCount || 0 }}</div>
              <div class="stat-label">已通过</div>
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
            <el-form-item label="竞赛类型">
              <el-input
                v-model="searchForm.jingsaileixing"
                placeholder="请输入竞赛类型"
                clearable
                prefix-icon="el-icon-s-order"
              ></el-input>
            </el-form-item>
            <el-form-item label="审核状态">
              <el-select v-model="searchForm.sfsh" placeholder="请选择" clearable style="width: 150px;">
                <el-option label="待审核" value="待审核"></el-option>
                <el-option label="已通过" value="是"></el-option>
                <el-option label="已驳回" value="否"></el-option>
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
        <el-button
          v-if="isAuth('jingsaibaoming','新增')"
          type="success"
          icon="el-icon-plus"
          @click="addOrUpdateHandler()"
        >新增</el-button>
        <el-button
          v-if="isAuth('jingsaibaoming','删除') && contents.tableSelection"
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
          :size="contents.tableSize"
          :show-header="contents.tableShowHeader"
          :header-row-style="headerRowStyle"
          :header-cell-style="headerCellStyle"
          :border="contents.tableBorder"
          :fit="contents.tableFit"
          :stripe="contents.tableStripe"
          :row-style="rowStyle"
          :cell-style="cellStyle"
          :data="dataList"
          v-loading="dataListLoading"
          @selection-change="selectionChangeHandler"
          v-if="isAuth('jingsaibaoming','查看')">
            <el-table-column v-if="contents.tableSelection"
                type="selection"
                header-align="center"
                align="center"
                width="50">
            </el-table-column>
            <el-table-column label="索引" v-if="contents.tableIndex" type="index" width="70" align="center" />
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
                prop="jingsaimingcheng"
                header-align="center"
                min-width="350"
                show-overflow-tooltip
                label="竞赛名称">
              <template slot-scope="scope">
                <span style="font-weight: 600; color: #5c7cfa;">{{scope.row.jingsaimingcheng}}</span>
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
                prop="jingsaileixing"
                header-align="center"
                width="150"
                label="竞赛类型">
              <template slot-scope="scope">
                <el-tag type="info" size="small">{{scope.row.jingsaileixing}}</el-tag>
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
                prop="xuehao"
                header-align="center"
                width="150"
                label="学号">
              <template slot-scope="scope">
                {{scope.row.xuehao}}
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
                prop="xueshengxingming"
                header-align="center"
                width="120"
                label="学生姓名">
              <template slot-scope="scope">
                {{scope.row.xueshengxingming}}
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
                prop="shenqingriqi"
                header-align="center"
                width="150"
                label="报名时间">
              <template slot-scope="scope">
                {{scope.row.shenqingriqi}}
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
                prop="cansaileixing"
                header-align="center"
                width="120"
                label="报名类型">
              <template slot-scope="scope">
                <el-tag type="info" size="small">{{scope.row.cansaileixing || '未设置'}}</el-tag>
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
                prop="sfsh"
                header-align="center"
                width="120"
                label="审核状态">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.sfsh)" size="small">{{scope.row.sfsh || '待审核'}}</el-tag>
              </template>
            </el-table-column>
            <el-table-column width="280" :align="contents.tableAlign"
                header-align="center"
                label="操作">
                <template slot-scope="scope">
                  <el-button v-if="isAuth('jingsaibaoming','查看')"
                    type="primary" icon="el-icon-view" size="mini"
                    @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
                  <template v-if="isAuth('jingsaibaoming','审核') && (!scope.row.sfsh || scope.row.sfsh === '待审核')">
                    <el-button type="warning" icon="el-icon-check" size="mini"
                      @click="shenheHandler(scope.row, '是')">通过</el-button>
                    <el-button type="danger" icon="el-icon-close" size="mini"
                      @click="shenheHandler(scope.row, '否')">驳回</el-button>
                  </template>
                  <el-tag v-else-if="isAuth('jingsaibaoming','审核') && scope.row.sfsh && scope.row.sfsh !== '待审核'" size="small" :type="scope.row.sfsh === '是' || scope.row.sfsh === '通过' ? 'success' : 'danger'">已审核</el-tag>
                  <el-button v-if="isAuth('jingsaibaoming','删除')"
                    type="danger" icon="el-icon-delete" size="mini"
                    @click="deleteHandler(scope.row.id)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
          class="tech-pagination"
          :layout="layouts"
          @size-change="sizeChangeHandle"
          @current-change="currentChangeHandle"
          :current-page="pageIndex"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="Number(contents.pageEachNum)"
          :total="totalPage"
          :small="contents.pageStyle"
          :background="contents.pageBtnBG"
          :style="{textAlign:contents.pagePosition==1?'left':contents.pagePosition==2?'center':'right'}"
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
      searchForm: { key: "", sfsh: '' },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      showFlag: true,
      addOrUpdateFlag: false,
      contents: {"tableSelection":true,"tableIndex":true,"tableSortable":true,"tableStripe":false,"tableFit":true,"tableShowHeader":true,"tableBorder":false,"pageTotal":true,"pageSizes":true,"pagePrevNext":true,"pagePager":true,"pageJumper":true,"pageStyle":true,"pageBtnBG":true,"pageEachNum":10,"pagePosition":"1"},
      layouts: '',
      // 表格样式属性（避免Vue警告）
      headerRowStyle: {},
      headerCellStyle: {},
      rowStyle: {},
      cellStyle: {},
      // 统计信息
      statistics: {
        totalBaoming: 0,
        passedCount: 0,
        pendingCount: 0
      },
    };
  },
  computed: {
    // 根据用户角色动态设置页面标题
    pageTitle() {
      const tableName = this.$storage.get("sessionTable");
      if (tableName === "xuesheng") {
        return "我的报名";
      }
      if (tableName === "jiaoshi") {
        return "我的竞赛报名";
      }
      return "竞赛报名管理";
    },
    pageSubtitle() {
      const tableName = this.$storage.get("sessionTable");
      if (tableName === "xuesheng") {
        return "My Registration Records";
      }
      if (tableName === "jiaoshi") {
        return "My Competition Registrations";
      }
      return "Competition Registration Management";
    },
    // 判断是否为学生角色
    isStudent() {
      const tableName = this.$storage.get("sessionTable");
      return tableName === "xuesheng";
    },
    // 判断是否为教师角色
    isTeacher() {
      const tableName = this.$storage.get("sessionTable");
      return tableName === "jiaoshi";
    }
  },
  created() {
    this.init();
    this.getDataList();
    this.getStatistics();
    this.contentPageStyleChange()
  },
  components: { AddOrUpdate },
  methods: {
    contentPageStyleChange(){
      let arr = []
      if(this.contents.pageTotal) arr.push('total')
      if(this.contents.pageSizes) arr.push('sizes')
      if(this.contents.pagePrevNext){
        arr.push('prev')
        if(this.contents.pagePager) arr.push('pager')
        arr.push('next')
      }
      if(this.contents.pageJumper) arr.push('jumper')
      this.layouts = arr.join()
    },
    getStatusType(status) {
      if (!status) return 'warning'
      if (status === '是' || status.includes('通过') || status.includes('成功')) return 'success'
      if (status === '否' || status.includes('拒绝') || status.includes('失败')) return 'danger'
      if (status.includes('待')) return 'warning'
      return 'info'
    },
    init () {},
    search() {
      this.pageIndex = 1;
      this.getDataList();
    },
    getDataList() {
      this.dataListLoading = true;
      let params = { page: this.pageIndex, limit: this.pageSize, sort: 'id' }
      
      // 根据用户角色过滤数据
      const tableName = this.$storage.get("sessionTable");
      
      if (tableName === "xuesheng") {
        // 学生用户只能查看自己的报名记录
        let xuehao = this.$storage.get("username");
        if (!xuehao) {
          xuehao = this.$storage.get("adminName");
        }
        if (xuehao) {
          params['xuehao'] = xuehao;
        }
      }
      // 教师和管理员可以查看所有竞赛的报名信息（报名管理需要显示所有竞赛的报名）
      
      // 添加模糊查询条件
      if(this.searchForm.jingsaimingcheng && this.searchForm.jingsaimingcheng.trim()) {
        params['jingsaimingcheng'] = '%' + this.searchForm.jingsaimingcheng.trim() + '%'
      }
      if(this.searchForm.jingsaileixing && this.searchForm.jingsaileixing.trim()) {
        params['jingsaileixing'] = '%' + this.searchForm.jingsaileixing.trim() + '%'
      }
      // 审核状态筛选
      if(this.searchForm.sfsh) {
        params['sfsh'] = this.searchForm.sfsh
      }
      
      console.log('最终请求参数:', params);
      console.log('完整请求URL:', this.$http.defaults.baseURL + '/jingsaibaoming/page');
      
      this.$http({ url: "jingsaibaoming/page", method: "get", params }).then(({ data }) => {
        console.log('报名列表API响应:', data);
        if (data && data.code === 0) {
          // 兼容两种返回格式：data.data.list 或 data.page.list
          if (data.data && data.data.list) {
            this.dataList = data.data.list;
            this.totalPage = data.data.total;
          } else if (data.page && data.page.list) {
            this.dataList = data.page.list;
            this.totalPage = data.page.total;
          } else {
            console.warn('未知的数据格式:', data);
            this.dataList = [];
            this.totalPage = 0;
          }
          console.log('报名列表数据:', this.dataList, '总数:', this.totalPage);
        } else {
          console.error('查询失败:', data.msg);
          this.dataList = [];
          this.totalPage = 0;
          this.$message.error(data.msg || '查询失败，请重试');
        }
        this.dataListLoading = false;
      }).catch(err => {
        console.error('报名列表查询异常:', err);
        this.dataList = [];
        this.totalPage = 0;
        this.dataListLoading = false;
        this.$message.error('查询失败：' + (err.message || '请检查后端服务是否正常运行'));
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
    addOrUpdateHandler(id,type) {
      this.showFlag = false;
      this.addOrUpdateFlag = true;
      if(type!='info'){ type = 'else'; }
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id,type);
      });
    },
    deleteHandler(id) {
      // 直接使用原始 ID，避免 Number() 转换导致 19 位雪花算法 ID 精度丢失
      var ids = id ? [id] : this.dataListSelections.map(item => item.id);
      this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$http({ url: "jingsaibaoming/delete", method: "post", data: ids }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message({ message: data.msg || "操作成功", type: "success", duration: 1500, onClose: () => { 
              this.search();
              this.getStatistics();
            }});
          } else {
            this.$message.error(data.msg);
          }
        });
      });
    },
    // 审核报名
    shenheHandler(row, result) {
      let tip = result === '是' ? '通过' : '驳回';
      this.$confirm(`确定审核${tip}该报名申请?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 传入完整的报名数据，包括学号等必填字段
        let updateData = Object.assign({}, row, { sfsh: result });
        this.$http({
          url: 'jingsaibaoming/update',
          method: 'post',
          data: updateData
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message.success('审核成功');
            this.search();
            this.getStatistics();
          } else {
            this.$message.error(data.msg || '审核失败');
          }
        });
      });
    },
    // 获取统计信息
    getStatistics() {
      this.$http({
        url: 'jingsaibaoming/statistics',
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

.tech-table {
  ::v-deep .el-table__body tr:hover > td {
    background: rgba(#00f2fe, 0.08) !important;
  }
  ::v-deep .el-button--mini {
    padding: 7px 12px;
    margin: 2px;
  }
  /* 统一表格单元格内边距 */
  ::v-deep .el-table__cell {
    padding: 12px 0;
  }
}

.tech-pagination { margin-top: 20px; }



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
