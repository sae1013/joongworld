package com.softworks.joongworld.product.controller;

import com.softworks.joongworld.product.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/product/{productId}")
    public ModelAndView detail(@PathVariable Long productId) {
        ModelAndView mav = new ModelAndView("product/detail");
        mav.addObject("product", productService.getProductDetail(productId));
        return mav;
    }

    @GetMapping("/posts/{productId}")
    public String redirectFromLegacyPost(@PathVariable Long productId) {
        return "redirect:/product/" + productId;
    }
}
