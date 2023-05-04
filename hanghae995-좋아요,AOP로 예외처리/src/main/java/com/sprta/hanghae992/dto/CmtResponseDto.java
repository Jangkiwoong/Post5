package com.sprta.hanghae992.dto;

import com.sprta.hanghae992.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CmtResponseDto {

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String content;
    private String username;
    private Long id;
    private Long good;

    public CmtResponseDto(Comment comment, Long temp) {
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
        this.content = comment.getContent();
        this.username = comment.getUsername();
        this.id = comment.getId();
        this.good = temp;
    }


}
