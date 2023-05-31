package com.product.api.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;

// Escorza Sánchez Jorge Luis

@Entity
@Table(name = "category")
public class Category{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    Integer category_id;
    
    @NotNull
    @Column(name = "category")
    String category;

    @Column(name = "acronym")
    String acronym;

    @Column(name = "status")
    @Min(value = 0, message = "status must be 0 or 1")
    @Max(value = 1, message = "status must be 0 or 1")
    @JsonIgnore
    Integer status;

    // Constructor vacío
    public Category(){

    }

    // Constructor con parámetros.
    public Category(Integer catId, String cat, String acro){
        this.category_id = catId;
        this.category = cat;
        this.acronym = acro;
    }

    /* GETTERS */

    // Método que obtiene el id.
    public Integer getId(){
        return category_id;
    }

    // Método que obtiene el nombre de la categoría.
    public String getCategory(){
        return category;
    }

    // Método que obtiene el acrónimo de la categoría.
    public String getAcronym(){
        return acronym;
    }

    // Método que obtiene el status de la categoría.
    public Integer getStatus(){
        return status;
    }

    /* SETTERS */

    // Método que asigna un valor al id.
    public void setId(Integer category_id){
        this.category_id = category_id;
    }

    // Método que asigna un nombre a la categoría.
    public void setCategory(String category){
        this.category = category;
    }

    // Método que asigna un acrónimo.
    public void setAcronym(String acronym){
        this.acronym = acronym;
    }

    // Método que asigna un status.
    public void setStatus(Integer status){
        this.status = status;
    }

    // Método toString
    public String toString(){
        return "Region [category_id = " + category_id + ", category = " + category + ", acronym = " + acronym + ", status = " + status + "]";
    }

}