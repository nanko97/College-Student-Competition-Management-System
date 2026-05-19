<template>
  <div class="addEdit-block">
    <el-form
      class="detail-form-content"
      ref="ruleForm"
      :model="ruleForm"
      :rules="rules"
      label-width="80px"
	  :style="{backgroundColor:addEditForm.addEditBoxColor}"
    >
      <el-row>
      <el-col :span="12">
        <el-form-item class="input" v-if="type!='info'"  label="竞赛名称" prop="jingsaimingcheng">
          <el-input v-model="ruleForm.jingsaimingcheng" 
              placeholder="竞赛名称" clearable  :readonly="ro.jingsaimingcheng"></el-input>
        </el-form-item>
        <div v-else>
          <el-form-item class="input" label="竞赛名称" prop="jingsaimingcheng">
              <el-input v-model="ruleForm.jingsaimingcheng" 
                placeholder="竞赛名称" readonly></el-input>
          </el-form-item>
        </div>
      </el-col>
      <el-col :span="12">
        <el-form-item class="input" v-if="type!='info'"  label="竞赛类型" prop="jingsaileixing">
          <el-input v-model="ruleForm.jingsaileixing" 
              placeholder="竞赛类型" clearable  :readonly="ro.jingsaileixing"></el-input>
        </el-form-item>
        <div v-else>
          <el-form-item class="input" label="竞赛类型" prop="jingsaileixing">
              <el-input v-model="ruleForm.jingsaileixing" 
                placeholder="竞赛类型" readonly></el-input>
          </el-form-item>
        </div>
      </el-col>
      <el-col :span="12">
        <el-form-item class="input" v-if="type!='info'"  label="竞赛地点" prop="jingsaididian">
          <el-input v-model="ruleForm.jingsaididian" 
              placeholder="竞赛地点" clearable  :readonly="ro.jingsaididian"></el-input>
        </el-form-item>
        <div v-else>
          <el-form-item class="input" label="竞赛地点" prop="jingsaididian">
              <el-input v-model="ruleForm.jingsaididian" 
                placeholder="竞赛地点" readonly></el-input>
          </el-form-item>
        </div>
      </el-col>
      <el-col :span="12">
        <el-form-item class="date" v-if="type!='info'" label="竞赛时间" prop="jingsaishijian">
            <el-date-picker
                value-format="yyyy-MM-dd HH:mm:ss"
                v-model="ruleForm.jingsaishijian" 
                type="datetime"
                placeholder="竞赛时间">
            </el-date-picker>
        </el-form-item>
        <div v-else>
          <el-form-item class="input" v-if="ruleForm.jingsaishijian" label="竞赛时间" prop="jingsaishijian">
              <el-input v-model="ruleForm.jingsaishijian" 
                placeholder="竞赛时间" readonly></el-input>
          </el-form-item>
        </div>
      </el-col>
      <el-col :span="12">
        <el-form-item class="select" v-if="type!='info'"  label="模式" prop="moshi">
          <el-select v-model="ruleForm.moshi" placeholder="请选择模式">
            <el-option
                v-for="(item,index) in moshiOptions"
                v-bind:key="index"
                :label="item"
                :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        <div v-else>
          <el-form-item class="input" label="模式" prop="moshi">
	      <el-input v-model="ruleForm.moshi"
                placeholder="模式" readonly></el-input>
          </el-form-item>
        </div>
      </el-col>
      <el-col :span="24">  
        <el-form-item class="upload" v-if="type!='info' && !ro.fengmiantupian" label="封面图片" prop="fengmiantupian">
          <file-upload
          tip="点击上传封面图片"
          action="file/upload"
          :limit="3"
          :multiple="true"
          :fileUrls="ruleForm.fengmiantupian?ruleForm.fengmiantupian:''"
          @change="fengmiantupianUploadChange"
          ></file-upload>
        </el-form-item>
        <div v-else>
          <el-form-item v-if="ruleForm.fengmiantupian" label="封面图片" prop="fengmiantupian">
            <img style="margin-right:20px;" v-bind:key="index" v-for="(item,index) in ruleForm.fengmiantupian.split(',')" :src="$imageUrl(item)" width="100" height="100">
          </el-form-item>
        </div>
      </el-col>
      <el-col :span="12">
        <el-form-item class="input" v-if="type!='info'"  label="工号" prop="gonghao">
          <el-input v-model="ruleForm.gonghao" 
              placeholder="工号" clearable  :readonly="ro.gonghao"></el-input>
        </el-form-item>
        <div v-else>
          <el-form-item class="input" label="工号" prop="gonghao">
              <el-input v-model="ruleForm.gonghao" 
                placeholder="工号" readonly></el-input>
          </el-form-item>
        </div>
      </el-col>
      <el-col :span="12">
        <el-form-item class="input" v-if="type!='info'"  label="教师姓名" prop="jiaoshixingming">
          <el-input v-model="ruleForm.jiaoshixingming" 
              placeholder="教师姓名" clearable  :readonly="ro.jiaoshixingming"></el-input>
        </el-form-item>
        <div v-else>
          <el-form-item class="input" label="教师姓名" prop="jiaoshixingming">
              <el-input v-model="ruleForm.jiaoshixingming" 
                placeholder="教师姓名" readonly></el-input>
          </el-form-item>
        </div>
      </el-col>
      </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item class="textarea" v-if="type!='info'" label="竞赛规则" prop="jingsaiguize">
                <el-input
                  style="min-width: 200px; max-width: 600px;"
                  type="textarea"
                  :rows="8"
                  placeholder="竞赛规则"
                  v-model="ruleForm.jingsaiguize" >
                </el-input>
              </el-form-item>
              <div v-else>
                <el-form-item label="竞赛规则" prop="jingsaiguize">
                    <div class="detail-textarea">{{ruleForm.jingsaiguize || '暂无内容'}}</div>
                </el-form-item>
              </div>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item class="textarea" v-if="type!='info'" label="竞赛奖励" prop="jingsaijiangli">
                <el-input
                  style="min-width: 200px; max-width: 600px;"
                  type="textarea"
                  :rows="8"
                  placeholder="竞赛奖励"
                  v-model="ruleForm.jingsaijiangli" >
                </el-input>
              </el-form-item>
              <div v-else>
                <el-form-item label="竞赛奖励" prop="jingsaijiangli">
                    <div class="detail-textarea">{{ruleForm.jingsaijiangli || '暂无内容'}}</div>
                </el-form-item>
              </div>
            </el-col>
          </el-row>
      <el-form-item class="btn">
        <el-button v-if="type!='info'" type="primary" class="btn-success" @click="onSubmit">提交</el-button>
        <el-button v-if="type!='info'" class="btn-close" @click="back()">取消</el-button>
        <el-button v-if="type=='info'" class="btn-close" @click="back()">返回</el-button>
      </el-form-item>
    </el-form>
    

  </div>
