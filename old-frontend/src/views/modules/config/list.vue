<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">管理页面</h2>
      <p class="page-subtitle">Management Page</p>
    </div>

    <!-- 提示信息 -->
    <div class="role-tip">
      <i class="el-icon-info"></i>
      <span>提示：管理系统配置信息，包括轮播图、公告等基础配置</span>
    </div>

    <!-- 列表页 -->
    <div v-if="showFlag">
      <!-- 搜索区域 -->
      <div class="search-wrapper">
        <el-form :inline="true" :model="searchForm" class="tech-search-form">
          <el-form-item label="名称">
            <el-input 
              v-model="searchForm.name" 
              placeholder="请输入名称" 
              clearable
              prefix-icon="el-icon-document"
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="search()">查询</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 操作按钮区域 -->
      <div class="action-wrapper">
        <el-button
          v-if="isAuth('config','新增')"
          type="success"
          icon="el-icon-plus"
          @click="addOrUpdateHandler()"
        >新增配置</el-button>
        <el-button
          v-if="isAuth('config','删除') && contents.tableSelection"
          :disabled="dataListSelections.length <= 0"
          type="danger"
          icon="el-icon-delete"
          @click="deleteHandler()"
        >批量删除</el-button>
      </div>

      <!-- 数据表格 -->
      <div class="table-wrapper">
        <el-table class="tech-table" :size="contents.tableSize" :show-header="contents.tableShowHeader"
            :header-row-style="headerRowStyle" :header-cell-style="headerCellStyle"
            :border="contents.tableBorder"
            :fit="contents.tableFit"
            :stripe="contents.tableStripe"
            :row-style="rowStyle"
            :cell-style="cellStyle"
            :style="{width: '100%',fontSize:contents.tableContentFontSize,color:contents.tableContentFontColor}"
            v-if="isAuth('config','查看')"
            :data="dataList"
            v-loading="dataListLoading"
            @selection-change="selectionChangeHandler">
            <el-table-column  v-if="contents.tableSelection"
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
                <el-table-column  :sortable="contents.tableSortable" :align="contents.tableAlign"
                    prop="name"
                    header-align="center"
		    label="名称">
		     <template slot-scope="scope">
                       {{scope.row.name}}
                     </template>
                </el-table-column>
                  <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign" prop="value"
                    header-align="center"
                    width="200"
                    label="值">
                    <template slot-scope="scope">
                      <div v-if="scope.row.value">
                        <img :src="scope.row.value.split(',')[0]" width="100" height="100">
                      </div>
                      <div v-else>无图片</div>
                    </template>
                  </el-table-column>
            <el-table-column width="300" :align="contents.tableAlign"
                header-align="center"
                label="操作">
                <template slot-scope="scope">
                <el-button v-if="isAuth('config','查看') && contents.tableBtnIcon == 1 && contents.tableBtnIconPosition == 1" type="success" icon="el-icon-tickets" size="mini" @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
                <el-button v-if="isAuth('config','查看') && contents.tableBtnIcon == 1 && contents.tableBtnIconPosition == 2" type="success" size="mini" @click="addOrUpdateHandler(scope.row.id,'info')">详情<i class="el-icon-tickets el-icon--right" /></el-button>
                <el-button v-if="isAuth('config','查看') && contents.tableBtnIcon == 0" type="success" size="mini" @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
                <el-button v-if="isAuth('config','修改') && contents.tableBtnIcon == 1 && contents.tableBtnIconPosition == 1" type="primary" icon="el-icon-edit" size="mini" @click="addOrUpdateHandler(scope.row.id)">修改</el-button>
                <el-button v-if="isAuth('config','修改') && contents.tableBtnIcon == 1 && contents.tableBtnIconPosition == 2" type="primary" size="mini" @click="addOrUpdateHandler(scope.row.id)">修改<i class="el-icon-edit el-icon--right" /></el-button>
                <el-button v-if="isAuth('config','修改') && contents.tableBtnIcon == 0" type="primary" size="mini" @click="addOrUpdateHandler(scope.row.id)">修改</el-button>




                <el-button v-if="isAuth('config','删除') && contents.tableBtnIcon == 1 && contents.tableBtnIconPosition == 1" type="danger" icon="el-icon-delete" size="mini" @click="deleteHandler(scope.row.id)">删除</el-button>
                <el-button v-if="isAuth('config','删除') && contents.tableBtnIcon == 1 && contents.tableBtnIconPosition == 2" type="danger" size="mini" @click="deleteHandler(scope.row.id)">删除<i class="el-icon-delete el-icon--right" /></el-button>
                <el-button v-if="isAuth('config','删除') && contents.tableBtnIcon == 0" type="danger" size="mini" @click="deleteHandler(scope.row.id)">删除</el-button>
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
    <!-- 添加/修改页面  将父组件的search方法传递给子组件-->
    <add-or-update v-if="addOrUpdateFlag" :parent="this" ref="addOrUpdate"></add-or-update>



  </div>
