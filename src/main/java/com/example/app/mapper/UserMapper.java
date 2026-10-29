package com.example.app.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.app.domain.User;
import com.example.app.domain.UserAuth;

@Mapper
public interface UserMapper {
	
	UserAuth getUserAuth(String mailaddress);
	void userSave(User user);
	void userAuthSave(UserAuth userAuth);
	void userDelete(Integer userId);
	User getUser(Integer userId);
	
}
