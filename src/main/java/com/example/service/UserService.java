package com.example.service;

import com.example.dto.AddUrlRequest;
import com.example.dto.RegisterResponseHolder;
import com.example.dto.StatisticResponseHolder;
import com.example.model.Redirection;
import com.example.model.User;

public interface UserService {
	User findUserByEmail(String email);

	void saveUser(User user);

	StatisticResponseHolder createStatisticForUserEmail(String email);

	Redirection getRedirectionForShortUrl(String shortUrl);

    RegisterResponseHolder registerUrl(String userName, AddUrlRequest registerRequest);

}
