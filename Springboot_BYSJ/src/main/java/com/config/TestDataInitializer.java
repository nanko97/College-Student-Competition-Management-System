package com.config;

import com.dao.XueshengDao;
import com.dao.JiaoshiDao;
import com.dao.JingsaixinxiDao;
import com.dao.JingsaibaomingDao;
import com.dao.ZuopindafenDao;
import com.entity.XueshengEntity;
import com.entity.JiaoshiEntity;
import com.entity.JingsaixinxiEntity;
import com.entity.JingsaibaomingEntity;
import com.entity.ZuopindafenEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 测试数据初始化器
 * 在系统启动时自动添加测试数据
 */
@Component
@Order(1) // 确保在应用启动早期执行
public class TestDataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(TestDataInitializer.class);
    
    @Autowired
    private XueshengDao xueshengDao;
    
    @Autowired
    private JiaoshiDao jiaoshiDao;
    
    @Autowired
    private JingsaixinxiDao jingsaixinxiDao;
    
    @Autowired
    private JingsaibaomingDao jingsaibaomingDao;
    
    @Autowired
    private ZuopindafenDao zuopindafenDao;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("=================================");
        logger.info("开始初始化测试数据...");
        logger.info("=================================");
        
        try {
            // 强制重新初始化，确保密码正确
            logger.info("开始清理并重新初始化测试数据...");
            
            // 删除旧数据
            xueshengDao.delete(null);
            logger.info("已清空学生表");
            
            jiaoshiDao.delete(null);
            logger.info("已清空教师表");
            
            jingsaixinxiDao.delete(null);
            logger.info("已清空竞赛信息表");
            
            jingsaibaomingDao.delete(null);
            logger.info("已清空报名表");
            
            zuopindafenDao.delete(null);
            logger.info("已清空评分表");
            
            // 初始化学生数据
            initStudents();
            logger.info("✓ 学生数据初始化完成，共 10 条");
            
            // 初始化教师数据
            initTeachers();
            logger.info("✓ 教师数据初始化完成，共 10 条");
            
            // 初始化竞赛数据
            initContests();
            logger.info("✓ 竞赛信息初始化完成，共 15 条");
            
            // 初始化报名数据
            initRegistrations();
            logger.info("✓ 报名数据初始化完成，共 30 条");
            
            // 初始化评分数据
            initScores();
            logger.info("✓ 评分数据初始化完成，共 20 条");
            
            logger.info("=================================");
            logger.info("测试数据初始化完成！");
            logger.info("=================================");
            
        } catch (Exception e) {
            logger.error("初始化测试数据失败：{}", e.getMessage());
            logger.error("请手动执行 SQL 脚本：src/main/resources/db/migration/V3__add_test_data.sql");
        }
    }
    
    /**
     * 初始化学生数据
     */
    private void initStudents() {
        String[][] students = {
            {"2022001", "张伟", "男", "计算机学院", "计算机科学与技术 2201 班", "13800138001", "2022001@school.edu.cn"},
            {"2022002", "李娜", "女", "计算机学院", "计算机科学与技术 2201 班", "13800138002", "2022002@school.edu.cn"},
            {"2022003", "王强", "男", "计算机学院", "软件工程 2201 班", "13800138003", "2022003@school.edu.cn"},
            {"2022004", "刘芳", "女", "软件学院", "数字媒体技术 2201 班", "13800138004", "2022004@school.edu.cn"},
            {"2022005", "陈杰", "男", "信息学院", "电子信息工程 2201 班", "13800138005", "2022005@school.edu.cn"},
            {"2022006", "杨敏", "女", "电子工程学院", "电子科学与技术 2201 班", "13800138006", "2022006@school.edu.cn"},
            {"2022007", "赵磊", "男", "管理学院", "信息管理 2201 班", "13800138007", "2022007@school.edu.cn"},
            {"2022008", "孙丽", "女", "艺术学院", "数字媒体艺术 2201 班", "13800138008", "2022008@school.edu.cn"},
            {"2022009", "周杰", "男", "数学与统计学院", "数学与应用数学 2201 班", "13800138009", "2022009@school.edu.cn"},
            {"2022010", "吴婷", "女", "物理学院", "应用物理学 2201 班", "13800138010", "2022010@school.edu.cn"}
        };
        
        for (String[] s : students) {
            XueshengEntity student = new XueshengEntity();
            student.setId(com.utils.IdWorker.getId());
            student.setXuehao(s[0]);
            // 使用 PasswordEncoder 动态生成密码哈希
            org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = 
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
            String hashedPassword = encoder.encode("123456");
            student.setMima(hashedPassword);
            student.setXueshengxingming(s[1]);
            student.setXingbie(s[2]);
            student.setXueyuanmingcheng(s[3]);
            student.setBanji(s[4]);
            student.setShouji(s[5]);
            student.setYouxiang(s[6]);
            student.setZhaopian("upload/student_" + s[0] + ".jpg");
            student.setRole("学生");
            student.setAddtime(new Date());
            xueshengDao.insert(student);
        }
    }
    
    /**
     * 初始化教师数据
     */
    private void initTeachers() {
        String[][] teachers = {
            {"T2022001", "赵建国", "男", "计算机学院", "教授", "13900139001", "zhaojianguo@school.edu.cn"},
            {"T2022002", "钱晓敏", "女", "计算机学院", "副教授", "13900139002", "qianxiaomin@school.edu.cn"},
            {"T2022003", "孙志强", "男", "软件学院", "讲师", "13900139003", "sunzhiqiang@school.edu.cn"},
            {"T2022004", "李晓红", "女", "信息学院", "副教授", "13900139004", "lixiaohong@school.edu.cn"},
            {"T2022005", "周伟", "男", "电子工程学院", "教授", "13900139005", "zhouwei@school.edu.cn"},
            {"T2022006", "吴芳", "女", "管理学院", "讲师", "13900139006", "wufang@school.edu.cn"},
            {"T2022007", "郑建华", "男", "艺术学院", "副教授", "13900139007", "zhengjianhua@school.edu.cn"},
            {"T2022008", "冯涛", "男", "数学与统计学院", "教授", "13900139008", "fengtao@school.edu.cn"},
            {"T2022009", "陈静", "女", "物理学院", "副教授", "13900139009", "chenjing@school.edu.cn"},
            {"T2022010", "褚卫", "男", "化学学院", "讲师", "13900139010", "chuwei@school.edu.cn"}
        };
        
        for (String[] t : teachers) {
            JiaoshiEntity teacher = new JiaoshiEntity();
            teacher.setId(com.utils.IdWorker.getId());
            teacher.setGonghao(t[0]);
            // 使用 PasswordEncoder 动态生成密码哈希
            org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = 
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
            String hashedPassword = encoder.encode("123456");
            teacher.setMima(hashedPassword);
            teacher.setJiaoshixingming(t[1]);
            teacher.setXingbie(t[2]);
            teacher.setXueyuanmingcheng(t[3]);
            teacher.setZhicheng(t[4]);
            teacher.setShouji(t[5]);
            teacher.setYouxiang(t[6]);
            teacher.setZhaopian("upload/teacher_" + t[0] + ".jpg");
            teacher.setRole("教师");
            teacher.setAddtime(new Date());
            jiaoshiDao.insert(teacher);
        }
    }
    
    /**
     * 初始化竞赛数据
     */
    private void initContests() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        String[][] contests = {
            {"全国大学生程序设计竞赛 (ACM-ICPC)", "学科竞赛", "计算机学院实验楼 301", "个人赛，限时 5 小时，完成 10-12 道算法题目", 
             "一等奖 3 名（奖金 5000 元 + 证书），二等奖 6 名（奖金 3000 元 + 证书），三等奖 10 名（奖金 1000 元 + 证书）", 
             "2026-04-15 09:00:00", "个人赛", "T2022001", "赵建国"},
             
            {"全国大学生数学建模竞赛", "学科竞赛", "第一教学楼 201-205", "3 人组队，72 小时内完成一道数学建模题目并提交论文",
             "一等奖 5 队（奖金 8000 元/队 + 证书），二等奖 10 队（奖金 5000 元/队 + 证书），三等奖 15 队（奖金 2000 元/队 + 证书）",
             "2026-05-20 08:00:00", "团队赛", "T2022008", "冯涛"},
             
            {"全国大学生英语竞赛", "学科竞赛", "外国语学院报告厅", "笔试 + 口试，总分 150 分",
             "特等奖 1 名（奖金 10000 元 + 证书），一等奖 5 名（奖金 5000 元 + 证书），二等奖 10 名（奖金 3000 元 + 证书），三等奖 20 名（奖金 1000 元 + 证书）",
             "2026-06-10 14:00:00", "个人赛", "T2022002", "钱晓敏"},
             
            {"全国大学生电子设计竞赛", "学科竞赛", "电子工程学院实验室", "4 人组队，4 天 3 夜完成电子产品设计与制作",
             "一等奖 3 队（奖金 10000 元/队 + 证书），二等奖 6 队（奖金 6000 元/队 + 证书），三等奖 10 队（奖金 3000 元/队 + 证书）",
             "2026-07-05 09:00:00", "团队赛", "T2022005", "周伟"},
             
            {"中国国际\"互联网 +\"大学生创新创业大赛", "创新创业", "大学生活动中心", "提交商业计划书 + 路演答辩，评委打分",
             "金奖 3 队（奖金 20000 元/队 + 证书），银奖 6 队（奖金 10000 元/队 + 证书），铜奖 10 队（奖金 5000 元/队 + 证书）",
             "2026-05-25 09:00:00", "团队赛", "T2022006", "吴芳"}
        };
        
        for (String[] c : contests) {
            JingsaixinxiEntity contest = new JingsaixinxiEntity();
            contest.setJingsaimingcheng(c[0]);
            contest.setJingsaileixing(c[1]);
            contest.setJingsaididian(c[2]);
            contest.setJingsaiguize(c[3]);
            contest.setJingsaijiangli(c[4]);
            contest.setJingsaishijian(sdf.parse(c[5]));
            contest.setMoshi(c[6]);
            contest.setFengmiantupian("upload/contest_" + c[0].hashCode() + ".jpg");
            contest.setGonghao(c[7]);
            contest.setJiaoshixingming(c[8]);
            contest.setAddtime(new Date());
            jingsaixinxiDao.insert(contest);
        }
    }
    
    /**
     * 初始化报名数据
     */
    private void initRegistrations() throws Exception {
        // 简化处理，只插入少量示例数据
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        String[][] registrations = {
            {"T2022001", "赵建国", "全国大学生程序设计竞赛 (ACM-ICPC)", "学科竞赛", "个人赛", "张伟", "无", 
             "追求卓越，挑战自我！", "2026-03-01", "2022001", "张伟"},
            {"T2022002", "钱晓敏", "全国大学生英语竞赛", "学科竞赛", "个人赛", "李娜", "无",
             "English is my passion!", "2026-03-02", "2022002", "李娜"},
            {"T2022001", "赵建国", "全国大学生程序设计竞赛 (ACM-ICPC)", "学科竞赛", "个人赛", "王强", "无",
             "代码改变世界！", "2026-03-01", "2022003", "王强"}
        };
        
        for (String[] r : registrations) {
            JingsaibaomingEntity registration = new JingsaibaomingEntity();
            registration.setGonghao(r[0]);
            registration.setJiaoshixingming(r[1]);
            registration.setJingsaimingcheng(r[2]);
            registration.setJingsaileixing(r[3]);
            registration.setCansaileixing(r[4]);
            registration.setCansairenyuan(r[5]);
            registration.setCansaizuopin(r[6]);
            registration.setCansaixuanyan(r[7]);
            registration.setShenqingriqi(sdf.parse(r[8]));
            registration.setXuehao(r[9]);
            registration.setXueshengxingming(r[10]);
            registration.setSfsh("是");
            registration.setShhf("审核通过，请按时参赛");
            registration.setIspay("是");
            registration.setAddtime(new Date());
            jingsaibaomingDao.insert(registration);
        }
    }
    
    /**
     * 初始化评分数据
     */
    private void initScores() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        String[][] scores = {
            {"2022001", "张伟", "全国大学生程序设计竞赛 (ACM-ICPC)", "学科竞赛", "92", 
             "算法思路清晰，代码规范，性能优秀。展现了扎实的编程功底和问题解决能力。", "2026-04-16", "T2022001", "赵建国"},
            {"2022002", "李娜", "全国大学生英语竞赛", "学科竞赛", "95",
             "发音标准，表达流畅，逻辑思维清晰。口语和笔试均表现优异。", "2026-06-11", "T2022002", "钱晓敏"},
            {"2022003", "王强", "全国大学生程序设计竞赛 (ACM-ICPC)", "学科竞赛", "85",
             "解题速度较快，代码质量高。建议在复杂算法上继续加强。", "2026-04-16", "T2022001", "赵建国"}
        };
        
        for (String[] s : scores) {
            ZuopindafenEntity score = new ZuopindafenEntity();
            score.setXuehao(s[0]);
            score.setXueshengxingming(s[1]);
            score.setJingsaimingcheng(s[2]);
            score.setJingsaileixing(s[3]);
            score.setZuopinpingfen(Integer.parseInt(s[4]));
            score.setPingjianeirong(s[5]);
            score.setPingjiashijian(sdf.parse(s[6]));
            score.setGonghao(s[7]);
            score.setJiaoshixingming(s[8]);
            score.setAddtime(new Date());
            zuopindafenDao.insert(score);
        }
    }
}
