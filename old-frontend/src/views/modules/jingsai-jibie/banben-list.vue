<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">级别版本管理</h2>
      <p class="page-subtitle">Competition Level Version Management</p>
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
      showFlag: true
    }
  },
  components: {
    AddOrUpdate
  },
  created() {
    this.getDataList()
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
                this.getDataList()
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
</style>
