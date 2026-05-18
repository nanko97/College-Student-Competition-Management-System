<template>
  <div class="main-content">
    <el-form :inline="true" :model="searchForm" class="form-content">
      <el-row :gutter="20" class="slt">
        <el-form-item label="竞赛名称">
          <el-input v-model="searchForm.jingsaimingcheng" placeholder="竞赛名称" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="search()">查询</el-button>
        </el-form-item>
      </el-row>
    </el-form>

    <!-- 统计信息 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <i class="el-icon-document"></i>
            </div>
            <div class="stat-info">
              <div class="stat-label">已报名竞赛</div>
              <div class="stat-value">{{ statistics.totalBaoming || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <i class="el-icon-upload"></i>
            </div>
            <div class="stat-info">
              <div class="stat-label">已提交作品</div>
              <div class="stat-value">{{ statistics.submittedCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
              <i class="el-icon-warning-outline"></i>
            </div>
            <div class="stat-info">
              <div class="stat-label">未提交作品</div>
              <div class="stat-value">{{ statistics.unsubmittedCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <div class="table-content">
      <el-table :data="dataList" v-loading="dataListLoading" border stripe style="width: 100%">
        <el-table-column label="序号" type="index" width="60" align="center" />
        <el-table-column prop="jingsaimingcheng" header-align="center" align="center" label="竞赛名称" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="jingsaileixing" header-align="center" align="center" label="竞赛类型" width="130"></el-table-column>
        <el-table-column prop="cansaileixing" header-align="center" align="center" label="参赛类型" width="100"></el-table-column>
        <el-table-column prop="sfsh" header-align="center" align="center" label="审核状态" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.sfsh === '通过'" type="success" effect="dark">已通过</el-tag>
            <el-tag v-else-if="scope.row.sfsh === '待审核'" type="warning">待审核</el-tag>
            <el-tag v-else-if="scope.row.sfsh === '不通过'" type="danger">未通过</el-tag>
            <el-tag v-else type="info">{{ scope.row.sfsh || '未知' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="cansaizuopin" header-align="center" align="center" label="作品状态" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.cansaizuopin" type="success" effect="dark">已提交</el-tag>
            <el-tag v-else type="warning">未提交</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="shenqingriqi" header-align="center" align="center" label="报名日期" width="110"></el-table-column>
        <el-table-column fixed="right" header-align="center" align="center" width="220" label="操作">
          <template slot-scope="scope">
            <el-button v-if="scope.row.sfsh === '通过' && !scope.row.cansaizuopin" type="success" size="mini" icon="el-icon-upload2" @click="uploadHandler(scope.row)">提交作品</el-button>
            <el-button v-if="scope.row.sfsh === '通过' && scope.row.cansaizuopin" type="primary" size="mini" icon="el-icon-refresh" @click="updateHandler(scope.row)">更新</el-button>
            <el-button v-if="scope.row.sfsh === '通过' && scope.row.cansaizuopin" type="info" size="mini" icon="el-icon-download" @click="downloadHandler(scope.row)">下载</el-button>
            <el-tag v-if="scope.row.sfsh !== '通过'" type="info" size="mini" effect="dark">{{ scope.row.sfsh === '待审核' ? '等待审核' : '审核未通过' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        @size-change="sizeChangeHandle"
        @current-change="currentChangeHandle"
        :current-page="pageIndex"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        :total="totalPage"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination-content">
      </el-pagination>
    </div>

    <!-- 上传作品对话框 -->
    <el-dialog :title="uploadDialogTitle" :visible.sync="uploadVisible" width="600px" :close-on-click-modal="false" top="10vh">
      <el-form :model="uploadForm" label-width="100px">
        <el-form-item label="竞赛名称">
          <el-input :value="currentBaoming.jingsaimingcheng" disabled placeholder="竞赛名称"></el-input>
        </el-form-item>
        <el-form-item label="竞赛类型">
          <el-input :value="currentBaoming.jingsaileixing" disabled placeholder="竞赛类型"></el-input>
        </el-form-item>
        <el-form-item label="选择文件" required>
          <el-upload
            ref="uploadRef"
            class="upload-demo"
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :file-list="fileList"
            :limit="1"
            :on-exceed="handleExceed"
            :before-remove="beforeRemove">
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <div class="el-upload__tip" slot="tip">
              支持格式：doc、docx、pdf、zip、rar、7z、ppt、pptx、xls、xlsx<br>
              文件大小不超过50MB
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="已选文件" v-if="uploadForm.fileName">
          <el-tag type="success" closable @close="clearFile()">{{ uploadForm.fileName }}</el-tag>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="uploadVisible = false" icon="el-icon-close">取 消</el-button>
        <el-button type="primary" @click="submitUpload()" :loading="uploading" icon="el-icon-upload">{{ uploadDialogTitle === '提交作品' ? '提交' : '更新' }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      searchForm: {
        jingsaimingcheng: ''
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      statistics: {
        totalBaoming: 0,
        submittedCount: 0,
        unsubmittedCount: 0
      },
      uploadVisible: false,
      uploadDialogTitle: '提交作品',
      uploading: false,
      currentBaoming: {},
      fileList: [],
      uploadForm: {
        file: null,
        fileName: ''
      }
    }
  },
  mounted() {
    this.getDataList()
    this.getStatistics()
  },
  activated() {
    this.getDataList()
    this.getStatistics()
  },
  methods: {
    getStatistics() {
      this.$http({
        url: 'zuopin/statistics',
        method: 'get'
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.statistics = data.data || {}
        }
      }).catch((error) => {
        console.error('获取统计数据失败:', error)
      })
    },
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: 'zuopin/my/list',
        method: 'get',
        params: {
          'page': this.pageIndex,
          'limit': this.pageSize,
          'jingsaimingcheng': this.searchForm.jingsaimingcheng
        }
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.dataList = data.page ? data.page.list : []
          this.totalPage = data.page ? data.page.total : 0
        } else {
          this.dataList = []
          this.totalPage = 0
        }
        this.dataListLoading = false
      }).catch((error) => {
        console.error('获取作品列表失败:', error)
        this.dataListLoading = false
      })
    },
    search() {
      this.pageIndex = 1
      this.getDataList()
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
    uploadHandler(row) {
      this.uploadDialogTitle = '提交作品'
      this.currentBaoming = row
      this.clearFile()
      this.uploadVisible = true
    },
    updateHandler(row) {
      this.uploadDialogTitle = '更新作品'
      this.currentBaoming = row
      this.clearFile()
      this.uploadVisible = true
    },
    handleFileChange(file) {
      this.uploadForm.file = file.raw
      this.uploadForm.fileName = file.name
    },
    handleExceed() {
      this.$message.warning('只能上传一个文件，请先移除已选文件')
    },
    clearFile() {
      this.uploadForm.file = null
      this.uploadForm.fileName = ''
      this.fileList = []
      // 清空 el-upload 组件内部的文件列表
      if (this.$refs.uploadRef) {
        this.$refs.uploadRef.clearFiles()
      }
    },
    beforeRemove(file) {
      return this.$confirm(`确定移除 ${file.name}？`).catch(() => {})
    },
    submitUpload() {
      if (!this.uploadForm.file) {
        this.$message.error('请选择作品文件')
        return
      }

      const formData = new FormData()
      formData.append('file', this.uploadForm.file)
      formData.append('baomingId', this.currentBaoming.id)

      this.uploading = true
      this.$http({
        url: 'zuopin/upload',
        method: 'post',
        headers: { 'Content-Type': 'multipart/form-data' },
        data: formData
      }).then(({data}) => {
        this.uploading = false
        if (data && data.code === 0) {
          this.$message({
            message: '上传成功',
            type: 'success',
            duration: 1500,
            onClose: () => {
              this.uploadVisible = false
              this.getDataList()
              this.getStatistics()
            }
          })
        } else {
          this.$message.error(data.msg || '上传失败')
        }
      }).catch(() => {
        this.uploading = false
        this.$message.error('上传失败')
      })
    },
    downloadHandler(row) {
      if (row.cansaizuopin) {
        window.open(this.$imageUrl(row.cansaizuopin))
      }
    }
  }
}
</script>

<style scoped>
.main-content {
  width: 100%;
  min-height: calc(100vh - 60px);
  padding: 20px;
  box-sizing: border-box;
}

.form-content {
  background: transparent;
  padding: 10px;
  margin-bottom: 10px;
}

.table-content {
  background: #fff;
  padding: 15px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
  overflow-x: auto;
}

.pagination-content {
  margin-top: 20px;
  text-align: right;
}

/* 统计卡片样式优化 */
.stat-card {
  transition: all 0.3s;
  height: 100%;
}

.stat-card:hover {
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
}

.stat-icon i {
  font-size: 28px;
  color: #fff;
}

.stat-info {
  flex: 1;
}

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

/* 响应式设计 - 平板设备 */
@media screen and (max-width: 1200px) {
  .main-content {
    padding: 15px;
  }
  
  .stat-card {
    margin-bottom: 15px;
  }
  
  .stat-icon {
    width: 50px;
    height: 50px;
  }
  
  .stat-icon i {
    font-size: 24px;
  }
  
  .stat-value {
    font-size: 24px;
  }
}

/* 响应式设计 - 手机设备 */
@media screen and (max-width: 768px) {
  .main-content {
    padding: 10px;
  }
  
  .form-content {
    padding: 5px;
  }
  
  .table-content {
    padding: 10px;
  }
  
  /* 统计卡片在手机上的布局 */
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
  
  /* 表格在小屏幕上的优化 */
  .el-table {
    font-size: 12px;
  }
  
  .el-table .cell {
    padding-left: 5px;
    padding-right: 5px;
  }
  
  /* 操作按钮在小屏幕上的优化 */
  .el-button--mini {
    padding: 5px 8px;
    font-size: 11px;
  }
  
  /* 分页器在小屏幕上的优化 */
  .pagination-content {
    text-align: center;
  }
  
  .el-pagination {
    justify-content: center;
  }
  
  /* 对话框在小屏幕上的优化 */
  ::v-deep .el-dialog {
    width: 95% !important;
    margin-top: 5vh !important;
  }
  
  ::v-deep .el-dialog__body {
    padding: 15px;
  }
  
  /* 上传组件在小屏幕上的优化 */
  .upload-demo {
    width: 100%;
  }
}

/* 响应式设计 - 超小屏幕设备 */
@media screen and (max-width: 480px) {
  .main-content {
    padding: 5px;
  }
  
  .stat-value {
    font-size: 18px;
  }
  
  .stat-label {
    font-size: 11px;
  }
  
  .el-table {
    font-size: 11px;
  }
  
  /* 隐藏部分列以节省空间 */
  .el-table th.el-table__cell:nth-child(4),
  .el-table td.el-table__cell:nth-child(4) {
    display: none;
  }
}

/* 横向滚动优化 */
@media screen and (max-width: 768px) {
  .table-content {
    -webkit-overflow-scrolling: touch;
  }
}
</style>
