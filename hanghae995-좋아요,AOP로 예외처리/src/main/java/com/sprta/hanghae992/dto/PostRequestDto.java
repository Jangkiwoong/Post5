package com.sprta.hanghae992.dto;

import lombok.Getter;

@Getter
public class PostRequestDto {

    private String title;
    private String contents;

    public PostRequestDto(String title, String contents) {   //test에 필요한 부분
        this.title = title;
        this.contents = contents;
    }
}
