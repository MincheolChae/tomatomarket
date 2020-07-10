package com.tomato.market.main;

import com.tomato.market.account.CurrentAccount;
import com.tomato.market.account.domain.Account;
import com.tomato.market.product.Product;
import com.tomato.market.product.ProductRepository;
import com.tomato.market.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.ElementCollection;
import javax.persistence.OrderBy;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final ProductService productService;

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {
        List<Product> productList = productService.getProductListToShow();
        model.addAttribute(productList);

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "account/login";
    }
}
