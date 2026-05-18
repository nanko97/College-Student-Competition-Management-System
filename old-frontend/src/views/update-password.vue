<template>
  <div>
    <el-form
      class="detail-form-content"
      ref="ruleForm"
      :rules="rules"
      :model="ruleForm"
      label-width="80px"
    >
      <el-form-item label="原密码" prop="password">
        <el-input v-model="ruleForm.password" show-password></el-input>
      </el-form-item>
      <el-form-item label="新密码" prop="newpassword">
        <el-input v-model="ruleForm.newpassword" show-password></el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="repassword">
        <el-input v-model="ruleForm.repassword" show-password></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onUpdateHandler">确 定</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
export default {
  data() {
    return {
      dialogVisible: false,
      ruleForm: {},
      user: {},
      rules: {
        password: [
          {
            required: true,
            message: "密码不能为空",
            trigger: "blur"
          }
        ],
        newpassword: [
          {
            required: true,
            message: "新密码不能为空",
            trigger: "blur"
          }
        ],
        repassword: [
          {
            required: true,
            message: "确认密码不能为空",
            trigger: "blur"
          }
        ]
      }
    };
  },
  mounted() {
    this.$http({
      url: `${this.$storage.get("sessionTable")}/session`,
      method: "get"
    }).then(({ data }) => {
      if (data && data.code === 0) {
        this.user = data.data;
      } else {
        this.$message.error(data.msg);
      }
    });
  },
  methods: {
    onLogout() {
      this.$storage.remove("Token");
      this.$router.replace({ name: "login" });
    },
    // 修改密码
    onUpdateHandler() {
      this.$refs["ruleForm"].validate(valid => {
        if (valid) {
          if (this.ruleForm.newpassword != this.ruleForm.repassword) {
            this.$message.error("两次密码输入不一致");
            return;
          }
          
          // 调用后端 API 验证原密码
          this.$http({
            url: `${this.$storage.get("sessionTable")}/verifyPassword`,
            method: "post",
            data: { oldPassword: this.ruleForm.password }
          }).then(({ data }) => {
            if (data && data.code === 0) {
              // 原密码验证成功，更新新密码
              this.user.password = this.ruleForm.newpassword;
              this.user.mima = this.ruleForm.newpassword;
              this.$http({
                url: `${this.$storage.get("sessionTable")}/update`,
                method: "post",
                data: this.user
              }).then(({ data }) => {
                if (data && data.code === 0) {
                  this.$message({
                    message: "修改密码成功，下次登录生效",
                    type: "success",
                    duration: 1500,
                    onClose: () => {
                    }
                  });
                } else {
                  this.$message.error(data.msg);
                }
              });
            } else {
              this.$message.error(data.msg || "原密码错误");
            }
          }).catch(() => {
            this.$message.error("验证失败，请重试");
          });
        }
      });
    }
  }
};
</script>
<style lang="scss" scoped>
</style>
