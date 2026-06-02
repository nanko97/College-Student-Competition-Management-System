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
            log.info("========== 消息列表查询开始，参数：{} ==========");
            
            // 根据角色过滤数据
            String tableName = (String) request.getSession().getAttribute("tableName");
            String username = (String) request.getSession().getAttribute("username");
            
            // 设置角色过滤参数
            if ("xuesheng".equals(tableName)) {
                params.put("jieshourenXuehao", username);
                log.info("学生角色，学号：{}", username);
            } else if ("jiaoshi".equals(tableName)) {
                params.put("jieshourenGonghao", username);
                log.info("教师角色，工号：{}", username);
            } else {
                log.info("管理员角色，查看所有消息");
            }
            
            PageUtils page = xiaoxiTongzhiService.queryPageView(params);
            log.info("消息列表查询成功，总数：{}", page.getTotal());
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
            log.info("========== 查询未读消息数量开始 ==========");
            String tableName = (String) request.getSession().getAttribute("tableName");
            String username = (String) request.getSession().getAttribute("username");
            
            // 使用 EntityWrapper + selectCount 避免全表扫描
            EntityWrapper<XiaoxiTongzhiEntity> ew = new EntityWrapper<>();
            ew.eq("is_read", "未读");
            if ("xuesheng".equals(tableName)) {
                ew.eq("jieshouren_xuehao", username);
            } else if ("jiaoshi".equals(tableName)) {
                ew.eq("jieshouren_gonghao", username);
            }
            int count = xiaoxiTongzhiService.selectCount(ew);
            
            log.info("未读消息数量：{}", count);
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
    public R markRead(@PathVariable Long id, HttpServletRequest request) {
        try {
            log.info("========== 标记消息已读开始，ID: {} ==========", id);
            
            String tableName = (String) request.getSession().getAttribute("tableName");
            String username = (String) request.getSession().getAttribute("username");
            
            XiaoxiTongzhiEntity entity = xiaoxiTongzhiService.selectById(id);
            if (entity == null) {
                log.warn("消息不存在，ID: {}", id);
                return R.error("消息不存在");
            }
            
            // 权限验证：确保用户只能标记自己的消息（大小写不敏感）
            boolean canMark = false;
            if ("xuesheng".equals(tableName)) {
                String receivedXuehao = entity.getJieshourenXuehao();
                canMark = username != null && receivedXuehao != null && username.equalsIgnoreCase(receivedXuehao);
            } else if ("jiaoshi".equals(tableName)) {
                String receivedGonghao = entity.getJieshourenGonghao();
                canMark = username != null && receivedGonghao != null && username.equalsIgnoreCase(receivedGonghao);
            } else {
                // 管理员可以标记所有消息
                canMark = true;
            }
            
            if (!canMark) {
                log.warn("无权操作此消息，用户: {}, 消息ID: {}", username, id);
                return R.error("无权操作此消息");
            }
            
            // 如果已经是已读状态，直接返回成功
            if ("已读".equals(entity.getIsRead())) {
                log.info("消息已是已读状态，ID: {}", id);
                return R.ok("操作成功");
            }
            
            entity.setIsRead("已读");
            entity.setReadTime(new Date());
            xiaoxiTongzhiService.updateById(entity);
            
            log.info("✓ 消息已标记为已读，ID: {}, 用户: {}", id, username);
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
            log.info("========== 批量标记消息已读开始 ==========");
            String tableName = (String) request.getSession().getAttribute("tableName");
            String username = (String) request.getSession().getAttribute("username");
            
            // 使用 EntityWrapper 查询未读消息，避免全表扫描
            EntityWrapper<XiaoxiTongzhiEntity> ew = new EntityWrapper<>();
            ew.eq("is_read", "未读");
            if ("xuesheng".equals(tableName)) {
                ew.eq("jieshouren_xuehao", username);
            } else if ("jiaoshi".equals(tableName)) {
                ew.eq("jieshouren_gonghao", username);
            }
            
            List<XiaoxiTongzhiEntity> unreadMessages = xiaoxiTongzhiService.selectList(ew);
            int count = 0;
            Date now = new Date();
            for (XiaoxiTongzhiEntity entity : unreadMessages) {
                entity.setIsRead("已读");
                entity.setReadTime(now);
                xiaoxiTongzhiService.updateById(entity);
                count++;
            }
            
            log.info("✓ 批量标记消息已读成功，数量: {}", count);
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
     * 更新消息（标记为已读）
     */
    @OperationLog("更新消息状态")
    @PostMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public R update(@RequestBody XiaoxiTongzhiEntity entity, HttpServletRequest request) {
        try {
            log.info("========== 更新消息开始，ID: {} ==========", entity.getId());
            log.info("前端传递的数据 - ID: {}, isRead: {}, readTime: {}", 
                    entity.getId(), entity.getIsRead(), entity.getReadTime());
                        
            String tableName = (String) request.getSession().getAttribute("tableName");
            String username = (String) request.getSession().getAttribute("username");
            log.info("当前用户 - tableName: {}, username: {}", tableName, username);
                        
            XiaoxiTongzhiEntity oldEntity = xiaoxiTongzhiService.selectById(entity.getId());
            if (oldEntity == null) {
                log.warn("消息不存在，ID: {}", entity.getId());
                return R.error("消息不存在");
            }
                        
            log.info("消息详情 - isRead: {}, jieshourenXuehao: {}, jieshourenGonghao: {}, jieshourenJuese: {}", 
                    oldEntity.getIsRead(), oldEntity.getJieshourenXuehao(), 
                    oldEntity.getJieshourenGonghao(), oldEntity.getJieshourenJuese());
                        
            // 权限验证：确保用户只能更新自己的消息
            boolean canUpdate = false;
            if ("xuesheng".equals(tableName)) {
                String receivedXuehao = oldEntity.getJieshourenXuehao();
                log.info("学生权限校验 - 当前username: {}, 消息接收人学号: {}", username, receivedXuehao);
                canUpdate = username != null && receivedXuehao != null && username.equalsIgnoreCase(receivedXuehao);
            } else if ("jiaoshi".equals(tableName)) {
                String receivedGonghao = oldEntity.getJieshourenGonghao();
                log.info("教师权限校验 - 当前username: {}, 消息接收人工号: {}", username, receivedGonghao);
                canUpdate = username != null && receivedGonghao != null && username.equalsIgnoreCase(receivedGonghao);
            } else {
                // 管理员可以更新所有消息
                log.info("管理员权限 - 允许更新所有消息");
                canUpdate = true;
            }
                        
            if (!canUpdate) {
                log.warn("无权操作此消息 - 用户: {}, 消息ID: {}, 消息接收人学号: {}, 消息接收人工号: {}, 消息接收人角色: {}", 
                        username, entity.getId(), oldEntity.getJieshourenXuehao(), 
                        oldEntity.getJieshourenGonghao(), oldEntity.getJieshourenJuese());
                return R.error("无权操作此消息");
            }
            
            // 使用前端传递的 isRead 值，如果为空则设置为“已读”
            String newIsRead = (entity.getIsRead() != null && !entity.getIsRead().isEmpty()) 
                    ? entity.getIsRead() : "已读";
            
            // 如果前端传递的 readTime 是字符串，则使用当前时间
            if (entity.getReadTime() == null) {
                entity.setReadTime(new Date());
            }
            
            // 设置更新后的值
            oldEntity.setIsRead(newIsRead);
            oldEntity.setReadTime(entity.getReadTime());
            
            log.info("准备更新 - ID: {}, 新状态: {}, readTime: {}", 
                    oldEntity.getId(), oldEntity.getIsRead(), oldEntity.getReadTime());
            
            // 更新消息
            xiaoxiTongzhiService.updateById(oldEntity);
            
            // 验证更新结果
            XiaoxiTongzhiEntity updatedEntity = xiaoxiTongzhiService.selectById(entity.getId());
            log.info("✓ 消息更新成功，ID: {}, 用户: {}, 更新后状态: {}", 
                    entity.getId(), username, updatedEntity.getIsRead());
            
            return R.ok("更新成功");
        } catch (Exception e) {
            log.error("✗ 更新消息异常，ID: {}", entity.getId(), e);
            return R.error("更新失败");
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
            
            // 使用 EntityWrapper + selectCount 避免全表扫描
            EntityWrapper<XiaoxiTongzhiEntity> baseEw = new EntityWrapper<>();
            if ("xuesheng".equals(tableName)) {
                baseEw.eq("jieshouren_xuehao", username);
            } else if ("jiaoshi".equals(tableName)) {
                baseEw.eq("jieshouren_gonghao", username);
            }
            
            // 统计总消息数
            int totalCount = xiaoxiTongzhiService.selectCount(baseEw);
            log.info("消息总数：{}", totalCount);
            stats.put("total", totalCount);
            
            // 统计未读消息数
            EntityWrapper<XiaoxiTongzhiEntity> unreadEw = new EntityWrapper<>();
            unreadEw.eq("is_read", "未读");
            if ("xuesheng".equals(tableName)) {
                unreadEw.eq("jieshouren_xuehao", username);
            } else if ("jiaoshi".equals(tableName)) {
                unreadEw.eq("jieshouren_gonghao", username);
            }
            int unreadCount = xiaoxiTongzhiService.selectCount(unreadEw);
            stats.put("unread", unreadCount);
            
            // 统计已读消息数
            EntityWrapper<XiaoxiTongzhiEntity> readEw = new EntityWrapper<>();
            readEw.eq("is_read", "已读");
            if ("xuesheng".equals(tableName)) {
                readEw.eq("jieshouren_xuehao", username);
            } else if ("jiaoshi".equals(tableName)) {
                readEw.eq("jieshouren_gonghao", username);
            }
            int readCount = xiaoxiTongzhiService.selectCount(readEw);
            stats.put("read", readCount);
            
            log.info("未读：{}，已读：{}", unreadCount, readCount);
            log.info("消息统计数据查询成功，角色：{}", tableName);
            return R.ok().put("data", stats);
            
        } catch (Exception e) {
            log.error("消息统计数据查询异常：", e);
            return R.error("统计查询失败，请重试");
        }
    }
}
