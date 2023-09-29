package com.user.test.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.user.test.enums.Authority;
import com.user.test.model.AuthorityModel;
import com.user.test.repository.AuthorityRepository;

@Service
public class AuthorityService {
	
	@Autowired
	AuthorityRepository authorityRepository;
	
	@PostConstruct
	public void init() {
		for(Authority authority: Authority.values()) {
			Boolean isExist = authorityRepository.existsByAuthority(authority);
			
			if(Boolean.FALSE.equals(isExist)) {
				AuthorityModel authorityToBeAdded = AuthorityModel.builder()
						.authority(authority)
						.build();
				
				authorityRepository.save(authorityToBeAdded);
			}
		}
	}
}
