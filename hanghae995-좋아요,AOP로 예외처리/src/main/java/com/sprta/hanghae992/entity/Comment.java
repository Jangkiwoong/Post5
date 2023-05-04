package com.sprta.hanghae992.entity;


import com.sprta.hanghae992.dto.CmtRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Comment extends Timestamped{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;


    @Column
    private String username;


//    //좋아요 기능
//    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL) //조회할때도 관련 테이블 한번에 가져오고 삭제 할때도 한번에 삭제
//    @JoinColumn(name = "comment_id")                                 //EAGER 즉시로딩
//    private List<CommentGood> commentGoodList = new ArrayList<>();

    //좋아요 갯수
    @Column
    private Long good = 0L;

    public Comment(CmtRequestDto cmtRequestDto, String username) { //댓글 작성

        this.username = username;
        this.content = cmtRequestDto.getContent();
    }

    public void update(CmtRequestDto cmtRequestDto,Long temp) {
        this.content = cmtRequestDto.getContent();
        this.good = temp;

    }


    public void setGood(Long good) {
        this.good = good;

    }
}
