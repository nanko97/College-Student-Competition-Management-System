<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">我的作品</h2>
      <p class="page-subtitle">My Competition Works</p>
    </div>

    <!-- 提示信息 -->
    <div class="role-tip">
      <i class="el-icon-info"></i>
      <span>提示：请确保您的作品文件格式正确，且在截止日期前提交</span>
    </div>

    <div class="search-wrapper">
      <el-form :inline="true" :model="searchForm" class="tech-search-form">
        <el-row :gutter="20" class="slt">
          <el-form-item label="竞赛名称">
            <el-input v-model="searchForm.jingsaimingcheng" placeholder="竞赛名称" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="success" @click="search()">查询</el-button>
          </el-form-item>
        </el-row>
      </el-form>
    </div>

    <!-- 统计信息 -->
    <div class="statistics-wrapper">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="stat-card stat-purple">
            <div class="stat-icon"><i class="el-icon-document"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.totalBaoming || 0 }}</div>
              <div class="stat-label">已报名竞赛</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card stat-blue">
            <div class="stat-icon"><i class="el-icon-upload"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.submittedCount || 0 }}</div>
              <div class="stat-label">已提交作品</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card stat-orange">
            <div class="stat-icon"><i class="el-icon-warning-outline"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.unsubmittedCount || 0 }}</div>
              <div class="stat-label">未提交作品</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="table-wrapper">
      <el-table :data="dataList" v-loading="dataListLoading" border stripe class="tech-table" style="width: 100%">
        <el-table-column label="序号" type="index" width="60" align="center" />
        <el-table-column prop="jingsaimingcheng" header-align="center" align="center" label="竞赛名称" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="jingsaileixing" header-align="center" align="center" label="竞赛类型" width="130"></el-table-column>
        <el-table-column prop="cansaileixing" header-align="center" align="center" label="参赛类型" width="100"></el-table-column>
        <el-table-column prop="sfsh" header-align="center" align="center" label="报名审核" width="100">
          <template slot-scope="scope">
            <el-tag v-if="isSfshPass(scope.row.sfsh)" type="success" effect="dark">已通过</el-tag>
            <el-tag v-else-if="scope.row.sfsh === '待审核' || !scope.row.sfsh" type="warning">待审核</el-tag>
            <el-tag v-else-if="scope.row.sfsh === '否' || scope.row.sfsh === '不通过'" type="danger">未通过</el-tag>
            <el-tag v-else type="info">{{ scope.row.sfsh || '未知' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ispay" header-align="center" align="center" label="缴费状态" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.ispay === '已支付'" type="success" effect="dark">缴费通过</el-tag>
            <el-tag v-else-if="scope.row.ispay === '已缴费'" type="warning">待审核</el-tag>
            <el-tag v-else-if="scope.row.ispay === '已驳回'" type="danger">缴费驳回</el-tag>
            <el-tag v-else-if="scope.row.ispay === '未缴费'" type="info">未缴费</el-tag>
            <el-tag v-else type="info">{{ scope.row.ispay || '未缴费' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="cansaizuopin" header-align="center" align="center" label="作品状态" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.cansaizuopin" type="success" effect="dark">已提交</el-tag>
            <el-tag v-else type="warning">未提交</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="shenqingriqi" header-align="center" align="center" label="报名日期" width="110"></el-table-column>
        <el-table-column fixed="right" header-align="center" align="center" width="280" label="操作">
          <template slot-scope="scope">
            <el-button v-if="isSfshPass(scope.row.sfsh) && scope.row.ispay === '已支付' && !scope.row.cansaizuopin" type="success" size="mini" icon="el-icon-upload2" @click="uploadHandler(scope.row)">提交作品</el-button>
            <el-button v-if="isSfshPass(scope.row.sfsh) && scope.row.ispay === '已支付' && scope.row.cansaizuopin" type="primary" size="mini" icon="el-icon-refresh" @click="updateHandler(scope.row)">更新</el-button>
            <el-button v-if="isSfshPass(scope.row.sfsh) && scope.row.ispay === '已支付' && scope.row.cansaizuopin" type="info" size="mini" icon="el-icon-download" @click="downloadHandler(scope.row)">下载</el-button>
            <el-button v-if="isSfshPass(scope.row.sfsh) && scope.row.ispay === '已支付' && scope.row.cansaizuopin" type="warning" size="mini" icon="el-icon-document" @click="exportPdfHandler(scope.row)">成绩单</el-button>
            <el-tag v-if="isSfshPass(scope.row.sfsh) && scope.row.ispay !== '已支付'" type="warning" size="mini" effect="dark">{{ getIspayTip(scope.row) }}</el-tag>
            <el-tag v-if="!isSfshPass(scope.row.sfsh)" type="info" size="mini" effect="dark">{{ scope.row.sfsh === '待审核' || !scope.row.sfsh ? '等待审核' : '审核未通过' }}</el-tag>
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
        class="tech-pagination">
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
              文件大小不超过50MB，大文件自动分片上传
            </div>
          </el-upload>
        </el-form-item>
        <!-- 【论文5.2.3】分片上传进度条 -->
        <el-form-item label="上传进度" v-if="chunkUploading">
          <el-progress :percentage="chunkProgress" :status="chunkProgressStatus" :stroke-width="20" :text-inside="true"></el-progress>
          <div style="margin-top:8px;color:#909399;font-size:12px">{{ chunkProgressText }}</div>
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
      chunkUploading: false,
      chunkProgress: 0,
      chunkProgressStatus: '',
      chunkProgressText: '',
      CHUNK_SIZE: 5 * 1024 * 1024, // 【论文5.2.3】5MB分片大小
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
    // 报名审核状态兼容判断（数据库中"是"和"通过"都表示审核通过）
    isSfshPass(sfsh) {
      return sfsh === '通过' || sfsh === '是'
    },
    // 缴费状态提示
    getIspayTip(row) {
      if (row.ispay === '已缴费') return '缴费待审核'
      if (row.ispay === '已驳回') return '缴费被驳回'
      if (row.ispay === '未缴费' || !row.ispay) return '未缴费'
      return '未完成缴费'
    },
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

      const file = this.uploadForm.file
      // 【论文5.2.3】大文件（>5MB）使用分片上传，小文件使用普通上传
      if (file.size > this.CHUNK_SIZE) {
        this.chunkUploadFile(file)
      } else {
        this.normalUploadFile(file)
      }
    },
    // 普通上传（小文件）
    normalUploadFile(file) {
      const formData = new FormData()
      formData.append('file', file)
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
    // 【论文5.2.3】分片上传（大文件）
    chunkUploadFile(file) {
      const totalChunks = Math.ceil(file.size / this.CHUNK_SIZE)
      // 生成文件唯一标识（文件名+大小+修改时间的MD5简化版）
      const fileIdentifier = file.name + '_' + file.size + '_' + Date.now()

      this.uploading = true
      this.chunkUploading = true
      this.chunkProgress = 0
      this.chunkProgressStatus = ''
      this.chunkProgressText = '准备分片上传...'

      // 先检查断点续传状态
      this.$http({
        url: 'zuopin/chunkCheck',
        method: 'get',
        params: { fileIdentifier }
      }).then(({data}) => {
        const uploadedChunks = (data && data.code === 0) ? (data.uploadedChunks || []) : []
        this.uploadChunks(file, fileIdentifier, totalChunks, uploadedChunks)
      }).catch(() => {
        // 检查失败时从头开始
        this.uploadChunks(file, fileIdentifier, totalChunks, [])
      })
    },
    // 分片上传执行
    uploadChunks(file, fileIdentifier, totalChunks, uploadedChunks) {
      let completedChunks = uploadedChunks.length
      this.chunkProgress = Math.round((completedChunks / totalChunks) * 100)
      this.chunkProgressText = `分片上传中 ${completedChunks}/${totalChunks}`

      // 逐个上传未完成的分片
      const uploadSequence = []
      for (let i = 0; i < totalChunks; i++) {
        if (!uploadedChunks.includes(i)) {
          uploadSequence.push(i)
        }
      }

      let currentIndex = 0
      const uploadNext = () => {
        if (currentIndex >= uploadSequence.length) {
          // 所有分片已传完，执行合并
          this.mergeChunks(fileIdentifier, totalChunks, file.name, file.size)
          return
        }

        const chunkIndex = uploadSequence[currentIndex]
        const start = chunkIndex * this.CHUNK_SIZE
        const end = Math.min(start + this.CHUNK_SIZE, file.size)
        const chunkBlob = file.slice(start, end)

        const formData = new FormData()
        formData.append('file', chunkBlob)
        formData.append('fileIdentifier', fileIdentifier)
        formData.append('chunkIndex', chunkIndex)
        formData.append('totalChunks', totalChunks)
        formData.append('originalFilename', file.name)
        formData.append('totalSize', file.size)

        this.$http({
          url: 'zuopin/chunkUpload',
          method: 'post',
          headers: { 'Content-Type': 'multipart/form-data' },
          data: formData,
          timeout: 60000 // 单个分片60秒超时
        }).then(({data}) => {
          currentIndex++
          completedChunks++
          this.chunkProgress = Math.round((completedChunks / totalChunks) * 100)
          this.chunkProgressText = `分片上传中 ${completedChunks}/${totalChunks}`

          if (data && data.completed) {
            // 服务端已确认全部接收，直接合并
            this.mergeChunks(fileIdentifier, totalChunks, file.name, file.size)
          } else {
            // 继续上传下一个分片
            uploadNext()
          }
        }).catch((err) => {
          this.uploading = false
          this.chunkUploading = false
          this.chunkProgressStatus = 'exception'
          this.chunkProgressText = '分片上传失败，请重试'
          this.$message.error('分片上传失败')
        })
      }

      uploadNext()
    },
    // 合合分片
    mergeChunks(fileIdentifier, totalChunks, originalFilename, totalSize) {
      this.chunkProgressText = '合并文件中...'
      this.chunkProgress = 99
      this.chunkProgressStatus = ''

      this.$http({
        url: 'zuopin/chunkMerge',
        method: 'post',
        data: {
          fileIdentifier,
          baomingId: this.currentBaoming.id
        }
      }).then(({data}) => {
        this.uploading = false
        this.chunkUploading = false
        if (data && data.code === 0) {
          this.chunkProgress = 100
          this.chunkProgressStatus = 'success'
          this.chunkProgressText = '上传完成！'
          this.$message({
            message: '作品上传成功',
            type: 'success',
            duration: 1500,
            onClose: () => {
              this.uploadVisible = false
              this.getDataList()
              this.getStatistics()
            }
          })
        } else {
          this.chunkProgressStatus = 'exception'
          this.chunkProgressText = '合并失败'
          this.$message.error(data.msg || '合并失败')
        }
      }).catch(() => {
        this.uploading = false
        this.chunkUploading = false
        this.chunkProgressStatus = 'exception'
        this.chunkProgressText = '合并失败'
        this.$message.error('合并失败')
      })
    },
    // 【论文3.1.1(5)】成绩单PDF导出
    exportPdfHandler(row) {
      this.$message.info('正在生成PDF，请稍候...')
      // 直接使用window.open，浏览器会自动携带session认证
      const token = this.$storage.get('Token')
      const url = '/BYSJ_Springboot/zuopindafen/exportPdf?jingsaimingcheng=' + encodeURIComponent(row.jingsaimingcheng) + '&Token=' + token
      window.open(url, '_blank')
    },
    downloadHandler(row) {
      if (row.cansaizuopin) {
        window.open(this.$imageUrl(row.cansaizuopin))
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/css/tech-theme.scss';
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

.table-wrapper {
  margin-top: 0;
}



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
  
  .upload-demo {
    width: 100%;
  }
}

/* 响应式设计 - 超小屏幕设备 */
@media screen and (max-width: 480px) {
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
  .table-wrapper {
    -webkit-overflow-scrolling: touch;
  }
}
</style>
