package isamrs.tim1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isamrs.tim1.model.User;
import isamrs.tim1.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public String editProfile(User user) throws Exception {
		User userToEdit = userRepository.findOneByEmail(user.getEmail());
		if (userToEdit == null) {
			return "User with given email address does not exist!";
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
			return "Database error!";
		}
		
		return null;
	}

}
