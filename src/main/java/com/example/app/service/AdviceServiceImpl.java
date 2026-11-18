package com.example.app.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.app.domain.User;
import com.example.app.domain.Weight;
import com.example.app.domain.WeightGoal;
import com.example.app.mapper.UserMapper;
import com.example.app.mapper.WeightMapper;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class AdviceServiceImpl implements AdviceService {
	
	private final UserMapper userMapper;
	private final WeightMapper weightMapper;
	private final ObjectMapper objectMapper;
	private final RestClient restClient = RestClient.builder().baseUrl("http://localhost:11434").build();
	
	// Ollamaモデル
	private static final String MODEL_NAME = "qwen3:0.6b";
	
	@Override
	public String getLatelyAnalysis(Integer userId) {
		String dataJson = createAdviceDataJSON(userId);
		
		Map<String, Object> request = Map.of(
				"model", MODEL_NAME,
				"messages", List.of(
						Map.of(
								"role", "system",
								"content", "体重と目標体重を比較し、現在の進捗を過去の記録とユーザーの情報を参考に分析して下さい。"
										+ "weightsは日単位の実体重、weightGoalsは期間単位の目標体重。"
										+ "日本語で30文字以内の1文で返して下さい。"
										+ "アドバイスの口調で返して下さい。"
										+ "入力の内容は文に記載しないで下さい。"
						),
						Map.of(
								"role", "user",
								"content", dataJson
						)
				),
				"think", false,
				"stream", false
		);
		
		Map<?, ?> response = restClient.post()
				.uri("/api/chat")
				.body(request)
				.retrieve()
				.body(Map.class);
		
		Map<?, ?> message = (Map<?, ?>) response.get("message");
		
		return (String) message.get("content");
	}
	
	@Override
	public List<String> getLatelyImprovement(Integer userId) {
		String dataJSON = createAdviceDataJSON(userId);
		
		Map<String, Object> responseSchema = Map.of(
			"type", "object",
			"properties", Map.of(
				"recommendations", Map.of(
					"type", "array",
					"items", Map.of("type", "string"),
					"minItems", 3,
					"maxItems", 3
				)
			),
			"required", List.of("recommendations"),
			"additionalProperties", false
		);
		
		Map<String, Object> request = Map.of(
			"model", MODEL_NAME,
			"format", responseSchema,
			"messages", List.of(
				Map.of(
					"role", "system",
					"content", "体重と目標体重を比較し、現在の進捗を過去の記録とユーザーの情報を参考に3つの改善案を下さい。"
							+ "weightsは日単位の実体重、weightGoalsは期間単位の目標体重。"
							+ "各案の内容は、重複しないで下さい。"
							+ "各案の書式は、日本語で60文字以内の1文で返して下さい。"
							+ "アドバイスの口調で返して下さい。"
							+ "入力の内容は文に記載しないで下さい。"
				),
				Map.of(
					"role", "user",
					"content", dataJSON
				)
			),
			"think", false,
			"stream", false
		);
		
		Map<?, ?> response = restClient.post()
				.uri("/api/chat")
				.body(request)
				.retrieve()
				.body(Map.class);
		
		Map<?, ?> message = (Map<?, ?>) response.get("message");
		
		String content = (String) message.get("content");
		JsonNode json = objectMapper.readTree(content);
		return objectMapper.convertValue(json.get("recommendations"), new TypeReference<List<String>>() {});
	}
	
	/*
	 * AIリクエストデータ作成
	 */
	private String createAdviceDataJSON(Integer userId) {
		Map<String, Object> data = new LinkedHashMap<>();
		
		// 個人情報
		User user = userMapper.getUser(userId);
		data.put("gender", user.getGender());
		data.put("height", user.getHeight());
		
		// 今日
		LocalDate today = LocalDate.now();
		data.put("today", today.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
		
		// 体重と目標体重の期間
		LocalDate startDt = today.minusMonths(1);
		LocalDate endDt = today.plusDays(1);
		
		// 体重
		List<Weight> weights = weightMapper
				.getWeightCalendarMonth(userId, startDt, endDt)
				.stream()
				.filter(weight -> weight.getWeight() != null)
				.toList();
		data.put("weights", weights);
		
		// 目標体重
		List<WeightGoal> weightGoals = weightMapper
				.getWeightGoalCalendar(userId, startDt, endDt)
				.stream()
				.filter(weightGoal -> weightGoal.getWeightGoal() != null)
				.toList();
		data.put("weightGoals", weightGoals);
		
		return objectMapper.writeValueAsString(data);
	}
}
