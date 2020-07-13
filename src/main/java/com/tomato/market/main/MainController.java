package com.tomato.market.main;

import com.tomato.market.account.CurrentAccount;
import com.tomato.market.account.domain.Account;
import com.tomato.market.product.Product;
import com.tomato.market.product.ProductRepository;
import com.tomato.market.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final ProductService productService;
    private final ProductRepository productRepository;

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {
        if(account != null){
            model.addAttribute(account);
        }
        List<Product> productList = productService.getProductListToShow();
        model.addAttribute(productList);

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "account/login";
    }

    @GetMapping("/search/product")  //QueryDSL 이용, Predicate 말고 확장 구현체를 사용함
    public String searchProduct(@CurrentAccount Account account, String keyword, Model model, @PageableDefault(size = 12, sort = "writeTime", direction = Sort.Direction.DESC) Pageable pageable){
        if (account != null) {
            model.addAttribute(account);
        }
        Page<Product> productPage = productRepository.findByKeyword(keyword, pageable);

        model.addAttribute("productPage", productPage);
        model.addAttribute("keyword", keyword);

        return "search-result";
    }
}
