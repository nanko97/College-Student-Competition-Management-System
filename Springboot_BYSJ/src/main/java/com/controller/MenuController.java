package com.controller;

import com.annotation.IgnoreAuth;
import com.entity.TokenEntity;
import com.service.TokenService;
import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 菜单控制器
 * 根据用户角色返回对应的菜单
 */
@RestController
@RequestMapping("menus")
@Slf4j
public class MenuController {

    @Autowired
    private TokenService tokenService;

    /**
     * 获取用户菜单
     * 前端期望：GET /menus
     * 返回格式适配 naive-ui-admin 的菜单格式
     */
    @GetMapping("")
    public R getMenus(HttpServletRequest request) {
        try {
            // 1. 获取token
            String token = request.getHeader("token");
            if (!StringUtils.hasText(token)) {
                log.warn("获取菜单失败：token为空");
                return R.error(401, "请先登录");
            }

            // 2. 验证token并获取用户角色
            TokenEntity tokenEntity = tokenService.getTokenEntity(token);
            if (tokenEntity == null) {
                log.warn("获取菜单失败：token无效");
                return R.error(401, "token无效或已过期");
            }

            String role = tokenEntity.getRole();
            log.info("获取菜单成功：角色={}", role);

            // 3. 根据角色返回菜单
            List<Map<String, Object>> menus = generateMenusByRole(role);
            return R.ok().put("data", menus);
        } catch (Exception e) {
            log.error("获取菜单异常：", e);
            return R.error("获取菜单失败");
        }
    }

    /**
     * 根据角色生成菜单
     */
    private List<Map<String, Object>> generateMenusByRole(String role) {
        List<Map<String, Object>> menus = new ArrayList<>();

        switch (role) {
            case "管理员":
                menus.addAll(getAdminMenus());
                break;
            case "学生":
                menus.addAll(getStudentMenus());
                break;
            case "教师":
                menus.addAll(getTeacherMenus());
                break;
            default:
                log.warn("未知角色：{}，返回空菜单", role);
                break;
        }

        return menus;
    }

    /**
     * 管理员菜单
     */
    private List<Map<String, Object>> getAdminMenus() {
        List<Map<String, Object>> menus = new ArrayList<>();

        // Dashboard
        menus.add(createMenu("dashboard", "Dashboard", "DashboardOutlined", "/", "dashboard/console",
                Arrays.asList(
                        createSubMenu("dashboard_console", "主控台", "dashboard/console"),
                        createSubMenu("dashboard_workplace", "工作台", "dashboard/workplace")
                )));

        // 学生管理
        menus.add(createMenu("xuesheng", "学生管理", "TeamOutlined", "/xuesheng", null,
                Arrays.asList(
                        createSubMenu("xuesheng_list", "学生列表", "xuesheng/list")
                )));

        // 教师管理
        menus.add(createMenu("jiaoshi", "教师管理", "UserOutlined", "/jiaoshi", null,
                Arrays.asList(
                        createSubMenu("jiaoshi_list", "教师列表", "jiaoshi/list")
                )));

        // 竞赛管理
        menus.add(createMenu("jingsai", "竞赛管理", "TrophyOutlined", "/jingsai", null,
                Arrays.asList(
                        createSubMenu("jingsai_xinxi", "竞赛信息", "jingsaixinxi/list"),
                        createSubMenu("jingsai_my", "我的竞赛", "jingsaixinxi/my-list"),
                        createSubMenu("jingsai_baoming", "竞赛报名", "jingsaibaoming/list"),
                        createSubMenu("jingsai_feiyong", "费用配置", "jingsai-feiyong/list"),
                        createSubMenu("jingsai_jiaofei", "缴费记录", "jingsai-jiaofei/list"),
                        createSubMenu("jingsai_jiaofei_shenhe", "缴费审核", "jingsai-jiaofei/shenhe-list")
                )));

        // 晋级管理
        menus.add(createMenu("jinji", "晋级管理", "ArrowUpOutlined", "/jinji", null,
                Arrays.asList(
                        createSubMenu("jinji_guanxi", "晋级关系", "jingsai-jinji/guanxi-list"),
                        createSubMenu("jinji_shenhe", "晋级审核", "jingsai-jinji/shenhe-list")
                )));

        // 级别认定管理
        menus.add(createMenu("jibie", "级别认定管理", "FlagOutlined", "/jibie", null,
                Arrays.asList(
                        createSubMenu("jibie_banben", "级别版本", "jingsai-jibie/banben-list")
                )));

        // 赛道管理
        menus.add(createMenu("saidao", "赛道管理", "PartitionOutlined", "/saidao", null,
                Arrays.asList(
                        createSubMenu("saidao_list", "赛道列表", "jingsai-saidao/list")
                )));

        // 团队管理
        menus.add(createMenu("tuandui", "团队管理", "UsergroupAddOutlined", "/tuandui", null,
                Arrays.asList(
                        createSubMenu("tuandui_list", "团队列表", "jingsai-tuandui/list")
                )));

        // 作品管理
        menus.add(createMenu("zuopin", "作品管理", "FileTextOutlined", "/zuopin", null,
                Arrays.asList(
                        createSubMenu("zuopin_list", "作品列表", "zuopin/list"),
                        createSubMenu("zuopin_dafen", "作品打分", "zuopindafen/list")
                )));

        // 成绩复核管理
        menus.add(createMenu("fuhe", "成绩复核管理", "CheckCircleOutlined", "/fuhe", null,
                Arrays.asList(
                        createSubMenu("fuhe_shenhe", "成绩复核审核", "zuopindafen/fuhe-shenhe")
                )));

        // 人员变更管理
        menus.add(createMenu("renyuan", "人员变更管理", "SwapOutlined", "/renyuan", null,
                Arrays.asList(
                        createSubMenu("renyuan_apply", "人员变更申请", "jingsai-renyuan-biangueng/apply"),
                        createSubMenu("renyuan_shenhe", "人员变更审核", "jingsai-renyuan-biangueng/shenhe-list")
                )));

        // 系统设置
        menus.add(createMenu("system", "系统设置", "SettingOutlined", "/system", null,
                Arrays.asList(
                        createSubMenu("system_user", "用户管理", "users/list"),
                        createSubMenu("system_menu", "菜单权限", "system/menu"),
                        createSubMenu("system_role", "角色权限", "system/role")
                )));

        return menus;
    }

