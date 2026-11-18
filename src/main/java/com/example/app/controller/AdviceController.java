package com.example.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.service.AdviceService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/advice")
public class AdviceController {
	
	private final AdviceService adviceService;
	private final HttpSession session;
	
	@GetMapping("analysis/get")
	public String getLatelyAnalysis() {
		Integer userId = (Integer) session.getAttribute("userId");
		return adviceService.getLatelyAnalysis(userId);
	}
	
	@GetMapping("improvement/get")
	public List<String> getLatelyImprovement() {
		Integer userId = (Integer) session.getAttribute("userId");
		return adviceService.getLatelyImprovement(userId);
	}
	
}
