<template>
  <el-dialog title="缴费凭证" :visible.sync="visible" width="700px">
    <el-descriptions :column="2" border style="margin-bottom: 20px">
      <el-descriptions-item label="竞赛名称">{{ jiaofeiInfo.jingsaimingcheng }}</el-descriptions-item>
      <el-descriptions-item label="应缴金额">{{ jiaofeiInfo.baomingfei }} 元</el-descriptions-item>
      <el-descriptions-item label="缴费状态">
        <el-tag :type="getZhuangtaiType(jiaofeiInfo.jiaofeiZhuangtai)">{{ jiaofeiInfo.jiaofeiZhuangtai }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="缴费时间">{{ jiaofeiInfo.jiaofeiShijian || '-' }}</el-descriptions-item>
      <el-descriptions-item label="审核意见" :span="2">{{ jiaofeiInfo.shenheYijian || '-' }}</el-descriptions-item>
    </el-descriptions>
    <div v-if="jiaofeiInfo.pingzhengTupian" style="text-align: center">
      <img :src="$imageUrl(jiaofeiInfo.pingzhengTupian)" alt="缴费凭证" style="max-width: 100%; max-height: 400px; border-radius: 8px">
    </div>
    <div v-else style="text-align: center; color: #999; padding: 40px">
      暂无凭证图片
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
      jiaofeiInfo: {}
    }
  },
  methods: {
    init(jiaofei) {
      this.jiaofeiInfo = Object.assign({}, jiaofei)
      this.visible = true
    },
    getZhuangtaiType(zhuangtai) {
      if (zhuangtai === '已通过') return 'success'
      if (zhuangtai === '已驳回') return 'danger'
      if (zhuangtai === '待审核') return 'warning'
      return 'info'
    }
  }
}
</script>
