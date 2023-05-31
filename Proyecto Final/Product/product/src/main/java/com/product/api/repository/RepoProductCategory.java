package com.product.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.product.api.dto.DtoProductCategory;

@Repository
public interface RepoProductCategory extends JpaRepository<DtoProductCategory, Integer> {
    
    @Modifying
	@Transactional
	@Query(value = "UPDATE product SET category_id = :category_id WHERE gtin = :gtin AND status = 1", nativeQuery = true)
	Integer updateProductCategory(@Param("category_id") Integer category_id, @Param("gtin") String gtin);

}
