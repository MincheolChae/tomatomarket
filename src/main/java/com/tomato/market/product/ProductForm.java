package com.tomato.market.product;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class ProductForm {

    private String id;  //게시글 번호

    @NotBlank
    @Length(min = 1, max = 35)
    private String title;

    @NotBlank
    private String category;

    @NotBlank
    private String price;

    private String images;

    @NotBlank
    private String description;


}
