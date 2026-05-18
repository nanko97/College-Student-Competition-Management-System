const menu = {
    list() {
        return [{
            "backMenu": [{
                "child": [{
                    "buttons": ["新增", "查看", "修改", "删除"],
                    "menu": "学生",
                    "menuJump": "列表",
                    "tableName": "xuesheng"
                }], "menu": "学生管理"
            }, {
                "child": [{
                    "buttons": ["新增", "查看", "修改", "删除"],
                    "menu": "教师",
                    "menuJump": "列表",
                    "tableName": "jiaoshi"
                }], "menu": "教师管理"
            }, {
                "child": [{"buttons": ["查看", "删除"], "menu": "竞赛信息", "menuJump": "列表", "tableName": "jingsaixinxi"}],
                "menu": "竞赛信息管理"
            }, {
                "child": [{"buttons": ["查看", "删除"], "menu": "竞赛报名", "menuJump": "列表", "tableName": "jingsaibaoming"}],
                "menu": "竞赛报名管理"
            }, {
                "child": [
                    {"buttons": ["新增", "查看", "修改", "删除"], "menu": "费用配置", "menuJump": "列表", "tableName": "jingsai_feiyong"},
                    {"buttons": ["查看"], "menu": "缴费记录", "menuJump": "列表", "tableName": "jingsai_jiaofei"},
                    {"buttons": ["查看", "审核"], "menu": "缴费审核", "menuJump": "列表", "tableName": "jingsai_jiaofei_shenhe"}
                ],
                "menu": "费用管理"
            }, {
                "child": [
                    {"buttons": ["新增", "查看", "修改", "删除"], "menu": "晋级关系", "menuJump": "列表", "tableName": "jingsai_jinji_guanxi"},
                    {"buttons": ["查看", "审核"], "menu": "晋级审核", "menuJump": "列表", "tableName": "jingsai_jinji_shenhe"}
                ],
                "menu": "晋级管理"
            }, {
                "child": [
                    {"buttons": ["新增", "查看", "修改", "删除"], "menu": "级别版本", "menuJump": "列表", "tableName": "jingsai_jibie_banben"}
                ],
                "menu": "级别认定管理"
            }, {
                "child": [
                    {"buttons": ["新增", "查看", "修改", "删除"], "menu": "赛道管理", "menuJump": "列表", "tableName": "jingsai_saidao"}
                ],
                "menu": "赛道分组管理"
            }, {
                "child": [
                    {"buttons": ["查看"], "menu": "团队管理", "menuJump": "列表", "tableName": "jingsai_tuandui"}
                ],
                "menu": "团队管理"
            }, {
                "child": [
                    {"buttons": ["新增", "查看", "修改", "删除"], "menu": "作品打分", "menuJump": "列表", "tableName": "zuopindafen"}
                ],
                "menu": "作品打分管理"
            }, {
                "child": [
                    {"buttons": ["查看", "下载", "评分"], "menu": "作品管理", "menuJump": "列表", "tableName": "zuopin"}
                ],
                "menu": "作品管理"
            }, {
                "child": [
                    {"buttons": ["查看", "审核"], "menu": "成绩复核", "menuJump": "列表", "tableName": "zuopindafen-fuhe-shenhe"}
                ],
                "menu": "成绩复核管理"
            }, {
                "child": [
                    {"buttons": ["新增", "查看"], "menu": "人员变更申请", "menuJump": "列表", "tableName": "jingsai_renyuan_biangueng"},
                    {"buttons": ["查看", "审核"], "menu": "人员变更审核", "menuJump": "列表", "tableName": "jingsai_renyuan_biangueng_shenhe"}
                ],
                "menu": "人员变更管理"
            }],
            "frontMenu": [],
            "hasBackLogin": "是",
            "hasBackRegister": "否",
            "hasFrontLogin": "否",
            "hasFrontRegister": "否",
            "roleName": "管理员",
            "tableName": "users"
        }, {
            "backMenu": [{
                "child": [{
                    "buttons": ["查看", "报名"],
                    "menu": "竞赛信息",
                    "menuJump": "列表",
                    "tableName": "jingsaixinxi",
                    "routePath": "/jingsaixinxi"
                }], "menu": "竞赛信息管理"
            }, {
                "child": [{"buttons": ["查看"], "menu": "我的报名", "menuJump": "列表", "tableName": "jingsaibaoming", "routePath": "/jingsaibaoming"}],
                "menu": "竞赛报名管理"
            }, {
                "child": [{"buttons": ["查看", "上传凭证"], "menu": "我的缴费", "menuJump": "列表", "tableName": "xuesheng_my_jiaofei", "routePath": "/xuesheng-my-jiaofei"}],
                "menu": "我的缴费"
            }, {
                "child": [{"buttons": ["查看", "提交作品"], "menu": "我的作品", "menuJump": "列表", "tableName": "xuesheng_my_zuopin", "routePath": "/xuesheng-my-zuopin"}],
                "menu": "我的作品"
            }, {
                "child": [{"buttons": ["查看", "申请晋级"], "menu": "我的晋级", "menuJump": "列表", "tableName": "xuesheng_my_jinji", "routePath": "/xuesheng-my-jinji"}],
                "menu": "我的晋级"
            }, {
                "child": [{"buttons": ["查看", "成绩复核"], "menu": "我的成绩", "menuJump": "列表", "tableName": "zuopindafen", "routePath": "/zuopindafen"}],
                "menu": "我的成绩"
            }, {
                "child": [
                    {"buttons": ["查看", "申请加入"], "menu": "我的团队", "menuJump": "列表", "tableName": "xuesheng_my_tuandui", "routePath": "/xuesheng-my-tuandui"},
                    {"buttons": ["查看"], "menu": "申请加入团队", "menuJump": "列表", "tableName": "xuesheng_tuandui_join", "routePath": "/xuesheng-tuandui-join"},
                    {"buttons": ["新增"], "menu": "创建团队", "menuJump": "列表", "tableName": "xuesheng_tuandui_create", "routePath": "/xuesheng-tuandui-create"}
                ],
                "menu": "团队管理"
            }, {
                "child": [{"buttons": ["查看"], "menu": "我的复核", "menuJump": "列表", "tableName": "zuopindafen_my_fuhe", "routePath": "/zuopindafen-my-fuhe"}],
                "menu": "我的复核"
            }],
            "frontMenu": [],
            "hasBackLogin": "是",
            "hasBackRegister": "是",
            "hasFrontLogin": "否",
            "hasFrontRegister": "否",
            "roleName": "学生",
            "tableName": "xuesheng"
        }, {
            "backMenu": [{
                "child": [{
                    "buttons": ["新增", "查看", "修改", "删除", "查看评论"],
                    "menu": "竞赛信息",
                    "menuJump": "列表",
                    "tableName": "jingsaixinxi"
                }], "menu": "竞赛信息管理"
            }, {
                "child": [{
                    "buttons": ["查看", "审核", "评分", "删除"],
                    "menu": "竞赛报名",
                    "menuJump": "列表",
                    "tableName": "jingsaibaoming"
                }], "menu": "竞赛报名管理"
            }, {
                "child": [
                    {"buttons": ["查看", "修改", "删除"], "menu": "作品打分", "menuJump": "列表", "tableName": "zuopindafen"},
                    {"buttons": ["查看", "审核"], "menu": "成绩复核审核", "menuJump": "列表", "tableName": "zuopindafen_fuhe_shenhe"}
                ],
                "menu": "作品打分管理"
            }, {
                "child": [
                    {"buttons": ["查看", "下载", "评分"], "menu": "作品管理", "menuJump": "列表", "tableName": "zuopin"}
                ],
                "menu": "作品管理"
            }, {
                "child": [
                    {"buttons": ["查看"], "menu": "缴费记录", "menuJump": "列表", "tableName": "jingsai_jiaofei"},
                    {"buttons": ["查看", "审核"], "menu": "缴费审核", "menuJump": "列表", "tableName": "jingsai_jiaofei_shenhe"}
                ],
                "menu": "费用管理"
            }, {
                "child": [
                    {"buttons": ["查看"], "menu": "晋级关系", "menuJump": "列表", "tableName": "jingsai_jinji_guanxi"},
                    {"buttons": ["查看", "审核"], "menu": "晋级审核", "menuJump": "列表", "tableName": "jingsai_jinji_shenhe"}
                ],
                "menu": "晋级管理"
            }, {
                "child": [
                    {"buttons": ["查看"], "menu": "级别版本", "menuJump": "列表", "tableName": "jingsai_jibie_banben"}
                ],
                "menu": "级别认定管理"
            }, {
                "child": [
                    {"buttons": ["新增", "查看", "修改", "删除"], "menu": "赛道管理", "menuJump": "列表", "tableName": "jingsai_saidao"}
                ],
                "menu": "赛道分组管理"
            }, {
                "child": [
                    {"buttons": ["查看"], "menu": "团队管理", "menuJump": "列表", "tableName": "jingsai_tuandui"}
                ],
                "menu": "团队管理"
            }, {
                "child": [
                    {"buttons": ["查看"], "menu": "人员变更申请", "menuJump": "列表", "tableName": "jingsai_renyuan_biangueng"},
                    {"buttons": ["查看", "审核"], "menu": "人员变更审核", "menuJump": "列表", "tableName": "jingsai_renyuan_biangueng_shenhe"}
                ],
                "menu": "人员变更管理"
            }],
            "frontMenu": [],
            "hasBackLogin": "是",
            "hasBackRegister": "是",
            "hasFrontLogin": "否",
            "hasFrontRegister": "否",
            "roleName": "教师",
            "tableName": "jiaoshi"
        }]
    }
}
export default menu;
