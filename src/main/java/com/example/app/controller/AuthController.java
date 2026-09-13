package com.example.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {
	
	private final HttpSession session;
	
	@GetMapping
	public String displayLogin() {
		return "login";
	}
	
	@PostMapping
	public String authLogin() {
		session.setAttribute("userId", 1);
		return "redirect:/user";
	}
	
}
