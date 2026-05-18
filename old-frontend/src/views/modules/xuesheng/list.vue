<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">学生管理</h2>
      <p class="page-subtitle">Student Management System</p>
    </div>

    <!-- 列表页 -->
    <div v-if="showFlag">
      <!-- 搜索区域 -->
      <div class="search-wrapper">
        <el-form :inline="true" :model="searchForm" class="tech-search-form">
          <el-row :gutter="20" class="search-row">
            <el-form-item label="学号">
              <el-input 
                v-model="searchForm.xuehao" 
                placeholder="请输入学号" 
                clearable
                prefix-icon="el-icon-user"
              ></el-input>
            </el-form-item>
            <el-form-item label="学生姓名">
              <el-input 
                v-model="searchForm.xueshengxingming" 
                placeholder="请输入姓名" 
                clearable
                prefix-icon="el-icon-s-custom"
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
          v-if="isAuth('xuesheng','新增')"
          type="success"
          icon="el-icon-plus"
          @click="addOrUpdateHandler()"
        >新增学生</el-button>
        <el-button
          v-if="isAuth('xuesheng','删除') && contents.tableSelection"
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
          v-if="isAuth('xuesheng','查看')">
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
                prop="xingbie"
                header-align="center"
                label="性别">
              <template slot-scope="scope">
                <el-tag :type="scope.row.xingbie === '男' ? 'primary' : 'danger'" size="small">
                  {{scope.row.xingbie}}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
                prop="xueyuanmingcheng"
                header-align="center"
                label="学院名称">
              <template slot-scope="scope">
                {{scope.row.xueyuanmingcheng}}
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
                prop="banji"
                header-align="center"
                label="班级">
              <template slot-scope="scope">
                {{scope.row.banji}}
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
                prop="shouji"
                header-align="center"
                label="手机号码">
              <template slot-scope="scope">
                {{scope.row.shouji}}
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
                prop="youxiang"
                header-align="center"
                label="邮箱">
              <template slot-scope="scope">
                {{scope.row.youxiang}}
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign" 
                prop="zhaopian"
                header-align="center"
                width="200"
                label="照片">
              <template slot-scope="scope">
                <div v-if="scope.row.zhaopian" class="image-preview">
                  <img :src="$imageUrl(scope.row.zhaopian.split(',')[0])" width="60" height="60" style="border-radius: 8px;" @error="handleImageError">
                </div>
                <div v-else class="no-image">无图片</div>
              </template>
            </el-table-column>
            <el-table-column width="300" :align="contents.tableAlign"
                header-align="center"
                label="操作">
                <template slot-scope="scope">
                  <el-button v-if="isAuth('xuesheng','查看')" 
                    type="primary" icon="el-icon-view" size="mini" 
                    @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
                  <el-button v-if="isAuth('xuesheng','修改')" 
                    type="success" icon="el-icon-edit" size="mini" 
                    @click="addOrUpdateHandler(scope.row.id)">修改</el-button>
                  <el-button v-if="isAuth('xuesheng','删除')" 
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
      contents:{"searchBtnFontColor":"#333","pagePosition":"1","inputFontSize":"14px","inputBorderRadius":"16px","tableBtnDelFontColor":"#333","tableBtnIconPosition":"2","searchBtnHeight":"40px","inputIconColor":"#C0C4CC","searchBtnBorderRadius":"4px","tableStripe":false,"btnAdAllWarnFontColor":"#333","tableBtnDelBgColor":"rgba(255, 140, 0, 1)","searchBtnIcon":"1","tableSize":"small","searchBtnBorderStyle":"solid","tableSelection":true,"searchBtnBorderWidth":"1px","tableContentFontSize":"14px","searchBtnBgColor":"#fff","inputTitleSize":"14px","btnAdAllBorderColor":"rgba(31, 147, 255, 0.73)","pageJumper":true,"btnAdAllIconPosition":"1","searchBoxPosition":"2","tableBtnDetailFontColor":"#333","tableBtnHeight":"40px","pagePager":true,"searchBtnBorderColor":"#DCDFE6","tableHeaderFontColor":"rgba(12, 12, 13, 0.67)","inputTitle":"1","tableBtnBorderRadius":"16px","btnAdAllFont":"1","btnAdAllDelFontColor":"#333","tableBtnIcon":"1","btnAdAllHeight":"40px","btnAdAllWarnBgColor":"rgba(190, 163, 18, 1)","btnAdAllBorderWidth":"1px","tableStripeFontColor":"#606266","tableBtnBorderStyle":"solid","inputHeight":"40px","btnAdAllBorderRadius":"16px","btnAdAllDelBgColor":"rgba(255, 215, 0, 1)","pagePrevNext":true,"btnAdAllAddBgColor":"rgba(241, 215, 70, 1)","searchBtnFont":"1","tableIndex":true,"btnAdAllIcon":"1","tableSortable":true,"pageSizes":true,"tableFit":true,"pageBtnBG":true,"searchBtnFontSize":"14px","tableBtnEditBgColor":"rgba(255, 140, 0, 1)","inputBorderWidth":"1px","inputFontPosition":"1","inputFontColor":"rgba(82, 135, 234, 1)","pageEachNum":10,"tableHeaderBgColor":"rgba(227, 226, 226, 0.14)","inputTitleColor":"#333","btnAdAllBoxPosition":"1","tableBtnDetailBgColor":"rgba(255, 140, 0, 1)","inputIcon":"1","searchBtnIconPosition":"1","btnAdAllFontSize":"14px","inputBorderStyle":"solid","inputBgColor":"rgba(224, 220, 220, 0.15)","pageStyle":true,"pageTotal":true,"btnAdAllAddFontColor":"#333","tableBtnFont":"1","tableContentFontColor":"rgba(78, 79, 82, 1)","inputBorderColor":"#dcdfe6","tableBtnEditFontColor":"#333","tableShowHeader":true,"tableBorder":false,"btnAdAllBorderStyle":"solid"},
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
        document.querySelectorAll('.tech-search-form .el-input__inner').forEach(el=>{
          el.style.textAlign = 'left'
          el.style.height = '45px'
          el.style.lineHeight = '45px'
        })
      })
    },
    contentSearchBtnStyleChange() {},
    contentBtnAdAllStyleChange() {},
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
    contentTableBtnStyleChange(){},
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
    getDataList() {
      this.dataListLoading = true;
      let params = {
        page: this.pageIndex,
        limit: this.pageSize,
        sort: 'id',
      }
      if(this.searchForm.xuehao!='' && this.searchForm.xuehao!=undefined){
        params['xuehao'] = '%' + this.searchForm.xuehao + '%'
      }
      if(this.searchForm.xueshengxingming!='' && this.searchForm.xueshengxingming!=undefined){
        params['xueshengxingming'] = '%' + this.searchForm.xueshengxingming + '%'
      }
      this.$http({
        url: "xuesheng/page",
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
    // 图片加载错误处理
    handleImageError(e) {
      // 如果已经是默认图片仍然失败，则隐藏图片
      if (e.target.src.includes('upload/人像.png')) {
        e.target.style.display = 'none';
      } else {
        // 尝试加载默认图片
        e.target.src = this.$imageUrl('upload/人像.png');
      }
    },
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
    download(file){
      window.open(`${file}`)
    },
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
          url: "xuesheng/delete",
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

.action-wrapper {
  margin-bottom: 20px;
  
  .el-button {
    margin-right: 10px;
  }
}

.tech-search-form {
  ::v-deep .el-form-item {
    margin-bottom: 0;
    margin-right: 20px;
  }
  
  ::v-deep .el-input__inner {
    width: 200px;
  }
}

.image-preview {
  display: flex;
  justify-content: center;
  align-items: center;
  
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

.tech-table {
  ::v-deep .el-table__body tr:hover > td {
    background: rgba(#5c7cfa, 0.08) !important;
  }
  
  ::v-deep .el-button--mini {
    padding: 7px 12px;
    margin: 2px;
  }
}

.tech-pagination {
  margin-top: 20px;
}
</style>
