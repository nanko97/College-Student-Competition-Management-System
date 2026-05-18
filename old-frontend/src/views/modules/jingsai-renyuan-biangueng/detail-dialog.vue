<template>
  <el-dialog title="变更详情" :visible.sync="visible" width="700px">
    <el-descriptions :column="2" border>
      <el-descriptions-item label="团队编号">{{ detailInfo.tuanduiBianhao }}</el-descriptions-item>
      <el-descriptions-item label="变更类型">
        <el-tag :type="getLeixingType(detailInfo.bianguengLeixing)">{{ detailInfo.bianguengLeixing }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="操作人">{{ detailInfo.caozuoRenXingming }}</el-descriptions-item>
      <el-descriptions-item label="操作人学号">{{ detailInfo.caozuoRenXuehao }}</el-descriptions-item>
      <el-descriptions-item label="审核状态">
        <el-tag :type="getZhuangtaiType(detailInfo.shenheZhuangtai)">{{ detailInfo.shenheZhuangtai }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="审核人">{{ detailInfo.shenheRen || '-' }}</el-descriptions-item>
      <el-descriptions-item label="审核时间" :span="2">{{ detailInfo.shenheShijian || '-' }}</el-descriptions-item>
      <el-descriptions-item label="变更原因" :span="2">{{ detailInfo.caozuoYuanyin }}</el-descriptions-item>
    </el-descriptions>

    <div v-if="detailInfo.caozuoQian" style="margin-top: 20px">
      <h4>变更前数据</h4>
      <pre style="background: #f5f7fa; padding: 10px; border-radius: 4px">{{ formatJson(detailInfo.caozuoQian) }}</pre>
    </div>
    <div v-if="detailInfo.caozuoHou" style="margin-top: 20px">
      <h4>变更后数据</h4>
      <pre style="background: #f5f7fa; padding: 10px; border-radius: 4px">{{ formatJson(detailInfo.caozuoHou) }}</pre>
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
      detailInfo: {}
    }
  },
  methods: {
    init(row) {
      this.detailInfo = Object.assign({}, row)
      this.visible = true
    },
    getLeixingType(leixing) {
      if (leixing === '添加成员') return 'success'
      if (leixing === '移除成员') return 'warning'
      return 'danger'
    },
    getZhuangtaiType(zhuangtai) {
      if (zhuangtai === '已通过') return 'success'
      if (zhuangtai === '已驳回') return 'danger'
      return 'warning'
    },
    formatJson(jsonStr) {
      try {
        return JSON.stringify(JSON.parse(jsonStr), null, 2)
      } catch (e) {
        return jsonStr
      }
    }
  }
}
</script>
