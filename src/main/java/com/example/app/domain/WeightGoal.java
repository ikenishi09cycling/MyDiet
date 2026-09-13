package com.example.app.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WeightGoal {
	
	private Integer userId;
	
	@NotNull
	private LocalDate stDt;
	
	@NotNull
	private LocalDate edDt;
	
	@NotNull
    @DecimalMin(value = "0", inclusive = true)
    @DecimalMax(value = "1000", inclusive = false)
    @Digits(integer = 3, fraction = 1)
	private BigDecimal weightGoal;
	
	private LocalDateTime createDt;
	
    private LocalDateTime updateDt;
	
}
