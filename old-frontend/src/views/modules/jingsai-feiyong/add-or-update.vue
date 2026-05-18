<template>
  <el-dialog :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false" :visible.sync="visible" width="600px">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="120px">
      <el-form-item label="竞赛名称" prop="jingsaimingcheng">
        <el-select v-model="dataForm.jingsaimingcheng" placeholder="请选择竞赛" filterable style="width: 100%">
          <el-option v-for="item in jingsaiList" :key="item.id" :label="item.jingsaimingcheng" :value="item.jingsaimingcheng"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="报名费(元)" prop="baomingfei">
        <el-input-number v-model="dataForm.baomingfei" :min="0" :precision="2" :step="10" style="width: 100%"></el-input-number>
      </el-form-item>
      <el-form-item label="是否收费" prop="shifouShoufei">
        <el-radio-group v-model="dataForm.shifouShoufei">
          <el-radio label="是">是</el-radio>
          <el-radio label="否">否</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="缴费截止日期" prop="jiaofeiJiezhiRiqi">
        <el-date-picker v-model="dataForm.jiaofeiJiezhiRiqi" type="date" value-format="yyyy-MM-dd" placeholder="选择日期" style="width: 100%"></el-date-picker>
      </el-form-item>
      <el-form-item label="备注" prop="beizhu">
        <el-input v-model="dataForm.beizhu" type="textarea" :rows="3" placeholder="请输入备注信息"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  data() {
    return {
      visible: false,
      jingsaiList: [],
      dataForm: {
        id: null,
        jingsaiId: null,
        jingsaimingcheng: '',
        baomingfei: 0,
        shifouShoufei: '是',
        jiaofeiJiezhiRiqi: '',
        beizhu: ''
      },
      dataRule: {
        jingsaimingcheng: [{ required: true, message: '请选择竞赛', trigger: 'change' }],
        baomingfei: [{ required: true, message: '请输入报名费', trigger: 'blur' }],
        shifouShoufei: [{ required: true, message: '请选择是否收费', trigger: 'change' }]
      }
    }
  },
  methods: {
    init(id) {
      this.dataForm.id = id || null
      this.visible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        this.getJingsaiList()
        if (this.dataForm.id) {
          this.$http({
            url: `jingsai/feiyong/info/${this.dataForm.id}`,
            method: 'get',
            params: {}
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.dataForm.jingsaiId = data.data.jingsaiId
              this.dataForm.jingsaimingcheng = data.data.jingsaimingcheng
              this.dataForm.baomingfei = data.data.baomingfei
              this.dataForm.shifouShoufei = data.data.shifouShoufei
              this.dataForm.jiaofeiJiezhiRiqi = data.data.jiaofeiJiezhiRiqi
              this.dataForm.beizhu = data.data.beizhu
            }
          }).catch(err => {
            console.error('获取费用信息失败:', err)
          })
        }
      })
    },
    getJingsaiList() {
      this.$http({
        url: 'jingsaixinxi/list',
        method: 'get',
        params: {
          page: 1,
          limit: 1000
        }
      }).then(({data}) => {
        console.log('竞赛列表API响应:', data)
        if (data && data.code === 0) {
          // 兼容多种数据返回格式
          if (data.page && data.page.list) {
            this.jingsaiList = data.page.list
          } else if (data.data && data.data.list) {
            this.jingsaiList = data.data.list
          } else if (Array.isArray(data.data)) {
            this.jingsaiList = data.data
          } else {
            this.jingsaiList = []
          }
          console.log('竞赛列表加载成功:', this.jingsaiList.length, '条', this.jingsaiList)
        } else {
          console.error('竞赛列表加载失败:', data.msg)
          this.$message.error(data.msg || '竞赛列表加载失败')
        }
      }).catch(err => {
        console.error('获取竞赛列表失败:', err)
        this.$message.error('获取竞赛列表失败：' + (err.message || '请检查后端服务'))
      })
    },
    dataFormSubmit() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          // 查找竞赛ID
          const selectedJingsai = this.jingsaiList.find(item => item.jingsaimingcheng === this.dataForm.jingsaimingcheng)
          if (selectedJingsai) {
            this.dataForm.jingsaiId = selectedJingsai.id
          }

          this.$http({
            url: `jingsai/feiyong/${!this.dataForm.id ? 'save' : 'update'}`,
            method: 'post',
            data: {
              'id': this.dataForm.id || undefined,
              'jingsaiId': this.dataForm.jingsaiId,
              'jingsaimingcheng': this.dataForm.jingsaimingcheng,
              'baomingfei': this.dataForm.baomingfei,
              'shifouShoufei': this.dataForm.shifouShoufei,
              'jiaofeiJiezhiRiqi': this.dataForm.jiaofeiJiezhiRiqi,
              'beizhu': this.dataForm.beizhu
            }
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
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
          })
        }
      })
    }
  }
}
</script>

<style scoped>
</style>
