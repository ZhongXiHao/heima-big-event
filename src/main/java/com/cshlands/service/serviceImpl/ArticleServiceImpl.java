package com.cshlands.service.serviceImpl;

import com.cshlands.exception.BusinessException;
import com.cshlands.pojo.Article;
import com.cshlands.service.ArticleService;
import com.cshlands.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public List<Article> list() {
//        Map<String, Object> parsed = null;
//        if (token == null) {
//            throw BusinessException.unauthorized("未登录");
//        }
//        try {
//            parsed = jwtUtil.parseToken(token);
//        } catch (Exception e) {
//            throw BusinessException.unauthorized("无权限");
//        }

        return null;
    }
}
