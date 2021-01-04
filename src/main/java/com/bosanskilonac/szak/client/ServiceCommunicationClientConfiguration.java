package com.bosanskilonac.szak.client;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import dto.TokenRequestDto;
import dto.TokenResponseDto;
import security.TokenInterceptor;
import utility.BLURL;

@Configuration
public class ServiceCommunicationClientConfiguration {
	public TokenInterceptor getServiceToken(RestTemplate restTemplate) {
		//given
		TokenRequestDto tokenRequest = new TokenRequestDto();
		tokenRequest.setUsername("microservice-access-point");
		tokenRequest.setPassword("XUhsqdFpmLkh75qwHnSzPpNz");
		HttpEntity<TokenRequestDto> requestLogin = new HttpEntity<>(tokenRequest);
		//when
		ResponseEntity<TokenResponseDto> response = restTemplate
                .exchange(BLURL.getAdminURL(), HttpMethod.POST, requestLogin, TokenResponseDto.class);
		//then
        if(response.getStatusCode().equals(HttpStatus.OK)) {
        	TokenInterceptor tokenInterceptor = new TokenInterceptor(response.getBody().getToken());
        	return tokenInterceptor;
        } else {
        	throw new HttpClientErrorException(response.getStatusCode());
        }
	}
	
	@Bean
	public RestTemplate serviceCommunicationRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setInterceptors(Collections.singletonList(getServiceToken(restTemplate)));
		return restTemplate;
	}
}
