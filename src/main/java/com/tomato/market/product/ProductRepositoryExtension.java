package com.tomato.market.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ProductRepositoryExtension {
    Page<Product> findByKeyword(String keyword, Pageable pageable);
}
