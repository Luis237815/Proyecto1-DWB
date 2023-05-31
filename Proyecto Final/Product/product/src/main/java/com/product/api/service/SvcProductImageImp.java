package com.product.api.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.product.api.dto.ApiResponse;
import com.product.api.entity.Product;
import com.product.api.entity.ProductImage;
import com.product.api.repository.RepoProduct;
import com.product.api.repository.RepoProductImage;
import com.product.exception.ApiException;

@Service
@PropertySource("classpath:configuration/path.config")
public class SvcProductImageImp implements SvcProductImage{

    @Autowired
    RepoProductImage repo;

    @Value("${product.images.path}")
    private String path;

    //Para buscar si existe el producto
    @Autowired
    RepoProduct repo2;

    @Override
    public List<ProductImage> getProductImages(Integer product_id) {
        return repo.findByProductId(product_id);
    }

    @Override
    public ApiResponse createProductImage(ProductImage in) {
        try {

            Product product = repo2.getProductById(in.getProduct_id());

            if(product == null){
                throw new ApiException(HttpStatus.NOT_FOUND, "product does not exist");
            }

            File folder = new File(path + "/" + in.getProduct_id());

            if (!folder.exists())
                folder.mkdirs();
            
            String file = path + in.getProduct_id() + "/img_" + new Date().getTime() + ".bmp";

            //La imágen está codificada en base 64, entonces la decodificamos para pasarla a un tipo archivo
            
            byte[] data = Base64.getMimeDecoder().decode(in.getImage().substring(in.getImage().indexOf(",")+1, in.getImage().length()));
            try(OutputStream stream = new FileOutputStream(file)) {
                //Guardamos la imagen en el sistema de archivos
                stream.write(data);
            }

            in.setStatus(1);
            in.setImage(in.getProduct_id() + "/img_" + new Date().getTime() + ".bmp");

            repo.save(in);
            return new ApiResponse("product image created");
        } catch (Exception e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "product image cannot be created" + ". " + e.getLocalizedMessage());
        }
    }

    @Override
    public ApiResponse deleteProductImage(Integer id) {
        if (repo.deleteProductImage(id) > 0){
            return new ApiResponse("product image removed");
        }else{
            throw new ApiException(HttpStatus.BAD_REQUEST, "product image cannot be deleted");
        }
    }

    
    
    

}
