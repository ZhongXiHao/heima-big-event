package com.cshlands.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ArticleState {
    DRAFT("草稿"),
    PUBLISHED("已发布");
    private final String value;

    ArticleState(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ArticleState fromValue(String value) {
        for (ArticleState item : ArticleState.values()) {
            if (item.value.equals(value)) {
                return item;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
