<template>
  <div>
    <!-- 管理员角色切换 -->
    <div v-if="flag === 'users'" style="margin-bottom: 20px; text-align: center;">
      <el-tag type="info" size="medium" style="margin-right: 10px;">当前角色：</el-tag>
      <el-select v-model="targetRole" @change="onRoleChange" placeholder="选择要修改的角色" size="medium">
        <el-option
          v-for="item in roleOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value">
        </el-option>
      </el-select>
    </div>
    
    <el-form
      class="detail-form-content"
      ref="ruleForm"
      :model="ruleForm"
      label-width="80px"
    >  
     <el-row>
      <el-col :span="12">
        <el-form-item   v-if="flag=='xuesheng'"  label="学号" prop="xuehao">
          <el-input v-model="ruleForm.xuehao" readonly              placeholder="学号" clearable></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item   v-if="flag=='xuesheng'"  label="学生姓名" prop="xueshengxingming">
          <el-input v-model="ruleForm.xueshengxingming"               placeholder="学生姓名" clearable></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item v-if="flag=='xuesheng'"  label="性别" prop="xingbie">
          <el-select v-model="ruleForm.xingbie" placeholder="请选择性别">
            <el-option
                v-for="(item,index) in xueshengxingbieOptions"
                v-bind:key="index"
                :label="item"
                :value="item">
            </el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item   v-if="flag=='xuesheng'"  label="学院名称" prop="xueyuanmingcheng">
          <el-input v-model="ruleForm.xueyuanmingcheng"               placeholder="学院名称" clearable></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item v-if="flag=='xuesheng'"  label="班级" prop="banji">
          <el-select v-model="ruleForm.banji" placeholder="请选择班级">
            <el-option
                v-for="(item,index) in xueshengbanjiOptions"
                v-bind:key="index"
                :label="item"
                :value="item">
            </el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item   v-if="flag=='xuesheng'"  label="手机" prop="shouji">
          <el-input v-model="ruleForm.shouji"               placeholder="手机" clearable></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item   v-if="flag=='xuesheng'"  label="邮箱" prop="youxiang">
          <el-input v-model="ruleForm.youxiang"               placeholder="邮箱" clearable></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="24">  
        <el-form-item v-if="flag=='xuesheng'" label="照片" prop="zhaopian">
          <file-upload
          tip="点击上传照片（建议尺寸：200x200像素，支持jpg/png格式）"
          action="file/upload"
          :limit="1"
          :multiple="false"
          :fileUrls="ruleForm.zhaopian?ruleForm.zhaopian:''"
          @change="xueshengzhaopianUploadChange"
          ></file-upload>
          <div v-if="ruleForm.zhaopian" style="margin-top: 10px;">
            <img :src="$imageUrl(ruleForm.zhaopian)" width="120" height="120" style="border-radius: 8px; border: 2px solid #e4e7ed;" @error="handleImageError" v-show="imageUrlValid">
          </div>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item   v-if="flag=='jiaoshi'"  label="工号" prop="gonghao">
          <el-input v-model="ruleForm.gonghao" readonly              placeholder="工号" clearable></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item   v-if="flag=='jiaoshi'"  label="教师姓名" prop="jiaoshixingming">
          <el-input v-model="ruleForm.jiaoshixingming"               placeholder="教师姓名" clearable></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item v-if="flag=='jiaoshi'"  label="性别" prop="xingbie">
          <el-select v-model="ruleForm.xingbie" placeholder="请选择性别">
            <el-option
                v-for="(item,index) in jiaoshixingbieOptions"
                v-bind:key="index"
                :label="item"
                :value="item">
            </el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item   v-if="flag=='jiaoshi'"  label="学院名称" prop="xueyuanmingcheng">
          <el-input v-model="ruleForm.xueyuanmingcheng"               placeholder="学院名称" clearable></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item   v-if="flag=='jiaoshi'"  label="职称" prop="zhicheng">
          <el-input v-model="ruleForm.zhicheng"               placeholder="职称" clearable></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item   v-if="flag=='jiaoshi'"  label="手机" prop="shouji">
          <el-input v-model="ruleForm.shouji"               placeholder="手机" clearable></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item   v-if="flag=='jiaoshi'"  label="邮箱" prop="youxiang">
          <el-input v-model="ruleForm.youxiang"               placeholder="邮箱" clearable></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="24">  
        <el-form-item v-if="flag=='jiaoshi'" label="照片" prop="zhaopian">
          <file-upload
          tip="点击上传照片（建议尺寸：200x200像素，支持jpg/png格式）"
          action="file/upload"
          :limit="1"
          :multiple="false"
          :fileUrls="ruleForm.zhaopian?ruleForm.zhaopian:''"
          @change="jiaoshizhaopianUploadChange"
          ></file-upload>
          <div v-if="ruleForm.zhaopian" style="margin-top: 10px;">
            <img :src="$imageUrl(ruleForm.zhaopian)" width="120" height="120" style="border-radius: 8px; border: 2px solid #e4e7ed;" @error="handleImageError" v-show="imageUrlValid">
          </div>
        </el-form-item>
      </el-col>
      <!-- 管理员个人信息 -->
      <el-col :span="12">
        <el-form-item v-if="flag=='users'" label="用户名" prop="username">
          <el-input v-model="ruleForm.username" placeholder="请输入用户名" readonly></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item v-if="flag=='users'" label="头像" prop="touxiang">
          <file-upload
          tip="点击上传头像（建议尺寸：200x200像素，支持jpg/png格式）"
          action="file/upload"
          :limit="1"
          :multiple="false"
          :fileUrls="ruleForm.touxiang?ruleForm.touxiang:''"
          @change="userstouxiangUploadChange"
          ></file-upload>
          <div v-if="ruleForm.touxiang" style="margin-top: 10px;">
            <img :src="$imageUrl(ruleForm.touxiang)" width="120" height="120" style="border-radius: 8px; border: 2px solid #e4e7ed;" @error="handleImageError" v-show="imageUrlValid">
          </div>
        </el-form-item>
      </el-col>
      <el-col :span="24">
      <el-form-item>
        <el-button type="primary" @click="onUpdateHandler">修 改</el-button>
      </el-form-item>
      </el-col>
      </el-row>
    </el-form>
  </div>
