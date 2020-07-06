package com.tomato.market.account;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FindIdForm {
    @NotBlank
    private String name;

    @NotBlank
    private String phone;
}
