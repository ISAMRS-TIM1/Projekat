package isamrs.tim1.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.UserDTO;
import isamrs.tim1.service.RegisteredUserService;

@RestController
public class RegisteredUserController {
	
	@Autowired
	private RegisteredUserService registeredUserService;
	
	@RequestMapping(value = "/api/searchUsers", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ArrayList<UserDTO> searchUsers(@RequestParam String firstName, @RequestParam String lastName, 
										@RequestParam String email) {
		return registeredUserService.searchUsers(firstName, lastName, email);
	}
	
	@RequestMapping(value = "/sendInvitation", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> sendInvitation(@RequestParam String invitedUser){
		return registeredUserService.sendInvitation(invitedUser);
	}
	
}
