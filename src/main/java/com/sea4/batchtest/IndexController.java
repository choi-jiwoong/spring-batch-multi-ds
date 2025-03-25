package com.sea4.batchtest;


import com.sea4.batchtest.user.model.LoginHistoryEntity;
import com.sea4.batchtest.user.model.UserEntity;
import com.sea4.batchtest.user.service.LoginService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class IndexController {

	private final LoginService loginService;

	private final JobLauncher jobLauncher;

	private final Job  loginHistoryResultLogJob;

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/login/{userId}")
	public String login(@PathVariable ("userId") int userId) {


		UserEntity user =
			loginService.addUser( UserEntity.createUser("username" + userId, "1234", "user" + userId + "@sample.com"));

		LoginHistoryEntity loginHistory = LoginHistoryEntity.builder()
			.user(user)
			.ipAddress("192.168.1.1")
			.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
			.loginTime(LocalDateTime.now())
			.success(true)
			.build();

		loginService.addLog(loginHistory);

		// 여기서 Batch Job 실행
		try {
			// 버퍼에 10건이 쌓였을 때만 Spring Batch Job 실행
			if (loginService.getBufferSize() >= 10) {
				jobLauncher.run(
					loginHistoryResultLogJob,
					new JobParametersBuilder()
						.addLong("time", System.currentTimeMillis()) // 매 실행마다 다른 JobInstance로 보장
						.toJobParameters()
				);
			}
		} catch (JobExecutionAlreadyRunningException e) {
			log.error("JobExecutionAlreadyRunningException", e);
		} catch (JobRestartException e) {
			log.error("JobRestartException", e);
		} catch (JobInstanceAlreadyCompleteException e) {
			log.error("JobInstanceAlreadyCompleteException", e);
		} catch (JobParametersInvalidException e) {
			log.error("JobParametersInvalidException", e);
		}

		return "login";
	}
}