</template>
<script>
// 数字，邮件，手机，url，身份证校验
import { isNumber,isIntNumer,isEmail,isMobile,isPhone,isURL,checkIdCard } from "@/utils/validate";

export default {
  data() {
    return {
      ruleForm: {},
      flag: '',
      usersFlag: false,
      xueshengxingbieOptions: [],
      xueshengbanjiOptions: [],
      jiaoshixingbieOptions: [],
      imageUrlValid: true,
      // 管理员可以选择要查看/修改的角色
      targetRole: '',
      roleOptions: [
        { value: 'xuesheng', label: '学生' },
        { value: 'jiaoshi', label: '教师' },
        { value: 'users', label: '管理员' }
      ]
    };
  },
  computed: {
    // 当前操作的角色（管理员使用 targetRole，其他角色使用 flag）
    currentRole() {
      return this.flag === 'users' ? this.targetRole : this.flag;
    }
  },
  mounted() {
    var table = this.$storage.get("sessionTable");
    this.flag = table;
    
    // 管理员默认查看自己的信息
    if (table === 'users') {
      this.targetRole = 'users';
      this.loadUserInfo('users');
    } else {
      // 学生和教师只能修改自己的信息
      this.loadUserInfo(table);
    }
    
    this.xueshengxingbieOptions = "男,女".split(',')
    this.jiaoshixingbieOptions = "男,女".split(',')
  },
  methods: {
    // 加载用户信息
    loadUserInfo(table) {
      this.$http({
        url: `${table}/session`,
        method: "get"
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.ruleForm = data.data;
          this.flag = table;
        } else {
          this.$message.error(data.msg);
        }
      });
    },
    // 管理员切换角色
    onRoleChange(role) {
      this.targetRole = role;
      this.loadUserInfo(role);
    },
    xueshengzhaopianUploadChange(fileUrls) {
        this.ruleForm.zhaopian = fileUrls;
    },
    jiaoshizhaopianUploadChange(fileUrls) {
        this.ruleForm.zhaopian = fileUrls;
    },
    userstouxiangUploadChange(fileUrls) {
        this.ruleForm.touxiang = fileUrls;
    },
    // 图片加载错误处理
    handleImageError(e) {
      console.error('图片加载失败:', e.target.src);
      // 设置默认图片
      e.target.src = this.$imageUrl('upload/人像.png');
      this.imageUrlValid = true;
    },
    onUpdateHandler() {
      // 管理员使用 targetRole，其他角色使用 flag
      const currentRole = this.flag === 'users' ? this.targetRole : this.flag;
      
      if((!this.ruleForm.xuehao)&& 'xuesheng'==currentRole){
        this.$message.error('学号不能为空');
        return
      }
      if((!this.ruleForm.mima)&& 'xuesheng'==currentRole){
        this.$message.error('密码不能为空');
        return
      }
      if((!this.ruleForm.xueshengxingming)&& 'xuesheng'==currentRole){
        this.$message.error('学生姓名不能为空');
        return
      }
      if( 'xuesheng' ==currentRole && this.ruleForm.shouji&&(!isMobile(this.ruleForm.shouji))){
        this.$message.error(`手机应输入手机格式`);
        return
      }
      if( 'xuesheng' ==currentRole && this.ruleForm.youxiang&&(!isEmail(this.ruleForm.youxiang))){
        this.$message.error(`邮箱应输入邮箱格式`);
        return
      }
      if((!this.ruleForm.gonghao)&& 'jiaoshi'==currentRole){
        this.$message.error('工号不能为空');
        return
      }
      if((!this.ruleForm.mima)&& 'jiaoshi'==currentRole){
        this.$message.error('密码不能为空');
        return
      }
      if((!this.ruleForm.jiaoshixingming)&& 'jiaoshi'==currentRole){
        this.$message.error('教师姓名不能为空');
        return
      }
      if( 'jiaoshi' ==currentRole && this.ruleForm.shouji&&(!isMobile(this.ruleForm.shouji))){
        this.$message.error(`手机应输入手机格式`);
        return
      }
      if( 'jiaoshi' ==currentRole && this.ruleForm.youxiang&&(!isEmail(this.ruleForm.youxiang))){
        this.$message.error(`邮箱应输入邮箱格式`);
        return
      }
      if('users'==currentRole && this.ruleForm.username.trim().length<1) {
	this.$message.error(`用户名不能为空`);
        return	
      }
      this.$http({
        url: `${currentRole}/update`,
        method: "post",
        data: this.ruleForm
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.$message({
            message: "修改信息成功",
            type: "success",
            duration: 1500,
            onClose: () => {
            }
          });
        } else {
          this.$message.error(data.msg);
        }
      });
    }
  }
};
</script>
<style lang="scss" scoped>
</style>
