<template>
  <el-dialog title="上传缴费凭证" :visible.sync="visible" width="600px" :close-on-click-modal="false">
    <el-form :model="dataForm" label-width="100px">
      <el-form-item label="竞赛名称">
        <el-input :value="jiaofeiInfo.jingsaimingcheng" disabled></el-input>
      </el-form-item>
      <el-form-item label="应缴金额">
        <el-input :value="`${jiaofeiInfo.baomingfei} 元`" disabled></el-input>
      </el-form-item>
      <el-form-item label="缴费凭证" prop="pingzhengTupian">
        <el-upload
          class="upload-demo"
          action="#"
          :auto-upload="false"
          :on-change="handleFileChange"
          :limit="1"
          list-type="picture-card">
          <i class="el-icon-plus"></i>
        </el-upload>
        <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过5MB</div>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="dataForm.beizhu" type="textarea" :rows="3" placeholder="请输入备注（可选）"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="submitUpload()" :loading="uploading">确定上传</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  data() {
    return {
      visible: false,
      uploading: false,
      jiaofeiInfo: {},
      dataForm: {
        beizhu: '',
        file: null
      }
    }
  },
  methods: {
    init(jiaofei) {
      console.log('缴费记录数据:', jiaofei)
      this.jiaofeiInfo = Object.assign({}, jiaofei)
      this.dataForm.beizhu = ''
      this.dataForm.file = null
      this.visible = true
    },
    handleFileChange(file) {
      this.dataForm.file = file.raw
    },
    submitUpload() {
      if (!this.dataForm.file) {
        this.$message.error('请选择缴费凭证图片')
        return
      }
      console.log('缴费记录ID:', this.jiaofeiInfo.id)
      if (!this.jiaofeiInfo.id) {
        this.$message.error('缴费记录ID不存在，请刷新页面重试')
        return
      }
      const formData = new FormData()
      formData.append('file', this.dataForm.file)
      formData.append('jiaofeiId', this.jiaofeiInfo.id)
      formData.append('beizhu', this.dataForm.beizhu)

      this.uploading = true
      this.$http({
        url: 'jingsai/jiaofei/upload-pingzheng',
        method: 'post',
        headers: { 'Content-Type': 'multipart/form-data' },
        data: formData
      }).then(({data}) => {
        this.uploading = false
        if (data && data.code === 0) {
          this.$message({
            message: '上传成功，等待审核',
            type: 'success',
            duration: 1500,
            onClose: () => {
              this.visible = false
              this.$emit('refreshDataList')
            }
          })
        } else {
          this.$message.error(data.msg || '上传失败')
        }
      }).catch(() => {
        this.uploading = false
        this.$message.error('上传失败')
      })
    }
  }
}
</script>
