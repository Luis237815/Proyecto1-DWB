package com.invoice.api.dto;

/*
 * Requerimiento 3
 * Agregar atributos de clase para la validación del producto
 */
public class DtoProduct {

    private String gtin; // Atributo para almacenar el código GTIN del producto
	private Integer stock; // Atributo para almacenar la cantidad de stock disponible 
	private Double price; // Atributo para almacenar el precio del producto

	//Métodos getters y setters de los atributos

    public String getGtin() {
		return gtin;
	}

	public void setGtin(String gtin) {
		this.gtin = gtin;
	}

	public Integer getStock(){
		return stock;
	}

	public void setStock(Integer stock){
		this.stock = stock;
	}

	public Double getPrice(){
		return price;
	}

	public void setPrice(Double price){
		this.price = price;
	}

}
