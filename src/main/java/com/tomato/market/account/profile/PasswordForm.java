package com.tomato.market.account.profile;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PasswordForm {

    @Length(min = 8, max = 30)
    private String newPassword;

    @Length(min = 8, max = 30)
    private String newPassword2;
}
