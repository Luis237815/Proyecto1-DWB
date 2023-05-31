package com.invoice.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.invoice.api.dto.ApiResponse;
import com.invoice.api.dto.DtoCustomer;
import com.invoice.api.dto.DtoProduct;
import com.invoice.api.entity.Cart;
import com.invoice.api.repository.RepoCart;
import com.invoice.configuration.client.CustomerClient;
import com.invoice.configuration.client.ProductClient;
import com.invoice.exception.ApiException;

@Service
public class SvcCartImp implements SvcCart {

	@Autowired
	RepoCart repo;

	@Autowired
	CustomerClient customerCl;

	@Autowired
	ProductClient productCl;

	@Override
	public List<Cart> getCart(String rfc) {
		return repo.findByRfcAndStatus(rfc, 1);
	}

	/**
	 * Verifica si un producto existe mediante su código GTIN.
	 * @param gtin el código GTIN del producto a validar.
	 * @return true si el producto existe, false si no existe o si ocurre un error
	 *         al realizar la validación.
	 */
	private boolean isProductValid(String gtin) {
		try{
			// Llamada al método getProduct del objeto productCl para obtener información del producto
			ResponseEntity<DtoProduct> response = productCl.getProduct(gtin);
			// Verificar si el estado de la respuesta es igual a HttpStatus.OK
        	// Si es así, significa que el producto es válido y se devuelve true
			return response.getStatusCode() == HttpStatus.OK;
		} catch(Exception e) {
			// Si se produce alguna excepción durante la llamada al método o la validación,
        	// se captura y se devuelve false para indicar que el producto no es válido
			return false;
		}
	}

	// El método agrega un elemento al carrito de compras. Se verifica si el cliente
	// asociado al carrito existe. Si el cliente no existe,
	// se lanza una excepción ApiException con un mensaje indicando que el cliente
	// no existe
	@Override
	public ApiResponse addToCart(Cart cart) {
		if (!validateCustomer(cart.getRfc()))
			throw new ApiException(HttpStatus.BAD_REQUEST, "customer does not exist");

		/*
		 * Requerimiento 3
		 * Validar que el GTIN exista. Si existe, asignar el stock del producto a la
		 * variable product_stock
		 */
		if (!isProductValid(cart.getGtin()))
			throw new ApiException(HttpStatus.BAD_REQUEST, "product does not exist");

		// Se realiza una solicitud a la API productCl para obtener la información del
		// producto correspondiente al GTIN proporcionado en el carrito. Luego, se
		// obtiene el stock del producto a partir de la respuesta de la solicitud.
		ResponseEntity<DtoProduct> solicit = productCl.getProduct(cart.getGtin());
		Integer product_stock = solicit.getBody().getStock();

		// Se verifica si la cantidad solicitada en el carrito es mayor que el stock del
		// producto o si la cantidad es menor que 1. Si alguna de estas condiciones se
		// cumple, se lanza una excepción ApiException con un mensaje indicando que la
		// cantidad es inválida.
		if (cart.getQuantity() > product_stock || cart.getQuantity() < 1) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "quantity is invalid");
		}

		/*
		 * Requerimiento 4
		 * Validar si el producto ya había sido agregado al carrito para solo actualizar
		 * su cantidad
		 */
		// Se recorre el carrito de compras para verificar si el producto ya se
		// encuentra en el carrito
		for (int i=0; i<getCart(cart.getRfc()).size(); i++) {
			// Si se encuentra, se actualiza la cantidad sumando la cantidad actual del
			// carrito con la cantidad del nuevo elemento.
			if (getCart(cart.getRfc()).get(i).getGtin().equals(cart.getGtin())) {

				Integer updatedQuant = getCart(cart.getRfc()).get(i).getQuantity() + cart.getQuantity();
				if (updatedQuant > product_stock){
					// Si la nueva cantidad supera el stock del producto, se lanza una excepción
					// ApiException indicando que la cantidad es inválida.
					throw new ApiException(HttpStatus.BAD_REQUEST, "invalid quantity");
				} else{
					// Si la nueva cantidad no supera el stock, se actualiza la cantidad del
					// producto en el carrito y se devuelve una respuesta ApiResponse indicando que
					// la cantidad se ha actualizado.
					repo.updateQuantity(cart.getGtin(), updatedQuant);
					return new ApiResponse("quantity updated");
				}
			}
		}

		// En dado caso que el prod. no este en el carrito se establece el estado del
		// producto como "1" (agregado) y se guarda en el repositorio.
		cart.setStatus(1);
		repo.save(cart);

		// se devuelve una respuesta ApiResponse indicando que el elemento se ha
		// agregado al carrito.
		return new ApiResponse("item added");
	}

	@Override
	public ApiResponse removeFromCart(Integer cart_id) {
		if (repo.removeFromCart(cart_id) > 0)
			return new ApiResponse("item removed");
		else
			throw new ApiException(HttpStatus.BAD_REQUEST, "item cannot be removed");
	}

	@Override
	public ApiResponse clearCart(String rfc) {
		if (repo.clearCart(rfc) > 0)
			return new ApiResponse("cart removed");
		else
			throw new ApiException(HttpStatus.BAD_REQUEST, "cart cannot be removed");
	}

	private boolean validateCustomer(String rfc) {
		try {
			ResponseEntity<DtoCustomer> response = customerCl.getCustomer(rfc);	
			if(response.getStatusCode() == HttpStatus.OK)
				return true;
			else
				return false;
		} catch (Exception e) {
			//throw new ApiException(HttpStatus.BAD_REQUEST, "unable to retrieve customer information");
			return false;
		}
	}

	

}
