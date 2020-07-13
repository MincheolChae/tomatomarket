package com.tomato.market.product;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryExtension {

    List<Product> findFirst16ByOrderByWriteTimeDesc();

    @EntityGraph(value = "Product.withTagsAndLocations", type = EntityGraph.EntityGraphType.FETCH)
    Product findProductWithTagsAndLocationsById(Long id);
}
