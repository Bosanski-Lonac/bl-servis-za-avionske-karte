package com.bosanskilonac.szak.runner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import security.TokenService;

@Profile({"default"})
@Component
public class Runner implements CommandLineRunner {
	@Value("${oauth.jwt.secret}")
	private String jwtSecret;
	
	private TokenService tokenService;
	
	public Runner(TokenService tokenService) {
		this.tokenService = tokenService;
	}
	
	@Override
	public void run(String... args) throws Exception {
		tokenService.setSecret(jwtSecret);
	}

}
