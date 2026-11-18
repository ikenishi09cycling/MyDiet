package com.example.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.app.domain.bmiCategory;

@Mapper
public interface BmiMasterMapper {
	
	List<bmiCategory> getBmiCategory();
	
}
