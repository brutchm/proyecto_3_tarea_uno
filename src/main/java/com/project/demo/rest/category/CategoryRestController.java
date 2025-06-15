package com.project.demo.rest.category;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.category.CategoryRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/categories")
public class CategoryRestController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Category> categoriesPage = categoryRepository.findAll(pageable);

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(categoriesPage.getTotalPages());
        meta.setTotalElements(categoriesPage.getTotalElements());
        meta.setPageNumber(categoriesPage.getNumber() + 1);
        meta.setPageSize(categoriesPage.getSize());

        return new GlobalResponseHandler().handleResponse("Categories retrieved successfully",
                categoriesPage.getContent(), HttpStatus.OK, meta);
    }

    @GetMapping("/{categoryId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getById(@PathVariable Long categoryId, HttpServletRequest request) {
        Optional<Category> foundCategory = categoryRepository.findById(categoryId);
        if (foundCategory.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Category retrieved successfully",
                    foundCategory.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Category id " + categoryId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PostMapping("/category")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> createCategory(@RequestBody Category category, HttpServletRequest request) {
        Category savedCategory = categoryRepository.save(category);
        return new GlobalResponseHandler().handleResponse("Category created successfully",
                savedCategory, HttpStatus.CREATED, request);
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateCategory(@PathVariable Long categoryId,
                                            @RequestBody Category category,
                                            HttpServletRequest request) {
        Optional<Category> foundCategory = categoryRepository.findById(categoryId);
        if (foundCategory.isPresent()) {
            category.setId(foundCategory.get().getId());
            categoryRepository.save(category);
            return new GlobalResponseHandler().handleResponse("Category updated successfully",
                    category, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Category id " + categoryId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PatchMapping("/{categoryId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> patchCategory(@PathVariable Long categoryId,
                                           @RequestBody Category category,
                                           HttpServletRequest request) {
        Optional<Category> foundCategory = categoryRepository.findById(categoryId);
        if (foundCategory.isPresent()) {
            Category existing = foundCategory.get();
            if (category.getName() != null) existing.setName(category.getName());
            if (category.getDescription() != null) existing.setDescription(category.getDescription());
            categoryRepository.save(existing);
            return new GlobalResponseHandler().handleResponse("Category updated successfully",
                    existing, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Category id " + categoryId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }


    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId, HttpServletRequest request) {
        Optional<Category> foundCategory = categoryRepository.findById(categoryId);
        if (foundCategory.isPresent()) {
            categoryRepository.deleteById(categoryId);
            return new GlobalResponseHandler().handleResponse("Category deleted successfully",
                    foundCategory.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Category id " + categoryId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }
}
