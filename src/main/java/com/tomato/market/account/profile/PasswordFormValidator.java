package com.tomato.market.account.profile;

import com.tomato.market.account.domain.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class PasswordFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(PasswordForm.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        PasswordForm passwordForm = (PasswordForm) o;

        if(!passwordForm.getNewPassword().equals(passwordForm.getNewPassword2())){
            errors.rejectValue("newPassword", "invalid.newPassword", "새 비밀번호가 일치하지 않습니다.");
        }
    }
}
