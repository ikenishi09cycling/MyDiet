package com.example.app.service;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.example.app.domain.User;
import com.example.app.domain.UserAuth;
import com.example.app.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final UserMapper userMapper;
	
	/*
	 * メールアドレス重複チェック
	 */
	@Override
	public boolean existsUserAuth(String mailaddress, Integer userId) {
		UserAuth userAuth = userMapper.getUserAuth(mailaddress);
		return userAuth != null && !userAuth.getUserId().equals(userId);
	}
	
	/*
	 * ユーザーデータ登録
	 */
	@Override
	public void userSave(User user) {
		// ユーザー基本データ登録
		userMapper.userSave(user);
		
		// ユーザー基本データ登録で採番したユーザーidを設定する
		Integer userId = user.getUserId();
		user.getUserAuth().setUserId(userId);
		// パスワードをハッシュ化する
		String passwordHash = BCrypt.hashpw(user.getUserAuth().getPassword(), BCrypt.gensalt());
		user.getUserAuth().setPasswordHash(passwordHash);
		
		// ユーザー認証データ登録
		userMapper.userAuthSave(user.getUserAuth());
	}
	
	/*
	 * ユーザーデータ削除
	 */
	@Override
	public void userDelete(Integer userId) {
		userMapper.userDelete(userId);
	}
	
	@Override
	public User getUser(Integer userId) {
		return userMapper.getUser(userId);
	}

}
