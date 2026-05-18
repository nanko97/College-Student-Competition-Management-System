<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">团队管理</h2>
      <p class="page-subtitle">Competition Team Management</p>
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
            <el-form-item label="团队名称">
              <el-input 
                v-model="searchForm.tuanduiMingcheng" 
                placeholder="请输入团队名称" 
                clearable
                prefix-icon="el-icon-s-custom"
              ></el-input>
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
      <div class="action-wrapper">
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
          <el-table-column prop="tuanduiBianhao" header-align="center" align="center" label="团队编号" width="130">
            <template slot-scope="scope">
              <el-tag size="small" type="info">{{ scope.row.tuanduiBianhao }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="jingsaimingcheng" header-align="center" align="center" label="竞赛名称" show-overflow-tooltip>
            <template slot-scope="scope">
              <span style="font-weight: 600; color: #5c7cfa;">{{ scope.row.jingsaimingcheng }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="saidaoMingcheng" header-align="center" align="center" label="赛道" width="120"></el-table-column>
          <el-table-column prop="tuanduiMingcheng" header-align="center" align="center" label="团队名称">
            <template slot-scope="scope">
              <span style="color: #ff9800; font-weight: 500;">{{ scope.row.tuanduiMingcheng }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="fuzerenXingming" header-align="center" align="center" label="负责人" width="100"></el-table-column>
          <el-table-column prop="fuzerenXuehao" header-align="center" align="center" label="学号" width="120"></el-table-column>
          <el-table-column prop="chengyuanRenshu" header-align="center" align="center" label="成员数" width="80">
            <template slot-scope="scope">
              <el-tag size="small" type="success">{{ scope.row.chengyuanRenshu }}人</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="shenheZhuangtai" header-align="center" align="center" label="审核状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getZhuangtaiType(scope.row.shenheZhuangtai)" size="small">{{ scope.row.shenheZhuangtai }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column fixed="right" header-align="center" align="center" width="250" label="操作">
            <template slot-scope="scope">
              <!-- 所有用户都可以查看成员 -->
              <el-button type="primary" icon="el-icon-user" size="mini" @click="viewChengyuanHandler(scope.row)">成员</el-button>
              
              <!-- 教师和管理员的审核和删除按钮 -->
              <template v-if="!isStudent">
                <el-button v-if="scope.row.shenheZhuangtai === '待审核'" type="success" icon="el-icon-check" size="mini" @click="shenheHandler(scope.row)">审核</el-button>
                <el-button type="danger" icon="el-icon-delete" size="mini" @click="deleteHandler(scope.row.id)">删除</el-button>
              </template>
              
              <!-- 学生用户的操作按钮 -->
              <template v-else>
                <!-- 团队负责人：可以管理团队 -->
                <template v-if="isTeamLeader && scope.row.id === myTeamId">
                  <el-button type="success" icon="el-icon-edit" size="mini" @click="editTeamHandler(scope.row)">管理</el-button>
                </template>
                
                <!-- 团队成员（非负责人）：可以申请退出 -->
                <template v-else-if="hasTeam && scope.row.id === myTeamId">
                  <el-button type="warning" icon="el-icon-switch-button" size="mini" @click="quitTeamHandler(scope.row)">申请退出</el-button>
                </template>
                
                <!-- 没有团队的学生：可以申请加入 -->
                <template v-else-if="!hasTeam && scope.row.shenheZhuangtai === '已通过'">
                  <el-button type="success" icon="el-icon-circle-plus" size="mini" @click="joinTeamHandler(scope.row)">申请加入</el-button>
                </template>
              </template>
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
    <chengyuan-manage v-if="chengyuanVisible" ref="chengyuanManage" @close="chengyuanVisible = false"></chengyuan-manage>
    <shenhe-dialog v-if="shenheVisible" ref="shenheDialog" @refreshDataList="getDataList" @close="shenheVisible = false"></shenhe-dialog>

    <!-- 申请加入团队对话框 -->
    <el-dialog
      title="申请加入团队"
      :visible.sync="joinDialogVisible"
      width="600px"
      :close-on-click-modal="false">
      <el-form :model="joinForm" label-width="100px">
        <el-form-item label="团队名称">
          <el-input v-model="joinForm.tuanduiMingcheng" readonly></el-input>
        </el-form-item>
        <el-form-item label="竞赛名称">
          <el-input v-model="joinForm.jingsaimingcheng" readonly></el-input>
        </el-form-item>
        <el-form-item label="申请理由" required>
          <el-input 
            type="textarea" 
            v-model="joinForm.applyReason" 
            :rows="4"
            placeholder="请说明您想加入该团队的原因..."
            maxlength="300"
            show-word-limit>
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="joinDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitJoinApply">提交申请</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import ChengyuanManage from './chengyuan-manage'
import ShenheDialog from './shenhe-dialog'
export default {
  data() {
    return {
      searchForm: {
        jingsaimingcheng: '',
        tuanduiMingcheng: '',
        shenheZhuangtai: ''
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      chengyuanVisible: false,
      shenheVisible: false,
      showFlag: true,
      // 当前登录学生信息
      currentUserXuehao: '',
      currentUserName: '',
      isTeamLeader: false, // 是否为团队负责人
      myTeamId: null, // 我所在的团队ID
      hasTeam: false, // 是否已有团队
      // 对话框相关
      joinDialogVisible: false,
      joinForm: {
        tuanduiId: '',
        tuanduiMingcheng: '',
        jingsaimingcheng: '',
        applyReason: ''
      }
    }
  },
  components: {
    ChengyuanManage,
    ShenheDialog
  },
  computed: {
    // 判断是否为学生角色
    isStudent() {
      const tableName = this.$storage.get('sessionTable')
      return tableName === 'xuesheng'
    }
  },
  created() {
    this.getCurrentUser()
    this.getDataList()
  },
  activated() {
    this.getDataList()
  },
  methods: {
    // 获取当前登录用户信息
    getCurrentUser() {
      const tableName = this.$storage.get('sessionTable')
      if (tableName === 'xuesheng') {
        this.currentUserXuehao = this.$storage.get('username') || ''
        this.currentUserName = this.$storage.get('userRealName') || ''
        this.checkMyTeamStatus()
      }
    },
    // 检查我的团队状态
    checkMyTeamStatus() {
      if (!this.currentUserXuehao) return
      
      this.$http({
        url: 'jingsai/tuandui/myStatus',
        method: 'get',
        params: { xuehao: this.currentUserXuehao }
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.isTeamLeader = data.data.isLeader || false
          this.myTeamId = data.data.teamId || null
          this.hasTeam = data.data.hasTeam || false
        }
      }).catch(err => {
        console.error('检查团队状态失败:', err)
      })
    },
    getZhuangtaiType(zhuangtai) {
      if (zhuangtai === '已通过') return 'success'
      if (zhuangtai === '已驳回') return 'danger'
      return 'warning'
    },
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: 'jingsai/tuandui/page',
        method: 'get',
        params: {
          'page': this.pageIndex,
          'limit': this.pageSize,
          'jingsaimingcheng': this.searchForm.jingsaimingcheng,
          'tuanduiMingcheng': this.searchForm.tuanduiMingcheng,
          'shenheZhuangtai': this.searchForm.shenheZhuangtai
        }
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.dataList = data.page?.list || []
          this.totalPage = data.page?.totalCount || 0
        } else {
          this.dataList = []
          this.totalPage = 0
          this.$message.error(data.msg || '查询失败')
        }
        this.dataListLoading = false
      }).catch(err => {
        console.error('团队查询异常:', err)
        this.dataList = []
        this.totalPage = 0
        this.dataListLoading = false
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
    viewChengyuanHandler(row) {
      this.chengyuanVisible = true
      this.$nextTick(() => {
        this.$refs.chengyuanManage.init(row)
      })
    },
    shenheHandler(row) {
      this.shenheVisible = true
      this.$nextTick(() => {
        this.$refs.shenheDialog.init(row)
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
          url: 'jingsai/tuandui/delete',
          method: 'post',
          data: ids
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
    },
    // 管理团队（仅负责人可用）
    editTeamHandler(row) {
      this.$message.info('团队管理功能正在开发中')
      // TODO: 打开团队管理页面，负责人可以修改团队信息、审核成员加入申请等
    },
    // 申请退出团队
    quitTeamHandler(row) {
      this.$confirm('确定要申请退出该团队吗？退出后需要重新申请加入', '提示', {
        confirmButtonText: '确定申请',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: 'jingsai/tuandui/applyQuit',
          method: 'post',
          data: {
            tuanduiId: row.id,
            xuehao: this.currentUserXuehao,
            xueshengxingming: this.currentUserName
          }
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.$message.success('退出申请已提交，请等待负责人审核')
            this.getDataList()
          } else {
            this.$message.error(data.msg || '申请失败')
          }
        }).catch(err => {
          console.error('申请退出失败:', err)
          this.$message.error('申请失败')
        })
      }).catch(() => {})
    },
    // 申请加入团队
    joinTeamHandler(row) {
      this.joinForm = {
        tuanduiId: row.id,
        tuanduiMingcheng: row.tuanduiMingcheng,
        jingsaimingcheng: row.jingsaimingcheng,
        applyReason: ''
      }
      this.joinDialogVisible = true
    },
    // 提交加入申请
    submitJoinApply() {
      if (!this.joinForm.applyReason || this.joinForm.applyReason.trim() === '') {
        this.$message.warning('请填写申请理由')
        return
      }

      this.$confirm('确认提交加入申请吗？', '提示', {
        confirmButtonText: '确认提交',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        this.$http({
          url: 'jingsai/tuandui/applyJoin',
          method: 'post',
          data: {
            tuanduiId: this.joinForm.tuanduiId,
            xuehao: this.currentUserXuehao,
            xueshengxingming: this.currentUserName,
            applyReason: this.joinForm.applyReason
          }
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.$message.success('申请已提交，请等待团队负责人审核')
            this.joinDialogVisible = false
            this.getDataList()
            this.checkMyTeamStatus()
          } else {
            this.$message.error(data.msg || '申请失败')
          }
        }).catch(err => {
          console.error('申请加入失败:', err)
          this.$message.error('申请失败')
        })
      }).catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/css/tech-theme.scss';
@import '@/assets/css/global-responsive-mixin.scss';

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
