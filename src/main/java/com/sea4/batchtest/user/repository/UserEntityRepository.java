package com.sea4.batchtest.user.repository;

import com.sea4.batchtest.user.model.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {


	Optional<UserEntity> findFirstByUsernameOrderByCreatedAtDesc(String username);
}
