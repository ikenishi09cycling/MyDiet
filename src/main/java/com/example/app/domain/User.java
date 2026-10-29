package com.example.app.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class User {
	
	private Integer userId;
	
	@Valid
	private UserAuth userAuth;
	
	@NotBlank
	@Size(max = 50)
	private String firstName;
	
	@NotBlank
	@Size(max = 50)
	private String lastName;
	
	@NotNull
	private Byte gender;
	
	@NotNull
    @DecimalMin(value = "0", inclusive = true)
    @DecimalMax(value = "1000", inclusive = false)
    @Digits(integer = 3, fraction = 1)
	private BigDecimal height;
	
	private LocalDateTime createDt;
	
	private LocalDateTime updateDt;
	
}
