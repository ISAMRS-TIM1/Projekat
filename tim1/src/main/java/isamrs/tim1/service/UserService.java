package isamrs.tim1.service;

import java.util.ArrayList;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isamrs.tim1.dto.UserDTO;
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

	public String editProfile(User user) throws Exception {
		User userToEdit = userRepository.findOneByEmail(user.getEmail());
		if (userToEdit == null) {
			return "User with given email address does not exist.";
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
			return "Database error.";
		}

		return null;
	}

	public ArrayList<UserDTO> getUsers(String firstName, String lastName, String email) {
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
		RegisteredUser regUser = (RegisteredUser) userRepository.findOneByEmail(email);
		if (regUser == null)
			return null;
		Set<User> users = userRepository.findByFirstAndLastName(firstName, lastName, "RegisteredUser", email);
		ArrayList<UserDTO> usersDTO = new ArrayList<UserDTO>();
		for (User us : users) {
			if (!(regUser.getInvitedUsers().contains(us)) && !(regUser.getInviters().contains(us)))
				usersDTO.add(new UserDTO(us));
		}
		return usersDTO;
	}

}
