package isamrs.tim1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.UserDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.model.RegisteredUser;
import isamrs.tim1.repository.UserRepository;

@Service
public class WebSocketService {
	
	@Autowired
	private SimpMessagingTemplate template;
	
	@Autowired
	private UserRepository userRepository;

	public ResponseEntity<MessageDTO> sendInvitation(String invitedUser) {
		RegisteredUser inviter = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		RegisteredUser invited = (RegisteredUser) userRepository.findOneByEmail(invitedUser);
		if (invited == null)
			return new ResponseEntity<MessageDTO>(new MessageDTO("User does not exist.", ToasterType.ERROR.toString()), HttpStatus.OK);
		UserDTO u = new UserDTO(inviter.getFirstName(), inviter.getLastName(), "", "", inviter.getEmail());
		this.template.convertAndSend("/friendsInvitation/" + invitedUser, u);
		invited.getInviters().add(inviter);
		inviter.getInvitedUsers().add(invited);
		userRepository.save(inviter);
		return new ResponseEntity<MessageDTO>(new MessageDTO("Friend invitation is sent.", ToasterType.SUCCESS.toString()), HttpStatus.OK);
	}
}
