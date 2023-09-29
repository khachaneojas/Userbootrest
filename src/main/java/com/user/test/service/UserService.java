package com.user.test.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.user.test.enums.Authority;
import com.user.test.exception.AccountDisabledException;
import com.user.test.exception.InvalidDataException;
import com.user.test.model.AuthorityModel;
import com.user.test.model.UserModel;
import com.user.test.payload.LoginRequest;
import com.user.test.payload.RegisterUser;
import com.user.test.payload.UpdateAuthority;
import com.user.test.payload.UpdateUser;
import com.user.test.repository.AuthorityRepository;
import com.user.test.repository.UserRepository;
import com.user.test.response.UserResponse;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	AuthorityRepository authorityRepository;

	private UserResponse getUserResponseFromUserModel(UserModel userModel) {

		return (null == userModel) ? null
				: UserResponse.builder().id(userModel.getId()).firstname(userModel.getFirstname())
						.lastname(userModel.getLastname()).email(userModel.getEmail())
						.username(userModel.getUsername()).authority(userModel.getAuthorityModel().getAuthority())
						.build();
	}

	public List<UserResponse> getAllUsers() {
		List<UserModel> listOfUserModels = userRepository.findAll();
		if (listOfUserModels.isEmpty())
			throw new RuntimeException("There are no users in the db.");

		return listOfUserModels.stream().map(this::getUserResponseFromUserModel).collect(Collectors.toList());
	}

	public UserResponse getUserById(int id) {
		UserModel user = null;
		try {
			user = this.userRepository.findById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getUserResponseFromUserModel(user);
	}

	public UserResponse getUserByUsername(String username) {
		UserModel user = null;
		try {
			user = this.userRepository.findByUsername(username);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getUserResponseFromUserModel(user);
	}

	public UserResponse getUserByEmail(String email) {
		UserModel user = null;
		try {
			user = this.userRepository.findByEmail(email);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getUserResponseFromUserModel(user);
	}

	public Boolean addUser(RegisterUser registerUser) {

		Boolean isEmailAlreadyExist = userRepository.existsByEmail(registerUser.getEmail());
		Boolean isUsernameAlreadyExist = userRepository.existsByUsername(registerUser.getUsername());

		if (Boolean.TRUE.equals(isEmailAlreadyExist) || Boolean.TRUE.equals(isUsernameAlreadyExist))
			return false;
		
		AuthorityModel defaultAuthority = authorityRepository.findByAuthority(Authority.Role_Default);

		UserModel userModel = UserModel.builder().firstname(registerUser.getFirstname())
				.lastname(registerUser.getLastname()).email(registerUser.getEmail())
				.username(registerUser.getUsername()).password(registerUser.getPassword()).authorityModel(defaultAuthority).build();

		try {
			userRepository.save(userModel);
			return true;
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return false;
	}

	public Boolean login(LoginRequest loginRequest) {
			UserModel userModel = userRepository.findByUsernameOrEmail(loginRequest.getUsername_or_email(),
					loginRequest.getUsername_or_email());

			if (null == userModel)
				return false;
			
			String userPasswordDB = userModel.getPassword();
			
			if(userPasswordDB.isBlank())
				throw new InvalidDataException("Something went wrong, contact the administrator.");
			
			if (userModel.getPassword().equals(loginRequest.getPassword())) {
				if(!userModel.isEnabled())
					throw new AccountDisabledException("Oops! you're account has been disabled by the administrator.");
				else
					return true;
			}
			return false;
			
	}

	public String updateUser(UpdateUser user) {
		
		int userid = user.getId();
		
		Optional<UserModel> userOptional = Optional.of(userRepository.findById(userid));
		
		if(userOptional.isEmpty()) 
			return "User with id " + userid + " not found";
			
			
			UserModel userModel = userOptional.get();
			
			userModel.setFirstname(user.getFirstname());
			userModel.setLastname(user.getLastname());
			userModel.setEmail(user.getEmail());
			userModel.setUsername(user.getUsername());
			userModel.setPassword(user.getPassword());
			
			userRepository.save(userModel);
			return "User details for ID "+ userid + " updated successfully";
			
	}
	
	public UserResponse updateAuthority(UpdateAuthority updateAuthority) {
		UserModel userModel = null;
		userModel = userRepository.findByUsernameOrEmail(updateAuthority.getUsername_or_email(),
				updateAuthority.getUsername_or_email());
		
		if(null == userModel)
			return null;
		else {
			long uid = updateAuthority.getAuthorityID();
			Optional<AuthorityModel> authorityModel = authorityRepository.findById(uid);
			
			AuthorityModel updateauthority = authorityModel.get();
			userModel.setEnabled(updateAuthority.getEnabled());
			userModel.setAuthorityModel(updateauthority);
			
			userRepository.save(userModel);
			
			return getUserResponseFromUserModel(userModel);
		}
			
		
	}

	public void deleteUser(int id) {
		userRepository.deleteById(id);
	}
}
