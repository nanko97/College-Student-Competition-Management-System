<template>
  <el-dialog :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false" :visible.sync="visible" width="700px">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
      <el-form-item label="竞赛名称" prop="jingsaimingcheng">
        <el-select v-model="dataForm.jingsaimingcheng" placeholder="请选择竞赛" filterable @change="jingsaiChange" style="width: 100%">
          <el-option v-for="item in jingsaiList" :key="item.id" :label="item.jingsaimingcheng" :value="item.jingsaimingcheng"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="竞赛年份" prop="jingsaiNianfen">
        <el-input-number v-model="dataForm.jingsaiNianfen" :min="2020" :max="2030" style="width: 100%"></el-input-number>
      </el-form-item>
      <el-form-item label="级别" prop="jibie">
        <el-select v-model="dataForm.jibie" placeholder="请选择级别" style="width: 100%">
          <el-option label="校级" value="校级"></el-option>
          <el-option label="省级" value="省级"></el-option>
          <el-option label="国家级" value="国家级"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="认定机构" prop="renzhengJigou">
        <el-input v-model="dataForm.renzhengJigou" placeholder="请输入认定机构"></el-input>
      </el-form-item>
      <el-form-item label="文件号" prop="wenjianHao">
        <el-input v-model="dataForm.wenjianHao" placeholder="请输入文件号"></el-input>
      </el-form-item>
      <el-form-item label="生效日期" prop="shengxiaoRiqi">
        <el-date-picker v-model="dataForm.shengxiaoRiqi" type="date" placeholder="选择日期" style="width: 100%" value-format="yyyy-MM-dd"></el-date-picker>
      </el-form-item>
      <el-form-item label="失效日期" prop="shixiaoRiqi">
        <el-date-picker v-model="dataForm.shixiaoRiqi" type="date" placeholder="选择日期" style="width: 100%" value-format="yyyy-MM-dd"></el-date-picker>
      </el-form-item>
      <el-form-item label="是否当前版本" prop="isCurrent">
        <el-radio-group v-model="dataForm.isCurrent">
          <el-radio label="是">是</el-radio>
          <el-radio label="否">否</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="备注" prop="beizhu">
        <el-input v-model="dataForm.beizhu" type="textarea" :rows="3" placeholder="请输入备注"></el-input>
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
        jingsaiNianfen: new Date().getFullYear(),
        jibie: '',
        renzhengJigou: '',
        wenjianHao: '',
        shengxiaoRiqi: '',
        shixiaoRiqi: '',
        beizhu: '',
        isCurrent: '否'
      },
      dataRule: {
        jingsaimingcheng: [{ required: true, message: '竞赛名称不能为空', trigger: 'blur' }],
        jingsaiNianfen: [{ required: true, message: '竞赛年份不能为空', trigger: 'blur' }],
        jibie: [{ required: true, message: '级别不能为空', trigger: 'change' }],
        renzhengJigou: [{ required: true, message: '认定机构不能为空', trigger: 'blur' }],
        shengxiaoRiqi: [{ required: true, message: '生效日期不能为空', trigger: 'change' }]
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
            url: `jingsai/jibiebanben/info/${this.dataForm.id}`,
            method: 'get'
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.dataForm = Object.assign({}, this.dataForm, data.jingsaijibiebanben)
            }
          }).catch(err => {
            console.error('获取级别版本信息失败:', err)
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
          this.$http({
            url: `jingsai/jibiebanben/${!this.dataForm.id ? 'save' : 'update'}`,
            method: 'post',
            data: this.dataForm
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
