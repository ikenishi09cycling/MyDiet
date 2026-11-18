package com.example.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.domain.bmiCategory;
import com.example.app.service.BmiMasterService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bmi")
public class BmiController {

	private final BmiMasterService bmiMasterService;

	@GetMapping("/category/get")
	public List<bmiCategory> getBmiCategory() {
		
		return bmiMasterService.getBmiCategory();
		
	}

}