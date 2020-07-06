package com.tomato.market.account;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SignUpForm {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min = 8, max = 30)
    private String password;

    @NotBlank
    @Length(min = 8, max = 30)
    private String password2;

    @NotBlank
    @Length(min = 2, max = 16)
    @Pattern(regexp = "^[가-힣a-zA-Z]{2,16}$")
    private String name;

    @NotBlank
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,16}$")
    private String nickname;

    @NotBlank
    @Pattern(regexp = "^01(?:0|1|[6-9])(\\d{3}|\\d{4})(\\d{4})$")
    private String phone;

}