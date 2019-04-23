package isamrs.tim1.service;

import java.util.ArrayList;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.dto.UserDTO;
import isamrs.tim1.model.RegisteredUser;
import isamrs.tim1.model.User;
import isamrs.tim1.repository.RegisteredUserRepository;

@Service
public class RegisteredUserService {
	
	@Autowired
	private SimpMessagingTemplate template;
	
	@Autowired
	private RegisteredUserRepository registeredUserRepository;
	
	public ArrayList<UserDTO> searchUsers(String firstName, String lastName, String email) {
		if (firstName.equals(""))
			firstName = "%";
		else {
			try {
				firstName = firstName.toLowerCase();
				firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
			}
			catch(Exception ex) {
				firstName = firstName.substring(0, 1);
			}
		}
		if (lastName.equals(""))
			lastName = "%";
		else {
			try {
				lastName = lastName.toLowerCase();
				lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
			}
			catch(Exception ex) {
				lastName = lastName.substring(0, 1);
			}
		}
		RegisteredUser regUser = registeredUserRepository.findOneByEmail(email);
		if (regUser == null)
			return null;
		Set<RegisteredUser> users = registeredUserRepository.findByFirstAndLastName(firstName, lastName, email);
		ArrayList<UserDTO> usersDTO = new ArrayList<UserDTO>();
		for (User us : users) {
			if (!(regUser.getInvitedUsers().contains(us)) && !(regUser.getInviters().contains(us)))
				usersDTO.add(new UserDTO(us));
		}
		return usersDTO;
	}
	
	public ResponseEntity<MessageDTO> sendInvitation(String invitedUser) {
		RegisteredUser inviter = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		RegisteredUser invited = registeredUserRepository.findOneByEmail(invitedUser);
		if (invited == null)
			return new ResponseEntity<MessageDTO>(new MessageDTO("User does not exist.", ToasterType.ERROR.toString()), HttpStatus.OK);
		UserDTO u = new UserDTO(inviter.getFirstName(), inviter.getLastName(), "", "", inviter.getEmail());
		this.template.convertAndSend("/friendsInvitation/" + invitedUser, u);
		invited.getInviters().add(inviter);
		inviter.getInvitedUsers().add(invited);
		registeredUserRepository.save(inviter);
		return new ResponseEntity<MessageDTO>(new MessageDTO("Friend invitation is sent.", ToasterType.SUCCESS.toString()), HttpStatus.OK);
	}
}
