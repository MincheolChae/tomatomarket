package com.tomato.market.account.event;

import com.tomato.market.account.domain.Account;
import lombok.Data;

@Data
public class SendingEmailEvent {
    private Account account;

    public SendingEmailEvent(Account account) {
        this.account = account;
    }
}
