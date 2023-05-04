package com.sprta.hanghae992.repository;

import com.sprta.hanghae992.entity.Comment;
import com.sprta.hanghae992.entity.CommentGood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentGoodRepository extends JpaRepository<CommentGood, Long> {

    void deleteByComment(Comment comment);

    CommentGood findByUsernameAndComment(String username, Comment comment);
}
