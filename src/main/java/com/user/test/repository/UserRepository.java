package com.user.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user.test.model.UserModel;

public interface UserRepository  extends JpaRepository<UserModel, Integer>{
	public UserModel findById(int id);
	public UserModel findByUsername(String username);
	public UserModel findByEmail(String email);
	public UserModel findByUsernameOrEmail(String username, String email);
	
	Boolean existsByUsername(String username);
	Boolean existsByEmail(String email);
}
