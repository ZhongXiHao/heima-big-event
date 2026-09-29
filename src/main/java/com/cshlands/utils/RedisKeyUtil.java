package com.cshlands.utils;

public class RedisKeyUtil {
    private static final String LOGIN_TOKEN_PREFIX = "login:token:";

    // 单点登录：每个用户在 Redis 中只保留一个有效 token
    public static String loginTokenKey(Object userId) {
        return LOGIN_TOKEN_PREFIX + userId;
    }
}
