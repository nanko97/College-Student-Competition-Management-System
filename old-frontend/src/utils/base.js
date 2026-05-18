const base = {
    get() {
        return {
            url: "http://localhost:9090/BYSJ_Springboot/",
            name: "BYSJ_Springboot",
            // 退出到首页链接
            indexUrl: 'http://localhost:9090/BYSJ_Springboot/front/index.html'
        };
    },
    getProjectName() {
        return {
            projectName: "大学生竞赛管理系统"
        }
    }
}
export default base
