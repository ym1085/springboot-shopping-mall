package com.shoppingmall.api;

import com.shoppingmall.common.dto.BaseResponse;
import com.shoppingmall.common.utils.ApiResponseUtils;
import com.shoppingmall.config.auth.PrincipalUserDetails;
import com.shoppingmall.dto.request.ProductSaveRequestDto;
import com.shoppingmall.dto.request.ProductUpdateRequestDto;
import com.shoppingmall.dto.request.SearchRequestDto;
import com.shoppingmall.exception.InvalidParameterException;
import com.shoppingmall.service.ProductService;
import com.shoppingmall.vo.Member;
import com.shoppingmall.vo.Product;
import com.shoppingmall.vo.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.shoppingmall.common.code.success.CommonSuccessCode.SUCCESS;
import static com.shoppingmall.common.code.success.product.ProductSuccessCode.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class ProductRestController {

    private final ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<BaseResponse<?>> getProducts(SearchRequestDto searchRequestDto) {
        ProductResponse products = productService.getProducts(searchRequestDto);
        return ApiResponseUtils.success(SUCCESS, products);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<BaseResponse<?>> getProductByProductId(@PathVariable("productId") Integer productId) {
        Product product = productService.getProductByProductId(productId);
        return ApiResponseUtils.success(SUCCESS, product);
    }

    @PostMapping("/products")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse<?>> saveProducts(
            @Valid @ModelAttribute ProductSaveRequestDto productRequestDto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new InvalidParameterException(bindingResult);
        }
        productService.saveProducts(productRequestDto);
        return ApiResponseUtils.success(SUCCESS_SAVE_PRODUCT);
    }

    @PutMapping("/products/{productId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse<?>> updateProduct(
            @PathVariable("productId") Integer productId,
            @Valid @ModelAttribute ProductUpdateRequestDto productUpdateRequestDto,
            BindingResult bindingResult,
            Authentication authentication) {

        if (bindingResult.hasErrors()) {
            throw new InvalidParameterException(bindingResult);
        }

        PrincipalUserDetails principalUserDetails = (PrincipalUserDetails) authentication.getPrincipal();
        Member member = principalUserDetails.getLoginMember();
        log.debug("member = {}", member);

        productUpdateRequestDto.setMemberId(member.getMemberId());
        productUpdateRequestDto.setProductId(productId);
        productService.updateProduct(productUpdateRequestDto);

        return ApiResponseUtils.success(SUCCESS_UPDATE_PRODUCT);
    }

    @DeleteMapping("/products/{productId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse<?>> deleteProduct(@PathVariable("productId") Integer productId) {
        productService.deleteProduct(productId);
        return ApiResponseUtils.success(SUCCESS_DELETE_PRODUCT);
    }
}
