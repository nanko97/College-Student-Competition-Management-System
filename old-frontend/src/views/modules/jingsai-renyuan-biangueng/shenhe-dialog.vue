<template>
  <el-dialog title="审核人员变更" :visible.sync="visible" width="600px" :close-on-click-modal="false">
    <el-form :model="dataForm" label-width="100px">
      <el-form-item label="团队编号">
        <el-input v-model="bianguengInfo.tuanduiBianhao" disabled></el-input>
      </el-form-item>
      <el-form-item label="变更类型">
        <el-tag :type="getLeixingType(bianguengInfo.bianguengLeixing)">{{ bianguengInfo.bianguengLeixing }}</el-tag>
      </el-form-item>
      <el-form-item label="申请人">
        <el-input :value="`${bianguengInfo.caozuoRenXingming} (${bianguengInfo.caozuoRenXuehao})`" disabled></el-input>
      </el-form-item>
      <el-form-item label="变更原因">
        <el-input v-model="bianguengInfo.caozuoYuanyin" type="textarea" :rows="3" disabled></el-input>
      </el-form-item>
      <el-form-item label="审核结果">
        <el-radio-group v-model="dataForm.shenheZhuangtai">
          <el-radio label="已通过">通过</el-radio>
          <el-radio label="已驳回">驳回</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="审核意见">
        <el-input v-model="dataForm.shenheYijian" type="textarea" :rows="3" placeholder="请输入审核意见（可选）"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="submitShenhe()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  data() {
    return {
      visible: false,
      bianguengInfo: {},
      dataForm: {
        shenheZhuangtai: '已通过',
        shenheYijian: ''
      }
    }
  },
  methods: {
    init(row, defaultResult) {
      this.bianguengInfo = Object.assign({}, row)
      this.dataForm.shenheZhuangtai = defaultResult || '已通过'
      this.dataForm.shenheYijian = ''
      this.visible = true
    },
    getLeixingType(leixing) {
      if (leixing === '添加成员') return 'success'
      if (leixing === '移除成员') return 'warning'
      return 'danger'
    },
    submitShenhe() {
      const url = this.dataForm.shenheZhuangtai === '已通过' 
        ? 'jingsai/biangueng/shenhe/approve' 
        : 'jingsai/biangueng/shenhe/reject'
      
      this.$http({
        url: url,
        method: 'post',
        data: {
          bianguengId: this.bianguengInfo.id
        }
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.$message({
            message: '审核成功',
            type: 'success',
            duration: 1500,
            onClose: () => {
              this.visible = false
              this.$emit('refreshDataList')
            }
          })
        } else {
          this.$message.error(data.msg)
        }
      }).catch(err => {
        console.error('审核人员变更失败:', err)
      })
    }
  }
}
</script>
