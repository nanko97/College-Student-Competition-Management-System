<template>
  <div class="page-container tech-theme animate-fade-in-up">
    <div class="page-header">
      <h2 class="page-title">成绩复核审核</h2>
      <p class="page-subtitle">Score Review Management</p>
    </div>

    <div v-if="showFlag">
      <div class="search-wrapper">
        <el-form :inline="true" :model="searchForm" class="tech-search-form">
          <el-form-item label="学生学号">
            <el-input v-model="searchForm.xuehao" placeholder="请输入学号" clearable></el-input>
          </el-form-item>
          <el-form-item label="复核状态">
            <el-select v-model="searchForm.fuheStatus" placeholder="请选择" clearable>
              <el-option label="待审核" value="待审核"></el-option>
              <el-option label="已通过" value="已通过"></el-option>
              <el-option label="已驳回" value="已驳回"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="search()">查询</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="table-wrapper">
        <el-table 
          class="tech-table"
          :data="dataList"
          v-loading="dataListLoading"
          stripe>
          <el-table-column type="index" label="索引" width="60" align="center"></el-table-column>
          <el-table-column prop="xuehao" label="学号" width="120"></el-table-column>
          <el-table-column prop="xueshengxingming" label="学生姓名" width="100"></el-table-column>
          <el-table-column prop="jingsaimingcheng" label="竞赛名称" show-overflow-tooltip></el-table-column>
          <el-table-column prop="yuanChengji" label="原成绩" width="100" align="center">
            <template slot-scope="scope">
              <span style="font-weight: 700; color: #ff6b6b;">{{scope.row.yuanChengji}}</span>
            </template>
          </el-table-column>
          <el-table-column prop="fuheReason" label="复核理由" show-overflow-tooltip width="250"></el-table-column>
          <el-table-column prop="fuheStatus" label="审核状态" width="100" align="center">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.fuheStatus)" size="small">{{scope.row.fuheStatus}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="shenqingShijian" label="申请时间" width="160"></el-table-column>
          <el-table-column label="操作" width="150" align="center">
            <template slot-scope="scope">
              <el-button 
                v-if="scope.row.fuheStatus === '待审核'"
                type="success" 
                icon="el-icon-check" 
                size="mini" 
                @click="handleAudit(scope.row)">审核</el-button>
              <el-button 
                type="primary" 
                icon="el-icon-view" 
                size="mini" 
                @click="viewDetail(scope.row)">详情</el-button>
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

    <!-- 审核对话框 -->
    <el-dialog
      title="审核成绩复核申请"
      :visible.sync="auditDialogVisible"
      width="600px"
      :close-on-click-modal="false">
      <el-form :model="auditForm" label-width="100px">
        <el-form-item label="学生信息">
          <span>{{auditForm.xueshengxingming}} ({{auditForm.xuehao}})</span>
        </el-form-item>
        <el-form-item label="竞赛名称">
          <span>{{auditForm.jingsaimingcheng}}</span>
        </el-form-item>
        <el-form-item label="原成绩">
          <span style="color: #ff6b6b; font-weight: 700;">{{auditForm.yuanChengji}} 分</span>
        </el-form-item>
        <el-form-item label="复核理由">
          <div style="background: #f5f7fa; padding: 12px; border-radius: 4px;">{{auditForm.fuheReason}}</div>
        </el-form-item>
        <el-form-item label="审核结果" required>
          <el-radio-group v-model="auditForm.fuheStatus">
            <el-radio label="已通过">通过（重新打分）</el-radio>
            <el-radio label="已驳回">驳回（保持原成绩）</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="auditForm.fuheStatus === '已通过'" label="新成绩" required>
          <el-input-number v-model="auditForm.xinChengjiValue" :min="0" :max="100" :step="1"></el-input-number>
          <span style="margin-left: 10px;">分</span>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input 
            type="textarea" 
            v-model="auditForm.shenheYijian" 
            :rows="3"
            placeholder="请输入审核意见（可选）">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="auditDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitAudit">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      title="复核申请详情"
      :visible.sync="detailDialogVisible"
      width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="学号">{{detailData.xuehao}}</el-descriptions-item>
        <el-descriptions-item label="学生姓名">{{detailData.xueshengxingming}}</el-descriptions-item>
        <el-descriptions-item label="竞赛名称" :span="2">{{detailData.jingsaimingcheng}}</el-descriptions-item>
        <el-descriptions-item label="原成绩">{{detailData.yuanChengji}} 分</el-descriptions-item>
        <el-descriptions-item label="新成绩">{{detailData.xinChengji || '无'}}</el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="getStatusType(detailData.fuheStatus)">{{detailData.fuheStatus}}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">{{detailData.shenqingShijian}}</el-descriptions-item>
        <el-descriptions-item label="复核理由" :span="2">{{detailData.fuheReason}}</el-descriptions-item>
        <el-descriptions-item label="审核教师" v-if="detailData.shenheJiaoshi">{{detailData.shenheJiaoshi}}</el-descriptions-item>
        <el-descriptions-item label="审核时间" v-if="detailData.shenheShijian">{{detailData.shenheShijian}}</el-descriptions-item>
        <el-descriptions-item label="审核意见" :span="2" v-if="detailData.shenheYijian">{{detailData.shenheYijian}}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      searchForm: {},
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      showFlag: true,
      auditDialogVisible: false,
      detailDialogVisible: false,
      auditForm: {
        id: '',
        zuopindafenId: '',
        xuehao: '',
        xueshengxingming: '',
        jingsaimingcheng: '',
        yuanChengji: '',
        fuheReason: '',
        fuheStatus: '已通过',
        xinChengjiValue: null,
        xinChengji: '',
        shenheYijian: ''
      },
      detailData: {}
    }
  },
  created() {
    this.getDataList()
  },
  methods: {
    getDataList() {
      this.dataListLoading = true
      let params = {
        page: this.pageIndex,
        limit: this.pageSize,
        sort: 'id'
      }
      if (this.searchForm.xuehao) {
        params.xuehao = this.searchForm.xuehao
      }
      if (this.searchForm.fuheStatus) {
        params.fuheStatus = this.searchForm.fuheStatus
      }
      
      this.$http({
        url: 'zuopindafenfuhe/page',
        method: 'get',
        params: params
      }).then(({ data }) => {
        if (data && data.code === 0) {
          if (data.data && data.data.list) {
            this.dataList = data.data.list
            this.totalPage = data.data.total
          }
        }
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
    getStatusType(status) {
      if (status === '待审核') return 'warning'
      if (status === '已通过') return 'success'
      if (status === '已驳回') return 'danger'
      return 'info'
    },
    handleAudit(row) {
      this.auditForm = {
        id: row.id,
        zuopindafenId: row.zuopindafenId,
        xuehao: row.xuehao,
        xueshengxingming: row.xueshengxingming,
        jingsaimingcheng: row.jingsaimingcheng,
        yuanChengji: row.yuanChengji,
        fuheReason: row.fuheReason,
        fuheStatus: '已通过',
        xinChengjiValue: parseInt(row.yuanChengji) || 0,
        xinChengji: '',
        shenheYijian: ''
      }
      this.auditDialogVisible = true
    },
    submitAudit() {
      if (this.auditForm.fuheStatus === '已通过' && !this.auditForm.xinChengjiValue) {
        this.$message.warning('请输入新成绩')
        return
      }
      
      this.auditForm.xinChengji = this.auditForm.fuheStatus === '已通过' 
        ? String(this.auditForm.xinChengjiValue) 
        : this.auditForm.yuanChengji
      
      // 获取当前教师信息
      const gonghao = this.$storage.get('username')
      const jiaoshixingming = this.$storage.get('userRealName') || ''
      
      this.$http({
        url: 'zuopindafenfuhe/shenhe',
        method: 'post',
        data: {
          id: this.auditForm.id,
          zuopindafenId: this.auditForm.zuopindafenId,
          yuanChengji: this.auditForm.yuanChengji,
          fuheStatus: this.auditForm.fuheStatus,
          xinChengji: this.auditForm.xinChengji,
          shenheYijian: this.auditForm.shenheYijian,
          shenheGonghao: gonghao,
          shenheJiaoshi: jiaoshixingming
        }
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.$message.success('审核成功')
          this.auditDialogVisible = false
          this.getDataList()
        } else {
          this.$message.error(data.msg || '审核失败')
        }
      })
    },
    viewDetail(row) {
      this.detailData = row
      this.detailDialogVisible = true
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/css/tech-theme.scss';
.tech-search-form {
  ::v-deep .el-form-item {
    margin-bottom: 0;
    margin-right: 20px;
  }
  ::v-deep .el-input__inner { width: 200px; }
  ::v-deep .el-select .el-input__inner { width: 150px; }
  // 让最后一个form-item（查询按钮）垂直居中
  ::v-deep .el-form-item:last-child {
    margin-right: 0;
    .el-form-item__content {
      display: flex;
      align-items: center;
      line-height: 32px; // 与输入框高度一致
    }
  }
}
.tech-pagination {
  margin-top: 20px;
}
</style>
