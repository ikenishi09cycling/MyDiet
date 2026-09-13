package com.example.app.service;

import java.time.LocalDate;
import java.util.List;

import com.example.app.domain.IdealWeight;
import com.example.app.domain.Weight;
import com.example.app.domain.WeightGoal;

public interface WeightService {
	
	List<Weight> getWeightLineChart(Integer userId, LocalDate startDt, LocalDate endDt);
	List<IdealWeight> getIdealWeightLineChart(Integer userId, LocalDate startDt, LocalDate endDt);
	List<Weight> getWeightCalendar(String viewType, Integer userId, LocalDate startDt, LocalDate endDt);
	Weight getWeightByDate(Integer userId, LocalDate weightDt);
	void saveWeightByDate(Weight weight);
	List<WeightGoal> getWeightGoalCalendar(String viewType, Integer userId, LocalDate startDt, LocalDate endDt);
	WeightGoal getWeightGoalByDate(Integer userId, LocalDate clickDt);
	List<String> errorCheckWeightGoal(WeightGoal weightGoal, LocalDate eventId);
	void saveWeightGoalByDate(WeightGoal weightGoal, LocalDate eventId);
	int deleteWeightGoalByDate(Integer userId, LocalDate eventId);
	
}
