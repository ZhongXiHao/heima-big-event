package com.cshlands.converter;

import com.cshlands.pojo.ArticleState;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToArticleStateConverter implements Converter<String, ArticleState> {
    @Override
    public ArticleState convert(String source) {
        if (source.isBlank()) {
            return null;              // 空串视为没传，即不会作为查询条件
        }
        return ArticleState.fromValue(source);
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return null;
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return null;
    }
}