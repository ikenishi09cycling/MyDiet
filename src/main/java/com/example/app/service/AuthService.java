package com.example.app.service;

import com.example.app.domain.UserAuth;

public interface AuthService {
	
	Integer authLogin(UserAuth userAuth);
	
}
