package com.htc.incidentmanagement.service;

import java.util.List;

import com.htc.incidentmanagement.dto.CategoryResponse;
import com.htc.incidentmanagement.model.Category;

public interface CategoryService {

    List<CategoryResponse> getAllCategories();

    Category getCategoryById(Long id);

    Category createCategory(Category category);

    Category updateCategory(Long id, Category category);

    void deleteCategory(Long id);
}
