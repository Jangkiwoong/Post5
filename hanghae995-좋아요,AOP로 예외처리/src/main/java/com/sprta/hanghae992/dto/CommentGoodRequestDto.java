package com.sprta.hanghae992.dto;

import jakarta.persistence.Column;
import lombok.Getter;


@Getter
public class CommentGoodRequestDto {

    @Column
    private String username;
}
