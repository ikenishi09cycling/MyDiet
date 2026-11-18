package com.example.app.service;

import java.util.List;

public interface AdviceService {
	
	String getLatelyAnalysis(Integer userId);
	List<String> getLatelyImprovement(Integer userId);
	
}