    /**
     * 学生菜单
     */
    private List<Map<String, Object>> getStudentMenus() {
        List<Map<String, Object>> menus = new ArrayList<>();

        // Dashboard
        menus.add(createMenu("dashboard", "Dashboard", "DashboardOutlined", "/", "dashboard/console",
                Arrays.asList(
                        createSubMenu("dashboard_console", "主控台", "dashboard/console"),
                        createSubMenu("dashboard_workplace", "工作台", "dashboard/workplace")
                )));

        // 竞赛管理
        menus.add(createMenu("jingsai", "竞赛管理", "TrophyOutlined", "/jingsai", null,
                Arrays.asList(
                        createSubMenu("jingsai_xinxi", "竞赛信息", "jingsaixinxi/list"),
                        createSubMenu("jingsai_baoming", "我的报名", "jingsaibaoming/list")
                )));

        // 我的缴费
        menus.add(createMenu("my_jiaofei", "我的缴费", "DollarOutlined", "/my-jiaofei", null,
                Arrays.asList(
                        createSubMenu("my_jiaofei_list", "缴费记录", "xuesheng/my-jiaofei")
                )));

        // 我的作品
        menus.add(createMenu("my_zuopin", "我的作品", "FileTextOutlined", "/my-zuopin", null,
                Arrays.asList(
                        createSubMenu("my_zuopin_list", "作品列表", "xuesheng/my-zuopin")
                )));

        // 我的晋级
        menus.add(createMenu("my_jinji", "我的晋级", "ArrowUpOutlined", "/my-jinji", null,
                Arrays.asList(
                        createSubMenu("my_jinji_list", "晋级记录", "xuesheng/my-jinji")
                )));

        // 我的成绩
        menus.add(createMenu("my_chengji", "我的成绩", "BarChartOutlined", "/my-chengji", null,
                Arrays.asList(
                        createSubMenu("my_chengji_list", "成绩列表", "zuopindafen/list"),
                        createSubMenu("my_fuhe", "我的复核", "zuopindafen/my-fuhe")
                )));

        // 团队管理
        menus.add(createMenu("my_tuandui", "团队管理", "UsergroupAddOutlined", "/my-tuandui", null,
                Arrays.asList(
                        createSubMenu("my_tuandui_list", "我的团队", "xuesheng/my-tuandui"),
                        createSubMenu("tuandui_create", "创建团队", "xuesheng/tuandui-create"),
                        createSubMenu("tuandui_join", "申请加入团队", "xuesheng/tuandui-join")
                )));

        return menus;
    }