</template>
<script>
import AddOrUpdate from "./add-or-update";
export default {
  data() {
    return {
      searchForm: {
        key: ""
      },
      form:{},
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      showFlag: true,
      sfshVisiable: false,
      shForm: {},
      chartVisiable: false,
      addOrUpdateFlag:false,
      contents:{"searchBtnFontColor":"#333","pagePosition":"1","inputFontSize":"14px","inputBorderRadius":"16px","tableBtnDelFontColor":"#333","tableBtnIconPosition":"2","searchBtnHeight":"40px","inputIconColor":"#C0C4CC","searchBtnBorderRadius":"4px","tableStripe":false,"btnAdAllWarnFontColor":"#333","tableBtnDelBgColor":"rgba(255, 140, 0, 1)","searchBtnIcon":"1","tableSize":"small","searchBtnBorderStyle":"solid","tableSelection":true,"searchBtnBorderWidth":"1px","tableContentFontSize":"14px","searchBtnBgColor":"#fff","inputTitleSize":"14px","btnAdAllBorderColor":"rgba(31, 147, 255, 0.73)","pageJumper":true,"btnAdAllIconPosition":"1","searchBoxPosition":"2","tableBtnDetailFontColor":"#333","tableBtnHeight":"40px","pagePager":true,"searchBtnBorderColor":"#DCDFE6","tableHeaderFontColor":"rgba(12, 12, 13, 0.67)","inputTitle":"1","tableBtnBorderRadius":"16px","btnAdAllFont":"1","btnAdAllDelFontColor":"#333","tableBtnIcon":"1","btnAdAllHeight":"40px","btnAdAllWarnBgColor":"rgba(190, 163, 18, 1)","btnAdAllBorderWidth":"1px","tableStripeFontColor":"#606266","tableBtnBorderStyle":"solid","inputHeight":"40px","btnAdAllBorderRadius":"16px","btnAdAllDelBgColor":"rgba(255, 215, 0, 1)","pagePrevNext":true,"btnAdAllAddBgColor":"rgba(241, 215, 70, 1)","searchBtnFont":"1","tableIndex":true,"btnAdAllIcon":"1","tableSortable":true,"pageSizes":true,"tableFit":true,"pageBtnBG":true,"searchBtnFontSize":"14px","tableBtnEditBgColor":"rgba(255, 140, 0, 1)","inputBorderWidth":"1px","inputFontPosition":"1","inputFontColor":"rgba(82, 135, 234, 1)","pageEachNum":10,"tableHeaderBgColor":"rgba(227, 226, 226, 0.14)","inputTitleColor":"#333","btnAdAllBoxPosition":"1","tableBtnDetailBgColor":"rgba(255, 140, 0, 1)","inputIcon":"1","searchBtnIconPosition":"1","btnAdAllFontSize":"14px","inputBorderStyle":"solid","inputBgColor":"rgba(224, 220, 220, 0.15)","pageStyle":true,"pageTotal":true,"btnAdAllAddFontColor":"#333","tableBtnFont":"1","tableContentFontColor":"rgba(78, 79, 82, 1)","inputBorderColor":"#DCDFE6","tableShowHeader":true,"tableBtnFontSize":"14px","tableBtnBorderColor":"#DCDFE6","inputIconPosition":"2","tableBorder":true,"btnAdAllBorderStyle":"solid","tableBtnBorderWidth":"1px","tableStripeBgColor":"#F5F7FA","tableBtnEditFontColor":"#333","tableAlign":"center"},
      layouts: '',


    };
  },
  created() {
    this.init();
    this.getDataList();
    this.contentStyleChange()
  },
  mounted() {

  },
  filters: {
    htmlfilter: function (val) {
      return val.replace(/<[^>]*>/g).replace(/undefined/g,'');
    }
  },
  components: {
    AddOrUpdate,
  },
  methods: {
    contentStyleChange() {
      this.contentSearchStyleChange()
      this.contentBtnAdAllStyleChange()
      this.contentSearchBtnStyleChange()
      this.contentTableBtnStyleChange()
      this.contentPageStyleChange()
    },
    contentSearchStyleChange() {
      this.$nextTick(()=>{
        document.querySelectorAll('.form-content .slt .el-input__inner').forEach(el=>{
          let textAlign = 'left'
          if(this.contents.inputFontPosition == 2) textAlign = 'center'
          if(this.contents.inputFontPosition == 3) textAlign = 'right'
          el.style.textAlign = textAlign
          el.style.height = this.contents.inputHeight
          el.style.lineHeight = this.contents.inputHeight
          el.style.color = this.contents.inputFontColor
          el.style.fontSize = this.contents.inputFontSize
          el.style.borderWidth = this.contents.inputBorderWidth
          el.style.borderStyle = this.contents.inputBorderStyle
          el.style.borderColor = this.contents.inputBorderColor
          el.style.borderRadius = this.contents.inputBorderRadius
          el.style.backgroundColor = this.contents.inputBgColor
        })
        if(this.contents.inputTitle) {
          document.querySelectorAll('.form-content .slt .el-form-item__label').forEach(el=>{
            el.style.color = this.contents.inputTitleColor
            el.style.fontSize = this.contents.inputTitleSize
            el.style.lineHeight = this.contents.inputHeight
          })
        }
        setTimeout(()=>{
          document.querySelectorAll('.form-content .slt .el-input__prefix').forEach(el=>{
            el.style.color = this.contents.inputIconColor
            el.style.lineHeight = this.contents.inputHeight
          })
          document.querySelectorAll('.form-content .slt .el-input__suffix').forEach(el=>{
            el.style.color = this.contents.inputIconColor
            el.style.lineHeight = this.contents.inputHeight
          })
          document.querySelectorAll('.form-content .slt .el-input__icon').forEach(el=>{
            el.style.lineHeight = this.contents.inputHeight
          })
        },10)

      })
    },
    // 搜索按钮
    contentSearchBtnStyleChange() {
      this.$nextTick(()=>{
        document.querySelectorAll('.form-content .slt .el-button--success').forEach(el=>{
          el.style.height = this.contents.searchBtnHeight
          el.style.color = this.contents.searchBtnFontColor
          el.style.fontSize = this.contents.searchBtnFontSize
          el.style.borderWidth = this.contents.searchBtnBorderWidth
          el.style.borderStyle = this.contents.searchBtnBorderStyle
          el.style.borderColor = this.contents.searchBtnBorderColor
          el.style.borderRadius = this.contents.searchBtnBorderRadius
          el.style.backgroundColor = this.contents.searchBtnBgColor
        })
      })
    },
    // 新增、批量删除
    contentBtnAdAllStyleChange() {
      this.$nextTick(()=>{
        document.querySelectorAll('.form-content .ad .el-button--success').forEach(el=>{
          el.style.height = this.contents.btnAdAllHeight
          el.style.color = this.contents.btnAdAllAddFontColor
          el.style.fontSize = this.contents.btnAdAllFontSize
          el.style.borderWidth = this.contents.btnAdAllBorderWidth
          el.style.borderStyle = this.contents.btnAdAllBorderStyle
          el.style.borderColor = this.contents.btnAdAllBorderColor
          el.style.borderRadius = this.contents.btnAdAllBorderRadius
          el.style.backgroundColor = this.contents.btnAdAllAddBgColor
        })
        document.querySelectorAll('.form-content .ad .el-button--danger').forEach(el=>{
          el.style.height = this.contents.btnAdAllHeight
          el.style.color = this.contents.btnAdAllDelFontColor
          el.style.fontSize = this.contents.btnAdAllFontSize
          el.style.borderWidth = this.contents.btnAdAllBorderWidth
          el.style.borderStyle = this.contents.btnAdAllBorderStyle
          el.style.borderColor = this.contents.btnAdAllBorderColor
          el.style.borderRadius = this.contents.btnAdAllBorderRadius
          el.style.backgroundColor = this.contents.btnAdAllDelBgColor
        })
        document.querySelectorAll('.form-content .ad .el-button--warning').forEach(el=>{
          el.style.height = this.contents.btnAdAllHeight
          el.style.color = this.contents.btnAdAllWarnFontColor
          el.style.fontSize = this.contents.btnAdAllFontSize
          el.style.borderWidth = this.contents.btnAdAllBorderWidth
          el.style.borderStyle = this.contents.btnAdAllBorderStyle
          el.style.borderColor = this.contents.btnAdAllBorderColor
          el.style.borderRadius = this.contents.btnAdAllBorderRadius
          el.style.backgroundColor = this.contents.btnAdAllWarnBgColor
        })
      })
    },
    // 表格
    rowStyle({ row, rowIndex}) {
      if (rowIndex % 2 == 1) {
        if(this.contents.tableStripe) {
          return {color:this.contents.tableStripeFontColor}
        }
      } else {
        return ''
      }
    },
    cellStyle({ row, rowIndex}){
      if (rowIndex % 2 == 1) {
        if(this.contents.tableStripe) {
          return {backgroundColor:this.contents.tableStripeBgColor}
        }
      } else {
        return ''
      }
    },
    headerRowStyle({ row, rowIndex}){
      return {color: this.contents.tableHeaderFontColor}
    },
    headerCellStyle({ row, rowIndex}){
      return {backgroundColor: this.contents.tableHeaderBgColor}
    },
    // 表格按钮
    contentTableBtnStyleChange(){
      // this.$nextTick(()=>{
      //   setTimeout(()=>{
      //     document.querySelectorAll('.table-content .tables .el-table__body .el-button--success').forEach(el=>{
      //       el.style.height = this.contents.tableBtnHeight
      //       el.style.color = this.contents.tableBtnDetailFontColor
      //       el.style.fontSize = this.contents.tableBtnFontSize
      //       el.style.borderWidth = this.contents.tableBtnBorderWidth
      //       el.style.borderStyle = this.contents.tableBtnBorderStyle
      //       el.style.borderColor = this.contents.tableBtnBorderColor
      //       el.style.borderRadius = this.contents.tableBtnBorderRadius
      //       el.style.backgroundColor = this.contents.tableBtnDetailBgColor
      //     })
      //     document.querySelectorAll('.table-content .tables .el-table__body .el-button--primary').forEach(el=>{
      //       el.style.height = this.contents.tableBtnHeight
      //       el.style.color = this.contents.tableBtnEditFontColor
      //       el.style.fontSize = this.contents.tableBtnFontSize
      //       el.style.borderWidth = this.contents.tableBtnBorderWidth
      //       el.style.borderStyle = this.contents.tableBtnBorderStyle
      //       el.style.borderColor = this.contents.tableBtnBorderColor
      //       el.style.borderRadius = this.contents.tableBtnBorderRadius
      //       el.style.backgroundColor = this.contents.tableBtnEditBgColor
      //     })
      //     document.querySelectorAll('.table-content .tables .el-table__body .el-button--danger').forEach(el=>{
      //       el.style.height = this.contents.tableBtnHeight
      //       el.style.color = this.contents.tableBtnDelFontColor
      //       el.style.fontSize = this.contents.tableBtnFontSize
      //       el.style.borderWidth = this.contents.tableBtnBorderWidth
      //       el.style.borderStyle = this.contents.tableBtnBorderStyle
      //       el.style.borderColor = this.contents.tableBtnBorderColor
      //       el.style.borderRadius = this.contents.tableBtnBorderRadius
      //       el.style.backgroundColor = this.contents.tableBtnDelBgColor
      //     })

      //   }, 50)
      // })
    },
    // 分页
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
      this.contents.pageEachNum = 10
    },

    init () {
    },
    search() {
      this.pageIndex = 1;
      this.getDataList();
    },
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true;
      let params = {
        page: this.pageIndex,
        limit: this.pageSize,
        sort: 'id',
      }
          if(this.searchForm.name!='' && this.searchForm.name!=undefined){
            params['name'] = '%' + this.searchForm.name + '%'
          }
      this.$http({
        url: "config/page",
        method: "get",
        params: params
      }).then(({ data }) => {
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
    // 每页数
    sizeChangeHandle(val) {
      this.pageSize = val;
      this.pageIndex = 1;
      this.getDataList();
    },
    // 当前页
    currentChangeHandle(val) {
      this.pageIndex = val;
      this.getDataList();
    },
    // 多选
    selectionChangeHandler(val) {
      this.dataListSelections = val;
    },
    // 添加/修改
    addOrUpdateHandler(id,type) {
      this.showFlag = false;
      this.addOrUpdateFlag = true;
      this.crossAddOrUpdateFlag = false;
      if(type!='info'){
        type = 'else';
      }
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id,type);
      });
    },
    // 查看评论
    // 下载
    download(file){
      window.open(`${file}`)
    },
    // 删除
    deleteHandler(id) {
      var ids = id
        ? [Number(id)]
        : this.dataListSelections.map(item => {
            return Number(item.id);
          });
      this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$http({
          url: "config/delete",
          method: "post",
          data: ids
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message({
              message: "操作成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.search();
              }
            });
          } else {
            this.$message.error(data.msg);
          }
        });
      });
    },
  }

};
</script>
<style lang="scss" scoped>
@import '@/assets/css/tech-theme.scss';
@import '@/assets/css/global-responsive-mixin.scss';

