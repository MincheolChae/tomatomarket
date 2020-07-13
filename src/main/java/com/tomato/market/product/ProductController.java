package com.tomato.market.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.tomato.market.account.CurrentAccount;
import com.tomato.market.account.domain.Account;
import com.tomato.market.location.Location;
import com.tomato.market.location.LocationForm;
import com.tomato.market.location.LocationRepository;
import com.tomato.market.location.LocationService;
import com.tomato.market.tag.Tag;
import com.tomato.market.tag.TagForm;
import com.tomato.market.tag.TagRepository;
import com.tomato.market.tag.TagService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ModelMapper modelMapper;
    private final ProductService productService;
    private final LocationRepository locationRepository;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final TagService tagService;

    @GetMapping("/new-product")
    public String newProductForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new ProductForm());
        return "product/new-product";
    }

    @PostMapping("/new-product")
    public String newProduct(@CurrentAccount Account account, @Valid ProductForm productForm, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "product/new-product";
        }

        Product newProduct = productService.createNewProduct(modelMapper.map(productForm, Product.class), account);
        return "redirect:/product/" + newProduct.getId();
    }

    @PostMapping("/new-product-notification/{id}")
    public String newProductNotification(@CurrentAccount Account account, @PathVariable String id) {
        Product newProduct = productService.getProduct(id);
        productService.checkIfAccountIsWriter(account, newProduct);
        productService.applyNewProductNotification(newProduct);
        return "redirect:/product/" + id;
    }

    @PostMapping(value="/uploadSummernoteImageFile", produces = "application/json")
    @ResponseBody
    public JsonObject uploadSummernoteImageFile(@RequestParam("file") MultipartFile multipartFile) {

        JsonObject jsonObject = new JsonObject();

        String fileRoot = "C:\\Users\\cmc75\\IdeaProjects\\tomatomarket\\src\\main\\resources\\static\\summernote_image\\";	//저장될 외부 파일 경로
        String originalFileName = multipartFile.getOriginalFilename();	//오리지날 파일명
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));	//파일 확장자

        String savedFileName = UUID.randomUUID() + extension;	//저장될 파일 명

        File targetFile = new File(fileRoot + savedFileName);

        try {
            InputStream fileStream = multipartFile.getInputStream();
            FileUtils.copyInputStreamToFile(fileStream, targetFile);	//파일 저장
            jsonObject.addProperty("url", "/summernote_image/"+savedFileName);  //뷰에서 불러올때의 src의 url
            jsonObject.addProperty("responseCode", "success");

        } catch (IOException e) {
            FileUtils.deleteQuietly(targetFile);	//저장된 파일 삭제
            jsonObject.addProperty("responseCode", "error");
            e.printStackTrace();
        }

        return jsonObject;
    }

    @GetMapping("/product/{id}")
    public String viewProduct(@CurrentAccount Account account, @PathVariable String id, Model model) throws JsonProcessingException {
        if(account != null) {
            model.addAttribute(account);
        }
        Product product = productService.getProduct(id);
        List<String> imageList = productService.getSeparatedImages(product);
        model.addAttribute("imageList", imageList);
        model.addAttribute(product);

        Set<Location> locations = productService.getLocations(id);
        model.addAttribute("locations", locations.stream().map(Location::toString).collect(Collectors.toList()));
        List<String> allLocations = locationRepository.findAll().stream().map(Location::toString).collect(Collectors.toList());
        model.addAttribute("locationWhitelist", objectMapper.writeValueAsString(allLocations));

        Set<Tag> tags = productService.getTags(id);
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));
        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("tagWhitelist", objectMapper.writeValueAsString(allTags));

        return "product/product-view";
    }

//    @PostMapping("/product/{id}/like")
//    @ResponseBody
//    public void productLiked(@PathVariable String id, @RequestBody ){
//
//    }


    @PostMapping("/product/{id}/tags/add")
    @ResponseBody
    public ResponseEntity addTag(@PathVariable String id, @RequestBody TagForm tagForm) {
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        productService.addTag(id, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/product/{id}/tags/remove")
    @ResponseBody
    public ResponseEntity removeTag(@PathVariable String id, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(title);
        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }

        productService.removeTag(id, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/product/{id}/locations/add")
    @ResponseBody
    public ResponseEntity addLocation(@PathVariable String id, @RequestBody LocationForm locationForm) {
        Location location = locationRepository.findByCityAndProvinceAndUnit(locationForm.getCityName(), locationForm.getProvinceName(), locationForm.getUnitName());
        if (location == null) {
            return ResponseEntity.badRequest().build();
        }
        productService.addLocation(id, location);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/product/{id}/locations/remove")
    @ResponseBody
    public ResponseEntity removeLocation(@PathVariable String id, @RequestBody LocationForm locationForm) {
        Location location = locationRepository.findByCityAndProvinceAndUnit(locationForm.getCityName(), locationForm.getProvinceName(), locationForm.getUnitName());
        if (location == null) {
            return ResponseEntity.badRequest().build();
        }
        productService.removeLocation(id, location);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/product/{id}/update")
    public String updateProductForm(@CurrentAccount Account account, @PathVariable String id, Model model) {
        Product product = productService.getProduct(id);
        productService.checkIfAccountIsWriter(account, product);

        ProductForm productForm = modelMapper.map(product, ProductForm.class);

        model.addAttribute(productForm);
        model.addAttribute(product);
        model.addAttribute(account);
        return "product/product-update";
    }

    @PostMapping("/product-update")
    public String updateProduct(@CurrentAccount Account account, @Valid ProductForm productForm, Errors errors, Model model) {
        Product product = productService.getProduct(productForm.getId());
        productService.checkIfAccountIsWriter(account, product);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "product/product-update";
        }

        Product updateProduct = productService.updateProduct(productForm, product);

        return "redirect:/product/" + updateProduct.getId();
    }

    @PostMapping("/product-soldout")
    public String productSoldOut(@CurrentAccount Account account, ProductForm productForm) {
        Product product = productService.getProduct(productForm.getId());
        productService.checkIfAccountIsWriter(account, product);

        productService.soldOut(product);

        return "redirect:/product/"+ productForm.getId();
    }


    @PostMapping("/product-delete")
    public String deleteProduct(@CurrentAccount Account account, ProductForm productForm) {
        Product product = productService.getProduct(productForm.getId());
        productService.checkIfAccountIsWriter(account, product);

        productService.deleteProduct(account, product);
        return "redirect:/";
    }





}
