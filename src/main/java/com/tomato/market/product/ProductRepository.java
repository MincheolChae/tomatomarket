package com.tomato.market.product;

import com.tomato.market.location.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryExtension {

    List<Product> findFirst16ByOrderByWriteTimeDesc();

    @EntityGraph(value = "Product.withTagsAndLocations", type = EntityGraph.EntityGraphType.FETCH)
    Product findProductWithTagsAndLocationsById(Long id);

    List<Product> findFirst10ByIsSoldOutOrderByLikeCountDesc(boolean isSoldOut);

    Page<Product> findAll(Pageable pageable);
}
