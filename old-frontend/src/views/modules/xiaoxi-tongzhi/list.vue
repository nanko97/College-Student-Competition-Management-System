<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">消息中心</h2>
      <p class="page-subtitle">Message Center</p>
    </div>

    <!-- 统计卡片 -->
    <div class="statistics-wrapper">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="stat-card stat-purple">
            <div class="stat-icon"><i class="el-icon-message"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.total }}</div>
              <div class="stat-label">总消息数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card stat-pink">
            <div class="stat-icon"><i class="el-icon-bell"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.unread }}</div>
              <div class="stat-label">未读消息</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card stat-blue">
            <div class="stat-icon"><i class="el-icon-circle-check"></i></div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.read }}</div>
              <div class="stat-label">已读消息</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 提示信息 -->
    <div class="role-tip">
      <i class="el-icon-info"></i>
      <span>提示：此处显示所有系统通知、审核结果、成绩通知等业务消息</span>
    </div>

    <!-- 搜索和操作区域 -->
    <div class="search-wrapper">
      <el-form :inline="true" :model="searchForm" class="tech-search-form">
        <el-row :gutter="20" class="search-row">
          <el-form-item label="消息标题">
            <el-input
              v-model="searchForm.biaoti"
              placeholder="请输入标题关键词"
              clearable
              prefix-icon="el-icon-search"
              style="width: 200px;"
            ></el-input>
          </el-form-item>
          <el-form-item label="消息类型">
            <el-select v-model="searchForm.leixing" placeholder="请选择类型" clearable style="width: 150px;">
              <el-option label="报名申请" value="报名申请"></el-option>
              <el-option label="审核结果" value="审核结果"></el-option>
              <el-option label="缴费审核" value="缴费审核"></el-option>
              <el-option label="复核申请" value="复核申请"></el-option>
              <el-option label="复核结果" value="复核结果"></el-option>
              <el-option label="晋级结果" value="晋级结果"></el-option>
              <el-option label="团队审核" value="团队审核"></el-option>
              <el-option label="团队申请" value="团队申请"></el-option>
              <el-option label="人员变更" value="人员变更"></el-option>
              <el-option label="作品提交" value="作品提交"></el-option>
              <el-option label="成绩通知" value="成绩通知"></el-option>
              <el-option label="系统通知" value="系统通知"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="阅读状态">
            <el-select v-model="searchForm.isRead" placeholder="请选择状态" clearable style="width: 120px;">
              <el-option label="未读" value="未读"></el-option>
              <el-option label="已读" value="已读"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="search()">查询</el-button>
            <el-button type="success" icon="el-icon-refresh" @click="resetSearch()">重置</el-button>
            <el-button type="warning" icon="el-icon-select" @click="markAllAsRead()" :disabled="statistics.unread === 0">全部已读</el-button>
            <el-button type="danger" icon="el-icon-delete" @click="batchDelete()" :disabled="selectedIds.length === 0">批量删除</el-button>
          </el-form-item>
        </el-row>
      </el-form>
    </div>

    <!-- 消息列表 -->
    <div class="table-wrapper">
      <el-table
        class="tech-table"
        :data="dataList"
        v-loading="dataListLoading"
        border
        stripe
        @selection-change="handleSelectionChange"
        style="width: 100%">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column label="序号" type="index" width="60" align="center"></el-table-column>
        <el-table-column prop="biaoti" label="消息标题" min-width="200" show-overflow-tooltip>
          <template slot-scope="scope">
            <span :style="{ fontWeight: scope.row.isRead === '未读' ? 'bold' : 'normal', color: scope.row.isRead === '未读' ? '#303133' : '#909399' }">
              <i v-if="scope.row.isRead === '未读'" class="el-icon-dot" style="color: #f56c6c; margin-right: 5px;"></i>
              {{ scope.row.biaoti }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="leixing" label="消息类型" width="120" align="center">
          <template slot-scope="scope">
            <el-tag :type="getTypeColor(scope.row.leixing)" size="small">{{ scope.row.leixing }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fasongren" label="发送人" width="100" align="center"></el-table-column>
        <el-table-column prop="isRead" label="状态" width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isRead === '已读' ? 'info' : 'danger'" size="small">
              {{ scope.row.isRead }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="addtime" label="接收时间" width="160" align="center"></el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.isRead === '未读'"
              type="primary"
              size="mini"
              icon="el-icon-view"
              @click="viewAndMarkRead(scope.row)">
              查看
            </el-button>
            <el-button
              v-else
              type="info"
              size="mini"
              icon="el-icon-view"
              @click="viewDetail(scope.row)">
              查看
            </el-button>
            <el-button
              type="danger"
              size="mini"
              icon="el-icon-delete"
              @click="deleteMessage(scope.row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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

    <!-- 消息详情对话框 -->
    <el-dialog
      title="消息详情"
      :visible.sync="detailVisible"
      width="600px"
      :close-on-click-modal="false">
      <div class="message-detail">
        <div class="detail-header">
          <h3>{{ currentMessage.biaoti }}</h3>
          <el-tag :type="getTypeColor(currentMessage.leixing)">{{ currentMessage.leixing }}</el-tag>
        </div>
        <div class="detail-meta">
          <span><i class="el-icon-user"></i> 发送人：{{ currentMessage.fasongren }}</span>
          <span><i class="el-icon-time"></i> 时间：{{ currentMessage.addtime }}</span>
        </div>
        <el-divider></el-divider>
        <div class="detail-content">
          {{ currentMessage.neirong }}
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailVisible = false" icon="el-icon-close">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      searchForm: {
        biaoti: '',
        leixing: '',
        isRead: ''
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      selectedIds: [],
      statistics: {
        total: 0,
        unread: 0,
        read: 0
      },
      detailVisible: false,
      currentMessage: {}
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
    // 格式化日期为后端期望的格式: yyyy-MM-dd HH:mm:ss
    formatDate(date) {
      if (!date) return null
      const d = new Date(date)
      const year = d.getFullYear()
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      const hours = String(d.getHours()).padStart(2, '0')
      const minutes = String(d.getMinutes()).padStart(2, '0')
      const seconds = String(d.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    },
    // 获取统计数据
    getStatistics() {
      this.$http({
        url: 'xiaoxitongzhi/statistics',
        method: 'get'
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.statistics.total = data.data.total || 0
          this.statistics.unread = data.data.unread || 0
          this.statistics.read = data.data.read || 0
        }
      })
    },

    // 获取消息列表
    getDataList() {
      this.dataListLoading = true

      const tableName = this.$storage.get('sessionTable')
      const username = this.$storage.get('username')

      let params = {
        page: this.pageIndex,
        limit: this.pageSize,
        sort: 'addtime',
        order: 'desc'
      }

      if (tableName === 'xuesheng') {
        params.jieshourenXuehao = username
      } else if (tableName === 'jiaoshi') {
        params.jieshourenGonghao = username
      }

      // 添加搜索条件
      if (this.searchForm.biaoti) {
        params.biaoti = '%' + this.searchForm.biaoti + '%'
      }
      if (this.searchForm.leixing) {
        params.leixing = this.searchForm.leixing
      }
      if (this.searchForm.isRead) {
        params.isRead = this.searchForm.isRead
      }

      this.$http({
        url: 'xiaoxitongzhi/page',
        method: 'get',
        params: params
      }).then(({ data }) => {
        this.dataListLoading = false
        if (data && data.code === 0) {
          this.dataList = data.data.list || []
          this.totalPage = data.data.total || 0
        }
      }).catch(() => {
        this.dataListLoading = false
      })
    },

    // 搜索
    search() {
      this.pageIndex = 1
      this.getDataList()
    },

    // 重置搜索
    resetSearch() {
      this.searchForm = {
        biaoti: '',
        leixing: '',
        isRead: ''
      }
      this.pageIndex = 1
      this.getDataList()
      this.getStatistics()
    },

    // 获取类型颜色
    getTypeColor(leixing) {
      const colorMap = {
        '报名申请': 'primary',
        '审核结果': 'success',
        '缴费审核': 'warning',
        '复核申请': '',
        '复核结果': 'success',
        '晋级结果': 'success',
        '团队审核': 'primary',
        '团队申请': '',
        '人员变更': '',
        '作品提交': 'warning',
        '成绩通知': 'danger',
        '系统通知': 'info'
      }
      return colorMap[leixing] || 'info'
    },

    // 查看并标记为已读
    viewAndMarkRead(row) {
      this.currentMessage = row
      this.detailVisible = true
      
      console.log('开始标记为已读，消息ID:', row.id)
      
      // 标记为已读
      this.$http({
        url: 'xiaoxitongzhi/update',
        method: 'post',
        data: { id: row.id, isRead: '已读', readTime: this.formatDate(new Date()) }
      }).then(({ data }) => {
        console.log('后端返回数据:', data)
        if (data && data.code === 0) {
          console.log('✓ 标记已读成功，更新界面')
          // 立即更新当前行的状态
          this.$set(row, 'isRead', '已读')
          // 刷新统计信息
          this.getStatistics()
          // 刷新列表数据，确保表格显示最新状态
          this.getDataList()
          this.$message.success('已标记为已读')
        } else {
          console.error('✗ 标记失败，返回数据:', data)
          this.$message.error('标记失败：' + (data.msg || '未知错误'))
        }
      }).catch(error => {
        console.error('✗ 标记已读失败:', error)
        this.$message.error('网络请求失败')
      })
    },

    // 查看详情（已读消息）
    viewDetail(row) {
      this.currentMessage = row
      this.detailVisible = true
    },

    // 删除消息
    deleteMessage(id) {
      this.$confirm('确定删除该消息?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: 'xiaoxitongzhi/delete',
          method: 'post',
          data: [id]
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message.success('删除成功')
            this.getDataList()
            this.getStatistics()
          }
        })
      }).catch(() => {})
    },

    // 多选
    handleSelectionChange(val) {
      this.selectedIds = val.map(item => item.id)
    },

    // 全部已读
    markAllAsRead() {
      this.$confirm('确定将所有未读消息标记为已读?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let unreadIds = this.dataList.filter(item => item.isRead === '未读').map(item => item.id)
        if (unreadIds.length === 0) {
          this.$message.info('没有未读消息')
          return
        }
        let promises = unreadIds.map(id => {
          return this.$http({
            url: 'xiaoxitongzhi/update',
            method: 'post',
            data: { id: id, isRead: '已读', readTime: this.formatDate(new Date()) }
          })
        })
        Promise.all(promises).then(() => {
          this.$message.success('全部标记已读成功')
          this.getDataList()
          this.getStatistics()
        })
      }).catch(() => {})
    },

    // 批量删除
    batchDelete() {
      if (this.selectedIds.length === 0) {
        this.$message.warning('请先选择要删除的消息')
        return
      }
      this.$confirm(`确定删除选中的 ${this.selectedIds.length} 条消息?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: 'xiaoxitongzhi/delete',
          method: 'post',
          data: this.selectedIds
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message.success('批量删除成功')
            this.selectedIds = []
            this.getDataList()
            this.getStatistics()
          }
        })
      }).catch(() => {})
    },

    // 分页
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
@import '@/assets/css/statistics-cards.scss';

.page-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 22px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.page-subtitle {
  font-size: 12px;
  color: #909399;
  margin: 5px 0 0 0;
}

.role-tip {
  background: #e6f7ff;
  border: 1px solid #91d5ff;
  border-radius: 4px;
  padding: 10px 15px;
  margin-bottom: 20px;
  color: #1890ff;
}

.search-wrapper {
  margin-bottom: 20px;
}

.message-detail {
  padding: 10px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.detail-header h3 {
  margin: 0;
  font-size: 18px;
}

.detail-meta {
  color: #909399;
  font-size: 13px;
  margin-bottom: 10px;
}

.detail-meta span {
  margin-right: 20px;
}

.detail-content {
  font-size: 15px;
  line-height: 1.8;
  color: #606266;
  white-space: pre-wrap;
  min-height: 100px;
}

.tech-pagination {
  margin-top: 20px;
  text-align: right;
}
</style>