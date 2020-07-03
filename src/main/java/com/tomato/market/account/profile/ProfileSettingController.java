package com.tomato.market.account.profile;

import com.tomato.market.account.AccountService;
import com.tomato.market.account.CurrentAccount;
import com.tomato.market.account.UserAccount;
import com.tomato.market.account.domain.Account;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@SessionAttributes("account")
@Controller
@RequiredArgsConstructor
public class ProfileSettingController {

    private final AccountService accountService;
    private final ModelMapper modelMapper;


    @GetMapping("/profile/settings")
    public String profileModifyForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, ProfileModifyForm.class));
        return "account/profile-modify";
    }

    @PostMapping("/profile/settings")
    public String profileModify(@CurrentAccount Account account, @Valid ProfileModifyForm profileModifyForm, Errors errors, Model model) throws UnsupportedEncodingException {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            System.out.println(errors);
            return "account/profile-modify";
        }
        accountService.modifyProfile(account, profileModifyForm);

        String URLencode = URLEncoder.encode(account.getNickname(), "UTF-8");  //URL에 한글을 넣어줄 때 인코딩 해줘야함
        return "redirect:/profile/"+URLencode;
    }


}
