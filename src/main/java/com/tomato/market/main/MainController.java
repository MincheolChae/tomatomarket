package com.tomato.market.main;

import com.tomato.market.account.CurrentAccount;
import com.tomato.market.account.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {
        if(account != null) {
            model.addAttribute(account);
            return "index";
        }

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "account/login";
    }
}
