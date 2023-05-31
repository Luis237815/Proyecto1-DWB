package com.product.api.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "product")
public class DtoProductCategory {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty("category_id")
	@Column(name = "category_id")
	private Integer category_id;

	public Integer getCategory_id() {
		return category_id;
	}

	public void setCategory_id(Integer category_id) {
		this.category_id = category_id;
	}

}
