package com.user.test.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.user.test.model.UserModel;

public class CustomUserDetails implements UserDetails {

	private String username;
	private String password;
	private boolean enabled;
	private Set<GrantedAuthority> authorities;

	public CustomUserDetails(UserModel userModel) {
		this.username = userModel.getUsername();
		this.password = userModel.getPassword();
		this.enabled = userModel.isEnabled();
		this.authorities = userModel.getAuthorities()
				.stream()
				.map(authorityModel -> new SimpleGrantedAuthority(authorityModel.getAuthority().toString()))
				.collect(Collectors.toSet());
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return authorities;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return enabled;
	}

}
