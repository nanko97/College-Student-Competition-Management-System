<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">我的缴费</h2>
      <p class="page-subtitle">My Payment Records</p>
    </div>

    <!-- 提示信息 -->
    <div class="role-tip">
      <i class="el-icon-info"></i>
      <span>提示：此处仅显示您的个人缴费记录及审核状态</span>
    </div>

    <div class="search-wrapper">
      <el-form :inline="true" :model="searchForm" class="tech-search-form">
        <el-row :gutter="20" class="slt">
          <el-form-item label="竞赛名称">
            <el-input v-model="searchForm.jingsaimingcheng" placeholder="竞赛名称" clearable></el-input>
          </el-form-item>
          <el-form-item label="缴费状态">
            <el-select v-model="searchForm.jiaofeiZhuangtai" placeholder="请选择" clearable>
              <el-option label="已缴费" value="已缴费"></el-option>
              <el-option label="已通过" value="已通过"></el-option>
              <el-option label="已驳回" value="已驳回"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="success" @click="search()">查询</el-button>
          </el-form-item>
        </el-row>
      </el-form>
    </div>
    <div class="table-wrapper">
      <el-table :data="dataList" v-loading="dataListLoading" border stripe class="tech-table">
        <el-table-column label="索引" type="index" width="50" />
        <el-table-column prop="jingsaimingcheng" header-align="center" align="center" label="竞赛名称" show-overflow-tooltip></el-table-column>
        <el-table-column prop="baomingfei" header-align="center" align="center" label="应缴金额(元)" width="120"></el-table-column>
        <el-table-column prop="jiaofeiJiezhiRiqi" header-align="center" align="center" label="截止日期" width="110"></el-table-column>
        <el-table-column prop="jiaofeiZhuangtai" header-align="center" align="center" label="缴费状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getZhuangtaiType(scope.row.jiaofeiZhuangtai)">{{ scope.row.jiaofeiZhuangtai }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="jiaofeiShijian" header-align="center" align="center" label="缴费时间" width="160"></el-table-column>
        <el-table-column prop="shenheYijian" header-align="center" align="center" label="审核意见" show-overflow-tooltip></el-table-column>
        <el-table-column fixed="right" header-align="center" align="center" width="150" label="操作">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="uploadHandler(scope.row)" v-if="scope.row.jiaofeiZhuangtai === '未缴费' || scope.row.jiaofeiZhuangtai === '已驳回'">{{ scope.row.jiaofeiZhuangtai === '已驳回' ? '重新上传' : '上传凭证' }}</el-button>
            <el-button type="text" size="small" @click="viewPingzheng(scope.row)" v-if="scope.row.jiaofeiZhuangtai !== '未缴费' && scope.row.jiaofeiZhuangtai !== '已驳回'">查看凭证</el-button>
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
    <upload-dialog v-if="uploadVisible" ref="uploadDialog" @refreshDataList="getDataList" @close="uploadVisible = false"></upload-dialog>
    <pingzheng-viewer v-if="viewerVisible" ref="pingzhengViewer" @close="viewerVisible = false"></pingzheng-viewer>
  </div>
</template>

<script>
import UploadDialog from './jiaofei-upload-dialog'
import PingzhengViewer from './pingzheng-viewer'
export default {
  data() {
    return {
      searchForm: {
        jingsaimingcheng: '',
        jiaofeiZhuangtai: ''
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      uploadVisible: false,
      viewerVisible: false
    }
  },
  components: {
    UploadDialog,
    PingzhengViewer
  },
  mounted() {
    this.getDataList()
  },
  activated() {
    this.getDataList()
  },
  methods: {
    getZhuangtaiType(zhuangtai) {
      if (zhuangtai === '已通过') return 'success'
      if (zhuangtai === '已驳回') return 'danger'
      if (zhuangtai === '待审核') return 'warning'
      return 'info'
    },
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: 'jingsai/jiaofei/my/list',
        method: 'get',
        params: {
          'page': this.pageIndex,
          'limit': this.pageSize,
          'jingsaimingcheng': this.searchForm.jingsaimingcheng,
          'jiaofeiZhuangtai': this.searchForm.jiaofeiZhuangtai
        }
      }).then(({data}) => {
        console.log('缴费记录返回数据:', data)
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
          console.log('缴费记录列表:', this.dataList, '总数:', this.totalPage)
        } else {
          console.error('获取缴费记录失败:', data.msg)
          this.dataList = []
          this.totalPage = 0
          this.$message.error(data.msg || '获取缴费记录失败')
        }
        this.dataListLoading = false
      }).catch(err => {
        console.error('获取缴费记录失败:', err)
        this.dataList = []
        this.totalPage = 0
        this.dataListLoading = false
        this.$message.error('获取缴费记录失败：' + (err.message || '请检查后端服务'))
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
      console.log('点击上传 - 缴费记录完整数据:', JSON.stringify(row, null, 2))
      this.uploadVisible = true
      this.$nextTick(() => {
        this.$refs.uploadDialog.init(row)
      })
    },
    viewPingzheng(row) {
      this.viewerVisible = true
      this.$nextTick(() => {
        this.$refs.pingzhengViewer.init(row)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/css/tech-theme.scss';
</style>
