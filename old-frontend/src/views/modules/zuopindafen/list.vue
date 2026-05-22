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
      <span>提示：为学生作品进行评分，请确保评分公正合理</span>
    </div>

    <!-- 学生提示信息 -->
    <div class="role-tip" v-if="!isTeacher">
      <i class="el-icon-info"></i>
      <span>提示：成绩由教师评定，如对成绩有异议，可发起成绩复核申请</span>
    </div>

    <!-- 统计信息 -->
    <div class="statistics-wrapper">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="stat-card stat-purple">
            <div class="stat-icon"><i class="el-icon-document"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.totalPingfen || 0 }}</div>
              <div class="stat-label">评分总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card stat-cyan">
            <div class="stat-icon"><i class="el-icon-star-on"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.avgScore || 0 }}</div>
              <div class="stat-label">平均分数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card stat-blue">
            <div class="stat-icon"><i class="el-icon-refresh"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.fuheCount || 0 }}</div>
              <div class="stat-label">复核申请</div>
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
            <!-- 学生角色不显示学生姓名筛选，因为只能查看自己的成绩 -->
            <el-form-item v-if="isTeacher" label="学生姓名">
              <el-input 
                v-model="searchForm.xueshengxingming" 
                placeholder="请输入学生姓名" 
                clearable
                prefix-icon="el-icon-user"
              ></el-input>
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
          v-if="isAuth('zuopindafen','查看')">
            <el-table-column v-if="contents.tableSelection"
                type="selection"
                header-align="center"
                align="center"
                width="50">
            </el-table-column>
            <el-table-column label="索引" v-if="contents.tableIndex" header-align="center" align="center" width="60">
              <template slot-scope="scope">
                {{ (pageIndex - 1) * pageSize + scope.$index + 1 }}
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
                prop="xuehao"
                header-align="center"
                label="学号">
              <template slot-scope="scope">
                {{scope.row.xuehao}}
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
                prop="xueshengxingming"
                header-align="center"
                label="学生姓名">
              <template slot-scope="scope">
                {{scope.row.xueshengxingming}}
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
                prop="jingsaimingcheng"
                header-align="center"
                label="竞赛名称">
              <template slot-scope="scope">
                <span style="font-weight: 600; color: #5c7cfa;">{{scope.row.jingsaimingcheng}}</span>
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
                prop="jingsaileixing"
                header-align="center"
                label="竞赛类型">
              <template slot-scope="scope">
                <el-tag type="info" size="small">{{scope.row.jingsaileixing}}</el-tag>
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
                prop="zuopinpingfen"
                header-align="center"
                label="作品评分">
              <template slot-scope="scope">
                <span style="font-weight: 700; color: #ff6b6b; font-size: 18px;">{{scope.row.zuopinpingfen}}</span>
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
                prop="pingjiashijian"
                header-align="center"
                label="评价时间">
              <template slot-scope="scope">
                {{scope.row.pingjiashijian}}
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
                prop="gonghao"
                header-align="center"
                label="工号">
              <template slot-scope="scope">
                {{scope.row.gonghao}}
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
                prop="jiaoshixingming"
                header-align="center"
                label="教师姓名">
              <template slot-scope="scope">
                {{scope.row.jiaoshixingming}}
              </template>
            </el-table-column>
            <!-- 学生端显示复核次数 -->
            <el-table-column v-if="!isTeacher" 
                label="复核次数" 
                align="center" 
                width="100">
              <template slot-scope="scope">
                <span v-if="scope.row.fuheCount !== undefined" style="font-weight: bold;">
                  {{ scope.row.fuheCount }}/2
                </span>
                <span v-else style="color: #868e96;">0/2</span>
              </template>
            </el-table-column>
            <!-- 学生端显示复核状态 -->
            <el-table-column v-if="!isTeacher" 
                prop="fuheStatus" 
                label="复核状态" 
                align="center" 
                width="120">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.fuheStatus" 
                        :type="getFuheStatusType(scope.row.fuheStatus)" 
                        size="small">
                  {{scope.row.fuheStatus}}
                </el-tag>
                <span v-else style="color: #868e96;">未申请</span>
              </template>
            </el-table-column>
            <el-table-column width="200" :align="contents.tableAlign"
                header-align="center"
                label="操作">
                <template slot-scope="scope">
                  <!-- 学生可以发起成绩复核 -->
                  <el-button v-if="!isTeacher" 
                    type="warning" 
                    icon="el-icon-refresh" 
                    size="mini" 
                    @click="applyReview(scope.row)"
                    :disabled="isReviewDisabled(scope.row)"
                    :title="getReviewButtonTitle(scope.row)">
                    成绩复核
                  </el-button>
                  <!-- 教师可以查看详情、修改、删除 -->
                  <template v-else>
                    <el-button v-if="isAuth('zuopindafen','查看')" 
                      type="primary" icon="el-icon-view" size="mini" 
                      @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
                    <el-button v-if="isAuth('zuopindafen','修改')" 
                      type="success" icon="el-icon-edit" size="mini" 
                      @click="addOrUpdateHandler(scope.row.id)">修改</el-button>
                    <el-button v-if="isAuth('zuopindafen','删除')" 
                      type="danger" icon="el-icon-delete" size="mini" 
                      @click="deleteHandler(scope.row.id)">删除</el-button>
                  </template>
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

    <!-- 成绩复核申请对话框 -->
    <el-dialog
      title="成绩复核申请"
      :visible.sync="reviewDialogVisible"
      width="600px"
      :close-on-click-modal="false">
      <el-form :model="reviewForm" :rules="reviewRules" ref="reviewForm" label-width="100px">
        <el-form-item label="竞赛名称">
          <el-input v-model="reviewForm.jingsaimingcheng" readonly></el-input>
        </el-form-item>
        <el-form-item label="原成绩">
          <el-input v-model="reviewForm.zuopinpingfen" readonly>
            <template slot="prepend">分</template>
          </el-input>
        </el-form-item>
        <el-form-item label="复核理由" prop="fuheReason">
          <el-input 
            type="textarea" 
            v-model="reviewForm.fuheReason" 
            :rows="4"
            placeholder="请详细说明您认为成绩有误的原因..."
            maxlength="500"
            show-word-limit>
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="reviewDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitReview">提交申请</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import AddOrUpdate from "./add-or-update";
import { getCurDateTime } from "@/utils/utils";
export default {
  data() {
    return {
      searchForm: { key: "" },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      showFlag: true,
      addOrUpdateFlag:false,
      zuopindafenCrossAddOrUpdateFlag: false,
      contents:{"tableSelection":true,"tableIndex":true,"tableSortable":true,"tableStripe":false,"tableFit":true,"tableShowHeader":true,"tableBorder":false,"pageTotal":true,"pageSizes":true,"pagePrevNext":true,"pagePager":true,"pageJumper":true,"pageStyle":true,"pageBtnBG":true,"pageEachNum":10,"pagePosition":"1"},
      layouts: '',
      // 表格样式属性（避免Vue警告）
      headerRowStyle: {},
      headerCellStyle: {},
      rowStyle: {},
      cellStyle: {},
      // 统计信息
      statistics: {
        totalPingfen: 0,
        avgScore: 0,
        fuheCount: 0
      },
      // 成绩复核相关
      reviewDialogVisible: false,
      reviewForm: {
        zuopindafenId: '',
        jingsaimingcheng: '',
        zuopinpingfen: '',
        fuheReason: ''
      },
      reviewRules: {
        fuheReason: [
          { required: true, message: '请输入复核理由', trigger: 'blur' },
          { min: 10, message: '复核理由不能少于10个字', trigger: 'blur' }
        ]
      }
    };
  },
  created() {
    this.init();
    this.getDataList();
    this.getStatistics();
    this.contentPageStyleChange()
  },
  components: { AddOrUpdate },
  computed: {
    // 根据用户角色动态设置页面标题
    pageTitle() {
      const tableName = this.$storage.get("sessionTable");
      if (tableName === "xuesheng") {
        return "我的打分";
      } else if (tableName === "jiaoshi") {
        return "我的打分";
      }
      return "我的打分";
    },
    pageSubtitle() {
      const tableName = this.$storage.get("sessionTable");
      if (tableName === "xuesheng") {
        return "My Scoring";
      }
      return "My Scoring";
    },
    // 判断是否为教师角色
    isTeacher() {
      const tableName = this.$storage.get("sessionTable");
      return tableName === "jiaoshi";
    },
    // 判断是否为学生角色
    isStudent() {
      const tableName = this.$storage.get("sessionTable");
      return tableName === "xuesheng";
    }
  },
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
    init () {},
    search() {
      this.pageIndex = 1;
      this.getDataList();
    },
    // 获取当前时间
    getCurDateTime() {
      return getCurDateTime();
    },
    getDataList() {
      this.dataListLoading = true;
      let params = { page: this.pageIndex, limit: this.pageSize, sort: 'id' }
      if(this.searchForm.jingsaimingcheng) params['jingsaimingcheng'] = '%' + this.searchForm.jingsaimingcheng + '%'
      if(this.searchForm.xueshengxingming) params['xueshengxingming'] = '%' + this.searchForm.xueshengxingming + '%'
      this.$http({ url: "zuopindafen/page", method: "get", params }).then(({ data }) => {
        if (data && data.code === 0) {
          this.dataList = data.data.list;
          this.totalPage = data.data.total;
        } else {
          this.dataList = [];
          this.totalPage = 0;
        }
        this.dataListLoading = false;
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
        this.$http({ url: "zuopindafen/delete", method: "post", data: ids }).then(({ data }) => {
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
        url: 'zuopindafen/statistics',
        method: 'get'
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.statistics = data.data || {}
        }
      }).catch((error) => {
        console.error('获取统计数据失败:', error)
      })
    },
    // 空方法，避免子组件调用时报错
    contentStyleChange() {
      // 该方法用于刷新页面样式，当前页面不需要特殊处理
    },
    // 获取复核状态类型
    getFuheStatusType(status) {
      if (status === '待审核') return 'warning'
      if (status === '已通过') return 'success'
      if (status === '已驳回') return 'danger'
      return 'info'
    },
    // 判断复核按钮是否禁用
    isReviewDisabled(row) {
      // 【自动检索】如果复核次数已达到2次，永久禁用
      if (row.fuheCount && row.fuheCount >= 2) {
        return true
      }
      // 【自动检索】如果已经有复核记录且最新状态不是"已驳回"，则禁用
      return row.fuheStatus && row.fuheStatus !== '' && row.fuheStatus !== '已驳回'
    },
    // 获取复核按钮提示文本
    getReviewButtonTitle(row) {
      // 【自动检索】复核次数已达上限
      if (row.fuheCount && row.fuheCount >= 2) {
        return `🚫 该成绩复核次数已达上限（${row.fuheCount}/2次），无法再次申请`
      }
      // 【自动检索】没有复核记录
      if (!row.fuheStatus || row.fuheStatus === '') {
        return '✅ 点击发起成绩复核申请'
      }
      // 【自动检索】复核申请正在审核中
      if (row.fuheStatus === '待审核') {
        return `⏳ 您的复核申请正在审核中（${row.fuheCount}/2次）`
      }
      // 【自动检索】复核已通过
      if (row.fuheStatus === '已通过') {
        return `✅ 该成绩已复核通过（${row.fuheCount}/2次）`
      }
      // 【自动检索】复核已驳回，可以重新申请
      if (row.fuheStatus === '已驳回') {
        return `🔄 该成绩复核已被驳回，可以重新申请（已用${row.fuheCount}/2次，剩余${2 - row.fuheCount}次）`
      }
      return ''
    },
    // 发起成绩复核申请
    applyReview(row) {
      this.reviewForm = {
        zuopindafenId: row.id,
        jingsaimingcheng: row.jingsaimingcheng,
        zuopinpingfen: row.zuopinpingfen,
        fuheReason: ''
      }
      this.reviewDialogVisible = true
      this.$nextTick(() => {
        this.$refs.reviewForm.clearValidate()
      })
    },
    // 提交复核申请
    submitReview() {
      this.$refs.reviewForm.validate((valid) => {
        if (valid) {
          this.$confirm('确认提交成绩复核申请？教师将收到您的申请并重新审核', '提示', {
            confirmButtonText: '确认提交',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            // 获取当前学生信息
            const xuehao = this.$storage.get('username')
            const xueshengxingming = this.$storage.get('userRealName') || ''
            
            this.$http({
              url: 'zuopindafenfuhe/add',
              method: 'post',
              data: {
                zuopindafenId: this.reviewForm.zuopindafenId,
                jingsaimingcheng: this.reviewForm.jingsaimingcheng,
                yuanChengji: this.reviewForm.zuopinpingfen,
                xuehao: xuehao,
                xueshengxingming: xueshengxingming,
                fuheReason: this.reviewForm.fuheReason,
                fuheStatus: '待审核',
                shenqingShijian: this.getCurDateTime()
              }
            }).then(({ data }) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '成绩复核申请已提交，请耐心等待教师审核',
                  type: 'success',
                  duration: 3000
                })
                this.reviewDialogVisible = false
                // 刷新列表
                this.getDataList()
              } else {
                this.$message.error(data.msg || '提交失败')
              }
            }).catch(err => {
              console.error('提交复核申请失败:', err)
              this.$message.error('提交失败：' + (err.message || '请重试'))
            })
          }).catch(() => {
            // 用户取消操作
            console.log('用户取消了提交')
          })
        } else {
          this.$message.warning('请检查表单填写是否完整')
          return false
        }
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
  ::v-deep .el-input__inner { width: 180px; }
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
