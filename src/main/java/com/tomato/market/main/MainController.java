package com.tomato.market.main;

import com.tomato.market.account.CurrentAccount;
import com.tomato.market.account.domain.Account;
import com.tomato.market.account.domain.AccountRepository;
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
    private final AccountRepository accountRepository;

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {
        if(account != null){
            model.addAttribute(account);
            List<Product> myTownProductList = productService.getMyTownProductListToShow(account);
            model.addAttribute("myTownProductList", myTownProductList);
            List<Product> productList = productService.getProductListToShow();
            model.addAttribute("productList", productList);
        } else {
            List<Product> productList = productService.getProductListToShow();
            model.addAttribute("productList", productList);
        }
        List<Product> popularProductList = productService.getPopularProductListToShow();
        model.addAttribute("popularProductList", popularProductList);

        return "index";
    }

    @GetMapping("/more-product")
    public String moreProduct(Model model, @PageableDefault(size = 12, sort = "writeTime", direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute("productPage", productRepository.findAll(pageable));
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("writeTime") ? "writeTime" : "likeCount");

        return "show-more-product";
    }

    @GetMapping("/more-town-product")
    public String moreTownProduct(@CurrentAccount Account account, Model model, @PageableDefault(size = 12, sort = "writeTime", direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute(account);
        Account currentAccount = accountRepository.findAccountWithLocationsById(account.getId());
        model.addAttribute("productPage", productRepository.findAllByAccount(currentAccount.getLocations(), pageable));
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("writeTime") ? "writeTime" : "likeCount");

        return "show-more-town-product";
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
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("writeTime") ? "writeTime" : "likeCount");
        return "search-result";
    }

    @GetMapping("/question")
    public String question() {
        return "question";
    }

}
