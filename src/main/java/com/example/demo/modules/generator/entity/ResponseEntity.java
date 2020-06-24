package com.example.demo.modules.generator.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: liuc
 * @Date: 18-12-16 16:22
 * @email i@liuchaoboy.com
 * @Description: 页面响应entity
 */
public class ResponseEntity extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    public ResponseEntity() {
        put("code", 0);
    }

    public static ResponseEntity error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static ResponseEntity error(String msg) {
        return error(500, msg);
    }

    public static ResponseEntity error(int code, String msg) {
        ResponseEntity r = new ResponseEntity();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static ResponseEntity ok(String msg) {
        ResponseEntity r = new ResponseEntity();
        r.put("msg", msg);
        return r;
    }

    public static ResponseEntity ok(Map<String, Object> map) {
        ResponseEntity r = new ResponseEntity();
        r.putAll(map);
        return r;
    }

    public static ResponseEntity ok() {
        return new ResponseEntity();
    }

    @Override
    public ResponseEntity put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
