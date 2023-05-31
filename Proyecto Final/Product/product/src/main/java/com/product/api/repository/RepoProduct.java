package com.product.api.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.product.api.entity.Product;

@Repository
public interface RepoProduct extends JpaRepository<Product, Integer>{
	
	// 3. Implementar la firma de un método que permita consultar un producto por su código GTIN y con estatus 1
	@Query(value = "SELECT * FROM product WHERE gtin = :gtin AND status = 1", nativeQuery = true)
	Product getProduct(@Param("gtin") String gtin);

	// Implementación extra para poder buscar un producto por su nombre y con estatus 1
	@Query(value = "SELECT * FROM product WHERE product = :product AND status = 1", nativeQuery = true)
	Product getProductByName(@Param("product") String product);

	// Implementación extra para consultar un producto por su código GTIN sin importar su estatus
	@Query(value = "SELECT * FROM product WHERE gtin = :gtin", nativeQuery = true)
	Product obtainProduct(@Param("gtin") String gtin);

	// Implementación extra para poder buscar un producto por su nombre sin importar su estatus
	@Query(value = "SELECT * FROM product WHERE product = :product", nativeQuery = true)
	Product obtainProductByName(@Param("product") String product);

	//Implementación súper extra xd para poder encontrar un producto por su id y con estatus 1
	@Query(value = "SELECT * FROM product WHERE product_id = :product_id AND status = 1", nativeQuery = true)
	Product getProductById(@Param("product_id") Integer product_id);

	// Implementación extra para poder crear productos
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO product (gtin,product,price,description,stock,category_id,status)" + 
					"VALUES (:gtin,:product,:price,:description,:stock,:category_id,1)", nativeQuery = true)
	void createProduct(@Param("gtin") String gtin, @Param("product") String product, @Param("price") double price,
						@Param("description") String description, @Param("stock") int stock, 
						@Param("category_id") int category_id);
	
	@Modifying
    @Transactional
    @Query(value = "UPDATE product SET status = 1 WHERE product_id = :product_id", nativeQuery = true)
    Integer activateProduct(@Param("product_id") Integer product_id);
	
	@Modifying
	@Transactional
	@Query(value ="UPDATE product "
					+ "SET gtin = :gtin, "
						+ "product = :product, "
						+ "description = :description, "
						+ "price = :price, "
						+ "stock = :stock, "
						+ "status = 1, "
						+ "category_id = :category_id "
					+ "WHERE product_id = :product_id", nativeQuery = true)
	Integer updateProduct(
			@Param("product_id") Integer product_id,
			@Param("gtin") String gtin, 
			@Param("product") String product, 
			@Param("description") String description, 
			@Param("price") Double price, 
			@Param("stock") Integer stock,
			@Param("category_id") Integer category_id
		);
	
	@Modifying
	@Transactional
	@Query(value ="UPDATE product SET status = 0 WHERE product_id = :product_id AND status = 1", nativeQuery = true)
	Integer deleteProduct(@Param("product_id") Integer product_id);
	
	@Modifying
	@Transactional
	@Query(value ="UPDATE product SET stock = :stock WHERE gtin = :gtin AND status = 1", nativeQuery = true)
	Integer updateProductStock(@Param("gtin") String gtin, @Param("stock") Integer stock);

}
