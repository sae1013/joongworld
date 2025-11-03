package com.softworks.joongworld.product.controller;


import com.softworks.joongworld.category.service.CategoryService;
import com.softworks.joongworld.product.dto.ProductDetailView;
import com.softworks.joongworld.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProductViewController {
    private final ProductService productService;
    private final CategoryService categoryService;

    /**
     * 상품 게시글 조회
     *
     * @param productId 상품 ID
     * @return View
    */
    @GetMapping("/product/{productId}")
    public ModelAndView detail(@PathVariable Long productId) {
        ModelAndView mav = new ModelAndView("product/detail");
        ProductDetailView product = productService.getProductDetail(productId);
        log.info("상품 상세 조회 productId={}, product={}", productId, product);
        mav.addObject("product", product);
        return mav;
    }

    @GetMapping("/product/{productId}/edit")
    public ModelAndView edit(@PathVariable Long productId) {
        ProductDetailView product = productService.getProductDetail(productId);
        ModelAndView mav = new ModelAndView("product/new");
        mav.addObject("product", product);
        mav.addObject("isEdit", true);
        mav.addObject("categories", categoryService.getAllCategories());
        return mav;
    }

    /**
     * 판매글 작성하기
     *
     * @return View
     */
    @GetMapping("/product/new")
    public ModelAndView productNew() {
        ModelAndView mav = new ModelAndView("product/new");
        mav.addObject("isEdit", false);
        mav.addObject("categories", categoryService.getAllCategories());
        return mav;
    }

//    @GetMapping("/product/form")
//    public ModelAndView form(@RequestParam(name = "type", defaultValue = "regist") String type) {
//        ModelAndView mav = new ModelAndView("product/form");
//        mav.addObject("type", type);
//        return mav;
//    }
}
