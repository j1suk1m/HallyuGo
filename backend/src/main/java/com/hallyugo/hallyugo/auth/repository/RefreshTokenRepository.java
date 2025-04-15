package com.hallyugo.hallyugo.auth.repository;

import com.hallyugo.hallyugo.auth.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
