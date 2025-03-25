package com.sea4.batchtest;

import com.sea4.batchtest.user.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class BatchTestApplication implements DisposableBean {

	private final LoginService loginService;

	public static void main(String[] args) {
		SpringApplication.run(BatchTestApplication.class, args);
	}

	@Override
	public void destroy() throws Exception {
		// 애플리케이션 종료 시 남은 로그 데이터를 flush 처리
		loginService.flushRemainingBuffer();
	}
}
