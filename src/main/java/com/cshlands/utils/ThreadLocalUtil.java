package com.cshlands.utils;

import java.util.Map;

public class ThreadLocalUtil {
    private static final ThreadLocal<Map<String, Object>> THREAD_LOCAL = new ThreadLocal<Map<String, Object>>();

    public static void set(Map<String, Object> claims) {
        THREAD_LOCAL.set(claims);
    }

    public static Map<String, Object> get() {
        return THREAD_LOCAL.get();
    }

    // 由于生命周期较长，在请求结束后需要手动清理ThreadLocal中的数据，避免内存泄漏
    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
