package com.controller;

import com.annotation.IgnoreAuth;
import com.annotation.OperationLog;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.XiaoxiTongzhiEntity;
import com.service.XiaoxiTongzhiService;
import com.utils.PageUtils;
import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 消息通知 Controller
 */
@Slf4j
@RestController
@RequestMapping("/xiaoxitongzhi")
public class XiaoxiTongzhiController {

    @Autowired
    private XiaoxiTongzhiService xiaoxiTongzhiService;

    /**
     * 分页查询消息列表
     */
    @GetMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        try {
            // 根据角色过滤数据
            String tableName = (String) request.getSession().getAttribute("tableName");
            String username = (String) request.getSession().getAttribute("username");
            
            if ("xuesheng".equals(tableName)) {
                params.put("jieshourenXuehao", username);
            } else if ("jiaoshi".equals(tableName)) {
                params.put("jieshourenGonghao", username);
            }
            
            PageUtils page = xiaoxiTongzhiService.queryPageView(params);
            return R.ok().put("data", page);
        } catch (Exception e) {
            log.error("查询消息列表异常", e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread/count")
    public R getUnreadCount(HttpServletRequest request) {
        try {
            String tableName = (String) request.getSession().getAttribute("tableName");
            String username = (String) request.getSession().getAttribute("username");
            
            EntityWrapper<XiaoxiTongzhiEntity> ew = new EntityWrapper<>();
            ew.eq("is_read", "未读");
            
            if ("xuesheng".equals(tableName)) {
                ew.eq("jieshouren_xuehao", username);
            } else if ("jiaoshi".equals(tableName)) {
                ew.eq("jieshouren_gonghao", username);
            }
            
            int count = xiaoxiTongzhiService.selectCount(ew);
            return R.ok().put("count", count);
        } catch (Exception e) {
            log.error("获取未读消息数量异常", e);
            return R.error("查询失败");
        }
    }

    /**
     * 标记消息为已读
     */
    @OperationLog("标记消息为已读")
    @PostMapping("/markRead/{id}")
    @Transactional(rollbackFor = Exception.class)
    public R markRead(@PathVariable Long id) {
        try {
            XiaoxiTongzhiEntity entity = xiaoxiTongzhiService.selectById(id);
            if (entity == null) {
                return R.error("消息不存在");
            }
            
            entity.setIsRead("已读");
            entity.setReadTime(new Date());
            xiaoxiTongzhiService.updateById(entity);
            
            log.info("✓ 消息已标记为已读，ID: {}", id);
            return R.ok("操作成功");
        } catch (Exception e) {
            log.error("✗ 标记消息已读异常，ID: {}", id, e);
            return R.error("操作失败");
        }
    }

    /**
     * 批量标记所有消息为已读
     */
    @OperationLog("批量标记消息为已读")
    @PostMapping("/markAllRead")
    @Transactional(rollbackFor = Exception.class)
    public R markAllRead(HttpServletRequest request) {
        try {
            String tableName = (String) request.getSession().getAttribute("tableName");
            String username = (String) request.getSession().getAttribute("username");
            
            EntityWrapper<XiaoxiTongzhiEntity> ew = new EntityWrapper<>();
            ew.eq("is_read", "未读");
            
            if ("xuesheng".equals(tableName)) {
                ew.eq("jieshouren_xuehao", username);
            } else if ("jiaoshi".equals(tableName)) {
                ew.eq("jieshouren_gonghao", username);
            }
            
            java.util.List<XiaoxiTongzhiEntity> list = xiaoxiTongzhiService.selectList(ew);
            for (XiaoxiTongzhiEntity entity : list) {
                entity.setIsRead("已读");
                entity.setReadTime(new Date());
                xiaoxiTongzhiService.updateById(entity);
            }
            
            log.info("✓ 批量标记消息已读成功，数量: {}", list.size());
            return R.ok("操作成功");
        } catch (Exception e) {
            log.error("✗ 批量标记消息已读异常", e);
            return R.error("操作失败");
        }
    }

    /**
     * 删除消息
     */
    @OperationLog("删除消息")
    @PostMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public R delete(@RequestBody Long[] ids) {
        try {
            for (Long id : ids) {
                xiaoxiTongzhiService.deleteById(id);
            }
            log.info("✓ 删除消息成功，数量: {}", ids.length);
            return R.ok("删除成功");
        } catch (Exception e) {
            log.error("✗ 删除消息异常", e);
            return R.error("删除失败");
        }
    }

    /**
     * 发送消息通知（内部调用或管理员使用）
     */
    @OperationLog("发送消息通知")
    @PostMapping("/send")
    @Transactional(rollbackFor = Exception.class)
    public R sendTongzhi(@RequestBody Map<String, Object> params) {
        try {
            String biaoti = (String) params.get("biaoti");
            String neirong = (String) params.get("neirong");
            String leixing = (String) params.get("leixing");
            String fasongren = (String) params.get("fasongren");
            String jieshourenXuehao = (String) params.get("jieshourenXuehao");
            String jieshourenGonghao = (String) params.get("jieshourenGonghao");
            String jieshourenJuese = (String) params.get("jieshourenJuese");
            Long guanlianId = params.get("guanlianId") != null ? Long.valueOf(params.get("guanlianId").toString()) : null;
            String guanlianLeixing = (String) params.get("guanlianLeixing");
            
            xiaoxiTongzhiService.sendTongzhi(biaoti, neirong, leixing, fasongren,
                    jieshourenXuehao, jieshourenGonghao, jieshourenJuese, guanlianId, guanlianLeixing);
            
            return R.ok("发送成功");
        } catch (Exception e) {
            log.error("发送消息异常", e);
            return R.error("发送失败");
        }
    }

    /**
     * 获取统计数据
     * 功能：统计总消息数、未读消息数、已读消息数
     * 
     * @param request HTTP 请求
     * @return R 统一返回结果，包含统计信息
     */
    @RequestMapping("/statistics")
    public R statistics(HttpServletRequest request) {
        try {
            log.info("========== 消息统计数据查询开始 ==========");
            Map<String, Object> stats = new java.util.HashMap<>();
            String tableName = (String) request.getSession().getAttribute("tableName");
            String username = (String) request.getSession().getAttribute("username");
            
            // 使用更可靠的查询方式：先查询所有，再过滤
            List<XiaoxiTongzhiEntity> allMessages = xiaoxiTongzhiService.selectList(null);
            
            // 根据角色过滤
            if ("xuesheng".equals(tableName)) {
                final String finalUsername = username;
                allMessages = allMessages.stream()
                    .filter(m -> finalUsername.equals(m.getJieshourenXuehao()))
                    .collect(java.util.stream.Collectors.toList());
            } else if ("jiaoshi".equals(tableName)) {
                final String finalUsername = username;
                allMessages = allMessages.stream()
                    .filter(m -> finalUsername.equals(m.getJieshourenGonghao()))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // 1. 统计总消息数
            int totalCount = allMessages != null ? allMessages.size() : 0;
            log.info("消息总数：{}", totalCount);
            stats.put("total", totalCount);
            
            // 2. 统计未读和已读消息数
            int unreadCount = 0;
            int readCount = 0;
            if (allMessages != null) {
                for (XiaoxiTongzhiEntity message : allMessages) {
                    if ("未读".equals(message.getIsRead())) {
                        unreadCount++;
                    } else if ("已读".equals(message.getIsRead())) {
                        readCount++;
                    }
                }
            }
            log.info("未读：{}，已读：{}", unreadCount, readCount);
            stats.put("unread", unreadCount);
            stats.put("read", readCount);
            
            log.info("消息统计数据查询成功，角色：{}", tableName);
            return R.ok().put("data", stats);
            
        } catch (Exception e) {
            log.error("消息统计数据查询异常：", e);
            return R.error("统计查询失败，请重试");
        }
    }
}
