package com.cshlands.service.serviceImpl;

import com.cshlands.dto.CreateCategoryDTO;
import com.cshlands.exception.BusinessException;
import com.cshlands.mapper.CategoryMapper;
import com.cshlands.pojo.Category;
import com.cshlands.pojo.Result;
import com.cshlands.pojo.User;
import com.cshlands.service.CategoryService;
import com.cshlands.utils.JwtUtil;
import com.cshlands.utils.ThreadLocalUtil;
import com.cshlands.vo.CategoryVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper, JwtUtil jwtUtil) {
        this.categoryMapper = categoryMapper;
    }


    @Override
    public CategoryVO addCategory(CreateCategoryDTO dto) {
        Integer userId = getCurrentUserId();

        Category category = new Category();
        category.setCategoryName(dto.getCategoryName());
        category.setCategoryAlias(dto.getCategoryAlias());
        category.setCreateUser(userId);
        category.setCreateTime(LocalDateTime.now().withNano(0));
        category.setUpdateTime(LocalDateTime.now().withNano(0));
        categoryMapper.insert(category);
        return toVO(category);
    }

    @Override
    public List<CategoryVO> getAllCategories() {
        Integer userId = getCurrentUserId();

        List<Category> categories = categoryMapper.selectAll(userId);
        return categories.stream().map(this::toVO).toList();
    }

    @Override
    public CategoryVO getCategoryById(Integer categoryId) {
        Integer userId = getCurrentUserId();
        Category category = getOwnedCategoryOrThrow(categoryId, userId);
        return toVO(category);
    }

    @Override
    public CategoryVO updateCategoryById(Integer categoryId, CreateCategoryDTO dto) {
        Integer userId = getCurrentUserId();
        Category category = getOwnedCategoryOrThrow(categoryId, userId);
        category.setCategoryAlias(dto.getCategoryAlias());
        category.setCategoryName(dto.getCategoryName());
        category.setUpdateTime(LocalDateTime.now().withNano(0));
        categoryMapper.updateById(category);
        return toVO(category);
    }

    @Override
    public void deleteCategoryById(Integer categoryId) {
        Integer userId = getCurrentUserId();
        getOwnedCategoryOrThrow(categoryId, userId);
        categoryMapper.deleteById(categoryId);
    }

    private CategoryVO toVO(Category category) {
        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setId(category.getId());
        categoryVO.setCategoryName(category.getCategoryName());
        categoryVO.setCategoryAlias(category.getCategoryAlias());
        categoryVO.setCreateTime(category.getCreateTime());
        categoryVO.setUpdateTime(category.getUpdateTime());
        return categoryVO;
    }

    private Category getOwnedCategoryOrThrow(Integer categoryId, Integer userId) {
        Category category = categoryMapper.selectById(categoryId);
        if (category == null || !category.getCreateUser().equals(userId)) {
            throw BusinessException.notFound("分类不存在");
        }
        return category;
    }

    private Integer getCurrentUserId() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        return ((Number) claims.get("id")).intValue();
    }

}
