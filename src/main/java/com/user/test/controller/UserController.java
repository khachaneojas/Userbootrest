package com.user.test.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.user.test.enums.Authority;
import com.user.test.exception.InvalidDataException;
import com.user.test.exception.UnauthorizedAccessException;
import com.user.test.model.UserModel;
import com.user.test.payload.AuthRequest;
import com.user.test.payload.LoginRequest;
import com.user.test.payload.RegisterUser;
import com.user.test.payload.UpdateAuthority;
import com.user.test.payload.UpdateUser;
import com.user.test.repository.UserRepository;
import com.user.test.response.TokenValidationResponse;
import com.user.test.response.UserResponse;
import com.user.test.service.UserService;
import com.user.test.util.JwtUtil;

@RestController
public class UserController {
	
	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/users")
//	@Secured({"Role_Admin"})
//	@PreAuthorize("hasAuthority('Role_Admin')")
	public ResponseEntity<?> getallUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String str){

		TokenValidationResponse tokenValidationResponse = userService.isTokenValid(str);
		
		if(!tokenValidationResponse.isAdmin())
			throw new UnauthorizedAccessException("You dont have access to this endpoint");
			
		return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);

	}

	@GetMapping("/userbyid/{id}")
	public ResponseEntity<?> getuserbyid(@RequestHeader(HttpHeaders.AUTHORIZATION) String str,@PathVariable("id") int id){

		TokenValidationResponse tokenValidationResponse = userService.isTokenValid(str);
		
		if(!tokenValidationResponse.isAdmin() || !tokenValidationResponse.isDefault())
			throw new UnauthorizedAccessException("You dont have access to this endpoint");
		
//		return (null == userService.getUserById(id))
//				? new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND)
//				: new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);		
		return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);	
	}

	@GetMapping("/userbyname/{username}")
	public ResponseEntity<?> getuserbyusername(@PathVariable("username") String uname) {
		UserResponse userResponse = userService.getUserByUsername(uname);
		if (userResponse == null) {
			return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(userResponse, HttpStatus.OK);
	}

	@GetMapping("/userbyemail/{email}")
	public ResponseEntity<?> getuserbyemail(@PathVariable("email") String email) {
		UserResponse userResponse = userService.getUserByEmail(email);
		if (userResponse == null) {
			return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(userResponse, HttpStatus.OK);
	}

	@PostMapping("/register")
	public ResponseEntity<?> adduser(@Valid @RequestBody RegisterUser registerUser) {
		boolean isAdded = userService.addUser(registerUser);

		return new ResponseEntity<>((isAdded ? "User has been successfully registered." : "Failed to register."),
				(isAdded ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST));

	}

	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		boolean isAuthorizedUser = userService.login(loginRequest);

		return new ResponseEntity<>(isAuthorizedUser, (isAuthorizedUser ? HttpStatus.OK : HttpStatus.UNAUTHORIZED));
	}

	@DeleteMapping("/deleteuserbyid/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable("id") int userId) {
		try {
			userService.deleteUser(userId);
			return new ResponseEntity<>("User Deleted", HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("User not deleted", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/updateuser")
	public ResponseEntity<?> updateuser(@Valid @RequestBody UpdateUser user) {
			return new ResponseEntity<>(userService.updateUser(user), HttpStatus.ACCEPTED);
	}
	
	@PutMapping("/updateauth")
	public ResponseEntity<?> updateAuthority(@Valid @RequestBody UpdateAuthority updateAuthority){
		
		UserResponse userResponse = userService.updateAuthority(updateAuthority);
		
		if (null==userResponse)
				return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
		else 
			return new ResponseEntity<>(userResponse, HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/authenticate")
	public ResponseEntity<?> generateToken(@RequestBody AuthRequest authRequest) throws Exception{
		return new ResponseEntity<>(userService.generateToken(authRequest), HttpStatus.CREATED);
	}	
}
