package com.sea4.batchtest.user.repository;

import com.sea4.batchtest.user.model.LoginHistoryEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistoryEntity, Long> {

	// 특정 유저의 로그인 이력 조회
	List<LoginHistoryEntity> findByUserId(Long userId);

	// 최근 로그인 이력 상위 N개 조회
	List<LoginHistoryEntity> findTop10ByUserIdOrderByLoginTimeDesc(Long userId);
}