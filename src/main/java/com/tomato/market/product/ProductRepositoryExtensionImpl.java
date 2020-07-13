package com.tomato.market.product;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.tomato.market.location.Location;
import com.tomato.market.location.QLocation;
import com.tomato.market.tag.QTag;
import com.tomato.market.tag.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Set;

public class ProductRepositoryExtensionImpl extends QuerydslRepositorySupport implements ProductRepositoryExtension{

    public ProductRepositoryExtensionImpl() {
        super(Product.class);
    }

    @Override
    public Page<Product> findByKeyword(String keyword, Pageable pageable) {   // keyword == 검색어
        QProduct product = QProduct.product;
        JPQLQuery<Product> query = from(product).where(product.isSoldOut.isFalse()
                .and(product.title.containsIgnoreCase(keyword))
                .or(product.tags.any().title.containsIgnoreCase(keyword))
                .or(product.locations.any().city.containsIgnoreCase(keyword))
                .or(product.locations.any().province.containsIgnoreCase(keyword))
                .or(product.locations.any().unit.containsIgnoreCase(keyword))
                .or(product.category.containsIgnoreCase(keyword)))
                .leftJoin(product.tags, QTag.tag).fetchJoin()   //QueryDSL 사용시의 N+1 Select 문제를 해결하기 위한 leftJoin + fetchJoin + distinct
                .leftJoin(product.locations, QLocation.location).fetchJoin()
                .distinct();

        //Pageable을 이용한 페이지네이션
        JPQLQuery<Product> pageableQuery = getQuerydsl().applyPagination(pageable, query);  //페이징이 적용된 쿼리가 나옴
        QueryResults<Product> fetchResults = pageableQuery.fetchResults();   //query.fetch()는 그냥 데이터만 가져오고 fetchResults()를 해야 페이징이 적용된 데이터를 가져옴.
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());  //리턴 타입을 Page인터페이스로 바꾸고 Page를 구현한 PageImpl을 반환함(인자들 : 컨텐츠List, pageable, 조건에 맞는 전체 데이터 갯수)
    }

//    @Override
    public List<Product> findByAccount(Set<Tag> tags, Set<Location> locations) {
        QProduct product = QProduct.product;
        JPQLQuery<Product> query = from(product).where(product.isSoldOut.isFalse()
                .and(product.tags.any().in(tags))
                .and(product.locations.any().in(locations)))
                .leftJoin(product.tags, QTag.tag).fetchJoin()
                .leftJoin(product.locations, QLocation.location).fetchJoin()
                .orderBy(product.writeTime.desc())
                .distinct()
                .limit(12);
        return query.fetch();
    }
}
