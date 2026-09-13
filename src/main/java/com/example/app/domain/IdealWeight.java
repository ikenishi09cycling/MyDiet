package com.example.app.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class IdealWeight {
	
	private LocalDate weightDt;
	private BigDecimal idealWeight;
	
}
