package isamrs.tim1.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import isamrs.tim1.dto.FriendDTO;
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
	
	@RequestMapping(value = "/api/getFriends", method = RequestMethod.GET)
	public ResponseEntity<ArrayList<FriendDTO>> getFriends(){
		return registeredUserService.getFriends();
	}
	
	@RequestMapping(value = "/sendInvitation", method = RequestMethod.POST)
	public ResponseEntity<MessageDTO> sendInvitation(@RequestBody String invitedUser){
		return registeredUserService.sendInvitation(invitedUser);
	}
	
	@RequestMapping(value = "/api/acceptInvitation", method = RequestMethod.POST)
	public ResponseEntity<MessageDTO> acceptInvitation(@RequestBody String acceptedUser){
		return registeredUserService.acceptInvitation(acceptedUser);
	}
	
	@RequestMapping(value = "/api/declineInvitation", method = RequestMethod.POST)
	public ResponseEntity<MessageDTO> declineInvitation(@RequestBody String declinedUser){
		return registeredUserService.declineInvitation(declinedUser);
	}
	
}
