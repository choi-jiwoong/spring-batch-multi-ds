package com.sea4.batchtest.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_history")
@Getter
@Setter
@NoArgsConstructor
public class LoginHistoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;  // PK

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)  // FK 연결
	private UserEntity user;

	@Column(nullable = false)
	private LocalDateTime loginTime = LocalDateTime.now();  // 로그인 시각

	@Column(length = 50)
	private String ipAddress;  // 접속 IP

	@Column(length = 255)
	private String userAgent;  // 브라우저 정보 등

	@Column(nullable = false)
	private boolean success;  // 로그인 성공 여부 (true / false)
}