package com.tomato.market.account;

import com.tomato.market.account.domain.Account;
import com.tomato.market.account.domain.AccountRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;

//    @Test
//    public void signUpForm_with_input() throws Exception {
//        mockMvc.perform(post("/signup")
//                .param("email", "cmc752@gmail.com")
//                .param("password", "12341234")
//                .param("password2", "12341234")
//                .param("nickname", "mincheol")
//                .param("phone", "01050504040")
//                .with(csrf()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/"))
//                .andExpect(authenticated().withUsername("cmc752@gmail.com"));
//
//        Account account = accountRepository.findByEmail("cmc752@gmail.com");
//        assertNotNull(account);
//        assertNotEquals(account.getPassword(), "12341234");
//        assertNotNull(account.getEmailCheckToken());
//    }
//
//    @Test
//    public void login() throws Exception {
//        SignUpForm signUpForm = new SignUpForm();
//        signUpForm.setEmail("cmc752@gmail.com");
//        signUpForm.setPassword("12341234");
//        signUpForm.setPassword2("12341234");
//        signUpForm.setNickname("채민철");
//        signUpForm.setPhone("01012341234");
//        accountService.processNewAccount(signUpForm);
//
//        mockMvc.perform(post("/login")
//                .param("username", "cmc752@gmail.com")
//                .param("password", "12341234")
//                .with(csrf()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/"))
//                .andExpect(authenticated().withUsername("채민철"));  //username 체크 다시
//    }
}