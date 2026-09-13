package com.example.app.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.app.domain.IdealWeight;
import com.example.app.domain.Weight;
import com.example.app.domain.WeightGoal;
import com.example.app.service.WeightService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/weight")
public class WeightController {
	
	private final WeightService weightService;
	private final HttpSession session;
	private final MessageSource messageSource;
	
	@GetMapping("weightHome")
	public String displayWeightHome() {
		return "/weight/weightHome";
	}
	
	/*
	 * 体重推移用折れ線グラフの体重データを取得
	 */
	@GetMapping("weightLineChart/get")
	public ResponseEntity<List<Weight>> getWeightLineChart(
			@RequestParam LocalDate startDt,
			@RequestParam LocalDate endDt) {
		// セッションからユーザーIDを取得する
		Integer userId = (Integer) session.getAttribute("userId");
		List<Weight> weights = weightService.getWeightLineChart(userId, startDt, endDt);
		return ResponseEntity.ok(weights);
	}
	
	/*
	 * 体重推移用折れ線グラフの理想体重データを取得
	 */
	@GetMapping("idealWeightLineChart/get")
	public ResponseEntity<List<IdealWeight>> getIdealWeightLineChart(
			@RequestParam LocalDate startDt,
			@RequestParam LocalDate endDt) {
		// セッションからユーザーIDを取得する
		Integer userId = (Integer) session.getAttribute("userId");
		List<IdealWeight> idealWeights = weightService.getIdealWeightLineChart(userId, startDt, endDt);
		return ResponseEntity.ok(idealWeights);
	}
	
	@GetMapping("weightCalendar")
	public String displayWeightCalendar(Model model) {
		model.addAttribute("weight", new Weight());
		return "/weight/weightCalendar";
	}
	
	/*
	 * 体重入力用カレンダーの体重データを取得
	 */
	@GetMapping("weightCalendar/get")
	public ResponseEntity<List<Weight>> getWeightCalendar(
			@RequestParam String viewType,
			@RequestParam LocalDate startDt,
			@RequestParam LocalDate endDt) {
		// セッションからユーザーIDを取得する
		Integer userId = (Integer) session.getAttribute("userId");
		List<Weight> weights = weightService.getWeightCalendar(viewType, userId, startDt, endDt);
		return ResponseEntity.ok(weights);
	}
	
	/*
	 * 指定日の体重データを表示
	 */
	@GetMapping("weightModal/get")
	public ResponseEntity<Weight> getWeightByDate(@RequestParam LocalDate weightDt) {
		// セッションからユーザーIDを取得する
		Integer userId = (Integer) session.getAttribute("userId");
		
		// 指定日の体重データを取得する
		Weight weight = weightService.getWeightByDate(userId, weightDt);
		
		// 体重データが未入力の場合は、空のJSON形式のデータを返す
		if (weight == null) {
		    return ResponseEntity.ok(new Weight());
		}
		return ResponseEntity.ok(weight);
	}
	
	/*
	 * 指定日の体重データを登録
	 */
	@PostMapping("weightModal/save")
	public ResponseEntity<?> saveWeightByDate(@Valid Weight weight, BindingResult result) {
		// バリデーションチェック
		if(result.hasErrors()) {
			// バリデーションエラーを取得し、フィールド単位でエラーメッセージをリストにする
			Map<String, List<String>> errors = result.getFieldErrors().stream()
					.collect(Collectors.groupingBy(
							error -> error.getField(),
							Collectors.mapping(
									error -> messageSource.getMessage(error, Locale.getDefault()),
									Collectors.toList()
							)
					));
			// HTTPステータスコード（400）でエラーメッセージを返す
			return ResponseEntity.badRequest().body(errors);
		}
		
		// セッションからユーザーIDを取得する
		Integer userId = (Integer) session.getAttribute("userId");
		weight.setUserId(userId);
		
		// 登録処理
		weightService.saveWeightByDate(weight);
		
		// 登録日の体重データを取得し、HTTPステータスコード（200）で返す
		Weight saveWeight = weightService.getWeightByDate(userId, weight.getWeightDt());
		return ResponseEntity.ok(saveWeight);
	}
	
