package com.user.test.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.user.test.annotation.Auditor;
import com.user.test.enums.Authority;
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
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private UserRepository userRepository; 

	Logger log = LoggerFactory.getLogger(UserController.class);

	@GetMapping("/allusers")
//	@Secured({"Role_Admin"})
//	@PreAuthorize("hasAuthority('Role_Admin')")
	public ResponseEntity<?> getallUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String str){
		log.info("ABC");
		return new ResponseEntity<>(userService.getAllUsers(str), HttpStatus.OK);
	}

	@GetMapping("/userbyid/{id}")
	public ResponseEntity<?> getuserbyid(@RequestHeader(HttpHeaders.AUTHORIZATION) String str,@PathVariable("id") int id){

			return new ResponseEntity<>(userService.getUserById(str, id), HttpStatus.OK);		
	}
	
	@GetMapping("/usertest/{userId}")
	@Auditor(validate = true, allowedRoles = Authority.Role_Admin)
	public ResponseEntity<?> getUserIdTest(@PathVariable("userId") Integer userid, TokenValidationResponse validationResponse){
		System.out.println("Inside Executed");
		return new ResponseEntity<>(userService.getUserResponseByUserId(validationResponse, userid), HttpStatus.OK);
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
