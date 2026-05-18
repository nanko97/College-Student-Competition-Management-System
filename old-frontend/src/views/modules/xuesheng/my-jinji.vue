<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">我的晋级信息</h2>
      <p class="page-subtitle">My Promotion Records</p>
    </div>
    
    <!-- 提示信息 -->
    <div class="role-tip">
      <i class="el-icon-info"></i>
      <span>提示：晋级由系统根据竞赛成绩和排名自动触发，或由教师审核后通过</span>
    </div>
    
    <el-form :inline="true" :model="searchForm" class="search-wrapper tech-search-form">
      <el-row :gutter="20" class="slt">
        <el-form-item label="竞赛名称">
          <el-input v-model="searchForm.jingsaimingcheng" placeholder="竞赛名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="searchForm.jinjiZhuangtai" placeholder="请选择" clearable>
            <el-option label="待审核" value="待审核"></el-option>
            <el-option label="已通过" value="已通过"></el-option>
            <el-option label="已驳回" value="已驳回"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="search()">查询</el-button>
        </el-form-item>
      </el-row>
    </el-form>
    <div class="table-wrapper">
      <el-table class="tech-table" :data="dataList" v-loading="dataListLoading" border stripe>
        <el-table-column label="索引" type="index" width="50" />
        <el-table-column prop="yuanJingsaimingcheng" header-align="center" align="center" label="原竞赛" show-overflow-tooltip></el-table-column>
        <el-table-column prop="xinJingsaimingcheng" header-align="center" align="center" label="目标竞赛" show-overflow-tooltip></el-table-column>
        <el-table-column prop="jinjiJibie" header-align="center" align="center" label="晋级级别" width="100">
          <template slot-scope="scope">
            <el-tag :type="getJibieType(scope.row.jinjiJibie)">{{ scope.row.jinjiJibie }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="jinjiZhuangtai" header-align="center" align="center" label="审核状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getZhuangtaiType(scope.row.jinjiZhuangtai)">{{ scope.row.jinjiZhuangtai }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="jinjiYuanyin" header-align="center" align="center" label="晋级原因" show-overflow-tooltip></el-table-column>
        <el-table-column prop="shenheShijian" header-align="center" align="center" label="审核时间" width="160"></el-table-column>
        <el-table-column fixed="right" header-align="center" align="center" width="150" label="审核时间">
          <template slot-scope="scope">
            <span>{{ scope.row.shenheShijian || '-' }}</span>
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
        layout="total, sizes, prev, pager, next, jumper">
      </el-pagination>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      searchForm: {
        jingsaimingcheng: '',
        jinjiZhuangtai: ''
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false
    }
  },
  mounted() {
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
    getZhuangtaiType(zhuangtai) {
      if (zhuangtai === '已通过') return 'success'
      if (zhuangtai === '已驳回') return 'danger'
      return 'warning'
    },
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: 'jingsai/jinji/my/list',
        method: 'get',
        params: {
          'page': this.pageIndex,
          'limit': this.pageSize,
          'jingsaimingcheng': this.searchForm.jingsaimingcheng,
          'jinjiZhuangtai': this.searchForm.jinjiZhuangtai
        }
      }).then(({data}) => {
        console.log('晋级记录返回数据:', data)
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
          console.log('晋级记录列表:', this.dataList, '总数:', this.totalPage)
        } else {
          console.error('获取晋级记录失败:', data.msg)
          this.dataList = []
          this.totalPage = 0
          this.$message.error(data.msg || '获取晋级记录失败')
        }
        this.dataListLoading = false
      }).catch(err => {
        console.error('获取晋级记录失败:', err)
        this.dataList = []
        this.totalPage = 0
        this.dataListLoading = false
        this.$message.error('获取晋级记录失败：' + (err.message || '请检查后端服务'))
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
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/css/tech-theme.scss';

.role-tip {
