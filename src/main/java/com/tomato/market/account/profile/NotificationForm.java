package com.tomato.market.account.profile;

import com.tomato.market.account.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationForm {
    private boolean newProductNotiByEmail;
    private boolean newProductNotiByWeb;

    public NotificationForm(Account account) {
        this.newProductNotiByEmail = account.isNewProductNotiByEmail();
        this.newProductNotiByWeb = account.isNewProductNotiByWeb();
    }
}
