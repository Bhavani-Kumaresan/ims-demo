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

/**
 * REST API Controller for managing ticket categories in the Incident Management System.
 * 
 * <h2>Overview</h2>
 * This controller provides RESTful endpoints for CRUD operations on ticket categories.
 * Categories are used to classify and organize incident tickets within the system.
 * 
 * <h2>Security</h2>
 * All endpoints in this controller are restricted to users with the <code>ADMIN</code> role.
 * Authentication is enforced via Bearer token in the Authorization header.
 * 
 * <h2>Endpoints</h2>
 * <ul>
 *   <li><strong>GET /api/categories/getAllCategories</strong> - Retrieve all available categories</li>
 *   <li><strong>GET /api/categories/{id}</strong> - Retrieve a specific category by ID</li>
 *   <li><strong>POST /api/categories/create</strong> - Create a new category (category name must be unique)</li>
 *   <li><strong>PUT /api/categories/update/{id}</strong> - Update an existing category</li>
 *   <li><strong>DELETE /api/categories/{id}</strong> - Delete a category (blocked if tickets reference it)</li>
 * </ul>
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li><b>Unique Category Names:</b> The system enforces unique category names to prevent duplicates</li>
 *   <li><b>Referential Integrity:</b> Categories cannot be deleted if they are referenced by existing tickets</li>
 *   <li><b>Comprehensive Error Handling:</b> Custom exceptions for entity not found, duplicate names, and invalid input</li>
 *   <li><b>OpenAPI Documentation:</b> All endpoints are documented with Swagger/OpenAPI annotations for API discovery</li>
 * </ul>
 * 
 * <h2>Typical Usage Flow</h2>
 * <ol>
 *   <li>Admin users authenticate and receive a Bearer token</li>
 *   <li>Admin retrieves all categories for reference or management purposes</li>
 *   <li>Admin creates new categories as needed for ticket classification</li>
 *   <li>Admin updates category details when changes are required</li>
 *   <li>Admin deletes categories when they are no longer needed (if no tickets reference them)</li>
 * </ol>
 * 
 * <h2>Data Model</h2>
 * <ul>
 *   <li><b>Category:</b> Contains category ID and name properties used to classify tickets</li>
 *   <li><b>CategoryResponse:</b> DTO used in API responses for retrieving category lists</li>
 * </ul>
 * 
 * @see com.htc.incidentmanagement.service.CategoryService
 * @see com.htc.incidentmanagement.model.Category
 * @see com.htc.incidentmanagement.dto.CategoryResponse
 * 
 * @author Incident Management System Team
 * @version 1.0
 */
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

        /**
         * Updates an existing category by its ID.
         * 
         * This endpoint allows clients to update a category's information. The new category name
         * must be unique within the system. The category is identified by its ID path variable.
         * 
         * @param id the unique identifier of the category to be updated (must exist in the system)
         * @param category the Category object containing the updated information
         * 
         * @return ResponseEntity containing the updated Category object with HTTP status 200 (OK)
         *         if the update is successful
         * 
         * @throws EntityNotFoundException if no category with the given ID is found (HTTP 404)
         * @throws DuplicateNameException if the new category name already exists in the system (HTTP 400)
         * @throws InvalidInputException if the provided input is invalid or malformed (HTTP 400)
         * 
         * @see Category
         * @see com.htc.incidentmanagement.service.CategoryService#updateCategory(Long, Category)
         */
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
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
                categoryService.deleteCategory(id);
                return ResponseEntity.noContent().build();
        }
}
