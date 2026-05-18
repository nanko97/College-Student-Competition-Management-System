<template>
  <el-dialog title="团队审核" :visible.sync="visible" width="600px" :close-on-click-modal="false">
    <el-form :model="dataForm" label-width="100px">
      <el-form-item label="团队编号">
        <el-input v-model="tuanduiInfo.tuanduiBianhao" disabled></el-input>
      </el-form-item>
      <el-form-item label="团队名称">
        <el-input v-model="tuanduiInfo.tuanduiMingcheng" disabled></el-input>
      </el-form-item>
      <el-form-item label="负责人">
        <el-input v-model="tuanduiInfo.fuzerenXingming" disabled></el-input>
      </el-form-item>
      <el-form-item label="审核结果" prop="shenheZhuangtai">
        <el-radio-group v-model="dataForm.shenheZhuangtai">
          <el-radio label="已通过">通过</el-radio>
          <el-radio label="已驳回">驳回</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="审核意见" prop="shenheYijian">
        <el-input v-model="dataForm.shenheYijian" type="textarea" :rows="4" placeholder="请输入审核意见"></el-input>
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
      tuanduiInfo: {},
      dataForm: {
        shenheZhuangtai: '已通过',
        shenheYijian: ''
      }
    }
  },
  methods: {
    init(tuandui) {
      this.tuanduiInfo = Object.assign({}, tuandui)
      this.dataForm.shenheZhuangtai = '已通过'
      this.dataForm.shenheYijian = ''
      this.visible = true
    },
    submitShenhe() {
      this.$http({
        url: 'jingsai/tuandui/shenhe',
        method: 'post',
        data: {
          id: this.tuanduiInfo.id,
          shenheZhuangtai: this.dataForm.shenheZhuangtai,
          shenheYijian: this.dataForm.shenheYijian
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
        console.error('团队审核失败:', err)
      })
    }
  }
}
</script>
