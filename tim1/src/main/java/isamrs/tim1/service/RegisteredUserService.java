package isamrs.tim1.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import isamrs.tim1.dto.FriendDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.dto.UserDTO;
import isamrs.tim1.model.RegisteredUser;
import isamrs.tim1.model.User;
import isamrs.tim1.repository.RegisteredUserRepository;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class RegisteredUserService {

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	private RegisteredUserRepository registeredUserRepository;

	public ArrayList<UserDTO> searchUsers(String firstName, String lastName) {
		if (firstName.equals(""))
			firstName = "%";
		else {
			try {
				firstName = firstName.toLowerCase();
				firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
			} catch (Exception ex) {
				firstName = firstName.substring(0, 1);
			}
		}
		if (lastName.equals(""))
			lastName = "%";
		else {
			try {
				lastName = lastName.toLowerCase();
				lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
			} catch (Exception ex) {
				lastName = lastName.substring(0, 1);
			}
		}
		RegisteredUser regUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (regUser == null)
			return null;
		Set<RegisteredUser> users = registeredUserRepository.findByFirstAndLastName(firstName, lastName, regUser.getEmail());
		ArrayList<UserDTO> usersDTO = new ArrayList<UserDTO>();
		for (User us : users) {
			if (!(regUser.getInvitedUsers().contains(us)) && !(regUser.getInviters().contains(us))
					&& !(regUser.getFriends().contains(us)))
				usersDTO.add(new UserDTO(us));
		}
		return usersDTO;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public ResponseEntity<MessageDTO> sendInvitation(String invitedUser) {
		RegisteredUser inviter = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		RegisteredUser invited = registeredUserRepository.findOneByEmail(invitedUser);
		if (invited == null)
			return new ResponseEntity<MessageDTO>(new MessageDTO("User does not exist.", ToasterType.ERROR.toString()),
					HttpStatus.OK);
		UserDTO u = new UserDTO(inviter.getFirstName(), inviter.getLastName(), "", "", inviter.getEmail());
		this.template.convertAndSend("/friendsInvitation/" + invitedUser, u);
		invited.getInviters().add(inviter);
		inviter.getInvitedUsers().add(invited);
		registeredUserRepository.save(inviter);
		return new ResponseEntity<MessageDTO>(
				new MessageDTO("Friend invitation is sent.", ToasterType.SUCCESS.toString()), HttpStatus.OK);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public ResponseEntity<MessageDTO> acceptInvitation(String acceptedUser) {
		RegisteredUser currUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		RegisteredUser accepted = registeredUserRepository.findOneByEmail(acceptedUser);
		if (accepted == null)
			return new ResponseEntity<MessageDTO>(new MessageDTO("User does not exist.", ToasterType.ERROR.toString()),
					HttpStatus.OK);
		this.template.convertAndSend("/friendsInvitation/" + acceptedUser, "Accepted-" + currUser.getEmail());
		Iterator<RegisteredUser> it = currUser.getInviters().iterator();
		while (it.hasNext()) {
			RegisteredUser ru = it.next();
			if (ru.getEmail().equals(accepted.getEmail())) {
				it.remove();
				break;
			}
		}
		currUser.getFriends().add(accepted);
		Iterator<RegisteredUser> iter = accepted.getInvitedUsers().iterator();
		while (iter.hasNext()) {
			RegisteredUser ru = iter.next();
			if (ru.getEmail().equals(currUser.getEmail())) {
				iter.remove();
				break;
			}
		}
		accepted.getFriends().add(currUser);
		registeredUserRepository.save(accepted);
		registeredUserRepository.save(currUser);
		return new ResponseEntity<MessageDTO>(
				new MessageDTO("Friend invitation accepted.", ToasterType.SUCCESS.toString()), HttpStatus.OK);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public ResponseEntity<MessageDTO> declineInvitation(String declinedUser) {
		RegisteredUser currUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		RegisteredUser declined = registeredUserRepository.findOneByEmail(declinedUser);
		if (declined == null)
			return new ResponseEntity<MessageDTO>(new MessageDTO("User does not exist.", ToasterType.ERROR.toString()),
					HttpStatus.OK);
		this.template.convertAndSend("/friendsInvitation/" + declinedUser, "Declined-" + currUser.getEmail());
		Iterator<RegisteredUser> it = currUser.getInviters().iterator();
		while (it.hasNext()) {
			RegisteredUser ru = it.next();
			if (ru.getEmail().equals(declined.getEmail())) {
				it.remove();
				break;
			}
		}
		Iterator<RegisteredUser> iter = declined.getInvitedUsers().iterator();
		while (iter.hasNext()) {
			RegisteredUser ru = iter.next();
			if (ru.getEmail().equals(currUser.getEmail())) {
				iter.remove();
				break;
			}
		}
		registeredUserRepository.save(declined);
		registeredUserRepository.save(currUser);
		return new ResponseEntity<MessageDTO>(
				new MessageDTO("Friend invitation declined.", ToasterType.SUCCESS.toString()), HttpStatus.OK);
	}

	public ResponseEntity<ArrayList<FriendDTO>> getFriendInvitations() {
		ArrayList<FriendDTO> friends = new ArrayList<FriendDTO>();
		RegisteredUser currUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		for (User us : currUser.getFriends()) {
			friends.add(new FriendDTO(us, "Accepted"));
		}
		for (User us : currUser.getInvitedUsers()) {
			friends.add(new FriendDTO(us, "Invitation sent"));
		}
		for (User us : currUser.getInviters()) {
			friends.add(new FriendDTO(us, "Invitation pending"));
		}
		return new ResponseEntity<ArrayList<FriendDTO>>(friends, HttpStatus.OK);
	}

	public ResponseEntity<ArrayList<FriendDTO>> getFriends() {
		ArrayList<FriendDTO> friends = new ArrayList<FriendDTO>();
		RegisteredUser currUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		for (User us : currUser.getFriends()) {
			friends.add(new FriendDTO(us, "Accepted"));
		}
		return new ResponseEntity<ArrayList<FriendDTO>>(friends, HttpStatus.OK);
	}
}
