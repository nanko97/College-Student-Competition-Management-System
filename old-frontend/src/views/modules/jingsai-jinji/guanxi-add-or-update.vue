<template>
  <el-dialog :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false" :visible.sync="visible" width="600px">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="120px">
      <el-form-item label="父竞赛" prop="fuJingsaiId">
        <el-select v-model="dataForm.fuJingsaiId" placeholder="请选择父竞赛" filterable style="width: 100%">
          <el-option v-for="item in jingsaiList" :key="item.id" :label="item.jingsaimingcheng" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="父级别" prop="fuJibie">
        <el-select v-model="dataForm.fuJibie" placeholder="请选择级别">
          <el-option label="校级" value="校级"></el-option>
          <el-option label="省级" value="省级"></el-option>
          <el-option label="国家级" value="国家级"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="子竞赛" prop="ziJingsaiId">
        <el-select v-model="dataForm.ziJingsaiId" placeholder="请选择子竞赛" filterable style="width: 100%">
          <el-option v-for="item in jingsaiList" :key="item.id" :label="item.jingsaimingcheng" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="子级别" prop="ziJibie">
        <el-select v-model="dataForm.ziJibie" placeholder="请选择级别">
          <el-option label="省级" value="省级"></el-option>
          <el-option label="国家级" value="国家级"></el-option>
          <el-option label="国际级" value="国际级"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="最低分数">
        <el-input-number v-model="dataForm.zuidiFenshu" :min="0" :max="100" :precision="0" style="width: 100%"></el-input-number>
      </el-form-item>
      <el-form-item label="最高名次">
        <el-input-number v-model="dataForm.zuidiMingci" :min="1" :precision="0" style="width: 100%"></el-input-number>
      </el-form-item>
      <el-form-item label="状态" prop="isActive">
        <el-radio-group v-model="dataForm.isActive">
          <el-radio label="是">启用</el-radio>
          <el-radio label="否">禁用</el-radio>
        </el-radio-group>
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
        fuJingsaiId: null,
        fuJibie: '',
        ziJingsaiId: null,
        ziJibie: '',
        zuidiFenshu: null,
        zuidiMingci: null,
        isActive: '是'
      },
      dataRule: {
        fuJingsaiId: [{ required: true, message: '请选择父竞赛', trigger: 'change' }],
        fuJibie: [{ required: true, message: '请选择父级别', trigger: 'change' }],
        ziJingsaiId: [{ required: true, message: '请选择子竞赛', trigger: 'change' }],
        ziJibie: [{ required: true, message: '请选择子级别', trigger: 'change' }],
        isActive: [{ required: true, message: '请选择状态', trigger: 'change' }]
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
            url: `jingsai/jinji/guanxi/info/${this.dataForm.id}`,
            method: 'get',
            params: {}
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.dataForm.fuJingsaiId = data.data.fuJingsaiId
              this.dataForm.fuJibie = data.data.fuJibie
              this.dataForm.ziJingsaiId = data.data.ziJingsaiId
              this.dataForm.ziJibie = data.data.ziJibie
              this.dataForm.zuidiFenshu = data.data.zuidiFenshu
              this.dataForm.zuidiMingci = data.data.zuidiMingci
              this.dataForm.isActive = data.data.isActive
            }
          }).catch(err => {
            console.error('获取晋级关系信息失败:', err)
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
          this.$http({
            url: `jingsai/jinji/guanxi/${!this.dataForm.id ? 'save' : 'update'}`,
            method: 'post',
            data: {
              'id': this.dataForm.id || undefined,
              'fuJingsaiId': this.dataForm.fuJingsaiId,
              'fuJibie': this.dataForm.fuJibie,
              'ziJingsaiId': this.dataForm.ziJingsaiId,
              'ziJibie': this.dataForm.ziJibie,
              'zuidiFenshu': this.dataForm.zuidiFenshu,
              'zuidiMingci': this.dataForm.zuidiMingci,
              'isActive': this.dataForm.isActive
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
