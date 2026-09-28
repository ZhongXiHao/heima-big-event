package com.cshlands.service;

import com.cshlands.dto.CategoryDTO;
import com.cshlands.vo.CategoryVO;

import java.util.List;

public interface CategoryService {
    CategoryVO addCategory(CategoryDTO dto);

    List<CategoryVO> getAllCategories();

    CategoryVO getCategoryById(Integer categoryId);

    CategoryVO updateCategoryById(Integer categoryId, CategoryDTO dto);

    void deleteCategoryById(Integer categoryId);
}
