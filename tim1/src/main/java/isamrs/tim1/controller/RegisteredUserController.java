package isamrs.tim1.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

	@PreAuthorize("hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/searchUsers", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ArrayList<UserDTO> searchUsers(@RequestParam String firstName, @RequestParam String lastName,
			@RequestParam String email) {
		return registeredUserService.searchUsers(firstName, lastName, email);
	}

	@PreAuthorize("hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/getFriendInvitations", method = RequestMethod.GET)
	public ResponseEntity<ArrayList<FriendDTO>> getFriendInvitations() {
		return registeredUserService.getFriendInvitations();
	}

	@PreAuthorize("hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/getFriends", method = RequestMethod.GET)
	public ResponseEntity<ArrayList<FriendDTO>> getFriends() {
		return registeredUserService.getFriends();
	}

	@PreAuthorize("hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/sendInvitation", method = RequestMethod.POST)
	public ResponseEntity<MessageDTO> sendInvitation(@RequestBody String invitedUser) {
		return registeredUserService.sendInvitation(invitedUser);
	}

	@PreAuthorize("hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/acceptInvitation", method = RequestMethod.POST)
	public ResponseEntity<MessageDTO> acceptInvitation(@RequestBody String acceptedUser) {
		return registeredUserService.acceptInvitation(acceptedUser);
	}

	@PreAuthorize("hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/declineInvitation", method = RequestMethod.POST)
	public ResponseEntity<MessageDTO> declineInvitation(@RequestBody String declinedUser) {
		return registeredUserService.declineInvitation(declinedUser);
	}
}
