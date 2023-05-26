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

		 // Consultamos si el cart tiene productos o no

		List<Cart> carrito = repoCart.findByRfcAndStatus(rfc, 1);
		
		if (carrito.isEmpty()){
			throw new ApiException(HttpStatus.NOT_FOUND, "cart has no items");
		}

		// Creamos una base pasa la factura, porque la creación de items la necesita
		Invoice inv = new Invoice();
		inv.setRfc(rfc);
		inv.setTotal(0.0);
		inv.setTaxes(0.0);
		inv.setSubtotal(0.0);
		LocalDateTime hora = LocalDateTime.now();
		inv.setCreated_at(hora);
		inv.setStatus(1);
		repo.save(inv);

		Double totalProducts = 0.0, totalTaxes = 0.0, totalSubs = 0.0;

		// Creamos cada uno de los items
		for(int i = 0; i < carrito.size(); i++){
			Double unit_price = productCl.getProduct(carrito.get(i).getGtin()).getBody().getPrice();
			Double total = unit_price * carrito.get(i).getQuantity();
			Double taxes = (total * 16) / 100;
			Double subtotal = total - taxes;

			// Para actualizar el invoice
			totalProducts += total;
			totalTaxes += taxes;
			totalSubs += subtotal;

			repoItem.createItem(inv.getInvoice_id(), carrito.get(i).getGtin(), carrito.get(i).getQuantity(), 
								unit_price, total, taxes, subtotal);
		}

		repo.updateProduct(inv.getInvoice_id(), totalProducts, totalTaxes, totalSubs);

		// Restamos el stock del producto utilizando el método updateProductStock
		for (int i = 0; i < carrito.size(); i++){
			String gtin = carrito.get(i).getGtin();
			Integer quantity = carrito.get(i).getQuantity();
			productCl.updateProductStock(gtin, quantity);
		}

		// Vaciamos el carrito del cliente
		repoCart.clearCart(rfc);
		
		return new ApiResponse("invoice generated");
	}

}
