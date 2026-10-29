package com.example.app.service;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.example.app.domain.UserAuth;
import com.example.app.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	
	private final UserMapper userMapper;
	
	@Override
	public Integer authLogin(UserAuth userAuth) {
		// 該当のメールアドレスと一致のデータを抽出する
		UserAuth dbUserAuth = userMapper.getUserAuth(userAuth.getMailaddress());
		// メールアドレスチェック
		if(dbUserAuth == null) return null;
		// パスワードチェック
		if(!BCrypt.checkpw(userAuth.getPassword(), dbUserAuth.getPasswordHash())) return null;
		// 認証が成功の場合はユーザーidを返す
		return dbUserAuth.getUserId();
	}

}
