package com.softworks.joongworld.product.controller;

import com.softworks.joongworld.product.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 상품 게시글 조회
     * @param productId 상품 ID
     * @return View
     */
    @GetMapping("/product/{productId}")
    public ModelAndView detail(@PathVariable Long productId) {
        ModelAndView mav = new ModelAndView("product/detail");
        mav.addObject("product", productService.getProductDetail(productId));
        return mav;
    }

    /**
     * 판매글 작성하기
     * @return View
     */
    @GetMapping("/product/new")
    public ModelAndView productNew(){
        ModelAndView mav = new ModelAndView("product/new");
        return mav;
    }
//    @GetMapping("/product/form")
//    public ModelAndView form(@RequestParam(name = "type", defaultValue = "regist") String type) {
//        ModelAndView mav = new ModelAndView("product/form");
//        mav.addObject("type", type);
//        return mav;
//    }
}
