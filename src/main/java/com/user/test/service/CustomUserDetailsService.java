package com.user.test.service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.user.test.model.UserModel;
import com.user.test.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		CustomUserDetails customUserDetails = new CustomUserDetails(userRepository.findByUsername(username));
		
//		Hibernate.initialize(userModel.getAuthorities());
		
//		Set<GrantedAuthority> authorities = userModel.getAuthorities()
//				.stream()
//				.map(authorityModel -> new SimpleGrantedAuthority(authorityModel.getAuthority().toString()))
//				.collect(Collectors.toSet());
		
//		return new User(userModel.getUsername(),userModel.getPassword(), authorities);
		
		return new User(customUserDetails.getUsername(), customUserDetails.getPassword(), customUserDetails.getAuthorities());
		
		//return userModel.map(CustomUserDetails :: new).orElseThrow(()-> new UsernameNotFoundException(username+" doesn't exist in system"));
	}

}
