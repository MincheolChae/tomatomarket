package com.tomato.market.product;

import com.google.gson.JsonObject;
import com.tomato.market.account.CurrentAccount;
import com.tomato.market.account.domain.Account;
import com.tomato.market.account.domain.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ModelMapper modelMapper;
    private final ProductService productService;
    private final ProductRepository productRepository;

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
            jsonObject.addProperty("url", "/summernote_image/"+savedFileName);  //뷰에서 불러올때의 src url
            jsonObject.addProperty("responseCode", "success");

        } catch (IOException e) {
            FileUtils.deleteQuietly(targetFile);	//저장된 파일 삭제
            jsonObject.addProperty("responseCode", "error");
            e.printStackTrace();
        }

        return jsonObject;
    }

    @GetMapping("/product/{id}")
    public String viewProduct(@PathVariable String id, Model model) {
        Product product = productService.getProduct(id);

//        model.addAttribute(account);
        model.addAttribute(product);
        return "product/product-view";
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
    public String deleteProduct(@CurrentAccount Account account, ProductForm productForm) {     //TODO : Soft-Delete로 바꿔보기
        Product product = productService.getProduct(productForm.getId());
        productService.checkIfAccountIsWriter(account, product);

        productService.deleteProduct(product);
        return "index";    //TODO : 돌려보낼 위치 나중에 바꾸기
    }
}
