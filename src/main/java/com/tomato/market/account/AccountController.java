package com.tomato.market.account;

import com.tomato.market.account.domain.Account;
import com.tomato.market.account.domain.AccountRepository;
import com.tomato.market.account.profile.NotificationForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        if(errors.hasErrors()){   //중복이메일과 비밀번호 일치하지 않을때 돌려보냄
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
        model.addAttribute("name", account.getName());
        model.addAttribute("nickname", account.getNickname());
        return "account/checked-email";
    }

    @GetMapping("/resend-confirm-email")
    public String resendConfirmEmail(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute("email", account.getEmail());
        if (!account.canSendConfirmEmail()) {
            model.addAttribute("error", "인증 이메일은 30분에 한번만 전송할 수 있습니다.");
            return "account/resend-email";
        }

        accountService.sendSignUpConfirmEmail(account);
        return "account/resend-email";
    }

    @GetMapping("/find-id")
    public String findId(Model model){
        model.addAttribute(new FindIdForm());
        return "account/find-id";
    }

    @PostMapping(value = "/find-id", produces = "application/json; charset=utf-8")
    public @ResponseBody String findIdResult(@RequestBody FindIdForm findIdForm){
        Account foundAccount = accountRepository.findByNameAndPhone(findIdForm.getName(), findIdForm.getPhone());
        if(foundAccount == null) {
            return "이름 또는 휴대폰 번호가 잘못되었거나 일치하는 아이디가 없습니다.";
        }
        if(foundAccount.getName().equals(findIdForm.getName()) && foundAccount.getPhone().equals(findIdForm.getPhone())){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("닉네임 : ");
            stringBuilder.append(foundAccount.getNickname());
            stringBuilder.append(" / ");
            stringBuilder.append("이메일 : ");
            stringBuilder.append(foundAccount.getEmail());
            return stringBuilder.toString();
        } else {
            return "이름 또는 휴대폰 번호가 잘못되었거나 일치하는 아이디가 없습니다.";
        }
    }

    @GetMapping("/email-login")
    public String emailLoginForm() {
        return "account/email-login";
    }

    @PostMapping("/email-login")
    public String sendEmailLoginLink(String email, Model model, RedirectAttributes attributes) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            model.addAttribute("error", "유효한 이메일 주소가 아닙니다.");
            return "account/email-login";
        }

        if (!account.canSendConfirmEmail()) {
            model.addAttribute("error", "이메일 로그인은 30분에 한번만 사용할 수 있습니다.");
            return "account/email-login";
        }

        accountService.sendLoginLink(account);
        attributes.addFlashAttribute("message", "이메일 인증 메일을 발송했습니다.");
        return "redirect:/email-login";
    }

    @GetMapping("/login-by-email")
    public String loginByEmail(String token, String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        if (account == null || !account.isValidToken(token)) {
            model.addAttribute("error", "로그인할 수 없습니다.");
            return "account/logged-in-by-email";
        }

        accountService.login(account);
        return "account/logged-in-by-email";
    }


}