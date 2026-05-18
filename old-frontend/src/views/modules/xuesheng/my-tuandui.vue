<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">我的团队</h2>
      <p class="page-subtitle">查看我参与的团队及申请记录</p>
      <div class="page-actions">
        <el-button type="primary" icon="el-icon-plus" @click="createTeam" v-if="!hasTeam">
          创建团队
        </el-button>
        <el-button type="success" icon="el-icon-circle-plus" @click="joinTeam" v-if="!hasTeam">
          申请加入
        </el-button>
      </div>
    </div>

    <!-- 没有团队时的提示 -->
    <el-alert
      v-if="!hasTeam && myTeamList.length === 0"
      title="您还没有加入任何团队"
      type="info"
      :closable="false"
      show-icon
      style="margin-bottom: 20px;">
      <template slot>
        <p>您可以：</p>
        <ul style="margin: 10px 0; padding-left: 20px;">
          <li>点击“创建团队”按钮创建新团队</li>
          <li>点击“申请加入”按钮加入已有团队</li>
        </ul>
      </template>
    </el-alert>

    <!-- 团队详情对话框 -->
    <el-dialog 
      :title="'团队详情 - ' + (currentTeam ? currentTeam.tuanduiMingcheng : '')"
      :visible.sync="teamDetailVisible"
      width="800px"
      @close="closeTeamDetail">
      
      <div v-if="currentTeam" class="team-detail">
        <!-- 团队基本信息 -->
        <el-descriptions :column="2" border>
          <el-descriptions-item label="团队编号">{{ currentTeam.tuanduiBianhao }}</el-descriptions-item>
          <el-descriptions-item label="团队名称">{{ currentTeam.tuanduiMingcheng }}</el-descriptions-item>
          <el-descriptions-item label="竞赛名称">{{ currentTeam.jingsaimingcheng }}</el-descriptions-item>
          <el-descriptions-item label="赛道">{{ currentTeam.saidao || '-' }}</el-descriptions-item>
          <el-descriptions-item label="负责人">{{ currentTeam.fuzerenXingming }}（{{ currentTeam.fuzerenXuehao }}）</el-descriptions-item>
          <el-descriptions-item label="成员数">{{ currentTeam.chengyuanRenshu }}</el-descriptions-item>
          <el-descriptions-item label="审核状态">
            <el-tag :type="getShenheType(currentTeam.shenheZhuangtai)" effect="dark">
              {{ currentTeam.shenheZhuangtai }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentTeam.createTime }}</el-descriptions-item>
          <el-descriptions-item label="团队简介" :span="2">{{ currentTeam.tuanduiJianjie || '-' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 团队成员列表 -->
        <div class="team-members-section">
          <h4 style="margin: 20px 0 15px; color: #495057;">
            <i class="el-icon-user-solid"></i> 团队成员
          </h4>
          <el-table 
            :data="teamMembers"
            v-loading="membersLoading"
            border
            size="small">
            <el-table-column label="序号" type="index" width="60" align="center"></el-table-column>
            <el-table-column prop="xueshengxingming" label="姓名" align="center" width="120"></el-table-column>
            <el-table-column prop="xuehao" label="学号" align="center" width="150"></el-table-column>
            <el-table-column prop="juese" label="角色" align="center" width="120">
              <template slot-scope="scope">
                <el-tag :type="scope.row.juese === '负责人' ? 'danger' : 'primary'" size="small">
                  {{ scope.row.juese }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="zhuanye" label="专业" align="center" min-width="150"></el-table-column>
            <el-table-column prop="banji" label="班级" align="center" width="120"></el-table-column>
            <el-table-column prop="lianxifangshi" label="联系方式" align="center" width="150"></el-table-column>
            <el-table-column label="操作" width="120" align="center" v-if="isCurrentUserLeader">
              <template slot-scope="scope">
                <el-button 
                  type="danger" 
                  icon="el-icon-delete" 
                  size="mini"
                  @click="removeMember(scope.row)"
                  v-if="scope.row.juese !== '负责人'">
                  移除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 负责人管理操作 -->
        <div v-if="isCurrentUserLeader" class="team-management-actions">
          <h4 style="margin: 20px 0 15px; color: #495057;">
            <i class="el-icon-setting"></i> 团队管理
          </h4>
          <el-button type="primary" icon="el-icon-edit" @click="editTeamInfo">
            编辑团队信息
          </el-button>
          <el-button type="warning" icon="el-icon-user-add" @click="showAddMemberDialog">
            添加成员
          </el-button>
          <el-button type="danger" icon="el-icon-delete" @click="dissolveTeam">
            解散团队
          </el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 编辑团队信息对话框 -->
    <el-dialog 
      title="编辑团队信息"
      :visible.sync="editTeamVisible"
      width="600px">
      <el-form :model="editTeamForm" label-width="100px">
        <el-form-item label="团队名称">
          <el-input v-model="editTeamForm.tuanduiMingcheng" placeholder="请输入团队名称"></el-input>
        </el-form-item>
        <el-form-item label="团队简介">
          <el-input 
            v-model="editTeamForm.tuanduiJianjie" 
            type="textarea" 
            :rows="4"
            placeholder="请输入团队简介">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editTeamVisible = false">取消</el-button>
        <el-button type="primary" @click="saveTeamInfo">保存</el-button>
      </span>
    </el-dialog>

    <!-- 添加成员对话框 -->
    <el-dialog 
      title="添加成员"
      :visible.sync="addMemberVisible"
      width="500px">
      <el-form :model="addMemberForm" label-width="100px">
        <el-form-item label="学号">
          <el-input v-model="addMemberForm.xuehao" placeholder="请输入学生学号"></el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="addMemberForm.juese" placeholder="请选择角色" style="width: 100%;">
            <el-option label="普通成员" value="普通成员"></el-option>
            <el-option label="核心成员" value="核心成员"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addMemberVisible = false">取消</el-button>
        <el-button type="primary" @click="addMember">添加</el-button>
      </span>
    </el-dialog>

    <!-- 选项卡 -->
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <!-- 我的团队 -->
      <el-tab-pane label="我的团队" name="myTeam">
        <div class="search-wrapper">
          <el-form :inline="true" :model="searchForm" class="tech-search-form">
            <el-form-item label="团队名称">
              <el-input 
                v-model="searchForm.tuanduiMingcheng" 
                placeholder="请输入团队名称" 
                clearable
                prefix-icon="el-icon-user-solid"
              ></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" @click="getMyTeamList()">查询</el-button>
              <el-button icon="el-icon-refresh" @click="resetSearch()">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <el-table 
          class="tech-table"
          :data="myTeamList"
          v-loading="dataListLoading"
          border>
          
          <el-table-column label="序号" type="index" width="60" align="center"></el-table-column>
          
          <el-table-column prop="tuanduiBianhao" label="团队编号" align="center" width="150"></el-table-column>
          
          <el-table-column prop="tuanduiMingcheng" label="团队名称" align="center" min-width="200">
            <template slot-scope="scope">
              <span style="font-weight: 600; color: #5c7cfa;">{{scope.row.tuanduiMingcheng}}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="jingsaimingcheng" label="竞赛名称" align="center" min-width="200"></el-table-column>
          
          <el-table-column prop="fuzerenXingming" label="负责人" align="center" width="120"></el-table-column>
          
          <el-table-column prop="chengyuanRenshu" label="成员数" align="center" width="100"></el-table-column>
          
          <el-table-column prop="shenheZhuangtai" label="审核状态" align="center" width="120">
            <template slot-scope="scope">
              <el-tag :type="getShenheType(scope.row.shenheZhuangtai)" effect="dark">
                {{scope.row.shenheZhuangtai}}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="250" align="center">
            <template slot-scope="scope">
              <el-button 
                type="primary" 
                icon="el-icon-view" 
                size="mini" 
                @click="viewTeamDetail(scope.row)">
                查看详情
              </el-button>
              <el-button 
                type="danger" 
                icon="el-icon-circle-close" 
                size="mini" 
                @click="applyQuit(scope.row)"
                v-if="!isLeader(scope.row)">
                申请退出
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 我的申请 -->
      <el-tab-pane label="我的申请" name="myApply">
        <div class="search-wrapper">
          <el-form :inline="true" :model="applySearchForm" class="tech-search-form">
            <el-form-item label="申请类型">
              <el-select v-model="applySearchForm.applyType" placeholder="请选择申请类型" clearable>
                <el-option label="加入" value="加入"></el-option>
                <el-option label="退出" value="退出"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="申请状态">
              <el-select v-model="applySearchForm.applyStatus" placeholder="请选择申请状态" clearable>
                <el-option label="待审核" value="待审核"></el-option>
                <el-option label="已通过" value="已通过"></el-option>
                <el-option label="已驳回" value="已驳回"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" @click="getMyApplyList()">查询</el-button>
              <el-button icon="el-icon-refresh" @click="resetApplySearch()">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <el-table 
          class="tech-table"
          :data="myApplyList"
          v-loading="applyListLoading"
          border>
          
          <el-table-column label="序号" type="index" width="60" align="center"></el-table-column>
          
          <el-table-column prop="tuanduiMingcheng" label="团队名称" align="center" min-width="200">
            <template slot-scope="scope">
              <span style="font-weight: 600; color: #5c7cfa;">{{scope.row.tuanduiMingcheng}}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="applyType" label="申请类型" align="center" width="120">
            <template slot-scope="scope">
              <el-tag :type="scope.row.applyType === '加入' ? 'success' : 'warning'" effect="dark">
                {{scope.row.applyType}}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="applyReason" label="申请理由" align="center" min-width="200" show-overflow-tooltip></el-table-column>
          
          <el-table-column prop="applyStatus" label="申请状态" align="center" width="120">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.applyStatus)" effect="dark">
                {{scope.row.applyStatus}}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="applyTime" label="申请时间" align="center" width="170"></el-table-column>
          
          <el-table-column prop="shenheXingming" label="审核人" align="center" width="120">
            <template slot-scope="scope">
              <span v-if="scope.row.shenheXingming">{{scope.row.shenheXingming}}</span>
              <span v-else style="color: #868e96;">-</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="shenheTime" label="审核时间" align="center" width="170">
            <template slot-scope="scope">
              <span v-if="scope.row.shenheTime">{{scope.row.shenheTime}}</span>
              <span v-else style="color: #868e96;">-</span>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
export default {
  data() {
    return {
      activeTab: 'myTeam',
      searchForm: {
        tuanduiMingcheng: ''
      },
      applySearchForm: {
        applyType: '',
        applyStatus: ''
      },
      myTeamList: [],
      myApplyList: [],
      dataListLoading: false,
      applyListLoading: false,
      hasTeam: false,
      // 团队详情相关
      teamDetailVisible: false,
      currentTeam: null,
      teamMembers: [],
      membersLoading: false,
      isCurrentUserLeader: false,
      // 编辑团队信息
      editTeamVisible: false,
      editTeamForm: {
        id: null,
        tuanduiMingcheng: '',
        tuanduiJianjie: ''
      },
      // 添加成员
      addMemberVisible: false,
      addMemberForm: {
        tuanduiId: null,
        xuehao: '',
        juese: '普通成员'
      }
    }
  },
  created() {
    this.getMyTeamList()
    this.getMyApplyList()
  },
  methods: {
    // 获取我的团队列表
    getMyTeamList() {
      this.dataListLoading = true
      let params = {
        page: 1,
        limit: 100,
        sort: 'id'
      }
      
      if (this.searchForm.tuanduiMingcheng) {
        params.tuanduiMingcheng = this.searchForm.tuanduiMingcheng
      }
      
      this.$http({
        url: 'jingsai/tuandui/my/list',
        method: 'get',
        params: params
      }).then(({ data }) => {
        console.log('【我的团队API响应】:', data)
        if (data && data.code === 0) {
          console.log('【page对象】:', data.page)
          if (data.page && data.page.list) {
            console.log('【团队列表】:', data.page.list)
            this.myTeamList = data.page.list
            this.hasTeam = this.myTeamList.length > 0
            console.log('✅ 加载团队数量:', this.myTeamList.length)
          } else {
            console.warn('️ 没有团队数据')
            this.myTeamList = []
            this.hasTeam = false
          }
        } else {
          console.error('❌ API返回错误:', data)
        }
        this.dataListLoading = false
      }).catch((error) => {
        console.error('❌ 请求失败:', error)
        this.dataListLoading = false
      })
    },
    
    // 获取我的申请列表
    getMyApplyList() {
      this.applyListLoading = true
      let params = {
        page: 1,
        limit: 100,
        sort: 'apply_time'
      }
      
      if (this.applySearchForm.applyType) {
        params.applyType = this.applySearchForm.applyType
      }
      if (this.applySearchForm.applyStatus) {
        params.applyStatus = this.applySearchForm.applyStatus
      }
      
      this.$http({
        url: 'tuandui/apply/my/list',
        method: 'get',
        params: params
      }).then(({ data }) => {
        if (data && data.code === 0) {
          if (data.page && data.page.list) {
            this.myApplyList = data.page.list
          }
        }
        this.applyListLoading = false
      }).catch(() => {
        this.applyListLoading = false
      })
    },
    
    // 选项卡切换
    handleTabClick(tab) {
      if (tab.name === 'myTeam') {
        this.getMyTeamList()
      } else if (tab.name === 'myApply') {
        this.getMyApplyList()
      }
    },
    
    // 搜索
    search() {
      this.getMyTeamList()
    },
    
    // 重置搜索
    resetSearch() {
      this.searchForm = {
        tuanduiMingcheng: ''
      }
      this.getMyTeamList()
    },
    
    // 重置申请搜索
    resetApplySearch() {
      this.applySearchForm = {
        applyType: '',
        applyStatus: ''
      }
      this.getMyApplyList()
    },
    
    // 获取审核状态类型
    getShenheType(status) {
      if (status === '已通过') return 'success'
      if (status === '已驳回') return 'danger'
      if (status === '审核中') return 'warning'
      return 'info'
    },
    
    // 获取申请状态类型
    getStatusType(status) {
      if (status === '待审核') return 'warning'
      if (status === '已通过') return 'success'
      if (status === '已驳回') return 'danger'
      return 'info'
    },
    
    // 判断是否是负责人
    isLeader(team) {
      const xuehao = this.$storage.get('username')
      return team.fuzerenXuehao === xuehao
    },
    
    // 查看团队详情
    viewTeamDetail(team) {
      this.currentTeam = team
      this.teamDetailVisible = true
      this.isCurrentUserLeader = this.isLeader(team)
      this.getTeamMembers(team.id)
    },
    
    // 获取团队成员
    getTeamMembers(tuanduiId) {
      this.membersLoading = true
      this.$http({
        url: `jingsai/tuandui/chengyuan/list/${tuanduiId}`,
        method: 'get'
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.teamMembers = data.data || []
          console.log('团队成员加载成功:', this.teamMembers.length, '人')
        } else {
          this.$message.error('加载团队成员失败')
          this.teamMembers = []
        }
        this.membersLoading = false
      }).catch((error) => {
        console.error('加载团队成员失败:', error)
        this.teamMembers = []
        this.membersLoading = false
      })
    },
    
    // 关闭团队详情
    closeTeamDetail() {
      this.currentTeam = null
      this.teamMembers = []
      this.isCurrentUserLeader = false
    },
    
    // 移除成员
    removeMember(member) {
      this.$confirm(`确定要移除成员 "${member.xueshengxingming}" 吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: 'jingsai/tuandui/chengyuan/remove',
          method: 'post',
          data: {
            chengyuanId: member.id
          }
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message.success('成员移除成功')
            this.getTeamMembers(this.currentTeam.id)
            this.getMyTeamList()
          } else {
            this.$message.error(data.msg || '移除失败')
          }
        })
      }).catch(() => {})
    },
    
    // 编辑团队信息
    editTeamInfo() {
      this.editTeamForm = {
        id: this.currentTeam.id,
        tuanduiMingcheng: this.currentTeam.tuanduiMingcheng,
        tuanduiJianjie: this.currentTeam.tuanduiJianjie || ''
      }
      this.editTeamVisible = true
    },
    
    // 保存团队信息
    saveTeamInfo() {
      this.$http({
        url: 'jingsai/tuandui/update',
        method: 'post',
        data: this.editTeamForm
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.$message.success('团队信息更新成功')
          this.editTeamVisible = false
          this.getMyTeamList()
          // 更新当前团队信息
          Object.assign(this.currentTeam, this.editTeamForm)
        } else {
          this.$message.error(data.msg || '更新失败')
        }
      })
    },
    
    // 显示添加成员对话框
    showAddMemberDialog() {
      this.addMemberForm = {
        tuanduiId: this.currentTeam.id,
        xuehao: '',
        juese: '普通成员'
      }
      this.addMemberVisible = true
    },
    
    // 添加成员
    addMember() {
      if (!this.addMemberForm.xuehao) {
        this.$message.warning('请输入学号')
        return
      }
      
      this.$http({
        url: 'jingsai/tuandui/chengyuan/add',
        method: 'post',
        data: this.addMemberForm
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.$message.success('成员添加成功')
          this.addMemberVisible = false
          this.getTeamMembers(this.currentTeam.id)
          this.getMyTeamList()
        } else {
          this.$message.error(data.msg || '添加失败')
        }
      })
    },
    
    // 解散团队
    dissolveTeam() {
      this.$confirm(
        '⚠️ 警告：解散团队后将删除所有团队成员数据，此操作不可恢复！\n\n确定要解散团队 "' + this.currentTeam.tuanduiMingcheng + '" 吗？',
        '解散团队确认',
        {
          confirmButtonText: '确定解散',
          cancelButtonText: '取消',
          type: 'error',
          distinguishCancelAndClose: true
        }
      ).then(() => {
        this.$http({
          url: 'jingsai/tuandui/dissolve',
          method: 'post',
          data: {
            id: this.currentTeam.id
          }
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message.success('团队已解散')
            this.teamDetailVisible = false
            this.getMyTeamList()
          } else {
            this.$message.error(data.msg || '解散失败')
          }
        })
      }).catch((action) => {
        if (action === 'cancel') {
          this.$message.info('已取消解散操作')
        }
      })
    },
    
    // 创建团队
    createTeam() {
      this.$router.push('/xuesheng-tuandui-create')
    },
    
    // 申请加入团队
    joinTeam() {
      this.$router.push('/xuesheng-tuandui-join')
    },
    
    // 申请退出团队
    applyQuit(team) {
      this.$confirm('确定要申请退出团队 "' + team.tuanduiMingcheng + '" 吗？退出后需要负责人审核通过。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const xuehao = this.$storage.get('username')
        const xueshengxingming = this.$storage.get('session')?.xingming || ''
        
        this.$http({
          url: 'tuandui/apply/quit',
          method: 'post',
          data: {
            tuanduiId: team.id,
            xuehao: xuehao,
            xueshengxingming: xueshengxingming,
            applyReason: '学生主动申请退出团队'
          }
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message.success('退出申请已提交，等待负责人审核')
            this.getMyTeamList()
            this.getMyApplyList()
          } else {
            this.$message.error(data.msg || '申请失败')
          }
        })
      }).catch(() => {
        this.$message.info('已取消退出申请')
      })
    },
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/css/tech-theme.scss';

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  .page-actions {
    display: flex;
    gap: 10px;
  }
}

.search-wrapper {
  margin-bottom: 20px;
}

.tech-search-form {
  ::v-deep .el-form-item {
    margin-bottom: 0;
    margin-right: 20px;
  }
}

.tech-table {
  ::v-deep .el-table__body tr:hover > td {
    background: rgba(#5c7cfa, 0.08) !important;
  }
}

.team-detail {
  .team-members-section {
    margin-top: 20px;
  }
  
  .team-management-actions {
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #e9ecef;
    
    .el-button {
      margin-right: 10px;
    }
  }
}

/* 响应式设计 - 平板设备 */
@media screen and (max-width: 1200px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
    
    .page-actions {
      width: 100%;
      justify-content: flex-start;
    }
  }
}

/* 响应式设计 - 手机设备 */
@media screen and (max-width: 768px) {
  .page-header {
    margin-bottom: 15px;
    
    .page-title {
      font-size: 18px;
    }
    
    .page-subtitle {
      font-size: 12px;
    }
    
    .page-actions {
      flex-direction: column;
      width: 100%;
      
      .el-button {
        width: 100%;
        margin-bottom: 5px;
      }
    }
  }
  
  .search-wrapper {
    margin-bottom: 15px;
  }
  
  .tech-search-form {
    ::v-deep .el-form-item {
      width: 100%;
      margin-right: 0;
      margin-bottom: 10px;
    }
    
    ::v-deep .el-input__inner {
      width: 100% !important;
    }
  }
  
  .team-detail {
    ::v-deep .el-descriptions {
      font-size: 12px;
    }
    
    ::v-deep .el-descriptions__label {
      width: 80px !important;
      min-width: 80px;
    }
    
    .team-members-section {
      margin-top: 15px;
      
      h4 {
        font-size: 14px;
        margin: 15px 0 10px;
      }
    }
    
    .team-management-actions {
      margin-top: 15px;
      padding-top: 15px;
      
      .el-button {
        width: 100%;
        margin-right: 0;
        margin-bottom: 8px;
      }
    }
  }
  
  /* 表格在小屏幕上的优化 */
  .tech-table {
    font-size: 12px;
    
    ::v-deep .el-table__cell {
      padding: 8px 0;
    }
  }
  
  /* 对话框在小屏幕上的优化 */
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
  .page-header {
    .page-title {
      font-size: 16px;
    }
    
    .page-subtitle {
      font-size: 11px;
    }
  }
  
  .tech-table {
    font-size: 11px;
  }
  
  .team-detail {
    ::v-deep .el-descriptions {
      font-size: 11px;
    }
  }
}
</style>
