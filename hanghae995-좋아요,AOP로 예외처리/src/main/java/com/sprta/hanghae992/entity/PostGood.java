package com.sprta.hanghae992.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor
public class PostGood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column
    private String username;

    @ManyToOne //(fetch = FetchType.EAGER)
    @JoinColumn(name="post_id", nullable = false)
    private Post post;

    public PostGood(String username,Post post) { //좋아요

        this.username = username;
        this.post = post;
    }


}
