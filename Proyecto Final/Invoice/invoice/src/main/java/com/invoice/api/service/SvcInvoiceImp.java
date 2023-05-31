package com.invoice.api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.invoice.api.dto.ApiResponse;
import com.invoice.api.entity.Cart;
import com.invoice.api.entity.Invoice;
import com.invoice.api.entity.Item;
import com.invoice.api.repository.RepoCart;
import com.invoice.api.repository.RepoInvoice;
import com.invoice.api.repository.RepoItem;
import com.invoice.configuration.client.ProductClient;
import com.invoice.exception.ApiException;

@Service
public class SvcInvoiceImp implements SvcInvoice {

	@Autowired
	RepoInvoice repo;
	
	@Autowired
	RepoItem repoItem;

	@Autowired
	RepoCart repoCart;

	@Autowired
	ProductClient productCl;

	@Override
	public List<Invoice> getInvoices(String rfc) {
		return repo.findByRfcAndStatus(rfc, 1);
	}

	@Override
	public List<Item> getInvoiceItems(Integer invoice_id) {
		return repoItem.getInvoiceItems(invoice_id);
	}

	@Override
	public ApiResponse generateInvoice(String rfc) {
		/*
		 * Requerimiento 5
		 * Implementar el método para generar una factura 
		 */
		List<Cart> auxCart = repoCart.findByRfcAndStatus(rfc, 1);
		
		if (auxCart.isEmpty()){
			throw new ApiException(HttpStatus.NOT_FOUND, "cart has no items");
		}

		// Creamos una instancia de Invoice para la nueva factura
		Invoice invoice = new Invoice();
		// Obtenemos la fecha y hora actual para establecer el campo 'created_at' de la factura
		LocalDateTime created_at = LocalDateTime.now();

		// Establecemos los valores iniciales de la factura
		invoice.setRfc(rfc); // Establece el RFC asociado a la factura
		invoice.setTotal(0.0); // Establece el total inicial de la factura en 0.0
		invoice.setTaxes(0.0); // Establece los impuestos iniciales de la factura en 0.0
		invoice.setSubtotal(0.0); // Establece el subtotal inicial de la factura en 0.0
		invoice.setCreated_at(created_at); // Establece la fecha y hora de creación de la factura
		invoice.setStatus(1); // Establece el estado de la factura como activa
		
		// Guardamos la instancia de la factura en la base de datos
		repo.save(invoice);

		// Estas variables nos sirven para poder calcular los totales de la invoice
		Double accumulatedProductTotal = 0.0; // Total acumulado de todos los productos en la factura 
		Double accumulatedTaxesTotal = 0.0; // Total acumulado de impuestos en la factura accumulatedTaxesTotal
		Double accumulatedSubsTotal = 0.0; // Total acumulado de subtotales en la factura accumulatedSubsTotal

		// Se crean los items de la factura
		for(int i = 0; i<auxCart.size(); i++){
			// Se obtiene el precio unitario del producto correspondiente al item
			Double unit_price = productCl.getProduct(auxCart.get(i).getGtin()).getBody().getPrice();
			
			// Calculamos el total, los impuestos y el subtotal para el item actual
			Double total = unit_price * auxCart.get(i).getQuantity();  
			Double taxes = (total * 16) / 100; // Cálculo de impuestos para el producto en la factura (16% del total)
			Double subtotal = total - taxes; // Cálculo del subtotal para el producto en la factura (total - impuestos)

			// Actualizamos los totales de la factura
			accumulatedProductTotal += total;
			accumulatedTaxesTotal += taxes;
			accumulatedSubsTotal += subtotal;

			//Creamos el item en la base de datos de la factura
			repoItem.createItem(invoice.getInvoice_id(), auxCart.get(i).getGtin(), auxCart.get(i).getQuantity(),unit_price, total, taxes, subtotal);
		}

		// Se actualizan los totales de la factura en la base de datos utilizando el método "updateProduct".
		// Se pasa el identificador de la factura y los totales acumulados de productos, impuestos y subtotal.
		repo.updateProduct(invoice.getInvoice_id(), accumulatedProductTotal, accumulatedTaxesTotal, accumulatedSubsTotal);

		// Se realiza la actualización del stock de cada producto en el carrito
		// Se recorre cada elemento del carrito y se obtiene el GTIN (Global Trade Item Number) y la cantidad del producto.
		// Luego se llama al método "updateProductStock" para restar la cantidad del producto al stock disponible.
		for (int i = 0; i < auxCart.size(); i++){
			String gtin = auxCart.get(i).getGtin();
			Integer quantity = auxCart.get(i).getQuantity();
			productCl.updateProductStock(gtin, quantity);
		}

		// Se vacía el carrito del cliente llamando al método "clearCart" del repositorio del carrito. Esto elimina todos los elementos del carrito asociados al RFC 
		repoCart.clearCart(rfc);
		
		return new ApiResponse("invoice generated");
	}

}
