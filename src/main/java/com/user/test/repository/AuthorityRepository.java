package com.user.test.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user.test.enums.Authority;
import com.user.test.model.AuthorityModel;

public interface AuthorityRepository extends JpaRepository<AuthorityModel, Long>{
	
	 Boolean existsByAuthority(Authority authority);
	 AuthorityModel findByAuthority(Authority authority);
	
}
