package isamrs.tim1.service;

import java.util.ArrayList;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.UserDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.model.RegisteredUser;
import isamrs.tim1.model.User;
import isamrs.tim1.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	public User findUserByToken(String token){
		return userRepository.findByToken(token);
	}

	public MessageDTO editProfile(User user) throws Exception {
		User userToEdit = userRepository.findOneByEmail(user.getEmail());
		if (userToEdit == null) {
			return new MessageDTO("User with given email address does not exist.", ToasterType.ERROR.toString());
		}

		String firstName = user.getFirstName();
		if (firstName != null) {
			userToEdit.setFirstName(firstName);
		}

		String lastName = user.getLastName();
		if (lastName != null) {
			userToEdit.setLastName(lastName);
		}

		String address = user.getAddress();
		if (address != null) {
			userToEdit.setAddress(address);
		}

		String phoneNumber = user.getPhoneNumber();
		if (phoneNumber != null) {
			userToEdit.setPhoneNumber(user.getPhoneNumber());
		}

		try {
			userRepository.save(userToEdit);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return new MessageDTO("Database error.", ToasterType.ERROR.toString());
		}

		return new MessageDTO("Profile changes saved successfully", ToasterType.SUCCESS.toString());
	}

}
