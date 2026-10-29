package com.example.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.app.domain.UserAuth;
import com.example.app.service.AuthService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {
	
	
	private final AuthService authService;
	private final HttpSession session;
	
	@GetMapping
	public String displayLogin(Model model) {
		model.addAttribute("userAuth", new UserAuth());
		return "login";
	}
	
	@PostMapping
	public String authLogin(@Valid UserAuth userAuth, Errors errors) {
		// 認証処理
		Integer userId = authService.authLogin(userAuth);
		// ログインエラー
		if(userId == null) {
			errors.reject("login.error");
			return "login";
		}
		// セッションにユーザーidを格納する
		session.setAttribute("userId", userId);
		return "redirect:/user/home";
	}
	
}
