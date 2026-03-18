package com.htc.incidentmanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.htc.incidentmanagement.dto.CategoryResponse;
import com.htc.incidentmanagement.model.Category;
import com.htc.incidentmanagement.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/categories")
public class CategoryController {

        private final CategoryService categoryService;

        public CategoryController(CategoryService categoryService) {
                this.categoryService = categoryService;
        }

        @Operation(summary = "Get all categories", description = "Returns list of all ticket categories available in the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Categories retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)))
        })
        @GetMapping("/getAllCategories")
        public ResponseEntity<List<CategoryResponse>> getAllCategories() {
                return ResponseEntity.ok(categoryService.getAllCategories());
        }

        @Operation(summary = "Get category by ID", description = "Returns a single category matching the given ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Category found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
                        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
        })
        @GetMapping("/{id}")
        public ResponseEntity<Category> getById(@PathVariable Long id) {
                return ResponseEntity.ok(categoryService.getCategoryById(id));
        }

        @Operation(summary = "Create new category", description = "Creates a new ticket category. Category name must be unique")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Category created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
                        @ApiResponse(responseCode = "400", description = "Category name already exists", content = @Content)
        })
        @PostMapping("/create")
        public ResponseEntity<Category> create(@RequestBody Category category) {
                Category created = categoryService.createCategory(category);
                return ResponseEntity.status(HttpStatus.CREATED).body(created);
        }

        @Operation(summary = "Update category", description = "Updates an existing category's name by ID. New name must be unique")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Category updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
                        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content),
                        @ApiResponse(responseCode = "400", description = "Invalid input or duplicate name", content = @Content)
        })
        @PutMapping("/update/{id}")
        public ResponseEntity<Category> update(@PathVariable Long id, @RequestBody Category category) {
                return ResponseEntity.ok(categoryService.updateCategory(id, category));
        }

        @Operation(summary = "Delete category", description = "Deletes a category by ID. Cannot delete if tickets reference it")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Category deleted successfully", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content),
                        @ApiResponse(responseCode = "409", description = "Cannot delete - tickets exist", content = @Content)
        })
        @DeleteMapping("/delete/{id}")
        public ResponseEntity<Void> delete(@PathVariable Long id) {
                categoryService.deleteCategory(id);
                return ResponseEntity.noContent().build();
        }
}
