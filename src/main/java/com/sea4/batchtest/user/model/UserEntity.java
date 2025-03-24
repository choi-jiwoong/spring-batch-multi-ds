package com.sea4.batchtest.user.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "users")  // 테이블명 명시
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

	@Builder
	public static  UserEntity createUser(String username, String password, String email) {
		return new UserEntity(username, password, email);  // 생성자 호출
	}

	public UserEntity(String username, String password, String email) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.role = "USER";
	}


}