package com.sea4.batchtest.user.service;

import com.sea4.batchtest.user.model.LoginHistoryEntity;
import com.sea4.batchtest.user.repository.LoginHistoryRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

	private final LoginHistoryRepository loginHistoryRepository;

	private final List<LoginHistoryEntity> buffer = new ArrayList<>();

	/**
	 * 외부에서 로그를 추가하는 메서드.
	 * 로그가 배치 사이즈에 도달하면 자동으로 flush 함.
	 */
	public synchronized void addLog(LoginHistoryEntity logData) {
		buffer.add(logData);
	}


	public synchronized ItemReader<LoginHistoryEntity> getReader() {

		List<LoginHistoryEntity> logsToProcess = new ArrayList<>(this.buffer);
		// 버포 로그를 초기화
		this.buffer.clear();

		return new ListItemReader<>(logsToProcess);
	}


	public void flushBuffer(Chunk<? extends LoginHistoryEntity> chunk) {

		for (LoginHistoryEntity logData : chunk) {
			loginHistoryRepository.save(logData);
		}

	}
}
