package com.sprta.hanghae992.repository;

import com.sprta.hanghae992.entity.ApiUseTime;
import com.sprta.hanghae992.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiUseTimeRepository extends JpaRepository<ApiUseTime, Long> {
    Optional<ApiUseTime> findByUser(User user);
}