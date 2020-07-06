//package com.tomato.market.account.profile;
//
//import com.tomato.market.account.domain.AccountRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.validation.Errors;
//import org.springframework.validation.Validator;
//
//@Component
//@RequiredArgsConstructor
//public class ProfileModifyFormValidator implements Validator {
//
//    private final AccountRepository accountRepository;
//
//    @Override
//    public boolean supports(Class<?> aClass) {
//        return ProfileModifyForm.class.isAssignableFrom(aClass);
//    }
//
//    @Override
//    public void validate(Object o, Errors errors) {
//        ProfileModifyForm profileModifyForm = (ProfileModifyForm)o;
//        if(accountRepository.existsByNickname(profileModifyForm.getNickname()) ){
//            errors.rejectValue("nickname", "invalid.nickname", new Object[]{profileModifyForm.getNickname()}, "이미 사용중인 닉네임입니다.");
//        }
//    }
//}
