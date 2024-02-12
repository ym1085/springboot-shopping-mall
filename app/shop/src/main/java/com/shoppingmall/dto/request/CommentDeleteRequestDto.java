package com.shoppingmall.dto.request;

import com.shoppingmall.vo.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDeleteRequestDto {
    private Long postId;
    private Long commentId;

    public Comment toEntity() {
        return Comment.builder()
                .postId(postId)
                .commentId(commentId)
                .build();
    }
}

