package com.tomato.market.product;

import com.tomato.market.account.domain.Account;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public Product createNewProduct(Product product, Account account) {
        Product newProduct = productRepository.save(product);
        newProduct.setWriter(account);
        newProduct.setWriteTime(LocalDateTime.now());
        return newProduct;
    }

    public Product getProduct(String id) {
        Product product = productRepository.findById(Long.parseLong(id.trim())).orElseThrow(IllegalArgumentException::new);

        return product;
    }

    public void checkIfAccountIsWriter(Account account, Product product) {
        if(!account.getNickname().equals(product.getWriter().getNickname())){
            throw new IllegalArgumentException();
        }
    }

    public Product updateProduct(ProductForm productForm ,Product product) {
        modelMapper.map(productForm, product);
        product.setUpdateTime(LocalDateTime.now());
        Product updateProduct = productRepository.save(product);
        return updateProduct;
    }

    public void deleteProduct(Product product) {
        productRepository.delete(product);
    }

    public void soldOut(Product product) {
        product.soldOut();
        productRepository.save(product);
    }

    public List<Product> getProductListToShow() {
        List<Product> productList = productRepository.findTop12ByOrderByIdDesc();
        for(Product product : productList) {
            if(product.getImages().contains("<img ") && product.getImages().contains("\">")) {
                String image = product.getImages().substring(product.getImages().indexOf("<img "), product.getImages().indexOf("\">") + 2);
                product.setRepresentativeImage(image);
            } else {
                product.setRepresentativeImage("<img src=/img/default_b72974fa-1b77-490a-9642-e6b2a443fff6.jpg>");
            }
        }
        return productList;
    }
}
