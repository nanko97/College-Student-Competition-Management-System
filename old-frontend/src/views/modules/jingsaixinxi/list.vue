  <template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">竞赛信息管理</h2>
      <p class="page-subtitle">Competition Information Management</p>
    </div>

    <!-- 提示信息 -->
    <div class="role-tip">
      <i class="el-icon-info"></i>
      <span>提示：管理所有竞赛信息，包括竞赛名称、类型、级别、时间等详细信息</span>
    </div>

    <!-- 统计信息 -->
    <div class="statistics-wrapper">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="stat-card stat-purple">
            <div class="stat-icon"><i class="el-icon-trophy"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.totalJingsai || 0 }}</div>
              <div class="stat-label">竞赛总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card stat-green">
            <div class="stat-icon"><i class="el-icon-video-play"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.ongoingJingsai || 0 }}</div>
              <div class="stat-label">进行中竞赛</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card stat-blue">
            <div class="stat-icon"><i class="el-icon-user"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.baomingJingsai || 0 }}</div>
              <div class="stat-label">报名竞赛数</div>
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
            <el-form-item label="竞赛地点">
              <el-input 
                v-model="searchForm.jingsaididian" 
                placeholder="请输入竞赛地点" 
                clearable
                prefix-icon="el-icon-location-outline"
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
        <!-- 竞赛信息页面：不显示任何操作按钮 -->
        <!-- 所有操作都集中到“我的竞赛”菜单中进行 -->
      </div>

      <!-- 数据表格 -->
      <div class="table-wrapper">
        <el-table 
          class="tech-table"
          :size="contents.tableSize" 
          :show-header="contents.tableShowHeader"
          :header-row-style="headerRowStyle" 
          :header-cell-style="headerCellStyle"
          border
          :fit="contents.tableFit"
          :stripe="contents.tableStripe"
          :row-style="rowStyle"
          :cell-style="cellStyle"
          :data="dataList"
          v-loading="dataListLoading"
          @selection-change="selectionChangeHandler"
          v-if="isAuth('jingsaixinxi','查看')">
            <el-table-column v-if="contents.tableSelection"
                type="selection"
                header-align="center"
                align="center"
                width="50">
            </el-table-column>
            <el-table-column label="索引" v-if="contents.tableIndex" type="index" width="70" align="center" />
            <el-table-column :sortable="contents.tableSortable" align="center"
                prop="jingsaimingcheng"
                header-align="center"
                min-width="250"
                show-overflow-tooltip
                label="竞赛名称">
              <template slot-scope="scope">
                <span style="font-weight: 600; color: #5c7cfa;">{{scope.row.jingsaimingcheng}}</span>
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" align="center"
                prop="nianfen"
                header-align="center"
                width="110"
                label="年份">
              <template slot-scope="scope">
                <span style="font-weight: 600; color: #f76707;">{{scope.row.nianfen || '-'}}</span>
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" align="center"
                prop="jingsaiJibie"
                header-align="center"
                width="120"
                label="竞赛级别">
              <template slot-scope="scope">
                <el-tag :type="scope.row.jingsaiJibie === 'A类' ? 'danger' : scope.row.jingsaiJibie === 'B类' ? 'warning' : 'success'" size="small">{{scope.row.jingsaiJibie || '-'}}</el-tag>
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" align="center"
                prop="jingsaileixing"
                header-align="center"
                width="130"
                label="竞赛类型">
              <template slot-scope="scope">
                <el-tag type="info" size="small">{{scope.row.jingsaileixing || '-'}}</el-tag>
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" align="center"
                prop="jingsaididian"
                header-align="center"
                width="150"
                show-overflow-tooltip
                label="竞赛地点">
              <template slot-scope="scope">
                {{scope.row.jingsaididian || '-'}}
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" align="center"
                prop="jingsaishijian"
                header-align="center"
                width="160"
                label="竞赛时间">
              <template slot-scope="scope">
                {{scope.row.jingsaishijian || '-'}}
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" align="center"
                prop="shifouYouSaidao"
                header-align="center"
                width="110"
                label="有赛道">
              <template slot-scope="scope">
                <el-tag :type="scope.row.shifouYouSaidao === '是' ? 'success' : 'info'" size="small">{{scope.row.shifouYouSaidao || '-'}}</el-tag>
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" align="center"
                prop="shifouXuyaoJinji"
                header-align="center"
                width="110"
                label="需晋级">
              <template slot-scope="scope">
                <el-tag :type="scope.row.shifouXuyaoJinji === '是' ? 'success' : 'info'" size="small">{{scope.row.shifouXuyaoJinji || '-'}}</el-tag>
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" align="center"
                prop="moshi"
                header-align="center"
                width="100"
                label="模式">
              <template slot-scope="scope">
                <el-tag :type="moshiTagType(scope.row.moshi)" size="small" effect="dark">{{scope.row.moshi || '-'}}</el-tag>
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" align="center"
                prop="jiaofeimoshi"
                header-align="center"
                width="100"
                label="缴费模式">
              <template slot-scope="scope">
                <el-tag :type="jiaofeimoshiTagType(scope.row.jiaofeimoshi)" size="small" effect="dark">{{scope.row.jiaofeimoshi || '-'}}</el-tag>
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" align="center"
                prop="jingsaiFeiyong"
                header-align="center"
                width="120"
                label="费用金额">
              <template slot-scope="scope">
                <span v-if="scope.row.jiaofeimoshi==='免费'" style="color: #67c23a;">免费</span>
                <span v-else style="font-weight: 700; color: #ff5252;">¥{{scope.row.jingsaiFeiyong || '0'}}</span>
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" align="center" 
                prop="fengmiantupian"
                header-align="center"
                width="120"
                label="封面图片">
              <template slot-scope="scope">
                <div v-if="scope.row.fengmiantupian" class="image-preview">
                  <img :src="$imageUrl(scope.row.fengmiantupian.split(',')[0])" width="60" height="60" style="border-radius: 8px;" @error="handleImageError">
                </div>
                <div v-else class="no-image">无图片</div>
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" align="center"
                prop="gonghao"
                header-align="center"
                width="130"
                label="工号">
              <template slot-scope="scope">
                {{scope.row.gonghao}}
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" align="center"
                prop="jiaoshixingming"
                header-align="center"
                width="120"
                label="教师姓名">
              <template slot-scope="scope">
                {{scope.row.jiaoshixingming}}
              </template>
            </el-table-column>
            <el-table-column width="200" align="center"
                header-align="center"
                label="操作">
                <template slot-scope="scope">
                  <!-- 学生端显示报名按钮 -->
                  <el-button v-if="isStudent()" 
                    type="warning" icon="el-icon-user" size="mini" 
                    @click="baomingHandle(scope.row)"
                    :disabled="isBaomingEnded(scope.row)">立即报名</el-button>
                  <!-- 教师/管理员只显示详情按钮 -->
                  <el-button v-if="!isStudent() && isAuth('jingsaixinxi','查看')" 
                    type="primary" icon="el-icon-view" size="mini" 
                    @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
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
import { getCurDateTime } from "@/utils/utils";
export default {
  data() {
    return {
      searchForm: { key: "" },
      form:{},
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      showFlag: true,
      addOrUpdateFlag:false,
      jingsaixinxiCrossAddOrUpdateFlag: false,
      contents:{"tableSelection":true,"tableIndex":true,"tableSortable":true,"tableStripe":false,"tableFit":true,"tableShowHeader":true,"tableBorder":false,"pageTotal":true,"pageSizes":true,"pagePrevNext":true,"pagePager":true,"pageJumper":true,"pageStyle":true,"pageBtnBG":true,"pageEachNum":10,"pagePosition":"1"},
      layouts: '',
      // 表格样式方法（避免Vue警告）
      headerRowStyle: {},
      headerCellStyle: {},
      rowStyle: {},
      cellStyle: {},
      // 统计信息
      statistics: {
        totalJingsai: 0,
        ongoingJingsai: 0,
        baomingJingsai: 0
      },
    };
  },
  created() {
    this.init();
    this.getDataList();
    this.getStatistics();
    this.contentPageStyleChange()
  },
  components: { AddOrUpdate },
  methods: {
    // 模式标签颜色
    moshiTagType(moshi) {
      const map = { '线上': 'primary', '线下': 'warning', '结合': 'success' };
      return map[moshi] || 'info';
    },
    // 缴费模式标签颜色
    jiaofeimoshiTagType(jiaofeimoshi) {
      const map = { '付费': 'danger', '免费': 'success' };
      return map[jiaofeimoshi] || 'info';
    },
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
    // 添加缺失的方法，供子组件调用
    contentStyleChange() {
      // 此方法用于子组件返回时刷新页面样式
      this.contentPageStyleChange();
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
      if(this.searchForm.jingsaileixing) params['jingsaileixing'] = '%' + this.searchForm.jingsaileixing + '%'
      if(this.searchForm.jingsaididian) params['jingsaididian'] = '%' + this.searchForm.jingsaididian + '%'
      
      this.$http({ url: "jingsaixinxi/page", method: "get", params }).then(({ data }) => {
        console.log('竞赛列表API响应:', data);
        if (data && data.code === 0) {
          // 兼容两种返回格式：data.data.list 或 data.page.list
          let list = [];
          if (data.data && data.data.list) {
            list = data.data.list;
            this.totalPage = data.data.total;
          } else if (data.page && data.page.list) {
            list = data.page.list;
            this.totalPage = data.page.total;
          } else {
            console.warn('未知的数据格式:', data);
            list = [];
            this.totalPage = 0;
          }
          
          // 字段兼容处理：确保所有字段都有值
          this.dataList = list.map(item => {
            // 打印第一条数据的字段名，用于调试
            if (list.indexOf(item) === 0) {
              console.log('第一条数据的所有字段名:', Object.keys(item));
              console.log('第一条数据的完整内容:', JSON.stringify(item, null, 2));
            }
            
            return {
              ...item,
              // 确保竞赛地点字段有值
              jingsaididian: item.jingsaididian || '-',
              // 确保竞赛时间字段有值
              jingsaishijian: item.jingsaishijian || '-',
              // 确保封面图片字段有值
              fengmiantupian: item.fengmiantupian || '',
              // 确保工号字段有值
              gonghao: item.gonghao || '-',
              // 确保教师姓名字段有值
              jiaoshixingming: item.jiaoshixingming || '-',
              // 确保有赛道字段有值
              shifouYouSaidao: item.shifouYouSaidao || item.shifou_you_saidao || '?',
              // 确保需晋级字段有值
              shifouXuyaoJinji: item.shifouXuyaoJinji || item.shifou_xuyao_jinji || '?',
              // 确保竞赛级别字段有值
              jingsaiJibie: item.jingsaiJibie || item.jingsai_jibie || '-'
            };
          });
          
          console.log('竞赛列表数据:', this.dataList, '总数:', this.totalPage);
        } else {
          console.error('查询失败:', data.msg);
          this.dataList = [];
          this.totalPage = 0;
          this.$message.error(data.msg || '查询失败，请重试');
        }
        this.dataListLoading = false;
      }).catch(err => {
        console.error('竞赛列表查询失败:', err);
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
      this.jingsaixinxiCrossAddOrUpdateFlag = false;
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
        this.$http({ url: "jingsaixinxi/delete", method: "post", data: ids }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message({ message: "操作成功", type: "success", duration: 1500, onClose: () => { this.search(); }});
          } else {
            this.$message.error(data.msg);
          }
        });
      });
    },
    // 判断是否为学生用户
    isStudent() {
      const tableName = this.$storage.get("sessionTable");
      return tableName === "xuesheng";
    },
    // 判断是否可以编辑/删除（教师只能操作自己创建的竞赛）
    canEditDelete(row) {
      const tableName = this.$storage.get("sessionTable");
      // 管理员可以编辑/删除所有竞赛
      if (tableName === "users") {
        return true;
      }
      // 教师只能编辑/删除自己创建的竞赛
      if (tableName === "jiaoshi") {
        const currentGonghao = this.$storage.get("username");
        return row.gonghao === currentGonghao;
      }
      // 其他角色默认不能编辑/删除
      return false;
    },
    // 检查报名是否已结束
    isBaomingEnded(row) {
      if (!row.jingsaishijian) return false;
      const now = new Date();
      const endTime = new Date(row.jingsaishijian);
      return now > endTime;
    },
    // 报名处理
    baomingHandle(row) {
      this.$confirm(`确认要报名"${row.jingsaimingcheng}"吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 获取当前登录学生的信息
        const xuehao = this.$storage.get("username");
        // 优先使用userRealName，如果没有则从session中获取其他可能的key
        let xueshengxingming = this.$storage.get("userRealName");
        if (!xueshengxingming) {
          xueshengxingming = this.$storage.get("name") || this.$storage.get("realName") || '';
        }
            
        console.log('报名学生信息 - 学号:', xuehao, '姓名:', xueshengxingming);
                    
        this.$http({
          url: 'jingsaibaoming/add',
          method: 'post',
          data: {
            jingsaiId: row.id,
            jingsaimingcheng: row.jingsaimingcheng,
            jingsaileixing: row.jingsaileixing,
            cansaileixing: '个人赛', // 默认为个人赛，与数据库保持一致
            saidao_mingcheng: '',
            cansairenyuan: xueshengxingming,
            xuehao: xuehao,
            xueshengxingming: xueshengxingming,
            shenqingriqi: this.getCurDateTime(),
            sfsh: '待审核'
          }
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message({
              message: '报名成功！请等待教师审核，审核通过后将生成缴费记录',
              type: 'success',
              duration: 5000
            });
            // 刷新列表和统计信息
            this.getDataList();
            this.getStatistics();
          } else {
            this.$message.error(data.msg || '报名失败');
          }
        }).catch(err => {
          console.error('报名失败:', err);
          this.$message.error('报名失败：' + (err.message || '请重试'));
        });
      }).catch(() => {});
    },
    // 获取统计信息
    getStatistics() {
      this.$http({
        url: 'jingsaixinxi/statistics',
        method: 'get'
      }).then(({data}) => {
        if (data && data.code === 0) {
          // 后端返回格式：R.ok().put("data", stats)
          const stats = data.data || {}
          // 使用后端返回的新字段名
          this.statistics = {
            totalJingsai: stats.totalJingsai || 0,
            ongoingJingsai: stats.ongoingJingsai || 0,
            baomingJingsai: stats.baomingJingsai || 0
          }
          console.log('竞赛统计数据加载成功:', this.statistics)
          console.log('原始返回数据:', stats)
        }
      }).catch((error) => {
        console.error('获取统计数据失败:', error)
      })
    },
    // 图片加载错误处理
    handleImageError(e) {
      // 检查是否已经是默认图片（避免无限重试）
      if (e.target.dataset.isDefault) {
        // 默认图片也加载失败，隐藏图片
        e.target.style.display = 'none';
        return;
      }
      
      // 首次失败，尝试加载默认图片
      if (!e.target.dataset.retryCount) {
        e.target.dataset.retryCount = '0';
      }
      
      const retryCount = parseInt(e.target.dataset.retryCount);
      
      // 最多重试1次
      if (retryCount === 0) {
        e.target.dataset.retryCount = '1';
        e.target.dataset.isDefault = 'true';
        e.target.src = this.$imageUrl('upload/比赛.png');
      } else {
        // 重试也失败，隐藏图片
        e.target.style.display = 'none';
      }
    },
  }
};
</script>

<style lang="scss" scoped>
@import '@/assets/css/tech-theme.scss';
@import '@/assets/css/global-responsive-mixin.scss';
@import '@/assets/css/statistics-cards.scss';

/* 统计卡片样式优化 */
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

.image-preview img {
  transition: transform 0.3s;
  &:hover { transform: scale(1.5); }
}

.no-image { color: #909399; font-size: 12px; }

.tech-table {
  ::v-deep .el-table__body tr:hover > td {
    background: rgba(#5c7cfa, 0.08) !important;
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
