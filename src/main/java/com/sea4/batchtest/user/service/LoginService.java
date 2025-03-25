package com.sea4.batchtest.user.service;

import com.sea4.batchtest.user.model.LoginHistoryEntity;
import com.sea4.batchtest.user.model.UserEntity;
import com.sea4.batchtest.user.repository.LoginHistoryRepository;
import com.sea4.batchtest.user.repository.UserEntityRepository;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

	private final UserEntityRepository userEntityRepository;
	private final LoginHistoryRepository loginHistoryRepository;

	private final List<LoginHistoryEntity> buffer = new ArrayList<>();

	/**
	 * 외부에서 로그를 추가하는 메서드.
	 * 로그가 배치 사이즈에 도달하면 자동으로 flush 함.
	 */
	public synchronized void addLog(LoginHistoryEntity logData) {
		buffer.add(logData);
	}


	// 버퍼 크기를 반환하는 메소드
	public synchronized int getBufferSize() {
		return buffer.size();
	}


	public synchronized ItemReader<LoginHistoryEntity> getReader() {
		log.info("getReader 호출 - 현재 buffer size: {}", buffer.size());
		if (buffer.isEmpty()) {
			return null;
		}
		List<LoginHistoryEntity> logsToProcess = new ArrayList<>(this.buffer);
		this.buffer.clear();
		return new ListItemReader<>(logsToProcess);
	}


	public UserEntity addUser(final UserEntity user) {
		return userEntityRepository.findFirstByUsernameOrderByCreatedAtDesc(user.getUsername())
			.orElseGet(() -> userEntityRepository.save(user));
	}

	@Transactional
	public void flushBuffer(Chunk<? extends LoginHistoryEntity> chunk) {
		log.info("flushBuffer size: {}", chunk.size());
		chunk.getItems().forEach(info -> log.info(
			"Processing LoginHistoryEntity: id={}, userId={}, loginTime={}, ipAddress={}, userAgent={}, success={}",
			info.getId(),
			info.getUser() != null ? info.getUser().getId() : "N/A",
			info.getLoginTime(),
			info.getIpAddress(),
			info.getUserAgent(),
			info.isSuccess()
		));
		loginHistoryRepository.saveAll(chunk);  // save
	}

	// 애플리케이션 종료 시 남은 버퍼를 flush 처리
	@PreDestroy
	@Transactional
	public synchronized void flushRemainingBuffer() {
		if (!buffer.isEmpty()) {
			log.info("애플리케이션 종료 전 남은 {} 건의 로그를 flush 처리합니다.", buffer.size());
			// 남은 로그들을 flush 처리 (트랜잭션 관리자에 맞는 flush 메소드를 호출)
			loginHistoryRepository.saveAll(new ArrayList<>(buffer));
			buffer.clear();
		}
	}


	// 5분마다 버퍼를 flush하는 스케줄링 메소드
	@Scheduled(fixedRate = 300000) // 300,000ms = 5분
	@Transactional
	public synchronized void scheduledFlush() {
		if (!buffer.isEmpty()) {
			log.info("스케줄에 의해 {} 건의 로그를 flush 처리합니다.", buffer.size());
			loginHistoryRepository.saveAll(new ArrayList<>(buffer));
			buffer.clear();
		} else {
			log.info("스케줄 flush 시 처리할 로그가 없습니다.");
		}
	}
}
