package com.tomato.market.account;

import com.tomato.market.account.domain.Account;
import com.tomato.market.account.domain.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model){
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    private String signUp(@Valid SignUpForm signUpForm, Errors errors){
        if(errors.hasErrors()){  //중복이메일과 비밀번호 일치하지 않을때 돌려보냄
            return "account/sign-up";
        }

        Account account = accountService.processNewAccount(signUpForm);  //가입처리
        accountService.login(account);  //로그인처리
        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            model.addAttribute("error", "이메일 주소가 정확하지 않습니다.");
            return "account/checked-email";
        }
        if (!account.isValidToken(token)) {
            model.addAttribute("error", "이메일 인증에 실패했습니다.");
            return "account/checked-email";
        }

        accountService.completeSignUp(account);
        model.addAttribute("nickname", account.getNickname());
        return "account/checked-email";
    }

    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable String nickname, Model model, @CurrentAccount Account account) {
        Account accountToView = accountService.getAccount(nickname);
        model.addAttribute(accountToView);
        model.addAttribute("isOwner", accountToView.equals(account));
        return "account/profile";
    }


}