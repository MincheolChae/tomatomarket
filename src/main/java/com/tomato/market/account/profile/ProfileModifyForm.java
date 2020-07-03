package com.tomato.market.account.profile;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class ProfileModifyForm {

//    @NotBlank
//    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{3,12}$")
//    private String nickname;

    @NotBlank
    @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$")  //TODO SignUpForm 과 패턴 일치시키기, 정규식 수정하기
    private String phone;

    private String location;

    private String profileImage;
}
