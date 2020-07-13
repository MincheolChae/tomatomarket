package com.tomato.market.product.event;

import com.tomato.market.product.Product;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

@Data
public class ProductCreatedEvent {

    private Product product;

    public ProductCreatedEvent(Product product) {
        this.product = product;
    }
}
