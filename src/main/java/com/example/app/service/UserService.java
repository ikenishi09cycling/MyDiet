package com.example.app.service;

import com.example.app.domain.User;

public interface UserService {
	
	boolean existsUserAuth(String mailaddress, Integer userId);
	void userSave(User user);
	void userDelete(Integer userId);
	User getUser(Integer userId);
	
}
