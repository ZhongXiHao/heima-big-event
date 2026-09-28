package com.cshlands.service;

import com.cshlands.dto.CreateCategoryDTO;
import com.cshlands.pojo.Category;
import com.cshlands.pojo.Result;
import com.cshlands.vo.CategoryVO;
import com.cshlands.vo.PageArticleVO;

import java.util.List;

public interface CategoryService {
    CategoryVO addCategory(CreateCategoryDTO dto);

    List<CategoryVO> getAllCategories();

    CategoryVO getCategoryById(Integer categoryId);

    CategoryVO updateCategoryById(Integer categoryId, CreateCategoryDTO dto);

    void deleteCategoryById(Integer categoryId);
}