    /**
     * 教师菜单
     */
    private List<Map<String, Object>> getTeacherMenus() {
        List<Map<String, Object>> menus = new ArrayList<>();

        // Dashboard
        menus.add(createMenu("dashboard", "Dashboard", "DashboardOutlined", "/", "dashboard/console",
                Arrays.asList(
                        createSubMenu("dashboard_console", "主控台", "dashboard/console"),
                        createSubMenu("dashboard_workplace", "工作台", "dashboard/workplace")
                )));

        // 竞赛管理
        menus.add(createMenu("jingsai", "竞赛管理", "TrophyOutlined", "/jingsai", null,
                Arrays.asList(
                        createSubMenu("jingsai_xinxi", "竞赛信息", "jingsaixinxi/list"),
                        createSubMenu("jingsai_my", "我的竞赛", "jingsaixinxi/my-list"),
                        createSubMenu("jingsai_baoming", "竞赛报名", "jingsaibaoming/list")
                )));

        // 作品管理
        menus.add(createMenu("zuopin", "作品管理", "FileTextOutlined", "/zuopin", null,
                Arrays.asList(
                        createSubMenu("zuopin_list", "作品列表", "zuopin/list"),
                        createSubMenu("zuopin_dafen", "作品打分", "zuopindafen/list"),
                        createSubMenu("zuopin_fuhe_shenhe", "成绩复核审核", "zuopindafen/fuhe-shenhe")
                )));

        // 费用管理
        menus.add(createMenu("feiyong", "费用管理", "DollarOutlined", "/feiyong", null,
                Arrays.asList(
                        createSubMenu("jiaofei_jilu", "缴费记录", "jingsai-jiaofei/list"),
                        createSubMenu("jiaofei_shenhe", "缴费审核", "jingsai-jiaofei/shenhe-list")
                )));

        // 晋级管理
        menus.add(createMenu("jinji", "晋级管理", "ArrowUpOutlined", "/jinji", null,
                Arrays.asList(
                        createSubMenu("jinji_guanxi", "晋级关系", "jingsai-jinji/guanxi-list"),
                        createSubMenu("jinji_shenhe", "晋级审核", "jingsai-jinji/shenhe-list")
                )));

        // 级别认定管理
        menus.add(createMenu("jibie", "级别认定管理", "FlagOutlined", "/jibie", null,
                Arrays.asList(
                        createSubMenu("jibie_banben", "级别版本", "jingsai-jibie/banben-list")
                )));

        // 赛道管理
        menus.add(createMenu("saidao", "赛道管理", "PartitionOutlined", "/saidao", null,
                Arrays.asList(
                        createSubMenu("saidao_list", "赛道列表", "jingsai-saidao/list")
                )));

        // 团队管理
        menus.add(createMenu("tuandui", "团队管理", "UsergroupAddOutlined", "/tuandui", null,
                Arrays.asList(
                        createSubMenu("tuandui_list", "团队列表", "jingsai-tuandui/list")
                )));

        // 人员变更管理
        menus.add(createMenu("renyuan", "人员变更管理", "SwapOutlined", "/renyuan", null,
                Arrays.asList(
                        createSubMenu("renyuan_apply", "人员变更申请", "jingsai-renyuan-biangueng/apply"),
                        createSubMenu("renyuan_shenhe", "人员变更审核", "jingsai-renyuan-biangueng/shenhe-list")
                )));

        return menus;
    }

    /**
     * 创建主菜单
     * 适配 naive-ui-admin 路由格式
     */
    private Map<String, Object> createMenu(String name, String title, String icon, String path, 
                                          String redirect, List<Map<String, Object>> children) {
        Map<String, Object> menu = new HashMap<>();
        menu.put("name", name);
        menu.put("path", path.startsWith("/") ? path.substring(1) : path); // 移除开头的 /
        menu.put("component", "LAYOUT"); // 固定为 LAYOUT
        if (redirect != null && !redirect.isEmpty()) {
            menu.put("redirect", redirect);
        }
        
        Map<String, Object> meta = new HashMap<>();
        meta.put("title", title);
        meta.put("icon", icon);
        meta.put("sort", 0);
        menu.put("meta", meta);
        
        if (children != null && !children.isEmpty()) {
            menu.put("children", children);
        }
        
        return menu;
    }

    /**
     * 创建子菜单
     * 适配 naive-ui-admin 路由格式
     */
    private Map<String, Object> createSubMenu(String name, String title, String path) {
        Map<String, Object> menu = new HashMap<>();
        menu.put("name", name);
        // 子路由path不需要包含父路径，generator.ts会自动拼接
        menu.put("path", path.contains("/") ? path.substring(path.lastIndexOf("/") + 1) : path);
        // 组件路径：相对于 views 目录，不包含 .vue 后缀
        menu.put("component", "/" + path);
        
        Map<String, Object> meta = new HashMap<>();
        meta.put("title", title);
        menu.put("meta", meta);
        
        return menu;
    }
}
