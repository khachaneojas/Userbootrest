package com.user.test.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.test.enums.Authority;
import com.user.test.exception.AccountDisabledException;
import com.user.test.exception.InvalidDataException;
import com.user.test.exception.UnauthorizedAccessException;
import com.user.test.exception.UserNotFoundException;
import com.user.test.model.AuthorityModel;
import com.user.test.model.UserModel;
import com.user.test.payload.AuthRequest;
import com.user.test.payload.LoginRequest;
import com.user.test.payload.RegisterUser;
import com.user.test.payload.UpdateAuthority;
import com.user.test.payload.UpdateUser;
import com.user.test.repository.AuthorityRepository;
import com.user.test.repository.UserRepository;
import com.user.test.response.TokenValidationResponse;
import com.user.test.response.UserResponse;
import com.user.test.util.JwtUtil;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	AuthorityRepository authorityRepository;
	
	@Autowired
	JwtUtil jwtUtil;
	
//	@Autowired
//	private BCryptPasswordEncoder passwordEncoder1;

	private UserResponse getUserResponseFromUserModel(UserModel userModel) {

		return (null == userModel) ? null
				: UserResponse.builder().id(userModel.getId()).firstname(userModel.getFirstname())
						.lastname(userModel.getLastname()).email(userModel.getEmail())
						.username(userModel.getUsername())
						.authorities(
								null != userModel.getAuthorities()
								? userModel.getAuthorities()
										.stream()
										.map(authorityModel -> authorityModel.getAuthority().toString())
										.collect(Collectors.toSet())
								: null
						)
						.build();
	}
	
	public<T extends Enum<T>>T stringToEnum(Class<T> enumClass,String str){
		
		try {
			if(null==str || null == enumClass)
				return null;
			
			return Enum.valueOf(enumClass, str);
		}catch(IllegalArgumentException e) {
			return null;
		}
	}

	public List<UserResponse> getAllUsers(String str) {
		TokenValidationResponse tokenValidationResponse = isTokenValid(str);
		
		if(!tokenValidationResponse.isAdmin())
			throw new UnauthorizedAccessException("You dont have access to this endpoint");
		
		List<UserModel> listOfUserModels = userRepository.findAll();
		if (listOfUserModels.isEmpty())
			throw new RuntimeException("There are no users in the db.");

		return listOfUserModels
				.stream()
				.map(this::getUserResponseFromUserModel)
				.collect(Collectors.toList());
		
	}

	public UserResponse getUserById(String str, int id) {
		
		TokenValidationResponse tokenValidationResponse = isTokenValid(str);
		
		boolean isNone = !tokenValidationResponse.isDefault() &&
				!tokenValidationResponse.isAdmin() &&
				!tokenValidationResponse.isSales();
		if(
				isNone ||
				!tokenValidationResponse.isAdmin() &&
				id != tokenValidationResponse.getUserId()
		)
			throw new UnauthorizedAccessException("You dont have access for userid " + id);
		
		UserModel user = userRepository.findById(id);
		if (null == user)
			throw new UserNotFoundException("Invalid user Id " + id);
		
		return getUserResponseFromUserModel(user);
		
//		if (!tokenValidationResponse.isAdmin()) {
//			
//			if(id == tokenValidationResponse.getUserId()) 
//				return getUserResponseFromUserModel(user);
//			else
//				throw new UnauthorizedAccessException("You dont have access for userid " + id);
//		}
		
//		if(tokenValidationResponse.isAdmin()) 
//			return getUserResponseFromUserModel(user);
		
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
//2		AuthorityModel adminAuthority = authorityRepository.findByAuthority(Authority.Role_Admin);
//2		Set<AuthorityModel> defaultauthorities = new HashSet<>();
//2		defaultauthorities.add(defaultAuthority);
		
//		String encryptedPwd = passwordEncoder1.encode(registerUser.getPassword());
		
		UserModel userModel = UserModel.builder()
				.firstname(registerUser.getFirstname())
				.lastname(registerUser.getLastname())
				.email(registerUser.getEmail())
				.username(registerUser.getUsername())
				.password(registerUser.getPassword())
//2				.authorities(defaultauthorities)
				.authorities(Collections.singleton(defaultAuthority))
//2				.authorities(new HashSet<>(Arrays.asList(defaultAuthority, adminAuthority)))
				.build();
		
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
//			long uid = updateAuthority.getAuthorityID();
			Set<String> setOfAuthorityString = updateAuthority.getAuthorities();
			// 1. DEFAULT, 2. XYZ, 3. ADMIN
			
			Set<AuthorityModel> setOfAuthorityModel = setOfAuthorityString
					.stream()
					.map(authorityString -> {
						
						Authority authority = stringToEnum(Authority.class, authorityString);
						if (null == authority)
							throw new InvalidDataException("Invalid Data");
						
						return authorityRepository.findByAuthority(authority);
					})
					.filter(Objects::nonNull)
					.collect(Collectors.toSet());
			
//			Set<AuthorityModel> setOfAuthorityModel = setOfAuthorityString
//			.stream()
//			.map(authorityString -> {
//				
//				Authority authority = stringToEnum(Authority.class, authorityString);
//				if (null == authority)
//					throw new InvalidDataException("Invalid Data");
//				
//				AuthorityModel authorityModel = authorityRepository.findByAuthority(authority);
//				if (null == authorityModel)
//					throw new InvalidDataException("Invalid Data");
//				
//				return authorityModel;
//			})
//			.collect(Collectors.toSet());
			
//			Set<AuthorityModel> setOfAuthorityModel = new HashSet<>();
//			
//			for (String authorityString : setOfAuthorityString) {
//				
//				Authority authority = stringToEnum(Authority.class, authorityString);
//				if (null == authority)
//					throw new InvalidDataException("Invalid Data");
//				
//				AuthorityModel authorityModel = authorityRepository.findByAuthority(authority);
//				if (null == authorityModel)
//					throw new InvalidDataException("Invalid Data");
//				
//				setOfAuthorityModel.add(authorityModel);		
//			}
			
//			AuthorityModel authorityModel = authority.stream().map.(authorityRepository.findByAuthority(this);
			
//			AuthorityModel updateauthority = authorityModel.get();
			userModel.setEnabled(updateAuthority.getEnabled());
			userModel.setAuthorities(setOfAuthorityModel);
			
			userRepository.save(userModel);
			
			return getUserResponseFromUserModel(userModel);
		}
	}

	public void deleteUser(int id) {
		userRepository.deleteById(id);
	}

	public TokenValidationResponse isTokenValid(String str) {
		
		Integer userId = jwtUtil.getUserIdfromJwt(str);
		
		TokenValidationResponse tokenValidationResponse = new TokenValidationResponse();
		tokenValidationResponse.setUserId(userId);

			Optional<UserModel> userModel = userRepository.findById(userId);
				if(userModel.isPresent()) {
					
					if(userModel.get().isEnabled()) {
						
						Set<String> auth = userModel.get()
								.getAuthorities()
								.stream()
								.map(authorityModel -> authorityModel.getAuthority().toString())
								.collect(Collectors.toSet());
						

							tokenValidationResponse.setAdmin(auth.contains("Role_Admin"));
							tokenValidationResponse.setSales(auth.contains("Role_Sales"));
							tokenValidationResponse.setDefault(auth.contains("Role_Default"));
						
							return tokenValidationResponse;
					}
				}
					throw new InvalidDataException("Contact administrator, account is not active ");
				
		}
	
	public TokenValidationResponse isTokenValidTest(Authority[] authorities, String str) {

		// ID FROM TOKEN
		Integer userId = jwtUtil.getUserIdfromJwt(str);
		
		// CHECKING IF IT EXIST IN THE DB
		Optional<UserModel> userOptional = userRepository.findById(userId);
		if (userOptional.isEmpty())
			throw new UserNotFoundException("User not present with user id "+userId);
		
		// IF IT'S DISABLED
		UserModel userModel = userOptional.get();
		if (!userModel.isEnabled())
			throw new InvalidDataException("Contact administrator, account is not active ");
			
		// GETTING USER AUTHORITIES
		Set<Authority> userAuthorities = userModel.getAuthorities()
				.stream()
				.map(authorityModel -> authorityModel.getAuthority())
				.collect(Collectors.toSet());
		
		EnumSet<Authority> allowedAuthorities = EnumSet.copyOf(Arrays.asList(authorities));
		
		boolean isAllowed = userAuthorities
				.stream()
				.anyMatch(authority -> 
					allowedAuthorities.contains(authority)
				);
		
		if (!isAllowed)
			throw new UnauthorizedAccessException("You have no access .. ");
		
		return TokenValidationResponse.builder()
				.userId(userId)
				.isDefault(userAuthorities.contains(Authority.Role_Default))
				.isAdmin(userAuthorities.contains(Authority.Role_Admin))
				.isSales(userAuthorities.contains(Authority.Role_Sales))
				.isNone(userAuthorities.isEmpty())
				.build();
	}

	public String generateToken(AuthRequest authRequest) {
		
		UserModel userModel = userRepository.findByUsername(authRequest.getUsername());
		
			if(userModel != null) {
				if(userModel.getPassword().equals(authRequest.getPassword())) {
					return jwtUtil.generateToken(userModel.getId());
				}
				else {
					throw new InvalidDataException("Incorrect Password ");
				}
			}else {
				throw new InvalidDataException("User not found with username "+authRequest.getUsername());
		}

	}

	public UserResponse getUserResponseByUserId(TokenValidationResponse validationResponse, Integer userid) {
		
		if(
				validationResponse.isNone() ||
				(!validationResponse.isAdmin() && validationResponse.getUserId() != userid)
		)
			throw new UnauthorizedAccessException("You have no access");
		
		Optional<UserModel> user = userRepository.findById(userid);
		if (user.isEmpty())
			throw new UserNotFoundException("Invalid user Id " + userid);
		
		return getUserResponseFromUserModel(user.get());
		
	}
	
}
