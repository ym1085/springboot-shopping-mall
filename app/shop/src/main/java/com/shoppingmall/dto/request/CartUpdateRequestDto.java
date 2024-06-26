package com.shoppingmall.dto.request;

import com.shoppingmall.vo.Cart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartUpdateRequestDto {

    @NotNull(message = "상품 아이디는 필수 입력 값 입니다.")
    private Integer productId;

    @Min(value = 1, message = "최소 1개 이상 담아주세요")
    private Integer amount;
    private Integer memberId;
    private Integer cartId;
    private String uuid;

    public Cart toEntity() {
        return Cart.builder()
                .cartId(cartId)
                .productId(productId)
                .amount(amount)
                .memberId(memberId)
                .uuid(uuid)
                .build();
    }
}