	@GetMapping("weightGoalCalendar")
	public String displayWeightGoalCalendar(Model model) {
		model.addAttribute("weightGoal", new WeightGoal());
		return "/weight/weightGoalCalendar";
	}
	
	/*
	 * 目標体重入力用カレンダーの目標体重データを取得
	 */
	@GetMapping("weightGoalCalendar/get")
	public ResponseEntity<List<WeightGoal>> getWeightGoalCalendar(
			@RequestParam String viewType,
			@RequestParam LocalDate startDt,
			@RequestParam LocalDate endDt) {
		// セッションからユーザーIDを取得する
		Integer userId = (Integer) session.getAttribute("userId");
		List<WeightGoal> weightGoals = weightService.getWeightGoalCalendar(viewType, userId, startDt, endDt);
		return ResponseEntity.ok(weightGoals);
	}
	
	/*
	 * 指定日の目標体重データを表示
	 */
	@GetMapping("weightGoalModal/get")
	public ResponseEntity<WeightGoal> getWeightGoalByDate(@RequestParam LocalDate clickDt) {
		// セッションからユーザーIDを取得する
		Integer userId = (Integer) session.getAttribute("userId");
		
		// 指定日の目標体重データを取得する
		WeightGoal weightGoal = weightService.getWeightGoalByDate(userId, clickDt);
		
		// 目標体重データが未入力の場合は、空のJSON形式のデータを返す
		if (weightGoal == null) {
		    return ResponseEntity.ok(new WeightGoal());
		}
		return ResponseEntity.ok(weightGoal);
	}
	
	/*
	 * 指定日の目標体重データを登録
	 */
	@PostMapping("weightGoalModal/save")
	public ResponseEntity<?> saveWeightGoalByDate(
			@Valid WeightGoal weightGoal,
			@RequestParam(required = false) LocalDate eventId,
			BindingResult result) {
		// レスポンス用エラーメッセージ
		Map<String, List<String>> errors = new HashMap<>();
		
		// バリデーションチェック
		if(result.hasErrors()) {
			// バリデーションエラーを取得し、フィールド単位でエラーメッセージをリストにする
			errors = result.getFieldErrors().stream()
					.collect(Collectors.groupingBy(
							error -> error.getField(),
							Collectors.mapping(
									error -> messageSource.getMessage(error, Locale.getDefault()),
									Collectors.toList()
							)
					));
			// HTTPステータスコード（400）でエラーメッセージを返す
			return ResponseEntity.badRequest().body(errors);
		}
		
		// セッションからユーザーIDを取得する
		Integer userId = (Integer) session.getAttribute("userId");
		weightGoal.setUserId(userId);
		
		// エラーチェック
		List<String> listErrors = weightService.errorCheckWeightGoal(weightGoal, eventId);
		if(!listErrors.isEmpty()) {
			errors.put("global", listErrors);
			return ResponseEntity.badRequest().body(errors);
		}
		
		// 登録処理
		weightService.saveWeightGoalByDate(weightGoal, eventId);
		
		// 登録日の目標体重データを取得し、HTTPステータスコード（200）で返す
		WeightGoal saveWeightGoal = weightService.getWeightGoalByDate(userId, weightGoal.getEdDt());
		saveWeightGoal.setEdDt(saveWeightGoal.getEdDt().plusDays(1));
		return ResponseEntity.ok(saveWeightGoal);
	}
	
	/*
	 * 指定日の目標体重データを削除
	 */
	@GetMapping("weightGoalModal/delete")
	public ResponseEntity<?> deleteWeightGoalByDate(@RequestParam LocalDate eventId) {
		// セッションからユーザーIDを取得する
		Integer userId = (Integer) session.getAttribute("userId");
		
		// 削除処理
		int deleteCount = weightService.deleteWeightGoalByDate(userId, eventId);
		
		// 削除件数が0の場合は、HTTPステータスコード（400）で返す
		if(deleteCount == 0) {
			return ResponseEntity.badRequest().build();
		}
		
		// HTTPステータスコード（200）で返す
		return ResponseEntity.ok().build();
	}
	
}
