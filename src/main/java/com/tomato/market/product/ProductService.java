package com.tomato.market.product;

import com.tomato.market.account.domain.Account;
import com.tomato.market.account.domain.AccountRepository;
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

    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public Product createNewProduct(Product product, Account account) {
        Product newProduct = productRepository.save(product);
        newProduct.makeWriterAndTime(account);
        newProduct.makeRepresentativeImage();
        account.addProduct(newProduct);

        return newProduct;
    }

    public Product getProduct(String id) {
        Product product = productRepository.findById(Long.parseLong(id.trim())).orElseThrow(IllegalArgumentException::new);

        return product;
    }

    public void checkIfAccountIsWriter(Account account, Product product) {
        if (!account.getNickname().equals(product.getWriter().getNickname())) {
            throw new IllegalArgumentException();
        }
    }

    public Product updateProduct(ProductForm productForm, Product product) {
        modelMapper.map(productForm, product);
        product.setUpdateTime(LocalDateTime.now());
        return productRepository.save(product);
    }

    public void deleteProduct(Account account, Product product) {
        account.deleteProduct(product);
        productRepository.delete(product);
    }

    public void soldOut(Product product) {
        product.soldOut();
        productRepository.save(product);
    }

    public List<Product> getProductListToShow() {
        List<Product> productList = productRepository.findTop12ByOrderByIdDesc();
        for (Product product : productList) {
            product.makeRepresentativeImage();
        }
        return productList;
    }

    public List<String> getSeparatedImages(Product product) {
        List<String> imageList = new ArrayList<>();
        int count = 0;
        int fromIndex = -1;
        int index;
        int[] indexArray = new int[10];
        while ((index = product.getImages().indexOf("<img ", fromIndex + 1)) >= 0) {
            fromIndex = index;
            indexArray[count++] = fromIndex;
        }

        switch (count) {
            case 0:
                imageList.add("<img src=/img/default_b72974fa-1b77-490a-9642-e6b2a443fff6.jpg>");
                break;

            default:
                for (int i = 0; i < count; i++) {
                    String image = (i == count - 1) ? product.getImages().substring(indexArray[i]) : product.getImages().substring(indexArray[i], indexArray[i + 1]);
                    imageList.add(image);
                }
                break;
        }
        return imageList;
    }
}
