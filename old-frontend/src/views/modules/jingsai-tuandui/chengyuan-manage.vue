<template>
  <el-dialog title="团队成员管理" :visible.sync="visible" width="900px" :close-on-click-modal="false">
    <div v-if="tuanduiInfo">
      <el-descriptions :column="3" border style="margin-bottom: 20px">
        <el-descriptions-item label="团队编号">{{ tuanduiInfo.tuanduiBianhao }}</el-descriptions-item>
        <el-descriptions-item label="团队名称">{{ tuanduiInfo.tuanduiMingcheng }}</el-descriptions-item>
        <el-descriptions-item label="负责人">{{ tuanduiInfo.fuzerenXingming }}</el-descriptions-item>
        <el-descriptions-item label="竞赛">{{ tuanduiInfo.jingsaimingcheng }}</el-descriptions-item>
        <el-descriptions-item label="赛道">{{ tuanduiInfo.saidaoMingcheng }}</el-descriptions-item>
        <el-descriptions-item label="当前人数">{{ tuanduiInfo.chengyuanRenshu || 0 }}</el-descriptions-item>
      </el-descriptions>

      <el-table :data="chengyuanList" border stripe>
        <el-table-column label="序号" type="index" width="60" />
        <el-table-column prop="xueshengxingming" label="学生姓名" width="120"></el-table-column>
        <el-table-column prop="xuehao" label="学号" width="130"></el-table-column>
        <el-table-column prop="juese" label="角色" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.juese === '负责人' ? 'danger' : 'info'">{{ scope.row.juese }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="xueyuan" label="学院"></el-table-column>
        <el-table-column prop="zhuanye" label="专业" width="150"></el-table-column>
        <el-table-column prop="isActive" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isActive === '是' ? 'success' : 'info'" size="small">{{ scope.row.isActive }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  data() {
    return {
      visible: false,
      tuanduiInfo: null,
      chengyuanList: []
    }
  },
  methods: {
    init(tuandui) {
      this.tuanduiInfo = tuandui
      this.visible = true
      this.getChengyuanList(tuandui.id)
    },
    getChengyuanList(tuanduiId) {
      this.$http({
        url: `jingsai/tuandui/tuanduichengyuan/list`,
        method: 'get',
        params: { tuanduiId: tuanduiId }
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.chengyuanList = data.data || []
          console.log('团队成员列表加载成功:', this.chengyuanList.length, '条')
        } else {
          console.error('团队成员列表加载失败:', data)
        }
      }).catch(err => {
        console.error('获取团队成员列表失败:', err)
      })
    }
  }
}
</script>
