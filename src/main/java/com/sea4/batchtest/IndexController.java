package com.sea4.batchtest;


import com.sea4.batchtest.user.model.LoginHistoryEntity;
import com.sea4.batchtest.user.model.UserEntity;
import com.sea4.batchtest.user.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class IndexController {

	private final LoginService loginService;

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/login")
	public String login() {

		for (int i = 0; i < 100; i++) {
			UserEntity user = UserEntity.createUser("username" + i, "1234", "user" + i + "@sample.com");
			LoginHistoryEntity loginHistory = LoginHistoryEntity.builder()
				.user(user)
				.ipAddress("192.168.1.1")
				.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
				.success(true)
				.build();

			loginService.addLog(loginHistory);
		}

		return "login";
	}
}