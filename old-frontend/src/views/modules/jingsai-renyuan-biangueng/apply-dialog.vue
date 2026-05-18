<template>
  <el-dialog title="申请人员变更" :visible.sync="visible" width="700px" :close-on-click-modal="false" class="apply-personnel-dialog">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
      <el-form-item label="选择团队" prop="tuanduiId">
        <div class="custom-select-wrapper">
          <el-input 
            :value="selectedTuanduiText" 
            placeholder="请选择团队" 
            readonly
            @click.native="showTuanduiDropdown = !showTuanduiDropdown"
            suffix-icon="el-icon-arrow-down"
            style="cursor: pointer;"
          ></el-input>
          <div v-show="showTuanduiDropdown" class="custom-dropdown">
            <el-input 
              v-model="tuanduiSearch" 
              placeholder="搜索团队" 
              prefix-icon="el-icon-search"
              size="small"
              style="margin-bottom: 5px;"
            ></el-input>
            <div class="custom-dropdown-list">
              <div 
                v-for="item in filteredTuanduiList" 
                :key="item.id" 
                class="custom-dropdown-item"
                @click="selectTuandui(item)"
              >
                {{ item.tuanduiMingcheng }} ({{ item.tuanduiBianhao }})
              </div>
              <div v-if="filteredTuanduiList.length === 0" class="custom-dropdown-empty">
                暂无数据
              </div>
            </div>
          </div>
        </div>
      </el-form-item>
      <el-form-item label="变更类型" prop="bianguengLeixing">
        <el-radio-group v-model="dataForm.bianguengLeixing" @change="leixingChange">
          <el-radio label="添加成员">添加成员</el-radio>
          <el-radio label="移除成员">移除成员</el-radio>
          <el-radio label="更换负责人">更换负责人</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 添加成员表单 -->
      <template v-if="dataForm.bianguengLeixing === '添加成员'">
        <el-form-item label="学号" prop="newXuehao">
          <el-input v-model="dataForm.newXuehao" placeholder="请输入学号"></el-input>
        </el-form-item>
        <el-form-item label="学生姓名" prop="newXueshengxingming">
          <el-input v-model="dataForm.newXueshengxingming" placeholder="请输入学生姓名"></el-input>
        </el-form-item>
        <el-form-item label="学院">
          <el-input v-model="dataForm.newXueyuan" placeholder="请输入学院"></el-input>
        </el-form-item>
        <el-form-item label="专业">
          <el-input v-model="dataForm.newZhuanye" placeholder="请输入专业"></el-input>
        </el-form-item>
      </template>

      <!-- 移除成员表单 -->
      <template v-if="dataForm.bianguengLeixing === '移除成员'">
        <el-form-item label="选择成员" prop="removeChengyuanId">
          <el-select v-model="dataForm.removeChengyuanId" placeholder="请选择要移除的成员" style="width: 100%" popper-class="dialog-select-dropdown">
            <el-option v-for="item in chengyuanList" :key="item.id" :label="`${item.xueshengxingming} (${item.xuehao})`" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
      </template>

      <!-- 更换负责人表单 -->
      <template v-if="dataForm.bianguengLeixing === '更换负责人'">
        <el-form-item label="新负责人" prop="newFuzerenId">
          <el-select v-model="dataForm.newFuzerenId" placeholder="请选择新负责人" style="width: 100%" popper-class="dialog-select-dropdown">
            <el-option v-for="item in chengyuanList.filter(c => c.juese !== '负责人')" :key="item.id" :label="`${item.xueshengxingming} (${item.xuehao})`" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
      </template>

      <el-form-item label="变更原因" prop="caozuoYuanyin">
        <el-input v-model="dataForm.caozuoYuanyin" type="textarea" :rows="4" placeholder="请详细说明变更原因"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="submitApply()">提交申请</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  data() {
    return {
      visible: false,
      tuanduiList: [],
      chengyuanList: [],
      showTuanduiDropdown: false,
      tuanduiSearch: '',
      dataForm: {
        tuanduiId: null,
        tuanduiBianhao: '',
        jingsaiId: null,
        bianguengLeixing: '',
        caozuoYuanyin: '',
        // 添加成员
        newXuehao: '',
        newXueshengxingming: '',
        newXueyuan: '',
        newZhuanye: '',
        // 移除成员
        removeChengyuanId: null,
        // 更换负责人
        newFuzerenId: null
      },
      dataRule: {
        tuanduiId: [{ required: true, message: '请选择团队', trigger: 'change' }],
        bianguengLeixing: [{ required: true, message: '请选择变更类型', trigger: 'change' }],
        caozuoYuanyin: [{ required: true, message: '请输入变更原因', trigger: 'blur' }]
      }
    }
  },
  computed: {
    selectedTuanduiText() {
      const tuandui = this.tuanduiList.find(t => t.id === this.dataForm.tuanduiId)
      return tuandui ? `${tuandui.tuanduiMingcheng} (${tuandui.tuanduiBianhao})` : ''
    },
    filteredTuanduiList() {
      if (!this.tuanduiSearch) {
        return this.tuanduiList
      }
      const keyword = this.tuanduiSearch.toLowerCase()
      return this.tuanduiList.filter(item => 
        item.tuanduiMingcheng.toLowerCase().includes(keyword) ||
        item.tuanduiBianhao.toLowerCase().includes(keyword)
      )
    }
  },
  mounted() {
    // 点击外部关闭下拉列表
    document.addEventListener('click', this.handleClickOutside)
  },
  beforeDestroy() {
    document.removeEventListener('click', this.handleClickOutside)
  },
  methods: {
    init() {
      this.visible = true
      this.showTuanduiDropdown = false
      this.tuanduiSearch = ''
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        this.getTuanduiList()
      })
    },
    getTuanduiList() {
      console.log('开始获取团队列表...')
      this.$http({
        url: 'jingsai/tuandui/my/list',
        method: 'get',
        params: {}
      }).then(({data}) => {
        console.log('团队列表API响应:', data)
        if (data && data.code === 0) {
          this.tuanduiList = data.page ? data.page.list : []
          console.log('团队列表数据:', this.tuanduiList, '总数:', this.tuanduiList.length)
        } else {
          console.error('团队列表API返回错误:', data)
          this.$message.error(data.msg || '获取团队列表失败')
        }
      }).catch(err => {
        console.error('获取团队列表失败:', err)
        this.$message.error('获取团队列表失败，请检查后端服务')
      })
    },
    selectTuandui(item) {
      this.dataForm.tuanduiId = item.id
      this.dataForm.tuanduiBianhao = item.tuanduiBianhao
      this.dataForm.jingsaiId = item.jingsaiId
      this.showTuanduiDropdown = false
      this.tuanduiSearch = ''
      this.getChengyuanList(item.id)
    },
    handleClickOutside(event) {
      // 如果点击的不是下拉列表区域，则关闭下拉列表
      const dropdown = this.$el.querySelector('.custom-select-wrapper')
      if (dropdown && !dropdown.contains(event.target)) {
        this.showTuanduiDropdown = false
      }
    },
    getChengyuanList(tuanduiId) {
      this.$http({
        url: `jingsai/tuandui/tuanduichengyuan/list`,
        method: 'get',
        params: { tuanduiId: tuanduiId }
      }).then(({data}) => {
        console.log('成员列表API响应:', data)
        if (data && data.code === 0) {
          this.chengyuanList = data.data || []
          console.log('成员列表数据:', this.chengyuanList)
        } else {
          console.error('成员列表API返回错误:', data)
        }
      }).catch(err => {
        console.error('获取成员列表失败:', err)
      })
    },
    leixingChange() {
      // 重置其他字段
      this.dataForm.newXuehao = ''
      this.dataForm.newXueshengxingming = ''
      this.dataForm.newXueyuan = ''
      this.dataForm.newZhuanye = ''
      this.dataForm.removeChengyuanId = null
      this.dataForm.newFuzerenId = null
    },
    submitApply() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          // 构建caozuoHou JSON
          let caozuoHou = {}
          if (this.dataForm.bianguengLeixing === '添加成员') {
            if (!this.dataForm.newXuehao || !this.dataForm.newXueshengxingming) {
              this.$message.error('请填写完整的成员信息')
              return
            }
            caozuoHou = {
              xuehao: this.dataForm.newXuehao,
              xueshengxingming: this.dataForm.newXueshengxingming,
              xueyuan: this.dataForm.newXueyuan,
              zhuanye: this.dataForm.newZhuanye,
              juese: '队员'
            }
          } else if (this.dataForm.bianguengLeixing === '移除成员') {
            if (!this.dataForm.removeChengyuanId) {
              this.$message.error('请选择要移除的成员')
              return
            }
            const chengyuan = this.chengyuanList.find(c => c.id === this.dataForm.removeChengyuanId)
            caozuoHou = { chengyuanId: chengyuan.id, xueshengxingming: chengyuan.xueshengxingming }
          } else if (this.dataForm.bianguengLeixing === '更换负责人') {
            if (!this.dataForm.newFuzerenId) {
              this.$message.error('请选择新负责人')
              return
            }
            const chengyuan = this.chengyuanList.find(c => c.id === this.dataForm.newFuzerenId)
            caozuoHou = { newFuzerenId: chengyuan.id, xueshengxingming: chengyuan.xueshengxingming }
          }

          this.$http({
            url: 'jingsai/biangueng/apply',
            method: 'post',
            data: {
              tuanduiId: this.dataForm.tuanduiId,
              tuanduiBianhao: this.dataForm.tuanduiBianhao,
              jingsaiId: this.dataForm.jingsaiId,
              bianguengLeixing: this.dataForm.bianguengLeixing,
              caozuoHou: JSON.stringify(caozuoHou),
              caozuoYuanyin: this.dataForm.caozuoYuanyin,
              shenheZhuangtai: '审核中'
            }
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.$message.success('申请提交成功，等待审核')
              this.visible = false
              this.$emit('refreshDataList')
            } else {
              this.$message.error(data.msg)
            }
          }).catch(err => {
            console.error('提交申请失败:', err)
            this.$message.error('提交失败，请重试')
          })
        }
      })
    }
  }
}
</script>

<style>
/* 自定义下拉列表样式 - 仅适用于人员变更对话框 */
.custom-select-wrapper {
  position: relative;
  width: 100%;
}

.custom-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: #fff;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 9999;
  margin-top: 4px;
  padding: 10px;
  max-height: 300px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.custom-dropdown-list {
  flex: 1;
  overflow-y: auto;
  max-height: 250px;
}

.custom-dropdown-item {
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 4px;
  font-size: 14px;
  color: #606266;
}

.custom-dropdown-item:hover {
  background-color: #f5f7fa;
  color: #409eff;
}

.custom-dropdown-empty {
  text-align: center;
  color: #c0c4cc;
  padding: 20px;
  font-size: 14px;
}

/* 滚动条样式 */
.custom-dropdown-list::-webkit-scrollbar {
  width: 6px;
}

.custom-dropdown-list::-webkit-scrollbar-thumb {
  background-color: #dcdfe6;
  border-radius: 3px;
}

.custom-dropdown-list::-webkit-scrollbar-track {
  background-color: #f5f7fa;
}
</style>
