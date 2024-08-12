package com.shoppingmall.dto.request;

import com.shoppingmall.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProductUpdateRequestDto {
    private Integer memberId;
    private Integer productId;
    private Integer categoryId;
    private String productName;
    private Integer productPrice;
    private Integer productStock;
    private String productDesc;
    private ItemSellStatus itemSellStatus = ItemSellStatus.SELL;

    private List<MultipartFile> files = new ArrayList<>();
}