package com.example.watch.repository;

import com.example.watch.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // Lấy danh mục chưa xóa theo ID
    @Query("SELECT c FROM Category c WHERE c.categoryId = ?1 AND c.deleted = false")
    Optional<Category> findActiveById(Integer categoryId);

    // Lấy tất cả danh mục chưa xóa
    @Query("SELECT c FROM Category c WHERE c.deleted = false")
    List<Category> findAllActiveCategories();

    // Kiểm tra danh mục có con không
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.parentCategory.categoryId = ?1 AND c.deleted = false")
    boolean hasChildren(Integer parentId);

    // Xóa mềm
    @Modifying
    @Query("UPDATE Category c SET c.deleted = true WHERE c.categoryId = ?1")
    void softDelete(Integer categoryId);
}