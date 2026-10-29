package com.example.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.app.domain.User;
import com.example.app.domain.UserAuth;
import com.example.app.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
	
	private final UserService userService;
	private final HttpSession session;
	
	@GetMapping("home")
	public String displayHome(Model model) {
		// セッションからユーザーIDを取得する
		Integer userId = (Integer) session.getAttribute("userId");
		// ユーザー情報を取得する
		User user = userService.getUser(userId);
		String name = user.getLastName() + " " + user.getFirstName();
		model.addAttribute("userName", name);
		return "home";
	}
	
	@GetMapping("add")
	public String displayUserAdd(Model model) {
		User user = new User();
		user.setUserAuth(new UserAuth());
		model.addAttribute("mode", "add");
		model.addAttribute("user", user);
		return "/user/userSave";
	}
	
	@GetMapping("edit")
	public String displayUserEdit(Model model) {
		// セッションからユーザーIDを取得する
		Integer userId = (Integer) session.getAttribute("userId");
		// ユーザー情報を取得する
		User user = userService.getUser(userId);
		model.addAttribute("mode", "edit");
		model.addAttribute("user", user);
		return "/user/userSave";
	}
	
	/*
	 * ユーザーデータ登録
	 */
	@PostMapping({"add", "edit"})
	public String userSave(
			String mode,
			@Valid User user,
			Errors errors,
			Model model) {
		// セッションからユーザーIDを取得する
		Integer userId = (Integer) session.getAttribute("userId");
		user.setUserId(userId);
		// パスワード一致チェック
		if(!user.getUserAuth().getPassword().equals(user.getUserAuth().getPasswordConfirm())) {
			errors.rejectValue("userAuth.password", "userAuth.password.mismatch");
		}
		// メールアドレス重複チェック
		if(userService.existsUserAuth(user.getUserAuth().getMailaddress(), userId)) {
			errors.rejectValue("userAuth.mailaddress", "userAuth.mailaddress.duplicate");
		}
		// バリデーションチェック
		if(errors.hasErrors()) {
			model.addAttribute("mode", mode);
			return "/user/userSave";
		}
		// 登録処理
		userService.userSave(user);
		return "redirect:/user/" + mode + "/complete";
	}
	
	/*
	 * ユーザーデータ削除
	 */
	@PostMapping("delete")
	public String userDelete() {
		// セッションからユーザーIDを取得する
		Integer userId = (Integer) session.getAttribute("userId");
		// 削除処理
		userService.userDelete(userId);
		// セッションを削除する
		session.invalidate();
		return "redirect:/user/delete/complete";
	}
	
	@GetMapping("{mode}/complete")
	public String displayUserComplete(@PathVariable String mode, Model model) {
		model.addAttribute("mode", mode);
		return "/user/userComplete";
	}
	
	@GetMapping("mypage")
	public String displayUserMyPage() {
		return "/user/userMyPage";
	}
	
	/*
	 * ログアウト
	 */
	@GetMapping("logout")
	public String logout() {
		// セッションを削除する
		session.invalidate();
		return "redirect:/";
	}
	
}
