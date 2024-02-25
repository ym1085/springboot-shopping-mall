package com.shoppingmall.dto.request;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ProductFileSaveRequestDto extends FileSaveRequestDto {
    private Long productFileId;
    private Long productId;

    /**
     * 파일 저장의 경우 상품 등록이 완료 된 이후에 진행 한다.
     * 해당 함수는 생성된 게시글 ID를 파일 요청 객체의 productId에 저장하는 용도로 사용
     * -> FileServiceImpl 클래스 참고
     */
    @Override
    public void setId(Long id) {
        this.productId = id;
    }

    @Override
    public Long getFileId() {
        return this.productFileId;
    }

    @Override
    public Long getId() {
        return this.productId;
    }
}
