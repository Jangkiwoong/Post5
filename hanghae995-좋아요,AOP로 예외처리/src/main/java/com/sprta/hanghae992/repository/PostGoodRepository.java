package com.sprta.hanghae992.repository;

import com.sprta.hanghae992.entity.Post;
import com.sprta.hanghae992.entity.PostGood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostGoodRepository extends JpaRepository<PostGood, Long> {
    PostGood findByUsername(String username);

    void deleteByUsername(String username);

    PostGood findByUsernameAndPost(String username, Post post);
}
