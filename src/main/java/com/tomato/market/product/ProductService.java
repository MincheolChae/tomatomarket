package com.tomato.market.product;

import com.tomato.market.account.domain.Account;
import com.tomato.market.account.domain.AccountRepository;
import com.tomato.market.location.Location;
import com.tomato.market.product.event.ProductCreatedEvent;
import com.tomato.market.tag.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Transactional
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AccountRepository accountRepository;

    public Product createNewProduct(Product product, Account account) {
        Product newProduct = productRepository.save(product);
        newProduct.makeWriterAndTime(account);
        newProduct.makeRepresentativeImage();
        account.addProduct(newProduct);

        return newProduct;
    }

    public void applyNewProductNotification(Product product){
        if(!product.isNotified() && !product.isSoldOut()) {
            applicationEventPublisher.publishEvent(new ProductCreatedEvent(product));
            product.setNotified(true);
        }
    }

    public Product getProduct(String id) {
        return productRepository.findById(Long.parseLong(id.trim())).orElseThrow(IllegalArgumentException::new);
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
        productRepository.deleteById(product.getId());
    }

    public void soldOut(Product product) {
        product.soldOut();
        productRepository.save(product);
    }

    public List<Product> getProductListToShow() {
        List<Product> productList = productRepository.findFirst16ByOrderByWriteTimeDesc();
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

        if (count == 0) {
            imageList.add("<img src=/img/default_b72974fa-1b77-490a-9642-e6b2a443fff6.jpg>");
        } else {
            for (int i = 0; i < count; i++) {
                String image = (i == count - 1) ? product.getImages().substring(indexArray[i]) : product.getImages().substring(indexArray[i], indexArray[i + 1]);
                imageList.add(image);
            }
        }
        return imageList;
    }

    public Set<Tag> getTags(String id) {
        Optional<Product> productFoundById = productRepository.findById(Long.parseLong(id.trim()));
        return productFoundById.orElseThrow(NoSuchElementException::new).getTags();
    }

    public void addTag(String id, Tag tag) {
        Optional<Product> productFoundById = productRepository.findById(Long.parseLong(id.trim()));
        productFoundById.ifPresent(product -> product.getTags().add(tag));
    }

    public void removeTag(String id, Tag tag) {
        Optional<Product> productFoundById = productRepository.findById(Long.parseLong(id.trim()));
        productFoundById.ifPresent(product -> product.getTags().remove(tag));
    }

    public Set<Location> getLocations(String id) {
        Optional<Product> productFoundById = productRepository.findById(Long.parseLong(id.trim()));
        return productFoundById.orElseThrow(NoSuchElementException::new).getLocations();
    }

    public void addLocation(String id, Location location) {
        Optional<Product> productFoundById = productRepository.findById(Long.parseLong(id.trim()));
        productFoundById.ifPresent(product -> product.getLocations().add(location));
    }

    public void removeLocation(String id, Location location) {
        Optional<Product> productFoundById = productRepository.findById(Long.parseLong(id.trim()));
        productFoundById.ifPresent(product -> product.getLocations().remove(location));
    }

    public List<Product> getPopularProductListToShow() {
        List<Product> popularProductList = productRepository.findFirst10ByIsSoldOutOrderByLikeCountDesc(false);
        for (Product product : popularProductList) {
            product.makeRepresentativeImage();
        }
        return popularProductList;
    }

    public void deleteLikedAccount(Product product) {
        product.getAccounts().forEach(account -> account.removeProductLiked(product));
        product.getAccounts().clear();
    }

    public List<Product> getMyTownProductListToShow(Account account) {
        Account currentAccount = accountRepository.findAccountWithLocationsById(account.getId());
        return productRepository.findFirst16ByAccountOrderByWriteTimeDesc(currentAccount.getLocations());

    }


}