/* 页面头部样式 */
.page-header {
  margin-bottom: 24px;
}

/* 提示信息样式 */
.role-tip {
  margin-bottom: 20px;
}

/* 搜索区域样式 */
.search-wrapper {
  margin-bottom: 20px;
}

/* 操作按钮区域样式 */
.action-wrapper {
  margin-bottom: 20px;
  
  .el-button {
    margin-right: 10px;
    
    &:last-child {
      margin-right: 0;
    }
  }
}

/* 表格区域样式 */
.table-wrapper {
  margin-top: 0;
}

/* 搜索表单样式 */
.tech-search-form {
  ::v-deep .el-form-item {
    margin-bottom: 0;
    margin-right: 20px;
  }
  ::v-deep .el-input__inner {
    width: 200px;
  }
}

/* 图片预览样式 */
.image-preview {
  img {
    transition: transform 0.3s;
    
    &:hover {
      transform: scale(1.5);
    }
  }
}

.no-image {
  color: #909399;
  font-size: 12px;
}

/* 表格样式 */
.tech-table {
  ::v-deep .el-table__body tr:hover > td {
    background: rgba(#5c7cfa, 0.08) !important;
  }
  
  ::v-deep .el-button--mini {
    padding: 7px 12px;
    margin: 2px;
  }
}

/* 分页样式 */
.tech-pagination {
  margin-top: 20px;
}

/* 响应式设计 - 平板设备 */
@media screen and (max-width: 1200px) {
  .search-wrapper {
    margin-bottom: 15px;
  }
  
  .action-wrapper {
    margin-bottom: 15px;
  }
  
  .tech-search-form {
    ::v-deep .el-input__inner {
      width: 150px;
    }
  }
}

/* 响应式设计 - 手机设备 */
@media screen and (max-width: 768px) {
  .page-header {
    margin-bottom: 16px;
  }
  
  .role-tip {
    margin-bottom: 15px;
  }
  
  .search-wrapper {
    margin-bottom: 10px;
  }
  
  .action-wrapper {
    margin-bottom: 10px;
    
    .el-button {
      margin-right: 5px;
      margin-bottom: 5px;
    }
  }
  
  .tech-search-form {
    ::v-deep .el-form-item {
      margin-right: 10px;
    }
    
    ::v-deep .el-input__inner {
      width: 120px;
    }
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
  .tech-search-form {
    ::v-deep .el-input__inner {
      width: 100px;
    }
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
