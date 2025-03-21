package com.sea4.batchtest.user.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "users")  // 테이블명 명시
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;  // PK

	@Column(nullable = false, length = 50, unique = true)
	private String username;  // 사용자 아이디

	@Column(nullable = false)
	private String password;  // 비밀번호 (암호화 저장 전제)

	@Column(nullable = false, length = 100)
	private String email;  // 이메일

	@Column(nullable = false, length = 20)
	private String role;  // 역할 (ex: USER, ADMIN)

	@Column(updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();  // 생성일시

	private LocalDateTime updatedAt;  // 수정일시

	@PreUpdate
	public void setUpdatedAt() {
		this.updatedAt = LocalDateTime.now();
	}
}