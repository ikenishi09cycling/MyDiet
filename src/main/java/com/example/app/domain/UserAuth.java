package com.example.app.domain;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserAuth {
	
	private Integer userId;
	
	@NotBlank
	@Size(max = 255)
	private String mailaddress;
	
	@NotBlank
	@Size(max = 255)
	private String password;
	
	@NotBlank
	@Size(max = 255)
	private String passwordConfirm;
	
	private String passwordHash;
	
	private LocalDateTime createDt;
	
	private LocalDateTime updateDt;
	
}
