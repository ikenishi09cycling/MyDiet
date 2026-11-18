package com.example.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.app.domain.bmiCategory;
import com.example.app.mapper.BmiMasterMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BmiMasterServiceImpl implements BmiMasterService {
	
	private final BmiMasterMapper bmiMasterMapper;
	
	@Override
	public List<bmiCategory> getBmiCategory() {
		return bmiMasterMapper.getBmiCategory();
	}
	
}
