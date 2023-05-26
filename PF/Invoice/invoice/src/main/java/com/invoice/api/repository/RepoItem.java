package com.invoice.api.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.invoice.api.entity.Item;

@Repository
public interface RepoItem extends JpaRepository<Item, Integer>{

	@Query(value ="SELECT * FROM item WHERE invoice_id = :invoice_id AND status = 1", nativeQuery = true)
	List<Item> getInvoiceItems(@Param("invoice_id") Integer invoice_id);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO item (invoice_id,gtin,quantity,unit_price,total,taxes,subtotal,status)" + 
			"VALUES (:invoice_id,:gtin,:quantity,:unit_price,:total,:taxes,:subtotal,1)", nativeQuery = true)
	void createItem(@Param("invoice_id") Integer invoice_id, @Param("gtin") String gtin, @Param("quantity") Integer quantity,
					@Param("unit_price") Double unit_price, @Param("total") Double total,@Param("taxes") Double taxes, 
					@Param("subtotal") Double subtotal);
}
