<template>
  <el-dialog :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false" :visible.sync="visible" width="700px">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
      <el-form-item label="竞赛名称" prop="jingsaimingcheng">
        <el-select v-model="dataForm.jingsaimingcheng" placeholder="请选择竞赛" filterable @change="jingsaiChange" style="width: 100%">
          <el-option v-for="item in jingsaiList" :key="item.id" :label="item.jingsaimingcheng" :value="item.jingsaimingcheng"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="赛道名称" prop="saidaoMingcheng">
        <el-input v-model="dataForm.saidaoMingcheng" placeholder="请输入赛道名称"></el-input>
      </el-form-item>
      <el-form-item label="赛道编码" prop="saidaoBianma">
        <el-input v-model="dataForm.saidaoBianma" placeholder="请输入赛道编码"></el-input>
      </el-form-item>
      <el-form-item label="参赛类型" prop="cansaiLeixing">
        <el-select v-model="dataForm.cansaiLeixing" placeholder="请选择参赛类型" style="width: 100%">
          <el-option label="个人赛" value="个人赛"></el-option>
          <el-option label="团队赛" value="团队赛"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="团队最少人数" prop="tuanduiRenshuMin">
        <el-input-number v-model="dataForm.tuanduiRenshuMin" :min="1" :max="20" style="width: 100%"></el-input-number>
      </el-form-item>
      <el-form-item label="团队最多人数" prop="tuanduiRenshuMax">
        <el-input-number v-model="dataForm.tuanduiRenshuMax" :min="1" :max="20" style="width: 100%"></el-input-number>
      </el-form-item>
      <el-form-item label="报名说明" prop="baomingShuoming">
        <el-input v-model="dataForm.baomingShuoming" type="textarea" :rows="3" placeholder="请输入报名说明"></el-input>
      </el-form-item>
      <el-form-item label="评审标准" prop="pingshenBiaozhun">
        <el-input v-model="dataForm.pingshenBiaozhun" type="textarea" :rows="3" placeholder="请输入评审标准"></el-input>
      </el-form-item>
      <el-form-item label="是否启用" prop="isActive">
        <el-radio-group v-model="dataForm.isActive">
          <el-radio label="是">是</el-radio>
          <el-radio label="否">否</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="排序" prop="sortOrder">
        <el-input-number v-model="dataForm.sortOrder" :min="0" :max="999" style="width: 100%"></el-input-number>
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
        saidaoMingcheng: '',
        saidaoBianma: '',
        cansaiLeixing: '团队赛',
        tuanduiRenshuMin: 3,
        tuanduiRenshuMax: 5,
        baomingShuoming: '',
        pingshenBiaozhun: '',
        isActive: '是',
        sortOrder: 0
      },
      dataRule: {
        jingsaimingcheng: [{ required: true, message: '竞赛名称不能为空', trigger: 'blur' }],
        saidaoMingcheng: [{ required: true, message: '赛道名称不能为空', trigger: 'blur' }],
        saidaoBianma: [{ required: true, message: '赛道编码不能为空', trigger: 'blur' }],
        cansaiLeixing: [{ required: true, message: '参赛类型不能为空', trigger: 'change' }],
        tuanduiRenshuMin: [{ required: true, message: '团队最少人数不能为空', trigger: 'blur' }],
        tuanduiRenshuMax: [{ required: true, message: '团队最多人数不能为空', trigger: 'blur' }]
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
            url: `jingsai/saidao/info/${this.dataForm.id}`,
            method: 'get'
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.dataForm = Object.assign({}, this.dataForm, data.jingsaisaidao)
            }
          }).catch(err => {
            console.error('获取赛道信息失败:', err)
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
    jingsaiChange(val) {
      const jingsai = this.jingsaiList.find(item => item.jingsaimingcheng === val)
      if (jingsai) {
        this.dataForm.jingsaiId = jingsai.id
      }
    },
    dataFormSubmit() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          if (this.dataForm.tuanduiRenshuMin > this.dataForm.tuanduiRenshuMax) {
            this.$message.error('团队最少人数不能大于最多人数')
            return
          }
          this.$http({
            url: `jingsai/saidao/${!this.dataForm.id ? 'save' : 'update'}`,
            method: 'post',
            data: this.dataForm
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.$message({
                message: data.msg || '操作成功',
                type: 'success',
                duration: 2000,
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
