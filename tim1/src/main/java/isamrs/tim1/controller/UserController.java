package isamrs.tim1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import isamrs.tim1.model.User;
import isamrs.tim1.service.UserService;

@RestController
public class UserController {

	@Autowired
	private UserService userService;
	
	@RequestMapping(
			value = "/api/users",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> editUserProfile(
			@RequestBody User user) throws Exception {
		User editedUser = userService.editProfile(user);
		if (editedUser == null) {
			return new ResponseEntity<User>(
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<User>(editedUser, HttpStatus.OK);
	}
}
