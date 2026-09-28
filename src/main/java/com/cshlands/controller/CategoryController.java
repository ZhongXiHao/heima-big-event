package com.cshlands.controller;

import com.cshlands.dto.CategoryDTO;
import com.cshlands.pojo.Result;
import com.cshlands.service.CategoryService;
import com.cshlands.vo.CategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<Result<CategoryVO>> addCategory(@RequestBody @Validated CategoryDTO dto) {
        CategoryVO categoryVO = categoryService.addCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.success(categoryVO));
    }

    @GetMapping
    public Result<List<CategoryVO>> getAllCategories() {
        List<CategoryVO> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    @GetMapping("/{id}")
    public Result<CategoryVO> getCategoryById(@PathVariable Integer id) {
        CategoryVO categoryVO = categoryService.getCategoryById(id);
        return Result.success(categoryVO);
    }

    @PutMapping("/{id}")
    public Result<CategoryVO> updateCategoryById(@PathVariable Integer id, @RequestBody @Validated CategoryDTO dto) {
        CategoryVO categoryVO = categoryService.updateCategoryById(id, dto);
        return Result.success(categoryVO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable Integer id) {
        categoryService.deleteCategoryById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
