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
    @Length(min = 8, max = 20)
    private String password;

    @NotBlank
    @Length(min = 8, max = 20)
    private String password2;

    @NotBlank
    @Pattern(regexp = "^[가-힣0-9]{2,8}|[a-zA-Z0-9]{3,16}$")  //TODO regexp 새로 작성
    private String nickname;

    @NotBlank
    @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$")   //TODO regexp 새로 작성 (특수문자 허용 여부)
    private String phone;

}