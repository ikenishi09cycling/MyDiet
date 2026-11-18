package com.example.app.domain;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class bmiCategory {
	
	private Integer labelId;
	private String labelName;
	private BigDecimal bmiSt;
	private BigDecimal bmiEd;
	private String color;
	
}
