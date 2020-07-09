package com.tomato.market.product;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class ProductForm {

    private String id;  //게시글 번호

    @NotBlank
    @Length(min = 1, max = 50)
    private String title;

    @NotBlank
    private String category;

    @NotBlank
    private String price;

    private String images;

    @NotBlank
    private String description;

    //TODO 태그 등록 -> 다른페이지로 넘길까?
    //TODO 동네 등록

}
