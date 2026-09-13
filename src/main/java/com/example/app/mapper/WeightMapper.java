package com.example.app.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.app.domain.IdealWeight;
import com.example.app.domain.Weight;
import com.example.app.domain.WeightGoal;

@Mapper
public interface WeightMapper {
	
	List<Weight> getWeightLineChart(
			@Param("userId") Integer userId,
			@Param("startDt") LocalDate startDt,
			@Param("endDt") LocalDate endDt
	);
	List<IdealWeight> getIdealWeightLineChart(
			@Param("userId") Integer userId,
			@Param("startDt") LocalDate startDt,
			@Param("endDt") LocalDate endDt
	);
	List<Weight> getWeightCalendarMonth(
			@Param("userId") Integer userId,
			@Param("startDt") LocalDate startDt,
			@Param("endDt") LocalDate endDt
	);
	List<Weight> getWeightCalendarWeek(
			@Param("userId") Integer userId,
			@Param("startDt") LocalDate startDt,
			@Param("endDt") LocalDate endDt
	);
	Weight getWeightByDate(
			@Param("userId") Integer userId,
			@Param("weightDt") LocalDate weightDt
	);
	void weightSave(Weight weight);
	List<WeightGoal> getWeightGoalCalendar(
			@Param("userId") Integer userId,
			@Param("startDt") LocalDate startDt,
			@Param("endDt") LocalDate endDt
	);
	WeightGoal getWeightGoalByDate(
			@Param("userId") Integer userId,
			@Param("clickDt") LocalDate clickDt
	);
	boolean weightGoalExists(
			@Param("weightGoal") WeightGoal weightGoal,
			@Param("eventDt") LocalDate eventDt
	);
	void weightGoalInsert(WeightGoal weightGoal);
	void weightGoalUpdate(
			@Param("weightGoal") WeightGoal weightGoal,
			@Param("eventDt") LocalDate eventDt
	);
	int weightGoalDelete(
			@Param("userId") Integer userId,
			@Param("eventDt") LocalDate eventDt
	);
	
}
