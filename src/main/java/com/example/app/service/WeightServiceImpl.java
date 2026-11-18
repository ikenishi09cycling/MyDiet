package com.example.app.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.example.app.domain.IdealWeight;
import com.example.app.domain.Weight;
import com.example.app.domain.WeightGoal;
import com.example.app.mapper.WeightMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeightServiceImpl implements WeightService {

	private final WeightMapper weightMapper;
	private final MessageSource messageSource;

	/*
	 * 最近の体重データを取得
	 */
	@Override
	public Weight getLatelyWeight(Integer userId) {
		return weightMapper.getLatelyWeight(userId);
	}
	
	/*
	 * 最近の目標体重データを取得
	 */
	@Override
	public WeightGoal getLatelyWeightGoal(Integer userId) {
		return weightMapper.getLatelyWeightGoal(userId);
	}
	
	@Override
	public Integer getWeightGoalProgress(Integer userId) {
		Weight latelyWeight = weightMapper.getLatelyWeight(userId);
		WeightGoal latelyWeightGoal = weightMapper.getLatelyWeightGoal(userId);
		IdealWeight startIdealWeight = weightMapper.getStartWeight(userId);

		if (latelyWeight == null || latelyWeightGoal == null || startIdealWeight == null) {
		    return 0;
		}

		// 最近の体重
		BigDecimal weight = latelyWeight.getWeight();
		// 最近の目標体重
		BigDecimal weightGoal = latelyWeightGoal.getWeightGoal();
		// 開始体重
		BigDecimal startWeight = startIdealWeight.getIdealWeight();

		if (weight == null || weightGoal == null || startWeight == null) {
		    return 0;
		}

		// 開始体重 - 最近の目標体重
		BigDecimal denominator = startWeight.subtract(weightGoal);

		if (denominator.compareTo(BigDecimal.ZERO) <= 0) {
		    return 100;
		}

		// 開始体重 - 最近の体重
		BigDecimal molecule = startWeight.subtract(weight);

		if (molecule.compareTo(BigDecimal.ZERO) <= 0) {
		    return 0;
		}

		BigDecimal progress = molecule
		        .divide(denominator, MathContext.DECIMAL128)
		        .multiply(BigDecimal.valueOf(100))
		        .max(BigDecimal.ZERO)
		        .min(BigDecimal.valueOf(100));

		return progress.setScale(0, RoundingMode.UP).intValue();
		}
	
	/*
	 * 体重推移用折れ線グラフの体重データを取得
	 */
	@Override
	public List<Weight> getWeightLineChart(Integer userId, LocalDate startDt, LocalDate endDt) {
		return weightMapper.getWeightLineChart(userId, startDt, endDt);
	}
	
	/*
	 * 体重推移用折れ線グラフの理想体重データを取得
	 */
	@Override
	public List<IdealWeight> getIdealWeightLineChart(Integer userId, LocalDate startDt, LocalDate endDt) {
		return weightMapper.getIdealWeightLineChart(userId, startDt, endDt);
	}
	
	@Override
	public List<Weight> getWeightCalendar(String viewType, Integer userId, LocalDate startDt, LocalDate endDt) {
		List<Weight> weights = null;
		if(viewType.equals("dayGridMonth")) {
			weights = weightMapper.getWeightCalendarMonth(userId, startDt, endDt);
		} else if(viewType.equals("dayGridWeek")) {
			weights = weightMapper.getWeightCalendarWeek(userId, startDt, endDt);
		}
		return weights;
	}
	
	@Override
	public Weight getWeightByDate(Integer userId, LocalDate weightDt) {
		return weightMapper.getWeightByDate(userId, weightDt);
	}
	
	/*
	 * 指定日の体重データ登録
	 */
	@Override
	public void saveWeightByDate(Weight weight) {
		weightMapper.weightSave(weight);
	}
	
	@Override
	public List<WeightGoal> getWeightGoalCalendar(String viewType, Integer userId, LocalDate startDt, LocalDate endDt) {
		return weightMapper.getWeightGoalCalendar(userId, startDt, endDt);
	}
	
	@Override
	public WeightGoal getWeightGoalByDate(Integer userId, LocalDate clickDt) {
		return weightMapper.getWeightGoalByDate(userId, clickDt);
	}
	
	/*
	 * 目標体重データ登録エラーチェック
	 */
	@Override
	public List<String> errorCheckWeightGoal(WeightGoal weightGoal, LocalDate eventId) {
		List<String> errors = new ArrayList<>();
		
		// 開始日・終了日の前後チェック
		if(weightGoal.getStDt().isAfter(weightGoal.getEdDt())) {
			errors.add(messageSource.getMessage("weightGoal.date.order", null, Locale.getDefault()));
		}
		
		// 目標体重データの重複チェック
		LocalDate eventDt = null;
		if(eventId != null) eventDt = eventId.minusDays(1);
		if(weightMapper.weightGoalExists(weightGoal, eventDt)) {
			errors.add(messageSource.getMessage("weightGoal.date.duplicate", null, Locale.getDefault()));
		}
		
		return errors;
	}
	
	/*
	 * 指定日の目標体重データ登録
	 */
	@Override
	public void saveWeightGoalByDate(WeightGoal weightGoal, LocalDate eventId) {
		if(eventId == null) {
			weightMapper.weightGoalInsert(weightGoal);
		} else {
			LocalDate eventDt = eventId.minusDays(1);
			weightMapper.weightGoalUpdate(weightGoal, eventDt);
		}
	}
	
	/*
	 * 指定日の目標体重データ削除
	 */
	@Override
	public int deleteWeightGoalByDate(Integer userId, LocalDate eventId) {
		LocalDate eventDt = eventId.minusDays(1);
		return weightMapper.weightGoalDelete(userId, eventDt);
	}
	
}
