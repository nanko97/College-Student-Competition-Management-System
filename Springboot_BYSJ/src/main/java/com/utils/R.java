package com.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一返回结果类
 * 功能：封装所有 Controller 的响应数据，统一格式
 * 
 * 返回数据结构：
 * <pre>
 * {
 *     "code": 0,          // 状态码：0-成功，500-失败，其他-自定义
 *     "msg": "操作成功",   // 响应消息
 *     "data": {}          // 返回数据 (可选)
 * }
 * </pre>
 * 
 * 使用示例：
 * <pre>
 * // 1. 返回成功 (无数据)
 * return R.ok();
 * 
 * // 2. 返回成功 (带消息)
 * return R.ok("操作成功");
 * 
 * // 3. 返回成功 (带数据)
 * return R.ok().put("data", userList);
 * 
 * // 4. 返回错误
 * return R.error("参数错误");
 * 
 * // 5. 返回错误 (带错误码)
 * return R.error(401, "未授权");
 * </pre>
 * 
 * @author毕业设计优化版
 * @date 2026-03-05
 */
public class R extends HashMap<String, Object> {
    
    /** 序列化版本 ID */
    private static final long serialVersionUID = 1L;

    /**
     * 默认构造函数
     * 初始化 code 为 0 (表示成功)
     */
    public R() {
        put("code", 0); // 0 表示成功
    }

    /**
     * 返回错误响应 (默认错误)
     * 说明：code=500, msg="未知异常，请联系管理员"
     * 
     * @return R 错误响应对象
     */
    public static R error() {
        return error(500, "未知异常，请联系管理员");
    }

    /**
     * 返回错误响应 (自定义消息)
     * 说明：code=500, msg=自定义消息
     * 
     * @param msg 错误消息
     * @return R 错误响应对象
     */
    public static R error(String msg) {
        return error(500, msg);
    }

    /**
     * 返回错误响应 (自定义错误码和消息)
     * 说明：支持设置任意错误码和消息
     * 
     * 常用错误码：
     * - 400: 参数错误
     * - 401: 未授权/登录过期
     * - 403: 禁止访问/权限不足
     * - 404: 资源不存在
     * - 500: 服务器内部错误
     * 
     * @param code 错误码
     * @param msg 错误消息
     * @return R 错误响应对象
     */
    public static R error(int code, String msg) {
        R r = new R();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    /**
     * 返回成功响应 (带消息)
     * 说明：code=0, msg=自定义消息
     * 
     * @param msg 成功消息
     * @return R 成功响应对象
     */
    public static R ok(String msg) {
        R r = new R();
        r.put("msg", msg);
        return r;
    }

    /**
     * 返回成功响应 (带 Map 数据)
     * 说明：将整个 Map 放入返回结果
     * 
     * @param map 数据 Map
     * @return R 成功响应对象
     */
    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    /**
     * 返回成功响应 (无数据)
     * 说明：最基础的返回方式，仅返回 code=0
     * 
     * @return R 成功响应对象
     */
    public static R ok() {
        return new R();
    }

    /**
     * 链式添加数据
     * 说明：支持链式调用，方便构建复杂响应
     * 
     * 使用示例：
     * <pre>
     * return R.ok()
     *     .put("data", userList)
     *     .put("total", totalCount)
     *     .put("page", currentPage);
     * </pre>
     * 
     * @param key 键
     * @param value 值
     * @return R 当前对象 (支持链式调用)
     */
    public R put(String key, Object value) {
        super.put(key, value);
        return this; // 返回 this，支持链式调用
    }
}