</template>
<script>
// 数字，邮件，手机，url，身份证校验
import { isNumber,isIntNumer,isEmail,isPhone, isMobile,isURL,checkIdCard } from "@/utils/validate";
export default {
  data() {
    let self = this
    var validateIdCard = (rule, value, callback) => {
      if(!value){
        callback();
      } else if (!checkIdCard(value)) {
        callback(new Error("请输入正确的身份证号码"));
      } else {
        callback();
      }
    };
    var validateUrl = (rule, value, callback) => {
      if(!value){
        callback();
      } else if (!isURL(value)) {
        callback(new Error("请输入正确的URL地址"));
      } else {
        callback();
      }
    };
    var validateMobile = (rule, value, callback) => {
      if(!value){
        callback();
      } else if (!isMobile(value)) {
        callback(new Error("请输入正确的手机号码"));
      } else {
        callback();
      }
    };
    var validatePhone = (rule, value, callback) => {
      if(!value){
        callback();
      } else if (!isPhone(value)) {
        callback(new Error("请输入正确的电话号码"));
      } else {
        callback();
      }
    };
    var validateEmail = (rule, value, callback) => {
      if(!value){
        callback();
      } else if (!isEmail(value)) {
        callback(new Error("请输入正确的邮箱地址"));
      } else {
        callback();
      }
    };
    var validateNumber = (rule, value, callback) => {
      if(!value){
        callback();
      } else if (!isNumber(value)) {
        callback(new Error("请输入数字"));
      } else {
        callback();
      }
    };
    var validateIntNumber = (rule, value, callback) => {
      if(!value){
        callback();
      } else if (!isIntNumer(value)) {
        callback(new Error("请输入整数"));
      } else {
        callback();
      }
    };
    return {
	  addEditForm: {"btnSaveFontColor":"#ffffff","selectFontSize":"14px","btnCancelBorderColor":"#dcdfe6","inputBorderRadius":"8px","inputFontSize":"14px","textareaBgColor":"#f5f7fa","btnSaveFontSize":"14px","textareaBorderRadius":"8px","uploadBgColor":"#f5f7fa","textareaBorderStyle":"solid","btnCancelWidth":"100px","textareaHeight":"120px","dateBgColor":"#f5f7fa","btnSaveBorderRadius":"8px","uploadLableFontSize":"14px","textareaBorderWidth":"1px","inputLableColor":"#606266","addEditBoxColor":"#ffffff","dateIconFontSize":"14px","btnSaveBgColor":"#409eff","uploadIconFontColor":"#8c939d","textareaBorderColor":"#dcdfe6","btnCancelBgColor":"#f5f7fa","selectLableColor":"#606266","btnSaveBorderStyle":"solid","dateBorderWidth":"1px","dateLableFontSize":"14px","dateBorderRadius":"8px","btnCancelBorderStyle":"solid","selectLableFontSize":"14px","selectBorderStyle":"solid","selectIconFontColor":"#c0c4cc","btnCancelHeight":"40px","inputHeight":"40px","btnCancelFontColor":"#606266","dateBorderColor":"#dcdfe6","dateIconFontColor":"#c0c4cc","uploadBorderStyle":"solid","dateBorderStyle":"solid","dateLableColor":"#606266","dateFontSize":"14px","inputBorderWidth":"1px","uploadIconFontSize":"28px","selectHeight":"40px","inputFontColor":"#303133","uploadHeight":"148px","textareaLableColor":"#606266","textareaLableFontSize":"14px","btnCancelFontSize":"14px","inputBorderStyle":"solid","btnCancelBorderRadius":"8px","inputBgColor":"#f5f7fa","inputLableFontSize":"14px","uploadLableColor":"#606266","uploadBorderRadius":"8px","btnSaveHeight":"40px","selectBgColor":"#f5f7fa","btnSaveWidth":"100px","selectIconFontSize":"14px","dateHeight":"40px","selectBorderColor":"#dcdfe6","inputBorderColor":"#dcdfe6","uploadBorderColor":"#dcdfe6","textareaFontColor":"#303133","selectBorderWidth":"1px","dateFontColor":"#303133","btnCancelBorderWidth":"1px","uploadBorderWidth":"1px","textareaFontSize":"14px","selectBorderRadius":"8px","selectFontColor":"#303133","btnSaveBorderColor":"#409eff","btnSaveBorderWidth":"1px"},
      id: '',
      type: '',
      ro:{
	jingsaimingcheng : false,
	jingsaileixing : false,
	jingsaididian : false,
	jingsaiguize : false,
	jingsaijiangli : false,
	jingsaishijian : false,
	moshi : false,
	fengmiantupian : false,
	gonghao : false,
	jiaoshixingming : false,
      },
      ruleForm: {
        jingsaimingcheng: '',
        jingsaileixing: '',
        jingsaididian: '',
        jingsaiguize: '',
        jingsaijiangli: '',
        jingsaishijian: '',
        moshi: '',
        fengmiantupian: '',
        gonghao: '',
        jiaoshixingming: '',
      },
          moshiOptions: [],
      rules: {
          jingsaimingcheng: [
          ],
          jingsaileixing: [
          ],
          jingsaididian: [
          ],
          jingsaiguize: [
          ],
          jingsaijiangli: [
          ],
          jingsaishijian: [
          ],
          moshi: [
          ],
          fengmiantupian: [
          ],
          gonghao: [
          ],
          jiaoshixingming: [
          ],
      }
    };
  },
  props: ["parent"],
  computed: {
  },
  created() {
	this.addEditStyleChange()
	this.addEditUploadStyleChange()
  },
  methods: {
    // 下载
    download(file){
      window.open(`${file}`)
    },
    // 初始化
    init(id,type) {
      if (id) {
        this.id = id;
        this.type = type;
      }
      if(this.type=='info'||this.type=='else'){
        this.info(id);
      }else if(this.type=='cross'){
        var obj = this.$storage.getObj('crossObj');
        for (var o in obj){
          if(o=='jingsaimingcheng'){
            this.ruleForm.jingsaimingcheng = obj[o];
	    this.ro.jingsaimingcheng = true;
            continue;
          }
          if(o=='jingsaileixing'){
            this.ruleForm.jingsaileixing = obj[o];
	    this.ro.jingsaileixing = true;
            continue;
          }
          if(o=='jingsaididian'){
            this.ruleForm.jingsaididian = obj[o];
	    this.ro.jingsaididian = true;
            continue;
          }
          if(o=='jingsaiguize'){
            this.ruleForm.jingsaiguize = obj[o];
	    this.ro.jingsaiguize = true;
            continue;
          }
          if(o=='jingsaijiangli'){
            this.ruleForm.jingsaijiangli = obj[o];
	    this.ro.jingsaijiangli = true;
            continue;
          }
          if(o=='jingsaishijian'){
            this.ruleForm.jingsaishijian = obj[o];
	    this.ro.jingsaishijian = true;
            continue;
          }
          if(o=='moshi'){
            this.ruleForm.moshi = obj[o];
	    this.ro.moshi = true;
            continue;
          }
          if(o=='fengmiantupian'){
            this.ruleForm.fengmiantupian = obj[o];
	    this.ro.fengmiantupian = true;
            continue;
          }
          if(o=='gonghao'){
            this.ruleForm.gonghao = obj[o];
	    this.ro.gonghao = true;
            continue;
          }
          if(o=='jiaoshixingming'){
            this.ruleForm.jiaoshixingming = obj[o];
	    this.ro.jiaoshixingming = true;
            continue;
          }
        }
      }
      // 获取用户信息（只在新增/编辑时自动填充，查看详情时不填充）
      if(this.type != 'info') {
        const sessionTable = this.$storage.get('sessionTable');
        if (!sessionTable) {
          console.error('sessionTable 为空，无法获取用户信息');
          this.$message.error('请先登录');
          return;
        }
        
        this.$http({
          url: `${sessionTable}/session`,
          method: "get"
        }).then(({ data }) => {
          if (data && data.code === 0) {
            var json = data.data;
            if(json.gonghao != '' && json.gonghao) {
              this.ruleForm.gonghao = json.gonghao;
              console.log('自动填充工号:', json.gonghao);
            }
            if(json.jiaoshixingming != '' && json.jiaoshixingming) {
              this.ruleForm.jiaoshixingming = json.jiaoshixingming;
              console.log('自动填充教师姓名:', json.jiaoshixingming);
            }
          } else {
            console.error('获取用户信息失败:', data.msg);
            this.$message.error(data.msg || '获取用户信息失败');
          }
        }).catch(error => {
          console.error('获取用户信息异常:', error);
          this.$message.error('获取用户信息失败，请重新登录');
        });
      }
            this.moshiOptions = "付费,免费".split(',')
    },
    // 多级联动参数
    info(id) {
      this.$http({
        url: `jingsaixinxi/info/${id}`,
        method: "get"
      }).then(({ data }) => {
        if (data && data.code === 0) {
        this.ruleForm = data.data;
	//解决前台上传图片后台不显示的问题
	let reg=new RegExp('../../../upload','g')//g代表全部
        } else {
          this.$message.error(data.msg);
        }
      });
    },
    // 提交
    onSubmit() {










      this.$refs["ruleForm"].validate(valid => {
        if (valid) {
          this.$http({
            url: `jingsaixinxi/${!this.ruleForm.id ? "save" : "update"}`,
            method: "post",
            data: this.ruleForm
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.parent.showFlag = true;
                  this.parent.addOrUpdateFlag = false;
                  this.parent.jingsaixinxiCrossAddOrUpdateFlag = false;
                  this.parent.search();
                  this.parent.contentStyleChange();
                }
              });
            } else {
              this.$message.error(data.msg);
            }
          });
        }
      });
    },
    // 获取uuid
    getUUID () {
      return new Date().getTime();
    },
    // 返回
    back() {
      if (this.parent) {
        this.parent.showFlag = true;
        this.parent.addOrUpdateFlag = false;
        if (this.parent.jingsaixinxiCrossAddOrUpdateFlag !== undefined) {
          this.parent.jingsaixinxiCrossAddOrUpdateFlag = false;
        }
        // 调用父组件的contentStyleChange方法（如果存在）
        if (typeof this.parent.contentStyleChange === 'function') {
          this.parent.contentStyleChange();
        }
      }
    },
    fengmiantupianUploadChange(fileUrls) {
	this.ruleForm.fengmiantupian = fileUrls;
			this.addEditUploadStyleChange()
    },
	addEditStyleChange() {
	  this.$nextTick(()=>{
	    // input
	    document.querySelectorAll('.addEdit-block .input .el-input__inner').forEach(el=>{
	      el.style.height = this.addEditForm.inputHeight
	      el.style.color = this.addEditForm.inputFontColor
	      el.style.fontSize = this.addEditForm.inputFontSize
	      el.style.borderWidth = this.addEditForm.inputBorderWidth
	      el.style.borderStyle = this.addEditForm.inputBorderStyle
	      el.style.borderColor = this.addEditForm.inputBorderColor
	      el.style.borderRadius = this.addEditForm.inputBorderRadius
	      el.style.backgroundColor = this.addEditForm.inputBgColor
	    })
	    document.querySelectorAll('.addEdit-block .input .el-form-item__label').forEach(el=>{
	      el.style.lineHeight = this.addEditForm.inputHeight
	      el.style.color = this.addEditForm.inputLableColor
	      el.style.fontSize = this.addEditForm.inputLableFontSize
	    })
	    // select
	    document.querySelectorAll('.addEdit-block .select .el-input__inner').forEach(el=>{
	      el.style.height = this.addEditForm.selectHeight
	      el.style.color = this.addEditForm.selectFontColor
	      el.style.fontSize = this.addEditForm.selectFontSize
	      el.style.borderWidth = this.addEditForm.selectBorderWidth
	      el.style.borderStyle = this.addEditForm.selectBorderStyle
	      el.style.borderColor = this.addEditForm.selectBorderColor
	      el.style.borderRadius = this.addEditForm.selectBorderRadius
	      el.style.backgroundColor = this.addEditForm.selectBgColor
	    })
	    document.querySelectorAll('.addEdit-block .select .el-form-item__label').forEach(el=>{
	      el.style.lineHeight = this.addEditForm.selectHeight
	      el.style.color = this.addEditForm.selectLableColor
	      el.style.fontSize = this.addEditForm.selectLableFontSize
	    })
	    document.querySelectorAll('.addEdit-block .select .el-select__caret').forEach(el=>{
	      el.style.color = this.addEditForm.selectIconFontColor
	      el.style.fontSize = this.addEditForm.selectIconFontSize
	    })
	    // date
	    document.querySelectorAll('.addEdit-block .date .el-input__inner').forEach(el=>{
	      el.style.height = this.addEditForm.dateHeight
	      el.style.color = this.addEditForm.dateFontColor
	      el.style.fontSize = this.addEditForm.dateFontSize
	      el.style.borderWidth = this.addEditForm.dateBorderWidth
	      el.style.borderStyle = this.addEditForm.dateBorderStyle
	      el.style.borderColor = this.addEditForm.dateBorderColor
	      el.style.borderRadius = this.addEditForm.dateBorderRadius
	      el.style.backgroundColor = this.addEditForm.dateBgColor
	    })
	    document.querySelectorAll('.addEdit-block .date .el-form-item__label').forEach(el=>{
	      el.style.lineHeight = this.addEditForm.dateHeight
	      el.style.color = this.addEditForm.dateLableColor
	      el.style.fontSize = this.addEditForm.dateLableFontSize
	    })
	    document.querySelectorAll('.addEdit-block .date .el-input__icon').forEach(el=>{
	      el.style.color = this.addEditForm.dateIconFontColor
	      el.style.fontSize = this.addEditForm.dateIconFontSize
	      el.style.lineHeight = this.addEditForm.dateHeight
	    })
	    // upload
	    let iconLineHeight = parseInt(this.addEditForm.uploadHeight) - parseInt(this.addEditForm.uploadBorderWidth) * 2 + 'px'
	    document.querySelectorAll('.addEdit-block .upload .el-upload--picture-card').forEach(el=>{
	      el.style.width = this.addEditForm.uploadHeight
	      el.style.height = this.addEditForm.uploadHeight
	      el.style.borderWidth = this.addEditForm.uploadBorderWidth
	      el.style.borderStyle = this.addEditForm.uploadBorderStyle
	      el.style.borderColor = this.addEditForm.uploadBorderColor
	      el.style.borderRadius = this.addEditForm.uploadBorderRadius
	      el.style.backgroundColor = this.addEditForm.uploadBgColor
	    })
	    document.querySelectorAll('.addEdit-block .upload .el-form-item__label').forEach(el=>{
	      el.style.lineHeight = this.addEditForm.uploadHeight
	      el.style.color = this.addEditForm.uploadLableColor
	      el.style.fontSize = this.addEditForm.uploadLableFontSize
	    })
	    document.querySelectorAll('.addEdit-block .upload .el-icon-plus').forEach(el=>{
	      el.style.color = this.addEditForm.uploadIconFontColor
	      el.style.fontSize = this.addEditForm.uploadIconFontSize
	      el.style.lineHeight = iconLineHeight
	      el.style.display = 'block'
	    })
	    // 多文本输入框
	    document.querySelectorAll('.addEdit-block .textarea .el-textarea__inner').forEach(el=>{
	      el.style.height = this.addEditForm.textareaHeight
	      el.style.color = this.addEditForm.textareaFontColor
	      el.style.fontSize = this.addEditForm.textareaFontSize
	      el.style.borderWidth = this.addEditForm.textareaBorderWidth
	      el.style.borderStyle = this.addEditForm.textareaBorderStyle
	      el.style.borderColor = this.addEditForm.textareaBorderColor
	      el.style.borderRadius = this.addEditForm.textareaBorderRadius
	      el.style.backgroundColor = this.addEditForm.textareaBgColor
	    })
	    document.querySelectorAll('.addEdit-block .textarea .el-form-item__label').forEach(el=>{
	      // el.style.lineHeight = this.addEditForm.textareaHeight
	      el.style.color = this.addEditForm.textareaLableColor
	      el.style.fontSize = this.addEditForm.textareaLableFontSize
	    })
	    // 保存
	    document.querySelectorAll('.addEdit-block .btn .btn-success').forEach(el=>{
	      el.style.width = this.addEditForm.btnSaveWidth
	      el.style.height = this.addEditForm.btnSaveHeight
	      el.style.color = this.addEditForm.btnSaveFontColor
	      el.style.fontSize = this.addEditForm.btnSaveFontSize
	      el.style.borderWidth = this.addEditForm.btnSaveBorderWidth
	      el.style.borderStyle = this.addEditForm.btnSaveBorderStyle
	      el.style.borderColor = this.addEditForm.btnSaveBorderColor
	      el.style.borderRadius = this.addEditForm.btnSaveBorderRadius
	      el.style.backgroundColor = this.addEditForm.btnSaveBgColor
	    })
	    // 返回
	    document.querySelectorAll('.addEdit-block .btn .btn-close').forEach(el=>{
	      el.style.width = this.addEditForm.btnCancelWidth
	      el.style.height = this.addEditForm.btnCancelHeight
	      el.style.color = this.addEditForm.btnCancelFontColor
	      el.style.fontSize = this.addEditForm.btnCancelFontSize
	      el.style.borderWidth = this.addEditForm.btnCancelBorderWidth
	      el.style.borderStyle = this.addEditForm.btnCancelBorderStyle
	      el.style.borderColor = this.addEditForm.btnCancelBorderColor
	      el.style.borderRadius = this.addEditForm.btnCancelBorderRadius
	      el.style.backgroundColor = this.addEditForm.btnCancelBgColor
	    })
	  })
	},
	addEditUploadStyleChange() {
		this.$nextTick(()=>{
		  document.querySelectorAll('.addEdit-block .upload .el-upload-list--picture-card .el-upload-list__item').forEach(el=>{
			el.style.width = this.addEditForm.uploadHeight
			el.style.height = this.addEditForm.uploadHeight
			el.style.borderWidth = this.addEditForm.uploadBorderWidth
			el.style.borderStyle = this.addEditForm.uploadBorderStyle
			el.style.borderColor = this.addEditForm.uploadBorderColor
			el.style.borderRadius = this.addEditForm.uploadBorderRadius
			el.style.backgroundColor = this.addEditForm.uploadBgColor
		  })
	  })
	},
  }
};
</script>
<style lang="scss">
.editor{
  height: 500px;
  
  &::v-deep .ql-container {
	  height: 310px;
  }
}
.amap-wrapper {
  width: 100%;
  height: 500px;
}
.search-box {
  position: absolute;
}
.addEdit-block {
	margin: -10px;
}
.detail-form-content {
	padding: 12px;
}
.btn .el-button {
  padding: 0;
}

/* 详情模式下的文本域样式 */
.detail-textarea {
  color: #303133 !important;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
  white-space: pre-wrap;
  min-height: 40px;
}
</style>
