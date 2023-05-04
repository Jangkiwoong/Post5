package com.sprta.hanghae992.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Getter
@Entity
@NoArgsConstructor
public class CommentGood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column
    private String username;

    @ManyToOne //(fetch = FetchType.EAGER)
    @JoinColumn(name="comment_id", nullable = false)
    private Comment comment;


    public CommentGood(String username,Comment comment) { //좋아요

        this.username = username;
        this.comment = comment;
    }


}
