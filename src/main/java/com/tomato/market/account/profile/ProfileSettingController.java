package com.tomato.market.account.profile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomato.market.account.AccountService;
import com.tomato.market.account.CurrentAccount;
import com.tomato.market.account.domain.Account;
import com.tomato.market.location.Location;
import com.tomato.market.location.LocationForm;
import com.tomato.market.location.LocationRepository;
import com.tomato.market.product.Product;
import com.tomato.market.tag.Tag;
import com.tomato.market.tag.TagForm;
import com.tomato.market.tag.TagRepository;
import com.tomato.market.tag.TagService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SessionAttributes("account")
@Controller
@RequiredArgsConstructor
public class ProfileSettingController {

    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final PasswordFormValidator passwordFormValidator;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final TagService tagService;
    private final LocationRepository locationRepository;

    @InitBinder("passwordForm")
    public void passwordInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(passwordFormValidator);
    }

    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable String nickname, Model model, @CurrentAccount Account account) {
        Account accountToView = accountService.getAccount(nickname);
        model.addAttribute("account", account);
        model.addAttribute("accountToView", accountToView);
        model.addAttribute("isOwner", accountToView.equals(account));

        //알림
        model.addAttribute(new NotificationForm(account));

        return "account/profile";
    }

    @PostMapping("/notification")
    public String updateNotification(@CurrentAccount Account account, @Valid NotificationForm notificationForm, Errors errors, Model model, RedirectAttributes attributes) throws UnsupportedEncodingException {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "/account/profile";
        }

        accountService.updateNotifications(account, notificationForm);
        attributes.addFlashAttribute("message", "알림 설정을 변경했습니다.");
        return "redirect:/profile/" + URLEncoder.encode(account.getNickname(), "UTF-8");
    }

    @GetMapping("/profile/tags")
    public String updateTags(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);
        Set<Tag> tags = accountService.getTags(account);
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));
        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));
        return "account/tags";
    }

    @PostMapping("/profile/tags/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentAccount Account account, @RequestBody TagForm tagForm) {
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        accountService.addTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/profile/tags/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentAccount Account account, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(title);
        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.removeTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile/locations")
    public String updateLocationsForm(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);
        Set<Location> locations = accountService.getLocations(account);
        model.addAttribute("locations", locations.stream().map(Location::toString).collect(Collectors.toList()));
        List<String> allLocations = locationRepository.findAll().stream().map(Location::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allLocations));
        return "account/locations";
    }

    @PostMapping("/profile/locations/add")
    @ResponseBody
    public ResponseEntity addLocation(@CurrentAccount Account account, @RequestBody LocationForm locationForm) {
        Location location = locationRepository.findByCityAndProvinceAndUnit(locationForm.getCityName(), locationForm.getProvinceName(), locationForm.getUnitName());
        if (location == null) {
            return ResponseEntity.badRequest().build();
        }
        accountService.addLocation(account, location);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/profile/locations/remove")
    @ResponseBody
    public ResponseEntity removeLocation(@CurrentAccount Account account, @RequestBody LocationForm locationForm) {
        Location location = locationRepository.findByCityAndProvinceAndUnit(locationForm.getCityName(), locationForm.getProvinceName(), locationForm.getUnitName());
        if (location == null) {
            return ResponseEntity.badRequest().build();
        }
        accountService.removeLocation(account, location);
        return ResponseEntity.ok().build();
    }

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
            return "account/profile-modify";
        }
        if(!accountService.isValidNickName(account, profileModifyForm)) {
            model.addAttribute("error", "이미 사용중인 닉네임입니다.");
            return "account/profile-modify";
        }

        accountService.modifyProfile(account, profileModifyForm);
        return "redirect:/profile/" + URLEncoder.encode(profileModifyForm.getNickname(), "UTF-8");    //URL에 한글을 넣어줄 때 인코딩 해줘야함
    }

    @GetMapping("/profile/password")
    public String passwordModifyForm(Model model) {
        model.addAttribute(new PasswordForm());
        return "account/password-modify";
    }

    @PostMapping("/profile/password")
    public String passwordModify(@CurrentAccount Account account, @Valid PasswordForm passwordForm, Errors errors, Model model) throws UnsupportedEncodingException {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "account/password-modify";
        }
        accountService.modifyPassword(account, passwordForm);
        String URLencode = URLEncoder.encode(account.getNickname(), "UTF-8");
        return "redirect:/profile/" + URLencode;
    }

    @GetMapping("/profile/products")
    public String getUploadedProducts(@CurrentAccount Account account, Model model){
        List<Product> productList = accountService.getUploadedProducts(account);

        model.addAttribute(productList);
        model.addAttribute(account);
        return "account/my-products";
    }

    @GetMapping("/profile/products_liked")
    public String getUploadedProductsLiked(@CurrentAccount Account account, Model model){
        List<Product> productList = accountService.getUploadedProductsLiked(account);

        model.addAttribute(productList);
        model.addAttribute(account);
        return "account/products-liked";
    }

//    @DeleteMapping("/profile/delete")
//    public String deleteAccount(@CurrentAccount Account account){
//        accountService.deleteAccount(account);
//
//        return "redirect:/";
//    }

}
