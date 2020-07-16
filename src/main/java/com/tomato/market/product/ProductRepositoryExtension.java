package com.tomato.market.product;

import com.tomato.market.location.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface ProductRepositoryExtension {
    Page<Product> findByKeyword(String keyword, Pageable pageable);

    List<Product> findFirst16ByAccountOrderByWriteTimeDesc(Set<Location> locations);

    Page<Product> findAllByAccount(Set<Location> locations, Pageable pageable);

}
