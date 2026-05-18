<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">人员变更申请</h2>
      <p class="page-subtitle">Personnel Change Application</p>
    </div>

    <!-- 列表页 -->
    <div v-if="showFlag">
      <!-- 搜索区域 -->
      <div class="search-wrapper">
        <el-form :inline="true" :model="searchForm" class="tech-search-form">
          <el-row :gutter="20" class="search-row">
            <el-form-item label="团队编号">
              <el-input 
                v-model="searchForm.tuanduiBianhao" 
                placeholder="请输入团队编号" 
                clearable
                prefix-icon="el-icon-s-order"
              ></el-input>
            </el-form-item>
            <el-form-item label="变更类型">
              <el-select v-model="searchForm.bianguengLeixing" placeholder="请选择" clearable style="width: 150px;">
                <el-option label="添加成员" value="添加成员"></el-option>
                <el-option label="移除成员" value="移除成员"></el-option>
                <el-option label="更换负责人" value="更换负责人"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="审核状态">
              <el-select v-model="searchForm.shenheZhuangtai" placeholder="请选择" clearable style="width: 150px;">
                <el-option label="待审核" value="待审核"></el-option>
                <el-option label="已通过" value="已通过"></el-option>
                <el-option label="已驳回" value="已驳回"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" @click="search()">查询</el-button>
            </el-form-item>
          </el-row>
        </el-form>
      </div>

      <!-- 操作按钮区域 -->
      <div class="action-wrapper" v-if="isStudent">
        <el-button type="success" icon="el-icon-plus" @click="addHandler()">申请变更</el-button>
      </div>

      <!-- 数据表格 -->
      <div class="table-wrapper">
        <el-table 
          class="tech-table"
          :data="dataList" 
          v-loading="dataListLoading" 
          border 
          stripe>
          <el-table-column label="索引" type="index" width="50" />
          <el-table-column prop="tuanduiBianhao" header-align="center" align="center" label="团队编号" width="130">
            <template slot-scope="scope">
              <el-tag size="small" type="info">{{ scope.row.tuanduiBianhao }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="bianguengLeixing" header-align="center" align="center" label="变更类型" width="120">
            <template slot-scope="scope">
              <el-tag :type="getLeixingType(scope.row.bianguengLeixing)" size="small">{{ scope.row.bianguengLeixing }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="caozuoRenXingming" header-align="center" align="center" label="操作人" width="100"></el-table-column>
          <el-table-column prop="caozuoYuanyin" header-align="center" align="center" label="变更原因" show-overflow-tooltip></el-table-column>
          <el-table-column prop="shenheZhuangtai" header-align="center" align="center" label="审核状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getZhuangtaiType(scope.row.shenheZhuangtai)" size="small">{{ scope.row.shenheZhuangtai }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="shenheRen" header-align="center" align="center" label="审核人" width="100"></el-table-column>
          <el-table-column prop="shenheShijian" header-align="center" align="center" label="审核时间" width="160"></el-table-column>
          <el-table-column fixed="right" header-align="center" align="center" width="150" label="操作">
            <template slot-scope="scope">
              <el-button type="primary" icon="el-icon-view" size="mini" @click="viewDetail(scope.row)">详情</el-button>
              <el-button v-if="scope.row.shenheZhuangtai === '待审核'" type="danger" icon="el-icon-close" size="mini" @click="deleteHandler(scope.row.id)">撤销</el-button>
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
    <apply-dialog v-if="applyVisible" ref="applyDialog" @refreshDataList="getDataList" @close="applyVisible = false"></apply-dialog>
    <detail-dialog v-if="detailVisible" ref="detailDialog" @close="detailVisible = false"></detail-dialog>
  </div>
</template>

<script>
import ApplyDialog from './apply-dialog'
import DetailDialog from './detail-dialog'
export default {
  data() {
    return {
      searchForm: {
        tuanduiBianhao: '',
        bianguengLeixing: '',
        shenheZhuangtai: ''
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      showFlag: true,
      applyVisible: false,
      detailVisible: false
    }
  },
  components: {
    ApplyDialog,
    DetailDialog
  },
  computed: {
    // 判断是否为学生角色
    isStudent() {
      const tableName = this.$storage.get('sessionTable')
      return tableName === 'xuesheng'
    },
    // 判断是否为教师角色
    isTeacher() {
      const tableName = this.$storage.get('sessionTable')
      return tableName === 'jiaoshi'
    }
  },
  created() {
    this.getDataList()
  },
  activated() {
    this.getDataList()
  },
  methods: {
    getLeixingType(leixing) {
      if (leixing === '添加成员') return 'success'
      if (leixing === '移除成员') return 'warning'
      return 'danger'
    },
    getZhuangtaiType(zhuangtai) {
      if (zhuangtai === '已通过') return 'success'
      if (zhuangtai === '已驳回') return 'danger'
      return 'warning'
    },
    getDataList() {
      this.dataListLoading = true
      
      // 构建查询参数
      let params = {
        'page': this.pageIndex,
        'limit': this.pageSize,
        'tuanduiBianhao': this.searchForm.tuanduiBianhao,
        'bianguengLeixing': this.searchForm.bianguengLeixing,
        'shenheZhuangtai': this.searchForm.shenheZhuangtai
      }
      
      // 学生只能查看自己的申请
      if (this.isStudent) {
        const xuehao = this.$storage.get('username')
        if (xuehao) {
          params['caozuoRenXuehao'] = xuehao
        }
      }
      
      // 教师查看所有申请
      this.$http({
        url: 'jingsai/biangueng/page',
        method: 'get',
        params: params
      }).then(({data}) => {
        console.log('人员变更 API 响应:', data)
        if (data && data.code === 0) {
          this.dataList = data.page?.list || []
          this.totalPage = data.page?.totalCount || 0
          console.log('人员变更数据:', this.dataList, '总数:', this.totalPage)
        } else {
          console.error('查询失败:', data.msg)
          this.dataList = []
          this.totalPage = 0
          this.$message.error(data.msg || '查询失败')
        }
        this.dataListLoading = false
      }).catch(err => {
        console.error('变更申请查询异常:', err)
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
    sizeChangeHandle(val) {
      this.pageSize = val
      this.pageIndex = 1
      this.getDataList()
    },
    currentChangeHandle(val) {
      this.pageIndex = val
      this.getDataList()
    },
    addHandler() {
      this.applyVisible = true
      this.$nextTick(() => {
        this.$refs.applyDialog.init()
      })
    },
    viewDetail(row) {
      this.detailVisible = true
      this.$nextTick(() => {
        this.$refs.detailDialog.init(row)
      })
    },
    deleteHandler(id) {
      this.$confirm('确定撤销该变更申请?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: 'jingsai/biangueng/delete',
          method: 'post',
          data: [id]
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.$message.success('操作成功')
            this.getDataList()
          } else {
            this.$message.error(data.msg)
          }
        }).catch(err => {
          console.error('删除失败:', err)
          this.$message.error('操作失败')
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
    margin-bottom: 10px;
    margin-right: 24px;
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
